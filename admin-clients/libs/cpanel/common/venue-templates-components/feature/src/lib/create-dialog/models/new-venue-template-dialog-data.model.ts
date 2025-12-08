import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { NewVenueTemplateDialogMode } from './new-venue-template-dialog-mode.enum';

export interface NewVenueTemplateDialogData {
    mode: NewVenueTemplateDialogMode;
    event?: Event;
}
