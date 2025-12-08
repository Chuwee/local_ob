import { VenueTemplateLabel } from '../../models/label-group/venue-template-label-group-list.model';

export interface SeasonTicketLinkableDialogData {
    label: VenueTemplateLabel;
    items: {
        seats: Set<number>;
        nnzs: Set<number>;
    };
}
