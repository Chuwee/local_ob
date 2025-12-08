import { DetailOverlayService } from '@OneboxTM/detail-overlay';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    ChipsComponent,
    ChipsFilterDirective,
    EphemeralMessageService,
    ExportDialogComponent,
    FilterItem,
    ListFilteredComponent,
    ListFiltersService,
    ObMatDialogConfig,
    PopoverComponent,
    PopoverDateRangePickerFilterComponent,
    PopoverFilterDirective
} from '@admin-clients/shared/common/ui/components';
import { ExportFormat } from '@admin-clients/shared/data-access/models';
import {
    ErrorDashboardRequest, ErrorDashboardService, errorResponsibles
} from '@admin-clients/shi-panel/sales/error-dashboard-data-access';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, viewChild, OnDestroy, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, switchMap, tap } from 'rxjs';
import { ErrorDashboardCauseChartComponent } from './cause-chart/error-dashboard-cause-chart.component';
import { exportDataErrorRates } from './error-dashboard-export-data';
import { ErrorDashboardFilterComponent } from './filter/error-dashboard-filter.component';
import { FloatCardComponent } from './float-card/float-card.component';
import { ErrorDashboardGroupByPeriodFilterComponent } from './group-by-period-filter/error-dashboard-group-by-period-filter.component';
import { ErrorDashboardRateChartComponent } from './rate-chart/error-dashboard-rate-chart.component';
import { ErrorDashboardRateSummaryComponent } from './rate-summary/error-dashboard-rate-summary.component';
import { ErrorDashboardSalesSummaryComponent } from './sales-summary/error-dashboard-sales-summary.component';

@Component({
    providers: [ListFiltersService, DetailOverlayService],
    imports: [
        TranslatePipe, ErrorDashboardFilterComponent, ChipsComponent, PopoverDateRangePickerFilterComponent, PopoverComponent,
        FloatCardComponent, ErrorDashboardRateSummaryComponent, PopoverFilterDirective, ChipsFilterDirective,
        ErrorDashboardGroupByPeriodFilterComponent, MatProgressSpinner, MatTooltip, ErrorDashboardCauseChartComponent,
        ErrorDashboardRateChartComponent, ErrorDashboardSalesSummaryComponent, MatIconModule, MatButtonModule
    ],
    selector: 'app-error-dashboard-page',
    templateUrl: './error-dashboard-page.component.html',
    styleUrls: ['./error-dashboard-page.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorDashboardPageComponent extends ListFilteredComponent implements AfterViewInit, OnDestroy {
    readonly #errorDashboardSrv = inject(ErrorDashboardService);
    readonly #detailOverlayService = inject(DetailOverlayService);
    readonly #router = inject(Router);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #dialog = inject(MatDialog);
    readonly #tableSrv = inject(TableColConfigService);
    readonly #ephemeralMsg = inject(EphemeralMessageService);

    #request: ErrorDashboardRequest;

    private readonly _$dateRangePickerComponent = viewChild(PopoverDateRangePickerFilterComponent);
    private readonly _$errorDashboardFilterComponent = viewChild(ErrorDashboardFilterComponent);
    private readonly _$timeLineFilterComponent = viewChild(ErrorDashboardGroupByPeriodFilterComponent);

    readonly $errorResponsibles = signal([...errorResponsibles]);
    readonly $isLoading = toSignal(this.#errorDashboardSrv.errorDashboard.isInProgress$());
    readonly $isExportLoading = toSignal(this.#errorDashboardSrv.errorDashboard.exportLoading$());
    readonly $errorDashboardData = toSignal(this.#errorDashboardSrv.errorDashboard.getErrorDashboardData$());

    constructor() {
        super();

        const urlParameters = Object.assign({}, this.#activatedRoute.snapshot.queryParams);
        if (!urlParameters['startDate'] || !urlParameters['endDate'] || !urlParameters['group-by-period']) {
            // Set default prediod from 7 days ago to today at 23:59
            if (!urlParameters['startDate'] || !urlParameters['endDate']) {
                const weekAgo = new Date();
                weekAgo.setDate(weekAgo.getDate() - 7);
                urlParameters['startDate'] = new Date(weekAgo.setHours(0, 0, 0, 0)).toISOString();
                urlParameters['endDate'] = new Date(new Date().setHours(23, 59, 59, 999)).toISOString();
            }
            // Set default group by period to DAY
            if (!urlParameters['group-by-period']) {
                urlParameters['group-by-period'] = 'DAY';
            }
            this.#router.navigate(['.'], { relativeTo: this.#activatedRoute, queryParams: urlParameters });
        }
    }

    ngAfterViewInit(): void {
        this.initListFilteredComponent([
            this._$dateRangePickerComponent(),
            this._$errorDashboardFilterComponent(),
            this._$timeLineFilterComponent()
        ]);
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {
            error_responsible: [],
            group_by_period: 'DAY'
        };
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'DATE_RANGE':
                        this.#request.date_from = values[0].value.start;
                        this.#request.date_to = values[0].value.end;
                        break;
                    case 'DELIVERY':
                        this.#request.delivery_method = values.map(val => val.value);
                        break;
                    case 'SUPPLIER':
                        this.#request.supplier = values.map(val => val.value);
                        break;
                    case 'COUNTRY':
                        this.#request.country_code = values.map(val => val.value);
                        break;
                    case 'CURRENCY':
                        this.#request.currency = values.map(val => val.value);
                        break;
                    case 'RESPONSIBLE':
                        this.#request.error_responsible = values.map(val => val.value);
                        break;
                    case 'TAXONOMIES':
                        this.#request.taxonomies = values.map(val => val.value);
                        break;
                    case 'DAYS_TO_EVENT_LTE':
                        this.#request.daysToEventLte = values[0].value;
                        break;
                    case 'DAYS_TO_EVENT_GTE':
                        this.#request.daysToEventGte = values[0].value;
                        break;
                    case 'GROUP_BY_PERIOD':
                        this.#request.group_by_period = values[0].value;
                }
            }
        });
        this.updateResponsibleList();
        this.loadErrorDashboardData();
    }

    openExportErrorRateDialog(): void {
        this.#dialog.open(ExportDialogComponent, new ObMatDialogConfig({
            exportData: exportDataErrorRates,
            exportFormat: ExportFormat.csv,
            selectedFields: this.#tableSrv.getColumns('EXP_SHI_ERROR_DASHBOARD')
        }))
            .beforeClosed()
            .pipe(
                filter(Boolean),
                tap(exportList =>
                    this.#tableSrv.setColumns('EXP_SHI_ERROR_DASHBOARD', exportList.fields.map(resultData => resultData.field))
                ),
                switchMap(exportList => this.#errorDashboardSrv.errorDashboard.exportErrorRates(this.#request, exportList)),
                filter(result => !!result.export_id)
            )
            .subscribe(() =>
                this.#ephemeralMsg.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' })
            );
    }

    updateResponsibleList(): void {
        this.$errorResponsibles.set(this.#request.error_responsible.length === 0 ? [...errorResponsibles] :
            [...errorResponsibles].filter(r => this.#request.error_responsible.includes(r))
        );
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#detailOverlayService.close();
    }

    private loadErrorDashboardData(): void {
        this.#detailOverlayService.close();
        this.#errorDashboardSrv.errorDashboard.load(this.#request);
    }
}
