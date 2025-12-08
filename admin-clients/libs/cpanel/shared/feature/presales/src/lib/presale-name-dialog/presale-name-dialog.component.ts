import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { Presale } from '@admin-clients/cpanel/shared/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { notEmpty } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-presale-name-dialog',
    templateUrl: './presale-name-dialog.component.html',
    imports: [TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent, MatIconModule, MatFormFieldModule,
        MatInputModule, MatDialogModule, MatButtonModule, PrefixPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PresaleNameDialogComponent {
    private readonly _dialogRef = inject(MatDialogRef<PresaleNameDialogComponent>);
    private readonly _fb = inject(FormBuilder);
    readonly presale = inject<Presale>(MAT_DIALOG_DATA);
    readonly form = this._fb.group({
        name: [{ value: this.presale?.name, disabled: false }, [Validators.required, Validators.maxLength(50), notEmpty()]]
    });

    constructor() {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    close(): void {
        this._dialogRef.close();
    }

    submit(): void {
        if (this.form.valid) {
            this._dialogRef.close({ name: this.form.value.name });
        }
    }

}
