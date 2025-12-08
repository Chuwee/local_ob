import { PromotersExternalProviderService } from '@admin-clients/cpanel/promoters/data-access';
import { AvetMatch, Event, EventFieldsRestriction, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { CustomManagementType, EntitiesBaseService, EventType, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { VenueAccessControlSystems, DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ActivitySaleType } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import {
    venueTemplatesProviders, VenueTemplatesService, VenueTemplateStatus
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import moment from 'moment-timezone';
import { BehaviorSubject, combineLatest, Observable, of } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, take, tap, withLatestFrom } from 'rxjs/operators';
import { SessionCreationType } from '../models/session-creation-type.enum';

@Component({
    selector: 'app-create-session-base-data',
    templateUrl: './create-session-base-data.component.html',
    styleUrls: ['./create-session-base-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        venueTemplatesProviders
    ],
    standalone: false
})
export class CreateSessionBaseDataComponent implements OnInit, OnDestroy {
    readonly #externalSrv = inject(PromotersExternalProviderService);
    readonly #eventsService = inject(EventsService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #sessionsService = inject(EventSessionsService);
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #fb = inject(FormBuilder);
    readonly #ref = inject(ChangeDetectorRef);

    readonly $eventId = input<number>(null, { alias: 'eventId' });
    readonly $lastSessionId = input<number>(null, { alias: 'lastSessionId' });
    readonly $form = input<UntypedFormGroup>(null, { alias: 'form' });
    readonly $creationType = input<SessionCreationType>(null, { alias: 'creationType' });
    readonly $refreshChanges = input<Observable<void>>(null, { alias: 'refreshChanges$' });

    readonly isReqInProgress$ = booleanOrMerge([
        this.#eventsService.event.inProgress$(),
        this.#venueTemplatesService.isVenueTemplatesListLoading$(),
        this.#eventsService.eventAdditionalConfig.loading$()
    ]);

    readonly event$ = this.#eventsService.event.get$()
        .pipe(
            first(Boolean),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly isActivitySource$ = this.event$.pipe(
        switchMap<Event, Observable<[boolean, Event]>>(event =>
            of([Boolean(event.type === EventType.activity || event.type === EventType.themePark), event])),
        take(1)
    );

    readonly $isSga = toSignal(this.event$.pipe(
        filter(event => event.additional_config.inventory_provider === ExternalInventoryProviders.sga),
        tap(event => this.#externalSrv.providerSessions.load({
            entity_id: event.entity.id,
            event_id: event.id,
            status: 'ACTIVE',
            skip_used: true
        }))
    ));

    readonly externalProviderSessions$ = this.#externalSrv.providerSessions.get$()
        .pipe(filter(Boolean), tap(() => {
            this.$form().get('externalProviderSession').enable();
            this.$form().get('sessionRates').disable();
        }));

    readonly eventRates$ = this.#eventsService.eventRates.get$().pipe(
        filter(value => value !== null),
        map(rates => rates.map(rate => ({
            id: rate.id,
            name: rate.name,
            default: rate.default,
            visible: rate.default
        }))),
        switchMap(rates => {
            if (this.$lastSessionId()) {
                return this.#sessionsService.session.get$().pipe(
                    filter(value => value !== null),
                    map(session => rates.map(rate => {
                        const sessionRate = session.settings.rates.find(r => r.id === rate.id);
                        return ({
                            ...rate,
                            default: sessionRate?.default,
                            visible: !!sessionRate
                        });
                    }))
                );
            }
            return of(rates);
        })
    );

    readonly venueTemplates$ = this.#venueTemplatesService.getVenueTemplatesListData$()
        .pipe(
            first(Boolean),
            tap(venueTpls => {
                if (venueTpls?.length === 1) {
                    this.$form().get('venueTemplate').setValue(venueTpls[0]);
                }
            }),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly taxes$ = this.#entitiesService.getEntityTaxes$()
        .pipe(
            filter(taxes => taxes !== null),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly showSmartBooking$ = this.#entitiesService.getEntity$().pipe(
        filter(Boolean),
        map(entity => {
            const hasSmartBooking = !!entity.settings?.external_integration?.custom_managements?.find(customManagement =>
                customManagement.type === CustomManagementType.smartbookingintegration)?.enabled;
            if (hasSmartBooking) {
                this.$form().get('enableSmartBooking').enable();
            } else {
                this.$form().get('enableSmartBooking').disable();
            }
            return hasSmartBooking;
        }));

    readonly showOrphanSeats$ = this.isActivitySource$.pipe(
        map(([isActivity]) => !isActivity),
        tap(showOrphanSeats => {
            if (showOrphanSeats) {
                this.$form().get('enableOrphanSeats').enable();
            } else {
                this.$form().get('enableOrphanSeats').disable();
            }
        })
    );

    readonly canAllowGroups$ = this.isActivitySource$.pipe(
        map(([isActivity, event]) => isActivity && event.settings.groups.allowed),
        tap(canAllowGroups => !canAllowGroups && this.$form().get('activitySaleType').disable())
    );

    readonly $hasFortressVenue = toSignal(
        this.event$.pipe(
            map(event => event?.venue_templates?.some(tpl =>
                tpl.venue.access_control_systems?.some(system => system?.name === VenueAccessControlSystems.fortressBRISTOL)) || false)
        ),
        { initialValue: false }
    );

    readonly matches = new BehaviorSubject<AvetMatch[]>([]);
    readonly activitySaleType = ActivitySaleType;
    readonly dateTimeFormats = DateTimeFormats;
    readonly sessionCreationType = SessionCreationType;
    readonly maxSessionNameLength = EventFieldsRestriction.eventSessionNameLength;

    defaultMatches: AvetMatch[];

    ngOnInit(): void {
        const req = {
            limit: 999,
            offset: 0,
            sort: 'name:asc',
            eventId: this.$eventId(),
            status: [VenueTemplateStatus.active],
            inventory_provider: this.$isSga() ? ExternalInventoryProviders.sga : null
        };
        this.#venueTemplatesService.loadVenueTemplatesList(req);
        // FormGroup creation

        this.$form().addControl('name', this.#fb.control(null,
            [Validators.required, Validators.maxLength(EventFieldsRestriction.eventNameLength)]
        ));
        this.$form().addControl('enableSmartBooking', this.#fb.control(false));
        this.$form().addControl('enableOrphanSeats', this.#fb.control(false));
        this.$form().addControl('sessionRates', this.#fb.control(null, Validators.required));
        this.$form().addControl('venueTemplate', this.#fb.control(null, Validators.required));
        this.$form().addControl('ticketTax', this.#fb.control(null, Validators.required));
        this.$form().addControl('surchargeTax', this.#fb.control(null, Validators.required));
        this.$form().addControl('activitySaleType', this.#fb.control(null, Validators.required));
        this.$form().addControl('reference', this.#fb.control(null));

        if (this.$creationType() === SessionCreationType.single) {
            this.$form().addControl('startDate', this.#fb.control(null, Validators.required));
            this.$form().addControl('endDate', this.#fb.control(null));
            this.$form().addControl('avetMatch', this.#fb.control(null));
        } else if (this.$creationType() === SessionCreationType.multiple) {
            const durationValidators = this.$hasFortressVenue()
                ? [Validators.min(0), this.#durationRequiredValidator]
                : [Validators.min(0)];

            this.$form().addControl('durationHours', this.#fb.control(null, durationValidators));
            this.$form().addControl('durationMins', this.#fb.control(null, [Validators.min(0), Validators.max(59)]));

            if (this.$hasFortressVenue()) {
                this.$form().get('durationMins').valueChanges
                    .pipe(takeUntilDestroyed(this.#destroyRef))
                    .subscribe(() =>
                        this.$form().get('durationHours').updateValueAndValidity({ emitEvent: false }));
            }
        }
        if (this.$isSga()) {
            this.$form().addControl('externalProviderSession', this.#fb.control({ value: null, disabled: true }, Validators.required));
        }

        this.#initDates();

        this.event$
            .pipe(
                withLatestFrom(
                    this.#eventsService.eventRates.get$()
                ),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([event, rates]) => {
                if (event.type !== EventType.avet && rates === null && !this.$isSga()) {
                    this.#eventsService.eventRates.load(event.id.toString());
                }
                this.#entitiesService.loadEntityTaxes(event.entity.id);
            });

        // avet initialization
        if (this.$creationType() === SessionCreationType.single) {
            combineLatest([
                this.event$.pipe(
                    filter((event: Event) => event.type === EventType.avet),
                    tap(avetEvent => {
                        this.#eventsService.eventAdditionalConfig.load(avetEvent.id.toString());
                    })),
                this.venueTemplates$,
                this.taxes$
            ]).pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(([_, __, taxes]) => {
                const defaultTax = taxes?.find(tax => tax?.default);
                if (defaultTax) {
                    this.$form().get('ticketTax').setValue(defaultTax);
                    this.$form().get('surchargeTax').setValue(defaultTax);
                    this.$form().get('sessionRates').clearValidators();
                    this.$form().get('sessionRates').updateValueAndValidity();
                }
            });

            this.$form().get('avetMatch').valueChanges.subscribe(match => {
                this.$form().get('name').setValue(match.name);
                this.$form().get('startDate').setValue(match.match_date);
            });

            this.$form().get('externalProviderSession')?.valueChanges.subscribe(session => {
                this.$form().get('name').setValue(session.name);
                this.$form().get('startDate').setValue(session.date);
            });

            this.#eventsService.eventAdditionalConfig.get$().pipe(
                filter(Boolean),
                map(config => config.avet_match_list)
            ).subscribe(matches => {
                this.matches.next(matches);
                this.defaultMatches = matches;
            });

        }

        this.$form().get('enableSmartBooking')?.valueChanges.subscribe(() => {
            if (this.$form().get('enableSmartBooking')?.value) {
                this.matches.next(this.matches.value.filter(match => match.smart_booking_related ? match.smart_booking_related : false));
            } else {
                this.matches.next(this.defaultMatches);
            }
        });

        this.$refreshChanges()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                this.#ref.detectChanges();
            });
    }

    ngOnDestroy(): void {
        this.#venueTemplatesService.clearVenueTemplateList();
    }

    #initDates(): void {
        if (this.$lastSessionId()) {
            this.#sessionsService.session.get$()
                .pipe(first(s => s !== null))
                .subscribe(session => {
                    this.$form().get('name').setValue(session.name);
                    if (this.$creationType() === SessionCreationType.single) {
                        this.$form().get('startDate').setValue(
                            moment(session.start_date).add(1, 'd').format(), {
                            emitEvent: false
                        }
                        );
                    }
                    this.$form().get('activitySaleType').setValue(session.settings.activity_sale_type);
                });
        } else {
            if (this.$creationType() === SessionCreationType.single) {
                const defaultDate = moment().set('h', 10).set('m', 0).set('s', 0).set('ms', 0);
                this.$form().get('startDate').setValue(defaultDate.format());
            }
        }
    }

    #durationRequiredValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
        if (!control.parent) return null;

        const hours = control.value || 0;
        const mins = control.parent.get('durationMins')?.value || 0;

        return (hours === 0 && mins === 0) ? { atLeastOneRequired: true } : null;
    };

}
