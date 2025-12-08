import { AggregationMetrics } from '@admin-clients/shared/data-access/models';

export const aggDataOrder: AggregationMetrics = {
    totalOperations: {
        addMetrics: ['total_operations'],
        isCurrency: false,
        headerKey: 'ORDER.AGGREGATED_OPERATIONS'
    },
    totalTickets: {
        addMetrics: ['total_ticket_type_items'],
        isCurrency: false,
        headerKey: 'ORDER.AGGREGATED_TICKETS'
    },
    totalPrimaryMktTickets: {
        addMetrics: ['total_primary_mkt_ticket_type_items'],
        isCurrency: false,
        headerKey: 'ORDER.AGGREGATED_PRIMARY_MARKET_TICKETS'
    },
    totalSecMktTickets: {
        addMetrics: ['total_sec_mkt_ticket_type_items'],
        isCurrency: false,
        headerKey: 'ORDER.AGGREGATED_SECONDARY_MARKET_TICKETS'
    },
    totalProducts: {
        addMetrics: ['total_product_type_items'],
        isCurrency: false,
        headerKey: 'ORDER.AGGREGATED_PRODUCTS'
    },
    totalBasePrice: {
        addMetrics: ['total_base_price'],
        isCurrency: true,
        headerKey: 'ORDER.AGGREGATED_BASE_PRICE'
    },
    totalPromotions: {
        addMetrics: ['total_promotion_manual', 'total_promotion_discount', 'total_promotion_auto',
            'total_order_promotion_auto', 'total_order_promotion_collective'],
        isCurrency: true,
        negated: true,
        headerKey: 'ORDER.AGGREGATED_PROMOTIONS'
    },
    totalCharges: {
        addMetrics: [
            'total_channel_charges', 'total_promoter_charges', 'total_reallocation_charges', 'total_ticket_reallocation_charges',
            'total_secondary_market_channel_charges', 'total_secondary_market_promoter_charges'],
        isCurrency: true,
        headerKey: 'ORDER.AGGREGATED_CHARGES'
    },
    paymentMethodCharges: {
        addMetrics: ['total_gateway'],
        isCurrency: true,
        headerKey: 'ORDER.AGGREGATED_PAYMENT_METHOD_CHARGES'
    },
    totalDonations: {
        addMetrics: ['total_donations'],
        isCurrency: true,
        headerKey: 'ORDER.AGGREGATED_DONATIONS'
    },
    totalFinalPrice: {
        addMetrics: ['total_final_price'],
        isCurrency: true,
        headerKey: 'ORDER.AGGREGATED_FINAL_PRICE'
    }
};

export const aggDataOrderWithoutCurrency: AggregationMetrics = {
    totalOperations: {
        addMetrics: ['total_operations'],
        isCurrency: false,
        headerKey: 'ORDER.AGGREGATED_OPERATIONS'
    },
    totalTickets: {
        addMetrics: ['total_ticket_type_items'],
        isCurrency: false,
        headerKey: 'ORDER.AGGREGATED_TICKETS'
    },
    totalProducts: {
        addMetrics: ['total_product_type_items'],
        isCurrency: false,
        headerKey: 'ORDER.AGGREGATED_PRODUCTS'
    },
    totalPrimaryMktTickets: {
        addMetrics: ['total_primary_mkt_ticket_type_items'],
        isCurrency: false,
        headerKey: 'ORDER.AGGREGATED_PRIMARY_MARKET_TICKETS'
    },
    totalSecMktTickets: {
        addMetrics: ['total_sec_mkt_ticket_type_items'],
        isCurrency: false,
        headerKey: 'ORDER.AGGREGATED_SECONDARY_MARKET_TICKETS'
    }
};
