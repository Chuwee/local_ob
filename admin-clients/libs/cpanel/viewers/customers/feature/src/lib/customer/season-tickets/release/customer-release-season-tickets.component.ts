import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    GetSeasonTicketSessionsRequest,
    seasonTicketSessionsProviders,
    SeasonTicketSessionsService
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import {
    CustomerSeatManagementType
} from '@admin-clients/cpanel-viewers-customers-data-access';
import { TicketsBaseService } from '@admin-clients/shared/common/data-access';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexModule } from '@angular/flex-layout';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first } from 'rxjs';
import { CustomerReleaseSeasonTicketsSessionListComponent } from './session-list/customer-release-season-tickets-session-list.component';

@Component({
    selector: 'app-customer-release-season-tickets',
    templateUrl: './customer-release-season-tickets.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatExpansionModule,
        FormContainerComponent,
        MatProgressSpinnerModule,
        AsyncPipe,
        FlexModule,
        MatIconModule,
        TranslatePipe,
        CustomerReleaseSeasonTicketsSessionListComponent
    ],
    providers: [
        seasonTicketSessionsProviders
    ]
})
export class CustomerReleaseSeasonTicketsComponent {
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #seasonTicketSessionSrv = inject(SeasonTicketSessionsService);
    readonly #destroyRef = inject(DestroyRef);

    readonly $selectedSeat = toSignal(this.#ticketsSrv.ticketDetail.get$().pipe(filter(Boolean)));
    readonly isInProgress$ = this.#ticketsSrv.ticketList.loading$();

    readonly seatManagementType = CustomerSeatManagementType;
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
