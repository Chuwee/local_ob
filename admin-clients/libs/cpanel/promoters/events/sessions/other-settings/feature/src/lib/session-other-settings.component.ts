import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { DigitalTicketModes, digitalTicketModes } from '@admin-clients/cpanel/organizations/entities/data-access';
import { ExternalEntityService } from '@admin-clients/cpanel/organizations/entities/feature';
import {
    EventChannelsService
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import {
    EventSessionsService, PutSession, Session, SessionExternalSessionsConfig,
    SessionExternalSessionsConfigRequest, SessionLoyaltyPoints, SessionLoyaltyPointsGainType, SessionType
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import {
    RestrictedRatesListComponent, SgaRestrictedRatesListComponent
} from '@admin-clients/cpanel/promoters/shared/feature/rate-restrictions';
import { SubscriptionListsService } from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { CountriesService, EntitiesBaseService, EventType, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, viewChildren, viewChild, DestroyRef
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, type FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, forkJoin, Observable, Subject, switchMap, throwError } from 'rxjs';
import { filter, finalize, first, map, shareReplay, startWith, tap, withLatestFrom } from 'rxjs/operators';
import { SessionCountryFilterComponent } from './country-filter/session-country-filter.component';
import { SessionLiveStreamingComponent } from './live-streaming/session-live-streaming.component';
import { MemberLoginLimitComponent } from './member-login-limit/member-login-limit.component';
import { SessionPriceTypeRestrictionsComponent } from './price-types-restrictions/session-price-type-restrictions.component';
import { SessionSaleConstraintsComponent } from './sale-constraints/session-sale-constraints.component';
import { SessionAttendantsComponent } from './session-attendants/session-attendants.component';
import { SessionTicketLimitComponent } from './session-ticket-limit/session-ticket-limit.component';
import { SessionShowDateComponent } from './show-date/session-show-date.component';
import { SessionSubscriptionListComponent } from './subscription-list/session-subscription-list.component';
import { SessionTaxDataComponent } from './taxes/session-tax-data.component';
import { VenueTemplateVipViewsComponent } from './venue-template-vip-views/venue-template-vip-views.component';
import { SessionVirtualQueuesComponent } from './virtual-queues/session-virtual-queues.component';

interface SessionDataFormGroup {
    enableCaptcha: FormControl<boolean>;
    enableOrphanSeatsProtection: FormControl<boolean>;
}

@Component({
    selector: 'app-session-other-settings',
    templateUrl: './session-other-settings.component.html',
    styleUrls: ['./session-other-settings.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('expandSelection', [
            state('expanded', style({ height: '*' })),
            state('collapsed', style({ height: '0' })),
            transition('expanded <=> collapsed', [animate('0.1s')])
        ])
    ],
    imports: [
        FormContainerComponent, MaterialModule, ReactiveFormsModule, CommonModule, TranslatePipe,
        SessionPriceTypeRestrictionsComponent, SessionSaleConstraintsComponent, SessionTicketLimitComponent,
        SessionTaxDataComponent, SessionSubscriptionListComponent, SessionShowDateComponent,
        SessionCountryFilterComponent, SessionAttendantsComponent, SessionVirtualQueuesComponent,
        SessionLiveStreamingComponent, MemberLoginLimitComponent, VenueTemplateVipViewsComponent,
        FormControlErrorsComponent, ArchivedEventMgrComponent, RouterLink, RestrictedRatesListComponent,
        SgaRestrictedRatesListComponent, MatProgressSpinnerModule
    ]
})
export class SessionOtherSettingsComponent implements WritingComponent, OnInit, OnDestroy {
    readonly #onDestroy = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #sessionsService = inject(EventSessionsService);
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #externalEntityService = inject(ExternalEntityService);
    readonly #producersService = inject(ProducersService);
    readonly #subscriptionListService = inject(SubscriptionListsService);
    readonly #authService = inject(AuthenticationService);
    readonly #eventChannelsService = inject(EventChannelsService);
    readonly #eventsSrv = inject(EventsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #countriesService = inject(CountriesService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #externalSrv = inject(ExternalEntityService);

    readonly #priceTypes$ = this.#venueTemplatesService.getVenueTemplatePriceTypes$()
        .pipe(filter(Boolean));

    private readonly _matExpansionPanelQueryList = viewChildren(MatExpansionPanel);

