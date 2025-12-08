import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    eventPromotionsProviders, EventPromotionsService, GetEventPromotionsRequest
} from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { InfiniteScrollCustomEvent } from '@ionic/angular';
import { filter, Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'events-promotions-tab',
    templateUrl: './promotions-tab.component.html',
    styleUrls: ['./promotions-tab.component.scss'],
    providers: [eventPromotionsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class PromotionsTabComponent implements OnInit, OnDestroy {
    readonly #eventPromotionsService = inject(EventPromotionsService);
    readonly #onDestroy = new Subject<void>();
    #offset = 0;
    #requestParams: GetEventPromotionsRequest = {
        offset: this.#offset,
        limit: 10
    };

    @Input() readonly event: Event;
    readonly $isLoading = toSignal(this.#eventPromotionsService.promotionsList.loading$());
    readonly $totalResultsCounter = signal(0);

    readonly promotionMetaData$ = this.#eventPromotionsService.promotionsList.getMetadata$().pipe(
        filter(Boolean), takeUntil(this.#onDestroy)).subscribe(
            metadata => this.$totalResultsCounter.set(metadata.total)
        );

    readonly promotionList$ = this.#eventPromotionsService.promotionsList.getData$().pipe(
        filter(Boolean), takeUntil(this.#onDestroy)).subscribe(response => {
            if (this.#offset === 0) {
                this.promotions = response;
            } else {
                this.promotions = [
                    ...this.promotions,
                    ...response
                ];
            }
            if (this.currentEvent) {
                this.currentEvent.target.complete();
            }
        });

    promotions = [];
    isFirstLoad = false;
    currentEvent: InfiniteScrollCustomEvent;

    ngOnInit(): void {
        this.isFirstLoad = true;
        this.#eventPromotionsService.promotionsList.load(this.event.id, this.#requestParams);
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    onIonInfinite(event: InfiniteScrollCustomEvent): void {
        this.currentEvent = event;
        if (this.#offset <= this.$totalResultsCounter()) {
            this.increaseParams();
            this.#eventPromotionsService.promotionsList.load(this.event.id, this.#requestParams);
        } else {
            event.target.complete();
        }
    }

    private increaseParams(): void {
        this.#offset += 10;
        this.#requestParams = {
            offset: this.#offset,
            limit: 10
        };
    }
}
