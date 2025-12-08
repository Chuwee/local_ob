import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export type EntityZoneTemplate = {
    id: number;
    name: string;
    code: string;
    status: ZoneTemplateStatus;
    contents_texts: CommunicationTextContent[];
    showImagesCarousel: boolean;
    whitelabel_settings: {
        modules: ZoneTemplateThanksModule[];
    };
};

export type PutEntityZoneTemplate = {
    name: string;
    status: ZoneTemplateStatus;
    contents_texts: CommunicationTextContent[];
    showImagesCarousel: boolean;
    whitelabel_settings: {
        modules: ZoneTemplateThanksModule[];
    };
};

export interface GetZoneTemplatesRequest extends PageableFilter {
    status?: ZoneTemplateStatus;
}

export enum ZoneTemplateStatus {
    enabled = 'ENABLED',
    disabled = 'DISABLED'
}

export type ZoneTemplateThanksModule = {
    blockId: number;
    enabled: boolean;
    visible: boolean;
    type?: 'MAIN' | 'SMARTBOOKING';
};
