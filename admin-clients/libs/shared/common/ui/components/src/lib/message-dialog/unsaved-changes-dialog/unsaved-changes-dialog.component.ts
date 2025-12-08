
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { MessageType } from '../../models/message-type.model';
import { UnsavedChangesDialogConfig, UnsavedChangesDialogResult } from '../models/unsaved-changes-dialog.model';

@Component({
    imports: [
        CommonModule,
        MatButtonModule,
        MatDialogModule,
        MatIconModule,
        TranslatePipe,
        FlexLayoutModule
    ],
    selector: 'app-unsaved-changes-dialog',
    templateUrl: './unsaved-changes-dialog.component.html',
    styleUrls: ['./unsaved-changes-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class UnsavedChangesDialogComponent {

    constructor(
        private _dialogRef: MatDialogRef<UnsavedChangesDialogComponent, UnsavedChangesDialogResult>,
        @Inject(MAT_DIALOG_DATA) public data: UnsavedChangesDialogConfig
    ) {
        const typeClass: string = Object.keys(MessageType).find(key => MessageType[key] === data.type);
        _dialogRef.addPanelClass([typeClass, data.size]);
    }

    continueWithoutSaving(): void {
        this._dialogRef.close(UnsavedChangesDialogResult.continue);
    }

    save(): void {
        this._dialogRef.close(UnsavedChangesDialogResult.save);
    }

    cancelNavigation(): void {
        this._dialogRef.close(UnsavedChangesDialogResult.cancel);
    }

}
