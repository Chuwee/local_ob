import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatIcon } from '@angular/material/icon';
import { MessageType } from '../models/message-type.model';

@Component({
    imports: [
        NgClass,
        MatIcon,
        FlexLayoutModule
    ],
    selector: 'app-context-notification',
    templateUrl: './context-notification.component.html',
    styleUrls: ['./context-notification.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContextNotificationComponent {

    private _messageType: MessageType;

    iconColorStyle: string;
    @Input() iconFxFlexAlign: 'center';

    @Input()
    set contextType(value: keyof typeof MessageType) {
        this._messageType = MessageType[value];
        this.iconColorStyle = Object.keys(MessageType).find(key => MessageType[key] === this._messageType);
    }

    get messageType(): MessageType {
        return this._messageType;
    }
}
