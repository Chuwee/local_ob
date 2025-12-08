import { ListResponse } from '@OneboxTM/utils-state';

export enum CustomerLoyaltyPointsType {
    manualModification = 'MANUAL_MODIFICATION',
    reset = 'RESET',
    purchase = 'PURCHASE',
    purchaseCancellation = 'PURCHASE_CANCELLATION',
    purchaseRefund = 'PURCHASE_REFUND',
    refundToVoucher = 'REFUND_TO_VOUCHER',
    sessionTransfer = 'SESSION_TRANSFER',
    sessionAttendance = 'SESSION_ATTENDANCE',
    redeem = 'REDEEM',
    expiration = 'EXPIRATION'
}

export interface CustomerLoyaltyPointsResponse extends ListResponse<CustomerLoyaltyPoints> {
    total_points: number;
}

export interface CustomerLoyaltyPoints {
    points: number;
    date: string;
    description?: string;
    type: CustomerLoyaltyPointsType;
    expiration?: string;
}

export interface PostLoyaltyPoints {
    points: number;
    description: string;
}
