import { BuyersQueryDef } from './buyers-query-def.model';
import { BuyersQuery } from './buyers-query.model';

export interface BuyersQueryWrapper extends BuyersQueryDef {
    query: BuyersQuery;
}
