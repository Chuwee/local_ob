import { EventsService, PutRateGroup } from '@admin-clients/cpanel/promoters/events/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, finalize, map } from 'rxjs/operators';
import { EventSgaProductsRatesListComponent } from '../sga-shared/event-sga-products-rates-list.component';

@Component({
    selector: 'app-event-sga-rates-group-list',
    template: `
        <app-event-sga-products-rates-list
            [event]="$event()"
            [sortedListData]="$ratesGroup()"
            [listDescription]="'EVENTS.SGA_RATES_LIST_DESCRIPTION'"
            (orderListChanged)="onListDrop($event)"
            (translateDialogSaved)="onTranslateDialogSaved($event)"
        />
    `,
    imports: [
        EventSgaProductsRatesListComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventSgaRatesGroupListComponent {
    readonly #eventsSrv = inject(EventsService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly $event = toSignal(this.#eventsSrv.event.get$());

    readonly $ratesGroup = toSignal(
        this.#eventsSrv.ratesGroup.get$().pipe(
            filter(Boolean),
            map(rates => rates.sort((a, b) => (a.position || 0) - (b.position || 0)) || [])
        )
    );

    onTranslateDialogSaved(isSaved: boolean): void {
        if (isSaved) {
            this.#eventsSrv.ratesGroup.load(this.$event().id, 'RATE');
        }
    }

    onListDrop(orderedRates: PutRateGroup[]): void {
        this.#eventsSrv.ratesGroup.updateMany(this.$event().id, orderedRates)
            .pipe(
                finalize(() => this.#eventsSrv.ratesGroup.load(this.$event().id, 'RATE'))
            )
            .subscribe(() =>
                this.#ephemeralMsgSrv.showSuccess({
                    msgKey: 'EVENTS.FEEDBACK.RATE_POSITION_SUCCESS'
                })
            );
    }
}
