import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { forceDatesTimezone, forceToDefaultTimezone } from '@admin-clients/cpanel/common/utils';
import { Event, EventsService, EventStatus, SessionFieldsRestrictions } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import {
    EventSessionsService, GeneralForm, getReleaseStatusIndicator, getSaleStatusIndicator, PutSession, Session, SessionAdditionalConfig,
    SessionDatesFormValidation, SessionGenerationStatus, SessionRate, OperativeDatesForm,
    SessionSmartBookingStatus, SessionStatus, SessionStatusIndicators, SessionType
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { SessionCommunicationService } from '@admin-clients/cpanel-promoters-events-sessions-communication-data-access';
import { EntitiesBaseService, Entity, EventType, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { ContextNotificationComponent, DateTimePickerComponent, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { VenueAccessControlSystems, DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ActivitySaleType } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { AsyncPipe, KeyValuePipe, NgClass, NgTemplateOutlet, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
    AbstractControl, FormBuilder, FormControl, ReactiveFormsModule, UntypedFormGroup, ValidationErrors, Validators
} from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { MatDivider } from '@angular/material/divider';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel, MatError } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatSelect } from '@angular/material/select';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { combineLatest, firstValueFrom, Observable, Subject, throwError } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, take, takeUntil, withLatestFrom } from 'rxjs/operators';
import { LinkedSessionsComponent } from './linked-sessions/linked-sessions.component';
import { SessionRate as Rate, SessionRatesComponent } from './rates/rates.component';

