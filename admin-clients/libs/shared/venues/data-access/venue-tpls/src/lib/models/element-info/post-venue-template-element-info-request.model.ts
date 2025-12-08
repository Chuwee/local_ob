import { VenueTemplateElementInfoDefaultInfo } from './venue-template-element-info-default-info.model';
import { VenueTemplateElementInfoType } from './venue-template-element-info-type.enum';

export interface PostVenueTemplateElementInfoRequest {
    default_info?: VenueTemplateElementInfoDefaultInfo;
    id?: number;
    type?: VenueTemplateElementInfoType;
    template_id?: number;
    status?: 'ENABLED' | 'DISABLED';
    copy_info?: {
        source: number;
        match_type: 'BY_CODE' | 'BY_NAME';
    };
}
