import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterOutlet } from '@angular/router';

@Component({
    selector: 'app-customer-season-ticket-details',
    imports: [RouterOutlet, NavTabsMenuComponent],
    templateUrl: './customer-season-ticket-details.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerSeasonTicketDetailsComponent {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);

    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$());
    readonly $showAutoRenewal = computed(() => this.$seasonTicket()?.settings.operative.renewal?.automatic);

}
