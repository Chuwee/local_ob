import { OrdersService } from '@admin-clients/cpanel-sales-data-access';
import { CopyTextComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs';

@Component({
    selector: 'app-change-seat-buyer-link',
    imports: [
        MatIcon, MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle,
        TranslatePipe, CopyTextComponent
    ],
    templateUrl: './change-seat-buyer-link.component.html',
    styleUrls: ['../change-seat-link.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BuyerLinkComponent {
    readonly #ordersService = inject(OrdersService);

    readonly $urlsData = toSignal(this.#ordersService.changeSeat.getData$().pipe(
        filter(Boolean),
        map(data =>
            data.order_event_change_seat_config?.map(config =>
                ({ url: config.url, name: config.events.map(e => e.name).join(', ') })
            ) ?? []
        )
    ), { initialValue: [] });
}
