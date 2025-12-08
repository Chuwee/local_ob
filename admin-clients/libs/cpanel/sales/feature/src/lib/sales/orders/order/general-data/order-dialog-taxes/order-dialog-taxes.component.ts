import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { OrderPriceRowComponent } from '../order-price-row/order-price-row.component';

@Component({
    selector: 'app-dialog-taxes',
    templateUrl: './order-dialog-taxes.component.html',
    styleUrls: ['./order-dialog-taxes.component.scss'],
    imports: [
        TranslatePipe, MatIcon, MatDialogTitle, MatDialogContent, MatDialogActions, MatIconButton, MatButton, OrderPriceRowComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderDialogTaxesComponent {
    readonly #ref = inject<MatDialogRef<OrderDialogTaxesComponent>>(MatDialogRef);
    readonly #data = inject<{
        totalTaxes: number;
        ccy: string;
        itemsTaxesTotal: number;
        productsTaxesTotal: number;
        surchargesTaxesTotal: number;
    }>(MAT_DIALOG_DATA);

    readonly totalTaxes = this.#data.totalTaxes;
    readonly ccy = this.#data.ccy;
    readonly itemsTaxesTotal = this.#data.itemsTaxesTotal;
    readonly productsTaxesTotal = this.#data.productsTaxesTotal;
    readonly surchargesTaxesTotal = this.#data.surchargesTaxesTotal;

    constructor() {
        this.#ref.addPanelClass(DialogSize.MEDIUM);
        this.#ref.disableClose = false;
    }

    close(): void {
        this.#ref.close();
    }
}
