import { Currency } from '@admin-clients/shared-utility-models';

export interface Operator {
    id: number;
    name: string;
    short_name: string;
    language: {
        id: number;
        name: string;
        code: string;
    };
    shard: string;
    currency: Currency;
    currencies: {
        default_currency: string;
        selected: Currency[];
    };
    timezone: {
        id: number;
        code: string;
    };
    gateways: string[];
    wallets: WalletAssociation[];
    allow_fever_zone?: boolean;
    allow_gateway_benefits?: boolean;
}

export type WalletAssociation = {
    wallet: string;
    gateways: string[];
};

export enum OperatorStatus {
    active = 'ACTIVE',
    pending = 'PENDING',
    blocked = 'BLOCKED',
    tempBlocked = 'TEMPORARY_BLOCKED'
}

export interface PostOperator {
    name: string;
    short_name: string;
    currency_code?: string;
    olson_id?: string;
    shard?: string;
    language_code?: string;
    gateways?: string[];
}

export interface PutOperator {
    name?: string;
    currency_code?: string;
    olson_id?: string;
    language_code?: string;
    gateways?: string[];
    wallets?: WalletAssociation[];
    allow_fever_zone?: boolean;
    allow_gateway_benefits?: boolean;
    allow_hard_tickets_pdf?: boolean;
}

export interface GetOperatorCurrencies {
    id: number;
    code: string;
}

export interface PutOperatorCurrencies {
    currency_codes?: string[];
}
