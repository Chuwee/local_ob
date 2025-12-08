import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    GetSeasonTicketSessionsRequest, seasonTicketSessionsProviders,
    SeasonTicketSessionsService
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { TicketsBaseService } from '@admin-clients/shared/common/data-access';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first } from 'rxjs';
import {
    CustomerTransferSeasonTicketsSessionListComponent
} from './session-list/customer-transfer-season-tickets-session-list.component';

@Component({
    selector: 'app-customer-transfer-season-tickets',
    templateUrl: './customer-transfer-season-tickets.component.html',
    styleUrls: ['./customer-transfer-season-tickets.component.scss'],
    imports: [
        FlexLayoutModule,
        MatIconModule,
        TranslatePipe,
        CustomerTransferSeasonTicketsSessionListComponent,
        FormContainerComponent
    ],
    providers: [seasonTicketSessionsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerTransferSeasonTicketsComponent {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #seasonTicketSessionSrv = inject(SeasonTicketSessionsService);
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly #destroyRef = inject(DestroyRef);

    readonly $selectedSeat = toSignal(this.#ticketsSrv.ticketDetail.get$().pipe(filter(Boolean)));

    request: GetSeasonTicketSessionsRequest;

    constructor() {
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(filter(Boolean), takeUntilDestroyed())
            .subscribe(() => {
                this.#loadSessions();
            });
    }

    requestChangedHandler(request: GetSeasonTicketSessionsRequest): void {
        this.request = request;
        this.#loadSessions();
    }

    #loadSessions(): void {
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(first(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(seasonTicket => {
                if (this.request) {
                    this.#seasonTicketSessionSrv.sessions.load(String(seasonTicket.id), this.request);
                }
            });
    }
}
