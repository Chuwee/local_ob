import { PriceType, Rate } from '@admin-clients/cpanel/promoters/events/session-packs/data-access';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';

export interface PartialRefundTableElement {
    id: string;
    price_type?: PriceType;
    rate?: Rate;
    price?: number;
    ctrl: UntypedFormGroup; // used to validate the row
    type: 'PRICETYPE' | 'RATE' | 'SUPER';
    hidden?: boolean;
    expanded?: boolean;
    sessions?: Record<string, {
        id: number;
        ctrl: UntypedFormControl; // percentage value from control
        calculatedPercentualPrice?: number;
    }>;
}
