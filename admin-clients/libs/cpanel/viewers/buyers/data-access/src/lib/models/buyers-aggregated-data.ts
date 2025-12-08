import { AggregationMetrics } from '@admin-clients/shared/data-access/models';

export const aggDataBuyers: AggregationMetrics = {
    totalOrders: {
        addMetrics: ['total_orders'],
        isCurrency: false,
        headerKey: 'ORDER.ORDERS'
    },
    totalOrderItems: {
        addMetrics: ['total_order_items'],
        isCurrency: false,
        headerKey: 'ORDER.TICKETS'
    },
    totalOrderTickets: {
        addMetrics: ['total_order_tickets'],
        isCurrency: false,
        headerKey: 'ORDER.TICKETS'
    },
    totalOrderProducts: {
        addMetrics: ['total_order_products'],
        isCurrency: false,
        headerKey: 'ORDER.PRODUCTS'
    },
    totalOrderItemsPrice: {
        addMetrics: ['total_order_items_price'],
        isCurrency: true,
        headerKey: 'BUYERS.TOTAL_PURCHASE_VALUE'
    },
    avgOrderItemsPrice: {
        addMetrics: ['avg_order_items_price'],
        isCurrency: true,
        headerKey: 'BUYERS.AVG_TICKET_VALUE'
    },
    avgPurchaseDays: {
        addMetrics: ['avg_purchase_days_before_session_date'],
        isCurrency: false,
        headerKey: 'BUYERS.AVG_PURCHASE_TIME'
    }
};
