export interface SessionPriceType {
    id: number;
    name: string;
    additional_config: {
        restrictive_access: boolean;
        gate_id: number;
    };
}
