import { AggregationMetrics } from '@admin-clients/shared/data-access/models';

export const aggDataListings: AggregationMetrics = {
    totalImported: {
        addMetrics: ['total_listings_imported'],
        isCurrency: false,
        headerKey: 'LISTINGS.LISTINGS_LIST.IMPORTED'
    },
    totalImportedWithError: {
        addMetrics: ['total_listings_imported_with_error'],
        isCurrency: false,
        isPercentage: true,
        headerKey: 'LISTINGS.LISTINGS_LIST.IMPORTED_WITH_ERROR'
    },
    totalDeleted: {
        addMetrics: ['total_listings_deleted'],
        isCurrency: false,
        headerKey: 'LISTINGS.LISTINGS_LIST.DELETED'
    },
    totalListings: {
        addMetrics: ['total_listings'],
        isCurrency: false,
        headerKey: 'LISTINGS.LISTINGS_LIST.TOTAL'
    }
};
