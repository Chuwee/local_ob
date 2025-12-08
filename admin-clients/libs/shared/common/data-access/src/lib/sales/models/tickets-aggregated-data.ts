import { AggregationMetrics } from '@admin-clients/shared/data-access/models';

export const aggDataTicket: AggregationMetrics = {
    totalProducts: {
        addMetrics: ['total_products'],
        isCurrency: false,
        headerKey: 'TICKET.AGGREGATED_TICKETS'
    },
    totalBasePrice: {
        addMetrics: ['total_base_price'],
        isCurrency: true,
        headerKey: 'TICKET.AGGREGATED_BASE_PRICE'
    },
    totalPromotions: {
        addMetrics: ['total_promotion_manual', 'total_promotion_discount', 'total_promotion_auto',
            'total_order_promotion_auto', 'total_order_promotion_collective'],
        isCurrency: true,
        negated: true,
        headerKey: 'TICKET.AGGREGATED_PROMOTIONS'
    },
    totalCharges: {
        addMetrics: ['total_channel_charges', 'total_promoter_charges', 'total_ticket_reallocation_charges',
            'total_secondary_market_channel_charges', 'total_secondary_market_promoter_charges'],
        isCurrency: true,
        headerKey: 'TICKET.AGGREGATED_CHARGES'
    },
    totalFinalPrice: {
        addMetrics: ['total_final_price'],
        isCurrency: true,
        headerKey: 'TICKET.AGGREGATED_FINAL_PRICE'
    }
};

export const aggDataTicketWithoutCurrency: AggregationMetrics = {
    totalProducts: {
        addMetrics: ['total_products'],
        isCurrency: false,
        headerKey: 'TICKET.AGGREGATED_TICKETS'
    }
};
