import { EventChannelContentTextType } from './event-channel-content-text-type.enum';

export interface EventChannelContentText {
    language: string;
    type: EventChannelContentTextType;
    value?: string;
}
