export const ticketOriginMarkets = ['PRIMARY', 'SECONDARY'] as const;
export type TicketOriginMarket = typeof ticketOriginMarkets[number];
