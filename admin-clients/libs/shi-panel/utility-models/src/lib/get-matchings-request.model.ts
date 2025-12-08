import { IdName } from '@admin-clients/shared/data-access/models';
import { GetBlacklistedMatchingsRequest } from './get-blacklisted-matchings.model';
import { MatchingStatus } from './matching.status';

export interface GetMatchingsRequest extends GetBlacklistedMatchingsRequest {
    status?: MatchingStatus[];
    shi_taxonomies?: IdName[];
    supplier_taxonomies?: IdName[];
    includeAggs?: boolean;
}
