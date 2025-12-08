import { ErrorDashboardService, errorResponsibles } from '@admin-clients/shi-panel/sales/error-dashboard-data-access';
import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { InformationPillComponent } from '../information-pill/information-pill.component';
import { ErrorDashboardSummaryComponent } from '../summary/error-dashboard-summary.component';

@Component({
    imports: [InformationPillComponent, ErrorDashboardSummaryComponent],
    selector: 'app-error-dashboard-rate-summary',
    templateUrl: './error-dashboard-rate-summary.component.html',
    styleUrls: ['./error-dashboard-rate-summary.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorDashboardRateSummaryComponent {
    readonly #errorDashboardSrv = inject(ErrorDashboardService);
    readonly #$errorDashboardData = toSignal(this.#errorDashboardSrv.errorDashboard.getErrorDashboardData$());
    readonly #$totalSales = computed(() => this.#$errorDashboardData()?.overall?.total_sales);
    readonly #$totalErrors = computed(() => this.#$errorDashboardData()?.overall.total_errors);

    readonly $errorResponsibles = input<typeof errorResponsibles>(null, { alias: 'errorResponsibles' });

    readonly $totalErrorRate = computed(() => this.#$totalSales() === 0 ? 0 : (this.#$totalErrors() / this.#$totalSales() * 100));
    readonly $previousTotalErrors = computed(() => this.#$errorDashboardData()?.previous_overall.total_errors);
    readonly $previousTotalErrorRate = computed(() => this.#$errorDashboardData()?.previous_overall.total_sales === 0 ? 0 :
        this.$previousTotalErrors() / this.#$errorDashboardData()?.previous_overall.total_sales * 100
    );

    readonly $responsibleOverallErrorRates = computed(() => this.$errorResponsibles().map(resp => {
        const ov = this.#$errorDashboardData()?.overall.by_responsible.find(o => o.responsible === resp);
        const prevOv = this.#$errorDashboardData()?.previous_overall.by_responsible.find(o => o.responsible === resp);
        return {
            responsible: resp,
            totalErrorRate:
                this.#$totalSales() === 0 ? 0 :
                    (ov?.count || 0) / this.#$totalSales() * 100,
            totalPrevErrorRate:
                this.#$errorDashboardData().previous_overall.total_sales === 0 ? 0 :
                    (prevOv?.count || 0) / this.#$errorDashboardData().previous_overall.total_sales * 100
        };
    }));
}
