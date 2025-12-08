import { PriceTotalTaxes } from '@admin-clients/shared/common/data-access';
import { DialogSize, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { AbsoluteAmountPipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { OrderDialogTaxesComponent } from '../order-dialog-taxes/order-dialog-taxes.component';

@Component({
    selector: 'app-order-taxes-info',
    templateUrl: './order-taxes-info.component.html',
    styleUrls: ['./order-taxes-info.component.scss'],
    imports: [
        TranslatePipe, MatIcon, LocalCurrencyPipe, AbsoluteAmountPipe, MatIconButton
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderTaxesInfoComponent {
    readonly #matDialog = inject(MatDialog);
    readonly $totalTaxes = input<number>(0, { alias: 'totalTaxes' });
    readonly $ccy = input<string>('', { alias: 'ccy' });
    readonly $itemsTaxes = input<PriceTotalTaxes>({ items: -1, charges: -1 }, { alias: 'itemsTaxes' });
    readonly $productsTaxes = input<PriceTotalTaxes>({ items: -1, charges: -1 }, { alias: 'productsTaxes' });

    openDialog(): void {
        this.#matDialog.open(
            OrderDialogTaxesComponent,
            new ObMatDialogConfig({
                extraClasses: [DialogSize.MEDIUM],
                totalTaxes: this.$totalTaxes(),
                ccy: this.$ccy(),
                itemsTaxesTotal: this.$itemsTaxes().items,
                productsTaxesTotal: this.$productsTaxes().items,
                surchargesTaxesTotal: Math.max(0, this.$itemsTaxes().charges) + Math.max(0, this.$productsTaxes().charges)
            })
        );
    }
}
