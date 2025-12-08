import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import { MatDialogActions, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { throwError } from 'rxjs';
import { VmSaleRequestGatewayBenefit } from '../vm-sale-request-gateway-benefit.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDialogTitle, MatIcon, MatIconButton, TranslatePipe, MatDialogContent, ReactiveFormsModule, MatError, MatFormField,
        MatLabel, MatOption, MatSelect, ObFormFieldLabelDirective, FormControlErrorsComponent, MatButton, MatDialogActions
    ],
    selector: 'app-sale-request-payment-methods-benefits-dialog',
    templateUrl: './sale-request-payment-methods-benefits-dialog.component.html'
})
export class SaleRequestPaymentMethodsBenefitsDialogComponent extends ObDialog<SaleRequestPaymentMethodsBenefitsDialogComponent,
    { benefits: VmSaleRequestGatewayBenefit[] }, VmSaleRequestGatewayBenefit['type']> {
    readonly #fb = inject(FormBuilder);
    readonly formControl = this.#fb.nonNullable.control(null as VmSaleRequestGatewayBenefit['type'], Validators.required);
    // The dialog can be open when the data benefits from outside are 0 or 1
    readonly $options = signal<VmSaleRequestGatewayBenefit['type'][]>(
        !this.data.benefits?.length ?
            ['PRESALE', 'INSTALLMENTS'] :
            (this.data.benefits[0].type === 'INSTALLMENTS' ? ['PRESALE'] : ['INSTALLMENTS'])
    );

    constructor() {
        super(DialogSize.MEDIUM, true);

        effect(() => {
            const options = this.$options();
            if (options.length === 1) this.formControl.setValue(options[0]);
        });
    }

    save(): void {
        if (this.formControl.valid) {
            this.dialogRef.close(this.formControl.value);
        } else {
            this.formControl.setValue(this.formControl.getRawValue());
            this.formControl.markAsTouched();
            throwError(() => new Error('Invalid form'));
        }
    }
}
