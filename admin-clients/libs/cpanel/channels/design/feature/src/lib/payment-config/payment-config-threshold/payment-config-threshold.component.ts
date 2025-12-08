import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import type { ThresholdFormGroup } from '@admin-clients/cpanel/channels/data-access';
import { CurrencyInputComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, computed, input, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { first } from 'rxjs';

@Component({
    selector: 'app-payment-config-threshold',
    templateUrl: './payment-config-threshold.component.html',
    imports: [
        TranslatePipe, CurrencyInputComponent, ReactiveFormsModule, MaterialModule,
        FormControlErrorsComponent, CurrencyInputComponent, LocalCurrencyPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentConfigThresholdComponent implements OnInit {
    readonly $form = input.required<ThresholdFormGroup>({ alias: 'form' });

    readonly $firstAmountValue = computed(() => this.$form().controls.amount.value);
    valueChanged = false;

    ngOnInit(): void {
        this.$form().controls.amount.valueChanges.pipe(first()).subscribe(() => this.valueChanged = true);
    }

}
