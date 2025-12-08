import { ContentImage } from '@admin-clients/shared/data-access/models';

export interface SessionChannelImageRequest extends ContentImage {
    position?: number;
}
