import { EventsService, PutRateGroup } from '@admin-clients/cpanel/promoters/events/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { filter, finalize, map, take, tap } from 'rxjs';
import { EventSgaProductsRatesListComponent } from '../rates/sga-shared/event-sga-products-rates-list.component';

@Component({
    selector: 'app-event-sga-products-list',
    template: `
    <app-form-container [hideActionBar]="true" layout="menu">
        <div class="flex">
            <app-event-sga-products-rates-list
                [event]="$event()"
                [sortedListData]="$vmEventProducts()"
                [listDescription]="'EVENTS.SGA_PRODUCTS_LIST_DESCRIPTION'"
                (orderListChanged)="onListDrop($event)"
                (translateDialogSaved)="onTranslateDialogSaved($event)"
                class="flex-[0_1_680px]"
            />
        </div>
    </app-form-container>
    @if($isInProgress()) {
        <div class="spinner-container">
            <mat-spinner />
        </div>
    }
    `,
    imports: [
        EventSgaProductsRatesListComponent, FormContainerComponent, MatProgressSpinner
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventSgaProductsListComponent {
    readonly #eventsSrv = inject(EventsService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);

    readonly $event = toSignal(this.#eventsSrv.event.get$().pipe(
        filter(Boolean),
        take(1),
        tap(event => {
            this.#eventsSrv.sgaProducts.load(event.id);
        })
    ));

    readonly $vmEventProducts = toSignal(
        this.#eventsSrv.sgaProducts.get$().pipe(
            filter(Boolean),
            map(products => products.sort((a, b) => (a.position || 0) - (b.position || 0)) || [])
        )
    );

    readonly $isInProgress = toSignal(this.#eventsSrv.sgaProducts.loading$());

    onTranslateDialogSaved(isSaved: boolean): void {
        if (isSaved) {
            this.#eventsSrv.sgaProducts.load(this.$event().id);
        }
    }

    onListDrop(orderedProducts: PutRateGroup[]): void {
        this.#eventsSrv.sgaProducts.update(this.$event().id, orderedProducts)
            .pipe(finalize(() => this.#eventsSrv.sgaProducts.load(this.$event().id)))
            .subscribe(() =>
                this.#ephemeralMsgSrv.showSuccess({
                    msgKey: 'EVENTS.FEEDBACK.RATE_POSITION_SUCCESS'
                })
            );
    }
}
