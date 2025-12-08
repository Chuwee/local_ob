import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    Channel, ChannelBuild, ChannelPurchaseConfig, ChannelsExtendedService, ChannelsPipesModule, ChannelsService, ChannelStatus,
    ChannelType, ChannelWhitelabelSettings, HeaderText, IsV3$Pipe, PricesDisplay, PromotionsCodePersistenceMode, RedirectionPolicyMode,
    RedirectionPolicyType, SessionsLayout, TaxesDisplay, VenueContentLayout
} from '@admin-clients/cpanel/channels/data-access';
import { EntitiesBaseService, Entity, InteractiveVenues } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, HelpButtonComponent,
    SelectSearchComponent, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { atLeastOneRequiredInFormGroup, booleanOrMerge, FormControlHandler, urlValidator } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, QueryList, viewChildren, ViewChildren } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatOption } from '@angular/material/autocomplete';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatError, MatInput, MatLabel } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatFormField, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, forkJoin, Observable, throwError } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-channel-components',
    templateUrl: './channel-components.component.html',
    styleUrls: ['./channel-components.component.scss'],
    imports: [
        FormContainerComponent, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent, AsyncPipe, UpperCasePipe,
        ChannelsPipesModule, TabsMenuComponent, TabDirective, SelectSearchComponent, HelpButtonComponent, IsV3$Pipe,
        MatProgressSpinnerModule, MatRadioGroup, MatRadioButton, MatSelect, MatOption, MatExpansionPanelHeader, MatIcon,
        MatExpansionPanel, MatExpansionPanelTitle, MatAccordion, MatCheckbox, MatFormField, MatInput, MatLabel, MatError
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelComponentsComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #channelsService = inject(ChannelsService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #entityService = inject(EntitiesBaseService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #selectedLanguage = new BehaviorSubject<string>(null);
    readonly #defaultLang = new BehaviorSubject<string>(null);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    private _tabsMenus = viewChildren(TabsMenuComponent);

    readonly channels$ = this.#channelsService.channelsList.getList$().pipe(
        filter(Boolean), map(channels => channels?.filter(ch => ch.id !== this.channel.id))
    );

    readonly showRelatedChannel = this.#fb.control(false);

    readonly form = this.#fb.group({
        headerTexts: this.#fb.group({
            purchaseOptionsAndClientInfo: false,
            paymentGateway: false
        }),
        venueLayout: { value: null as VenueContentLayout, disabled: true },
        interactiveVenue: this.#fb.group({
            enabled: { value: false, disabled: true },
            venueTypes: [{ value: null as InteractiveVenues[], disabled: true }, [Validators.required]],
            venueOptions: this.#fb.group({
                venueViewEnabled: { value: false, disabled: true },
                venueSectorViewEnabled: { value: false, disabled: true },
                venueSeatViewEnabled: { value: false, disabled: true }
            }, { validators: atLeastOneRequiredInFormGroup('required') })
        }),
        allowPriceTypeTagFilter: false as boolean,
        sessionsLayout: this.#fb.group({
            type: [null as SessionsLayout, Validators.required],
            listMax: [{ value: null as number, disabled: true }, [Validators.required, Validators.min(1)]]
        }),
        taxDisplay: this.#fb.group({
            type: [null as TaxesDisplay, Validators.required],
            info: false,
            prices: [null as PricesDisplay, Validators.required]
        }),
        otherSettings: this.#fb.group({
            includeTaxesSeparately: false,
            persistPromoValidationCodes: false
        }),
        redirectionPolicies: this.#fb.group({}),
        allowKeepBuying: false,
        addRelatedChannel: this.showRelatedChannel,
        relatedChannel: [{ value: null as number, disabled: true }, [Validators.required]]
    });

    readonly $showRelatedChannel = toSignal(this.showRelatedChannel.valueChanges
        .pipe(tap(value => value ? this.form.controls.relatedChannel.enable() : this.form.controls.relatedChannel.disable())));

    readonly inProgress$ = booleanOrMerge([
        this.#entityService.isEntityLoading$(),
        this.#channelsExtSrv.isPurchaseConfigLoading$(),
        this.#channelsExtSrv.isPurchaseConfigSaving$(),
        this.#channelsService.isChannelLoading$(),
        this.#channelsService.isChannelSaving$(),
        this.#channelsService.channelWhitelabelSettings.loading$()
    ]);

    readonly languages$ = this.#channelsService.getChannel$()
        .pipe(
            first(Boolean),
            tap(channel => this.#defaultLang.next(channel.languages.default)),
            map(channel => channel.languages.selected),
            filter(Boolean),
            tap(languages => {
                this.#selectedLanguage.next(languages[0]);
            }),
            shareReplay(1)
        );

    $languages = toSignal(this.languages$);

    readonly selectedLanguage$ = this.#selectedLanguage.asObservable();
    readonly defaultLang$ = this.#defaultLang.asObservable();
    isInteractiveVenueEnabled = false;
    channel!: Channel;
    isHeaderTextsEnabled = false;
    isLayoutEnabled = false;
    allowedInteractiveVenues: InteractiveVenues[] = [];
    venueContentLayouts = VenueContentLayout;
    sessionsLayouts = SessionsLayout;
    taxesDisplay = TaxesDisplay;
    pricesDisplay = PricesDisplay;
    redirectionPolicyTypesValues = Object.values(RedirectionPolicyType);
    redirectionPolicyModes = RedirectionPolicyMode;
    channelType = ChannelType;
    langFormGroup = this.#fb.group({});

    ngOnInit(): void {
        this.#initForm();

        combineLatest([
            this.#channelsService.getChannel$()
                .pipe(
                    first(Boolean),
                    switchMap(channel => {
                        this.channel = channel;
                        this.#channelsExtSrv.loadPurchaseConfig(channel.id);
                        this.#channelsService.channelWhitelabelSettings.load(channel.id);
                        this.#initHeaderTextFields(channel);
                        this.#initVenueLayoutsFields(channel);
                        this.#channelsService.channelsList.load({
                            limit: 999,
                            offset: 0,
                            sort: 'name:asc',
                            entityId: channel.entity.id,
                            status: [ChannelStatus.active],
                            type: ChannelType.web,
                            includeThirdPartyChannels: true
                        });
                        return this.#entityService.getEntity$();
                    }),
                    first(Boolean),
                    tap(entity => this.#initInteractiveVenueFields(entity))
                ),
            this.#channelsExtSrv.getPurchaseConfig$().pipe(filter(Boolean)),
            this.#channelsService.channelWhitelabelSettings.get$().pipe(filter(Boolean))
        ])
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([_, config, whitelabelSettings]) => {
                this.#updateFormValues(config, whitelabelSettings);
            });

        this.#initFormHandlers();
    }

    ngOnDestroy(): void {
        this.#channelsExtSrv.clearPurchaseConfig();
        this.#entityService.clearEntity();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#loadData();
        });
    }

    save$(): Observable<void[]> {
        if (this.form.valid && this.form.dirty) {
            const ctrls = this.form.controls;
            const purchaseCtrls = [
                ctrls.headerTexts,
                ctrls.interactiveVenue,
                ctrls.otherSettings,
                ctrls.allowPriceTypeTagFilter,
                ctrls.sessionsLayout,
                ctrls.taxDisplay,
                ctrls.venueLayout,
                ctrls.redirectionPolicies,
                ctrls.relatedChannel,
                ctrls.addRelatedChannel
            ];
            const obs$: Observable<void>[] = [];
            const data = this.form.value;
            if (purchaseCtrls.some(ctrl => ctrl.dirty)) {
                const { purchaseOptionsAndClientInfo, paymentGateway } = data.headerTexts;
                const headerTexts = Object.values(HeaderText).filter(value =>
                    (value === HeaderText.purchaseOptionsAndClientInfo && purchaseOptionsAndClientInfo) ||
                    (value === HeaderText.paymentGateway && paymentGateway)
                );
                const config: ChannelPurchaseConfig = {
                    include_taxes_separately: data.otherSettings.includeTaxesSeparately,
                    venue: {},
                    allow_price_type_tag_filter: data.allowPriceTypeTagFilter,
                    sessions: {
                        visualization: {
                            format: data.sessionsLayout.type,
                            max_listed: data.sessionsLayout.listMax ?? undefined
                        },
                        promotions: {
                            code_persistence: data.otherSettings.persistPromoValidationCodes
                                ? PromotionsCodePersistenceMode.mantain : PromotionsCodePersistenceMode.disappear
                        }
                    },
                    header_texts: headerTexts,
                    redirection_policy: Object.keys(data.redirectionPolicies).map(type => ({
                        type: (type as RedirectionPolicyType),
                        mode: data.redirectionPolicies[type].mode,
                        value: data.redirectionPolicies[type].mode === RedirectionPolicyMode.goToUrl ?
                            this.#removeBlankAttributes(data.redirectionPolicies[type].value) : null
                    })),
                    price_display: {
                        taxes: data.taxDisplay.type === TaxesDisplay.included && data.taxDisplay.info
                            ? TaxesDisplay.includedInfo : data.taxDisplay.type,
                        prices: data.taxDisplay.prices
                    }
                };
                if (this.isLayoutEnabled) {
                    config.venue.content_layout = data.venueLayout;
                }
                if (this.isInteractiveVenueEnabled) {
                    config.venue.allow_interactive_venue = data.interactiveVenue.enabled;
                    config.venue.interactive_venue_types = data.interactiveVenue.venueTypes;
                    config.venue.allow_venue_3d_view = data.interactiveVenue.venueOptions?.venueViewEnabled;
                    config.venue.allow_seat_3d_view = data.interactiveVenue.venueOptions?.venueSeatViewEnabled;
                    config.venue.allow_sector_3d_view = data.interactiveVenue.venueOptions?.venueSectorViewEnabled;
                }
                if (this.showRelatedChannel.value) {
                    config.related_channel = data.relatedChannel;
                }
                config.add_related_channel = this.showRelatedChannel.value;
                obs$.push(this.#channelsExtSrv.updatePurchaseConfig(this.channel.id, config));
            }
            if (ctrls.allowKeepBuying.dirty) {
                const whitelabelSettings = { cart: { allow_keep_buying: data.allowKeepBuying } };
                obs$.push(this.#channelsService.channelWhitelabelSettings.update(this.channel.id, whitelabelSettings)
                    .pipe(tap(() => this.#channelsService.channelWhitelabelSettings.load(this.channel.id))));
            }

            return forkJoin(obs$).pipe(
                tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            this.#goToRedirectionPolicyInvalidTab();
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        if (this.form.controls.allowKeepBuying.dirty) {
            this.#channelsService.channelWhitelabelSettings.load(this.channel.id);
        }
        this.#loadData();
    }

    #initForm(): void {
        this.languages$.pipe(
            take(1)
        ).subscribe(languages => {
            this.redirectionPolicyTypesValues.forEach(type => {
                this.langFormGroup = this.#fb.group({});
                languages.forEach(language => {
                    if (language === this.#defaultLang.getValue()) {
                        this.langFormGroup.addControl(language, this.#fb.control(
                            { value: '', disabled: true }, [Validators.required, urlValidator()])
                        );
                    } else {
                        this.langFormGroup.addControl(language, this.#fb.control(
                            { value: '', disabled: true }, [urlValidator()]
                        ));
                    }
                });
                this.form.controls.redirectionPolicies.setControl(type, this.#fb.group({
                    mode: [RedirectionPolicyMode.origin, Validators.required],
                    value: this.langFormGroup
                }));
            });
        });
    }

    #initVenueLayoutsFields(channel: Channel): void {
        this.isLayoutEnabled = channel.build !== ChannelBuild.mmc;
        if (this.isLayoutEnabled) {
            this.form.controls.venueLayout.enable();
        }
    }

    #initHeaderTextFields(channel: Channel): void {
        this.isHeaderTextsEnabled = channel.type === ChannelType.webBoxOffice || channel.type === ChannelType.web;
    }

    #initInteractiveVenueFields(entity: Entity): void {
        this.isInteractiveVenueEnabled = entity.settings?.interactive_venue?.enabled;
        if (this.isInteractiveVenueEnabled) {
            let interactiveVenues = entity.settings.interactive_venue.allowed_venues.slice();
            const showPacifaVenue = this.channel.settings.v4_enabled;
            if (!showPacifaVenue) {
                interactiveVenues = interactiveVenues.filter(venue => venue !== InteractiveVenues.venue3dPacifa);
            }
            this.allowedInteractiveVenues = interactiveVenues;
            this.form.controls.interactiveVenue.controls.enabled.enable();
        }
    }

    #updateFormValues(config: ChannelPurchaseConfig, whitelabelSettings: ChannelWhitelabelSettings): void {
        this.form.patchValue({
            venueLayout: config.venue?.content_layout,
            interactiveVenue: {
                enabled: config.venue?.allow_interactive_venue ?? false,
                venueTypes: config.venue.interactive_venue_types || [],
                venueOptions: {
                    venueViewEnabled: config.venue?.allow_venue_3d_view ?? false,
                    venueSectorViewEnabled: config.venue?.allow_sector_3d_view ?? false,
                    venueSeatViewEnabled: config.venue?.allow_seat_3d_view ?? false
                }
            },
            allowPriceTypeTagFilter: config.allow_price_type_tag_filter,
            sessionsLayout: {
                type: config.sessions?.visualization?.format,
                listMax: config.sessions?.visualization?.max_listed
            },
            taxDisplay: {
                type: [TaxesDisplay.included, TaxesDisplay.includedInfo].includes(config?.price_display?.taxes)
                    ? TaxesDisplay.included : TaxesDisplay.onTop,
                info: config?.price_display?.taxes === TaxesDisplay.includedInfo,
                prices: config?.price_display?.prices ?? PricesDisplay.base
            },
            otherSettings: {
                includeTaxesSeparately: config.include_taxes_separately,
                persistPromoValidationCodes: config.sessions?.promotions?.code_persistence === 'MAINTAIN_AFTER_VALIDATION'
            },
            allowKeepBuying: whitelabelSettings.cart?.allow_keep_buying,
            headerTexts: {
                purchaseOptionsAndClientInfo: config.header_texts.includes(HeaderText.purchaseOptionsAndClientInfo),
                paymentGateway: config.header_texts.includes(HeaderText.paymentGateway)
            },
            relatedChannel: config.related_channel,
            addRelatedChannel: config.add_related_channel
        });
        config.redirection_policy?.forEach(policy => {
            const element = this.form.get(['redirectionPolicies', policy.type]);
            element.patchValue({
                mode: policy.mode,
                value: policy.value
            });
            if (policy.mode === RedirectionPolicyMode.goToUrl) {
                element.enable();
            }
        });
        if (this.allowedInteractiveVenues?.length === 1 && !config.venue?.interactive_venue_types?.length) {
            this.form.controls.interactiveVenue.controls.venueTypes.setValue(this.allowedInteractiveVenues);
        }
    }

    #initFormHandlers(): void {
        this.form.controls.sessionsLayout.controls.type.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((selectedType: SessionsLayout) => {
                if (selectedType === SessionsLayout.list) {
                    this.form.controls.sessionsLayout.controls.listMax.enable();
                } else {
                    this.form.controls.sessionsLayout.controls.listMax.disable();
                }
            });

        let previousPricesValue: PricesDisplay = PricesDisplay.base;
        this.form.controls.taxDisplay.controls.type.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((selectedTax: TaxesDisplay) => {
                const pricesControl = this.form.controls.taxDisplay.controls.prices;
                if (selectedTax === TaxesDisplay.onTop) {
                    previousPricesValue = pricesControl.value && pricesControl.value !== PricesDisplay.net
                        ? pricesControl.value : previousPricesValue;
                    pricesControl.setValue(PricesDisplay.net);
                } else {
                    pricesControl.setValue(previousPricesValue);
                }
            });

        this.redirectionPolicyTypesValues.forEach(type => {
            this.form.get(['redirectionPolicies', type, 'mode']).valueChanges
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe((selectedMode: RedirectionPolicyMode) => {
                    if (selectedMode === RedirectionPolicyMode.goToUrl) {
                        this.form.get(['redirectionPolicies', type, 'value']).enable();
                    } else {
                        this.form.get(['redirectionPolicies', type, 'value']).disable();
                    }
                });
        });

        this.form.controls.interactiveVenue.controls.enabled.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((enabled: boolean) => {
                if (enabled) {
                    this.form.controls.interactiveVenue.controls.venueTypes.enable();
                    this.form.get('interactiveVenue.venueOptions.venueViewEnabled').enable();
                    this.form.get('interactiveVenue.venueOptions.venueSectorViewEnabled').enable();
                    this.form.get('interactiveVenue.venueOptions.venueSeatViewEnabled').enable();
                } else {
                    this.form.controls.interactiveVenue.controls.venueTypes.disable();
                    this.form.get('interactiveVenue.venueOptions.venueViewEnabled').disable();
                    this.form.get('interactiveVenue.venueOptions.venueSectorViewEnabled').disable();
                    this.form.get('interactiveVenue.venueOptions.venueSeatViewEnabled').disable();
                }
            });

        combineLatest([
            this.#channelsExtSrv.getPurchaseConfig$()
                .pipe(filter(purchaseConfig => !!purchaseConfig)),
            this.#entityService.getEntity$()
                .pipe(first(entity => !!entity)), // only used as a debouncer
            this.form.valueChanges // only used as a trigger
        ])
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([config]) => {
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.venueLayout, config.venue?.content_layout
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.interactiveVenue.controls.enabled, config.venue?.allow_interactive_venue
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.interactiveVenue.controls.venueTypes, config.venue?.interactive_venue_types
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('interactiveVenue.venueOptions.venueViewEnabled'), config.venue?.allow_venue_3d_view
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('interactiveVenue.venueOptions.venueSectorViewEnabled'), config.venue?.allow_sector_3d_view
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('interactiveVenue.venueOptions.venueSeatViewEnabled'), config.venue?.allow_seat_3d_view
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.sessionsLayout.controls.type, config.sessions?.visualization?.format
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.sessionsLayout.controls.listMax, config.sessions?.visualization?.max_listed
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.taxDisplay.controls.type,
                    [TaxesDisplay.included, TaxesDisplay.includedInfo].includes(config?.price_display?.taxes)
                        ? TaxesDisplay.included : TaxesDisplay.onTop
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.taxDisplay.controls.info, config?.price_display?.taxes === TaxesDisplay.includedInfo
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.taxDisplay.controls.prices, config?.price_display?.prices
                );
                config.redirection_policy?.forEach(policy => {
                    FormControlHandler.checkAndRefreshDirtyState(
                        this.form.get(['redirectionPolicies', policy.type, 'mode']), policy.mode
                    );
                    FormControlHandler.checkAndRefreshDirtyState(
                        this.form.get(['redirectionPolicies', policy.type, 'value']), policy.value
                    );
                });
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.otherSettings.controls.includeTaxesSeparately, config.include_taxes_separately
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.otherSettings.controls.persistPromoValidationCodes, config.sessions?.promotions?.code_persistence
                );
            });
    }

    #loadData(): void {
        this.#channelsExtSrv.loadPurchaseConfig(this.channel.id);
        this.form.markAsPristine();
    }

    #removeBlankAttributes(obj: Record<string, string>): Record<string, string> {
        const result = {};
        for (const key in obj) {
            if (obj[key] !== null && obj[key] !== undefined && obj[key] !== '') {
                result[key] = obj[key];
            }
        }
        return result;
    }

    #goToRedirectionPolicyInvalidTab(): void {
        let invalidMenu = false;
        for (let i = 0; i < this._tabsMenus()?.length && !invalidMenu; i++) {
            invalidMenu = this._tabsMenus().at(i)?.goToInvalidCtrlTab();
        }
    }
}
