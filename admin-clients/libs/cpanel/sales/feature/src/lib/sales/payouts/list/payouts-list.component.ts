import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    payoutsColumnList, PayoutsService, PayoutsFields, GetPayoutsRequest, payoutStatus
} from '@admin-clients/cpanel-sales-data-access';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    ListFiltersService, ListFilteredComponent, PaginatorComponent, FilterItem,
    ObMatDialogConfig, PopoverDateRangePickerFilterComponent, ExportDialogComponent, EphemeralMessageService,
    PopoverComponent, ChipsFilterDirective, ChipsComponent, CopyTextComponent, ContextNotificationComponent,
    PopoverFilterDirective, SearchInputComponent, MessageDialogService, DialogSize, ColSelectionDialogComponent, HelpButtonComponent
} from '@admin-clients/shared/common/ui/components';
import {
    DateTimeFormats, ExportFormat, FieldData
} from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, computed, inject, signal, viewChild
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { first, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { PayoutsListFilterComponent } from './filter/payouts-list-filter.component';
import { exportDataPayout } from './payouts-export-data';

@Component({
    selector: 'app-payouts-list',
    templateUrl: './payouts-list.component.html',
    styleUrls: ['./payouts-list.component.scss'],
    providers: [ListFiltersService],
    imports: [
        PaginatorComponent, TranslatePipe, PopoverDateRangePickerFilterComponent, MatMenuModule, PopoverComponent,
        PayoutsListFilterComponent, MatButtonModule, ChipsFilterDirective, AsyncPipe, DragDropModule,
        ChipsComponent, MatTableModule, MatProgressSpinner, CopyTextComponent, MatTooltip, MatIcon, ContextNotificationComponent,
        FlexLayoutModule, PopoverFilterDirective, LocalCurrencyPipe, SearchInputComponent, DateTimePipe, MatDivider,
        EllipsifyDirective, RouterLink, NgTemplateOutlet,
        HelpButtonComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PayoutListComponent extends ListFilteredComponent implements AfterViewInit {
    readonly #payoutsSrv = inject(PayoutsService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #dialog = inject(MatDialog);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #tableSrv = inject(TableColConfigService);
    readonly #translate = inject(TranslateService);

    readonly #$user = toSignal(this.#authSrv.getLoggedUser$().pipe(first()));
    readonly #$operatorCurrencyCodes = computed(() => AuthenticationService.operatorCurrencyCodes(this.#$user()));
    readonly #$areCurrenciesShown = computed(() => AuthenticationService.operatorCurrencyCodes(this.#$user())?.length > 1);
    readonly #$request = signal<GetPayoutsRequest>({});
    #applyRelevance = false;

    private readonly _paginatorComponent = viewChild(PaginatorComponent);
    private readonly _filterComponent = viewChild(PayoutsListFilterComponent);
    private readonly _dateRangePickerComponent = viewChild(PopoverDateRangePickerFilterComponent);
    private readonly _searchInputComponent = viewChild(SearchInputComponent);

    readonly payoutStatus = payoutStatus;
    readonly payoutsColumns = PayoutsFields;
    readonly defaultDisplayedColumns = [
        PayoutsFields.channel,
        PayoutsFields.code,
        PayoutsFields.event,
        PayoutsFields.session,
        PayoutsFields.seat,
        PayoutsFields.client,
        PayoutsFields.payoutType,
        PayoutsFields.price,
        PayoutsFields.status,
        PayoutsFields.actions
    ];

    displayedColumns = this.#tableSrv.getColumns('PAYOUTS')?.filter(
        col => Object.values(PayoutsFields).map(col => String(col)).includes(col)) || this.defaultDisplayedColumns;

    readonly fixedColumns: string[] = [
        PayoutsFields.status,
        PayoutsFields.actions
    ];

    readonly $isMgr = toSignal(this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR]));

    readonly initSortCol = 'purchaseDate';
    readonly initSortDir = 'desc';
    readonly pageSize = 20;
    readonly dateTimeFormats = DateTimeFormats;

    readonly isExportEnabled$ = this.#authSrv.getLoggedUser$()
        .pipe(map(user => user && AuthenticationService.isSomeRoleInUserRoles(
            user, [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.CNL_MGR]
        )));

    readonly emptyListWithWordAndDates$ = this.#payoutsSrv.payoutsList.getMetaData$()
        .pipe(map(metadata => !!(metadata?.total === 0 && this.#$request()?.purchase_date_from)));

    readonly payouts$ = this.#payoutsSrv.payoutsList.getData$();
    readonly payoutsMetadata$ = this.#payoutsSrv.payoutsList.getMetaData$();

    readonly reqInProgress$ = booleanOrMerge([
        this.#payoutsSrv.payoutsList.loading$(),
        this.#payoutsSrv.payoutsList.loadingExport$()
    ]);

    readonly isHandsetOrTablet$: Observable<boolean> = isHandsetOrTablet$();
    readonly startDate: string;
    readonly endDate: string;

    constructor(
        router: Router,
        activatedRoute: ActivatedRoute
    ) {
        super();
        const urlParameters = Object.assign({}, activatedRoute.snapshot.queryParams);
        if (!urlParameters['startDate'] && !urlParameters['startDate'] && !urlParameters['noDate']) {
            urlParameters['startDate'] = new Date(new Date().setHours(0, 0, 0, 0)).toISOString();
            urlParameters['endDate'] = new Date(new Date().setHours(23, 59, 59, 999)).toISOString();
            router.navigate(['.'], { relativeTo: activatedRoute, queryParams: urlParameters, replaceUrl: true });
        }
        this.startDate = urlParameters['startDate'];
        this.endDate = urlParameters['endDate'];
    }

    ngAfterViewInit(): void {
        this.initListFilteredComponent([
            this._paginatorComponent(),
            this._filterComponent(),
            this._dateRangePickerComponent(),
            this._searchInputComponent()]);
    }

    loadData(filters: FilterItem[]): void {
        const request: GetPayoutsRequest = {
            limit: this.pageSize,
            offset: 0
        };
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'PAGINATION':
                        request.limit = values[0].value.limit;
                        request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        request.q = values[0].value;
                        break;
                    case 'CHANNEL':
                        request.channel_id = values.map(val => val.value);
                        break;
                    case 'EVENT':
                        request.event_id = values.map(val => val.value);
                        break;
                    case 'SESSION':
                        request.session_id = values.map(val => val.value);
                        break;
                    case 'DATE_RANGE':
                        request.purchase_date_from = values[0].value.start;
                        request.purchase_date_to = values[0].value.end;
                        break;
                    case 'PAYOUT_TYPE':
                        request.payout_type = values[0].value;
                        break;
                    case 'CURRENCY':
                        // currency_code is not set by the filter in case of only one operator currency or monocurrency
                        request.currency_code = values[0].value;
                        break;
                }
            }
        });

        //TODO(MULTICURRENCY): delete this.#$user().currency when the multicurrency functionality is finished
        request.currency_code = this.#$areCurrenciesShown() ? request.currency_code :
            (this.#$operatorCurrencyCodes()?.[0] ?? this.#$user().currency);
        this.#$request.set(request);
        this.#payoutsSrv.payoutsList.load(this.#$request(), this.#applyRelevance);
    }

    exportPayouts(): void {
        this.#dialog.open(
            ExportDialogComponent,
            new ObMatDialogConfig({
                exportData: exportDataPayout,
                exportFormat: ExportFormat.csv,
                selectedFields: this.#tableSrv.getColumns('EXP_PAYOUTS')
            }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                this.#tableSrv.setColumns('EXP_PAYOUTS', exportList.fields.map(resultData => resultData.field));
                const exportRequest = structuredClone(this.#$request());
                delete exportRequest.limit;
                delete exportRequest.offset;
                this.#payoutsSrv.payoutsList.export(exportRequest, exportList)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this.#ephemeralMessageSrv.showSuccess({
                            msgKey: 'ACTIONS.EXPORT.OK.MESSAGE'
                        });
                    });
            });
    }

    updatePayoutStatus(id: string, status: 'PAID' | 'UNPAID'): void {
        this.#payoutsSrv.updatePayoutStatus(id, status).subscribe(() => {
            this.#ephemeralMessageSrv.showSuccess({
                msgKey: 'PAYOUT_REQUESTS.CHANGE_STATUS_SUCCESS',
                msgParams: {
                    status: this.#translate.instant(`PAYOUT_REQUESTS.STATUS_OPTS.${status}`)
                }
            });
        });
    }

    payToBalance(id: string, payoutPrice: string): void {
        this.#messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'PAYOUTS.PAY_TO_BALANCE_WARNING.TITLE',
            message: 'PAYOUTS.PAY_TO_BALANCE_WARNING.MESSAGE',
            messageParams: { amount: payoutPrice },
            actionLabel: 'PAYOUTS.PAY_TO_BALANCE_WARNING.ACTION'
        })
            .subscribe(success => {
                if (success) {
                    this.updatePayoutStatus(id, 'PAID');
                }
            });
    }

    applyRelevance(value: string): void {
        this.#applyRelevance = !!value;
    }

    unapplyRelevance(): void {
        this.#applyRelevance = false;
    }

    removeDatesFilter(): void {
        this.listFiltersService.removeFilter('DATE_RANGE', null);
    }

    changeColSelection(): void {
        this.#dialog.open(ColSelectionDialogComponent, new ObMatDialogConfig(
            {
                fieldDataGroups: payoutsColumnList,
                selectedFields: payoutsColumnList
                    .map(columnGroup => columnGroup.fields)
                    .reduce((previousValue, currentValue) => currentValue.concat(...previousValue))
                    .filter(column => this.displayedColumns.includes(column.field))
            }))
            .beforeClosed()
            .pipe(filter(result => !!result))
            .subscribe((result: FieldData[]) => {
                const resultFields = result.map(resultData => resultData.field);
                const sortResult = [].concat(
                    ...payoutsColumnList
                        .map(colGroup => colGroup.fields)
                )
                    .filter(data => resultFields.includes(data.field))
                    .map(data => data.field);
                this.displayedColumns = [...sortResult, PayoutsFields.actions];
                this.#tableSrv.setColumns('PAYOUTS', this.displayedColumns);
                this.#ref.markForCheck();
            });
    }

    dropItem(event: CdkDragDrop<PayoutsFields[]>): void {
        if (!this.fixedColumns.includes(this.displayedColumns[event.currentIndex])
            && !this.fixedColumns.includes(this.displayedColumns[event.previousIndex])) {
            moveItemInArray(this.displayedColumns, event.previousIndex, event.currentIndex);
            this.#tableSrv.setColumns('PAYOUTS', this.displayedColumns);
        }
    }
}