    private readonly _vipViews = viewChild(VenueTemplateVipViewsComponent);
    private readonly _saleConstraintsComponent = viewChild(SessionSaleConstraintsComponent);
    private readonly _attendantsComponent = viewChild(SessionAttendantsComponent);
    private readonly _liveStreamingComponent = viewChild(SessionLiveStreamingComponent);
    private readonly _virtualQueuesComponent = viewChild(SessionVirtualQueuesComponent);
    private readonly _subscriptionListComponent = viewChild(SessionSubscriptionListComponent);
    private readonly _ticketLimitComponent = viewChild(SessionTicketLimitComponent);
    private readonly _showDateComponent = viewChild(SessionShowDateComponent);
    private readonly _countryFilterComponent = viewChild(SessionCountryFilterComponent);
    private readonly _taxDataComponent = viewChild(SessionTaxDataComponent);
    private readonly _memberLimitComponent = viewChild(MemberLoginLimitComponent);

    #eventId: number;
    #sessionId: number;
    linkToEventWalletConfig: string;

    readonly sessionTypes = SessionType;
    readonly digitalTicketModes = digitalTicketModes;

    readonly saleConstraintsGroup = this.#fb.group({
        enableCartTicketsLimit: false,
        cartTicketsLimit: [{ value: null, disabled: true }, [Validators.required, Validators.min(1)]],
        enablePriceGroupLimits: { value: false, disabled: true },
        priceGroupLimitsMatrix: this.#fb.array([])
    });

    readonly sessionDataGroup = this.getSessionDataFormGroup();

    readonly externalConfigFormGroup = this.#fb.group({
        digital_ticket_mode: null as DigitalTicketModes,
        useSessionConfig: null as boolean
    });

    readonly form = this.#fb.group({
        saleConstraintsGroup: this.saleConstraintsGroup,
        sessionDataGroup: this.sessionDataGroup,
        enableJointSales: { value: false, disabled: true },
        loyaltyPointGainConfig: this.#fb.group({
            amount: [0, Validators.min(0)],
            type: SessionLoyaltyPointsGainType.sessionPurchased
        }),
        enableHighDemand: null as boolean,
        session_external_config: this.externalConfigFormGroup
    });

    readonly session$ = this.#sessionsService.session.get$().pipe(filter(Boolean));

    readonly sessionType$ = this.session$.pipe(map(session => session.type));

    readonly loading$ = booleanOrMerge([
        this.#sessionsService.isSaleConstraintsInProgress$(),
        this.#sessionsService.isPriceTypeLimitInProgress$(),
        this.#sessionsService.isCartLimitInProgress$(),
        this.#sessionsService.isSessionExternalSessionsConfigLoading$(),
        this.#sessionsService.isSessionExternalBarcodesSaving$(),
        this.#sessionsService.isSessionSaving$(),
        this.#sessionsService.loyaltyPoints.loading$(),
        this.#producersService.isProducersListLoading$(),
        this.#producersService.isInvoicePrefixesLoading$(),
        this.#producersService.invoiceProvider.loading$(),
        this.#producersService.isProducerLoading$(),
        this.#eventChannelsService.eventChannel.inProgress$(),
        this.#subscriptionListService.isSubscriptionListsListLoading$(),
        this.#externalEntityService.configuration.loading$(),
        this.#venueTemplatesService.isVenueTemplatesListLoading$(),
        this.#sessionsService.rates.loading$(),
        this.#externalSrv.inventoryProviders.loading$()
    ]);

    readonly saving$ = new Subject<boolean>();
    readonly isOperator$ = this.#authService.getLoggedUser$().pipe(
        first(user => user !== null),
        map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR]))
    );

    readonly allowsVipViews$ = this.#entitiesSrv.getEntity$()
        .pipe(
            withLatestFrom(this.session$, this.isOperator$),
            filter(([entity, session]) => !!session && !!entity),
            map(([entity, session, isOperator]) =>
                entity.settings?.allow_vip_views && session.venue_template?.graphic && isOperator
            )
        );

    readonly countries$ = this.#countriesService.getCountries$().pipe(filter(countries => !!countries));
    readonly isAvetWs$ = this.#eventsSrv.event.get$()
        .pipe(
            filter(event => !!event),
            map(event => EventsService.isAvetWS(event)),
            tap(isAvet => {
                if (isAvet) {
                    this.form.get('enableJointSales').enable();
                }
            })
        );

    readonly showLiveStreaming$ = this.#entitiesSrv.getEntity$()
        .pipe(
            filter(entity => !!entity),
            map(entity => entity.settings?.live_streaming?.enabled)
        );

    readonly isAvetEvent$ = this.#eventsSrv.event.get$().pipe(
        filter(Boolean),
        map(event => event.type === EventType.avet),
        tap(isAvet => {
            if (isAvet) this.#venueTemplatesService.loadVenueTemplatesList({ eventId: this.#eventId, limit: 1 });
        }),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly isAvetApim$ = this.#externalEntityService.configuration.get$()
        .pipe(
            first(Boolean),
            map(config => config.avet_connection_type === 'APIM')
        );

    readonly $isSga = toSignal(this.#externalSrv.inventoryProviders.get$().pipe(
        filter(Boolean),
        map(inv => inv?.inventory_providers?.includes(ExternalInventoryProviders.sga))
    ));

    readonly $showLoyaltyPointsSettings = toSignal(
        this.#entitiesSrv.getEntity$().pipe(first(Boolean), map(entity => entity.settings?.allow_loyalty_points))
    );

    readonly $sessionRates = toSignal(this.#sessionsService.rates.get$().pipe(
        filter(Boolean)
    ));

    readonly sessionLoyaltyPointsGainType = SessionLoyaltyPointsGainType;

    enableRestrictions = false;

    ngOnInit(): void {
        this.#eventsSrv.event.get$()
            .pipe(first(event => !!event))
            .subscribe(event => this.#eventId = event.id);

        this.#venueTemplatesService.getVenueTemplatesListData$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(venueTmpls => {
                this.linkToEventWalletConfig = `/events/${this.#eventId}/general-data/additional-options`;
                this.#externalEntityService.configuration.reload(venueTmpls[0].entity.id);
            });

        this.session$
            .pipe(
                tap(session => {
                    this.form.get('enableHighDemand').patchValue(session.settings?.high_demand);
                    const digitalTicketMode = session.settings.session_external_config?.digital_ticket_mode;
                    this.#externalSrv.inventoryProviders.load(session.entity?.id);
                    if (digitalTicketMode) {
                        this.form.controls.session_external_config.controls.useSessionConfig.patchValue(true, { emitEvent: false });
                        this.form.controls.session_external_config.controls.useSessionConfig.disable();
                        this.form.controls.session_external_config.controls.digital_ticket_mode
                            .patchValue(digitalTicketMode, { emitEvent: false });
                    } else {
                        this.form.controls.session_external_config.controls.useSessionConfig.patchValue(false, { emitEvent: false });
                    }
                }),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(session => {
                this.#sessionId = session.id;
                if (this.$showLoyaltyPointsSettings()) {
                    this.#sessionsService.loyaltyPoints.load(this.#eventId, session.id);
                }
                this.#sessionsService.rates.load(this.#eventId, session.id);
            });

        combineLatest([
            this.#eventsSrv.event.get$().pipe(filter(event => !!event)),
            this.session$
        ]).pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(([event, session]) => {
                if (EventsService.isAvetWS(event)) {
                    this.#sessionsService.loadSessionExternalSessionsConfig(event.id, session.id);
                }
            });

        this.#countriesService.loadCountries();

        combineLatest([
            this.#priceTypes$,
            this.session$,
            this.#sessionsService.getSessionExternalSessionsConfig$()
                .pipe(
                    filter(session => !!session),
                    startWith(null as SessionExternalSessionsConfig)
                )
        ])
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(([priceTypes, session, externalSessionsConfig]) => {
                this.enableRestrictions = priceTypes.length > 1;
                this.updateForm(session, externalSessionsConfig);
            });

        this.#sessionsService.loyaltyPoints.get$()
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(loyaltyPoints => {
                this.form.get('loyaltyPointGainConfig.amount').patchValue(loyaltyPoints?.point_gain?.amount || 0);
                this.form.get('loyaltyPointGainConfig.type')
                    .patchValue(loyaltyPoints?.point_gain?.type || SessionLoyaltyPointsGainType.sessionPurchased);
            });

    }

    ngOnDestroy(): void {
        this.#sessionsService.clearSaleConstraints();
        this.#venueTemplatesService.clearVenueTemplatePriceTypes();
        this.#sessionsService.clearSessionExternalSessionsConfig();
        this.#producersService.clearProducersList();
        this.#producersService.clearInvoicePrefixes();
        this.#sessionsService.rates.clear();
        this.#externalSrv.inventoryProviders.clear();
    }

    cancel(): void {
        this.form.controls.session_external_config.reset();
        this.reloadModels();
        if (this.$showLoyaltyPointsSettings()) {
            this.#sessionsService.loyaltyPoints.load(this.#eventId, this.#sessionId);
        }
    }

    save(): void {
        this.save$().subscribe(() => this.form.markAsPristine());
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            this.saving$.next(true);
            return this.#priceTypes$.pipe(
                first(),
                switchMap(priceTypes => {

                    const data = this.form.getRawValue();
                    let requests: Observable<void>[] = [];

                    const saleConstraintsRequests = this._saleConstraintsComponent().getSaleConstraintsRequest(
                        priceTypes,
                        this.#eventId,
                        this.#sessionId
                    );

                    requests = requests.concat(...saleConstraintsRequests);

                    if (this.sessionDataGroup.dirty
                        || this.form.get('enableHighDemand').dirty
                        || this.form.controls.session_external_config.dirty) {
                        const sessionData = data.sessionDataGroup;
                        const digitalTicketMode = data.session_external_config?.digital_ticket_mode;
                        const session: PutSession = {
                            id: this.#sessionId,
                            settings: {
                                channels: this._showDateComponent().getValue(),
                                enable_captcha: sessionData.enableCaptcha,
                                enable_orphan_seats: sessionData.enableOrphanSeatsProtection,
                                taxes: this._taxDataComponent().getValue(),
                                subscription_list: this._subscriptionListComponent().getValue(),
                                limits: {
                                    ...this._ticketLimitComponent().getValue(),
                                    ...this._memberLimitComponent()?.getValue()
                                },
                                virtual_queue: this._virtualQueuesComponent()?.getValue(),
                                live_streaming: this._liveStreamingComponent()?.getValue(),
                                attendant_tickets: this._attendantsComponent().getValue(),
                                country_filter: this._countryFilterComponent()?.getValue(),
                                high_demand: data.enableHighDemand,
                                session_external_config: {
                                    digital_ticket_mode: digitalTicketMode
                                }
                            }
                        };

                        requests.push(
                            this.#sessionsService.updateSession(this.#eventId, this.#sessionId, session)
                        );
                    }

                    if (this.form.get('enableJointSales').dirty) {
                        const enableJointSales: SessionExternalSessionsConfigRequest = {
                            generalAdmission: data.enableJointSales
                        };
                        requests.push(
                            this.#sessionsService.saveSessionExternalSessionsConfig(this.#eventId, this.#sessionId, enableJointSales)
                        );
                    }

                    if (this.form.get('loyaltyPointGainConfig').dirty) {
                        const loyaltyPoints: SessionLoyaltyPoints = {
                            point_gain: data.loyaltyPointGainConfig
                        };
                        requests.push(
                            this.#sessionsService.loyaltyPoints.update(this.#eventId, this.#sessionId, loyaltyPoints)
                        );
                    }

                    const vipViews = this._vipViews();
                    if (vipViews && this.form.get(vipViews.formGroupName)?.dirty) {
                        requests.push(vipViews.save$());
                    }

                    return forkJoin(requests).pipe(
                        tap(() => {
                            this.#ephemeralMessage.showSaveSuccess();
                            this.reloadModels();
                            if (this.$showLoyaltyPointsSettings()) {
                                this.#sessionsService.loyaltyPoints.load(this.#eventId, this.#sessionId);
                            }
                        }));
                }),
                finalize(() => this.saving$.next(false))
            );
        } else {
            FormControlHandler.markAllControlsAsTouched(this.form);
            this._saleConstraintsComponent().markForCheck();
            this._attendantsComponent().markForCheck();
            this._liveStreamingComponent()?.markForCheck();
            this._virtualQueuesComponent()?.markForCheck();
            this._subscriptionListComponent().markForCheck();
            this._ticketLimitComponent().markForCheck();
            this._showDateComponent().markForCheck();
            this._countryFilterComponent()?.markForCheck();
            this._taxDataComponent().markForCheck();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList());
            return throwError(() => 'invalid form');
        }
    }

    private updateForm(session: Session, externalSessionsConfig: SessionExternalSessionsConfig): void {
        this.sessionDataGroup.patchValue({
            enableCaptcha: session.settings?.enable_captcha,
            enableOrphanSeatsProtection: !!session.settings?.enable_orphan_seats
        }, { onlySelf: true });
        this.sessionDataGroup.markAsPristine();
        this.sessionDataGroup.markAsUntouched();
        this.form.get('enableJointSales').patchValue(externalSessionsConfig?.generalAdmission, { onlySelf: true });
        this.form.get('enableJointSales').markAsPristine();
        this.form.get('enableJointSales').markAsUntouched();
    }

    private reloadModels(): void {
        if (this.form.get('enableJointSales').dirty) {
            this.#sessionsService.loadSessionExternalSessionsConfig(this.#eventId, this.#sessionId);
        }
        this.#sessionsService.session.load(this.#eventId, this.#sessionId);
    }

    private getSessionDataFormGroup(): FormGroup<SessionDataFormGroup> {
        const formGroup = this.#fb.group({
            enableCaptcha: false,
            enableOrphanSeatsProtection: false
        });
        this.#eventsSrv.event.get$()
            .pipe(
                first(event => !!event),
                filter(event => event.type === EventType.activity || event.type === EventType.themePark)
            )
            .subscribe(() => formGroup.controls.enableOrphanSeatsProtection.disable());

        return formGroup;
    }
}
