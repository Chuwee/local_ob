import { AuthResponseState } from '@admin-clients/cpanel/core/data-access';
import { ContextNotificationComponent } from '@admin-clients/shared/common/ui/components';
import { AUTHENTICATION_SERVICE, AuthError, AuthErrorAction, AuthErrorCode } from '@admin-clients/shared/core/data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgClass, UpperCasePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastController } from '@ionic/angular';
import {
    IonButton, IonContent, IonIcon, IonInput, IonItem, IonLabel, IonNavLink, IonSpinner,
    IonText, IonHeader, IonToolbar, IonButtons, IonImg, IonFooter, IonModal
} from '@ionic/angular/standalone';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { EMPTY, filter, finalize, map, Observable, shareReplay, switchMap, take, withLatestFrom } from 'rxjs';
import { BackButtonComponent } from 'apps/native/src/app/core/components/back-button/back-button.component';
import { ModalComponent } from 'apps/native/src/app/core/components/modal/modal.component';
import { PickerComponent } from 'apps/native/src/app/core/components/picker/picker.component';
import { DeviceStorage } from 'apps/native/src/app/core/services/deviceStorage';
import { TrackingService } from 'apps/native/src/app/core/services/tracking.service';

@Component({
    selector: 'login-form',
    templateUrl: './login-form.component.html',
    styleUrls: ['./login-form.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, ContextNotificationComponent, UpperCasePipe, TranslatePipe, NgClass,
        AsyncPipe, BackButtonComponent, ModalComponent, PickerComponent, IonButton, IonIcon, IonItem,
        IonLabel, IonInput, IonNavLink, IonText, IonSpinner, IonContent, IonToolbar, IonHeader, IonButtons,
        IonImg, IonFooter, IonModal
    ]
})
export class LoginFormComponent implements OnInit, OnDestroy {
    readonly #auth = inject(AUTHENTICATION_SERVICE);
    readonly #fb = inject(FormBuilder);
    readonly #toastController = inject(ToastController);
    readonly #translateService = inject(TranslateService);
    readonly #changeDetectorRef = inject(ChangeDetectorRef);
    readonly #deviceStorage = inject(DeviceStorage);
    readonly #router = inject(Router);
    readonly #destroyRef = inject(DestroyRef);
    readonly #tracking = inject(TrackingService);

    readonly $contextualAlert = signal<{ show: boolean; message: string }>({ show: false, message: '' });
    readonly $accountBlocked = signal<{ show: boolean; type: AuthErrorAction }>({ show: false, type: null });
    readonly $submitted = signal(false);
    readonly $passIsShowed = signal(false);
    readonly $modalIsOpen = signal(false);
    readonly $mfaModalIsOpen = signal(false);
    readonly $pickerdata = signal([]);

