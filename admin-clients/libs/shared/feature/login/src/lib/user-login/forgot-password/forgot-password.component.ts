import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AUTHENTICATION_SERVICE } from '@admin-clients/shared/core/data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { animate, style, transition, trigger } from '@angular/animations';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, Output } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatFormField, MatError, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { LoginPages } from '../../models/login-pages.enum';

@Component({
    selector: 'app-forgot-password',
    templateUrl: 'forgot-password.component.html',
    styleUrls: ['./forgot-password.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('slideIn', [
            transition(':enter', [
                style({ transform: 'translateX(100%)' }),
                animate('360ms ease-in', style({ transform: 'translateX(0%)' }))
            ])
        ])
    ],
    imports: [
        MatFormField, MatInput, MatError, MatLabel, MatButton, MatProgressSpinner, MatIcon,
        ReactiveFormsModule, TranslatePipe, AsyncPipe, FormControlErrorsComponent
    ]
})
export class ForgotPasswordComponent {
    readonly #formBuilder = inject(UntypedFormBuilder);
    readonly #auth = inject(AUTHENTICATION_SERVICE);

    @Output() goToPage = new EventEmitter<LoginPages>();
    @Output() trySubmit = new EventEmitter<void>();

    form: UntypedFormGroup = this.#formBuilder.group({
        email: ['', [Validators.required, Validators.email]]
    });

    reqInProgress$ = booleanOrMerge([
        this.#auth.isForgotPwdLoading$()
    ]);

    email: string;
    submitted = false;
    loginPages = LoginPages;

    onSubmit(): void {
        if (this.form.valid) {
            this.#auth.forgotPassword(this.form.value.email)
                .subscribe(({ email }) => {
                    this.email = email;
                    this.submitted = true;
                });
        }
    }
}
