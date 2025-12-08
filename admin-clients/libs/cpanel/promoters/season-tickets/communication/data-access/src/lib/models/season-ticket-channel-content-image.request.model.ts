import { ContentImage } from '@admin-clients/shared/data-access/models';
import { SeasonTicketChannelContentImageType } from './season-ticket-channel-content-image-type.enum';

export interface SeasonTicketChannelContentImageRequest extends ContentImage<SeasonTicketChannelContentImageType> {
    position?: number;
}
