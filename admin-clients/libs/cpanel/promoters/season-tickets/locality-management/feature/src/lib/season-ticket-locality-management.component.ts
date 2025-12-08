import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { filter, map } from 'rxjs';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';

@Component({
    selector: 'app-season-ticket-locality-management',
    templateUrl: './season-ticket-locality-management.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketLocalityManagementComponent {
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly $showSecondaryMarket = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean), map(entity => entity.settings?.allow_secondary_market)));
    readonly $allowReleaseSeat = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(filter(Boolean), map(seasonTicket => seasonTicket.settings.operative.allow_release_seat)));
    readonly deepPath$ = getDeepPath$(this.#router, this.#route);

}
