import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ContextNotificationComponent, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { AUTHENTICATION_SERVICE, AuthErrorAction } from '@admin-clients/shared/core/data-access';
import { checkPasswords, passwordValidator } from '@admin-clients/shared/utility/utils';
import { KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { UserPasswordConditionsErrors } from '../models/user-password-conditions-errors.enum';

@Component({
    selector: 'app-new-password',
    templateUrl: './new-password.component.html',
    styleUrls: ['./new-password.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, TranslatePipe, ContextNotificationComponent, MatFormFieldModule, MatInputModule, MatIconModule,
        FormControlErrorsComponent, MatProgressSpinnerModule, KeyValuePipe, MatButtonModule
    ]
})
export class NewPasswordComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #auth = inject(AUTHENTICATION_SERVICE);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    readonly passwordConditionErrors = UserPasswordConditionsErrors;

    readonly $isNewPwdBlur = signal(false);
    readonly $isPasswordExpired = signal(false);
    readonly $showPasswordPreviouslyUsed = signal(false);
    readonly $maxPasswordStorage = signal(0);
    readonly $showNewPasswordValue = signal(false);
    readonly $showConfirmPasswordValue = signal(false);

    readonly $reqInProgress = toSignal(this.#auth.isNewPwdLoading$());

    #userToken: string;
    form: UntypedFormGroup = this.#fb.group({
        newPassword: [null, [
            Validators.required,
            passwordValidator(/^(?=.{12,}$)/, 'noMinLength'),
            passwordValidator(/^(?=.*[0-9])/, 'noDigit'),
            passwordValidator(/^(?=.*[a-z])(?=.*[A-Z])/, 'noLowerAndUpperCase'),
            passwordValidator(/^(?=.*[^A-Za-z0-9])/, 'noSpecialCharacter')
        ]],
        confirmPassword: [null, Validators.required]
    }, { validators: checkPasswords('newPassword', 'confirmPassword') });

    ngOnInit(): void {
        this.#initializeRouteParams();
        this.#verifyToken();
    }

    setNewPassword(): void {
        if (this.form.invalid) return;
        const request: { new_password: string; token: string } = {
            new_password: this.form.value.newPassword,
            token: this.#userToken
        };
        this.#auth.setNewPassword(request).subscribe({
            next: () => {
                this.#router.navigate(['login']);
                this.#ephemeralSrv.showSuccess({ msgKey: 'LOGIN.NEW_PASSWORD.SUCCESS' });
            }, error: error => {
                if (error.error?.code === AuthErrorAction.PASSWORD_NOT_VALID) {
                    this.$showPasswordPreviouslyUsed.set(true);
                    this.form.reset();
                }
            }
        });

    }

    onNewPwdBlur(): void {
        this.$isNewPwdBlur.set(true);
    }

    handleShowNewPasswordValue(): void {
        this.$showNewPasswordValue.update(value => !value);
    }

    handleShowConfirmPasswordValue(): void {
        this.$showConfirmPasswordValue.update(value => !value);
    }

    #initializeRouteParams(): void {
        const { token, expired, storage } = this.#route.snapshot.queryParams;
        this.#userToken = token;
        this.$isPasswordExpired.set(expired || false);
        this.$maxPasswordStorage.set(storage || 0);
    }

    #verifyToken(): void {
        this.#auth.verifyToken(this.#userToken).subscribe({
            error: () => this.#router.navigate(['login'])
        });
    }
}
