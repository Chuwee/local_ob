import { VoucherChannelsType } from './voucher-channels-type.enum';
import { VoucherLimitlessValue } from './voucher-limitlessValue.enum';

export interface GiftCardGroupConfig {
    amount?: number;
    expiration?: string;
    channels?: {
        scope: VoucherChannelsType;
        items: {
            id: number;
            name: string;
        }[];
    };
    usage_limit?: {
        type: VoucherLimitlessValue;
        value: number;
    };
    price_range?: {
        from: number;
        to: number;
    };
}
