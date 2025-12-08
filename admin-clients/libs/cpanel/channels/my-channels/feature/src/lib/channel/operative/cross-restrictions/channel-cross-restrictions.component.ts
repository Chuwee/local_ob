import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelsService, ChannelsExtendedService,
    ChannelCrossSaleRestriction, GetChannelEventsRequest
} from '@admin-clients/cpanel/channels/data-access';
import {
    EphemeralMessageService, SearchInputComponent
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListOption, MatSelectionList } from '@angular/material/list';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, defer, Observable, throwError } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, first, map, shareReplay, startWith, switchMap, tap } from 'rxjs/operators';
import { ChannelOperativeService } from '../channel-operative.service';

const debounce = 100;

const filterAndDebounce = <T>(obs$: Observable<T>): Observable<T> =>
    obs$.pipe(
        debounceTime(debounce),
        distinctUntilChanged()
    );

const match = (text: string, search: string): boolean =>
    text.toLowerCase().includes(search.toLowerCase());

@Component({
    selector: 'app-channel-cross-restrictions',
    templateUrl: './channel-cross-restrictions.component.html',
    styleUrls: ['./channel-cross-restrictions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatCheckbox,
        MatProgressSpinner,
        TranslatePipe,
        FormContainerComponent,
        SearchInputComponent,
        MatButtonToggle,
        MatButtonToggleGroup,
        MatListOption,
        MatSelectionList
    ]
})
export class ChannelCrossRestrictionsComponent implements OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #channelsService = inject(ChannelsService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #channelOperativeService = inject(ChannelOperativeService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    readonly #filters: GetChannelEventsRequest = { offset: 0, limit: 999, published: true };

    #$channelId = toSignal(this.#channelsService.getChannel$().pipe(
        first(Boolean), map(channel => channel.id)
    ));

    readonly form = this.#fb.group<Record<string, FormControl<number[]>>>({});
    readonly formEvent = new FormControl(null as number);

    readonly currentForm$ = this.formEvent.valueChanges.pipe(map(id => id && this.form.controls[id]));

    readonly isInProgress$ = booleanOrMerge([
        this.#channelsExtSrv.isChannelEventsLoading$(),
        this.#channelOperativeService.crossSaleRestrictions.loading$()
    ]);

    readonly searchEvents = new BehaviorSubject<string>('');
    readonly searchSelection = new BehaviorSubject<string>('');

    readonly #allChannelEvents$ = this.#channelsService.getChannel$()
        .pipe(
            first(Boolean),
            tap(() => this.loadEvents()),
            tap(() => this.loadRestrictions()),
            switchMap(() => combineLatest([
                this.#channelsExtSrv.getChannelEventsData$(),
                this.#channelOperativeService.crossSaleRestrictions.get$()]
            )),
            filter(([events, restrictions]) => !!events && !!restrictions),
            tap(([events, restrictions]) => {
                this.form.reset();
                events.forEach(({ event }) => {
                    const eventRestrictions = restrictions.find(({ required_event_id: id }) => id === event.id);
                    this.form.setControl(`${event.id}`, new FormControl(eventRestrictions?.cart_event_ids || []));
                });
                this.formEvent.setValue(this.currentEventId() || events[0].event.id);
                this.form.markAsPristine();

            }),
            map(([events]) => events.map(({ event }) => ({
                id: event.id, name: event.name, currency: event.currency
            }))),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    totalEvents$ = this.#allChannelEvents$.pipe(map(events => events.length));
    filteredChannelEvents$ = combineLatest([
        this.#allChannelEvents$,
        filterAndDebounce(this.searchEvents)
    ]).pipe(
        map(([events, query]) => query ? events.filter(event => match(event.name, query)) : events),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    // Using `defer` here to delay observable creation until subscription,
    // which helps avoid premature side effects from combining streams like formEvent.valueChanges.
    //
    // This is a quick workaround to prevent timing issues on the page.
    // Ideally, this should be refactored to better separate side effects and use signals or clearer reactive flows.
    filteredSuggestedEvents$ = defer(() => combineLatest([
        this.#allChannelEvents$,
        filterAndDebounce(this.searchSelection),
        this.formEvent.valueChanges.pipe(startWith(this.formEvent.value))
    ]).pipe(
        map(([events, query, currentEventId]) => {
            const currentEvent = events.find(event => event.id === currentEventId);
            if (!currentEvent) {
                return [];
            }
            return events
                .filter(event =>
                    event.id !== currentEventId &&
                    event.currency === currentEvent.currency &&
                    (!query || match(event.name, query))
                );
        }),
        shareReplay({ bufferSize: 1, refCount: true })
    ));

    filteredSuggestedEventsLength$ = combineLatest([
        this.filteredSuggestedEvents$,
        this.formEvent.valueChanges.pipe(startWith(this.formEvent.value))
    ]).pipe(map(([events]) => events.filter(event => event.id !== this.currentEventId())?.length));

    filteredformSelectionLength$ = combineLatest([
        this.filteredSuggestedEvents$.pipe(startWith(null)),
        this.form.valueChanges.pipe(startWith(this.form.value)),
        this.formEvent.valueChanges.pipe(startWith(this.formEvent.value))
    ]).pipe(
        map(([filteredEvents]) => {
            const selection = this.currentForm().value;
            return selection?.filter(id => filteredEvents?.find(event => event.id === id)).length || 0;
        })
    );

    readonly currentEventId = (): number => this.formEvent.value;
    readonly currentForm = (): FormControl<number[]> => this.form.controls[`${this.currentEventId()}`];

    ngOnDestroy(): void {
        this.#channelsExtSrv.clearChannelEvents();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const req: ChannelCrossSaleRestriction[] = Object.keys(this.form.value).map(eventId =>
                this.form.value?.[eventId] && ({
                    required_event_id: +eventId,
                    cart_event_ids: this.form.value[eventId]
                })).filter(elem => !!elem);
            return this.#channelOperativeService.crossSaleRestrictions.update(this.#$channelId(), req)
                .pipe(tap(() => {
                    this.#ephemeralSrv.showSaveSuccess();
                    this.form.markAsPristine();
                }));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'Invalid Form');
        }
    }

    cancel(): void {
        this.loadRestrictions();
    }

    selectAll(change?: MatCheckboxChange): void {
        const form = this.currentForm();
        if (change?.checked) {
            this.filteredSuggestedEvents$.pipe(first())
                .subscribe(events => {
                    const eventsIds = events.filter(({ id }) => id !== this.currentEventId()).map(({ id }) => id);
                    form.setValue([...new Set([...eventsIds, ...this.currentForm().value])]);
                    form.markAsDirty();
                });
        } else {
            this.filteredSuggestedEvents$.pipe(first())
                .subscribe(events => {
                    const eventsIds = events.filter(({ id }) => id !== this.currentEventId()).map(({ id }) => id);

                    if (eventsIds.length === (events.length - 1)) {
                        form.setValue([]);
                    } else {
                        form.setValue(this.currentForm().value.filter(id => !eventsIds.includes(id)));
                    }

                    form.markAsDirty();
                });
        }
    }

    private loadRestrictions(): void {
        this.#channelOperativeService.crossSaleRestrictions.load(this.#$channelId());
        this.form.markAsPristine();
    }

    private loadEvents(): void {
        this.#channelsExtSrv.loadChannelEvents(this.#$channelId(), this.#filters);
        this.form.markAsPristine();
    }
}
