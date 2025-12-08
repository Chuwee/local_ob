
export interface PostDeliveryPoint {
    entity_id: number;
    name: string;
    location: {
        country: string;
        country_subdivision: string;
        city: string;
        address: string;
    };
}
