import { PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';

export interface VmEventChannelPriceSimulationMapped {
    [key: number]: VmEventChannelPriceSimulationValue[];
}

export interface VmEventChannelPriceSimulationValue {
    priceTypes?:
        {
            base: number;
            channelSurcharges: number;
            priceTypeName: string;
            promoterSurcharges: number;
            total: number;
            promotions?: {
                type: PromotionType;
                name: string;
            }[];
        }[];
    rateName: string;
}
