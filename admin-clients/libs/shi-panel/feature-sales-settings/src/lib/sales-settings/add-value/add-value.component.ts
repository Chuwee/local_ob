import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { includedInArrayValidator } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    imports: [CommonModule, MaterialModule, TranslatePipe, FormControlErrorsComponent, ReactiveFormsModule, FlexLayoutModule],
    selector: 'app-add-value',
    templateUrl: './add-value.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewValueDialogComponent
    extends ObDialog<NewValueDialogComponent, { placeHolder: string; arrayValidator?: string[]; email?: boolean }, { newValue: string }> {
    readonly #fb = inject(FormBuilder);

    readonly placeHolder = this.data.placeHolder;
    readonly arrayValidator = this.data.arrayValidator;

    readonly form = this.#fb.group({
        new_value: [
            '' as string,
            this.arrayValidator ?
                [Validators.required, includedInArrayValidator(this.arrayValidator)] :
                this.data.email ?
                    [Validators.required, Validators.email] : [Validators.required]
        ]
    });

    constructor() {
        super(DialogSize.MEDIUM);
    }

    addValue(): void {
        if (this.form.valid) {
            this.close(this.form.value.new_value);
        } else {
            this.form.markAllAsTouched();
        }
    }

    close(value?: string): void {
        if (value) {
            this.dialogRef.close({ newValue: value });
        } else {
            this.dialogRef.close();
        }
    }
}
