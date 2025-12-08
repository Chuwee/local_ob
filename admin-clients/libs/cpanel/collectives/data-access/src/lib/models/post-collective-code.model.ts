export interface PostCollectiveCode {
    code: string;
    key?: string;
    validity_period?: {
        from?: string;
        to?: string;
    };
    usage_limit?: number;
}
