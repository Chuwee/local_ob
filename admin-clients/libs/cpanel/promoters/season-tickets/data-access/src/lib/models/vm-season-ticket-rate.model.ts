import { UntypedFormControl } from '@angular/forms';
import { SeasonTicketRate } from './season-ticket-rate.model';

export interface VmSeasonTicketRate extends SeasonTicketRate {
    nameCtrl: UntypedFormControl;
}
