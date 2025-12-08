import { VenueTemplateView } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTplEditorViewLink } from './venue-tpl-editor-view-link.model';

export interface VenueTplEditorViewData {
    view: VenueTemplateView;
    links: VenueTplEditorViewLink[];
    create?: boolean;
    modify?: boolean;
    delete?: boolean;
}
