import { RangeElement } from '@admin-clients/shared-utility-models';

export enum ChannelSurchargeType {
    generic = 'GENERIC',
    promotion = 'PROMOTION',
    invitation = 'INVITATION'
}
export interface ChannelSurcharge {
    type: ChannelSurchargeType;
    enabled_ranges?: boolean;
    ranges: RangeElement[];
    allow_channel_use_alternative_charges?: boolean;
}