    readonly form = this.#fb.group({
        username: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required],
        operator: '',
        mfa: this.#fb.group({
            type: '',
            code: ''
        })
    });

    readonly authErrorCode = AuthErrorCode;
    readonly authErrorAction = AuthErrorAction;

    readonly error$: Observable<AuthError> = this.#auth.getLoginError$().pipe(
        filter(Boolean),
        map(({ error }: HttpErrorResponse) => ({
            code: error.state === AuthResponseState.fail ?
                (this.form.value.mfa.type ? this.authErrorCode.WRONG_CREDENTIALS : this.authErrorCode.INVALID) : undefined,
            description: error.properties?.error_description || error.properties?.message,
            operators: error.properties?.operators,
            mfaType: error.properties?.mfa_type,
            user: {
                isBlocked: error.properties?.action === this.authErrorAction.USER_TEMPORAL_BLOCKED ||
                    error.properties?.action === this.authErrorAction.USER_PERMANENT_BLOCKED,
                blockedType: error.properties?.action
            },
            password: {
                expired: error.properties?.action === this.authErrorAction.RESET_PASSWORD,
                max_password_storage: error.properties?.max_password_storage || 0,
                reset_token: error.properties?.reset_token || null
            }
        })),
        shareReplay(1)
    );

    readonly loading$: Observable<boolean> = booleanOrMerge([
        this.#auth.isTokenLoading$(),
        this.#auth.isLoggedUserLoading$()
    ]);

    ngOnInit(): void {
        this.#deviceStorage.getItem('onboarding-executed').subscribe({
            next: onboardingIsExecuted => {
                if (!onboardingIsExecuted) {
                    this.#router.navigate(['/onboarding']);
                }
            }
        });

        this.#handleErrors();
    }

    ngOnDestroy(): void {
        this.form.reset();
    }

    onSubmit(): void {
        if (this.form.valid) {
            this.$submitted.set(true);
            this.loading$
                .pipe(
                    take(1),
                    switchMap(loading => loading ? EMPTY : this.#auth.login(this.form.getRawValue())),
                    take(1),
                    withLatestFrom(
                        this.#auth.getToken$().pipe(filter(token => !!token))
                    ),
                    take(1),
                    finalize(() => {
                        setTimeout(() => this.form.enable(), 500);
                    })
                )
                .subscribe({
                    next: () => {
                        this.$submitted.set(false);
                        this.$mfaModalIsOpen.set(false);
                        this.form.reset();
                        this.#router.navigate(['/tabs/home']);
                        this.$contextualAlert.set({ show: false, message: '' });
                    },
                    error: () => undefined
                });
        }
    }

    onResendCode(): void {
        this.#showToast('LOGIN.FORMS.FEEDBACKS.RESEND_CODE');
        this.#mfaCodeCtrl.reset();
        this.form.controls.mfa.disable();
        this.onSubmit();
    }

    get usernameCtrl(): AbstractControl {
        return this.form.get('username');
    }

    toggleShowPassword(): void {
        this.$passIsShowed.update(value => !value);
    }

    openModal(): void {
        this.$modalIsOpen.set(true);
    }

    onPick(pickOption: (string | number)[]): void {
        const value = pickOption[0];
        this.#operatorCtrl.setValue(String(value));
        this.onSubmit();
        this.closeModal();
    }

    goToForgotPassword(): void {
        this.#router.navigate(['/forgot-password']);
    }

    validateWhitespaces(e: KeyboardEvent): void {
        if (e.key === ' ') {
            e.preventDefault();
            const input = e.target as HTMLInputElement;
            input.value = input.value.replace(/\s/g, '');
        }
    }

    closeModal(): void {
        this.$modalIsOpen.set(false);
        this.#changeDetectorRef.detectChanges();
        this.#operatorCtrl.setValidators(null);
        this.$mfaModalIsOpen.set(false);

        if (!this.#operatorCtrl.value) {
            this.form.reset();
        }
    }

    openMfaModal(): void {
        this.$mfaModalIsOpen.set(true);
        this.#mfaCodeCtrl.setValidators(Validators.required);
        this.#mfaCodeCtrl.updateValueAndValidity();
    }

    closeMfaModal(): void {
        this.$mfaModalIsOpen.set(false);
        this.#mfaCodeCtrl.setValidators(null);
        this.#mfaCodeCtrl.updateValueAndValidity();
        this.form.controls.mfa.reset();
    }

    async #showToast(message: string, success: boolean = true): Promise<void> {
        const toast = await this.#toastController.create({
            message: this.#translateService.instant(message),
            duration: 2500,
            position: 'top',
            icon: `./assets/media/icons/${success ? 'success' : 'error'}_circle.svg`,
            cssClass: `ob-toast ob-toast--${success ? 'success' : 'error'}`
        });
        await toast.present();
    }

    #handleErrors(): void {
        this.error$.pipe(
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe((error: AuthError) => {
            this.#operatorCtrl.setValidators(null);
            this.#operatorCtrl.setValue(null);

            if (error.user?.isBlocked) return this.#handleUserBlockedError(error);
            if (error.password?.expired) return this.#handleExpiredPasswordError(error);

            this.#handleGeneralErrors(error);

            this.form.enable();
        });
    }

    #handleGeneralErrors(error: AuthError): void {
        if (error.code) {
            this.form.controls.mfa.reset();
            this.$contextualAlert.set({ show: true, message: error.code });
            this.$mfaModalIsOpen.set(false);
        } else if (error.operators) {
            this.$pickerdata.set(error.operators.map(operItem => ({ value: operItem.code, label: operItem.name })));
            this.openModal();
            this.#operatorCtrl.setValidators([Validators.required]);
        } else if (error.mfaType) {
            this.#mfaTypeCtrl.setValue(error.mfaType);
            this.openMfaModal();
        } else {
            this.$contextualAlert.set({ show: true, message: 'bad_login' });
        }
    }

    #handleExpiredPasswordError(error: AuthError): void {
        this.#router.navigate(['/reset-password'], {
            queryParams: {
                token: error.password?.reset_token,
                storage: error.password?.max_password_storage || 0
            }
        });
        this.form.get('password').reset();
        this.$submitted.set(false);
        this.$contextualAlert.set({ show: false, message: '' });
    }

    #handleUserBlockedError(error: AuthError): void {
        this.$contextualAlert.set({ show: false, message: '' });
        this.$accountBlocked.set({ show: true, type: error.user.blockedType });
        this.form.reset();
        this.$submitted.set(false);
    }

    get #passwordCtrl(): AbstractControl {
        return this.form.get('password');
    }

    get #operatorCtrl(): AbstractControl {
        return this.form.get('operator');
    }

    get #mfaCodeCtrl(): AbstractControl {
        return this.form.controls.mfa.controls.code;
    }

    get #mfaTypeCtrl(): AbstractControl {
        return this.form.controls.mfa.controls.type;
    }

}
