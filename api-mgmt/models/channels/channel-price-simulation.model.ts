import { PromotionType } from './common-types';

export enum ChannelSurchargeSimulationType {
    channel = 'CHANNEL',
    promoter = 'PROMOTER'
}

export interface ChannelPriceVenueTemplate {
    id: string;
    name: string;
}

export interface ChannelPriceSimulation {
    venue_template: ChannelPriceVenueTemplate;
    rates: {
        id: number;
        name: string;
        price_types: {
            id: number;
            name: string;
            simulations: {
                price: {
                    surcharges: {
                        type: ChannelSurchargeSimulationType;
                        value: number;
                    }[];
                    base: number;
                    total: number;
                };
                promotions: {
                    type: PromotionType;
                    name: string;
                }[];
            }[];
        }[];
    }[];
}
