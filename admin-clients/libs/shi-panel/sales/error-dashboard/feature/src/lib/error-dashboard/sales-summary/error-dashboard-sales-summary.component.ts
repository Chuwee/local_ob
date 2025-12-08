
import { ErrorDashboardService, errorResponsibles } from '@admin-clients/shi-panel/sales/error-dashboard-data-access';
import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { InformationPillComponent } from '../information-pill/information-pill.component';
import { ErrorDashboardSummaryComponent } from '../summary/error-dashboard-summary.component';

@Component({
    imports: [ErrorDashboardSummaryComponent, InformationPillComponent],
    selector: 'app-error-dashboard-sales-summary',
    templateUrl: './error-dashboard-sales-summary.component.html',
    styleUrls: ['./error-dashboard-sales-summary.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorDashboardSalesSummaryComponent {
    readonly #errorDashboardSrv = inject(ErrorDashboardService);
    readonly #$errorDashboardData = toSignal(this.#errorDashboardSrv.errorDashboard.getErrorDashboardData$());

    readonly $errorResponsibles = input<typeof errorResponsibles>(null, { alias: 'errorResponsibles' });

    readonly $totalErrors = computed(() => this.#$errorDashboardData()?.overall.total_errors);
    readonly $previousTotalErrors = computed(() => this.#$errorDashboardData()?.previous_overall.total_errors);
    readonly $responsibleOverallCounts = computed(() => this.$errorResponsibles().map(resp => {
        const ov = this.#$errorDashboardData()?.overall.by_responsible.find(o => o.responsible === resp);
        const prevOv = this.#$errorDashboardData()?.previous_overall.by_responsible.find(o => o.responsible === resp);
        return {
            responsible: resp,
            overallCount: ov?.count || 0,
            prevOverallCount: prevOv?.count || 0
        };
    }));
}
