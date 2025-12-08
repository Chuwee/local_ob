import { OperativeGrant } from '@admin-clients/shared/common/data-access';

export interface OrderOperativeGrant extends OperativeGrant {
    cancel: boolean;
    resend: boolean;
    resend_external_invoice: boolean;
    regenerate: boolean;
    voucher_refund: boolean;
    external_reimbursement: boolean;
    change_seat: boolean;
    /**
     * resend permissions to external access control
     */
    refresh_external_permissions?: boolean;
}
