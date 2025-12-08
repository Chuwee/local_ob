import { TicketPassbook, TicketsPassbookService } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-ticket-passbook-details',
    templateUrl: './ticket-passbook-details.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe,
        GoBackComponent,
        NavTabsMenuComponent,
        RouterOutlet
    ]
})
export class TicketPassbookDetailsComponent implements OnInit, OnDestroy {

    ticketPassbook$: Observable<TicketPassbook>;

    constructor(private _ticketsPassbookService: TicketsPassbookService) { }

    ngOnInit(): void {
        this.ticketPassbook$ = this._ticketsPassbookService.getTicketPassbook$();
    }

    ngOnDestroy(): void {
        this._ticketsPassbookService.clearTicketPassbook();
    }

}
