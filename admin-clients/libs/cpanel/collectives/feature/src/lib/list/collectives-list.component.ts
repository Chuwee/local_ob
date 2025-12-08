import { Metadata } from '@OneboxTM/utils-state';
import { Collective, CollectiveStatus, CollectivesService, GetCollectivesRequest } from '@admin-clients/cpanel/collectives/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    DialogSize, MessageDialogService, ObMatDialogConfig, ListFilteredComponent, ListFiltersService, SortFilterComponent,
    PaginatorComponent, FilterItem, SearchInputComponent, EphemeralMessageService,
    ChipsComponent, ContextNotificationComponent, PopoverComponent, StatusSelectComponent, ChipsFilterDirective, PopoverFilterDirective
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { NgIf, AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, first, map, switchMap } from 'rxjs/operators';
import { NewCollectiveDialogComponent } from '../create/new-collective-dialog.component';
import { CollectivesFilterComponent } from './filter/collectives-filter.component';

const writeRoles = [UserRoles.OPR_MGR, UserRoles.COL_MGR];

@Component({
    selector: 'app-collectives-list',
    templateUrl: './collectives-list.component.html',
    styleUrls: ['./collectives-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        NgIf, AsyncPipe, MaterialModule, TranslatePipe, FlexLayoutModule, RouterLink,
        PopoverComponent, ContextNotificationComponent, StatusSelectComponent, ChipsFilterDirective, PopoverFilterDirective,
        CollectivesFilterComponent, ChipsComponent, PaginatorComponent, SearchInputComponent
    ]
})
export class CollectivesListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {
    private _request: GetCollectivesRequest;
    private _sortFilterComponent: SortFilterComponent;

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(CollectivesFilterComponent) private _filterComponent: CollectivesFilterComponent;

    pageSize = 20;
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    collectiveStatus = CollectiveStatus;
    metadata$: Observable<Metadata>;
    isLoading$: Observable<boolean>;
    userEntityId: number;
    entities$: Observable<Collective[]>;
    userCanWrite$: Observable<boolean>;
    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    updateStatus: (id: number, status: CollectiveStatus) => Observable<void>;

    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();

    readonly displayedColumns$ = this.canSelectEntity$.pipe(
        first(),
        map(canSelectEntity => {
            if (canSelectEntity) {
                return ['entity', 'name', 'description', 'type', 'subtype', 'status', 'actions'];
            } else {
                return ['name', 'description', 'type', 'subtype', 'status', 'actions'];
            }
        })
    );

    constructor(
        private _collectivesService: CollectivesService,
        private _auth: AuthenticationService,
        private _breakpointObserver: BreakpointObserver,
        private _ref: ChangeDetectorRef,
        private _matDialog: MatDialog,
        private _msgDialogSrv: MessageDialogService,
        private _ephemeralMessageService: EphemeralMessageService,
        private _router: Router,
        private _route: ActivatedRoute
    ) {
        super();
    }

    trackByFn = (_: number, item: Collective): number => item.id;

    ngOnInit(): void {
        this._auth.getLoggedUser$()
            .pipe(first(user => !!user))
            .subscribe(user => this.userEntityId = user.entity.id);
        this.userCanWrite$ = this._auth.hasLoggedUserSomeRoles$(writeRoles);
        this.metadata$ = this._collectivesService.getCollectivesListMetadata$();
        this.isLoading$ = this._collectivesService.isCollectiveListLoading$();
        this.entities$ = this._collectivesService.getCollectivesListData$()
            .pipe(
                filter(entities => !!entities)
            );

        this.updateStatus = (id, status) => this._collectivesService.updateCollectiveStatus(id, status);
    }

    ngAfterViewInit(): void {
        this._sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this._sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent
        ]);
    }

    loadData(filters: FilterItem[]): void {
        this._request = {};
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
                        break;
                    case 'TYPE':
                        this._request.type = values[0].value;
                        break;
                    case 'STATUS':
                        this._request.status = values[0].value;
                        break;
                    case 'ENTITY':
                        this._request.entity_id = values[0].value;
                        break;
                }
            }
        });
        this.loadCollectives();
    }

    openNewCollectiveDialog(): void {
        this._matDialog.open(NewCollectiveDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .pipe(filter(created => !!created))
            .subscribe(collective => {
                if (collective) {
                    this._ephemeralMessageService.showSuccess({
                        msgKey: 'COLLECTIVE.CREATE_COLLECTIVE_SUCCESS',
                        msgParams: { name: collective.name }
                    });
                    this._router.navigate([collective.id], { relativeTo: this._route });
                }
            });
    }

    openDeleteCollectiveDialog(collective: Collective): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_COLLECTIVE',
            message: 'COLLECTIVE.DELETE_WARNING',
            messageParams: { name: collective.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._collectivesService.deleteCollective(collective.id))
            )
            .subscribe(() => {
                this._ephemeralMessageService.showSuccess({
                    msgKey: 'COLLECTIVE.DELETE_COLLECTIVE_SUCCESS',
                    msgParams: { name: collective.name }
                });
                this.loadCollectives();
            });
    }

    private loadCollectives(): void {
        this._collectivesService.loadCollectivesList(this._request);
        this._ref.detectChanges();
    }

}
