import { TicketsBaseService } from '@admin-clients/shared/common/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatDrawer, MatDrawerContainer, MatDrawerContent } from '@angular/material/sidenav';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { CustomerSeasonTicketListComponent } from '../list/customer-season-ticket-list.component';

@Component({
    selector: 'app-customer-season-tickets-container',
    imports: [RouterOutlet, MatDrawer, MatDrawerContainer, MatDrawerContent, CustomerSeasonTicketListComponent,
        EmptyStateComponent, TranslatePipe
    ],
    templateUrl: './customer-season-tickets-container.component.html',
    styleUrls: ['./customer-season-tickets-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerSeasonTicketsContainerComponent {
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly $seasonTicketProducts = toSignal(this.#ticketsSrv.ticketList.getData$());
}