import { SaleRequestGatewayBenefitBinGroup } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatListOption, MatSelectionList } from '@angular/material/list';
import { TranslatePipe } from '@ngx-translate/core';
import { VmSaleRequestGatewayBenefitContentType } from '../../vm-sale-request-gateway-benefit.model';

type Option = {
    value: VmSaleRequestGatewayBenefitContentType;
    label: string;
    description: string;
    disabled: boolean;
    image: string;
};

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, MatButton, MatDialogActions, MatDialogContent, MatDialogTitle, MatIconButton, MatIcon, MatSelectionList,
        ReactiveFormsModule, EllipsifyDirective, MatListOption, ErrorMessage$Pipe, AsyncPipe
    ],
    selector: 'app-sale-request-payment-methods-benefits-communication-dialog',
    templateUrl: './sale-request-payment-methods-benefits-communication-dialog.component.html'
})
export class SaleRequestPaymentMethodsBenefitsCommunicationDialogComponent extends ObDialog<
    SaleRequestPaymentMethodsBenefitsCommunicationDialogComponent, SaleRequestGatewayBenefitBinGroup['checkout_communication_elements'],
    VmSaleRequestGatewayBenefitContentType>{

    readonly optionCtrl = inject(FormBuilder)
        .nonNullable.control({ value: null as VmSaleRequestGatewayBenefitContentType[],
            disabled: !!this.data?.badge && !!this.data?.description }, Validators.required);

    readonly options: Option[] = [
        {
            value: 'BADGE',
            label: 'FORMS.LABELS.BADGE',
            description: 'SALE_REQUESTS.PAYMENT_METHODS.BENEFITS.FORMS.INFOS.ADD_COMMUNICATION_BADGE_CONTENT',
            disabled: !!this.data?.badge,
            image: 'assets/view-info-contents-options/tag.svg'
        },
        {
            value: 'DESCRIPTION',
            label: 'FORMS.LABELS.DESCRIPTION',
            description: 'SALE_REQUESTS.PAYMENT_METHODS.BENEFITS.FORMS.INFOS.ADD_COMMUNICATION_DESCRIPTION_CONTENT',
            disabled: !!this.data?.description,
            image: 'assets/view-info-contents-options/highlighted.svg'
        }
    ];

    constructor() {
        super(DialogSize.MEDIUM, true);
    }

    save(): void {
        if (this.optionCtrl.valid) {
            this.dialogRef.close(this.optionCtrl.value[0]);
        } else {
            this.optionCtrl.markAllAsTouched();
            this.optionCtrl.setValue(this.optionCtrl.getRawValue());
        }
    }

    close(): void {
        this.dialogRef.close();
    }
}
