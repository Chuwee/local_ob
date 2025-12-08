import { TicketDetail } from '@admin-clients/shared/common/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, inject } from '@angular/core';
import { Router } from '@angular/router';
import { IonicModule, NavController } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { formattedCode } from '../../../helpers/string.utils';

@Component({
    selector: 'ticket-card',
    templateUrl: './ticket-card.component.html',
    styleUrls: ['./ticket-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [CommonModule, IonicModule, TranslatePipe, DateTimePipe]
})
export class TicketCardComponent {
    private readonly _navCtrl = inject(NavController);
    private readonly _router = inject(Router);
    @Input() readonly ticket: TicketDetail;
    readonly dateTimeFormats = DateTimeFormats;

    get getCssClass(): string {
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
        return formattedCode(this.ticket.ticket.barcode.code);
    }

    goToTicketDetail(): void {
        this._router.navigate(['ticket-detail'],
            {
                queryParams: {
                    order_code: this.ticket.order.code,
                    id: this.ticket.id
                }
            });
    }
}
