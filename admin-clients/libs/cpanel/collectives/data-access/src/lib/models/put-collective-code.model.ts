export interface PutCollectiveCode {
    validity_period?: {
        from?: string;
        to?: string;
    };
    usage_limit?: number;
}
