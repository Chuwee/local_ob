import { EntityUsersService, MfaActivationState, PostMfaActivation, PostMfaActivationResponse } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { DialogSize, MessageDialogService, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component, inject, signal, viewChild, WritableSignal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { take } from 'rxjs';

@Component({
    selector: 'ob-activate-mfa-dialog',
    templateUrl: './activate-mfa-dialog.component.html',
    styleUrl: './activate-mfa-dialog.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MaterialModule, TranslatePipe, FlexLayoutModule, WizardBarComponent, ReactiveFormsModule]
})
export class ActivateMfaDialogComponent
    extends ObDialog<ActivateMfaDialogComponent, { userId: number | 'myself' }, { success: boolean; msgKey?: string }> {
    readonly #fb = inject(FormBuilder);
    readonly #entityUsersSrv = inject(EntityUsersService);
    readonly #msgDialogService = inject(MessageDialogService);

    private readonly _wizardBar = viewChild(WizardBarComponent);

    readonly currentStep$: WritableSignal<number> = signal(1);

    readonly form = this.#fb.group({
        password: ['', Validators.required],
        code: ['', Validators.required]
    });

    readonly $isLoading = toSignal(this.#entityUsersSrv.isMfaActivationLoading$());

    constructor() {
        super(DialogSize.MEDIUM, true);
    }

    close(): void {
        this.dialogRef.close();
    }

    setStep1(): void {
        this.form.reset();
        this._wizardBar()?.previousStep();
        this.currentStep$.set(1);
    }

    setStep2(): void {
        const password = this.form.controls.password;
        password.markAsTouched();
        if (password.invalid || !this.data.userId) return;

        const body: PostMfaActivation = {
            password: password.value,
            mfa: {
                type: 'EMAIL'
            }
        };
        this.#entityUsersSrv.sendMfaEmail$(
            this.data.userId,
            body
        )
            .pipe(take(1))
            .subscribe({
                next: () => {
                    this._wizardBar()?.nextStep();
                    this.currentStep$.set(2);
                }
            });
    }

    save(): void {
        this.form.markAllAsTouched();
        if (this.form.invalid) return;

        const { password, code } = this.form.value;
        const body: PostMfaActivation = {
            password,
            mfa: {
                type: 'EMAIL',
                code
            }
        };
        this.#entityUsersSrv.activateMfa$(
            this.data.userId,
            body
        ).pipe(
            take(1)
        ).subscribe({
            next: ({ state }: PostMfaActivationResponse) => {
                if (state === MfaActivationState.success) {
                    this.dialogRef.close({ success: true, msgKey: 'USERS.MFA.FORMS.FEEDBACKS.ACTIVATE_SUCCESS' });
                } else {
                    this.dialogRef.close({ success: false });
                    this.#msgDialogService.showAlert({
                        title: 'USERS.MFA.FORMS.ERRORS.ACTIVATE_FAIL',
                        message: 'USERS.MFA.FORMS.ERRORS.ACTIVATE_FAIL_MESSAGE',
                        actionLabel: 'FORMS.ACTIONS.AGREED',
                        size: DialogSize.SMALL
                    });
                }
            }
        });
    }

}
