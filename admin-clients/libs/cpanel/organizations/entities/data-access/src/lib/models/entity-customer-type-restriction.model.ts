
export interface EntityCustomerTypeRestrictions {
    entity_id: number;
    restrictions: EntityCustomerTypeRestriction[];
}

export interface EntityCustomerTypeRestriction {
    key: CustomerTypeRestrictions;
    restricted_customer_types: number[];
}

export const customerTypeRestrictions = ['LIST_TICKET_EXCHANGE', 'LOYALTY_POINTS', 'BUY_TICKET_EXCHANGE'] as const;
export type CustomerTypeRestrictions = typeof customerTypeRestrictions[number];
