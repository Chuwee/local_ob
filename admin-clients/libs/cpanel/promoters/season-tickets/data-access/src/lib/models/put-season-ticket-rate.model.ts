export interface PutSeasonTicketRate {
    id: number;
    name?: string;
    default?: boolean;
    enabled?: boolean;
    restrictive_access?: boolean;
    position?: number;
    texts?: {
        name?: Record<string, string>;
    };
}
