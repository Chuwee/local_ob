import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
    imports: [MatIconModule],
    selector: 'app-error-dashboard-summary',
    templateUrl: './error-dashboard-summary.component.html',
    styleUrls: ['./error-dashboard-summary.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorDashboardSummaryComponent {
    $value = input<number>(null, { alias: 'value' });
    $previousValue = input<number>(null, { alias: 'previousValue' });
    $valueSuffix = input<string>(null, { alias: 'valueSuffix' });
    $increment = computed(() => this.$value() - this.$previousValue());
    $incrementAbs = computed(() => Math.abs(this.$increment()));
    $roundedValue = computed(() => Math.round((this.$value() * 100)) / 100);
    $roundedIncrement = computed(() => Math.round((this.$incrementAbs() * 100)) / 100);
}
