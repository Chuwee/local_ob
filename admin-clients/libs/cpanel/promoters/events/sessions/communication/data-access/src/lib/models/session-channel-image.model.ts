import { ContentImage } from '@admin-clients/shared/data-access/models';

export interface SessionChannelImage extends ContentImage {
    image_url: string;
    position?: number;
}
