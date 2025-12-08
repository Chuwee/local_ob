import { ChannelContentFieldsRestrictions } from './channel-content-fields-restrictions.enum';
import { ChannelPurchaseContentTextType } from './channel-purchase-content-text-type.enum';

export interface ChannelPurchaseContentText {
    language: string;
    type: ChannelPurchaseContentTextType;
    redirect_url?: string;
}

export interface ChannelPurchaseContentTextField {
    formField: string;
    type: ChannelPurchaseContentTextType;
    maxLength?: ChannelContentFieldsRestrictions;
}
