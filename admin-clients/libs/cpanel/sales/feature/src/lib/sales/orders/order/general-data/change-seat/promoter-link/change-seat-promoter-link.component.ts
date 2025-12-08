import { OrdersService } from '@admin-clients/cpanel-sales-data-access';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatFormField, MatLabel, MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, firstValueFrom, map } from 'rxjs';

@Component({
    selector: 'app-change-seat-promoter-link',
    imports: [
        MatIcon, MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatFormField, MatLabel,
        TranslatePipe, MatButton, MatTooltip, MatSelect, MatOption, MatProgressSpinner, UpperCasePipe, ReactiveFormsModule
    ],
    templateUrl: './change-seat-promoter-link.component.html',
    styleUrls: ['../change-seat-link.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PromoterLinkComponent {
    readonly #ordersService = inject(OrdersService);

    readonly eventCtrl = new FormControl<number | null>(null);

    readonly $loading = toSignal(this.#ordersService.changeSeat.loading$());
    readonly $events = toSignal(this.#ordersService.changeSeat.getData$().pipe(
        filter(Boolean),
        map(data => data?.order_event_change_seat_config.map(config => config.events).flat() ?? [])
    ), { initialValue: [] });

    constructor() {
        effect(() => this.eventCtrl.setValue(this.$events().at(0)?.id ?? null));
    }

    async openPromoterLinkInNewTab(): Promise<void> {
        const eventId = this.eventCtrl.value;
        const code = (await firstValueFrom(this.#ordersService.getOrderDetail$()))?.code;
        if (!code || !eventId) return;

        this.#ordersService.changeSeat.generatePromoterUrl$(code, eventId).subscribe(url => window.open(url, '_blank'));
    }
}
