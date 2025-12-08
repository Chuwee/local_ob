export interface RatesExternalType {
    id: number;
    name: string;
    code: string;
}

export interface Rate {
    id: number;
    name: string;
    default: boolean;
    restrictive_access: boolean;
    position?: number;
    enabled?: boolean;
    texts: {
        name: { [key: string]: string };
    };
    external_rate_type?: RatesExternalType;
}
