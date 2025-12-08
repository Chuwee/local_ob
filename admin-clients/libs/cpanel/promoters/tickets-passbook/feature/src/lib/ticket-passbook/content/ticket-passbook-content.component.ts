import { TicketsPassbookService } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
    selector: 'app-ticket-passbook-content',
    templateUrl: './ticket-passbook-content.component.html',
    styleUrls: ['./ticket-passbook-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketPassbookContentComponent {
    private _route = inject(ActivatedRoute);
    private _router = inject(Router);
    private _ticketsPassbookService = inject(TicketsPassbookService);

    deepPath$ = getDeepPath$(this._router, this._route);
    ticketPassbook$ = this._ticketsPassbookService.getTicketPassbook$();

}
