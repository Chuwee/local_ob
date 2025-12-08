import { ComponentType } from '@angular/cdk/overlay';
import { inject, Injectable, ViewContainerRef } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ObMatDialogConfig } from '../message-dialog/models/message-dialog.model';
import { ObDialog } from './ob-dialog';

/**
 * @deprecated Use ObDialog.openDialog function, it requires only a MatDialog reference
 */
@Injectable({ providedIn: 'root' })
export class ObDialogService {

    private _matDialog = inject(MatDialog);

    constructor() { }

    open<C, D, R>(
        component: ComponentType<C & ObDialog<C, D, R>>,
        data?: D,
        viewContainerRef?: ViewContainerRef
    ): MatDialogRef<C, R> {
        return this._matDialog.open<C, D, R>(component, new ObMatDialogConfig(data, viewContainerRef));
    }
}
