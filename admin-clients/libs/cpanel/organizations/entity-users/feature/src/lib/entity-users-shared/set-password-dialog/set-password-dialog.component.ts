import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    EntityUsersService, PostEntityUserPasswordRequest,
    PostMyUserPasswordRequest
} from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { ContextNotificationComponent, DialogSize } from '@admin-clients/shared/common/ui/components';
import { AuthErrorAction } from '@admin-clients/shared/core/data-access';
import { UserPasswordConditionsErrors } from '@admin-clients/shared/feature/login';
import {
    booleanOrMerge,
    checkPasswords,
    passwordValidator
} from '@admin-clients/shared/utility/utils';
import { KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map } from 'rxjs';

@Component({
    selector: 'ob-set-password-dialog',
    templateUrl: './set-password-dialog.component.html',
    styleUrls: ['./set-password-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, FormControlErrorsComponent, TranslatePipe, FlexLayoutModule,
        ContextNotificationComponent, MatIconModule, MatFormFieldModule, MatProgressSpinnerModule,
        MatInputModule, MatButtonModule, KeyValuePipe, MatDialogModule
    ]
})
export class SetPasswordDialogComponent implements OnInit {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #dialogRef = inject(MatDialogRef);
    readonly #data: { userId: number; isOperator: boolean; selectionIsMyUser?: boolean; userEntityId?: number } = inject(MAT_DIALOG_DATA);
    readonly #route = inject(ActivatedRoute);
    readonly #auth = inject(AuthenticationService);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #entityUserSrv = inject(EntityUsersService);
    readonly passwordConditionErrors = UserPasswordConditionsErrors;

    #userId = this.#data.userId;
    #userEntityId = this.#data?.userEntityId;
    #selectionIsMyUser = this.#data.selectionIsMyUser;

    readonly $passwordNotValid = signal(false);
    readonly $showOldPasswordValue = signal(false);
    readonly $showNewPasswordValue = signal(false);
    readonly $showConfirmPasswordValue = signal(false);
    readonly $isOperator = signal(this.#data.isOperator);
    readonly $isMyUser = signal(false);
    readonly $isNewPwdBlur = signal(false);

    readonly $loading = toSignal(booleanOrMerge([
        this.#entityUserSrv.isPasswordSaving$(),
        this.#entitiesSrv?.entitySecurity?.inProgress$()

    ]));

    readonly $maxPasswordStorage = toSignal<number>(this.#entitiesSrv.entitySecurity.getEntitySecurity$().pipe(
        filter(Boolean),
        map(securitySettings => securitySettings?.password_config?.storage?.amount)
    ));

    form: UntypedFormGroup;

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;

        if (this.#selectionIsMyUser !== undefined) {
            this.$isMyUser.set(this.#selectionIsMyUser);
        } else {
            this.#auth.getLoggedUser$()
                .pipe(first(loggedUser => !!loggedUser))
                .subscribe(loggedUser => {
                    this.$isMyUser.set(Number(this.#route.snapshot.firstChild.firstChild.params?.['userId']) === loggedUser.id
                        || !this.#route.snapshot.firstChild.firstChild.params?.['userId']);
                });
        }
        this.#entitiesSrv.entitySecurity.load(this.#userEntityId);
        this.#initForm();
    }

    close(isDone = false): void {
        this.#dialogRef.close(isDone);
    }

    setPassword(): void {
        if (this.form.valid) {
            const { oldPassword, newPassword } = this.form.value;
            if (this.$isOperator() && !this.$isMyUser()) {
                const request: PostEntityUserPasswordRequest = { password: newPassword };
                this.#entityUserSrv.saveEntityUserPassword(this.#userId, request).subscribe({
                    next: () => this.close(true),
                    error: error => this.$passwordNotValid.set(
                        error.error?.code === AuthErrorAction.PASSWORD_NOT_VALID
                    )
                });
            } else {
                const request: PostMyUserPasswordRequest = { password: newPassword, old_password: oldPassword };
                this.#entityUserSrv.saveMyUserPassword(request).subscribe({
                    next: () => this.close(true),
                    error: error => this.$passwordNotValid.set(
                        error.error?.code === AuthErrorAction.PASSWORD_NOT_VALID
                    )
                });
            }

        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    onNewPwdBlur(): void {
        this.$isNewPwdBlur.set(true);
    }

    handleShowOldPasswordValue(): void {
        this.$showOldPasswordValue.update(value => !value);
    }

    handleShowNewPasswordValue(): void {
        this.$showNewPasswordValue.update(value => !value);
    }

    handleShowConfirmPasswordValue(): void {
        this.$showConfirmPasswordValue.update(value => !value);
    }

    #initForm(): void {
        this.form = this.#fb.group({
            newPassword: [null, [
                Validators.required,
                passwordValidator(/^(?=.{12,}$)/, 'noMinLength'),
                passwordValidator(/^(?=.*[0-9])/, 'noDigit'),
                passwordValidator(/^(?=.*[a-z])(?=.*[A-Z])/, 'noLowerAndUpperCase'),
                passwordValidator(/^(?=.*[^A-Za-z0-9])/, 'noSpecialCharacter')
            ]],
            confirmPassword: [null, Validators.required],
            oldPassword: null
        }, { validators: checkPasswords('newPassword', 'confirmPassword') });

        if (!this.$isOperator() || this.$isMyUser()) {
            this.form.get('oldPassword').setValidators(Validators.required);
        }
    }
}
