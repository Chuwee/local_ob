import { BaseContentImage } from '@admin-clients/shared/data-access/models';

export interface CustomerContentImage extends Omit<BaseContentImage<CustomerContentImageType>, 'language'> {
    image_url?: string;
}

export interface CustomerContentImageField {
    formField: string;
    type: CustomerContentImageType;
    maxSize: number;
}

export interface PutCustomerContentImage {
    type?: CustomerContentImageType;
    image?: string;
    image_url?: string;
}

export type CustomerContentImageType = 'AVATAR';
