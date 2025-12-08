import { AggregationMetrics } from '@admin-clients/shared/data-access/models';

export const aggDataMatchings: AggregationMetrics = {
    totalListingsMapped: {
        addMetrics: ['total_matchings_mapped'],
        isCurrency: false,
        headerKey: 'MATCHINGS.TOTAL_MATCHINGS_MAPPED'
    },
    totalListingsMatched: {
        addMetrics: ['total_matchings_matched'],
        isCurrency: false,
        headerKey: 'MATCHINGS.TOTAL_MATCHINGS_MATCHED'
    },
    totalListingsCandidate: {
        addMetrics: ['total_matchings_candidate'],
        isCurrency: false,
        headerKey: 'MATCHINGS.TOTAL_MATCHINGS_CANDIDATE'
    },
    totalListingsNotRelated: {
        addMetrics: ['total_matchings_not_related'],
        isCurrency: false,
        headerKey: 'MATCHINGS.TOTAL_MATCHINGS_NOT_RELATED'
    },
    totalListings: {
        addMetrics: ['total_matchings'],
        isCurrency: false,
        headerKey: 'MATCHINGS.TOTAL_MATCHINGS'
    },
};
