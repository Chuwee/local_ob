import { B2bClientDepositType } from './b2b-client-transactions.model';

export interface B2bClientOperation {
    amount: number;
    currency_code?: string;
    entity_id?: number;
    notes?: string;
    effective_date: Date;
    additional_info?: {
        deposit_transaction_id?: string;
        deposit_type: B2bClientDepositType;
    };
}

export enum B2bClientOperationType {
    creditLimit = 'CREDIT_LIMIT',
    cashAdjustment = 'CASH_ADJUSTMENT',
    deposit = 'DEPOSIT'
}
