import { IdName } from '@admin-clients/shared/data-access/models';

export interface OrderChangeSeat {
    enabled: boolean;
    order_event_change_seat_config?: {
        url: string;
        events: IdName[];
    }[];
}
