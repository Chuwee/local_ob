
export interface PutProductEventSessionDeliveryPoints {
    id: number;
    delivery_points: { delivery_point_id: number; is_default: boolean }[];
}
