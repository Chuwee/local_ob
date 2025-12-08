import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetEventChannelSessionLinksRequest extends PageableFilter {
    eventId?: number;
    channelId?: number;
    language?: string;
    session_status?: string[];
    fields?: string[];
}
