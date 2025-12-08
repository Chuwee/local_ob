
export type B2bConditionsGroupType = 'OPERATOR' | 'ENTITY' | 'EVENT';
export type B2bConditionsClientsGroupType = 'ENTITY' | 'EVENT';
export type B2bGroupType = B2bConditionsGroupType | B2bConditionsClientsGroupType | 'CLIENT_B2B' | 'CLIENT_B2B_EVENT';

type BoolConditionType = 'CAN_BUY' | 'CAN_BOOK' | 'CAN_PUBLISH' | 'CAN_INVITE' | 'SHOW_TICKET_PRICE' | 'SHOW_TICKET_CLIENT_DISCOUNT';
type NumConditionType = 'MAX_SEATS_PER_EVENT' | 'BOOKING_EXPIRATION_DAYS' | 'CLIENT_DISCOUNT_PERCENTAGE' | 'CLIENT_COMMISSION';
type PaymentMethodsConditionType = 'PAYMENT_METHODS';
type ClientDiscount = 'CLIENT_DISCOUNT';
export type ConditionType = BoolConditionType | NumConditionType | PaymentMethodsConditionType | ClientDiscount;

export type B2bCondition = {
    condition_type: BoolConditionType;
    value: boolean;
} | {
    condition_type: NumConditionType;
    value: number;
} | {
    condition_type: PaymentMethodsConditionType;
    value: PaymentMethodType[];
} | {
    condition_type: ClientDiscount;
    value: number;
    currencies?: B2BClientDiscountCurrency[];
};

export enum PaymentMethodType {
    channelTpv = 'CHANNEL_TPV',
    clientBalance = 'CLIENT_BALANCE'
}

export enum BookingExpirationDaysMode {
    fromEvent = 'FROM_EVENT',
    customDays = 'CUSTOM_DAYS'
}

export enum ClientDiscountMode {
    fixed = 'FIXED',
    percent = 'PERCENTAGE'
}

export interface B2BClientDiscountCurrency {
    value: number;
    currency_code: string;
}
