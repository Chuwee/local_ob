import { DialogSize } from '../../dialog/models/dialog-size.enum';
import { MessageType } from '../../models/message-type.model';

export enum UnsavedChangesDialogResult {
    cancel = 'cancel',
    save = 'save',
    continue = 'continue'
}

export interface UnsavedChangesDialogConfig {
    type?: MessageType;
    size: DialogSize;
    title: string;
    message: string;
    messageParams?: unknown;
}
