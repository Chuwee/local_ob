
import { TicketsBaseService } from '@admin-clients/shared/common/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatDrawer, MatDrawerContainer, MatDrawerContent } from '@angular/material/sidenav';
import { RouterOutlet } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { CustomerTicketsListComponent } from '../list/customer-tickets-list.component';

@Component({
    selector: 'app-customer-tickets-container',
    standalone: true,
    imports: [RouterOutlet, MatDrawer, MatDrawerContainer, MatDrawerContent,
        EmptyStateComponent, TranslateModule, CustomerTicketsListComponent
    ],
    templateUrl: './customer-tickets-container.component.html',
    styleUrls: ['./customer-tickets-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerTicketsContainerComponent implements OnDestroy {
    readonly #ticketsSrv = inject(TicketsBaseService);

    readonly $ticketProducts = toSignal(this.#ticketsSrv.ticketList.getData$());

    ngOnDestroy(): void {
        this.#ticketsSrv.ticketList.clear();
    }

}