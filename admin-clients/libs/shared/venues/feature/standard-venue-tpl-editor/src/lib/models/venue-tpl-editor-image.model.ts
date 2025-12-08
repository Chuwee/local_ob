import { VenueTemplateImage } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';

export interface VenueTplEditorImage extends VenueTemplateImage {
    create?: boolean;
    delete?: boolean;
    data?: string;
    fileName?: string;
}
