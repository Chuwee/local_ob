import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { MessageType } from '../models/message-type.model';
import { MessageDialogConfig, MessageDialogConfigWithSecondary, MessageDialogSecondaryValue } from './models/message-dialog.model';

@Component({
    imports: [
        CommonModule,
        MatButtonModule,
        MatDialogModule,
        MatIconModule,
        TranslatePipe,
        FlexLayoutModule
    ],
    selector: 'app-message-dialog',
    templateUrl: './message-dialog.component.html',
    styleUrls: ['./message-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MessageDialogComponent {
    readonly data = this.#getCompleteData(inject<MessageDialogConfig>(MAT_DIALOG_DATA));
    readonly #dialogRef = inject(MatDialogRef<MessageDialogComponent, boolean | MessageDialogSecondaryValue>);

    constructor() {
        const typeClass: string = Object.keys(MessageType).find(key => MessageType[key] === this.data.type);
        this.#dialogRef.addPanelClass([typeClass, this.data.size]);
    }

    close(success: boolean | MessageDialogSecondaryValue = false): void {
        success = this.data.invertSuccess ? !success : success;
        this.#dialogRef.close(success);
    }

    #getCompleteData(config: MessageDialogConfig): MessageDialogConfigWithSecondary {
        return {
            ...config,
            showSecondaryButton: 'showSecondaryButton' in config ? config.showSecondaryButton : undefined,
            secondaryActionLabel: 'secondaryActionLabel' in config ? config.secondaryActionLabel : undefined
        };
    }
}
