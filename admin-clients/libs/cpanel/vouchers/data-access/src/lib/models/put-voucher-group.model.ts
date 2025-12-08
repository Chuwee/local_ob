import { GiftCardGroupDateType } from './gift-card-group-date-type.enum';
import { VoucherChannelsType } from './voucher-channels-type.enum';
import { VoucherGroupStatus } from './voucher-group-status.enum';
import { VoucherGroupValidationMethod } from './voucher-group-validation-method.enum';

export interface PutVoucherGroup {
    name?: string;
    description?: string;
    status?: VoucherGroupStatus;
    validation_method?: VoucherGroupValidationMethod;
    expiration?: {
        type: GiftCardGroupDateType;
        date?: string;
        amount?: number;
        time_period?: number;
    };
    channels?: {
        scope: VoucherChannelsType;
        ids: number[];
    };
}