@Component({
    selector: 'app-planning',
    templateUrl: './session-planning.component.html',
    styleUrls: ['./session-planning.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [SessionDatesFormValidation],
    imports: [
        FormContainerComponent, ContextNotificationComponent, TranslatePipe, AsyncPipe, UpperCasePipe,
        NgClass, ReactiveFormsModule, RouterModule, NgTemplateOutlet,
        KeyValuePipe, FormControlErrorsComponent, LocalDateTimePipe, DateTimePickerComponent,
        SessionRatesComponent, LinkedSessionsComponent, ArchivedEventMgrComponent, MatProgressSpinner,
        MatDivider, MatLabel, MatFormField, MatExpansionPanelTitle, MatExpansionPanelHeader, MatIcon,
        MatRadioButton, MatRadioGroup, MatExpansionPanel, MatOption, MatSelect, MatTooltip, MatInput,
        MatError, MatSlideToggle
    ]
})
export class SessionPlanningComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #onDestroy = new Subject<void>();
    readonly #fb = inject(FormBuilder);
    readonly #sessionsSrv = inject(EventSessionsService);
    readonly #eventsSrv = inject(EventsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #changeDetector = inject(ChangeDetectorRef);
    readonly #sessionCommunicationService = inject(SessionCommunicationService);
    readonly #sessionDatesFormValidation = inject(SessionDatesFormValidation);
    readonly #formChanged = new Subject<void>();

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly generalForm = this.#fb.group({
        name: [
            null as string,
            [Validators.required, Validators.maxLength(SessionFieldsRestrictions.sessionNameLength)]
        ],
        status: [null as SessionStatus, Validators.required],
        start_date: [null as string, [Validators.required]],
        end_date: null as string,
        reference: [
            null as string,
            [Validators.maxLength(SessionFieldsRestrictions.sessionReferenceLength)]
        ],
        rates: [null as SessionRate[], SessionPlanningComponent.requiredMap],
        taxes: this.#fb.group({
            ticket: this.#fb.group({
                id: [null as number, Validators.required]
            }),
            charges: this.#fb.group({
                id: [null as number, Validators.required]
            })
        }),
        activity_sale_type: null as ActivitySaleType,
        smart_booking_status: null
    });

    readonly operativeDatesForm = this.#fb.group({
        release: this.#fb.group({
            enable: false,
            date: null as string
        }),
        booking: this.#fb.group({
            enable: false,
            start_date: null as string,
            end_date: null as string
        }),
        sale: this.#fb.group({
            enable: false,
            start_date: null as string,
            end_date: null as string
        }),
        secondary_market_sale: this.#fb.group({
            enable: false,
            start_date: null as string,
            end_date: null as string
        })
    });

    readonly form = this.#fb.group({
        generalForm: this.generalForm,
        operativeDatesForm: this.operativeDatesForm
    });

    readonly activitySaleTypes = ActivitySaleType;
    isLoading$: Observable<boolean>;
    sessionStatuses: SessionStatus[] = [
        SessionStatus.scheduled,
        SessionStatus.preview,
        SessionStatus.ready,
        SessionStatus.cancelled
    ];

    sessionSmartBookingStatuses: SessionSmartBookingStatus[] = [
        SessionSmartBookingStatus.disabled,
        SessionSmartBookingStatus.ready
        // TODO implementar backend
        //SessionSmartBookingStatus.scheduled
    ];

    showSmartBookingRelated$ = this.#sessionsSrv.session.get$()
        .pipe(
            filter(session => !!session?.settings?.smart_booking),
            map(({ settings }) => !!settings?.smart_booking?.related_id && settings?.smart_booking?.type !== 'SMART_BOOKING')
        );

    readonly $sessionInContingencyMode = toSignal(this.#sessionsSrv.session.get$()
        .pipe(
            filter(session => !!session?.external_data),
            map(session => Boolean(session.external_data['smart_booking_contingency']))
        ));

    readonly rates$: Observable<Rate[]> = this.#eventsSrv.event.get$()
        .pipe(
            first(),
            switchMap(event => {
                if (event.type === EventType.avet) {
                    return combineLatest([
                        this.#sessionsSrv.rates.get$(),
                        this.#sessionsSrv.session.get$()
                    ]).pipe(
                        filter(values => values.every(value => !!value)),
                        map(([rates, session]) => rates.map(rate => {
                            const sessionRate = session.settings?.rates?.find(r => r.id === rate.id);
                            return ({
                                id: rate.id,
                                name: rate.rate_group.name,
                                default: sessionRate?.default,
                                visible: !!sessionRate
                            });
                        }))
                    );
                } else {
                    return combineLatest([
                        this.#eventsSrv.eventRates.get$(),
                        this.#sessionsSrv.session.get$()
                    ]).pipe(
                        filter(values => values.every(value => !!value)),
                        map(([rates, session]) => rates.map(rate => {
                            const sessionRate = session.settings?.rates?.find(r => r.id === rate.id);
                            return ({
                                id: rate.id,
                                name: rate.name,
                                default: sessionRate?.default,
                                visible: !!sessionRate
                            });
                        }))
                    );
                }
            })
        );

    readonly smartBookingSessionRoute$ = this.#sessionsSrv.session.get$()
        .pipe(
            filter(Boolean),
            map(session => {
                const relatedSessionId = session.settings?.smart_booking?.related_id;
                return relatedSessionId ? `/events/${session.event?.id}/sessions/${relatedSessionId}/planning` : null;
            })
        );

    readonly taxes$ = this.#entitiesSrv.getEntityTaxes$();

    showSecondaryMarketOperative$: Observable<boolean> = this.#entitiesSrv.getEntity$().pipe(
        filter(Boolean),
        map(entity => entity.settings?.allow_secondary_market)
    );

    sessionImageUrl$: Observable<string>;

    showBookingSettings$: Observable<boolean>;
    isSessionGenerationError$: Observable<boolean>;
    avetConfig$: Observable<SessionAdditionalConfig>;
    dateTimeFormats = DateTimeFormats;
    readonly isAvet$ = this.#eventsSrv.event.get$()
        .pipe(
            take(1),
            map(event => event.type === EventType.avet),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly isSGA$ = this.#eventsSrv.event.get$()
        .pipe(
            take(1),
            map(event => event.additional_config?.inventory_provider === ExternalInventoryProviders.sga),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    eventStatus: EventStatus;
    sessionReleaseStatusInd: SessionStatusIndicators;
    sessionSaleStatusInd: SessionStatusIndicators;
    sessionType: SessionType;
    sessionTypes = SessionType;
    currentSession$: Observable<Session>;
    groupsAllowed$: Observable<boolean>;
    hasFortressVenue: boolean;

    ngOnInit(): void {
        this.#eventsSrv.event.get$()
            .pipe(first())
            .subscribe(event => {
                this.eventStatus = event.status;
                if (event.type !== EventType.avet) {
                    this.#eventsSrv.eventRates.load(event.id.toString());
                }
            });
        this.#sessionsSrv.session.get$()
            .pipe(
                filter(session => !!session),
                withLatestFrom(this.#eventsSrv.event.get$().pipe(first())),
                takeUntil(this.#onDestroy)
            )
            .subscribe(([session, event]) => {
                const relatedSessionId = session.settings?.smart_booking?.related_id;
                this.updateNestedControl(this.generalForm, 'smart_booking_status', !!relatedSessionId);
                this.#sessionsSrv.clearSessionAdditionalConfig();
                this.#entitiesSrv.loadEntityTaxes(session.entity.id);
                if (event.type === EventType.avet) {
                    this.#sessionsSrv.loadSessionAdditionalConfig(session.event.id, session.id);
                    this.#sessionsSrv.rates.load(event.id, session.id);
                }
                this.#sessionCommunicationService.loadChannelImages(session.event.id, session.id);
            });
        this.model();
    }

    getSmartBookingSessionRoute(eventId: number, relatedSessionId: number): string {
        return `/events/${eventId}/sessions/${relatedSessionId}/planning`;
    }

    ngOnDestroy(): void {
        this.#sessionsSrv.rates.clear();
        this.#eventsSrv.eventRates.clear();
        this.#formChanged.next(null);
        this.#formChanged.complete();
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    async cancelChanges(): Promise<void> {
        const event = await firstValueFrom(this.#eventsSrv.event.get$());
        const session = await firstValueFrom(this.#sessionsSrv.session.get$());
        if (event.type === EventType.avet) {
            this.#sessionsSrv.rates.clear();
        }
        this.#sessionsSrv.session.load(session.event.id, session.id);
    }

    isTrulyInvalidOperativeDatesForm(): boolean {
        let isTrulyInvalid = false;
        if (!this.operativeDatesForm.valid) {
            const releaseGroup = this.operativeDatesForm.get('release');
            if (releaseGroup.invalid) {
                const releaseEnabled = this.operativeDatesForm.get('release.enable').value;
                const isReleaseDateRequired = this.operativeDatesForm.get('release.date').hasError('required');
                if (releaseEnabled || isReleaseDateRequired) {
                    isTrulyInvalid = true;
                }
            }
            const bookingGroup = this.operativeDatesForm.get('booking');
            if (bookingGroup.invalid) {
                const bookingEnabled = this.operativeDatesForm.get('booking.enable').value;
                const isBookingStartDateRequired = this.operativeDatesForm.get('booking.start_date').hasError('required');
                const isBookingEndDateRequired = this.operativeDatesForm.get('booking.end_date').hasError('required');
                if (bookingEnabled || isBookingStartDateRequired || isBookingEndDateRequired) {
                    isTrulyInvalid = true;
                }
            }
            const saleGroup = this.operativeDatesForm.get('sale');
            if (saleGroup.invalid) {
                const saleEnabled = this.operativeDatesForm.get('sale.enable').value;
                const isSaleStartDateRequired = this.operativeDatesForm.get('sale.start_date').hasError('required');
                const isSaleEndDateRequired = this.operativeDatesForm.get('sale.end_date').hasError('required');
                if (saleEnabled || isSaleStartDateRequired || isSaleEndDateRequired) {
                    isTrulyInvalid = true;
                }
            }
            const secondaryMarketSaleGroup = this.operativeDatesForm.get('secondary_market_sale');
            if (secondaryMarketSaleGroup.invalid) {
                const secondaryMarketSaleEnabled = this.operativeDatesForm.get('secondary_market_sale.enable').value;
                const isSecondaryMarketSaleStartDateRequired = this.operativeDatesForm.get('secondary_market_sale.start_date')
                    .hasError('required');
                const isSecondaryMarketSaleEndDateRequired = this.operativeDatesForm.get('secondary_market_sale.end_date')
                    .hasError('required');
                if (secondaryMarketSaleEnabled || isSecondaryMarketSaleStartDateRequired || isSecondaryMarketSaleEndDateRequired) {
                    isTrulyInvalid = true;
                }
            }
        }

        return isTrulyInvalid;
    }

    save(): void {
        this.save$().subscribe();
    }

    buildDirtyPayload(generalFormDirty: Record<string, GeneralForm>, operativeDatesDirty: Record<string, OperativeDatesForm>): PutSession {
        const settingsFields = [
            'taxes',
            'rates',
            'activity_sale_type',
            'smart_booking_status'
        ];

        const payload: PutSession = {};

        Object.keys(generalFormDirty).forEach(key => {
            if (settingsFields.includes(key)) {
                payload.settings = payload.settings || {};

                if (key === 'smart_booking_status') {
                    payload.settings.smart_booking = {
                        status: generalFormDirty[key]
                    };
                } else {
                    payload.settings[key] = generalFormDirty[key];
                }
            } else {
                payload[key] = generalFormDirty[key];
            }
        });

        if (operativeDatesDirty && Object.keys(operativeDatesDirty).length > 0) {
            payload.settings = {
                ...(payload.settings || {}),
                ...operativeDatesDirty
            };
        }

        return payload;
    }

    save$(): Observable<void> {
        if (this.generalForm.valid && !this.isTrulyInvalidOperativeDatesForm()) {
            return this.#sessionsSrv.session.get$()
                .pipe(
                    take(1),
                    switchMap(session => {
                        // Get the fields that have been modified
                        const generalFormDirty = FormControlHandler.getDirtyValues(this.generalForm);
                        const operativeDatesFormDirty = FormControlHandler.getDirtyValues(this.operativeDatesForm);
                        if (operativeDatesFormDirty['secondary_market_sale']) {
                            operativeDatesFormDirty['secondary_market_sale'] = this.operativeDatesForm.value.secondary_market_sale;
                        }
                        // Create an object with the required structure to send to the backend.
                        const sessionToSave = this.buildDirtyPayload(generalFormDirty, operativeDatesFormDirty);

                        // Disable fields sale,booking and secondary_market if the publication date is disabled in channels
                        if (!this.operativeDatesForm.value?.release?.enable) {
                            if (this.operativeDatesForm.value?.sale) {
                                sessionToSave.settings.sale = {
                                    ...(sessionToSave?.settings?.sale || {}),
                                    enable: false
                                };
                            }
                            if (this.operativeDatesForm.value?.booking) {
                                sessionToSave.settings.booking = {
                                    ...(sessionToSave?.settings?.booking || {}),
                                    enable: false
                                };
                            }
                            if (this.operativeDatesForm.value?.secondary_market_sale) {
                                sessionToSave.settings.secondary_market_sale = {
                                    ...(sessionToSave?.settings?.secondary_market_sale || {}),
                                    enable: false
                                };
                            }
                        }

                        forceDatesTimezone(sessionToSave, session.venue_template.venue.timezone);
                        return this.#sessionsSrv.updateSession(session.event.id, session.id, sessionToSave)
                            .pipe(map(() => session));
                    }),
                    switchMap(session => {
                        this.#ephemeralMessage.showSaveSuccess();
                        this.#sessionsSrv.session.load(session.event.id, session.id);
                        return this.#sessionsSrv.session.loading$()
                            .pipe(
                                first(isLoading => !isLoading),
                                map(() => this.#sessionsSrv.setRefreshSessionsList())
                            );
                    })
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    private model(): void {
        this.isLoading$ = booleanOrMerge([
            this.#entitiesSrv.isEntityLoading$(),
            this.#entitiesSrv.isEntityTaxesLoading(),
            this.#eventsSrv.eventRates.inProgress$(),
            this.#sessionsSrv.isSessionAdditionalConfigLoading$(),
            this.#sessionsSrv.isSessionSaving$(),
            this.#sessionCommunicationService.isChannelImagesInProgress$()
        ]);
        this.groupsAllowed$ = this.#eventsSrv.event.get$().pipe(filter(Boolean), map(event => event.settings.groups.allowed));
        this.isSessionGenerationError$ = this.#sessionsSrv.session.get$()
            .pipe(
                filter(session => !!session),
                map(session => session.generation_status === SessionGenerationStatus.error),
                shareReplay({ refCount: true, bufferSize: 1 })
            );
        this.showBookingSettings$ = this.#eventsSrv.event.get$()
            .pipe(filter(Boolean), map(event => event.settings.bookings?.enable));
        this.currentSession$ = this.#sessionsSrv.session.get$()
            .pipe(filter(value => !!value));
        this.#sessionsSrv.session.get$()
            .pipe(
                filter(value => !!value),
                withLatestFrom(this.#eventsSrv.event.get$(), this.#entitiesSrv.getEntity$()),
                takeUntil(this.#onDestroy)
            )
            .subscribe(([session, event, _]) => {
                this.#formChanged.next();
                this.sessionType = session.type;
                this.sessionReleaseStatusInd = getReleaseStatusIndicator(session);
                this.sessionSaleStatusInd = getSaleStatusIndicator(session);
                this.hasFortressVenue = event.venue_templates?.some(tpl =>
                    tpl.venue.access_control_systems?.some(system => system?.name === VenueAccessControlSystems.fortressBRISTOL)) || false;

                /*Check to prevent finalized option duplicates and
                prevent remaining finalized option when changing session*/
                const statusFinalizedIndex = this.sessionStatuses.indexOf(SessionStatus.finalized);
                if (statusFinalizedIndex !== -1) {
                    this.sessionStatuses.splice(statusFinalizedIndex, 1);
                }
                if (session.status === SessionStatus.cancelledExternal) {
                    this.sessionStatuses = [SessionStatus.cancelledExternal];
                } else if (session.generation_status === SessionGenerationStatus.error) {
                    this.sessionStatuses = [
                        SessionStatus.scheduled,
                        SessionStatus.cancelled
                    ];
                } else if (session.status === SessionStatus.finalized ||
                    (moment(session.settings.sale.end_date).isBefore(moment()) && moment(session.start_date).isBefore(moment()))) {
                    this.sessionStatuses.push(SessionStatus.finalized);
                }
                this.#entitiesSrv.getEntity$().pipe(first(Boolean)).subscribe(entity => {
                    this.updateForms(session, event, entity);
                    this.#changeDetector.markForCheck();
                });

            });

        this.avetConfig$ = this.#sessionsSrv.getSessionAdditionalConfig$()
            .pipe(
                filter(value => value !== null),
                takeUntil(this.#onDestroy),
                shareReplay({ refCount: true, bufferSize: 1 })
            );

        this.sessionImageUrl$ = this.#sessionCommunicationService.getChannelImages$()
            .pipe(
                filter(value => !!value),
                map(contents => contents.length && contents[0].image_url)
            );
    }

    private updateForms(session: Session, event: Event, entity: Entity): void {
        this.form.reset();
        // validators
        this.#sessionDatesFormValidation.addSessionDateValidations(
            {
                status: this.generalForm.get('status'),
                startDate: this.generalForm.get('start_date'),
                endDate: this.generalForm.get('end_date'),
                releaseEnable: this.operativeDatesForm.get('release.enable'),
                releaseDate: this.operativeDatesForm.get('release.date'),
                bookingEnable: this.operativeDatesForm.get('booking.enable'),
                bookingStartDate: this.operativeDatesForm.get('booking.start_date'),
                bookingEndDate: this.operativeDatesForm.get('booking.end_date'),
                saleEnable: this.operativeDatesForm.get('sale.enable'),
                saleStartDate: this.operativeDatesForm.get('sale.start_date'),
                saleEndDate: this.operativeDatesForm.get('sale.end_date'),
                secondaryMarketSaleStartDate: this.operativeDatesForm.get('secondary_market_sale.start_date'),
                secondaryMarketSaleEndDate: this.operativeDatesForm.get('secondary_market_sale.end_date')
            },
            event,
            this.#formChanged,
            entity,
            this.hasFortressVenue
        );
        this.addNestedControls(event, entity);
        this.form.patchValue({
            generalForm: {
                name: session.name,
                status: session.status,
                start_date: forceToDefaultTimezone(session.start_date),
                end_date: forceToDefaultTimezone(session.end_date) || null,
                reference: session.reference,
                rates: session.settings?.rates,
                taxes: {
                    ticket: {
                        id: session.settings?.taxes?.ticket.id
                    },
                    charges: {
                        id: session.settings?.taxes?.charges.id
                    }
                },
                activity_sale_type: session.settings?.activity_sale_type
            },
            operativeDatesForm: {
                release: {
                    enable: session.settings?.release.enable,
                    date: forceToDefaultTimezone(session.settings?.release.date)
                },
                booking: {
                    enable: session.settings?.booking.enable,
                    start_date: forceToDefaultTimezone(session.settings?.booking.start_date),
                    end_date: forceToDefaultTimezone(session.settings?.booking.end_date)
                },
                sale: {
                    enable: session.settings?.sale.enable,
                    start_date: forceToDefaultTimezone(session.settings?.sale.start_date),
                    end_date: forceToDefaultTimezone(session.settings?.sale.end_date)
                },
                secondary_market_sale: {
                    enable: session.settings?.secondary_market_sale?.enable ?? false,
                    start_date: entity.settings?.allow_secondary_market
                        ? forceToDefaultTimezone(session.settings?.secondary_market_sale?.start_date ?? session.settings?.sale.start_date)
                        : undefined,
                    end_date: entity.settings?.allow_secondary_market
                        ? forceToDefaultTimezone(session.settings?.secondary_market_sale?.end_date ?? session.settings?.sale.end_date)
                        : undefined
                }
            }
        });
        if (!entity.settings?.allow_secondary_market) {
            this.form.controls.operativeDatesForm.controls.secondary_market_sale.disable();
        }
        this.form.markAsPristine();
    }

    private addNestedControls(event: Event, entity: Entity): void {
        this.operativeDatesForm.get('release.enable').valueChanges
            .pipe(takeUntil(this.#formChanged))
            .subscribe(value => {
                if (event.settings.bookings?.enable) {
                    this.updateNestedControl(this.operativeDatesForm, 'booking.enable', value);
                }
                if (entity.settings.allow_secondary_market) {
                    this.updateNestedControl(this.operativeDatesForm, 'secondary_market_sale.enable', value);
                }
                this.updateNestedControl(this.operativeDatesForm, 'sale.enable', value);
            });

        this.operativeDatesForm.get('booking.enable').valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(checked => {
                const startControl = this.form.get('operativeDatesForm.booking.start_date');
                const endControl = this.form.get('operativeDatesForm.booking.end_date');

                startControl.markAsTouched();
                endControl.markAsTouched();

                if (checked) {
                    startControl.addValidators([Validators.required]);
                    endControl.addValidators([Validators.required]);
                } else {
                    startControl.removeValidators([Validators.required]);
                    endControl.removeValidators([Validators.required]);
                }
            });
    }

    private updateNestedControl(form: UntypedFormGroup, key: string, isEnabled: boolean): void {
        const nestControl = form.get(key) as FormControl;
        if (!isEnabled && nestControl.value) {
            nestControl.setValue(false);
        }
        this.changeControlEnabledState(nestControl, isEnabled);
    }

    private changeControlEnabledState(control: AbstractControl, isEnabled: boolean): void {
        if (isEnabled) {
            control.enable();
        } else {
            control.disable();
        }
    }

    private static requiredMap(control: AbstractControl): ValidationErrors | null {
        return control.value == null || control.value.size < 1 ? { required: true } : null;
    }
}
