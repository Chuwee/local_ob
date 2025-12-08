export interface SessionRate {
    id: number;
    name: string;
    default: boolean;
    rate_group?: {
        id: number;
        name: string;
    };
}
