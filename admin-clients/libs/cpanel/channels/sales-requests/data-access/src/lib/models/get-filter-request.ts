import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetFilterRequest extends Omit<PageableFilter, 'offset' | 'sort' | 'aggs'> {

}
