import { PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';

export interface VmSeasonTicketChannelPriceSimulationValue {
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
