/**
 * Buyers export fields
 *
 * These are in an specific order, to match the old cpanel order of fields
 * please keep them like this, any new added field put it down below
 */
export enum BuyerFields {
    email = 'email',
    name = 'name',
    surname = 'surname',
    gender = 'gender',
    birthDate = 'date_of_birth',
    docType = 'identity_card.type',
    doc = 'identity_card.id',
    fixPhone = 'phone.fix',
    mobilePhone = 'phone.mobile',
    country = 'location.country.code',
    countrySubdivision = 'location.country_subdivision.code',
    city = 'location.city',
    zipCode = 'location.zip_code',
    address = 'location.address',
    creationDate = 'date.create',
    modificationDate = 'date.last_update',
    purchaseCount = 'total_purchases',
    ticketCount = 'total_tickets',
    productCount = 'total_products',
    purchaseAmount = 'sum_price',
    avgAmount = 'avg_price',
    avgDaysBeforeDateBuyed = 'avg_days_before_date_buyed',
    refundedItemsCount = 'total_refunded_items',
    refundAmount = 'sum_refunded_price',
    firstPurchaseDate = 'first_purchase',
    lastPurchaseDate = 'last_purchase',
    allowCommercialMailing = 'allow_commercial_mailing',
    subscriptionList = 'subscription_lists',
    collectives = 'collectives',
    channels = 'channels',
    type = 'type',
    actions = 'actions'
}
