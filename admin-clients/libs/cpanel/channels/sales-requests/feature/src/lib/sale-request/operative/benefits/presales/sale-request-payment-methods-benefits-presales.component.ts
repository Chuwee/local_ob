import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import {
    SaleRequestGatewayBenefit, SaleRequestGatewayBenefitBinGroup
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
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
import { SaleRequestPaymentMethodsBenefitsValidityComponent } from '../validity/sale-request-payment-methods-benefits-validity.component';
import {
    VmSaleRequestGatewayBenefit
} from '../vm-sale-request-gateway-benefit.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        SaleRequestPaymentMethodsBenefitsBinsComponent, SaleRequestPaymentMethodsBenefitsValidityComponent, ReactiveFormsModule,
        TranslatePipe, MatButton, MatDivider, MatIcon, SaleRequestPaymentMethodsBenefitsCommunicationComponent
    ],
    selector: 'app-sale-request-payment-methods-benefits-presales',
    templateUrl: './sale-request-payment-methods-benefits-presales.component.html'
})
export class SaleRequestPaymentMethodsBenefitsPresalesComponent {
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
    postValidityPeriod: SaleRequestGatewayBenefitBinGroup['validity_period'];
    postCustomValidPeriod: SaleRequestGatewayBenefitBinGroup['custom_valid_period'];
    postContents: SaleRequestGatewayBenefitBinGroup['checkout_communication_elements'];

    save(): void {
        if (this.form.valid) {
            const benefit: SaleRequestGatewayBenefit = {
                type: 'PRESALE',
                bin_groups: [
                    {
                        bins: this.postBins,
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
            scrollIntoFirstInvalidFieldOrErrorMsg(document, null, '#benefits-presales');
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
