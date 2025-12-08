export interface Tax {
    id: number;
    name?: string;
    description?: string; // way to deprecation, don't use it
    value?: number;
    default?: boolean;
}
