import { VenueTemplateElementInfoDefaultInfo } from './venue-template-element-info-default-info.model';
import { VenueTemplateElementInfoType } from './venue-template-element-info-type.enum';

export interface VenueTemplateElementInfo {
    id: number;
    type: VenueTemplateElementInfoType;
    name: string;
    code: string;
    status?: boolean;
    element?: {
        id: string;
        tag: string;
    };
}

export interface VenueTemplateElementInfoDetail {
    id: string;
    tag: string;
    status?: 'ENABLED' | 'DISABLED';
    default_info: VenueTemplateElementInfoDefaultInfo;
}
