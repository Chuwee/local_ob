import { ChannelStatus } from './channel-status.model';
import { ChannelType } from './channel-type.model';

export interface ChannelListElement {
    id: number;
    name: string;
    url: string;
    status: ChannelStatus;
    entity: {
        id: number;
        name: string;
        logo: string;
    };
    type: ChannelType;
}
