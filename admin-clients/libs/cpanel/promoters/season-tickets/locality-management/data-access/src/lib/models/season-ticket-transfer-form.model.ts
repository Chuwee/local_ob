import { FormControl, FormGroup } from '@angular/forms';

export interface SeasonTicketTransferForm {
    enable_transfer_delay: FormControl<boolean>;
    transfer_delay_type: FormControl<DelayType>;
    transfer_delay: FormGroup<{
        from: FormControl<number>;
        to: FormControl<number>;
        range: FormGroup<{
            min_delay_time: FormControl<number>;
            max_delay_time: FormControl<number>;
        }>;
    }>;
    enable_recovery_delay: FormControl<boolean>;
    recovery_ticket_max_delay_time: FormControl<number>;
    enable_max_ticket_transfers: FormControl<boolean>;
    enable_friends_family: FormControl<boolean>;
    max_ticket_transfers: FormControl<number>;
    loyalty_points: FormGroup<{}>;
    enable_bulk: FormControl<boolean>;
    bulk_customer_types: FormControl<number[]>;
}

export const delayType = ['FROM', 'TO', 'RANGE'] as const;
export type DelayType = typeof delayType[number];
