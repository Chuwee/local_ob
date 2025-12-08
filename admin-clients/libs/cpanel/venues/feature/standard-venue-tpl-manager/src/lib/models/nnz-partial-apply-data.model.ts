import { NotNumberedZone } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateLabel } from './label-group/venue-template-label-group-list.model';

export interface NnzPartialApplyData {
    label: VenueTemplateLabel;
    nnz: NotNumberedZone;
}
