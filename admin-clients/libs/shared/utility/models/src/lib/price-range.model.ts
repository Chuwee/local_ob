import { UntypedFormGroup } from '@angular/forms';

export interface RangeElement {
    from: number;
    values: {
        percentage?: number;
        fixed?: number;
        min?: number;
        max?: number;
    };
    //TODO(MULTICURRENCY): delete ? operator when the multicurrency functionality is finished
    currency_code?: string;
}

export interface RangeTableElement extends RangeElement {
    ctrl?: UntypedFormGroup;
}
