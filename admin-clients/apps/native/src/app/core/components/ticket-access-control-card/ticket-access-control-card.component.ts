import { TicketDetail } from '@admin-clients/shared/common/data-access';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'ticket-access-control-card',
    templateUrl: './ticket-access-control-card.component.html',
    styleUrls: ['./ticket-access-control-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [CommonModule, IonicModule, TranslatePipe]
})
export class TicketAccessControlCardComponent {
    // TODO: Cambiar este tipado quizá a InternalBarcode
    @Input() readonly ticket: TicketDetail;
    @Output() readonly tap = new EventEmitter<TicketDetail>();

    get getCssClass(): string {
        // TODO: Esta función está penada para la tarjeta de entrada, aquí creo que se reciben más estados.
        const cssClass = {
            validated: 'validated',
            validated_out: 'not-validated',
            default: 'not-validated'
        };

        return (
            cssClass[
            this.ticket?.ticket?.validations?.[
                this.ticket?.ticket?.validation_last_date
            ]?.status.toLowerCase()
            ] || cssClass.default
        );
    }

    get formattedCode(): string {
        return this.ticket.order.code.replace(/^.{4}/, '****');
    }

    onTap(): void {
        this.tap.emit(this.ticket);
    }
}
