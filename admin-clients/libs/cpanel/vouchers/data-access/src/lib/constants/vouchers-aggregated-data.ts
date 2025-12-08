import { AggregationMetrics } from '@admin-clients/shared/data-access/models';

export const aggDataVouchers: AggregationMetrics = {
    codesCreated: {
        addMetrics: ['codes'],
        isCurrency: false,
        headerKey: 'VOUCHERS.CODES_CREATED'
    },
    balanceConsolidated: {
        addMetrics: ['balance'],
        isCurrency: true,
        headerKey: 'VOUCHERS.BALANCE_CONSOLIDATED'
    }
};
