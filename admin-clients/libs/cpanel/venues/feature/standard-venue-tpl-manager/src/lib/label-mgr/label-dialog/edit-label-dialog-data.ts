import { VenueTemplateLabel } from '../../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../../models/label-group/venue-template-label-group-type.enum';

export interface EditLabelDialogData {
    templateId: number;
    isCreation: boolean;
    labelGroupType: VenueTemplateLabelGroupType;
    label?: VenueTemplateLabel;
    groupId?: number;
}
