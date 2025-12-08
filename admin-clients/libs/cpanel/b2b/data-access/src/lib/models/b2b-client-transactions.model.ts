import { ListResponse } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetB2bClientTransactionsResponse extends ListResponse<B2bClientTransaction> {
}

export interface B2bClientTransaction {
    id: string; // movement ID
    transaction_code: string; // set by the user as free text when adding a movement
    order_code: string; // purchase locator
    created: string;
    channel: string;
    user: string;
    notes: string;
    transaction_type: B2bClientTransactionType;
    deposit_type?: B2bClientDepositType;
    previous_balance: number;
    amount: number;
    balance: number;
    credit: number;
    debt: number;
    effective_date: Date;
}

export interface GetB2bClientTransactionsRequest extends PageableFilter {
    entity_id?: number;
    transaction_date_from?: string;
    transaction_date_to?: string;
    type?: B2bClientTransactionType;
    currency_code?: string;
}

export enum B2bClientTransactionType {
    purchase = 'PURCHASE',
    creditLimit = 'CREDIT_LIMIT',
    cashAdjustment = 'CASH_ADJUSTMENT',
    refund = 'REFUND',
    deposit = 'DEPOSIT'
}

export enum B2bClientDepositType {
    cash = 'CASH',
    depositType = 'TRANSFER',
    check = 'CHECK'
}
