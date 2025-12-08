import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { ENTITY_SERVICE, LOGIN_CONFIG_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import { EntityCustomerType, LoginPlacement, socialLogins } from '@admin-clients/shared/common/data-access';
import { HelpButtonComponent } from '@admin-clients/shared/common/ui/components';
import { maxDecimalLength } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, FormControl, FormGroup, FormGroupDirective, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, startWith } from 'rxjs';

@Component({
    selector: 'ob-customer-login',
    templateUrl: './customer-login.component.html',
    styleUrls: ['./customer-login.component.scss'],
    imports: [
        MatCheckbox, TranslatePipe, ReactiveFormsModule, MatFormField, MatRadioGroup,
        MatSelect, HelpButtonComponent, MatIcon, MatRadioButton, MatLabel,
        MatTooltip, FormControlErrorsComponent, MatInput, MatError, MatOption
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class CustomerLoginComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #formGroupDirective = inject(FormGroupDirective);
    readonly #loginConfSrv = inject(LOGIN_CONFIG_SERVICE);
    readonly #entitySrv = inject(ENTITY_SERVICE);
    readonly #entitiesService = inject(EntitiesService);
    readonly #fb = inject(FormBuilder);

    readonly $customerTypes = input<EntityCustomerType[]>([], { alias: 'customerTypes' });
    readonly $isChannel = input.required<boolean>({ alias: 'isChannel' });
    readonly socialLogins = socialLogins;

    form: FormGroup<{
        allowGuest: FormControl<boolean>;
        socialLogin: FormGroup<{
            enabled: FormControl<boolean>;
            vendors: FormGroup<Record<string, FormControl<boolean>>>;
        }>;
        disableCreateAccount: FormControl<boolean>;
        blocked_customer_types_enabled: FormControl<boolean>;
        blocked_customer_types: FormControl<string[]>;
        placement: FormControl<LoginPlacement>;
        counter?: FormGroup<{
            mode: FormControl<'DEFAULT' | 'AUTOINCREMENT'>;
            id: FormControl<number>;
        }>;
    }>;

    ngOnInit(): void {
        this.form = this.#formGroupDirective.control;

        // ADD FORM CONTROL FOR EACH SOCIAL LOGIN ID
        socialLogins.forEach(id => {
            this.form.controls.socialLogin.controls.vendors.addControl(id, this.#fb.control({ value: false, disabled: true }));
        });

        this.#formChangesHandler();
        if (!this.$isChannel()) {
            this.#handleCounterModeChange();
        } else {
            this.form.controls.counter?.disable();
        }
        this.#refreshFormDataHandler();
    }

    #refreshFormDataHandler(): void {
        this.#loginConfSrv.authConfig.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(config => {
                const auths = config.authenticators;
                const hasAuthTypeDefault = auths?.some(auth => auth.type === 'DEFAULT');
                const socialLogin = hasAuthTypeDefault && auths?.some(auth => auth.type === 'VENDOR');
                const enabledSocialLogins = auths?.map(auth => auth.id).filter(auth => socialLogins.includes(auth)) || [];
                this.form.patchValue({
                    allowGuest: config.settings?.mode === 'NON_REQUIRED',
                    disableCreateAccount: config.settings?.account_creation === 'DISABLED',
                    socialLogin: {
                        enabled: socialLogin
                    },
                    blocked_customer_types_enabled: config.settings?.blocked_customer_types_enabled,
                    blocked_customer_types: config.settings?.blocked_customer_types,
                    ...(hasAuthTypeDefault && { placement: config.settings?.triggers_on?.[0] })
                });

                // UPDATE SOCIAL LOGINS VALUES
                socialLogins.forEach(auth => {
                    this.form.controls.socialLogin.controls.vendors.controls[auth].patchValue(enabledSocialLogins.includes(auth));
                });

                if (this.form.controls.counter?.controls.mode.value === 'DEFAULT') {
                    this.form.controls.counter?.controls.id.disable();
                }

                this.form.markAsPristine();
                this.form.markAsUntouched();
            });

        if (!this.$isChannel()) {
            this.#entitySrv.getEntity$()
                .pipe(
                    filter(Boolean),
                    takeUntilDestroyed(this.#destroyRef)
                ).subscribe(entity => {
                    this.form.controls.counter.patchValue({
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
                    this.form.controls.counter.patchValue({
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

    #formChangesHandler(): void {
        this.form.controls.blocked_customer_types_enabled.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => {
                if (enabled) {
                    this.form.controls.blocked_customer_types.enable();
                    this.form.controls.blocked_customer_types.addValidators(Validators.required);
                } else {
                    this.form.controls.blocked_customer_types.disable();
                    this.form.controls.blocked_customer_types.clearValidators();
                }
            });

        this.form.controls.socialLogin.controls.enabled.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => {
                if (enabled) {
                    this.form.controls.socialLogin.controls.vendors.enable();
                } else {
                    this.form.controls.socialLogin.controls.vendors.reset();
                    this.form.controls.socialLogin.controls.vendors.disable();
                }

            });

        combineLatest([
            this.form.controls.disableCreateAccount.valueChanges,
            this.form.controls.socialLogin.controls.enabled.valueChanges
        ])
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([disableCreateAccount, socialLoginEnabled]) => {
                const disableCreateAccountCtrl = this.form.controls.disableCreateAccount;
                const socialLoginEnabledCtrl = this.form.controls.socialLogin.controls.enabled;
                if (disableCreateAccount === socialLoginEnabled) {
                    disableCreateAccountCtrl.enable({ emitEvent: false });
                    socialLoginEnabledCtrl.enable({ emitEvent: false });
                } else if (disableCreateAccount) {
                    socialLoginEnabledCtrl.patchValue(false, { emitEvent: false });
                    socialLoginEnabledCtrl.disable({ emitEvent: false });
                    /*
                        IN THE CASE OF HAVING BOTH TRUE WHEN LANDING,
                        CHANGING ONE OF THEM TO FALSE WILL DISABLE THIS SAME CONTROL,
                        SO PARENT FORM WOULD NOT BE MARKED AS DIRTY BECAUSE THE CHANGE HAPPENED ON A CURRENTLY DISABLED CONTROL.
                    */
                    this.form.markAsDirty();
                } else {
                    disableCreateAccountCtrl.patchValue(false, { emitEvent: false });
                    disableCreateAccountCtrl.disable({ emitEvent: false });
                    this.form.markAsDirty();
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

    #minIfFormDirty(minValue: number, formGroup: FormGroup) {
        return (control: AbstractControl) => {
            if (!formGroup.dirty) return null;
            return control.value > minValue ? null : { min: { min: minValue + 1, actual: control.value } };
        };
    }
}
