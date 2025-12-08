import { CopyTextComponent, DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';

@Component({
    selector: 'app-show-password-dialog',
    templateUrl: './show-password-dialog.component.html',
    styleUrls: ['./show-password-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        CopyTextComponent
    ]
})
export class ShowPasswordDialogComponent implements OnDestroy {
    private _onDestroy = new Subject<void>();

    password: string;

    constructor(
        private _dialogRef: MatDialogRef<ShowPasswordDialogComponent, void>,
        @Inject(MAT_DIALOG_DATA) private _data: { password: string }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this.password = _data.password;
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(): void {
        this._dialogRef.close();
    }
}
