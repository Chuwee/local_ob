export interface PostEventRate {
    name: string;
    default: boolean;
    restrictive_access: boolean;
    external_rate_type_id?: number;
    texts?: {
        name: Map<string, string>;
    };
}
