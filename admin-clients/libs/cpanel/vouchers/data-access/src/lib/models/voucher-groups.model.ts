import { GiftCardGroupDateType } from './gift-card-group-date-type.enum';
import { VoucherChannelsType } from './voucher-channels-type.enum';
import { VoucherGroupStatus } from './voucher-group-status.enum';
import { VoucherGroupType } from './voucher-group-type.enum';
import { VoucherGroupValidationMethod } from './voucher-group-validation-method.enum';

export interface VoucherGroup {
    id: number;
    name: string;
    description: string;
    status?: VoucherGroupStatus;
    validation_method?: VoucherGroupValidationMethod;
    type?: VoucherGroupType;
    entity: {
        id: number;
        name: string;
    };
    sales_config: number;
    channels?: {
        scope: VoucherChannelsType;
        items: { id: number; name: string }[];
    };
    expiration?: {
        type: GiftCardGroupDateType;
        date?: string;
        amount?: number;
        time_period?: number;
    };
    //TODO(MULTICURRENCY) when finished, it is required
    currency_code?: string;
}
