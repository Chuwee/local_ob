/**
 * Orders export fields
 *
 * These are in an specific order, to match the old cpanel order of fields
 * please keep them like this, any new added field put it down below
 */
export enum OrdersFields {
    code = 'code',
    channel = 'channel',
    type = 'type',
    date = 'date',
    client = 'client',
    event = 'event',
    ticketsCount = 'tickets_count',
    productsCount = 'products_count',
    basePrice = 'base_price',
    promotions = 'promotions',
    charges = 'charges',
    donation = 'donation',
    finalPrice = 'final_price',
    actions = 'actions',
    delivery = 'delivery',
    priceGateway = 'price_gateway',
    internationalPhonePrefix = 'buyer_data.international_phone.prefix',
    internationalPhoneNumber = 'buyer_data.international_phone.number',
    reallocationRefund = 'reallocation_refund'
}
