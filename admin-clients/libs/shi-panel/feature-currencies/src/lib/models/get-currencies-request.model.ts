import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetCurrenciesRequest extends PageableFilter {
    source?: string;
    target?: string;
    lastUpdate?: Date;
}
