import { FormControl } from '@angular/forms';

export interface CustomerSeatManagementSeasonTicketsFiltersForm {
    product: FormControl<number>;
    seat: FormControl<number>;
}
