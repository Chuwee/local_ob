import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import {
    MAT_DIALOG_DATA,
    MatDialogModule,
    MatDialogRef
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { CurrencyInputComponent } from '../../currency-input/currency-input.component';
import { DialogSize } from '../../dialog/models/dialog-size.enum';

export type NewRangeDialogInput = { currency: string; currencyFormat: 'narrow' | 'wide' };

@Component({
    selector: 'app-new-range-dialog',
    templateUrl: './new-range-dialog.component.html',
    styleUrls: ['./new-range-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, FlexLayoutModule,
        MatDialogModule, MatIcon, MatButton, MatIconButton, MatFormFieldModule,
        TranslatePipe, CurrencyInputComponent, LocalCurrencyPipe
    ]
})
export class NewRangeDialogComponent implements OnInit, AfterViewInit {
    private readonly _dialogRef = inject<MatDialogRef<NewRangeDialogComponent>>(MatDialogRef);

    @ViewChild(CurrencyInputComponent) private readonly _input: CurrencyInputComponent;

    readonly form = inject(FormBuilder).group({
        from: [null, [Validators.required, Validators.min(0)]]
    });

    readonly currencyFormat = inject<NewRangeDialogInput>(MAT_DIALOG_DATA).currencyFormat;
    readonly currency = inject<NewRangeDialogInput>(MAT_DIALOG_DATA).currency;

    ngOnInit(): void {
        this._dialogRef.addPanelClass(DialogSize.SMALL);
        this._dialogRef.disableClose = false;
    }

    ngAfterViewInit(): void {
        // focus input on start to improve UX
        setTimeout(() => this._input.focus());
    }

    close(): void {
        this._dialogRef.close();
    }

    submit(): void {
        if (this.form.valid) {
            this._dialogRef.close(this.form.value.from);
        }
    }

}
