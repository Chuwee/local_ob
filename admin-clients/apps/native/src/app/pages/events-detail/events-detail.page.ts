import { EventsService, eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { NavController, SegmentCustomEvent } from '@ionic/angular';
import { filter } from 'rxjs';
import { DeviceStorage } from '../../core/services/deviceStorage';

@Component({
    selector: 'events-detail-page',
    templateUrl: 'events-detail.page.html',
    styleUrls: ['events-detail.page.scss'],
    providers: [eventsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventsDetailPage {
    readonly #navCtrl = inject(NavController);
    readonly #eventService = inject(EventsService);
    readonly #route = inject(ActivatedRoute);
    readonly #deviceStorage = inject(DeviceStorage);

    #id: number;
    #segment: string;

    readonly $event = toSignal(this.#eventService.event.get$().pipe(filter(Boolean)));
    readonly $isLoading = toSignal(this.#eventService.event.inProgress$());

    $lastViewed = effect(() => {
        if (this.$event()) {
            this.#deviceStorage.getItem('last_events').subscribe((results: number[]) => {
                if (!results) {
                    this.#deviceStorage.setItem('last_events', [this.$event().id]);
                } else {
                    const lastEventsViewed = results;
                    if (lastEventsViewed.length === 10) {
                        lastEventsViewed.shift();
                    }
                    lastEventsViewed.push(this.$event().id);
                    const uniqueArray = Array.from(new Set(lastEventsViewed));
                    this.#deviceStorage.setItem('last_events', uniqueArray);
                }
            });
        }
    });

    loadData$ = this.#route.params.subscribe(params => {
        this.#id = params['id'];
        this.loadEvent();
        this.#segment = params['segment'];
        if (this.#segment) {
            this.changeSegment();
        }
    });

    index: 'info' | 'sessions' | 'session-pack' | 'channels' | 'promotions' = 'info';
    isError = false;

    loadEvent(): void {
        if (!this.#id) {
            this.#navCtrl.back();
        } else {
            this.#eventService.event.load(this.#id.toString());
        }
    }

    changeSegment(event?: SegmentCustomEvent): void {
        this.index = (event?.detail.value || this.#segment) as 'info' | 'sessions' | 'session-pack' | 'channels' | 'promotions';
    }

    reTry(): void {
        this.isError = false;
        this.loadEvent();
    }
}
