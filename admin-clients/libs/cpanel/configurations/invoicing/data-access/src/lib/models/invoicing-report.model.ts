import { InvoicingEntityOperatorTypes } from './invoicing-entity-operator-types.enum';

export interface PostInvoicingReport {
    user_id: number;
    operator_id: number;
    entities_id?: number[];
    entity_code?: string;
    event_ids?: number[];
    email: string;
    from: string;
    to: string;
    order_perspective: InvoicingEntityOperatorTypes;
}
