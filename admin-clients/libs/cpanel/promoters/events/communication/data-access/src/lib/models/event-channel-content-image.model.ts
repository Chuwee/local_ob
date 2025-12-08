import { ContentImage } from '@admin-clients/shared/data-access/models';
import { EventChannelContentImageType } from './event-channel-content-image-type.enum';

export interface EventChannelContentImage extends ContentImage<EventChannelContentImageType> {
    image_url?: string;
    position?: number;
}
