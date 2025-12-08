import { VenueTemplateLabel } from '../../models/label-group/venue-template-label-group-list.model';

export interface VmQuotaCounter {
    label: VenueTemplateLabel;
    count: number;
}

export interface VmQuotaCounterSource extends VmQuotaCounter {
    disabled: boolean;
    initialCount?: number;
    available?: number;
}

export interface VmQuotaCounterTarget extends VmQuotaCounter {
    previousCount: number;
}
