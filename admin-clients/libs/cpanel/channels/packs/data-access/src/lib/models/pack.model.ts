import { IdName } from '@admin-clients/shared/data-access/models';

export interface Pack {
    id: number;
    name: string;
    active: boolean;
    type: 'MANUAL' | 'AUTOMATIC';
    channel_id: number;
    has_sales: boolean;
    promotion?: {
        enabled: boolean;
        promotion_id: number;
    };
    pack_period?: {
        type: 'AUTOMATIC' | 'CUSTOM';
        start_date?: string;
        end_date?: string;
    };
    pricing?: {
        type: 'COMBINED' | 'INCREMENTAL' | 'NEW_PRICE';
        price_increment?: number;
    };
    ui_settings?: {
        show_date: boolean;
        show_date_time: boolean;
        show_main_venue: boolean;
        show_main_date: boolean;
    };
}

export interface PackItem {
    id: number;
    item_id: number;
    name: string;
    type: 'SESSION' | 'PRODUCT' | 'EVENT';
    main: boolean;
    display_item_in_channels: boolean;
    informative_price?: number;
    session_data?: SessionData;
    product_data?: ProductData;
    event_data?: EventData;
    editing?: boolean;
}

interface ProductData {
    delivery_point: IdName;
    shared_barcode: boolean;
    shared_stock: boolean;
    variant: IdName;
}
interface SessionData {
    event: IdName;
    dates?: {
        start: string;
        end: string;
    };
    venue_template: {
        id: number;
        name: string;
        venue: IdName;
    };
    price_type?: IdName;
    price_type_mapping?: {
        source_price_type: IdName;
        target_price_type: IdName;
    }[];
}
interface EventData {
    venue_template: {
        id: number;
        name: string;
        venue: IdName;
    };
}
