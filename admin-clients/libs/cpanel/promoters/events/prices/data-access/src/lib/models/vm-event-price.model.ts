import { UntypedFormControl } from '@angular/forms';
import { ActivityTicketType } from '@admin-clients/shared/venues/data-access/venue-tpls';

export interface VmEventPrice {
    initValue: number;
    rateId: number;
    priceTypeId: number;
    ctrl: UntypedFormControl;
    priority?: number;
    ticketType: ActivityTicketType;
}
