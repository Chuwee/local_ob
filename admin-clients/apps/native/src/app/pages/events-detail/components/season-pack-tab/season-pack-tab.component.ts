import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, GetSessionsRequest, Session, SessionType, eventSessionsProviders
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { ChangeDetectionStrategy, Component, Input, OnInit, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { InfiniteScrollCustomEvent, RefresherCustomEvent } from '@ionic/angular';
import { filter } from 'rxjs';

@Component({
    selector: 'season-pack-tab',
    templateUrl: './season-pack-tab.component.html',
    styleUrls: ['./season-pack-tab.component.scss'],
    providers: [eventSessionsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonPackTabComponent implements OnInit {
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #router = inject(Router);
    readonly #filterSessionPacks: GetSessionsRequest = {
        q: '',
        type: [SessionType.restrictedPack, SessionType.unrestrictedPack],
        offset: 0,
        limit: 10
    };

    @Input() readonly event: Event;
    readonly $isLoading = toSignal(this.#eventSessionsSrv.sessionList.inProgress$());
    readonly $totalSessionPacksEvents = signal(0);

    readonly sessionList$ = this.#eventSessionsSrv.sessionList.get$()
        .pipe(
            filter(Boolean)
        ).subscribe({
            next: response => {
                if (this.#filterSessionPacks.offset === 0) {
                    this.foundSessionPacks = response.data;
                } else {
                    this.foundSessionPacks = [
                        ...this.foundSessionPacks,
                        ...response.data
                    ];
                }
                if (this.currentEvent) {
                    this.currentEvent.target.complete();
                }
                this.$totalSessionPacksEvents.set(response.metadata.total);
            },
            error: () => {
                this.isError = true;
            }
        });

    isError = false;
    foundSessionPacks: Session[] = [];
    ionInfinite = false;
    currentEvent: InfiniteScrollCustomEvent;

    ngOnInit(): void {
        this.loadSessions();
    }

    loadSessions = (): void => {
        this.#eventSessionsSrv.sessionList.load(this.event.id, this.#filterSessionPacks);
    };

    handleRefresh = (event: RefresherCustomEvent): void => {
        this.#filterSessionPacks.offset = 0;
        setTimeout(() => {
            this.loadSessions();
            event.target.complete();
        }, 1000);
    };

    onIonInfinite(event: InfiniteScrollCustomEvent): void {
        this.ionInfinite = true;
        this.currentEvent = event;
        if (this.#filterSessionPacks.offset <= this.$totalSessionPacksEvents()) {
            this.#filterSessionPacks.offset += this.#filterSessionPacks.limit;
            this.loadSessions();
        } else {
            event.target.complete().then();
        }
    }

    goToSessionDetail = (session: Session): void => {
        this.#router.navigate(['session-detail'], {
            queryParams: {
                eventId: this.event.id,
                id: session.id
            }
        });
    };
}
