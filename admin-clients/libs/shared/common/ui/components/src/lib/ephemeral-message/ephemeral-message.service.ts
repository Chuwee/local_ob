import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { MessageType } from '../models/message-type.model';
import {
    NotificationSnackbarComponent, NotificationSnackbarData, SnackbarDataWithoutType
} from '../notification-snackbar/notification-snackbar.component';

const panelClass = ['ob-snackbar'];

@Injectable({ providedIn: 'root' })
export class EphemeralMessageService {

    constructor(private _snackBar: MatSnackBar) { }

    showSaveSuccess(): void {
        this.showSuccess({ msgKey: 'FORMS.FEEDBACK.SAVE_SUCCESS' });
    }

    showCreateSuccess(): void {
        this.showSuccess({ msgKey: 'FORMS.FEEDBACK.CREATE_SUCCESS' });
    }

    showDeleteSuccess(): void {
        this.showSuccess({ msgKey: 'FORMS.FEEDBACK.DELETE_SUCCESS' });
    }

    showSuccess(data: SnackbarDataWithoutType): void {
        this.show({ type: MessageType.success, ...data });
    }

    show(data: NotificationSnackbarData): void {
        const config: MatSnackBarConfig = { panelClass, data };
        if (data.duration != null) {
            config.duration = data.duration;
        }
        this._snackBar.openFromComponent(NotificationSnackbarComponent, config);
    }
}
