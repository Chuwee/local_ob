import { ChannelPurchaseContentImageType } from './channel-purchase-content-image-type.enum';

export interface ChannelPurchaseContentImage {
    language: string;
    type: ChannelPurchaseContentImageType;
    image_url?: string;
}

export interface ChannelPurchaseContentImageField {
    formField: string;
    type: ChannelPurchaseContentImageType;
    maxSize: number;
}
