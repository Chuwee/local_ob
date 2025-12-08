import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { CollectivesService } from '@admin-clients/cpanel/collectives/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { IsItalianEntityPipe } from '@admin-clients/cpanel/organizations/entities/utils';
import {
    ENTITY_SERVICE, LOGIN_CONFIG_SERVICE
} from '@admin-clients/cpanel/shared/data-access';
import {
    LoginAuthConfig, LoginAuthMethod, LoginPlacement, PutEntity, vendorsWithProviders
} from '@admin-clients/shared/common/data-access';
import {
    CopyTextComponent,
    DialogSize, EphemeralMessageService, MessageDialogConfig, MessageDialogService, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import {
    atLeastOneRequiredInArray, atLeastOneRequiredInFormGroup, booleanOrMerge, maxDecimalLength
} from '@admin-clients/shared/utility/utils';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, forkJoin, Observable, of, throwError } from 'rxjs';
import { delay, filter, first, map, switchMap, take, tap, withLatestFrom } from 'rxjs/operators';
import { CollectiveLoginComponent } from './collective-login/collective-login.component';
import { CustomerLoginComponent } from './customer-login/customer-login.component';
import { VendorLoginComponent } from './vendor-login/vendor-login.component';

const unsavedChangesDialogData: MessageDialogConfig = {
    actionLabel: 'FORMS.ACTIONS.UPDATE',
    showCancelButton: true,
    message: 'LOGIN_CONFIG.STATUS_CHANGE_WARNING.DESCRIPTION',
    title: 'LOGIN_CONFIG.STATUS_CHANGE_WARNING.TITLE',
    size: DialogSize.MEDIUM
};

type ExtendedLoginAuthMethod = LoginAuthMethod['type'] | 'VENDOR_WITH_CUSTOMER';

