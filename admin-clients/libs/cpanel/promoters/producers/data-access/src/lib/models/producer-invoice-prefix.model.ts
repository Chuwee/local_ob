import { ListResponse } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { Producer } from './producer.model';

export interface ProducerInvoicePrefix {
    id: number;
    producer: Pick<Producer, 'id' | 'name'>;
    prefix: string;
    suffix: string;
    default: boolean;
}

export interface GetProducerInvoicePrefixes extends ListResponse<ProducerInvoicePrefix> {
}

export type PostProducerInvoicePrefix = Pick<ProducerInvoicePrefix, 'prefix'>;
export type RequestProducerInvoicePrefixes = Pick<PageableFilter, 'limit' | 'offset'>;
export type PutProducerInvoicePrefix = Pick<ProducerInvoicePrefix, 'default'>;
