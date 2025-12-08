/**
 * Tickets export fields
 *
 * These are in an specific order, to match the old cpanel order of fields
 * please keep them like this, any new added field put it down below
 */
export enum TicketsFields {
    code = 'order_code',
    event = 'event_name',
    session = 'session_name',
    sessionDate = 'session_date',
    purchaseDate = 'purchase_date',
    sector = 'sector',
    priceType = 'price_type',
    barcode = 'barcode',
    channel = 'channel_name',
    client = 'client',
    prints = 'prints',
    validation = 'validation',
    state = 'state',
    price = 'final_price',
    actions = 'actions',
    originMarket = 'origin_market'
}
