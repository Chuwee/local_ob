import { Category, EntityCategory } from '@admin-clients/shared/common/data-access';

export interface PutPack {
    name?: string;
    active?: boolean;
    pack_period?: {
        type: 'AUTOMATIC' | 'CUSTOM';
        start_date?: string;
        end_date?: string;
    };
    ui_settings?: {
        show_date?: boolean;
        show_date_time?: boolean;
        show_main_venue?: boolean;
        show_main_date?: boolean;
    };
    pricing?: {
        type: 'COMBINED' | 'INCREMENTAL' | 'NEW_PRICE';
        price_increment?: number;
    };
    settings?: {
        categories?: {
            base?: {
                id?: number;
                code?: string;
                description?: string;
            };
            custom?: Category | EntityCategory;
        };
    };
}

export interface AddPackItems {
    id: number;
    type: 'SESSION' | 'PRODUCT';
}
