import { FormControl, FormGroup } from '@angular/forms';

export interface InvoiceConfig {
    enabled: boolean;
    mandatory_thresholds?: Threshold[];
    // uncomment when back is ready
    // invoice_generation_mode?: 'MANUAL' | 'AUTOMATIC';
    invoice_request_type?: 'MANDATORY' | 'BY_AMOUNT';
};

export type Threshold = Partial<{ currency: string; amount: number | null }>;

export type ThresholdFormGroup = FormGroup<{
    currency: FormControl<string>;
    amount: FormControl<number | null>;
}>;
