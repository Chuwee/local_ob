import {
    Event, EventsService, GetEventPricesRequest, RateGroup
} from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, Session, SessionsFilterFields } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EventType, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';
import { filter, map, shareReplay, tap } from 'rxjs/operators';

@Component({
    selector: 'app-event-price-types-rates-group-filters',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './event-price-types-rates-group-filters.component.html',
    imports: [
        ReactiveFormsModule, TranslatePipe, MatSlideToggle, MatFormFieldModule, MatSelectModule, EllipsifyDirective,
        SelectSearchComponent, AsyncPipe, MatTooltip
    ]
})
export class EventPriceTypesRatesGroupFiltersComponent implements OnInit, OnDestroy {
    readonly #eventSessionsService = inject(EventSessionsService);
    readonly #eventService = inject(EventsService);
    readonly #destroyRef = inject(DestroyRef);

    readonly compareWith = compareWithIdOrCode;
    readonly filtersForm = inject(FormBuilder).group({
        venue: new FormControl<Session>(null, Validators.required),
        session: new FormControl<Session>(null, Validators.required),
        product: new FormControl<RateGroup>({ value: null, disabled: true }, Validators.required),
        rateGroup: new FormControl<RateGroup>({ value: null, disabled: true }),
        showPastSessions: [true]
    });

