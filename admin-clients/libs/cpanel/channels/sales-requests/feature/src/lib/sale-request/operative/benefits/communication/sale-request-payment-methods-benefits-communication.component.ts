import { SaleRequestGatewayBenefitBinGroup } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { openDialog } from '@admin-clients/shared/common/ui/components';
import { cloneObject } from '@admin-clients/shared/utility/utils';
import { UpperCasePipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, effect, inject, input, model, ViewContainerRef
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import {
    VmSaleRequestGatewayBenefit, VmSaleRequestGatewayBenefitContentType
} from '../vm-sale-request-gateway-benefit.model';
import {
    SaleRequestPaymentMethodsBenefitsBadgeComponent
} from './badge/sale-request-payment-methods-benefits-badge.component';
import {
    SaleRequestPaymentMethodsBenefitsDescriptionComponent
} from './description/sale-request-payment-methods-benefits-description.component';
import {
    SaleRequestPaymentMethodsBenefitsCommunicationDialogComponent
} from './dialog/sale-request-payment-methods-benefits-communication-dialog.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatButton, MatIcon, TranslatePipe, SaleRequestPaymentMethodsBenefitsBadgeComponent,
        SaleRequestPaymentMethodsBenefitsDescriptionComponent, UpperCasePipe
    ],
    selector: 'app-sale-request-payment-methods-benefits-communication',
    templateUrl: './sale-request-payment-methods-benefits-communication.component.html',
    styleUrl: './sale-request-payment-methods-benefits-communication.component.scss'
})
export class SaleRequestPaymentMethodsBenefitsCommunicationComponent {
    readonly #dialogSrv = inject(MatDialog);
    readonly #viewContainerRef = inject(ViewContainerRef);

    readonly $vmSaleRequestGatewayBenefit = input.required<VmSaleRequestGatewayBenefit>({ alias: 'vmSaleRequestGatewayBenefit' });
    readonly $postContents = model.required<SaleRequestGatewayBenefitBinGroup['checkout_communication_elements']>({ alias: 'postContents' });

    constructor() {
        effect(() => {
            const vmSaleRequestGatewayBenefit = this.$vmSaleRequestGatewayBenefit();

            if (vmSaleRequestGatewayBenefit.beingModified) return;

            const [binGroup] = vmSaleRequestGatewayBenefit.bin_groups;
            const { checkout_communication_elements: checkoutCommunicationElements } = binGroup;
            this.$postContents.set(cloneObject(checkoutCommunicationElements));
        });
    }

    add(): void {
        openDialog(this.#dialogSrv, SaleRequestPaymentMethodsBenefitsCommunicationDialogComponent, this.$postContents(),
            this.#viewContainerRef).beforeClosed()
            .subscribe(contentType => {
                if (!contentType) return;

                let newContent: SaleRequestGatewayBenefitBinGroup['checkout_communication_elements'];
                if (contentType === 'BADGE') {
                    newContent = {
                        badge: {
                            text: {},
                            background_color: null,
                            text_color: null
                        }
                    };
                } else if (contentType === 'DESCRIPTION') {
                    newContent = {
                        description: {}
                    };
                }

                this.$postContents.update(contents => {
                    contents ??= {};
                    return {
                        ...contents,
                        ...newContent
                    };
                });
            });
    }

    removeContent(contentType: VmSaleRequestGatewayBenefitContentType): void {
        if (!this.$vmSaleRequestGatewayBenefit().beingModified) return;

        this.$postContents.update(contents => {
            const contentsEntries = Object.entries(contents);
            if (contentsEntries.length === 1) return null;

            if (contentType === 'BADGE') {
                const { description } = contents;
                return { description };
            } else { //DESCRIPTION
                const { badge } = contents;
                return { badge };
            }
        });
    }
}
