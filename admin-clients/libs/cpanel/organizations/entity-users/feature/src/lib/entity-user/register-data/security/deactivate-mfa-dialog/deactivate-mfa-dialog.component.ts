import {
    EntityUsersService, MfaActivationErrorCodes, MfaActivationState,
    PostMfaActivation, PostMfaActivationResponse
} from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { take } from 'rxjs';

@Component({
    selector: 'ob-deactivate-mfa-dialog',
    templateUrl: './deactivate-mfa-dialog.component.html',
    styleUrl: './deactivate-mfa-dialog.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MaterialModule, TranslatePipe, FlexLayoutModule, AsyncPipe, ReactiveFormsModule]
})
export class DeactivateMfaDialogComponent
    extends ObDialog<DeactivateMfaDialogComponent, { userId: number | 'myself' }, { success: boolean; msgKey?: string }> {
    readonly #fb = inject(FormBuilder);
    readonly #entityUsersSrv = inject(EntityUsersService);

    readonly form = this.#fb.group({
        password: ['', Validators.required]
    });

    readonly isLoading$ = this.#entityUsersSrv.isMfaActivationLoading$();

    readonly $mfaErrorCodes = signal(MfaActivationErrorCodes);

    constructor() {
        super(DialogSize.MEDIUM, true);
    }

    close(): void {
        this.dialogRef.close();
    }

    save(): void {
        this.form.markAllAsTouched();
        if (this.form.invalid) return;

        const body: PostMfaActivation = {
            password: this.form.value.password,
            mfa: {
                type: 'DISABLED'
            }
        };
        this.#entityUsersSrv.activateMfa$(
            this.data.userId,
            body
        ).pipe(
            take(1)
        ).subscribe({
            next: ({ state, message: code }: PostMfaActivationResponse) => {
                if (state === MfaActivationState.success) {
                    this.dialogRef.close({ success: true, msgKey: 'USERS.MFA.FORMS.FEEDBACKS.DEACTIVATE_SUCCESS' });
                } else {
                    this.form.controls.password.setErrors({ [code]: true });
                }
            }
        });
    }

}
