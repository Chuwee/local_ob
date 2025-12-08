import { ChartComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import {
    chartColors, chartDatasetLabels, ErrorDashboardService, errorResponsibles
} from '@admin-clients/shi-panel/sales/error-dashboard-data-access';
import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, input, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { TranslateService } from '@ngx-translate/core';
import { ChartConfiguration } from 'chart.js/auto';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        NgTemplateOutlet, TabDirective, TabsMenuComponent, ChartComponent
    ],
    selector: 'app-error-dashboard-cause-chart',
    styleUrl: './error-dashboard-cause-chart.component.scss',
    templateUrl: './error-dashboard-cause-chart.component.html'
})
export class ErrorDashboardCauseChartComponent {
    readonly #errorDashboardSrv = inject(ErrorDashboardService);
    readonly #translateSrv = inject(TranslateService);
    readonly #$errorDashboardData = toSignal(this.#errorDashboardSrv.errorDashboard.getErrorDashboardData$());

    readonly $errorResponsibles = input<typeof errorResponsibles>(null, { alias: 'errorResponsibles' });

    readonly pieChartColors = [...chartColors];
    readonly $totalOverallByResponsible = computed(() => [
        { responsible: 'TOTAL', error_causes: this.#$errorDashboardData()?.overall.by_cause }
    ].concat(this.$errorResponsibles().length > 1 ? this.#$errorDashboardData()?.overall.by_responsible : []));

    readonly $pieChartConfigs: Signal<ChartConfiguration[]> = computed(() =>
        this.$totalOverallByResponsible()?.map(i => {
            if (i.error_causes?.length === 0) return undefined;
            const sortedErrorCauses = i.error_causes.sort((a, b) => b.count - a.count);

            return {
                type: 'pie',
                data: {
                    labels: sortedErrorCauses.map(ec => this.#translateSrv.instant('ERROR_DASHBOARD.INFOS.ERROR_CAUSE_' + ec.cause)),
                    datasets: [{
                        label: chartDatasetLabels,
                        data: sortedErrorCauses.map(ec => ec.count),
                        backgroundColor: this.pieChartColors
                    }]
                },
                options: {
                    plugins: {
                        legend: {
                            display: false
                        }
                    }
                }
            };
        }).filter(config => !!config) as ChartConfiguration[]
    );
}
