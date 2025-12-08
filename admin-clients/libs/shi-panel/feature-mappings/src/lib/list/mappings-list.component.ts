import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import { ChipsComponent, ChipsFilterDirective, ContextNotificationComponent, CopyTextComponent, DateTimeModule, DialogSize, EphemeralMessageService, ExportDialogComponent, FilterItem, ListFilteredComponent, ListFiltersService, MessageDialogService, ObMatDialogConfig, openDialog, PaginatorComponent, PopoverComponent, PopoverDateRangePickerFilterComponent, PopoverFilterDirective, SearchInputComponent, SortFilterComponent, StatusSelectComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats, ExportFormat } from '@admin-clients/shared/data-access/models';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { DefaultIconComponent } from '@admin-clients/shared-common-ui-default-icon';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, EventEmitter, inject, OnInit, signal, ViewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable, of, shareReplay, startWith, switchMap, take, tap } from 'rxjs';
import { NewMappingDialogComponent } from '../create/new-mapping-dialog.component';
import { MappingsService } from '../mappings.service';
import { favoriteMappingLiterals } from '../models/favorite-mapping-literals';
import { GetMappingsRequest } from '../models/get-mappings-request.model';
import { MappingStatus } from '../models/mapping-status.enum';
import { Mapping } from '../models/mapping.model';
import { BulkCleanListingsComponent } from './bulk-clean-listings/bulk-clean-listings.component';
import { BulkCreateMappingComponent } from './bulk-create-mapping/bulk-create-mapping.component';
import { MappingsListFilterComponent } from './filter/mappings-list-filter.component';
import { exportDataMappingsList } from './mappings-list-export-data';

const PAGE_SIZE = 20;
const EXCLUDED_FILTER_KEYS = ['q', 'noDate', 'sort', 'limit', 'offset', 'aggs'];

