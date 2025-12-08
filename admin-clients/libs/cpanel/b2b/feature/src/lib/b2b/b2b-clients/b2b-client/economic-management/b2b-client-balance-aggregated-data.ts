import { AggregationMetrics } from '@admin-clients/shared/data-access/models';

export const b2bClientBalanceAggMetrics: AggregationMetrics = {
    currentBalance: {
        addMetrics: ['balance'],
        isCurrency: true,
        headerKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.BALANCE'
    },
    creditLimit: {
        addMetrics: ['credit_limit'],
        isCurrency: true,
        headerKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.CREDIT_LIMIT'
    },
    debt: {
        addMetrics: ['debt'],
        isCurrency: true,
        headerKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.DEBT'
    },
    totalAvailable: {
        addMetrics: ['total_available'],
        isCurrency: true,
        headerKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.TOTAL_AVAILABLE'
    }
};
