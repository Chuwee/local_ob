import { AggregationMetrics } from '@admin-clients/shared/data-access/models';

export const aggDataSales: AggregationMetrics = {
    totalSold: {
        addMetrics: ['total_sales_sold'],
        isCurrency: false,
        isOk: true,
        headerKey: 'SALES.SALES_LIST.TOTAL_SALES_SOLD'
    },
    totalSoldWithError: {
        addMetrics: ['total_sales_sold_with_error'],
        isCurrency: false,
        isError: true,
        isPercentage: true,
        headerKey: 'SALES.SALES_LIST.TOTAL_SALES_SOLD_WITH_ERROR'
    },
    totalFulfilled: {
        addMetrics: ['total_sales_fulfilled'],
        isCurrency: false,
        isFulfilled: true,
        headerKey: 'SALES.SALES_LIST.TOTAL_SALES_FULFILLED'
    },
    totalSales: {
        addMetrics: ['total_sales'],
        isCurrency: false,
        isTotal: true,
        headerKey: 'SALES.SALES_LIST.TOTAL_SALES'
    },
    totalPrice: {
        addMetrics: ['total_sales_price'],
        isCurrency: false,
        isTotalPrice: true,
        headerKey: 'SALES.SALES_LIST.TOTAL_PRICE'
    }
};
