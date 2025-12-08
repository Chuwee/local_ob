import { ReleaseAction } from '@admin-clients/shared/common/data-access';
import { CustomerFriends } from './customer-friends.model';
import { VmCustomerSeasonTicketProduct } from './vm-customer-seat-management-season-ticket-product.model';
import { VmCustomerSession } from './vm-customer-seat-management-session.model';

export interface CustomerSeatManagementDialogData {
    readonly session: VmCustomerSession;
    readonly selectedSeat: VmCustomerSeasonTicketProduct;
    readonly action?: ReleaseAction;
    readonly emailSubmit?: boolean;
    readonly friends?: CustomerFriends[];
    readonly transferPolicy?: 'ALL' | 'FRIENDS_AND_FAMILY';
}
