import { AggregationMetrics } from '@admin-clients/shared/data-access/models';

export const aggDataMobileLicenses: AggregationMetrics = {
    mobileUsersUsed: {
        addMetrics: ['mobile_used'],
        isCurrency: false,
        headerKey: 'BI_REPORTS.MOBILE_USERS.USED'
    },
    mobileUsersLimit: {
        addMetrics: ['mobile_limit'],
        isCurrency: false,
        headerKey: 'BI_REPORTS.MOBILE_USERS.LIMIT'
    }
};
