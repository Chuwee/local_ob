import { NotNumberedZone } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplate } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ZoneActionType } from './venue-tpl-tree-dialog-type.enum';

export interface VenueTemplateZoneDialogData {
    action: ZoneActionType;
    venueTemplate: VenueTemplate;
    zone: NotNumberedZone;
    capacityEditable: boolean;
}
