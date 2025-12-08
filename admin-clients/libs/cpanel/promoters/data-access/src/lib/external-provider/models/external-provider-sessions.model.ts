export interface ExternalProviderSessions {
    id: string;
    name: string;
    description: string;
    date: Date;
    status: 'ACTIVE' | 'INACTIVE';
    standalone: boolean;
    external_properties: {
        inventory_id: string;
        group_id: string;
        status: string;
        external_venue_id: string;
    };
}

export interface ExternalProviderSessionsQuery {
    entity_id: number;
    event_id: number;
    status: 'ACTIVE' | 'INACTIVE';
    skip_used?: boolean;
}
