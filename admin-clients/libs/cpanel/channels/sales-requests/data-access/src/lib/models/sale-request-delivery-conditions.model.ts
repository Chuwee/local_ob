export enum TicketHandlingOptions {
    useChannelConfig = 'USE_CHANNEL_CONFIG',
    avoidTicket = 'AVOID_TICKET'
}

export interface SaleRequestDeliveryConditions {
    ticket_handling: TicketHandlingOptions;
}
