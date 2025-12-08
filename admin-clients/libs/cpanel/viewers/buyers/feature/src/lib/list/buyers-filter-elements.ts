export enum TicketDataType {
    event = 'events',
    eventAndSessions = 'sessions',
    eventAndPromos = 'promotions',
    sessionDates = 'sessionDates'
}

export enum OrderItemValueType {
    ticket = 'ticket',
    order = 'order',
    invitations = 'invitations'
}

export const buyerFilterElements = {
    entity: { key: 'ENTITY', param: 'entity' },
    sort: { key: 'SORT', param: 'sort' },
    pagination: { key: 'PAGINATION', param: 'pagination' },
    keyword: { key: 'KEYWORD', param: 'keyword' },
    orderCode: { key: 'ORDER_CODE', param: 'order_code' },
    barcode: { key: 'BARCODE', param: 'barcode' },
    gender: { key: 'GENDER', param: 'gender' },
    name: { key: 'NAME', param: 'name' },
    surname: { key: 'SURNAME', param: 'surname' },
    age: {
        param: 'age',
        from: { key: 'AGE_FROM', param: 'age_from' },
        to: { key: 'AGE_TO', param: 'age_to' }
    },
    country: { key: 'COUNTRY', param: 'country' },
    phone: { key: 'PHONE', param: 'phone' },
    email: { key: 'EMAIL', param: 'email' },
    countrySubdivision: { key: 'COUNTRY_SUBDIVISION', param: 'country_subdivision' },
    allowComercialMailing: { key: 'ALLOW_COMMERCIAL_MAILING', param: 'allow_commercial_mailing' },
    type: { key: 'TYPE', param: 'type' },
    subscriptionLists: { key: 'SUBSCRIPTION_LISTS', param: 'subscription_lists' },
    channels: { key: 'CHANNELS', param: 'channels' },
    collectives: { key: 'COLLECTIVES', param: 'collectives' },
    orderDate: {
        param: 'order_date',
        from: { key: 'ORDER_DATE_FROM', param: 'order_date_from' },
        to: { key: 'ORDER_DATE_TO', param: 'order_date_to' }
    },
    firstOrderDate: {
        param: 'first_order_date',
        from: { key: 'FIRST_ORDER_DATE_FROM', param: 'first_order_date_from' },
        to: { key: 'FIRST_ORDER_DATE_TO', param: 'first_order_date_to' }
    },
    withoutOrdersDate: {
        param: 'without_orders_date',
        from: { key: 'WITHOUT_ORDERS_DATE_FROM', param: 'without_orders_date_from' },
        to: { key: 'WITHOUT_ORDERS_DATE_TO', param: 'without_orders_date_to' }
    },
    ordersPurchased: {
        param: 'orders_purchased',
        from: { key: 'ORDERS_PURCHASED_FROM', param: 'orders_purchased_from' },
        to: { key: 'ORDERS_PURCHASED__TO', param: 'orders_purchased__to' }
    },
    itemsPurchased: {
        param: 'items_purchased',
        from: { key: 'ITEMS_PURCHASED_FROM', param: 'items_purchased_from' },
        to: { key: 'ITEMS_PURCHASED_TO', param: 'items_purchased_to' }
    },
    itemsRefunded: {
        param: 'items_refunded',
        from: { key: 'ITEMS_REFUNDED_FROM', param: 'items_refunded_from' },
        to: { key: 'ITEMS_REFUNDED_TO', param: 'items_refunded_to' }
    },
    presaleDays: {
        param: 'presale_days',
        from: { key: 'PRESALE_DAYS_FROM', param: 'presale_days_from' },
        to: { key: 'PRESALE_DAYS_TO', param: 'presale_days_to' }
    },
    ordersPurchasedPrice: {
        param: 'orders_purchased_price',
        from: { key: 'ORDERS_PURCHASED_PRICE_FROM', param: 'orders_purchased_price_from' },
        to: { key: 'ORDERS_PURCHASED_PRICE_TO', param: 'orders_purchased_price_to' }
    },
    ordersRefundedPrice: {
        param: 'orders_refunded_price',
        from: { key: 'ORDERS_REFUNDED_PRICE_FROM', param: 'orders_refunded_price_from' },
        to: { key: 'ORDERS_REFUNDED_PRICE_TO', param: 'orders_refunded_price_to' }
    },
    orderItemAvgPrice: {
        param: 'order_item_avg_price',
        from: { key: 'ORDER_ITEM_AVG_PRICE_FROM', param: 'order_item_avg_price_from' },
        to: { key: 'ORDER_ITEM_AVG_PRICE_TO', param: 'order_item_avg_price_to' }
    },
    ticketData: {
        param: 'ticketDataFilter',
        opts: TicketDataType,
        events: { key: 'EVENTS', param: 'events' },
        sessions: { key: 'SESSIONS', param: 'sessions' },
        promotions: { key: 'PROMOTIONS', param: 'promotions' },
        sessionDatesFrom: { key: 'SESSION_DATES_FROM', param: 'session_dates_from' },
        sessionDatesTo: { key: 'SESSION_DATES_TO', param: 'session_dates_to' }
    },
    orderItemValue: {
        param: 'order_item_value',
        opts: OrderItemValueType,
        basePriceFrom: { key: 'ORDER_ITEMS_BASE_PRICE_FROM', param: 'order_items_base_price_from' },
        basePriceTo: { key: 'ORDER_ITEMS_BASE_PRICE_TO', param: 'order_items_base_price_to' },
        finalPriceFrom: { key: 'ORDER_ITEMS_FINAL_PRICE_FROM', param: 'order_items_final_price_from' },
        finalPriceTo: { key: 'ORDER_ITEMS_FINAL_PRICE_TO', param: 'order_items_final_price_to' },
        invitations: { key: 'INVITATIONS', param: 'invitations' }
    }
};
