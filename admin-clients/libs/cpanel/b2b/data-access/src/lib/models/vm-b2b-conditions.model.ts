import {
    B2BClientDiscountCurrency,
    B2bGroupType, BookingExpirationDaysMode, ClientDiscountMode, PaymentMethodType
} from './b2b-condition.model';

interface VmB2bConditionsBase {
    canBuy: boolean;
    canBook: boolean;
    canPublish?: boolean;
    canInvite?: boolean;
    maxSeats: number;
    bookingExpirationDays: number;
    clientComission: number;
    paymentMethods: PaymentMethodType[];
    condHierarchicalLevel: B2bGroupType;
    showTicketPrice: boolean;
    showTicketClientDiscount: boolean;
}

export interface VmB2bConditions extends VmB2bConditionsBase {
    // solo se usan en el form de condiciones
    bookingExpirationMode: BookingExpirationDaysMode;
    clientDiscountMode: ClientDiscountMode;
    clientDiscountFixed: {
        value: number;
        currencies: B2BClientDiscountCurrency[];
    };
    clientDiscountPercent: number;
}

export interface VmB2bConditionsClient extends VmB2bConditionsBase {
    id: number;
    name: string;
    clientDiscount: number; // solo se usa en el listado de forma unificada
    modified: boolean;
}

export interface VmEditPromoterConditionsData {
    context: 'EVENT' | 'SEASON_TICKET';
    contextId: number;
    contextCurrency: string;
    selectedClients?: VmB2bConditionsClient[];
}

/* eslint-disable @typescript-eslint/naming-convention */
export enum ConditionsToVmConditionsMap {
    CAN_BUY = 'canBuy',
    CAN_BOOK = 'canBook',
    CAN_PUBLISH = 'canPublish',
    CAN_INVITE = 'canInvite',
    MAX_SEATS_PER_EVENT = 'maxSeats',
    BOOKING_EXPIRATION_DAYS = 'bookingExpirationDays',
    CLIENT_DISCOUNT = 'clientDiscountFixed',
    CLIENT_DISCOUNT_PERCENTAGE = 'clientDiscountPercent',
    CLIENT_COMMISSION = 'clientComission',
    PAYMENT_METHODS = 'paymentMethods',
    SHOW_TICKET_PRICE = 'showTicketPrice',
    SHOW_TICKET_CLIENT_DISCOUNT = 'showTicketClientDiscount'
}
/* eslint-enable @typescript-eslint/naming-convention */
