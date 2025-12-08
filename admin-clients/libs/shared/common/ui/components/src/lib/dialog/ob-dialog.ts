import { ComponentType } from '@angular/cdk/overlay';
import { inject, ViewContainerRef } from '@angular/core';
import {
    MAT_DIALOG_DATA, MatDialog, MatDialogRef
} from '@angular/material/dialog';
import { ObMatDialogConfig } from '../message-dialog/models/message-dialog.model';
import { DialogSize } from './models/dialog-size.enum';

/**
 * type C: Dialog component class
 * type D: model of the Dialog data
 * type R: Dialog return type
 * @prop {MatDialogRef<C, R>} dialogRef: The dialog instance reference
 * @prop {D} data: The data sent from outside when opening the dialog instance
 */
export abstract class ObDialog<C, D, R> {
    protected readonly dialogRef: MatDialogRef<C, R> = inject(MatDialogRef<C, R>);
    protected readonly data: D = inject(MAT_DIALOG_DATA);

    protected constructor(dialogSize?: DialogSize, disabledClose?: boolean) {
        if (dialogSize) {
            this.dialogRef.addPanelClass(dialogSize);
        }
        this.dialogRef.disableClose = disabledClose;
    }
}

export function openDialog<C, D, R>(dialogSrv: MatDialog,
    component: ComponentType<C & ObDialog<C, D, R>>,
    data?: D,
    viewContainerRef?: ViewContainerRef,
    // It sets a different size and position of the cdk-overlay-pane, 'dialog' - see ObMatDialogConfig implementation -.
    // Exclusively use of lateral dialogs at the moment. Don't overuse it.
    // Pending refactor if needed (more type of dialogs).
    // The size of the dialog should be defined by the dialog component in the constructor extending ObDialog class.
    dialogSize?: DialogSize
): MatDialogRef<C, R> {
    return dialogSrv.open<C, D, R>(component, new ObMatDialogConfig(data, viewContainerRef, dialogSize));
}
