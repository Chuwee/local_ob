import { ContentImage } from '@admin-clients/shared/data-access/models';
import { GiftCardGroupContentImageType } from './gift-card-group-content-image-type.enum';

export interface GiftCardGroupContentImage extends ContentImage<GiftCardGroupContentImageType> {
    image_url?: string;
}
