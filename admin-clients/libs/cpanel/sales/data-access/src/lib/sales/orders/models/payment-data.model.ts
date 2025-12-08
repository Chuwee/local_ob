import { PaymentType } from './payment-type.enum';

export interface PaymentData {
    date: string;
    reference: string;
    type: PaymentType;
    value: number;
    merchant?: string;
    gateway_sid?: string;
    conf_sid?: string;
    gateway?: string;
}
