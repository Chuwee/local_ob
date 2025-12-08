export interface B2BPublishConfigurationRequest {
    enabled: boolean;
    published_seat_quota_id: number;
    published_seat_price_type?: {
        enabled: boolean;
        price_types_relations: {
            source_price_type_id: number;
            target_price_type_ids: number[];
        }[];
    }[];
}

export interface B2BPublishConfigurationResponse {
    enabled: boolean;
    published_seat_quota: {
        id: number;
        name: string;
        code: string;
    };
    published_seat_price_type: {
        enabled: boolean;
        price_types_relations: {
            source_price_type_id: number;
            target_price_type_ids: number[];
        }[];
    };
}