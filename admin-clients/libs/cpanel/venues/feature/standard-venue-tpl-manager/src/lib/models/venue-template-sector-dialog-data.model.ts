import { Sector } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplate } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { SectorActionType } from './venue-tpl-tree-dialog-type.enum';

export interface VenueTemplateSectorDialogData {
    action: SectorActionType;
    venueTemplate: VenueTemplate;
    sector: Sector;
}
