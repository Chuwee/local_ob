import { TicketTemplatesService } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
    selector: 'app-ticket-template-details',
    templateUrl: './ticket-template-details.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [GoBackComponent, NavTabsMenuComponent, RouterOutlet]
})
export class TicketTemplateDetailsComponent {
    readonly #ticketsTemplateService = inject(TicketTemplatesService);

    readonly $ticketTemplate = toSignal(this.#ticketsTemplateService.getTicketTemplate$().pipe(filter(Boolean)), { initialValue: null });
    readonly $isContentTabsEnabled = computed(() => !!this.$ticketTemplate()?.languages?.default);
    readonly $isHardTicketTemplate = computed(() => this.$ticketTemplate()?.design.format === 'HARD_TICKET_PDF');
}
