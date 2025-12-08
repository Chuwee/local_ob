import { AggregationMetrics } from '@admin-clients/shared/data-access/models';

export const aggDataEntitiesLicenses: AggregationMetrics = {
    advancedUsersLimit: {
        addMetrics: ['advanced'],
        isCurrency: false,
        headerKey: 'BI_REPORTS.ADVANCED_USERS.TITLE'
    },
    basicUsersLimit: {
        addMetrics: ['basic'],
        isCurrency: false,
        headerKey: 'BI_REPORTS.BASIC_USERS.TITLE'
    },
    totalUsersUsed: {
        addMetrics: ['total'],
        isCurrency: false,
        headerKey: 'AGGREGATED_METRIC.METRIC.TOTAL'
    }
};
