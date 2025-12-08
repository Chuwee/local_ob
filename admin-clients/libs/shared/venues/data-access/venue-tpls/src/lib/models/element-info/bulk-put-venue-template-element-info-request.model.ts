import { PutVenueTemplateElementInfoRequest } from './put-venue-template-element-info-request.model';
import { VenueTemplateElementInfoType } from './venue-template-element-info-type.enum';

export interface BulkPutVenueTemplateElementInfoRequest {
    update_all_elements_info: boolean;
    element_info: PutVenueTemplateElementInfoRequest;
    elements_type_related_id_map: Record<VenueTemplateElementInfoType, number[]>;
}
