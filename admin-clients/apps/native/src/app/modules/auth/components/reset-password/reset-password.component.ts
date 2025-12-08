import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { ContextNotificationComponent } from '@admin-clients/shared/common/ui/components';
import { UserPasswordConditionsErrors } from '@admin-clients/shared/feature/login';
import { passwordValidator, checkPasswords } from '@admin-clients/shared/utility/utils';
import { KeyValuePipe, NgClass, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ModalController, ToastController } from '@ionic/angular';
import {
    IonButton, IonContent, IonFooter, IonHeader, IonIcon, IonImg, IonInput,
    IonItem, IonLabel, IonSpinner, IonText
} from '@ionic/angular/standalone';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'reset-password',
    templateUrl: './reset-password.component.html',
    styleUrls: ['./reset-password.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        IonHeader, IonContent, ReactiveFormsModule, IonItem, IonImg, IonText, TranslatePipe, ContextNotificationComponent,
        UpperCasePipe, IonLabel, IonInput, NgClass, IonButton, IonIcon, KeyValuePipe, IonSpinner, IonFooter
    ]
})
export class ResetPasswordComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #auth = inject(AuthenticationService);
    readonly #toastController = inject(ToastController);
    readonly #translate = inject(TranslateService);
    readonly #modalCtrl = inject(ModalController);

    readonly $showPasswordValue = signal<boolean>(false);
    readonly $showConfirmPasswordValue = signal<boolean>(false);
    readonly $maxPasswordStorage = signal<number>(0);
    readonly $isNewPwdBlur = signal<boolean>(false);
    readonly $isOldPasswordRepeated = signal<{ show: boolean; message: string }>({ show: false, message: '' });

    readonly $loading = toSignal(this.#auth.isNewPwdLoading$());

    readonly form = this.#fb.group({
        newPassword: [null as string, [
            Validators.required,
            passwordValidator(/^(?=.{12,}$)/, 'noMinLength'),
            passwordValidator(/^(?=.*[0-9])/, 'noDigit'),
            passwordValidator(/^(?=.*[a-z])(?=.*[A-Z])/, 'noLowerAndUpperCase'),
            passwordValidator(/^(?=.*[^A-Za-z0-9])/, 'noSpecialCharacter')
        ]],
        confirmPassword: [null as string, Validators.required],
        token: [null as string, Validators.required]
    }, { validators: checkPasswords('newPassword', 'confirmPassword') });

    readonly passwordConditionErrors = UserPasswordConditionsErrors;

    ngOnInit(): void {
        this.$maxPasswordStorage.set(this.#route.snapshot.queryParams?.['storage'] || 0);
        this.form.patchValue({ token: this.#route.snapshot.queryParams?.['token'] || '' });
        this.#modalCtrl?.dismiss();
    }

    onSubmit(): void {
        if (!this.form.valid) return;
        const request: { new_password: string; token: string } = {
            new_password: this.form.value.newPassword,
            token: this.form.value.token
        };
        this.#auth.setNewPassword(request).subscribe({
            next: () => {
                this.#router.navigate(['login']);
                this.#showToast('success', 'RESET-PASS.NOTIFICATION.SUCCESS');
            },
            error: error => {
                this.$isOldPasswordRepeated.set({
                    show: true,
                    message: 'RESET-PASS.NOTIFICATION.ERROR.' + error.error.code
                });
            }
        });
    }

    async #showToast(type: 'success' | 'error', message: string): Promise<void> {
        const activeToast = await this.#toastController.getTop();
        if (activeToast) await this.#toastController.dismiss();

        const toast = await this.#toastController.create({
            message: this.#translate.instant(message),
            duration: 2500,
            position: 'top',
            icon: `./assets/media/icons/${type}_circle.svg`,
            cssClass: `ob-toast ob-toast--${type}`
        });

        await toast.present();
    }
}
