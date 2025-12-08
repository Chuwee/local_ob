import { ContentImage } from '@admin-clients/shared/data-access/models';
import { EventChannelContentImageType } from './event-channel-content-image-type.enum';

export interface EventChannelContentImageRequest extends ContentImage<EventChannelContentImageType> {
    position?: number;
    image_url?: string;
}

export interface EventChannelContentImageRequestConfig {
    session_id: number;
    image_origin: string;
}