export interface FilterOption {
    id: string;
    name: string;
    date_start?: string;
}
@Component({
    imports: [
        AsyncPipe, TranslatePipe, PaginatorComponent, MaterialModule, FlexLayoutModule, ContextNotificationComponent, RouterModule,
        DateTimeModule, SharedUtilityDirectivesModule, SearchInputComponent, MappingsListFilterComponent, ChipsComponent,
        StatusSelectComponent, DateTimePipe, CopyTextComponent, PopoverFilterDirective, PopoverComponent, ChipsFilterDirective,
        PopoverDateRangePickerFilterComponent, DefaultIconComponent
    ],
    selector: 'app-mappings-list',
    templateUrl: './mappings-list.component.html',
    styleUrls: ['./mappings-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MappingsListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {
    readonly #mappingsService = inject(MappingsService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #dialog = inject(MatDialog);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #authService = inject(AuthenticationService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #tableSrv = inject(TableColConfigService);

    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(MappingsListFilterComponent) private readonly _filterComponent: MappingsListFilterComponent;
    @ViewChild(PopoverDateRangePickerFilterComponent) private readonly _dateRangePickerComponent: PopoverDateRangePickerFilterComponent;

    #request: GetMappingsRequest;
    #sortFilterComponent: SortFilterComponent;

    readonly displayedColumns = [
        'favorite',
        'shi_id',
        'created',
        'name',
        'taxonomies',
        'category',
        'date',
        'updated',
        'country_code',
        'supplier',
        'supplier_id',
        'related_listings',
        'status',
        'actions'
    ];

    readonly initSortCol = 'created';
    readonly initSortDir: SortDirection = 'desc';
    readonly mappingsPageSize = PAGE_SIZE;
    readonly dateTimeFormats = DateTimeFormats;
    readonly mappingStatus = MappingStatus;
    readonly showSelectedOnlyClick = new EventEmitter<boolean>();
    readonly allSelectedClick = new EventEmitter<boolean>();
    readonly selectedOnly$ = this.showSelectedOnlyClick.pipe(
        startWith(false),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    #bulckUpdatedStatusMappings = new Set<string>();
    readonly selectedMappings = signal<Mapping[]>([]);
    readonly selectedStatus = signal<MappingStatus | null>(null);

    mappings$ = this.selectedOnly$.pipe(
        switchMap(isSelectedOnly =>
            isSelectedOnly
                ? of(this.selectedMappings())
                : this.#mappingsService.list.getMappingsListData$().pipe(
                    map(mappings =>
                        mappings.map(mapping => {
                            if (this.#bulckUpdatedStatusMappings.has(mapping.code)) {
                                return {
                                    ...mapping,
                                    status: MappingStatus.active === mapping.status ? MappingStatus.disable : MappingStatus.active
                                };
                            }
                            return mapping;
                        })
                    ),
                    tap(() => this.#bulckUpdatedStatusMappings.clear())
                )
        ),
        shareReplay(1)
    );

    readonly allSelected$ = this.allSelectedClick.pipe(
        startWith(false),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    readonly mappingsMetadata$ = this.#mappingsService.list.getMappingsListMetadata$();
    readonly loadingData$ = this.#mappingsService.list.loading$();
    readonly isExportLoading$ = this.#mappingsService.list.exportLoading$();
    readonly hasWritePermissions$: Observable<boolean> = this.#authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.mappingWrite])));

    readonly isHandsetOrTablet$ = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly emptyListWithWordAndFilters$ = this.#mappingsService.list.getMappingsListMetadata$()
        .pipe(
            map(metadata => !!(metadata?.total === 0 && this.#request?.q && (
                Object.keys(this.#request).filter(key => !EXCLUDED_FILTER_KEYS.includes(key)).length > 0
            )))
        );

    readonly favoriteMappingLiterals = favoriteMappingLiterals;

    updateFavorite: (code: string, favorite: boolean) => Observable<boolean>;

    constructor() {
        super();

        //Set default creation dates from yesterday 00 to today 23:59
        const urlParameters = Object.assign({}, this.activatedRoute.snapshot.queryParams);
        if (!urlParameters['startDate'] && !urlParameters['startDate'] && !urlParameters['noDate']) {
            const yesterday = new Date();
            yesterday.setDate(yesterday.getDate() - 1);
            urlParameters['startDate'] = new Date(yesterday.setHours(0, 0, 0, 0)).toISOString();
            urlParameters['endDate'] = new Date(new Date().setHours(23, 59, 59, 999)).toISOString();
            this.router.navigate(['.'], { relativeTo: this.activatedRoute, queryParams: urlParameters });
        }
    }

    ngOnInit(): void {
        this.updateFavorite = (code, favorite) =>
            this.#mappingsService.list.updateFavorites({
                mappings: [{ code, favorite }]
            })
                .pipe(
                    filter(Boolean),
                    tap(() => {
                        this.showSuccess(
                            favorite ?
                                'MAPPINGS.FORMS.INFOS.ADD_FAVORITE_SUCCESS' :
                                'MAPPINGS.FORMS.INFOS.DELETE_FAVORITE_SUCCESS',
                            { code }
                        );
                    })
                );
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent,
            this._dateRangePickerComponent
        ]);
        this.#ref.detectChanges();
    }

    clickShowSelected(): void {
        this.selectedOnly$.pipe(take(1)).subscribe(isSelected => {
            if (this.selectedMappings().length === 0) {
                this.showSelectedOnlyClick.emit(false);
            } else {
                this.showSelectedOnlyClick.emit(!isSelected);
            }
        });
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {
            limit: this.mappingsPageSize,
            aggs: true,
            offset: 0
        };
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this.#request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this.#request.limit = values[0].value.limit;
                        this.#request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this.#request.q = values[0].value;
                        break;
                    case 'DATE_RANGE':
                        this.#request.create_date_from = values[0].value.start;
                        this.#request.create_date_to = values[0].value.end;
                        break;
                    case 'STATUS':
                        this.#request.status = values.map(val => val.value);
                        break;
                    case 'SUPPLIER':
                        this.#request.supplier = values.map(val => val.value);
                        break;
                    case 'CATEGORY':
                        this.#request.category = values.map(val => val.value);
                        break;
                    case 'COUNTRY_CODE':
                        this.#request.country_code = values.map(val => val.value);
                        break;
                    case 'TAXONOMIES':
                        this.#request.taxonomies = values.map(val => val.value);
                        break;
                    case 'START_DATE_EVENT':
                        this.#request.event_date_from = values[0].value;
                        break;
                    case 'END_DATE_EVENT':
                        this.#request.event_date_to = values[0].value;
                        break;
                    case 'START_DATE_UPDATE':
                        this.#request.update_date_from = values[0].value;
                        break;
                    case 'END_DATE_UPDATE':
                        this.#request.update_date_to = values[0].value;
                        break;
                    case 'FAVORITE':
                        this.#request.favorite = values[0].value;
                        break;
                }
            }
        });
        this.loadMappings();
    }

    openExportMappingslistDialog(): void {
        this.#dialog.open(ExportDialogComponent, new ObMatDialogConfig({
            exportData: exportDataMappingsList,
            exportFormat: ExportFormat.csv,
            selectedFields: this.#tableSrv.getColumns('EXP_SHI_MAPPINGS')
        }))
            .beforeClosed()
            .pipe(
                filter(Boolean),
                tap(exportList => this.#tableSrv.setColumns('EXP_SHI_MAPPINGS', exportList.fields.map(resultData => resultData.field))),
                switchMap(exportList => this.#mappingsService.list.exportMappingslist(this.#request, exportList)),
                filter(result => !!result.export_id)
            )
            .subscribe(() => this.#ephemeralMsg.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' }));
    }

    override refresh(): void {
        this.loadData(this.listFiltersService.getFilters());
    }

    openNewMappingDialog(): void {
        this.#dialog.open(NewMappingDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(mappingId => {
                if (mappingId) {
                    this.#ephemeralMsg.showSuccess({ msgKey: 'MAPPINGS.NEW_MAPPING_SUCCESS' });
                }
            });
    }

    updateStatus: (code: number, status: MappingStatus) => Observable<unknown> = (code, status) => this.#msgDialogSrv.showWarn({
        size: DialogSize.SMALL,
        title: 'TITLES.CHANGE_MAPPING_STATUS_TO_' + status.toUpperCase(),
        message: 'MAPPING.CHANGE_STATUS_TO_' + status.toUpperCase() + '_WARNING',
        actionLabel: 'FORMS.ACTIONS.CHANGE_TO_' + status.toUpperCase(),
        showCancelButton: true
    })
        .pipe(
            filter(accepted => !!accepted),
            switchMap(accepted => {
                if (accepted) {
                    return this.#mappingsService.list.updateMapping(code, { status });
                }
                return null;
            })
        );

    openDeleteMappingDialog(mapping: Mapping): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_MAPPING',
            message: 'MAPPINGS.DELETE_MAPPING_WARNING',
            messageParams: { shiId: mapping.shi_id },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#mappingsService.list.deleteMapping(mapping.code))
            )
            .subscribe(() => {
                this.#ephemeralMsg.showSuccess({
                    msgKey: 'MAPPINGS.DELETE_MAPPING_SUCCESS',
                    msgParams: { shiId: mapping.shi_id }
                });
                this.loadMappings();
            });
    }

    openCleanListingsDialog(mapping: Mapping): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.CLEAN_LISTINGS',
            message: 'MAPPINGS.CLEAN_LISTINGS_WARNING',
            messageParams: { shiId: mapping.shi_id },
            actionLabel: 'FORMS.ACTIONS.CLEAN_LISTINGS',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#mappingsService.list.cleanListings(mapping.code))
            )
            .subscribe(() => {
                this.#ephemeralMsg.showSuccess({
                    msgKey: 'MAPPINGS.CLEAN_LISTINGS_SUCCESS'
                });
                this.loadMappings();
            });
    }

    openBulkCleanListingsDialog(): void {
        openDialog(this.#dialog, BulkCleanListingsComponent)
            .beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap(data => this.#mappingsService.list.bulkCleanListings(data.idsToClean))
            )
            .subscribe(() => {
                this.#ephemeralMsg.showSuccess({ msgKey: 'MAPPINGS.BULK_CLEAN_SUCCESS' });
                this.loadMappings();
            });
    }

    openBulkCreateMappingsDialog(): void {
        openDialog(this.#dialog, BulkCreateMappingComponent)
            .beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap(data => this.#mappingsService.list.bulkCreateMapping(data.mappingsToCreate))
            )
            .subscribe(() => {
                this.#ephemeralMsg.showSuccess({ msgKey: 'MAPPINGS.BULK_CREATE_SUCCESS' });
                this.loadMappings();
            });
    }

    removeFilters(): void {
        this.listFiltersService.resetFilters('SEARCH_INPUT');
    }

    getTaxonomies(row: Mapping): string {
        return row.taxonomies.map(taxonomy => taxonomy.name).join(', ');
    }

    goToRelatedListings(supplierId: string): void {
        const baseUrl = window.location.origin;
        const urlTree = this.router.createUrlTree(['/listings'], { queryParams: { q: supplierId, noDate: true } });
        window.open(baseUrl + this.router.serializeUrl(urlTree));
    }

    isSelected(mapping: Mapping): boolean {
        return this.selectedMappings().some(selected => selected.code === mapping.code);
    }

    changeSelection(mapping: Mapping): void {
        const currentSelections = this.selectedMappings();
        const isSelected = this.isSelected(mapping);

        if (isSelected) {
            const updatedSelections = currentSelections.filter(item => item.code !== mapping.code);
            this.selectedMappings.set(updatedSelections);

            if (updatedSelections.length === 0) {
                this.selectedStatus.set(null);
                this.showSelectedOnlyClick.emit(false);
            } else {
                const uniqueStatuses = new Set(updatedSelections.map(m => m.status));
                if (uniqueStatuses.size === 1) {
                    this.selectedStatus.set([...uniqueStatuses][0]);
                } else {
                    this.selectedStatus.set(null);
                }
            }
            return;
        }

        if (this.selectedStatus() === null) {
            this.selectedStatus.set(mapping.status);
        } else if (this.selectedStatus() !== mapping.status) {
            return;
        }

        this.selectedMappings.set([...currentSelections, mapping]);
    }

    updateBulkStatus(status: MappingStatus): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.CHANGE_MAPPINGS_STATUS_TO_' + status.toUpperCase(),
            message: 'MAPPINGS.CHANGE_STATUS_TO_' + status.toUpperCase() + '_WARNING',
            actionLabel: 'FORMS.ACTIONS.CHANGE_TO_' + status.toUpperCase(),
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => {
                    this.#bulckUpdatedStatusMappings = new Set(this.selectedMappings().map(mapping => mapping.code));
                    return this.#mappingsService.list.bulkUpdateStatus(
                        this.selectedMappings().map(mapping => mapping.code),
                        status
                    );
                })
            )
            .subscribe(() => {
                this.#ephemeralMsg.showSuccess({ msgKey: 'MAPPINGS.BULK_UPDATE_STATUS' });
                this.selectedMappings.set([]);
                this.selectedStatus.set(null);
                this.showSelectedOnlyClick.emit(false);
            });
    }

    private showSuccess(msgKey: string, msgParams?: { [key: string]: string }): void {
        this.#ephemeralMsg.showSuccess({
            msgKey,
            msgParams
        });
    }

    private loadMappings(): void {
        this.#mappingsService.list.load(this.#request);
        this.#ref.detectChanges();
    }
}
