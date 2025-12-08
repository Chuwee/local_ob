import { VoucherGroupType } from './voucher-group-type.enum';
import { VoucherGroupValidationMethod } from './voucher-group-validation-method.enum';

export interface PostVoucherGroup {
    entity_id: number;
    //TODO MULTICURRENCY MANDATORY WHEN MIGRATION IS DONE
    currency_code?: string;
    name: string;
    description: string;
    type: VoucherGroupType;
    validation_method: VoucherGroupValidationMethod;
}
