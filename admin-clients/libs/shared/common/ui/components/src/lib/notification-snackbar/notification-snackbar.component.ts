import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { MessageType } from '../models/message-type.model';

export type NotificationSnackbarData = {
    type: string;
    msgKey: string;
    msgParams?: unknown;
    duration?: number;
    customBtn?: {
        icon: string;
        action: () => void;
        tooltip?: string;
    };
    hideCloseBtn?: boolean;
};

export type SnackbarDataWithoutType = Omit<NotificationSnackbarData, 'type'>;

@Component({
    imports: [
        NgClass,
        MatTooltipModule, MatIcon, MatIconButton,
        TranslatePipe,
        FlexLayoutModule
    ],
    selector: 'app-notification-snackbar',
    templateUrl: './notification-snackbar.component.html',
    styleUrls: ['./notification-snackbar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NotificationSnackbarComponent {
    typeClass: string;
    typeEnum = MessageType;

    constructor(
        @Inject(MAT_SNACK_BAR_DATA) public data: NotificationSnackbarData,
        private _snackbarRef: MatSnackBarRef<NotificationSnackbarComponent>
    ) {
        this.typeClass = Object.keys(MessageType).find(key => MessageType[key] === data.type);
    }

    close(): void {
        this._snackbarRef.dismiss();
    }
}
