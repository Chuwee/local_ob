import { ChannelType } from './channel-type.model';

export interface PostChannelRequest {
    name: string;
    type: ChannelType;
    url?: string;
    entity: {
        id: number;
    };
    collective?: {
        id: number;
    };
}
