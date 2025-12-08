
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    imports: [MatIconModule, TranslatePipe],
    selector: 'app-information-pill',
    templateUrl: './information-pill.component.html',
    styleUrls: ['./information-pill.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class InformationPillComponent {
    $title = input<string>(null, { alias: 'title' });
    $value = input<number>(null, { alias: 'value' });
    $previousValue = input<number>(null, { alias: 'previousValue' });
    $valueSuffix = input<string>(null, { alias: 'valueSuffix' });
    $increment = computed(() => this.$value() - this.$previousValue());
    $incrementAbs = computed(() => Math.abs(this.$increment()));
    $roundedValue = computed(() => Math.round((this.$value() * 100)) / 100);
    $roundedIncrement = computed(() => Math.round((this.$incrementAbs() * 100)) / 100);
}
