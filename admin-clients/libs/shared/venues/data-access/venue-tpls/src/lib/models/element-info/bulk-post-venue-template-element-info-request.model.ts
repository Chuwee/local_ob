import { VenueTemplateElementInfoDefaultInfo } from './venue-template-element-info-default-info.model';

export interface BulkPostVenueTemplateElementInfoRequest {
    update_all_elements_info: boolean;
    default_info: VenueTemplateElementInfoDefaultInfo;
}
