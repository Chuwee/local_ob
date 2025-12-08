import { VenueTemplateElementInfoDefaultInfo } from './venue-template-element-info-default-info.model';

export interface PutVenueTemplateElementInfoRequest {
    status?: 'ENABLED' | 'DISABLED';
    default_info?: VenueTemplateElementInfoDefaultInfo;
}
