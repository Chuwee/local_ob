import { ExportRequest } from '@admin-clients/shared/data-access/models';
import { B2bClientTransactionType } from './b2b-client-transactions.model';

export interface B2bClientTransactionsExportReq extends ExportRequest {
    entity_id?: number;
    filter: {
        transaction_date_from: string;
        transaction_date_to: string;
        q?: string;
        type?: B2bClientTransactionType;
        currency_code?: string;
    };
    translations?: {
        key: string;
        value: string;
    }[];
}
export interface B2bClientTransactionsExportResponse {
    export_id: string;
}
