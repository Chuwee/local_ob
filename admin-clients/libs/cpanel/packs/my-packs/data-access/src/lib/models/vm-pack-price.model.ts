import { UntypedFormControl } from '@angular/forms';

export interface VmPackPrice {
    initValue: number;
    rateId: number;
    priceTypeId: number;
    ctrl: UntypedFormControl;
}
