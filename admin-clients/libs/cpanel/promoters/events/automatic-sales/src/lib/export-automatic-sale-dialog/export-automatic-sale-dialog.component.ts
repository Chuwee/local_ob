import { eventChannelsProviders } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { AUTHENTICATION_SERVICE } from '@admin-clients/shared/core/data-access';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';

@Component({
    selector: 'app-export-automatic-sale-dialog',
    templateUrl: './export-automatic-sale-dialog.component.html',
    styleUrls: ['./export-automatic-sale-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule,
        CommonModule, FlexLayoutModule
    ],
    providers: [
        eventChannelsProviders
    ]
})
export class ExportAutomaticSaleDialogComponent
    extends ObDialog<
        ExportAutomaticSaleDialogComponent,
        void,
        string
    >
    implements OnInit, OnDestroy {

    private readonly _onDestroy: Subject<void> = new Subject();
    private readonly _authService = inject(AUTHENTICATION_SERVICE);
    private readonly _fb = inject(FormBuilder);

    readonly form = this._fb.group({
        receipt_email: [null as string, [Validators.required, Validators.email]]
    });

    constructor() {
        super(DialogSize.MEDIUM);
        this.dialogRef.addPanelClass('no-padding');
    }

    ngOnInit(): void {
        this._authService.getLoggedUser$().subscribe(user => this.form.controls.receipt_email.patchValue(user?.email));

    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(): void {
        this.dialogRef.close();
    }

    save(): void {
        this.form.updateValueAndValidity();
        if (this.form.valid) {
            this.dialogRef.close(this.form.value.receipt_email);
        }
    }

}
