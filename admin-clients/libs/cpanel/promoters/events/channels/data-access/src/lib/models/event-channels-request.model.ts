import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { EventChannelReleaseStatus } from './event-channel-release-status.enum';
import { EventChannelRequestStatus } from './event-channel-request-status.enum';
import { EventChannelSaleStatus } from './event-channel-sale-status.enum';

export interface EventChannelsRequest extends PageableFilter {
    entity_id?: number;
    request_status?: EventChannelRequestStatus | EventChannelRequestStatus[];
    sale_status?: EventChannelSaleStatus;
    release_status?: EventChannelReleaseStatus;
    type?: ChannelType | ChannelType[];
}
