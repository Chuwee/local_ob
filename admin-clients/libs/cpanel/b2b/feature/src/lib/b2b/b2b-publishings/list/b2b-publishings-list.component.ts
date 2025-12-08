import { B2bSeatReduced, B2bService, GetB2bSeatsListRequest } from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    GetEntitiesRequest, TableColConfigService
} from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, ExportDialogComponent, FilterItem,
    ListFilteredComponent, ListFiltersService, PaginatorComponent, SortFilterComponent, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, ExportFormat } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnDestroy, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { firstValueFrom, map, Observable, Subject, filter } from 'rxjs';
import { B2bPublishingsListFilterComponent } from '../filter/b2b-publishings-list-filter.component';
import { exportDataB2bPublishing } from './b2b-publishings-export-data';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-b2b-publishings-list',
    templateUrl: './b2b-publishings-list.component.html',
    styleUrls: ['./b2b-publishings-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bPublishingsListComponent extends ListFilteredComponent implements AfterViewInit, OnDestroy {
    private readonly _b2bSrv = inject(B2bService);
    private _sortFilterComponent: SortFilterComponent;
    @ViewChild(MatSort) private _matSort: MatSort;
    private readonly _ref = inject(ChangeDetectorRef);
    private readonly _breakpointObserver = inject(BreakpointObserver);
    private readonly _authSrv = inject(AuthenticationService);
    private _tableSrv = inject(TableColConfigService);
    private readonly _matDialog = inject(MatDialog);
    private readonly _ephemeralMsg = inject(EphemeralMessageService);

    private _onDestroy = new Subject<void>();
    private _request: GetB2bSeatsListRequest;

    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(B2bPublishingsListFilterComponent) private _filterComponent: B2bPublishingsListFilterComponent;

    displayedColumns = [
        'seat', 'session.name', 'event.name', 'channel.name', 'date', 'status', 'publisher.client_name'
    ];

    pageSize = PAGE_SIZE;
    dateTimeFormats = DateTimeFormats;
    hasSearchFilterApplied = false;
    initSortCol = 'date';
    initSortDir: SortDirection = 'desc';

    readonly b2bSeats$ = this._b2bSrv.b2bSeatsList.getList$();
    readonly metadata$ = this._b2bSrv.b2bSeatsList.getMetadata$();

    readonly loadingData$ = booleanOrMerge([
        this._b2bSrv.b2bSeatsList.loading$()
    ]);

    readonly getB2bEntitiesRequest: GetEntitiesRequest = {
        limit: 999,
        offset: 0,
        b2b_enabled: true
    };

    readonly isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly canReadMultipleEntities$ = this._authSrv.canReadMultipleEntities$();
    readonly isB2bUserCreationAllowed$ = this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR]);

    trackByFn = (_, item: B2bSeatReduced): number => item.id;

    exportB2bSeatsList(): void {
        this._matDialog.open(ExportDialogComponent, new ObMatDialogConfig({
            exportData: exportDataB2bPublishing,
            exportFormat: ExportFormat.csv,
            selectedFields: this._tableSrv.getColumns('EXP_B2B_SEATS')
        }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                this._tableSrv.setColumns('EXP_B2B_SEATS', exportList.fields.map(resultData => resultData.field));
                this._b2bSrv.b2bSeatsList.export(this._request, exportList)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this._ephemeralMsg.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
                    });
            });

    }

    ngAfterViewInit(): void {
        this._sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._sortFilterComponent,
            this._paginatorComponent,
            this._filterComponent
        ]);
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    async loadData(filters: FilterItem[]): Promise<void> {
        const canReadMultipleEntities = await firstValueFrom(this.canReadMultipleEntities$);
        this.hasSearchFilterApplied = false;
        this._request = {
            limit: this.pageSize,
            offset: 0
        };

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
                    case 'ENTITY':
                        if (canReadMultipleEntities) {
                            this._request.entity_ids = [values[0].value];
                            this.hasSearchFilterApplied = true;
                        }
                        break;
                    case 'CHANNEL':
                        this._request.channel_ids = [values[0].value];
                        this.hasSearchFilterApplied = true;
                        break;
                    case 'EVENT':
                        this._request.event_ids = [values[0].value];
                        this.hasSearchFilterApplied = true;
                        break;
                    case 'SESSION':
                        this._request.session_ids = [values[0].value];
                        this.hasSearchFilterApplied = true;
                        break;
                    case 'CLIENT':
                        this._request.client_ids = values.map(val => val.value);
                        this.hasSearchFilterApplied = true;
                        break;
                    case 'START_DATE':
                        this._request.date_from = values[0].value;
                        this.hasSearchFilterApplied = true;
                        break;
                    case 'END_DATE':
                        this._request.date_to = values[0].value;
                        this.hasSearchFilterApplied = true;
                        break;
                    case 'STATUS':
                        this._request.types = [values[0].value];
                        break;
                }
            }
        });
        this.loadB2bSeats();
    }

    private loadB2bSeats(): void {
        this._b2bSrv.b2bSeatsList.load(this._request);
        this._ref.detectChanges();
    }

}
