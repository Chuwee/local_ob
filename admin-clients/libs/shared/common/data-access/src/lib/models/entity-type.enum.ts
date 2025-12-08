export const entityTypes = [
    'SUPER_OPERATOR',
    'OPERATOR',
    'ENTITY', 'VENUE_ENTITY',
    'EVENT_ENTITY',
    'CHANNEL_ENTITY',
    'MULTI_PRODUCER',
    'INSURANCER',
    'ENTITY_ADMIN'
] as const;

export type EntityType = typeof entityTypes[number];
