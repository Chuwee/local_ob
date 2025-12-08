import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { AUTHENTICATION_SERVICE } from '@admin-clients/shared/core/data-access';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, Output } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { takeWhile, tap } from 'rxjs/operators';

@Component({
    selector: 'app-mfa-code',
    templateUrl: './mfa-code.component.html',
    styleUrl: './mfa-code.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatFormField, MatInput, MatError, MatLabel, MatButton, MatProgressSpinner,
        FlexLayoutModule, ReactiveFormsModule, TranslatePipe, AsyncPipe, FormControlErrorsComponent
    ]
})
export class MfaCodeComponent {
    readonly #auth = inject(AUTHENTICATION_SERVICE);
    readonly #formBuilder = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);

    @Output() readonly trySubmit = new EventEmitter<void>();

    readonly loading$ = this.#auth.isTokenLoading$();
    readonly form: FormGroup<{ mfaCode: FormControl }> = this.#formBuilder.group({
        mfaCode: ['', Validators.required]
    });

    onSubmit(): void {
        this.form.controls.mfaCode.updateValueAndValidity();
        if (this.form.valid) {
            this.trySubmit.emit();
            this.loading$
                .pipe(
                    tap(loading => {
                        if (loading) {
                            this.form.disable();
                            this.form.controls.mfaCode.setValidators([]);
                        } else {
                            this.form.enable();
                            this.form.reset();
                            this.form.controls.mfaCode.setValidators([Validators.required]);
                        }
                    }),
                    takeWhile(Boolean),
                    takeUntilDestroyed(this.#destroyRef)
                )
                .subscribe();
        }
    }

    resendCode(): void {
        this.form.reset();
        this.trySubmit.emit();
        this.#ephemeralMessageService.showSuccess({ msgKey: 'LOGIN.MFA.RESEND.INFO' });
    }
}
