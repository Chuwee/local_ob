import { TicketsService } from '@admin-clients/cpanel-sales-data-access';
import { EventType, TicketDetail } from '@admin-clients/shared/common/data-access';
import { GoBackComponent } from '@admin-clients/shared/common/ui/components';
import { ObfuscatePattern, ObfuscateStringPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-ticket-details',
    templateUrl: './ticket-details.component.html',
    styleUrls: ['./ticket-details.component.css'],
    imports: [GoBackComponent, RouterOutlet, ObfuscateStringPipe, AsyncPipe, TranslatePipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TicketDetailsComponent implements OnInit, OnDestroy {
    readonly eventType = EventType;

    obfuscatePattern = ObfuscatePattern;
    ticketDetail$: Observable<TicketDetail>;

    constructor(private _ticketsService: TicketsService) { }

    ngOnInit(): void {
        this.ticketDetail$ = this._ticketsService.ticketDetail.get$();
    }

    ngOnDestroy(): void {
        this._ticketsService.ticketDetail.clear();
    }

}
