import { TicketDetail } from '@admin-clients/shared/common/data-access';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-ticket-buyer-data',
    imports: [TranslatePipe],
    templateUrl: './buyer-data.component.html',
    styleUrls: ['./buyer-data.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BuyerDataComponent {
    readonly $ticket = input.required<TicketDetail>({ alias: 'ticket' });
} 