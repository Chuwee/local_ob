import { OrderItemBase } from '@admin-clients/shared/common/data-access';

export interface VmCustomerSeasonTicketProduct extends OrderItemBase {
    seasonTicketName: string;
}
