import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    AdditionalCondition, BuyerRegistrationType, CancellationService, Channel, ChannelAuthVendorsSso, ChannelAuthVendorsUserData,
    ChannelCancellationServices, ChannelFieldsRestrictions, ChannelForms, ChannelFormsType, ChannelPurchaseConfig,
    ChannelsExtendedService, ChannelsService, ChannelType, channelWebTypes,
    ChannelWhitelabelSettings,
    CommercialConsentType, LoyaltyReceptionType,
    PutChannelCancellationServices, ChannelInvitationsSelectionMode as SelectionMode
} from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService, Entity } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge, maxDecimalLength } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { combineLatest, forkJoin, Observable, of, throwError } from 'rxjs';
import { filter, first, map, shareReplay, startWith, switchMap, take, tap, withLatestFrom } from 'rxjs/operators';
import { ChannelOperativeService } from '../channel-operative.service';
import { ChannelFormsColumnsSettings } from './channel-forms/channel-forms.component';

@Component({
    selector: 'app-channel-options',
    templateUrl: './channel-options.component.html',
    styleUrls: ['./channel-options.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelOptionsComponent implements OnInit, OnDestroy, WritingComponent {

    readonly #onDestroyRef = inject(DestroyRef);
    readonly #channelOperativeService = inject(ChannelOperativeService);
    readonly #channelsService = inject(ChannelsService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #authService = inject(AuthenticationService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #auth = inject(AuthenticationService);

    readonly #additionalConditionsForm =
        this.#fb.array<FormGroup<{ conditionRequired: FormControl<boolean>; conditionEnabled: FormControl<boolean> }>>([]);

    readonly #purchaseConfigForm = this.#fb.group({
        commercialConsentType: [{ value: null as CommercialConsentType, disabled: true }, Validators.required],
        buyerRegistrationType: [{ value: null as BuyerRegistrationType, disabled: true }, Validators.required],
        showAcceptAllOption: null as boolean,
        loyaltyProgram: this.#fb.group({
            sessionHours: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
            purchaseHours: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
            type: 'PURCHASE' as LoyaltyReceptionType
        })
    });

    readonly #vendorForm = this.#fb.group({
        userData: this.#fb.group({
            allowed: this.#fb.control<boolean>(null),
            mandatory_login: this.#fb.control<boolean>(null),
            editable_data: this.#fb.control<boolean>(null),
            vendors: [{ value: null }, Validators.required]
        }),
        sso: this.#fb.group({
            allowed: this.#fb.control<boolean>(null),
            vendors: [{ value: null }, Validators.required]
        })
    });

    readonly #cancellationServicesForm = this.#fb.group({
        providers: this.#fb.group({} as { [id: string]: boolean }),
        defaultSelectedPolicy: this.#fb.group({
            enabled: this.#fb.control<boolean>(false),
            policy: this.#fb.control<number | null>({ value: null, disabled: true }, Validators.required)
        })
    });

    readonly buyerDataForm = this.#fb.group({});
    readonly buyerDataProtectionDataForm = this.#fb.group({});

    readonly showBuyerRegistration$ = this.#channelsService.getChannel$()
        .pipe(
            filter(Boolean),
            map(channel => channel.type === ChannelType.web && !channel.settings.v4_config_enabled)
        );

    readonly limitsForm = this.#fb.group({
        purchaseMax: [{ value: null as number, disabled: true }, [
            Validators.required,
            Validators.min(1),
            Validators.max(ChannelFieldsRestrictions.channelPurchaseMaxLimit)
        ]],
        preselected_items: [{ value: null as number, disabled: true }, [
            Validators.required,
            Validators.max(ChannelFieldsRestrictions.channelPurchaseMaxLimit)
        ]],
        bookingMax: [{ value: null as number, disabled: true }, [
            Validators.required,
            Validators.min(1),
            Validators.max(ChannelFieldsRestrictions.channelBookingMaxLimit)
        ]],
        issueMax: [{ value: null as number, disabled: true }, [
            Validators.required,
            Validators.min(1),
            Validators.max(ChannelFieldsRestrictions.channelIssueMaxLimit)
        ]]
    });

    readonly invitationsForm = this.#fb.group({
        enabled: null as boolean,
        selection_mode: [{ value: null as SelectionMode.auto | 'MANUAL', disabled: true }, [Validators.required]],
        manual_mode: [{ value: null as SelectionMode.manualAll | SelectionMode.manualNone, disabled: true }, [Validators.required]]
    });

    readonly supportEmailForm = this.#fb.group({
        enabled: null as boolean,
        address: [{ value: null as string, disabled: true }, [Validators.required, Validators.email]]
    });

    readonly channelForm = this.#fb.group({
        limits: this.limitsForm,
        invitations: this.invitationsForm,
        supportEmail: this.supportEmailForm,
        allowAutomaticSeatSelection: [{ value: false, disabled: true }, Validators.required],
        enableB2B: [{ value: false, disabled: true }, Validators.required],
        enableDataProtection: [{ value: false, disabled: true }, Validators.required],
        enableLinkedCustomers: [{ value: false, disabled: true }, Validators.required],
        customerAssignation: this.#fb.group({
            enabled: [{ value: false, disabled: true }, Validators.required],
            mode: [{ value: 'OPTIONAL' as 'OPTIONAL' | 'REQUIRED', disabled: true }, Validators.required]
        }),
        resendTickets: [{ value: false, disabled: true }, Validators.required]
    });

    readonly form = this.#fb.group({
        channelForms: this.buyerDataForm,
        channelProtectionDataForms: this.buyerDataProtectionDataForm,
        additionalConditions: this.#additionalConditionsForm,
        purchaseConfig: this.#purchaseConfigForm,
        channel: this.channelForm,
        cancellationServices: this.#cancellationServicesForm,
        vendor: this.#vendorForm
    });

    readonly isOperatorMgr$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    readonly isOperatorOrCnlMgr$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.CNL_MGR]);
    readonly channel$ = this.#channelsService.getChannel$()
        .pipe(
            filter(Boolean),
            withLatestFrom(this.isOperatorMgr$),
            map(([channel, isOperatorMr]) => {
                if (channel.type === ChannelType.boxOffice) {
                    this.form.get('channel.limits.bookingMax').enable();
                    this.form.get('channel.limits.issueMax').enable();
                    this.form.get('channel.limits.purchaseMax').enable();
                    this.form.get('channel.customerAssignation.enabled').enable();
                    this.form.get('channel.customerAssignation.mode').enable();
                    if (channel.settings.allow_data_protection_fields) {
                        this.form.get('channel.enableDataProtection').enable();
                    }
                } else {
                    if (channelWebTypes.includes(channel.type)) {
                        this.form.get('channel.enableLinkedCustomers').enable();
                    }
                    if (channel.type !== ChannelType.members) {
                        if (isOperatorMr) {
                            this.form.get('channel.allowAutomaticSeatSelection').enable();
                        }
                        this.form.get('channel.limits.purchaseMax').enable();
                    }

                    this.#channelsExtSrv.loadPurchaseConfig(channel.id);
                    this.#loadChannelWhitelabelSettings(channel.id);

                    if (channel.type === ChannelType.web) {
                        this.form.get('purchaseConfig.buyerRegistrationType').enable();
                        this.form.get('channel.supportEmail').enable({ onlySelf: true });
                        this.form.get('channel.limits.preselected_items').enable();
                        this.form.get('channel.resendTickets').enable(); // <- añadido de la otra rama
                    }

                    if (channel.type === ChannelType.webB2B) {
                        this.form.get('channel.limits.preselected_items').enable();
                    }

                    if (channel.type === ChannelType.webB2B && (channel.settings?.v4_config_enabled || channel.settings?.v4_enabled)) {
                        this.form.get('channel.customerAssignation.enabled').enable();
                        this.form.get('channel.customerAssignation.mode').enable();
                    }
                    this.form.get('purchaseConfig.commercialConsentType').enable();
                }

                this.#updateChannelData(channel);
                return channel;
            }),
            shareReplay(1)
        );

    readonly $channel = toSignal(this.channel$);
    readonly $resendTicketsEnabled = signal(false);

    readonly additionalConditions$ = this.channel$
        .pipe(
            first(channel => channel.type !== ChannelType.boxOffice),
            switchMap(channel => {
                this.#loadAdditionalConditions(channel.id);
                return this.#channelOperativeService.getAdditionalConditions$();
            }),
            filter(Boolean),
            tap(additionalConditions => this.#updateAdditionalConditionsData(additionalConditions)),
            startWith(null as AdditionalCondition[]),
            shareReplay(1)
        );

    readonly channelForms$ = this.channel$
        .pipe(
            first(),
            switchMap(channel => {
                this.#loadChannelForms(channel.id);
                return this.#channelOperativeService.getChannelForms$();
            }),
            filter(channelForms => channelForms !== null),
            shareReplay(1)
        );

    readonly isDataProtectionModuleAllowedByEntity$ = this.channel$
        .pipe(
            first(channel => channel.type === ChannelType.boxOffice),
            withLatestFrom(this.isOperatorOrCnlMgr$),
            switchMap(([_, isOperator]) => {
                if (isOperator) {
                    return this.#entitiesService.getEntity$();
                }
                return of<Entity>(null);
            }),
            first(Boolean),
            map(entity => entity.settings?.allow_data_protection_fields),
            startWith(false),
            tap(allowed => allowed && this.form.get('channel.enableDataProtection').enable()),
            shareReplay(1)
        );

    readonly isInvitationsEnabled$ = this.channel$
        .pipe(
            first(channel => channel?.type === ChannelType.webB2B),
            switchMap(() => this.#entitiesService.getEntity$()),
            first(Boolean),
            map(entity => entity.settings?.allow_invitations),
            startWith(false),
            tap(allowed => allowed && this.invitationsForm.get('enabled').enable()),
            shareReplay(1)
        );

    readonly isSupportEmailEnabled$ = this.channel$
        .pipe(
            first(channel => (channel?.type === ChannelType.web || channel?.type === ChannelType.members)),
            tap(() => this.invitationsForm.get('enabled').enable()),
            shareReplay(1)
        );

    readonly channelProtectionDataForms$ = this.channel$
        .pipe(
            first(channel => channel?.type === ChannelType.boxOffice && channel?.settings?.allow_data_protection_fields),
            switchMap(channel => {
                this.#loadBuyerDataProtectionDataForms(channel.id);
                return this.#channelOperativeService.getBuyerDataProtectionDataForms$();
            }),
            filter(channelProtectionDataForms => channelProtectionDataForms !== null),
            shareReplay(1)
        );

    readonly cancellationServices$ = this.channel$
        .pipe(
            filter(channel => (channel?.type === ChannelType.web || channel?.type === ChannelType.webB2B)),
            switchMap(channel => combineLatest([
                of(channel),
                this.#authService.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS]),
                this.#authService.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR])
            ])),
            filter(([, canRead]) => canRead),
            switchMap(([, canRead, canWrite]) =>
                combineLatest([
                    this.#channelsExtSrv.getCancellationServices$(),
                    this.#channelsExtSrv.isCancellationServicesLoading$(),
                    of(canRead), of(canWrite)
                ])
            ),
            filter(([channelCancellationServices, loading]) =>
                channelCancellationServices !== null && !loading
            ),
            tap(([channelCancellationServices, , , canWrite]) => {
                const allProviders = channelCancellationServices.providers || [];
                this.#initCancellationServicesForm(allProviders);
                this.#updateCancellationServicesForm({ providers: allProviders });
                this.#toggleStatusCancellationServicesForm(canWrite);
            }),
            map(([channelCancellationServices]) => channelCancellationServices.providers || [])
        );

    readonly $defaultSelectedCancellationServices = toSignal(
        this.#cancellationServicesForm.controls.providers.valueChanges.pipe(
            startWith(this.#cancellationServicesForm.controls.providers.value),
            withLatestFrom(this.cancellationServices$),
            map(([selectedProviders, providers]) => {
                const selectedIds = Object.entries(selectedProviders || {})
                    .filter(([_, isSelected]) => isSelected)
                    .map(([id, _]) => parseInt(id));

                return (providers || []).filter(p =>
                    selectedIds.includes(p.id) && p.default_allowed
                );
            })
        ),
        { initialValue: [] }
    );

    readonly $hasAnyDefaultSelected = computed(() => this.$defaultSelectedCancellationServices().length > 0);

    readonly isB2bAndB2bAllowedByEntity$ = this.channel$
        .pipe(
            first(channel => channel.type === ChannelType.webB2B),
            switchMap(() => this.#entitiesService.getEntity$()),
            first(Boolean),
            map(entity => entity.settings?.enable_B2B),
            startWith(false),
            shareReplay(1)
        );

    readonly isBoxOfficeAndB2bAllowedByEntity$ = this.channel$
        .pipe(
            first(channel => channel.type === ChannelType.boxOffice),
            switchMap(() => this.#entitiesService.getEntity$()),
            first(Boolean),
            map(entity => entity.settings?.enable_B2B),
            startWith(false),
            tap(isB2bModuleAllowed => isB2bModuleAllowed && this.form.get('channel.enableB2B').enable()),
            shareReplay(1)
        );

    readonly entityAllowMembers$ = this.#entitiesService.getEntity$()
        .pipe(
            first(Boolean),
            map(entity => entity.settings?.allow_members),
            startWith(false),
            shareReplay(1)
        );

    readonly entityVendors$ = this.channel$
        .pipe(
            first(channel => channel.type === ChannelType.web ||
                channel.type === ChannelType.webBoxOffice),
            withLatestFrom(this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.CNL_MGR])),
            filter(([, permission]) => !!permission),
            switchMap(([channel]) => combineLatest([of(channel), this.#entitiesService.getEntity$()])),
            first(([channel, entity]) => !!entity && entity.id === channel.entity.id &&
                entity.settings?.external_integration?.auth_vendor?.enabled),
            switchMap(([channel, entity]) => {
                this.#channelOperativeService.loadAuthVendorUserData(channel.id);
                this.#channelOperativeService.loadAuthVendorSso(channel.id);
                return combineLatest([
                    this.#channelOperativeService.getAuthVendorUserData$(),
                    this.#channelOperativeService.getAuthVendorSso$(),
                    of(entity)
                ]).pipe(
                    tap(([vendorUserData, vendorSSO]) => this.#initAuthVendorForms(vendorUserData, vendorSSO))
                );
            }),
            map(([, , entity]) => entity.settings?.external_integration.auth_vendor.vendor_id)
        );

    readonly isInProgress$ = booleanOrMerge([
        this.#channelsService.isChannelInProgress$(),
        this.#channelOperativeService.isChannelFormsInProgress$(),
        this.#channelOperativeService.isBuyerDataProtectionDataFormsInProgress$(),
        this.#channelOperativeService.isAdditionalConditionsInProgress$(),
        this.#channelsExtSrv.isPurchaseConfigLoading$(),
        this.#channelsExtSrv.isCancellationServicesLoading$(),
        this.#channelOperativeService.isAuthVendorSsoLoading$(),
        this.#channelOperativeService.isAuthVendorUserDataLoading$(),
        this.#entitiesService.isEntityLoading$(),
        this.#channelsService.channelWhitelabelSettings.loading$()
    ]);

    readonly isHandsetOrTablet$ = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly $showLoyaltyPointsSettings = toSignal(
        this.#entitiesService.getEntity$().pipe(first(Boolean), map(entity => entity.settings?.allow_loyalty_points))
    );

    readonly $hideVisibleColumn = computed(() => {
        const { type: channelType, url } = this.$channel() || {};
        return channelType === ChannelType.boxOffice && !url;
    });

    readonly $channelFormsColumnsSettings = computed<ChannelFormsColumnsSettings>(() => ({
        visible: {
            hideColumn: this.$hideVisibleColumn(),
            disabled: false
        },
        uneditable: {
            hideColumn: false,
            disabled: this.$channel()?.type !== ChannelType.members
        },
        mandatory: {
            hideColumn: false,
            disabled: false
        }
    }));

    readonly channelTypes = ChannelType;
    readonly invitationMode = SelectionMode;
    readonly commercialConsentTypes = CommercialConsentType;
    readonly buyerRegistrationTypes = BuyerRegistrationType;

    constructor() {
        this.#handleCancellationServicesFormChanges();
    }

    ngOnInit(): void {
        this.#initForm();

        this.#channelsService.getChannel$()
            .pipe(
                filter(Boolean),
                take(1),
                takeUntilDestroyed(this.#onDestroyRef)
            )
            .subscribe(channel => {
                if (channel.type === ChannelType.web || channel.type === ChannelType.webB2B) {
                    this.#channelsExtSrv.loadCancellationServices(channel.id);
                }
            });

        this.#channelsExtSrv.getPurchaseConfig$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(config => this.#updatePurchaseConfig(config));

        this.#channelsService.channelWhitelabelSettings.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(settings => this.#updateWhitelabelSettings(settings));

        this.form.get('purchaseConfig.loyaltyProgram.type').valueChanges
            .pipe(filter(() => this.$showLoyaltyPointsSettings()), takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(type => {
                const purchaseHours = this.form.get('purchaseConfig.loyaltyProgram.purchaseHours');
                const sessionHours = this.form.get('purchaseConfig.loyaltyProgram.sessionHours');
                if (type === 'SESSION_START') {
                    purchaseHours.disable();
                    sessionHours.enable();
                } else if (type === 'PURCHASE_START') {
                    purchaseHours.enable();
                    sessionHours.disable();
                } else {
                    purchaseHours.disable();
                    sessionHours.disable();
                }
            });

        this.limitsForm.get('purchaseMax').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(purchaseMaxValue => {
                const preselectedItemsControl = this.limitsForm.get('preselected_items');
                if (purchaseMaxValue && purchaseMaxValue > 0) {
                    preselectedItemsControl.setValidators([
                        Validators.required,
                        Validators.max(purchaseMaxValue)
                    ]);
                } else {
                    preselectedItemsControl.setValidators([
                        Validators.required,
                        Validators.max(ChannelFieldsRestrictions.channelPurchaseMaxLimit)
                    ]);
                }
                preselectedItemsControl.updateValueAndValidity();
            });

        this.#handleCustomerAssignationChange();

    }

    ngOnDestroy(): void {
        this.#channelOperativeService.clearChannelForms$();
        this.#channelOperativeService.clearBuyerDataProtectionDataForms$();
        this.#channelOperativeService.clearAdditionalConditions$();
        this.#channelsExtSrv.clearCancellationServices();
        this.#channelsService.channelWhitelabelSettings.clear();
    }

    save$(): Observable<unknown> {
        if (this.form.valid && this.form.dirty) {

            const obs$: Observable<void>[] = [of(null)];
            const channel = this.$channel();

            if (this.buyerDataForm.dirty) {
                obs$.push(this.#saveBuyerData$(channel));
            }
            if (this.buyerDataProtectionDataForm.dirty) {
                obs$.push(this.#saveBuyerProtectionData$(channel));
            }
            if (this.#additionalConditionsForm.dirty) {
                obs$.push(this.#saveAdditionalConditions$(channel));
            }
            if (this.channelForm.dirty) {
                obs$.push(this.#saveChannel$(channel));
            }
            if (this.#purchaseConfigForm.dirty) {
                obs$.push(this.#savePurchaseConfig$(channel));
            }
            if (this.#cancellationServicesForm.dirty) {
                obs$.push(this.#saveCancellationServices$(channel));
            }
            if (this.#vendorForm.get('userData').dirty) {
                obs$.push(this.#saveVendorUserData$(channel));
            }
            if (this.#vendorForm.get('sso').dirty) {
                obs$.push(this.#saveVendorSso$(channel));
            }
            if (this.limitsForm.controls.preselected_items.dirty || this.channelForm.controls.resendTickets.dirty) {
                obs$.push(this.#saveChannelWhitelabelSettings$(channel));
            }

            return forkJoin(obs$).pipe(
                tap(() => this.#ephemeralMessageService.showSuccess({
                    msgKey: 'CHANNELS.UPDATE_SUCCESS',
                    msgParams: { channelName: channel.name }
                }))
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'Invalid channel options form');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    cancel(): void {
        this.channel$.pipe(first()).subscribe(channel => {
            this.#loadAllData(channel.id, channel.type);
        });
    }

    #initForm(): void {
        this.form.get('vendor').disable();

        combineLatest([
            this.invitationsForm.controls.enabled.valueChanges.pipe(startWith(this.invitationsForm.controls.enabled.value)),
            this.invitationsForm.controls.selection_mode.valueChanges.pipe(startWith(this.invitationsForm.controls.selection_mode.value))
        ])
            .pipe(
                takeUntilDestroyed(this.#onDestroyRef)
            ).subscribe(([enabled, mode]) => {
                const opts = { emitEvent: false, onlySelf: true };
                const selectionModeForm = this.invitationsForm.controls.selection_mode;
                const manualModeForm = this.invitationsForm.controls.manual_mode;
                enabled ? selectionModeForm.enable(opts) : selectionModeForm.disable(opts);
                enabled && mode === 'MANUAL' ? manualModeForm.enable(opts) : manualModeForm.disable(opts);
            });

        this.supportEmailForm.controls.enabled.valueChanges
            .pipe(
                startWith(this.supportEmailForm.controls.enabled.value),
                takeUntilDestroyed(this.#onDestroyRef)
            ).subscribe(enabled => enabled ?
                this.supportEmailForm.controls.address.enable({ emitEvent: false }) :
                this.supportEmailForm.controls.address.disable({ emitEvent: false }));
    }

    #initAuthVendorForms(vendorUserData: ChannelAuthVendorsUserData, vendorSSO: ChannelAuthVendorsSso): void {
        const vendorForm = this.#vendorForm;
        vendorForm.enable();

        vendorForm.get('userData').reset({ ...vendorUserData, vendors: vendorUserData?.vendors?.[0] });
        vendorForm.get('sso').reset({ ...vendorSSO, vendors: vendorSSO?.vendors?.[0] });

        vendorForm.get('userData.allowed').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(enabled => enabled ? vendorForm.get('userData.vendors').enable() : vendorForm.get('userData.vendors').disable());

        vendorForm.get('sso.allowed').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(enabled => enabled ? vendorForm.get('sso.vendors').enable() : vendorForm.get('sso.vendors').disable());
    }

    #initCancellationServicesForm(cancellationServices: CancellationService[]): void {
        const cancellationSrvForm = this.form.get('cancellationServices.providers') as FormGroup<{ [id: string]: FormControl<boolean> }>;
        cancellationServices.forEach(elem => {
            const key = String(elem.id);
            if (!cancellationSrvForm.get(key)) cancellationSrvForm.addControl(key, new FormControl(false));
            else cancellationSrvForm.get(key).reset(false);
        });
    }

    #updateCancellationServicesForm(cancellationSevices: ChannelCancellationServices): void {
        const cancelServsForm = this.form.get('cancellationServices.providers');
        cancellationSevices.providers?.forEach(provider => {
            const ctrl = cancelServsForm.get(String(provider.id));
            if (ctrl) ctrl.reset(!!provider.enabled, { emitEvent: false });
        });

        const defaultProvider = cancellationSevices.providers?.find(p => p.default_selected);
        this.#cancellationServicesForm.patchValue({
            defaultSelectedPolicy: {
                enabled: !!defaultProvider,
                policy: defaultProvider ? defaultProvider.id : null
            }
        }, { emitEvent: false });

        cancelServsForm.markAsPristine();
        this.#cancellationServicesForm.controls.defaultSelectedPolicy.markAsPristine();
    }

    #toggleStatusCancellationServicesForm(enable: boolean): void {
        const cancelServsForm = this.form.get('cancellationServices.providers');
        enable ? cancelServsForm.enable() : cancelServsForm.disable();
    }

    #updateChannelData(channel: Channel): void {
        const invitations = channel.settings?.invitations;
        this.channelForm.reset({
            limits: {
                purchaseMax: channel.limits?.tickets.purchase_max,
                bookingMax: channel.limits?.tickets.booking_max,
                issueMax: channel.limits?.tickets.issue_max
            },
            ...(invitations && {
                invitations: {
                    enabled: invitations.enabled,
                    selection_mode: invitations.selection_mode ?
                        (invitations.selection_mode !== SelectionMode.auto ? 'MANUAL' : invitations.selection_mode) : null,
                    manual_mode: invitations.selection_mode ?
                        (invitations.selection_mode !== SelectionMode.auto && invitations.selection_mode || null) : null
                }
            }),
            allowAutomaticSeatSelection: channel.settings.automatic_seat_selection,
            enableB2B: channel.settings.enable_b2b ?? false,
            enableDataProtection: channel.settings.allow_data_protection_fields ?? false,
            enableLinkedCustomers: channel.settings.allow_linked_customers ?? false,
            customerAssignation: {
                enabled: channel.settings.customer_assignation?.enabled ?? false,
                mode: channel.settings.customer_assignation?.mode ?? 'OPTIONAL'
            },
            supportEmail: {
                enabled: channel.settings.support_email?.enabled ?? false,
                address: channel.settings.support_email?.address ?? null
            }
        });
    }

    #updatePurchaseConfig(config: ChannelPurchaseConfig): void {
        this.#purchaseConfigForm.patchValue({
            commercialConsentType: config.commercial_information_consent,
            buyerRegistrationType: config.buyer_registration,
            showAcceptAllOption: config.show_accept_all_option,
            loyaltyProgram: {
                type: config.loyalty_program?.reception?.type === 'PURCHASE' &&
                    config.loyalty_program?.reception?.hours !== 0
                    ? 'PURCHASE_START' : config.loyalty_program?.reception?.type || 'PURCHASE',
                purchaseHours: config.loyalty_program?.reception?.type === 'PURCHASE' &&
                    config.loyalty_program?.reception?.hours !== 0 ? config.loyalty_program?.reception?.hours : 1,
                sessionHours: config.loyalty_program?.reception?.type === 'SESSION_START'
                    ? config.loyalty_program?.reception?.hours : 1
            }
        });

        if (this.#purchaseConfigForm.get('loyaltyProgram.type').value === 'SESSION_START') {
            this.#purchaseConfigForm.get('loyaltyProgram.sessionHours').enable();
        } else if (this.#purchaseConfigForm.get('loyaltyProgram.type').value === 'PURCHASE_START') {
            this.#purchaseConfigForm.get('loyaltyProgram.purchaseHours').enable();
        }

        this.#purchaseConfigForm.markAsPristine();
    }

    #updateWhitelabelSettings(settings: ChannelWhitelabelSettings): void {
        const preselectedItems = settings.venue_map?.preselected_items || 0;
        const preselectedCtrl = this.limitsForm.controls.preselected_items;
        const resendTickets = settings.resend_tickets?.enabled || false;
        const resendCtrl = this.channelForm.controls.resendTickets;
        this.$resendTicketsEnabled.set(!!settings.resend_tickets?.enabled);

        if (preselectedCtrl && !preselectedCtrl.dirty) {
            preselectedCtrl.patchValue(preselectedItems);
        }
        if (resendCtrl) {
            resendCtrl.patchValue(resendTickets);
        }
    }

    #updateAdditionalConditionsData(additionalConditions: AdditionalCondition[]): void {
        this.#additionalConditionsForm.clear();
        additionalConditions?.sort((a, b) => a.position - b.position).forEach(cond => {
            const group = this.#fb.group({ conditionEnabled: cond.enabled, conditionRequired: cond.mandatory });
            this.#additionalConditionsForm.push(group);
        });

        this.#additionalConditionsForm.markAsPristine();
    }

    #getBuyerDataFormValues(channelForms: ChannelForms, channelFormsValue: ChannelForms): ChannelForms {
        const hideVisibleColumn = this.$hideVisibleColumn();
        Object.keys(channelFormsValue).forEach(formType => {
            channelFormsValue[formType] = channelFormsValue[formType]
                .filter(formFieldValue => formType === ChannelFormsType.member ||
                    channelForms[formType].find(formField => formField.key === formFieldValue.key)?.mutable)
                .map(formFieldValue => {
                    if (hideVisibleColumn) {
                        delete formFieldValue.visible;
                    }
                    return formFieldValue;
                });
        });
        return channelFormsValue;
    }

    #getChannelDataFormValues(channel: Channel): Channel {
        const channelValue = this.channelForm.value;

        const result: Channel = {
            id: channel.id,
            limits: {
                tickets: {
                    purchase_max: channelValue.limits?.purchaseMax,
                    booking_max: channelValue.limits?.bookingMax,
                    issue_max: channelValue.limits?.issueMax
                }
            }
        };

        if (this.channelForm.controls.invitations.dirty) {
            const invitations = this.invitationsForm.value;
            result.settings = result.settings || {};
            result.settings.invitations = {
                enabled: invitations.enabled,
                selection_mode: invitations.selection_mode === 'MANUAL' ?
                    invitations.manual_mode : invitations.selection_mode as SelectionMode
            };
        }

        if (channelValue.allowAutomaticSeatSelection != null ||
            channelValue.enableB2B != null ||
            channelValue.enableDataProtection != null ||
            channelValue.enableLinkedCustomers != null ||
            channelValue.customerAssignation.enabled != null
        ) {
            result.settings = result.settings || {};
            result.settings.automatic_seat_selection = channelValue.allowAutomaticSeatSelection;
            result.settings.enable_b2b = channelValue.enableB2B;
            result.settings.allow_data_protection_fields = channelValue.enableDataProtection;
            result.settings.allow_linked_customers = channelValue.enableLinkedCustomers;
            if ((channel.type === ChannelType.webB2B && (channel.settings?.v4_config_enabled || channel.settings?.v4_enabled)
                || channel.type === ChannelType.boxOffice)) {
                result.settings.customer_assignation = channelValue.customerAssignation;
            }
        }

        if (this.channelForm.controls.supportEmail.dirty) {
            const supportEmailData = this.supportEmailForm.value;
            result.settings = result.settings || {};
            result.settings.support_email = {
                enabled: supportEmailData.enabled,
                address: supportEmailData.address
            };
        }

        return result;
    }

    #loadChannel(channelId: number): void {
        this.#channelsService.loadChannel(channelId.toString());
    }

    #loadChannelForms(channelId: number): void {
        this.#channelOperativeService.loadChannelForms(channelId);
    }

    #loadBuyerDataProtectionDataForms(channelId: number): void {
        this.channelForm.get('enableDataProtection').enabled && this.#channelOperativeService.loadBuyerDataProtectionDataForms(channelId);
    }

    #loadAdditionalConditions(channelId: number): void {
        this.#channelOperativeService.loadAdditionalConditions(channelId);
    }

    #loadAuthVendors(channelId: number): void {
        if (this.#vendorForm.enabled) {
            this.#channelOperativeService.loadAuthVendorSso(channelId);
            this.#channelOperativeService.loadAuthVendorUserData(channelId);
        }
    }

    #loadPurchaseConfig(channelId: number): void {
        this.#channelsExtSrv.loadPurchaseConfig(channelId);
    }

    #loadChannelWhitelabelSettings(channelId: number): void {
        this.#channelsService.channelWhitelabelSettings.load(channelId);
    }

    #loadAllData(channelId: number, channelType: ChannelType): void {
        this.#loadChannel(channelId);
        this.#loadChannelForms(channelId);
        this.#loadBuyerDataProtectionDataForms(channelId);
        if (channelType !== ChannelType.boxOffice) {
            this.#loadAdditionalConditions(channelId);
        }
        this.#loadAuthVendors(channelId);
        this.#loadChannelWhitelabelSettings(channelId);
    }

    #saveBuyerData$(channel: Channel): Observable<void> {
        return this.channelForms$.pipe(
            first(),
            switchMap(channelForms => {
                const value = this.buyerDataForm.getRawValue();
                const buyerData = this.#getBuyerDataFormValues(channelForms, value);
                return this.#channelOperativeService.updateChannelForms(channel.id, buyerData);
            }),
            tap(() => this.#loadChannelForms(channel.id))
        );
    }

    #saveBuyerProtectionData$(channel: Channel): Observable<void> {
        return this.channelProtectionDataForms$.pipe(
            first(),
            switchMap(channelForms => {
                const value = this.buyerDataProtectionDataForm.getRawValue();
                const buyerData = this.#getBuyerDataFormValues(channelForms, value);
                return this.#channelOperativeService.updateBuyerDataProtectionDataForms(channel.id, buyerData);
            }),
            tap(() => this.#loadBuyerDataProtectionDataForms(channel.id))
        );
    }

    #saveAdditionalConditions$(channel: Channel): Observable<void> {
        return this.additionalConditions$.pipe(
            first(),
            switchMap(additionalConditions => {
                if (additionalConditions) {
                    const additionalConditionsData = additionalConditions.map((condition, index) => ({
                        id: condition.id,
                        position: index,
                        mandatory: this.#additionalConditionsForm.at(index).value.conditionRequired,
                        enabled: this.#additionalConditionsForm.at(index).value.conditionEnabled
                    }));
                    return this.#channelOperativeService.updateAdditionalConditions(channel.id, additionalConditionsData).pipe(
                        tap(() => this.#loadAdditionalConditions(channel.id))
                    );
                } else {
                    return of(null);
                }
            }));
    }

    #saveChannel$(channel: Channel): Observable<void> {
        const channelData = this.#getChannelDataFormValues(channel);
        return this.#channelsService.saveChannel(channelData.id, channelData).pipe(
            tap(() => this.#loadChannel(channel.id))
        );
    }

    #savePurchaseConfig$(channel: Channel): Observable<void> {
        const form = this.#purchaseConfigForm;
        const payload: ChannelPurchaseConfig = {
            buyer_registration: form.get('buyerRegistrationType').value,
            commercial_information_consent: form.get('commercialConsentType').value,
            show_accept_all_option: form.get('showAcceptAllOption').value
        };

        if (this.$showLoyaltyPointsSettings()) {
            const loyaltyProgram = form.get('loyaltyProgram').value;
            const receptionType = loyaltyProgram.type === 'PURCHASE_START'
                ? 'PURCHASE' : loyaltyProgram.type;
            let receptionHours = 0;
            if (loyaltyProgram.type === 'PURCHASE_START') {
                receptionHours = loyaltyProgram.purchaseHours;
            } else if (loyaltyProgram.type === 'SESSION_START') {
                receptionHours = loyaltyProgram.sessionHours;
            }
            payload.loyalty_program = {
                reception: {
                    type: receptionType,
                    hours: receptionHours
                }
            };
        }

        return this.#channelsExtSrv.updatePurchaseConfig(channel.id, payload)
            .pipe(tap(() => {
                if (this.channelForm.pristine) {
                    this.#loadPurchaseConfig(channel.id);
                }
            }));
    }

    #saveCancellationServices$(channel: Channel): Observable<void> {
        const providersGroup = this.#cancellationServicesForm.controls.providers;
        const allIds = Object.keys(providersGroup.controls).map(id => Number(id));
        const enabledMap = this.#cancellationServicesForm.getRawValue().providers as Record<string, boolean | null>;
        const defaultPolicyEnabled = !!this.#cancellationServicesForm.getRawValue().defaultSelectedPolicy?.enabled;
        const selectedDefaultPolicyId = this.#cancellationServicesForm.getRawValue().defaultSelectedPolicy?.policy;
        const providers = allIds.map(id => {
            const enabled = !!enabledMap?.[String(id)];
            return {
                id,
                enabled,
                default_selected: defaultPolicyEnabled && enabled && selectedDefaultPolicyId === id
            };
        });
        const payload: PutChannelCancellationServices = { providers };
        return this.#channelsExtSrv.updateCancellationServices(channel.id, payload).pipe(
            tap(() => {
                this.#channelsExtSrv.loadCancellationServices(channel.id);
                this.#cancellationServicesForm.markAsPristine();
            })
        );

    }

    #saveVendorUserData$(channel: Channel): Observable<void> {
        const value = this.#vendorForm.get('userData').value;
        const payload = { ...value, vendors: value.vendors && [value.vendors] || [] };

        return this.#channelOperativeService.updateAuthVendorUserData(channel.id, payload).pipe(
            tap(() => this.#channelOperativeService.loadAuthVendorUserData(channel.id))
        );
    }

    #saveVendorSso$(channel: Channel): Observable<void> {
        const value = this.#vendorForm.get('sso').value;
        const payload = { ...value, vendors: value.vendors && [value.vendors] || [] };

        return this.#channelOperativeService.updateAuthVendorSso(channel.id, payload)
            .pipe(tap(() => this.#channelOperativeService.loadAuthVendorSso(channel.id)));
    }

    #saveChannelWhitelabelSettings$(channel: Channel): Observable<void> {
        const preselectedItems = this.limitsForm.controls.preselected_items.value;
        const resendTicketsEnabled = this.channelForm.controls.resendTickets.value;
        return this.#channelsService.channelWhitelabelSettings.get$().pipe(
            first(Boolean),
            switchMap(currentSettings => {
                const payload = {
                    venue_map: {
                        ...currentSettings.venue_map,
                        preselected_items: preselectedItems
                    },
                    resend_tickets: {
                        enabled: resendTicketsEnabled
                    }
                };

                return this.#channelsService.channelWhitelabelSettings.update(channel.id, payload);
            })
        );
    }

    #handleCustomerAssignationChange(): void {
        this.channelForm.controls.customerAssignation.controls.enabled.valueChanges.pipe(startWith(null)).pipe(
            takeUntilDestroyed(this.#onDestroyRef)
        ).subscribe(showAssignation => {
            if (!!showAssignation) {
                this.channelForm.get('customerAssignation.mode').enable();
            } else {
                this.channelForm.get('customerAssignation.mode').disable();
            }
        });
    }

    #handleCancellationServicesFormChanges(): void {
        const enabledCtrl = this.#cancellationServicesForm.controls.defaultSelectedPolicy.controls.enabled;
        const policyCtrl = this.#cancellationServicesForm.controls.defaultSelectedPolicy.controls.policy;
        const hasAnyDefaultSelected$ = toObservable(this.$hasAnyDefaultSelected);
        const providersGroup = this.#cancellationServicesForm.controls.providers;

        providersGroup.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(map => {
                if (!enabledCtrl.value) return;
                if (policyCtrl.value == null) return;
                if (!map || map[String(policyCtrl.value)]) return;
                policyCtrl.setValue(null);
            });

        hasAnyDefaultSelected$
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(hasAny => {
                if (!hasAny) {
                    enabledCtrl.disable({ emitEvent: false });
                    policyCtrl.disable({ emitEvent: false });
                    return;
                }

                enabledCtrl.enable({ emitEvent: false });
                if (enabledCtrl.value) {
                    policyCtrl.enable({ emitEvent: false });
                } else {
                    policyCtrl.disable({ emitEvent: false });
                }
            });

        enabledCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(isOn => {
                if (isOn) {
                    policyCtrl.enable({ emitEvent: true });
                } else {
                    policyCtrl.disable({ emitEvent: false });
                }
            });
    }

}

