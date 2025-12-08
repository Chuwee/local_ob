export interface PostSeasonTicketRate {
    name: string;
    default: boolean;
    restrictive_access: boolean;
    enabled: boolean;
    texts?: {
        name: Map<string, string>;
    };
}
