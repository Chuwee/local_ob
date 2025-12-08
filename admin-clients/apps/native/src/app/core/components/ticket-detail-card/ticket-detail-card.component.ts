import { OrderItem } from '@admin-clients/shared/common/data-access';
import { LocalCurrencyPipe, VariantTextPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'ticket-detail-card',
    templateUrl: './ticket-detail-card.component.html',
    styleUrls: ['./ticket-detail-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [CommonModule, IonicModule, TranslatePipe, LocalCurrencyPipe, VariantTextPipe]
})
export class TicketDetailCardComponent {
    @Input() readonly ticket: OrderItem;

    get getCssClass(): string {
        const cssClass = {
            validated: 'validated',
            validated_out: 'not-validated',
            default: 'not-validated'
        };

        if (this.ticket.ticket) {
            return (
                cssClass[
                this.ticket.ticket.validations?.[
                    this.ticket.ticket.validation_last_date
                ]?.status.toLowerCase()
                ] || cssClass.default
            );
        } else {
            // TO-DO: products validations still WIP
            return cssClass.default;
        }
    }

    get formattedCode(): string {
        if (this.ticket.ticket) {
            if (this.ticket.ticket?.barcode?.code) {
                return this.ticket.ticket.barcode.code.replace(/^.{4}/, '****');
            } else {
                return '-';
            }
        } else {
            // TO-DO: products barcodes still WIP
            return '-';
        }

    }
}
