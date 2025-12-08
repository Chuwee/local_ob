import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { MemberOrderState } from './member-order-state.enum';
import { MemberOrderType } from './member-order-type.enum';

export interface GetMemberOrdersRequest extends PageableFilter {
    entity_id?: string[];
    type?: MemberOrderType[];
    state?: MemberOrderState[];
    purchase_date_from?: string;
    purchase_date_to?: string;
    currency_code?: string;
}
