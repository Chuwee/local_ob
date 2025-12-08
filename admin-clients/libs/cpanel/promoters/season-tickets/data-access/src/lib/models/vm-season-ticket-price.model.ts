import { UntypedFormControl } from '@angular/forms';

export interface VmSeasonTicketPrice {
    initValue: number;
    rateId: number;
    priceTypeId: number;
    ctrl: UntypedFormControl;
    priority?: number;
}
