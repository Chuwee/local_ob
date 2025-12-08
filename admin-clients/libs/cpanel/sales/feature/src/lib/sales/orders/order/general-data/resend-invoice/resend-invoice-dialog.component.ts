import { OrdersService } from '@admin-clients/cpanel-sales-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './resend-invoice-dialog.component.html',
    standalone: false
})
export class ResendInvoiceDialogComponent {
    private readonly _matDialogData = inject<{ code: string; buyerEmail: string }>(MAT_DIALOG_DATA);
    private readonly _ordersSrv = inject(OrdersService);
    private readonly _dialogRef = inject(MatDialogRef<ResendInvoiceDialogComponent>);

    readonly isInProgress$ = this._ordersSrv.invoice.loading$();
    readonly form = inject(FormBuilder).group({
        email: new FormControl<string>(
            this._matDialogData.buyerEmail, { validators: [Validators.required, Validators.email] })
    });

    constructor() {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    close(): void {
        this._dialogRef.close();
    }

    resend(): void {
        this._ordersSrv.invoice.resend(this._matDialogData.code, this.form.value.email).subscribe(() =>
            this._dialogRef.close(true));
    }
}
