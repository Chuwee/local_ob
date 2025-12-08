import { VenueTemplatePriceTypeChannelContentType } from './venue-template-price-type-channel-content-type.enum';

export interface VenueTemplatePriceTypeChannelContent {
    language: string;
    type: VenueTemplatePriceTypeChannelContentType;
    value?: string;
}
