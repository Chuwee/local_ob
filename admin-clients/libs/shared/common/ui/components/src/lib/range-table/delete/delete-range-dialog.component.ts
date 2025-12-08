import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { DialogSize } from '../../dialog/models/dialog-size.enum';

export enum ResizeRangeResult {
    resizePrevious = 'prev',
    resizeNext = 'next'
}

@Component({
    selector: 'app-delete-range-dialog',
    templateUrl: './delete-range-dialog.component.html',
    styleUrls: ['./delete-range-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [ReactiveFormsModule, FlexLayoutModule, MatButtonModule, MatDialogModule, MatIconModule, TranslatePipe,
        MatRadioGroup, MatRadioButton]
})
export class DeleteRangeDialogComponent {
    readonly resizeRangeResult = ResizeRangeResult;

    form: FormControl<ResizeRangeResult> = new FormControl(ResizeRangeResult.resizePrevious);

    constructor(
        private _dialogRef: MatDialogRef<DeleteRangeDialogComponent, ResizeRangeResult | boolean>,
        @Inject(MAT_DIALOG_DATA) public data: { range: string; previousRange?: string; nextRange?: string }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    close(): void {
        this._dialogRef.close(false);
    }

    submit(): void {
        this._dialogRef.close(this.form.value);
    }

}
