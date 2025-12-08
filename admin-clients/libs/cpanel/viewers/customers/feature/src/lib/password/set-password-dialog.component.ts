import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    CustomersService, CustomerPasswordConditionsErrors,
    CustomerListItem, PostCustomerPasswordRequest
} from '@admin-clients/cpanel-viewers-customers-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import {
    checkPasswords,
    passwordValidator
} from '@admin-clients/shared/utility/utils';
import { KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-set-password-dialog',
    templateUrl: './set-password-dialog.component.html',
    styleUrls: ['./set-password-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [FlexLayoutModule, MaterialModule, ReactiveFormsModule, FormControlErrorsComponent, TranslatePipe, KeyValuePipe]
})
export class SetPasswordDialogComponent implements OnInit {
    private readonly _customersSrv = inject(CustomersService);
    private readonly _fb = inject(FormBuilder);
    private readonly _dialogRef = inject(MatDialogRef<SetPasswordDialogComponent, boolean>);
    private readonly _data = inject<{ customer: CustomerListItem }>(MAT_DIALOG_DATA);

    private _customerId: string = this._data.customer.id;
    private _entityId: number = this._data.customer.entity.id;

    form = this._fb.group({
        password: [null as string, [
            Validators.required,
            passwordValidator(/^(?=.{5,}$)/, 'noMinLength')
        ]],
        confirmPassword: [null, Validators.required]
    }, { validators: checkPasswords('password', 'confirmPassword') });

    isNewPwdBlur = false;
    passwordConditionErrors = CustomerPasswordConditionsErrors;

    ngOnInit(): void {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    close(isDone = false): void {
        this._dialogRef.close(isDone);
    }

    setPassword(): void {
        if (this.form.valid) {
            const request: PostCustomerPasswordRequest = { password: this.form.value.password };
            this._customersSrv.customerPassword.save(this._customerId, this._entityId, request).subscribe(() => this.close(true));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    onNewPwdBlur(): void {
        this.isNewPwdBlur = true;
    }
}
