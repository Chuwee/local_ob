export interface ExternalProviderEvents {
    id: string;
    name: string;
    inventory_id: string;
    standalone: boolean;
}

export interface ExternalProviderEventsQuery {
    type: 'MEMBERS' | 'TICKETING';
    entity_id: number;
    venue_template_id: number;
    skip_used?: boolean;
}
