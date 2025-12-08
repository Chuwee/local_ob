import { VenueTemplateLabel } from '../../models/label-group/venue-template-label-group-list.model';

export interface SessionPackLinkDialogData {
    eventId: number;
    sessionId: number;
    label: VenueTemplateLabel;
    unrestrictedPack: boolean;
    items: {
        seats: Set<number>;
        nnzs: Set<number>;
    };
}
