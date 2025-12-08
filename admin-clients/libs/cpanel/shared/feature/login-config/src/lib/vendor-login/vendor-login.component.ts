import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { IsItalianEntityPipe } from '@admin-clients/cpanel/organizations/entities/utils';
import { ENTITY_SERVICE, LOGIN_CONFIG_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import {
    EntityCustomerType, LoginPlacement, socialLogins, vendorsWithProviders
} from '@admin-clients/shared/common/data-access';
import { Chip, ChipsComponent, CollectionInputComponent } from '@admin-clients/shared/common/ui/components';
import { atLeastOneRequiredInArray, maxDecimalLength } from '@admin-clients/shared/utility/utils';
import {
    booleanAttribute, ChangeDetectionStrategy, Component, computed, DestroyRef, inject, input, OnDestroy, OnInit
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormControl, FormGroup, FormGroupDirective, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, filter, map, startWith } from 'rxjs';

@Component({
    selector: 'ob-vendor-login',
    templateUrl: './vendor-login.component.html',
    styleUrls: ['./vendor-login.component.scss'],
    imports: [TranslatePipe, ReactiveFormsModule, MatFormFieldModule, MatSelectModule, MatRadioModule, MatCheckboxModule,
        MatInput, MatIcon, MatTooltip, FormControlErrorsComponent, CollectionInputComponent, ChipsComponent, IsItalianEntityPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class VendorLoginComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #formGroupDirective = inject(FormGroupDirective);
    readonly #entitySrv = inject(ENTITY_SERVICE);
    readonly #entitiesService = inject(EntitiesService);
    readonly #loginConfSrv = inject(LOGIN_CONFIG_SERVICE);
    readonly #currentLegalTerms = new BehaviorSubject<string[]>([]);

    readonly loginProviders$ = this.#entitiesService.authVendor.get$()
        .pipe(map(vendor => vendor?.properties?.['LOGIN_PROVIDERS']?.split(',') || []));

    readonly currentLegalTerms$ = this.#currentLegalTerms.asObservable();
    readonly currentLegalTermsChips$ = this.currentLegalTerms$.pipe(
        map(terms => terms.map((term, index) => ({ label: term, value: index } as Chip)))
    );

    readonly $loginProviders = toSignal(this.loginProviders$);

    readonly $customerRegistry = input(false, { alias: 'customerRegistry', transform: booleanAttribute });
    readonly $customerTypes = input<EntityCustomerType[]>([], { alias: 'customerTypes' });
    readonly $isChannel = input.required<boolean>({ alias: 'isChannel' });

    readonly $authVendors = toSignal(this.#entitySrv.getEntity$()
        .pipe(
            filter(Boolean),
            map(entity => entity.settings.external_integration?.auth_vendor?.vendor_id)));

    readonly $entity = toSignal(this.#entitySrv.getEntity$().pipe(filter(Boolean)));

    readonly $authVendorsFiltered = computed(() => this.$authVendors()?.filter(id =>
        !socialLogins.includes(id) && (this.$customerRegistry() ? true : id !== 'FEVER')
    ) || []);

    readonly $availableValidators = toSignal(this.#loginConfSrv.authConfig.get$().pipe(
        map(config => config?.settings?.phone_validator?.available_validators)
    ));

    form: FormGroup<{
        vendor: FormControl<string>;
        providersSettings: FormGroup<{
            loginProvider: FormControl<string[]>;
            legalTerms: FormControl<string[]>;
            signupSource: FormControl<string>;
        }>;
        allowEditUserData: FormControl<boolean>;
        allowGuest: FormControl<boolean>;
        blocked_customer_types_enabled?: FormControl<boolean>;
        disableCreateAccount?: FormControl<boolean>;
        blocked_customer_types?: FormControl<string[]>;
        placement: FormControl<LoginPlacement>;
        counter?: FormGroup<{
            mode: FormControl<'DEFAULT' | 'AUTOINCREMENT'>;
            id: FormControl<number>;
        }>;
        phoneValidator: FormGroup<{
            enabled: FormControl<boolean>;
            validator_id: FormControl<string>;
        }>;
    }>;

    ngOnInit(): void {
        this.form = this.#formGroupDirective.control;
        if (this.$customerRegistry()) {
            if (this.$customerTypes()?.length) this.#handleCustomerTypesEnabledChange();
            if (!this.$isChannel()) this.#handleCounterModeChange();
        }
        if (this.$isChannel()) this.form.controls.counter?.disable();

        this.#handleVendorChange();
        this.#refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this.#entitiesService.authVendor.clear();
    }

    removeLegalTerm(legalTerm: Chip): void {
        if (this.form.controls.providersSettings.controls.legalTerms.enabled) {
            const index = legalTerm.value as number;
            const currentLegalTerms = this.form.controls.providersSettings.controls.legalTerms.value;

            currentLegalTerms.splice(index, 1);

            this.form.controls.providersSettings.controls.legalTerms.patchValue(currentLegalTerms);
            this.#currentLegalTerms.next(currentLegalTerms);
            this.form.controls.providersSettings.controls.legalTerms.markAsDirty();
        }
    }

    #refreshFormDataHandler(): void {
        this.#loginConfSrv.authConfig.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(config => {
                const isVendorWithCustomer = config.authenticators?.some(auth => auth.type === 'VENDOR' && auth.customer_creation);
                const hasAuthTypeDefault = config.authenticators?.some(auth => auth.type === 'DEFAULT');

                if (isVendorWithCustomer === this.$customerRegistry()) {
                    const auths = config.authenticators?.filter(auth => !socialLogins.includes(auth.id));
                    this.form.patchValue({
                        phoneValidator: {
                            enabled: config.settings?.phone_validator?.enabled,
                            validator_id: config.settings?.phone_validator?.validator_id
                        },
                        vendor: auths?.find(auth => auth.type === 'VENDOR')?.id,
                        providersSettings: {
                            loginProvider: auths?.find(auth => auth.type === 'VENDOR')?.properties?.['LOGIN_PROVIDERS']?.split(','),
                            legalTerms: auths?.find(auth => auth.type === 'VENDOR')?.properties?.['LEGAL_TERMS']?.split(','),
                            signupSource: auths?.find(auth => auth.type === 'VENDOR')?.properties?.['SIGNUP_SOURCE']
                        },
                        allowEditUserData: config.settings?.user_data_editable,
                        allowGuest: config.settings?.mode === 'NON_REQUIRED',
                        ...(!hasAuthTypeDefault && { placement: config.settings?.triggers_on?.[0] }),
                        disableCreateAccount: !this.$customerRegistry() || config.settings?.account_creation === 'DISABLED',
                        ...(this.$customerTypes().length && {
                            blocked_customer_types_enabled: config.settings?.blocked_customer_types_enabled,
                            blocked_customer_types: config.settings?.blocked_customer_types
                        })
                    });

                    if (this.form.controls.counter?.controls.mode.value === 'DEFAULT') {
                        this.form.controls.counter?.controls.id.disable();
                    }

                    this.form.markAsPristine();
                    this.form.markAsUntouched();
                }
                if (config.authenticators?.some(auth => auth.properties?.['LEGAL_TERMS'])) {
                    this.#currentLegalTerms.next(config.authenticators
                        .find(auth => auth.properties?.['LEGAL_TERMS'])?.properties?.['LEGAL_TERMS']?.split(',') || []);
                }
            });
        if (!this.$isChannel()) {
            this.#entitySrv.getEntity$()
                .pipe(
                    filter(Boolean),
                    takeUntilDestroyed(this.#destroyRef)
                ).subscribe(entity => {
                    this.form.controls.counter?.patchValue({
                        mode: entity?.settings?.member_id_generation ?? 'DEFAULT'
                    });

                    this.form.markAsPristine();
                    this.form.markAsUntouched();
                });

            this.#entitiesService.entityMemberCounter.get$()
                .pipe(
                    filter(Boolean),
                    takeUntilDestroyed(this.#destroyRef)
                ).subscribe(memberCounter => {
                    this.form.controls.counter?.patchValue({
                        id: memberCounter?.member_counter ?? null
                    });
                    this.form.controls.counter?.controls.id.setValidators([
                        Validators.required,
                        this.#minIfFormDirty(memberCounter.member_counter, this.form.controls.counter),
                        maxDecimalLength(0)
                    ]);
                    this.form.controls.counter?.controls.id.updateValueAndValidity({ emitEvent: false });

                    this.form.markAsPristine();
                    this.form.markAsUntouched();
                });
        }
    }

    #handleCustomerTypesEnabledChange(): void {
        this.form.controls.blocked_customer_types_enabled.valueChanges.pipe(
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(enabled => {
            if (enabled) {
                this.form.controls.blocked_customer_types.enable();
                this.form.controls.blocked_customer_types.addValidators(Validators.required);
            } else {
                this.form.controls.blocked_customer_types.disable();
                this.form.controls.blocked_customer_types.clearValidators();
            }
        });
    }

    #handleCounterModeChange(): void {
        this.form.controls.counter?.controls.mode.valueChanges
            .pipe(
                takeUntilDestroyed(this.#destroyRef),
                startWith(this.form.controls.counter?.controls.mode.value)
            )
            .subscribe(mode => {
                if (mode === 'AUTOINCREMENT') this.form.controls.counter?.controls.id.enable();
                else this.form.controls.counter?.controls.id.disable();
            });
    }

    #handleVendorChange(): void {
        this.form.controls.providersSettings.controls.legalTerms.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(legalTerms => {
                this.#currentLegalTerms.next(legalTerms || []);
            });

        combineLatest([this.loginProviders$, this.form.controls.vendor.valueChanges])
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([providers, vendorValue]) => {
                if (providers.length && vendorsWithProviders.includes(vendorValue)) {
                    this.form.controls.providersSettings.enable();
                    this.form.controls.providersSettings.controls.loginProvider.addValidators(atLeastOneRequiredInArray());
                    this.form.controls.providersSettings.controls.legalTerms.addValidators(atLeastOneRequiredInArray());
                    this.form.controls.providersSettings.controls.signupSource.addValidators(Validators.required);
                } else {
                    this.form.controls.providersSettings.disable();
                    this.form.controls.providersSettings.controls.loginProvider.clearValidators();
                    this.form.controls.providersSettings.controls.legalTerms.clearValidators();
                    this.form.controls.providersSettings.controls.signupSource.clearValidators();
                }
            });

        this.form.controls.vendor.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(vendor => {
                if (vendor && vendorsWithProviders.includes(vendor)) {
                    this.#entitiesService.authVendor.load(vendor);
                } else {
                    this.#entitiesService.authVendor.clear();
                }
            });
    }

    #minIfFormDirty(minValue: number, formGroup: FormGroup) {
        return (control: AbstractControl) => {
            if (!formGroup.dirty) return null;
            return control.value > minValue ? null : { min: { min: minValue + 1, actual: control.value } };
        };
    }
}
