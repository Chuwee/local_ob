export interface CreatePackItemRequest {
    item_id: number;
    type: 'SESSION' | 'PRODUCT';
    display_item_in_channels: boolean;
    price_type_id?: number;
    variant_id?: number;
    delivery_point_id?: number;
    shared_barcode?: boolean;
    shared_stock?: boolean;
    price_type_mapping?: {
        source_price_type_id: number;
        target_price_type_id: number[];
    }[];
}
