import { ContentImage, ImageRestrictions } from '@admin-clients/shared/data-access/models';

export enum EntityCommunicationElementImageType {
    headerBanner = 'HEADER_BANNER'
}

export const entityCommunicationElementImageRestrictions: Record<EntityCommunicationElementImageType, ImageRestrictions> = {
    [EntityCommunicationElementImageType.headerBanner]: { width: 670, height: 56, size: 54272 }
};

export interface EntityCommunicationElementImage extends ContentImage<EntityCommunicationElementImageType> {
    image: string;
    image_url: string;
    position?: number;
}
