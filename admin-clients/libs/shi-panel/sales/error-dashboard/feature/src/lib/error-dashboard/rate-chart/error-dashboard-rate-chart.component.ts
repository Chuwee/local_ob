/* eslint-disable @typescript-eslint/naming-convention */
import { ChartComponent } from '@admin-clients/shared/common/ui/components';
import {
    ErrorDashboardService, errorResponsibles, legendColors, pointStyle, totalBorderDash
} from '@admin-clients/shi-panel/sales/error-dashboard-data-access';
import { ChangeDetectionStrategy, Component, computed, inject, input, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { TranslateService } from '@ngx-translate/core';
import Chart, { ChartConfiguration, ChartEvent, InteractionItem } from 'chart.js/auto';
import moment from 'moment';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: {
        // eslint-disable-next-line @typescript-eslint/naming-convention
        '[style.display]': '$lineChartConfig()?.data?.labels?.length > 0 ? "block" : "none"'
    },
    imports: [ChartComponent],
    selector: 'app-error-dashboard-rate-chart',
    templateUrl: './error-dashboard-rate-chart.component.html'
})
export class ErrorDashboardRateChartComponent {
    readonly #translateSrv = inject(TranslateService);
    readonly #errorDashboardSrv = inject(ErrorDashboardService);
    readonly #$errorDashboardData = toSignal(this.#errorDashboardSrv.errorDashboard.getErrorDashboardData$());
    readonly #legendColors = [...legendColors];

    readonly $errorResponsibles = input<typeof errorResponsibles>(null, { alias: 'errorResponsibles' });

    readonly $lineChartConfig: Signal<ChartConfiguration> = computed(() => ({
        type: 'line',
        data: {
            labels: this.#$errorDashboardData()?.by_period?.map(day => (moment(new Date(day.date))).format('DD-MM-YYYY')),
            datasets: [{
                label: this.#translateSrv.instant('ERROR_DASHBOARD.INFOS.ERROR_RATE_TOTAL'),
                data: this.#$errorDashboardData()?.by_period?.map(
                    day => day.total_sales === 0 ? 0 : ((day.total_errors || 0) / day.total_sales * 100).toFixed(2)
                ),
                borderColor: this.#legendColors[0],
                backgroundColor: this.#legendColors[0],
                borderDash: totalBorderDash,
                pointStyle
            }].concat(this.$errorResponsibles().map(
                (responsible, index) => ({
                    label: this.#translateSrv.instant('ERROR_DASHBOARD.INFOS.ERROR_RATE_' + responsible),
                    data: this.#$errorDashboardData()?.by_period?.map(day => day.total_sales === 0 ? 0 :
                        ((day.count_by_responsible.find(c => c.responsible === responsible)?.count || 0) / day.total_sales * 100).toFixed(2)
                    ),
                    borderColor: this.#legendColors[index + 1],
                    backgroundColor: this.#legendColors[index + 1],
                    borderDash: [],
                    pointStyle
                })
            ))
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    align: 'start',
                    labels: {
                        usePointStyle: true,
                        padding: 20,
                        color: getComputedStyle(document.documentElement).getPropertyValue('--ob-theme-color-black').trim(),
                        font: {
                            weight: 400,
                            size: 14,
                            family: 'Nunito Sans',
                            lineHeight: 1.5
                        }
                    },
                    onHover(event: ChartEvent) {
                        (event.native?.target as HTMLCanvasElement).style.cursor = 'pointer';
                    },
                    onLeave(event: ChartEvent) {
                        (event.native?.target as HTMLCanvasElement).style.cursor = 'default';
                    }
                }
            },
            scales: {
                y: {
                    ticks: {
                        callback: value => `${value}%`
                    }
                },
                x: {
                    ticks: {
                        align: 'end',
                        color: getComputedStyle(document.documentElement).getPropertyValue('--ob-theme-color-grey').trim(),
                        font: {
                            family: 'Nunito Sans',
                            weight: 400,
                            size: 12
                        }
                    }
                }
            },
            elements: {
                line: {
                    borderWidth: 1.5
                }
            },
            onHover: (event: ChartEvent, chartElement: InteractionItem[], chart: Chart) => {
                chart.data.datasets.forEach((dataset, index) => {
                    dataset.borderWidth = (chartElement.length && chartElement[0].datasetIndex === index) ? 2.5 : 1;
                });
                chart.update('none');
            },
            onLeave: event => {
                const chart = event.chart;
                chart.data.datasets.forEach(dataset => {
                    dataset.borderWidth = 1;
                });
                chart.update('none');
            }
        }
    }) as ChartConfiguration);
}
