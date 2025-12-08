import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { DigitalTicketModes, digitalTicketModes } from '@admin-clients/cpanel/organizations/entities/data-access';
import { ExternalEntityService } from '@admin-clients/cpanel/organizations/entities/feature';
import { Event, EventSessionPackConf, EventsService, EventStatus, PutEvent } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { EventSessionsService, SessionsFilterFields, SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EntitiesBaseService, EventType, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, viewChildren, viewChild, OnDestroy, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, throwError } from 'rxjs';
import { filter, first, map, shareReplay, tap } from 'rxjs/operators';
import { EventAccommodationsComponent } from './accommodations/event-accommodations.component';
import { EventBookingComponent } from './booking/event-booking.component';
import { EventInteractiveVenueComponent } from './interactive-venue/event-interactive-venue.component';

@Component({
    imports: [
        AsyncPipe, ReactiveFormsModule, MaterialModule, TranslatePipe, EventAccommodationsComponent,
        EventBookingComponent, EventInteractiveVenueComponent, FormContainerComponent, ArchivedEventMgrComponent, RouterLink
    ],
    selector: 'app-event-additional-options',
    templateUrl: './event-additional-options.component.html',
    styleUrls: ['./event-additional-options.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventAdditionalOptionsComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #fb = inject(FormBuilder);
    readonly #eventsService = inject(EventsService);
    readonly #entityService = inject(EntitiesBaseService);
    readonly #externalEntityService = inject(ExternalEntityService);
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #sessionsService = inject(EventSessionsService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #onDestroy = inject(DestroyRef);

    private readonly matExpansionPanelQueryList = viewChildren(MatExpansionPanel);
    private readonly _bookingComponent = viewChild(EventBookingComponent);
    private readonly _interactiveVenueComponent = viewChild(EventInteractiveVenueComponent);
    private readonly _accommodationsComponent = viewChild(EventAccommodationsComponent);

    // In the future, if we have more verificators, we can add them to the array
    #verificators = [ExternalInventoryProviders.italianCompliance];
    #eventId: number;
    entityId: number;
    linkToEntityWalletConfig: string;
    isActivity = false;
    $isAvetEvent = signal(null as boolean);
    $attendantVerificationRequired = signal(null as boolean);
    $verificator = signal(null as ExternalInventoryProviders);

    readonly #event$ = this.#eventsService.event.get$()
        .pipe(
            filter(Boolean),
            tap((event: Event) => {
                this.#sessionsService.loadAllSessions(event.id, { fields: [SessionsFilterFields.type] });
                this.#eventId = event.id;
                this.isActivity = event.type === EventType.themePark || event.type === EventType.activity;
                this.$isAvetEvent.set(event.type === EventType.avet);
                if (this.$isAvetEvent()) {
                    this.#venueTemplatesService.loadVenueTemplatesList({ eventId: event.id, limit: 1 });
                }
                this.$verificator.set(event.additional_config?.inventory_provider);
                this.$attendantVerificationRequired.set(this.#verificators.includes(this.$verificator()));
                if (event.status === EventStatus.ready || !this.$attendantVerificationRequired()) {
                    this.form?.controls.attendant_verification.controls.attendant_verification_required.disable();
                }
            }),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly digitalTicketModes = digitalTicketModes;
    readonly eventSessionPackConf = EventSessionPackConf;
    readonly isInteractiveVenueEnabled = this.#entityService.getEntity$()
        .pipe(
            first(Boolean),
            map(entity => entity.settings?.interactive_venue?.enabled)
        );

    readonly isAccommodationsVisible$ = this.#entityService.getEntity$()
        .pipe(
            first(Boolean),
            map(entity => !!entity.settings?.accommodations?.enabled),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly $isOtpSmsVerificationEnabled = toSignal(this.#entityService.getEntity$().pipe(
        first(Boolean), map(
            entity => entity?.settings?.external_integration?.phone_validator?.enabled
        ), tap(enabled => {
            if (!enabled) {
                this.form?.controls.phone_verification.controls.phone_verification_required.disable();
            }
        }))
    );

    readonly isAvetApim$ = this.#externalEntityService.configuration.get$()
        .pipe(
            first(Boolean),
            map(config => config.avet_connection_type === 'APIM')
        );

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#eventsService.event.inProgress$(),
        this.#sessionsService.isAllSessionsLoading$(),
        this.#externalEntityService.configuration.loading$()
    ]);

    readonly specialConfigsForm = this.#fb.group({
        enableFestival: null as boolean,
        allow_venue_reports: null as boolean,
        allowGroups: null as boolean
    });

    readonly sessionPackConfigForm = this.#fb.group({
        enabled: { value: false, disabled: true },
        type: { value: null, disabled: true }
    });

    readonly tiersForm = this.#fb.group({
        enableTiers: null as boolean
    });

    readonly phoneVerificationForm = this.#fb.group({
        phone_verification_required: null as boolean
    });

    readonly externalConfigForm = this.#fb.group({
        digital_ticket_mode: null as DigitalTicketModes,
        useEventConfig: null as boolean
    });

    readonly attendantVerificationForm = this.#fb.group({
        attendant_verification_required: null as boolean
    });

    readonly form = this.#fb.group({
        specialConfigs: this.specialConfigsForm,
        sessionPack: this.sessionPackConfigForm,
        tiers: this.tiersForm,
        event_external_config: this.externalConfigForm,
        attendant_verification: this.attendantVerificationForm,
        phone_verification: this.phoneVerificationForm
    });

    ngOnInit(): void {
        // data update handlers
        this.updateForm();
        this.initFormHandlers();

        this.#venueTemplatesService.getVenueTemplatesListData$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(venueTmpls => {
                this.entityId = venueTmpls[0].entity.id;
                this.linkToEntityWalletConfig = `/entities/${this.entityId}/external/ticketing`;
                this.#externalEntityService.configuration.reload(this.entityId);
            });
    }

    ngOnDestroy(): void {
        this.#externalEntityService.configuration.clear();
        this.#sessionsService.clearAllSessions();
    }

    cancel(): void {
        this.form.controls.event_external_config.reset();
        this.form.markAsPristine();
        this.#eventsService.event.load(this.#eventId.toString());
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const formValues = this.form.getRawValue();
            const event: PutEvent = { settings: {} };
            if (formValues.specialConfigs?.enableFestival != null) {
                event.settings.festival = formValues.specialConfigs.enableFestival;
            }
            if (formValues.specialConfigs?.allow_venue_reports != null) {
                event.settings.allow_venue_reports = formValues.specialConfigs?.allow_venue_reports;
            }
            if (!this.isActivity && formValues.sessionPack) {
                if (formValues.sessionPack?.enabled) {
                    event.settings.session_pack = formValues.sessionPack.type;
                } else {
                    event.settings.session_pack = EventSessionPackConf.disabled;
                }
            }

            const bookingComponent = this._bookingComponent();
            if (bookingComponent?.getValue()) {
                event.settings.bookings = bookingComponent?.getValue();
            } else {
                event.settings.bookings = {
                    enable: false
                };
            }

            if (formValues.tiers?.enableTiers) {
                event.settings.use_tiered_pricing = formValues.tiers.enableTiers;
            }

            if (this.form?.controls.attendant_verification.controls.attendant_verification_required.enabled) {
                event.attendant_verification_required = formValues.attendant_verification.attendant_verification_required;
            }

            if (formValues.phone_verification?.phone_verification_required) {
                event.phone_verification_required = formValues.phone_verification.phone_verification_required;
            }

            const interactiveVenueComponent = this._interactiveVenueComponent();
            if (interactiveVenueComponent?.getValue()) {
                event.settings.interactive_venue = interactiveVenueComponent?.getValue();
            }

            const accommodationsComponent = this._accommodationsComponent();
            if (accommodationsComponent?.getValue()) {
                event.settings.accommodations = accommodationsComponent?.getValue();
            }

            if (this.specialConfigsForm.controls.allowGroups.value !== null) {
                event.settings.groups = {
                    allowed: this.specialConfigsForm.controls.allowGroups.value
                };
            }

            if (this.form.controls.event_external_config.controls.digital_ticket_mode.value !== null
                && this.form.controls.event_external_config.dirty) {
                event.settings.event_external_config = {
                    digital_ticket_mode: this.form.controls.event_external_config.controls.digital_ticket_mode.value
                };
            }

            return this.#eventsService.event.update(this.#eventId, event).pipe(
                tap(() => {
                    this.#eventsService.event.load(this.#eventId.toString());
                    this.#ephemeralMessage.showSaveSuccess();
                })
            );
        } else {
            FormControlHandler.markAllControlsAsTouched(this.form);
            this._bookingComponent()?.markForCheck();
            this._interactiveVenueComponent()?.markForCheck();
            this._accommodationsComponent()?.markForCheck();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this.matExpansionPanelQueryList());
            return throwError(() => 'invalid form');
        }
    }

    saveEvent(): void {
        this.save$().subscribe();
    }

    private updateForm(): void {
        combineLatest([
            this.#event$,
            this.#sessionsService.getAllSessions$()
        ])
            .pipe(
                filter(resp => resp.every(Boolean)),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(([event, sessions]) => {
                this.form.patchValue({
                    specialConfigs: {
                        enableFestival: event.settings.festival,
                        allowGroups: (event.type === EventType.activity || event.type === EventType.themePark) ?
                            event.settings.groups?.allowed : null,
                        allow_venue_reports: event.settings.allow_venue_reports
                    },
                    sessionPack: {
                        enabled: event.settings.session_pack !== EventSessionPackConf.disabled,
                        type: event.settings.session_pack !== EventSessionPackConf.disabled && event.settings.session_pack
                            || EventSessionPackConf.restricted
                    },
                    tiers: {
                        enableTiers: event.settings.use_tiered_pricing
                    },
                    attendant_verification: {
                        attendant_verification_required: event.attendant_verification_required
                    },
                    phone_verification: {
                        phone_verification_required: event.phone_verification_required
                    }
                });

                if (!this.isActivity) {
                    const isSessionPackAvailable = sessions.data?.find(session => session.type !== SessionType.session) !== undefined;
                    if (isSessionPackAvailable) {
                        this.form.controls.sessionPack.disable();
                    } else {
                        this.form.controls.sessionPack.enable();
                        if (!this.sessionPackConfigForm.controls.enabled.value) {
                            this.sessionPackConfigForm.controls.type.disable();
                        }
                    }
                }

                if (event.settings.use_tiered_pricing || !!sessions.metadata.total) {
                    this.form.controls.tiers.controls.enableTiers.disable();
                }

                const digitalTicketMode = event.settings.event_external_config?.digital_ticket_mode;
                if (digitalTicketMode) {
                    this.form.controls.event_external_config.controls.useEventConfig.patchValue(true, { emitEvent: false });
                    this.form.controls.event_external_config.controls.useEventConfig.disable();
                    this.form.controls.event_external_config.controls.digital_ticket_mode
                        .patchValue(digitalTicketMode, { emitEvent: false });
                } else {
                    this.form.controls.event_external_config.controls.useEventConfig.patchValue(false, { emitEvent: false });
                }

                this.form.markAsPristine();
            });
    }

    private initFormHandlers(): void {
        combineLatest([
            this.#eventsService.event.get$(),
            this.form.valueChanges // only used as a trigger
        ])
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(([event]) => {
                FormControlHandler
                    .checkAndRefreshDirtyState(this.specialConfigsForm.controls.enableFestival, event.settings.festival);
                FormControlHandler
                    .checkAndRefreshDirtyState(this.specialConfigsForm.controls.allow_venue_reports, event.settings.allow_venue_reports);

                if (!this.isActivity) {
                    FormControlHandler.checkAndRefreshDirtyState(this.sessionPackConfigForm.controls.enabled,
                        event.settings.session_pack !== EventSessionPackConf.disabled);
                    FormControlHandler.checkAndRefreshDirtyState(this.sessionPackConfigForm.controls.type,
                        event.settings.session_pack !== EventSessionPackConf.disabled && event.settings.session_pack);
                }
                // tiers
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.tiers.controls.enableTiers, event.settings.use_tiered_pricing
                );
            });

        if (!this.isActivity) {
            this.sessionPackConfigForm.controls.enabled.valueChanges
                .pipe(takeUntilDestroyed(this.#onDestroy))
                .subscribe(enabled => {
                    if (this.sessionPackConfigForm.controls.enabled && enabled) {
                        this.sessionPackConfigForm.controls.type.enable();
                    } else {
                        this.sessionPackConfigForm.controls.type.disable();
                    }
                });
        }
    }
}
