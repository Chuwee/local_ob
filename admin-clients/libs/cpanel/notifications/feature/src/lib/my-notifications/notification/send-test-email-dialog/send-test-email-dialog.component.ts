import { NotificationsService } from '@admin-clients/cpanel/notifications/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { emailsValidator, maxEmailsExceedValidator } from '@admin-clients/shared/utility/utils';
import { NgIf } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit
} from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormBuilder, UntypedFormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';

@Component({
    selector: 'app-send-test-email-dialog',
    templateUrl: './send-test-email-dialog.component.html',
    styleUrls: ['./send-test-email-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [FlexModule, MaterialModule, FormsModule, ReactiveFormsModule, NgIf, TranslatePipe]
})
export class SendTestEmailDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    form: UntypedFormGroup;
    notCode: string;
    emailError: boolean;

    constructor(
        private _dialogRef: MatDialogRef<SendTestEmailDialogComponent>,
        private _notificationsService: NotificationsService,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) private _data: { notCode: string }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.notCode = _data.notCode;
    }

    ngOnInit(): void {
        this.form = this._fb.group({
            emails: [null, [
                Validators.required,
                emailsValidator(),
                maxEmailsExceedValidator()
            ]]
        });
        this.emailError = false;
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(): void {
        this._dialogRef.close();
    }

    submit(): void {
        let emails = this.form.get('emails').value.split(',');
        emails = emails.map(email => email.trim());

        this._notificationsService.sendTestNotification(this.notCode, emails).subscribe(() => {
            this._dialogRef.close(true);
        });
    }
}
