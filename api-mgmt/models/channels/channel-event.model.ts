import { IdName } from './common-types';
import { ChannelEventStatus } from './channel-event-status.enum';

export interface ChannelEvent {
    id: number;
    event: IdName & {
        start_date: string;
        end_date: string;
        currency?: string;
    };
    published: boolean;
    on_sale: boolean;
    status: ChannelEventStatus;
    catalog: {
        visible: boolean;
        position: number | null;
        carousel_position: number | null;
    };
    filtered?: boolean; // Aux: this is not from API!
}
