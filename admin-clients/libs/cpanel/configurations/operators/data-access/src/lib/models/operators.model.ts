import { ListResponse } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { Operator, OperatorStatus } from './operator.model';

export class GetOperatorsRequest implements PageableFilter {
    limit: number;
    offset?: number;
    sort?: string; // "(date|num_items):(asc|desc)"
    q?: string;  // Wildcard filter
    id?: number;
    name?: string;
    shortName?: string;
    status?: OperatorStatus;
    fields?: string[];

    constructor() {
        this.limit = 20;
        this.offset = 0;
        this.q = '';
        this.id = null;
        this.name = '';
        this.shortName = '';
        this.status = null;
        this.fields = [];
    }
}

export interface GetOperatorsResponse extends ListResponse<Operator>{
}
