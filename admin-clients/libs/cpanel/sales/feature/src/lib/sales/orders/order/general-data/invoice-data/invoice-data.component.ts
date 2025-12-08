import { InvoiceData } from '@admin-clients/shared/common/data-access';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-order-details-invoice-data',
    templateUrl: './invoice-data.component.html',
    styleUrl: './invoice-data.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [TranslatePipe]
})
export class OrderDetailsInvoiceDataComponent {
    readonly invoiceData = input<InvoiceData>();
}
