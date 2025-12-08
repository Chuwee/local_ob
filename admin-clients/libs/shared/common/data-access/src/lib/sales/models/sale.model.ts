export interface Sale {
    id: number;
    name: string;
    activator?: {
        code?: string;
        collective?: { id: number; name: string };
    };
}
