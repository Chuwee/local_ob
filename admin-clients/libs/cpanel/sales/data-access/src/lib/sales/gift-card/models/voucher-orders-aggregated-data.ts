import { AggregationMetrics } from '@admin-clients/shared/data-access/models';

export const aggDataVoucherOrder: AggregationMetrics = {
    totalOperations: {
        addMetrics: ['total_operations'],
        isCurrency: false,
        headerKey: 'VOUCHER_ORDER.AGGREGATED_OPERATIONS'
    },
    totalProducts: {
        addMetrics: ['total_products'],
        isCurrency: false,
        headerKey: 'VOUCHER_ORDER.AGGREGATED_PRODUCTS'
    },
    totalFinalPrice: {
        addMetrics: ['total_final_price'],
        isCurrency: true,
        headerKey: 'VOUCHER_ORDER.AGGREGATED_FINAL_PRICE'
    }
};

export const aggDataVoucherOrderWithoutCurrency: AggregationMetrics = {
    totalOperations: {
        addMetrics: ['total_operations'],
        isCurrency: false,
        headerKey: 'VOUCHER_ORDER.AGGREGATED_OPERATIONS'
    },
    totalProducts: {
        addMetrics: ['total_products'],
        isCurrency: false,
        headerKey: 'VOUCHER_ORDER.AGGREGATED_PRODUCTS'
    }
};
