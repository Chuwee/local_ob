import { VmOrderWithFields } from '@admin-clients/cpanel-sales-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { IonicModule, NavController } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'transaction-card',
    templateUrl: './transaction-card.component.html',
    styleUrls: ['./transaction-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [CommonModule, IonicModule, TranslatePipe, DateTimePipe, LocalCurrencyPipe, EllipsifyDirective]
})
export class TransactionCardComponent {
    private readonly _navCtrl = inject(NavController);

    @Input() readonly transaction: VmOrderWithFields;
    @Output() readonly tap = new EventEmitter<any>();
    readonly dateTimeFormats = DateTimeFormats;

    goToTransactionDetail(): void {
        this._navCtrl.navigateForward(
            `transaction-detail?order_code=${this.transaction.code}`
        );
    }
}
