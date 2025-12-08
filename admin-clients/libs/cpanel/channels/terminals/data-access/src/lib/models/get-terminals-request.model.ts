import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { Terminal } from './terminal.model';

export interface GetTerminalsRequest extends Omit<PageableFilter, 'aggs' | 'sort'> {
    entity_id?: number;
    type?: Terminal['type'];
    license_enabled?: boolean;
}
