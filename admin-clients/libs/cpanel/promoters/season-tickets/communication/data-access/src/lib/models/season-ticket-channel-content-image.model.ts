import { ContentImage } from '@admin-clients/shared/data-access/models';
import { SeasonTicketChannelContentImageType } from './season-ticket-channel-content-image-type.enum';

export interface SeasonTicketChannelContentImage extends ContentImage<SeasonTicketChannelContentImageType> {
    image_url?: string;
    position?: number;
}
