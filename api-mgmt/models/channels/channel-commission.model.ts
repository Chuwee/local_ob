import { RangeElement } from './common-types';

export enum ChannelCommissionType {
    generic = 'GENERIC',
    promotion = 'PROMOTION'
}

export interface ChannelCommission {
    type: ChannelCommissionType;
    enabled_ranges?: boolean;
    ranges: RangeElement[];
}
