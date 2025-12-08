export interface AdditionalCondition {
    id?: number;
    name?: string;
    position?: number;
    mandatory?: boolean;
    enabled?: boolean;
    texts?: Record<string, string>;
}
