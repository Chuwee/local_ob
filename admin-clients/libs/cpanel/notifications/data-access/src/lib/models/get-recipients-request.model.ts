import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetRecipientsRequest extends PageableFilter {
    entity_id: number;
    event_id: number[];
    session_id?: number[];
    channel_id?: number[];
    purchase_from?: string;
    purchase_to?: string;
    exclude_commercial_mailing_not_allowed?: boolean;
}
