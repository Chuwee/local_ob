import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetFilterSessionDataRequest extends Omit<PageableFilter, 'sort' | 'aggs'> {
    session_id?: number[];
}
