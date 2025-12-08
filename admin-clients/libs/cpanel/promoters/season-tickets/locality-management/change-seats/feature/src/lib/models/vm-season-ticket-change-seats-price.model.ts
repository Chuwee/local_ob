import { UntypedFormControl } from '@angular/forms';

export interface VmSeasonTicketChangeSeatsPrice {
    initValue: number;
    relationId: number;
    priceTypeId: number;
    ctrl: UntypedFormControl;
    priority?: number;
}
