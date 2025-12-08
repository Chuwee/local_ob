import { ChannelDeliveryMethodStatus } from './channel-delivery-method-status.enum';
import { ChannelDeliveryMethodTypes } from './channel-delivery-method-types.enum';

export interface ChannelDeliveryMethod {
    type: ChannelDeliveryMethodTypes;
    currencies: { cost: number; currency_code: string }[];
    default: boolean;
    status: ChannelDeliveryMethodStatus;
    active?: boolean;
    taxes?: {
        id: number;
    }[];
}
