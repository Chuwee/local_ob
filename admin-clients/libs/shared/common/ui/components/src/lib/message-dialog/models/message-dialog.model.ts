import { ViewContainerRef } from '@angular/core';
import { MatDialogConfig } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { DialogSize } from '../../dialog/models/dialog-size.enum';
import { MessageType } from '../../models/message-type.model';
import { SnackbarDataWithoutType } from '../../notification-snackbar/notification-snackbar.component';

export class ObMatDialogConfig<D = unknown> extends MatDialogConfig<D> {

    constructor(data: D = null, viewContainerRef?: ViewContainerRef, dialogSize?: DialogSize) {
        super();
        if (viewContainerRef) {
            this.viewContainerRef = viewContainerRef;
        }
        this.panelClass = 'ob-dialog';
        this.disableClose = true;
        if (dialogSize === DialogSize.LATERAL) {
            this.maxHeight = '100vh';
            this.position = { right: '0' };
        } else {
            this.maxHeight = '90vh';
        }
        this.maxWidth = '90vw';
        this.autoFocus = false;
        this.data = data;
    }
}

export type MessageDialogConfig = MessageDialogConfigBase | MessageDialogConfigWithSecondary;

export interface MessageDialogConfigBase {
    type?: MessageType;
    size?: DialogSize;
    title?: string;
    message: string;
    messageParams?: unknown;
    subMessages?: string[];
    actionLabel?: string;
    showCancelButton?: boolean;
    cancelLabel?: string;
    invertSuccess?: boolean;
}

export interface MessageDialogConfigWithSecondary extends MessageDialogConfigBase {
    showSecondaryButton: boolean;
    secondaryActionLabel?: string;
}

export type MessageDialogSecondaryValue = 'secondary';

export type DeleteConfirmationConfig<T> = {
    confirmation?: Partial<MessageDialogConfig>;
    success?: SnackbarDataWithoutType;
    delete$: Observable<T>;
};
