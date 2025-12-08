import { VoucherChannelsType } from './voucher-channels-type.enum';
import { VoucherLimitlessValue } from './voucher-limitlessValue.enum';

export interface PutGiftCardGroupConfig {
    amount?: number;
    expiration?: string;
    channels?: {
        scope: VoucherChannelsType;
        ids: number[];
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
