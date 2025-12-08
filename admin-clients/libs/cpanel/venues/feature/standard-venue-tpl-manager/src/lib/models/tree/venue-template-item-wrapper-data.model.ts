import { NotNumberedZone, Seat, SeatStatus } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { SafeHtml } from '@angular/platform-browser';
import { VenueTemplateLabel, VenueTemplateLabelGroup } from '../label-group/venue-template-label-group-list.model';

export interface VenueTemplateItemWrapperDataModel {
    statusLabelsMap?: Map<SeatStatus, { color: string; icon: SafeHtml }>;
    blockingReasonsMap?: Map<string, VenueTemplateLabel>;
    selectedLabelGroup?: VenueTemplateLabelGroup;
    selectedLabelGroupLabels?: Map<string, VenueTemplateLabel>;
    seatIdentifierFunction?: (seat: Seat) => string;
    nnzIdentifierFunction?: (zone: NotNumberedZone) => string;
    selectedSeats?: Set<number>;
    selectedZones?: Set<number>;
    locale?: string;

    canCloneSectors?: boolean;
    canEditSectors?: boolean;
    canDeleteSectors?: boolean;

    canCloneNNZ?: boolean;
    canEditNNZ?: boolean;
    canDeleteNNZ?: boolean;
}
