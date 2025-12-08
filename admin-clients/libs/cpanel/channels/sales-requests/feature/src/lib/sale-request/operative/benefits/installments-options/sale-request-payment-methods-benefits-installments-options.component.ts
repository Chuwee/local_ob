import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { SaleRequestGatewayBenefitBinGroup } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component, effect, inject, input, model, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroupDirective, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { VmSaleRequestGatewayBenefit } from '../vm-sale-request-gateway-benefit.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent, MatError, ObFormFieldLabelDirective,
        MatLabel, MatSelect, MatOption, MatFormField
    ],
    selector: 'app-sale-request-payment-methods-benefits-installments-options',
    templateUrl: './sale-request-payment-methods-benefits-installments-options.component.html'
})
export class SaleRequestPaymentMethodsBenefitsInstallmentsOptionsComponent implements OnInit {
    readonly #formGroup = inject(FormGroupDirective);
    readonly #fb = inject(FormBuilder);
    readonly #translateSrv = inject(TranslateService);

    readonly $postInstallments = model.required<SaleRequestGatewayBenefitBinGroup['installment_options']>({ alias: 'postInstallments' });
    readonly $vmSaleRequestGatewayBenefit = input.required<VmSaleRequestGatewayBenefit>({ alias: 'vmSaleRequestGatewayBenefit' });

    // If formGroup is needed, make it formGroup with different controls, this is just an example
    readonly installmentsCtrl = this.#fb.control([] as number[], [Validators.required]);
    readonly installmentsList = Array.from({ length: 37 }, (_, i) => ({
        value: i + 1,
        label: (i + 1 === 1) ? this.#translateSrv.instant('FORMS.LABELS.MONTH_AMOUNT', { value: i + 1 }) :
            this.#translateSrv.instant('FORMS.LABELS.MONTHS_AMOUNT', { value: i + 1 })
    }));

    constructor() {
        effect(() => {
            const vmSaleRequestGatewayBenefit = this.$vmSaleRequestGatewayBenefit();
            if (vmSaleRequestGatewayBenefit.beingModified) {
                this.installmentsCtrl.enable({ emitEvent: false });
            } else {
                const [binGroup] = vmSaleRequestGatewayBenefit.bin_groups;
                this.installmentsCtrl.reset(binGroup.installment_options, { emitEvent: false });
                this.installmentsCtrl.disable({ emitEvent: false });
                this.$postInstallments.set(binGroup.installment_options);
            }
        });

        this.installmentsCtrl.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(value => this.$postInstallments.set(value));
    }

    ngOnInit(): void {
        this.#formGroup.control.addControl('installmentsCtrl', this.installmentsCtrl, { emitEvent: false });
    }
}