@Component({
    selector: 'app-login-config',
    templateUrl: './login-config.component.html',
    styleUrls: ['./login-config.component.scss'],
    imports: [
        FormContainerComponent, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent, MatRadioGroup,
        MatRadioButton, MatIcon, MatTooltip, RouterLink, SearchablePaginatedSelectionModule, MatSlideToggle,
        MatFormField, MatLabel, MatInput, MatError, MatProgressSpinner, CollectiveLoginComponent, CustomerLoginComponent,
        VendorLoginComponent, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, CopyTextComponent,
        MatButtonModule, MatCheckbox, IsItalianEntityPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginConfigComponent implements OnDestroy {
    readonly #loginConfSrv = inject(LOGIN_CONFIG_SERVICE);
    readonly #entitySrv = inject(ENTITY_SERVICE);
    readonly #entitiesService = inject(EntitiesService);
    readonly #auth = inject(AuthenticationService);
    readonly #collectiveSrv = inject(CollectivesService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #route = inject(ActivatedRoute);
    readonly #channelsService = inject(ChannelsService);
    readonly #ephemeral = inject(EphemeralMessageService);
    readonly isChannel = this.#route.snapshot.data['isChannel'];
    readonly $customersLink = signal('');
    readonly #isItalianEntityPipe = inject(IsItalianEntityPipe);

    #channelId?: number;
    entityId: number;
    hasAuthVendors = false;

    // Más adelante, se eliminará este flag específico de Italia y vendrá flageado de manera que el front
    // no necesite saber de qué provider externo se trata.
    readonly $entity = toSignal(this.#entitySrv.getEntity$().pipe(filter(Boolean)));
    readonly $customerTypes = toSignal(this.#entitiesService.entityCustomerTypes.get$());
    readonly customerLoginForm = this.#fb.group({
        allowGuest: false,
        disableCreateAccount: false,
        socialLogin: this.#fb.group({
            enabled: false,
            vendors: this.#fb.group({} as Record<string, FormControl<boolean>>, { validators: atLeastOneRequiredInFormGroup() })
        }),
        blocked_customer_types_enabled: false,
        blocked_customer_types: this.#fb.control<string[]>({ value: [], disabled: true }, [Validators.required]),
        placement: this.#fb.control<LoginPlacement>(null, Validators.required),
        counter: this.#fb.group({
            mode: this.#fb.control<'DEFAULT' | 'AUTOINCREMENT'>('DEFAULT', Validators.required),
            id: this.#fb.control<number>({ value: null, disabled: true }, [Validators.required, Validators.min(1), maxDecimalLength(0)])
        })
    });

    readonly vendorLoginForm = this.#fb.group({
        vendor: this.#fb.control<string>(null, Validators.required),
        providersSettings: this.#fb.group({
            loginProvider: this.#fb.control<string[]>(null),
            signupSource: this.#fb.control(null),
            legalTerms: this.#fb.control<string[]>(null)
        }),
        allowEditUserData: false,
        allowGuest: false,
        placement: this.#fb.control<LoginPlacement>(null, Validators.required)
    });

    readonly vendorWithCustomerLoginForm = this.#fb.group({
        vendor: this.#fb.control<string>(null, Validators.required),
        providersSettings: this.#fb.group({
            loginProvider: this.#fb.control<string[]>(null),
            signupSource: this.#fb.control(null),
            legalTerms: this.#fb.control<string[]>(null)
        }),
        phoneValidator: this.#fb.group({
            enabled: false,
            validator_id: this.#fb.control<string>(null)
        }),
        allowEditUserData: false,
        allowGuest: false,
        blocked_customer_types_enabled: false,
        blocked_customer_types: this.#fb.control<string[]>([]),
        placement: this.#fb.control<LoginPlacement>(null, Validators.required),
        disableCreateAccount: false,
        counter: this.#fb.group({
            mode: this.#fb.control<'DEFAULT' | 'AUTOINCREMENT'>('DEFAULT', Validators.required),
            id: this.#fb.control<number>(null, [Validators.required, Validators.min(1), maxDecimalLength(0)])
        })
    });

    readonly queuesForm = this.#fb.group({
        active: true,
        alias: this.#fb.control<string>(null, Validators.required)
    });

    readonly form = this.#fb.group({
        loginType: this.#fb.control<ExtendedLoginAuthMethod>(null, Validators.required),
        entityConfig: null as 'ENTITY_CONFIG' | 'CHANNEL_CONFIG',
        collectiveLogin: this.#fb.group({
            collectives: this.#fb.control<LoginAuthMethod[]>([], atLeastOneRequiredInArray()),
            memberLimit: this.#fb.group({
                enableLimit: null as boolean,
                limit: this.#fb.control<number>({ value: null, disabled: true }, [Validators.required, Validators.min(1)])
            })
        }),
        customerLogin: this.customerLoginForm,
        vendorLogin: this.vendorLoginForm,
        vendorWithCustomerLogin: this.vendorWithCustomerLoginForm,
        queues: this.queuesForm,
        autoAssignPreviousOrders: false
    });

    readonly statusCtrl = this.#fb.control({ value: null, disabled: true });

    readonly $canGoToEntity = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR]));
    readonly $loading = toSignal(booleanOrMerge([
        this.#collectiveSrv.isCollectiveListLoading$(),
        this.#loginConfSrv.authConfig.inProgress$(),
        this.#entitiesService.isEntityLoading$(),
        this.#entitiesService.entityMemberCounter.inProgress$()
    ]));

    constructor() {
        this.#loadAuthConfig();
        this.#loadCustomerTypes();
        this.#loadEntityMemberCounter();
        this.#formChangesHandler();
        this.#refreshFormDataHandler();
        this.#refreshEntityFieldsFormHandler();
    }

    ngOnDestroy(): void {
        this.#loginConfSrv.authConfig.clear();
        this.#entitiesService.entityCustomerTypes.clear();
        this.#loginConfSrv.authConfig.clear();
        this.#entitiesService.entityMemberCounter.clear();
    }

    save(): void {
        this.save$().subscribe({
            next: () => {
                this.#ephemeralMessageService.showSuccess({ msgKey: 'LOGIN_CONFIG.UPDATE_SUCCESS' });
                this.#loadAuthConfig();
                this.#loadEntityMemberCounter();
            },
            error: (error: HttpErrorResponse) => {
                if (error.status === 412) {
                    this.#msgDialogSrv.showAlert({
                        size: DialogSize.SMALL,
                        title: 'TITLES.LOGIN_CONFIG.ERROR_DIALOG.' + error.error.code,
                        message: 'LOGIN_CONFIG.ERROR_DIALOG.' + error.error.code
                    });
                }
            }
        });
    }

    save$(): Observable<(void | void[])[]> {
        if (this.form.valid && this.form.dirty) {
            const loginTypeValue = this.form.value.loginType;
            const authConfig: Partial<LoginAuthConfig> = {
                use_entity_config: this.form.controls.entityConfig?.value === 'ENTITY_CONFIG' ? true : false,
                authenticators: []
            };

            switch (loginTypeValue) {
                case 'DEFAULT':
                    authConfig.authenticators.push({ type: 'DEFAULT' });
                    if (this.customerLoginForm.getRawValue().socialLogin.enabled) {
                        Object.entries(this.customerLoginForm.value.socialLogin.vendors).forEach(([id, value]) => {
                            if (value) authConfig.authenticators.push({ type: 'VENDOR', id });
                        });
                    }
                    authConfig.settings = {
                        mode: this.customerLoginForm.value.allowGuest ? 'NON_REQUIRED' : 'REQUIRED',
                        account_creation: this.customerLoginForm.getRawValue().disableCreateAccount ? 'DISABLED' : 'ENABLED',
                        user_data_editable: true,
                        triggers_on: [this.customerLoginForm.value.placement], // [vendorFormValue.placement],
                        blocked_customer_types_enabled: this.customerLoginForm.value.blocked_customer_types_enabled,
                        ...(this.customerLoginForm.value.blocked_customer_types_enabled &&
                            { blocked_customer_types: this.customerLoginForm.value.blocked_customer_types })
                    };
                    break;
                case 'COLLECTIVE':
                    const selectedCols = this.form.controls.collectiveLogin.controls.collectives.getRawValue().map(col => col.id);
                    if (selectedCols.length) {
                        authConfig.authenticators = selectedCols.map(id => ({ type: 'COLLECTIVE', id }));
                        authConfig.max_members = {
                            enabled: this.form.controls.collectiveLogin.controls.memberLimit.value.enableLimit,
                            limit: this.form.controls.collectiveLogin.controls.memberLimit.value.limit
                        };
                    }
                    break;
                case 'VENDOR':
                    const vendorFormValue = this.form.controls.vendorLogin.value;
                    const authenticator: LoginAuthMethod = {
                        type: 'VENDOR',
                        id: vendorFormValue.vendor
                    };
                    if (vendorsWithProviders.includes(vendorFormValue.vendor)) {
                        authenticator.properties = {
                            ['LOGIN_PROVIDERS']: vendorFormValue.providersSettings.loginProvider.join(','),
                            ['LEGAL_TERMS']: vendorFormValue.providersSettings.legalTerms.join(','),
                            ['SIGNUP_SOURCE']: vendorFormValue.providersSettings.signupSource
                        };
                    }
                    authConfig.authenticators.push(authenticator);
                    authConfig.settings = {
                        mode: vendorFormValue.allowGuest ? 'NON_REQUIRED' : 'REQUIRED',
                        account_creation: 'ENABLED',
                        user_data_editable: vendorFormValue.allowEditUserData,
                        triggers_on: [vendorFormValue.placement] // [vendorFormValue.placement]
                    };
                    break;
                case 'VENDOR_WITH_CUSTOMER':
                    const vendorWithCustomerFormValue = this.form.controls.vendorWithCustomerLogin.value;
                    const authenticatorWithCustomer: LoginAuthMethod = {
                        type: 'VENDOR',
                        id: vendorWithCustomerFormValue.vendor,
                        customer_creation: 'ENABLED'
                    };
                    if (vendorsWithProviders.includes(vendorWithCustomerFormValue.vendor)) {
                        authenticatorWithCustomer.properties = {
                            ['LOGIN_PROVIDERS']: vendorWithCustomerFormValue.providersSettings.loginProvider.join(','),
                            ['LEGAL_TERMS']: vendorWithCustomerFormValue.providersSettings.legalTerms.join(','),
                            ['SIGNUP_SOURCE']: vendorWithCustomerFormValue.providersSettings.signupSource
                        };
                    }
                    authConfig.authenticators.push(authenticatorWithCustomer);
                    authConfig.settings = {
                        mode: vendorWithCustomerFormValue.allowGuest ? 'NON_REQUIRED' : 'REQUIRED',
                        account_creation: vendorWithCustomerFormValue.disableCreateAccount ? 'DISABLED' : 'ENABLED',
                        user_data_editable: vendorWithCustomerFormValue.allowEditUserData,
                        triggers_on: [vendorWithCustomerFormValue.placement], // [vendorFormValue.placement]
                        blocked_customer_types_enabled: this.vendorWithCustomerLoginForm.value.blocked_customer_types_enabled,
                        ...(this.vendorWithCustomerLoginForm.value.blocked_customer_types_enabled &&
                            { blocked_customer_types: this.vendorWithCustomerLoginForm.value.blocked_customer_types }),
                        phone_validator: {
                            enabled: this.vendorWithCustomerLoginForm.value.phoneValidator.enabled,
                            validator_id: this.vendorWithCustomerLoginForm.value.phoneValidator.validator_id
                        }
                    };
                    break;
            }

            const obs$: Observable<void | void[]>[] = [];
            if (!this.isChannel) {
                if (this.form.controls.queues.dirty || this.form.controls.autoAssignPreviousOrders.dirty) {
                    const entity: PutEntity = { settings: {} };
                    if (this.form.controls.queues.dirty) {
                        entity.settings.account = {
                            queue_config: {
                                active: this.form.controls.queues.value.active,
                                alias: this.form.controls.queues.getRawValue().alias
                            }
                        };
                    }
                    if (this.form.controls.autoAssignPreviousOrders.dirty) {
                        entity.settings.customers = {
                            auto_assign_orders: this.form.controls.autoAssignPreviousOrders.value
                        };
                    }
                    obs$.push(this.#entitiesService.updateEntity(this.entityId, entity));
                }

                const loginType = this.form.controls.loginType.value;
                const customerLogin = this.form.controls.customerLogin;
                const vendorWithCustomerLogin = this.form.controls.vendorWithCustomerLogin;
                if ((loginType === 'DEFAULT' && customerLogin.controls.counter.dirty)
                    || (loginType === 'VENDOR_WITH_CUSTOMER' && vendorWithCustomerLogin.controls.counter.dirty)) {
                    const formGroup = loginType === 'DEFAULT' ? customerLogin : vendorWithCustomerLogin;
                    const entity: PutEntity = {
                        settings: { member_id_generation: formGroup.controls.counter.value.mode }
                    };
                    obs$.push(this.#entitiesService.updateEntity(this.entityId, entity));
                    if (formGroup.controls.counter.controls.mode.value === 'AUTOINCREMENT') {
                        const counterId = formGroup.controls.counter.value.id;
                        obs$.push(this.#entitiesService.entityMemberCounter.update(this.entityId, { member_counter: counterId }));
                    }
                }
            }

            const dirtyControls = Object.keys(this.form.controls).filter(key => key !== 'queues' && this.form.controls[key].dirty);

            if (this.isChannel || dirtyControls.length) {
                obs$.push(this.#loginConfSrv.authConfig.update(this.#channelId ? this.#channelId : this.entityId, authConfig));
            }

            return forkJoin(obs$).pipe(tap(() => {
                if (!this.isChannel) this.#entitiesService.loadEntity(this.entityId);
            }));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'Invalid form');
        }
    }

    cancel(): void {
        this.#loadAuthConfig();
        this.#loadEntityMemberCounter();
        if (!this.isChannel) this.#entitiesService.loadEntity(this.entityId);
    }

    handleStatusChange(isActive: boolean): void {
        if (this.form?.dirty) {
            of(null).pipe(
                delay(100),
                tap(() => this.statusCtrl.setValue(!isActive)),
                switchMap(() =>
                    this.#msgDialogSrv.showWarn(unsavedChangesDialogData)
                ),
                switchMap(saveAccepted =>
                    saveAccepted ? this.save$().pipe(map(() => true)) : of(false)
                )
            )
                .pipe(filter(Boolean))
                .subscribe({
                    next: () => {
                        this.#ephemeralMessageService.showSuccess({ msgKey: 'LOGIN_CONFIG.UPDATE_SUCCESS' });
                        this.#loadAuthConfig();
                    },
                    error: (error: HttpErrorResponse) => {
                        if (error.status === 412) {
                            this.#msgDialogSrv.showAlert({
                                size: DialogSize.SMALL,
                                title: 'TITLES.LOGIN_CONFIG.ERROR_DIALOG.' + error.error.code,
                                message: 'LOGIN_CONFIG.ERROR_DIALOG.' + error.error.code
                            });
                        }
                    }
                });
        } else {
            if (!this.isChannel && isActive) {
                this.form.controls.queues.enable();
                this.form.controls.autoAssignPreviousOrders.enable();
                if (!this.form.controls.queues.value.active) this.form.controls.queues.controls.alias.disable();
            };
            this.#saveStatus(isActive);
        }
    }

    #formChangesHandler(): void {
        this.form.controls.entityConfig.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(useEntityConfig => {
                if (this.#isItalianEntityPipe.transform(this.$entity())) {
                    this.form.controls.loginType.setValue('VENDOR_WITH_CUSTOMER');
                    this.form.controls.vendorWithCustomerLogin.controls.placement.setValue('BEFORE_SELECT_LOCATION');
                }
                if (useEntityConfig === 'ENTITY_CONFIG') {
                    this.form.controls.loginType.disable({ emitEvent: false });
                    this.form.controls.collectiveLogin.disable({ emitEvent: false });
                    this.form.controls.customerLogin.disable({ emitEvent: false });
                    this.form.controls.vendorLogin.disable({ emitEvent: false });
                    this.form.controls.vendorWithCustomerLogin.disable({ emitEvent: false });
                } else {
                    this.form.controls.loginType.enable();
                }
            });

        this.form.controls.loginType.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(loginType => {
                const collectiveLoginForm = this.form.controls.collectiveLogin;
                switch (loginType) {
                    case 'DEFAULT':
                        this.customerLoginForm.enable();
                        this.vendorLoginForm.disable();
                        this.vendorWithCustomerLoginForm.disable();
                        collectiveLoginForm.disable();
                        break;
                    case 'COLLECTIVE':
                        this.customerLoginForm.disable();
                        this.vendorLoginForm.disable();
                        this.vendorWithCustomerLoginForm.disable();
                        collectiveLoginForm.enable();
                        break;
                    case 'VENDOR':
                        this.customerLoginForm.disable();
                        this.vendorLoginForm.enable();
                        if (!vendorsWithProviders.includes(this.vendorLoginForm.controls.vendor.value)) {
                            this.vendorLoginForm.controls.providersSettings.disable({ emitEvent: false });
                        }
                        this.vendorWithCustomerLoginForm.disable({ emitEvent: false });
                        collectiveLoginForm.disable();
                        break;
                    case 'VENDOR_WITH_CUSTOMER':
                        this.customerLoginForm.disable();
                        this.vendorLoginForm.disable();
                        this.vendorWithCustomerLoginForm.enable({ emitEvent: false });
                        if (!vendorsWithProviders.includes(this.vendorWithCustomerLoginForm.controls.vendor.value)) {
                            this.vendorWithCustomerLoginForm.controls.providersSettings.disable({ emitEvent: false });
                        }
                        collectiveLoginForm.disable();
                        break;
                    default:
                        this.customerLoginForm.disable();
                        this.vendorLoginForm.disable();
                        this.vendorWithCustomerLoginForm.disable();
                        collectiveLoginForm.disable();
                }
            });

        if (!this.isChannel) {
            const { active, alias } = this.form.controls.queues.controls;
            active.valueChanges.pipe(takeUntilDestroyed()).subscribe(
                isActive => isActive ? alias.enable() : alias.disable()
            );
        }

        const phoneValidatorControls = this.vendorWithCustomerLoginForm.controls.phoneValidator.controls;
        phoneValidatorControls.enabled.valueChanges.pipe(takeUntilDestroyed()).subscribe(
            isEnabled => {
                if (isEnabled) {
                    phoneValidatorControls.validator_id.setValidators(Validators.required);
                } else {
                    phoneValidatorControls.validator_id.clearValidators();
                }
                phoneValidatorControls.validator_id.updateValueAndValidity();
            }
        );
    }

    #loadAuthConfig(): void {
        this.#entitySrv.getEntity$().pipe(
            first(Boolean)
        ).subscribe(entity => {
            this.entityId = entity.id;
            this.hasAuthVendors = entity.settings.external_integration?.auth_vendor?.enabled
                && !!entity.settings.external_integration?.auth_vendor?.vendor_id.length;
            if (this.isChannel) {
                this.#channelsService.getChannel$()
                    .pipe(take(1))
                    .subscribe(channel => {
                        this.#channelId = channel.id;
                        this.#loginConfSrv.authConfig.load(this.#channelId);
                    });
            } else {
                this.#loginConfSrv.authConfig.load(entity.id);
            }
        });
    }

    #loadCustomerTypes(): void {
        combineLatest([
            this.#entitySrv.getEntity$(),
            this.#channelsService.getChannel$()
        ]).pipe(
            first(resp => resp.every(Boolean))
        ).subscribe(([entity, channel]) => {
            if (channel.settings.v4_enabled) {
                this.#entitiesService.entityCustomerTypes.load(entity.id);
            }
        });
    }

    #loadEntityMemberCounter(): void {
        this.#entitySrv.getEntity$().pipe(first(Boolean))
            .subscribe(entity => {
                if (!this.isChannel) {
                    this.#entitiesService.entityMemberCounter.load(entity.id);
                }
            });
    }

    #saveStatus(isActive: boolean): void {
        const authConfig = { enabled: isActive, use_entity_config: this.form.controls.entityConfig.value === 'ENTITY_CONFIG' };
        this.#loginConfSrv.authConfig.update(this.#channelId ? this.#channelId : this.entityId, authConfig)
            .subscribe({
                next: () => isActive && this.#ephemeral.showSuccess({ msgKey: 'LOGIN_CONFIG.ACTIVATE_LOGIN_SUCCESS' }),
                complete: () => this.#loadAuthConfig(),
                error: () => this.statusCtrl.patchValue(!isActive)
            });
    }

    #refreshFormDataHandler(): void {
        this.#loginConfSrv.authConfig.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(),
                withLatestFrom(this.#channelsService.getChannel$(), this.#entitySrv.getEntity$())
            ).subscribe(([config, channel, entity]) => {
                const auths = config.authenticators;
                const customerCreation = auths?.some(auth => auth.customer_creation);
                const isVendor = auths?.every(auth => auth.type === 'VENDOR');
                const isCollective = auths?.every(auth => auth.type === 'COLLECTIVE');
                const isOneboxLogin = auths?.some(auth => auth.type === 'DEFAULT');
                let loginType: ExtendedLoginAuthMethod;
                if (isVendor && customerCreation) {
                    loginType = 'VENDOR_WITH_CUSTOMER';
                } else if (isVendor) {
                    loginType = 'VENDOR';
                } else if (isCollective) {
                    loginType = 'COLLECTIVE';
                } else if (isOneboxLogin) {
                    loginType = 'DEFAULT';
                }

                if (this.isChannel && isOneboxLogin) {
                    this.$customersLink.set(channel?.url?.replace('tickets', 'account').split('?')[0]);
                } else {
                    this.$customersLink.set('');
                }

                this.form.patchValue({
                    loginType,
                    entityConfig: config.use_entity_config ? 'ENTITY_CONFIG' : 'CHANNEL_CONFIG'
                });

                this.statusCtrl.patchValue(config.enabled);
                // Más adelante, se eliminará este flag específico de Italia y vendrá flageado de manera que el front
                // no necesite saber de qué provider externo se trata.
                if (
                    (config.authenticators?.length || this.form.controls.entityConfig.value === 'ENTITY_CONFIG') &&
                    !this.#isItalianEntityPipe.transform(entity)
                ) {
                    this.statusCtrl.enable();
                } else {
                    this.statusCtrl.disable();
                }

                if (!this.isChannel && !config.enabled) {
                    this.form.controls.queues.disable();
                    this.form.controls.autoAssignPreviousOrders.disable();
                    this.form.controls.queues.controls.active.setValue(false);
                    this.form.controls.autoAssignPreviousOrders.setValue(false);
                }

                this.form.markAsPristine();
                this.form.markAsUntouched();
            });
    }

    #refreshEntityFieldsFormHandler(): void {
        if (this.isChannel) {
            this.form.controls.queues.disable();
            this.form.controls.autoAssignPreviousOrders.disable();
            return;
        };
        this.#entitiesService.getEntity$().pipe(filter(Boolean), takeUntilDestroyed())
            .subscribe(entity => {
                const config = entity?.settings?.account?.queue_config;
                const queues = this.form.controls.queues;
                queues.patchValue(config);
                const autoAssign = entity?.settings?.customers?.auto_assign_orders ?? false;
                this.form.controls.autoAssignPreviousOrders.patchValue(autoAssign);
                if (!config?.active) queues.controls.alias.disable();
            });
    }
}
