import { ChannelGateway } from '@admin-clients/cpanel/channels/data-access';

export interface SaleRequestGateway {
    custom: boolean;
    channel_gateways: ChannelGateway[];
    benefits: boolean;
}

export interface SaleRequestGatewayBenefits {
    benefits: SaleRequestGatewayBenefit[];
}

export interface SaleRequestGatewayBenefit {
    type: 'INSTALLMENTS' | 'PRESALE';
    bin_groups: SaleRequestGatewayBenefitBinGroup[];
}

export interface SaleRequestGatewayBenefitBinGroup {
    bins: string[];
    installment_options?: number[];
    validity_period?: {
        start_date?: string;
        end_date?: string;
    };
    custom_valid_period?: boolean; // only for POST, it is not persisted,
    checkout_communication_elements?: {
        badge?: {
            text: Record<string, string>;
            background_color: string;
            text_color: string;
        };
        description?: Record<string, string>;
    };
}
