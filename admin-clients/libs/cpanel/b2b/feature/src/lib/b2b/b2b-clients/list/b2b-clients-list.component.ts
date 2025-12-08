import { B2bClientReduced, B2bService, GetB2bClientsRequest } from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntityFilterButtonComponent } from '@admin-clients/cpanel/organizations/entities/feature';
import { EntitiesFilterFields, GetEntitiesRequest } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, FilterItem, ListFilteredComponent, ListFiltersService, MessageDialogService, ObMatDialogConfig,
    PaginatorComponent, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnDestroy, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { Router } from '@angular/router';
import { BehaviorSubject, firstValueFrom, Observable, of, Subject } from 'rxjs';
import { filter, map, shareReplay, switchMap } from 'rxjs/operators';
import { NewB2bClientDialogComponent } from '../create/new-b2b-client-dialog.component';

const PAGE_SIZE = 20;

@Component({
    selector: 'app-b2b-clients-list',
    templateUrl: './b2b-clients-list.component.html',
    styleUrls: ['./b2b-clients-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bClientsListComponent extends ListFilteredComponent implements OnDestroy, AfterViewInit {
    private readonly _b2bSrv = inject(B2bService);
    private readonly _ref = inject(ChangeDetectorRef);
    private readonly _breakpointObserver = inject(BreakpointObserver);
    private readonly _authSrv = inject(AuthenticationService);
    private readonly _matDialog = inject(MatDialog);
    private readonly _msgDialogService = inject(MessageDialogService);
    private readonly _ephemeralMsg = inject(EphemeralMessageService);

    private _entityIdQueryParam = new BehaviorSubject<{ entityId: string }>(null);
    private _onDestroy = new Subject<void>();
    private _userEntityId: number;
    private _request: GetB2bClientsRequest;
    private _entityId: number;

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild('entityFilterButton') private _entityFilterButton: EntityFilterButtonComponent;

    displayedColumns = [
        'name', 'category_type', 'business_name', 'tax_id', 'creation_date',
        'country', 'country_subdivision', 'actions'
    ];

    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    b2bClientsPageSize = PAGE_SIZE;
    dateTimeFormats = DateTimeFormats;
    hasSearchFilterApplied = false;

    readonly entityIdQueryParam$ = this._entityIdQueryParam.asObservable();
    readonly b2bClients$ = this._b2bSrv.b2bClientsList.getList$();
    readonly b2bClientsMetadata$ = this._b2bSrv.b2bClientsList.getMetadata$();
    readonly loadingData$ = booleanOrMerge([
        this._b2bSrv.b2bClientsList.loading$(),
        this._b2bSrv.isB2bClientInProgress$()
    ]);

    readonly getB2bEntitiesRequest: GetEntitiesRequest = {
        limit: 999,
        offset: 0,
        sort: 'name:asc',
        fields: [EntitiesFilterFields.name],
        b2b_enabled: true
    };

    readonly isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly canReadMultipleEntities$ = this._authSrv.canReadMultipleEntities$();
    readonly isB2bUserCreationAllowed$ = this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR]);

    readonly isEntitySelected$ = this._authSrv.canReadMultipleEntities$()
        .pipe(
            switchMap(canReadMultEnt => {
                if (canReadMultEnt) {
                    return this.listFiltersService.onFilterValuesApplied$()
                        .pipe(map(filterItems => !!filterItems?.find(filterItem => filterItem.key === 'ENTITY')?.values?.length));
                } else {
                    return of(true);
                }
            }),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    constructor(
        private _router: Router
    ) {
        super();
    }

    trackByFn = (_, item: B2bClientReduced): number => item.id;

    ngAfterViewInit(): void {
        this.initListFilteredComponent([
            this._paginatorComponent,
            new SortFilterComponent(this._matSort),
            this._searchInputComponent,
            this._entityFilterButton
        ]);
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._b2bSrv.clearB2bClientsList();
    }

    async loadData(filters: FilterItem[]): Promise<void> {
        const canReadMultipleEntities = await firstValueFrom(this.canReadMultipleEntities$);
        this.hasSearchFilterApplied = false;
        this._request = {
            limit: this.b2bClientsPageSize,
            offset: 0
        };

        if (!canReadMultipleEntities) {
            this._request.entity_id = this._userEntityId;
        }

        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this._request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this._request.limit = values[0].value.limit;
                        this._request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this._request.q = values[0].value;
                        this.hasSearchFilterApplied = true;
                        break;
                    case 'ENTITY':
                        if (canReadMultipleEntities) {
                            this._request.entity_id = values[0].value;
                        }
                        break;
                }
            }
        });
        this._entityId = this._request.entity_id;
        if (canReadMultipleEntities) {
            this._entityIdQueryParam.next({ entityId: this._entityId?.toString() });
            if (this._request.entity_id) {
                this.loadB2bClients();
            }
        } else {
            this.loadB2bClients();
        }
    }

    async openNewB2bClientDialog(): Promise<void> {
        const canReadMultipleEntities = await firstValueFrom(this.canReadMultipleEntities$);
        this._matDialog.open<NewB2bClientDialogComponent, { entityId: number }, number>(
            NewB2bClientDialogComponent, new ObMatDialogConfig({ entityId: this._entityId })
        )
            .beforeClosed()
            .subscribe(b2bClientId => {
                if (b2bClientId) {
                    this._ephemeralMsg.showSuccess({ msgKey: 'B2B_CLIENTS.CREATE_B2B_CLIENT_SUCCESS' });
                    this._router.navigate(
                        [b2bClientId, 'general-data'],
                        {
                            relativeTo: this.activatedRoute,
                            queryParams: canReadMultipleEntities ? { entityId: this._entityId?.toString() } : null
                        }
                    );
                }
            });
    }

    async openDeleteB2bClientDialog(b2bClient: B2bClientReduced): Promise<void> {
        const canReadMultipleEntities = await firstValueFrom(this.canReadMultipleEntities$);
        this._msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_B2B_CLIENT',
            message: 'B2B_CLIENTS.DELETE_B2B_CLIENT_WARNING',
            messageParams: { b2bClientName: b2bClient.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                filter(accepted => !!accepted),
                switchMap(() => canReadMultipleEntities ?
                    this._b2bSrv.deleteB2bClient(b2bClient.id, this._entityId) : this._b2bSrv.deleteB2bClient(b2bClient.id))
            )
            .subscribe(() => {
                this._ephemeralMsg.showSuccess({
                    msgKey: 'B2B_CLIENTS.DELETE_B2B_CLIENT_SUCCESS',
                    msgParams: { b2bClientName: b2bClient.name }
                });
                this.loadB2bClients();
            });
    }

    private loadB2bClients(): void {
        this._b2bSrv.loadB2bClientsList(this._request);
        this._ref.detectChanges();
    }

}
