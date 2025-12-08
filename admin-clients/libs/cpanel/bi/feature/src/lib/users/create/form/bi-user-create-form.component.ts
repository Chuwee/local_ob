import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { BiUser } from '@admin-clients/cpanel/bi/data-access';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-bi-user-create-form',
    templateUrl: './bi-user-create-form.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, FormControlErrorsComponent, ReactiveFormsModule, MatInput,
        MatFormField, MatError, MatLabel
    ]
})
export class BiUserCreateFormComponent {
    readonly #fb = inject(FormBuilder);

    readonly form = this.#fb.group({
        name: ['', [Validators.required]],
        last_name: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.email]]
    });

    getFormValue(): Partial<BiUser> | null {
        if (this.form.valid) {
            return this.form.getRawValue();
        }

        this.form.markAllAsTouched();
        return null;
    }
}
