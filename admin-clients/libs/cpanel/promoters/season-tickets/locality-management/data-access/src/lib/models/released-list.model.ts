import { ReleaseDataSessionStatus } from '@admin-clients/shared/common/data-access';

export interface ReleasedList {
    status: ReleaseDataSessionStatus;
    session: {
        id: string;
        name: string;
        start_date: string;
    };
    userId: string;
    order_code: string;
    product_id: number;
    release_date: string;
    ticket_data: {
        seat: string;
        row: string;
        sector: string;
        price_zone: string;
    };
    sold_price: number;
    total_gained: number;
}