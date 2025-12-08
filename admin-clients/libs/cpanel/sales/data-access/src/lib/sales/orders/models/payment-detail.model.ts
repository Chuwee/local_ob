import { ReimbursementConstraints } from './reimbursement-constraints.model';
import { ReimbursementInfo } from './reimbursement-info.model';
import { VoucherType } from './voucher-type.enum';

export interface PaymentDetail {
    gateway: string;
    gateway_additional_info?: { [key: string]: Record<string, any>[] };
    reimbursement_constraints?: ReimbursementConstraints;
    reimbursements_info?: ReimbursementInfo[];
    voucher?: {
        group: { id: number; name: string; type: VoucherType };
        code: string;
    };
    payment_method: string;
}
