import { Metadata } from '@OneboxTM/utils-state';

export interface ProductEventSessionDeliveryPoint {
    id: number;
    name: string;
    dates: {
        start: string;
        end: string;
    };
    delivery_points: [
        {
            id: number;
            name: string;
            is_default: boolean;
        }
    ];
}

export interface GetProductEventSessionsDeliveryPointsResponse {
    data: ProductEventSessionDeliveryPoint[];
    metadata: Metadata;
}
