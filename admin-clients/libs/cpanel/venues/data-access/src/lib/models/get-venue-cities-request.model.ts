export interface GetVenueCitiesRequest {
    limit: number;
    offset: number;
    sort: string; // "(name):(asc|desc)"
    entity_id: number;
    country_code: string;
    q: string;
    isThirdPartyVenuesIncluded: boolean;
}
