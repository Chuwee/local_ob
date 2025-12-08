import { Metadata } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { Weekdays } from '@admin-clients/shared-utility-models';

export interface GetProductEventSessionsStockAndPricesResponse {
    data: ProductEventSessionStockAndPrices[];
    metadata: Metadata;
}

export interface GetSessionsStockRequest extends PageableFilter {
    initStartDate?: string;
    finalStartDate?: string;
    initEndDate?: string;
    finalEndDate?: string;
    weekdays?: Weekdays[];
    status?: ['EDITED', 'UNEDITED'];
}

export interface ProductEventSessionStockAndPrices {
    id: number;
    name: string;
    dates: {
        start: string;
        end: string;
    };
    variants?: {
        id: number;
        use_custom_stock?: boolean;
        stock?: number;
        use_custom_price?: boolean;
        price?: number;
    }[];
    //TODO: Delete this when refactor is done
    stock?: number;
    use_custom_stock?: boolean;
    smart_booking?: {
        type: 'SMART_BOOKING' | 'SEAT_SELECTION';
    }
}

export interface GetSessionsPriceRequest extends PageableFilter {
    initStartDate?: string;
    finalStartDate?: string;
    initEndDate?: string;
    finalEndDate?: string;
    weekdays?: Weekdays[];
    status?: ['EDITED', 'UNEDITED'];
}

export interface PutProductEventSessionsStockAndPrices {
    variants?: {
        id: number;
        use_custom_stock?: boolean;
        stock?: number;
        use_custom_price?: boolean;
        price?: number;
    }[];
    //TODO: Delete this when refactor is done
    stock?: number;
    use_custom_stock?: boolean;
}
