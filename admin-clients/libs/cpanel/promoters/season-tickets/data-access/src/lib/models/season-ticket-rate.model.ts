export interface SeasonTicketRate {
    id: number;
    name: string;
    default: boolean;
    enabled: boolean;
    position: number;
    restrictive_access: boolean;
    texts: {
        name: Record<string, string>;
    };
}
