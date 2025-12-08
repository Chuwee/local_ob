export interface EntitiesUsersLimitsResponse {
    bi: {
        basic: {
            used: number;
            limit: number;
            total: number;
        };
        advanced: {
            used: number;
            limit: number;
            total: number;
        };
        mobile: {
            used: number;
            limit: number;
        };
    };
}
