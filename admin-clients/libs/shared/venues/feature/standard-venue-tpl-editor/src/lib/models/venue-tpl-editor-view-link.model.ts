import { VenueTemplateViewLink } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';

export interface VenueTplEditorViewLink extends VenueTemplateViewLink {
    create?: boolean;
    delete?: boolean;
}
