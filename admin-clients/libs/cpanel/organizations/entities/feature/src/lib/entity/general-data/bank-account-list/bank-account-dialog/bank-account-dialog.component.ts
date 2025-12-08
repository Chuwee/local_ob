import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { type EntityBankAccount } from '@admin-clients/cpanel/organizations/entities/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { ibanValidator, noWhitespaceValidator, requiredLength } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-bank-account-dialog',
    templateUrl: './bank-account-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, ReactiveFormsModule, MatIcon, MatButton, MatDialogContent, MatDialogTitle, MatIconButton,
        MatDialogActions, MatFormField, MatInput, MatLabel, MatError, FormControlErrorsComponent
    ]
})
export class BankAccountDialogComponent extends ObDialog<BankAccountDialogComponent,
    { bankAccount: EntityBankAccount }, EntityBankAccount>
    implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<BankAccountDialogComponent, EntityBankAccount>);

    readonly form = this.#fb.group({
        name: [null as string, [Validators.required]],
        iban: [null as string, [Validators.required, ibanValidator()]],
        bic: [null as string, [Validators.required, Validators.maxLength(11)]],
        bankCode: [null as string, [Validators.required, requiredLength(4), noWhitespaceValidator()]],
        businessCode: [null as string, [Validators.required, requiredLength(3), noWhitespaceValidator()]],
        nif: [null as string, [Validators.required, noWhitespaceValidator(), Validators.maxLength(13)]]
    });

    constructor() {
        super(DialogSize.MEDIUM, false);
    }

    ngOnInit(): void {
        if (this.data?.bankAccount) {
            const values = this.data.bankAccount;
            this.form.patchValue({
                name: values.name,
                iban: values.iban,
                bic: values.bic,
                bankCode: values.cc.substring(0, 4),
                businessCode: values.cc.substring(4, 7),
                nif: values.cc.substring(7)
            });
        }
    }

    save(): void {
        if (this.form.valid) {
            const values = this.form.value;
            const bankCode = values.bankCode?.trim().padStart(4, '0');
            const businessCode = values.businessCode?.trim().padStart(3, '0');
            const nif = values.nif?.trim();

            this.#dialogRef.close({
                name: values.name,
                iban: values.iban,
                bic: values.bic,
                cc: bankCode + businessCode + nif
            });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
        }
    }

    close(): void {
        this.#dialogRef.close();
    }
}