    readonly sessions$ = this.#eventSessionsService.sessionList.get$().pipe(
        filter(Boolean),
        map(sl => sl.data.filter(session => session.venue_template.id === this.filtersForm.controls.venue.value.id))
    );

    readonly venueTpls$ = this.#eventService.event.get$().pipe(
        filter(Boolean),
        tap(event => {
            if (event.venue_templates.length === 1) {
                this.filtersForm.controls.venue.setValue(event.venue_templates[0]);
            }
        }),
        map(event => event.venue_templates),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly ratesGroup$ = this.#eventService.ratesGroup.get$().pipe(filter(Boolean));
    readonly products$ = this.#eventService.sgaProducts.get$().pipe(filter(Boolean));
    readonly selectedRate = this.filtersForm.controls.rateGroup.valueChanges;
    readonly $isSga = signal(false);
    readonly $event = signal<Event>(null);
    readonly eventType = EventType;
    readonly $isSgaEvent = computed(() => this.$event()?.type !== EventType.activity && this.$isSga());
    readonly $isSgaMembership = computed(() => this.$event()?.type === EventType.activity && this.$isSga());

    async ngOnInit(): Promise<void> {
        this.#eventSessionsService.sessionList.clear();
        this.#eventService.eventPrices.clear();
        const event = await firstValueFrom(this.#eventService.event.get$());
        this.$event.set(event);
        this.$isSga.set(event.additional_config.inventory_provider === ExternalInventoryProviders.sga);

        if (this.$isSga()) {
            // in SGA there's no venue filter so we need the subscribe to set the venue template in the form
            this.venueTpls$.subscribe();
            // in SGA sessions are the first filter so we need to load them
            this.loadSessions(this.filtersForm.controls.showPastSessions.value);

            // SGA event filter changes
            this.filtersForm.controls.session.valueChanges
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(() => {
                    this.filtersForm.controls.product.enable({ emitEvent: false });
                    this.filtersForm.controls.product.reset(null, { emitEvent: false });
                    this.#eventService.eventPrices.clear();
                });
            if (this.$isSgaMembership()) {
                // SGA membership doesn't have products so we need to enable rates group regardless of the product
                this.filtersForm.controls.rateGroup.reset(null, { emitEvent: false });
                this.filtersForm.controls.rateGroup.enable({ emitEvent: false });
                this.#eventService.ratesGroup.load(event.id, 'RATE');
            }
            this.filtersForm.controls.product.valueChanges
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(product => {
                    this.filtersForm.controls.rateGroup.reset(null, { emitEvent: false });
                    this.filtersForm.controls.rateGroup.enable({ emitEvent: false });
                    this.#eventService.eventPrices.clear();
                    if (product) {
                        this.#eventService.ratesGroup.load(event.id, 'RATE');
                    }
                });
        } else {
            // AVET event filter changes
            this.filtersForm.controls.venue.valueChanges
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(() => {
                    this.filtersForm.controls.session.enable({ emitEvent: false });
                    this.filtersForm.controls.session.reset(null, { emitEvent: false });
                    this.loadSessions(this.filtersForm.controls.showPastSessions.value);
                    this.#eventService.eventPrices.clear();
                });

            this.filtersForm.controls.session.valueChanges
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(() => {
                    this.filtersForm.controls.rateGroup.enable({ emitEvent: false });
                    this.filtersForm.controls.rateGroup.reset(null, { emitEvent: false });
                    this.loadEventPrices();
                });
        }

        this.filtersForm.controls.rateGroup.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => this.loadEventPrices());

        this.filtersForm.controls.showPastSessions.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => this.loadSessions(value));

        this.sessions$.pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(sessions => {
                if (!this.filtersForm.controls.session.value) {
                    if (sessions.length === 1) {
                        this.filtersForm.controls.session.setValue(sessions[0]);
                    }
                    return;
                }
                if (sessions.find(session => session.id === this.filtersForm.controls.session.value.id)) {
                    return;
                }
                this.filtersForm.controls.session.reset(null, { emitEvent: false });
                if (this.$isSgaEvent()) {
                    this.filtersForm.controls.rateGroup.disable({ emitEvent: false });
                }
                this.#eventService.eventPrices.clear();
            });

        this.#eventService.ratesGroup.get$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(ratesGroup => {
                if ((ratesGroup === null || ratesGroup.length === 0)) {
                    if (!this.$isSga()) {
                        this.filtersForm.controls.session.disable({ emitEvent: false });
                    }
                    this.filtersForm.controls.rateGroup.disable({ emitEvent: false });
                    this.filtersForm.controls.showPastSessions.disable({ emitEvent: false });
                    return;
                }

                if (!this.filtersForm.controls.venue.value) {
                    this.filtersForm.controls.venue.enable({ emitEvent: false });
                    if (this.$isSga()) {
                        this.filtersForm.controls.product.enable({ emitEvent: false });
                    } else {
                        this.filtersForm.controls.session.disable({ emitEvent: false });
                    }
                    return;
                }

                if (!this.filtersForm.controls.session.value) {
                    this.filtersForm.controls.session.enable({ emitEvent: false });
                    this.filtersForm.controls.rateGroup.disable({ emitEvent: false });
                    this.filtersForm.controls.product.disable({ emitEvent: false });
                    this.filtersForm.controls.showPastSessions.enable({ emitEvent: false });
                    return;
                }

                this.filtersForm.controls.venue.enable({ emitEvent: false });
                this.filtersForm.controls.session.enable({ emitEvent: false });
                this.filtersForm.controls.rateGroup.enable({ emitEvent: false });
                this.filtersForm.controls.showPastSessions.enable({ emitEvent: false });

                if (!ratesGroup.find(rate => rate.id === this.filtersForm.controls.rateGroup.value?.id)) {
                    this.filtersForm.controls.rateGroup.reset(null, { emitEvent: false });
                }
                this.loadEventPrices();
            });
    }

    ngOnDestroy(): void {
        this.#eventSessionsService.sessionList.clear();
        this.#eventService.eventPrices.clear();
    }

    private loadEventPrices(): void {
        const { venue, session, product, rateGroup } = this.filtersForm.getRawValue();
        const venueId = venue?.id ?? null;
        const request: GetEventPricesRequest = {
            sessionId: session?.id ?? null,
            productId: product?.id ?? null,
            rateGroupId: rateGroup?.id ?? null
        };
        this.#eventService.eventPrices.load(
            this.$event().id.toString(),
            venueId.toString(),
            request
        );
    }

    private loadSessions(showPastSessions: boolean): void {
        this.#eventSessionsService.sessionList.load(this.$event().id,
            {
                initEndDate: showPastSessions ? null : new Date().toISOString(),
                fields: [SessionsFilterFields.name, SessionsFilterFields.venueTemplateId]
            });
    }
}
