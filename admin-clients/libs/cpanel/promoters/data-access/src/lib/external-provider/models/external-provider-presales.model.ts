
export interface ExternalProviderPresales {
    id: string;
    name: string;
    description: string;
    date: Date;
    status: 'ACTIVE' | 'INACTIVE';
}

export interface ExternalProviderSessionsPresalesQuery {
    event_id: number;
    session_id: number;
    skip_used?: boolean;
}
