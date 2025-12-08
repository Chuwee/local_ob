import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { ChannelStatus } from './channel-status.model';
import { ChannelType } from './channel-type.model';

export class GetChannelsRequest implements PageableFilter {
    limit: number;
    offset: number;
    sort?: string;
    name?: string;
    q?: string;
    status?: ChannelStatus[] | null;
    type?: ChannelType | ChannelType[] | null;
    entityId?: number;
    entityAdminId?: number;
    operatorId?: number;
    includeThirdPartyChannels?: boolean;

    constructor() {
        this.limit = 20;
        this.offset = 0;
        this.status = [];
    }
}
