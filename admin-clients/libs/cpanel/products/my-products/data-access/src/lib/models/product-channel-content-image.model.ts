import { ContentImage } from '@admin-clients/shared/data-access/models';

export interface ProductChannelImageContent extends ContentImage<ProductChannelContentImageType> {
    image: string;
    image_url: string;
    position: number;
};

export enum ProductChannelContentImageType {
    landscape = 'LANDSCAPE'
}
