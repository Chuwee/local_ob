import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import {
    SaleRequestGatewayBenefit, SaleRequestGatewayBenefitBinGroup
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { throwError } from 'rxjs';
import {
    SaleRequestPaymentMethodsBenefitsBinsComponent
} from '../bins/sale-request-payment-methods-benefits-bins.component';
import {
    SaleRequestPaymentMethodsBenefitsCommunicationComponent
} from '../communication/sale-request-payment-methods-benefits-communication.component';
import {
    SaleRequestPaymentMethodsBenefitsInstallmentsOptionsComponent
} from '../installments-options/sale-request-payment-methods-benefits-installments-options.component';
import { SaleRequestPaymentMethodsBenefitsValidityComponent } from '../validity/sale-request-payment-methods-benefits-validity.component';
import {
    VmSaleRequestGatewayBenefit
} from '../vm-sale-request-gateway-benefit.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        SaleRequestPaymentMethodsBenefitsBinsComponent, SaleRequestPaymentMethodsBenefitsValidityComponent, ReactiveFormsModule,
        TranslatePipe, MatButton, MatIcon, MatDivider, SaleRequestPaymentMethodsBenefitsInstallmentsOptionsComponent,
        SaleRequestPaymentMethodsBenefitsCommunicationComponent, UpperCasePipe
    ],
    selector: 'app-sale-request-payment-methods-benefits-installments',
    templateUrl: './sale-request-payment-methods-benefits-installments.component.html'
})
export class SaleRequestPaymentMethodsBenefitsInstallmentsComponent {
    readonly #fb = inject(FormBuilder);
    readonly form = this.#fb.nonNullable.group({});

    readonly $vmSaleRequestGatewayBenefit = input.required<VmSaleRequestGatewayBenefit>({ alias: 'vmSaleRequestGatewayBenefit' });
    readonly deleteOutput = output<void>();
    readonly saveOutput = output<SaleRequestGatewayBenefit>();
    readonly editOutput = output<void>();
    readonly cancelOutput = output<void>();

    //TODO: Delete when ready
    readonly isPreEnv = inject(ENVIRONMENT_TOKEN)?.env !== 'pro';

    postBins: SaleRequestGatewayBenefitBinGroup['bins'];
    postInstallmentsOptions: SaleRequestGatewayBenefitBinGroup['installment_options'];
    postValidityPeriod: SaleRequestGatewayBenefitBinGroup['validity_period'];
    postCustomValidPeriod: SaleRequestGatewayBenefitBinGroup['custom_valid_period'];
    postContents: SaleRequestGatewayBenefitBinGroup['checkout_communication_elements'];

    save(): void {
        if (this.form.valid) {
            const benefit: SaleRequestGatewayBenefit = {
                type: 'INSTALLMENTS',
                bin_groups: [
                    {
                        bins: this.postBins,
                        installment_options: this.postInstallmentsOptions,
                        validity_period: this.postValidityPeriod,
                        custom_valid_period: this.postCustomValidPeriod
                    }
                ]
            };
            if (this.postContents && this.isPreEnv) {
                benefit.bin_groups[0].checkout_communication_elements = this.postContents;
            }
            this.saveOutput.emit(benefit);
        } else {
            this.form.markAllAsTouched();
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document, null, '#benefits-installments');
            throwError(() => new Error('Invalid form'));
        }
    }

    cancel(): void {
        if (this.$vmSaleRequestGatewayBenefit().beingModified?.create) {
            this.deleteOutput.emit();
        } else {
            this.cancelOutput.emit();
        }
    }
}
