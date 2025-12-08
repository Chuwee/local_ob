import { SaleRequestGatewayBenefit } from '@admin-clients/cpanel-channels-sales-requests-data-access';

export interface VmSaleRequestGatewayBenefit extends Partial<SaleRequestGatewayBenefit> {
    beingModified?: {
        create?: boolean;
        edit?: boolean;
    };
    saved?: boolean;
    deleted?: boolean;
}

export type VmSaleRequestGatewayBenefitContentType = 'BADGE' | 'DESCRIPTION';

