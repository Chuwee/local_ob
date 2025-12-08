import { TicketDetail, EventType, ActionsHistoryType } from '@admin-clients/shared/common/data-access';
import { DialogSize, openDialog } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { LocalDateTimePipe, ObfuscateStringPipe, ObfuscatePattern } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, computed, inject, input, ViewContainerRef } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { TicketRelocationHistoricDialogComponent } from '../relocation-history/ticket-relocation-historic-dialog.component';

@Component({
    selector: 'app-ticket-data',
    imports: [TranslatePipe, LocalDateTimePipe, ObfuscateStringPipe, MatDivider],
    templateUrl: './ticket-data.component.html',
    styleUrls: ['./ticket-data.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TicketDataComponent {

    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #matDialog = inject(MatDialog);
    readonly #viewContainerRef = inject(ViewContainerRef);

    readonly $ticket = input.required<TicketDetail>({ alias: 'ticket' });

    readonly $address = computed(() => {
        const venue = this.$ticket()?.ticket?.allocation?.venue;
        const name = venue?.name;
        const city = venue?.city;
        return (name ? name : '') + (name && city ? ', ' : '') + (city ? city : '');
    });

    readonly $isRelocated = computed(() =>
        this.$ticket()?.actions_history?.some(action => action.type === ActionsHistoryType.relocated)
    );

    readonly dateTimeFormats = DateTimeFormats;
    readonly eventType = EventType;
    readonly obfuscatePattern = ObfuscatePattern;

    goToOriginalItemRoute = (): void => {
        if (this.$ticket()?.secondary_market?.original_order) {
            const isTransaction = !!this.#route.snapshot.parent?.params?.['orderCode'];
            const order = this.$ticket()?.secondary_market?.original_order;
            const ticketId = this.$ticket().secondary_market?.season_ticket_seat_id ?? this.$ticket().id;
            if (isTransaction) {
                this.#router.navigate([`../../transactions/${order}/tickets/${ticketId}`]);
            } else {
                this.#router.navigate([`../../tickets/${order}-${ticketId}`]);
            }
        }
    };

    goToPurchasedItemRoute = (): void => {
        if (this.$ticket()?.secondary_market?.purchase_order) {
            const order = this.$ticket()?.secondary_market?.purchase_order;
            const ticketId = this.$ticket().id;
            if (this.#route.snapshot.parent?.params?.['orderCode']) {
                this.#router.navigate([`../../transactions/${order}/tickets/${ticketId}`]);
            } else {
                this.#router.navigate([`../../tickets/${order}-${ticketId}`]);
            }

        }
    };

    openRelocationHistory(): void {
        openDialog(
            this.#matDialog,
            TicketRelocationHistoricDialogComponent,
            {
                orderCode: this.$ticket()?.order.code,
                ticketId: this.$ticket().id
            },
            this.#viewContainerRef,
            DialogSize.LATERAL
        );
    }
}
