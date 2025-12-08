export interface PackRate {
    id: number;
    name: string;
    default: boolean;
    restrictive_access: boolean;
}

export interface CreateRateRequest {
    name: string;
    default: boolean;
    restrictive_access: boolean;
}
