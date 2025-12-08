import { EventTierChannelContentType } from './event-tiers-type-channel-content.enum';

export interface EventTiersChannelContent {
    language: string;
    type: EventTierChannelContentType;
    value?: string;
}
