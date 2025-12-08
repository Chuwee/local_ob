import type { MatDateFormats } from '@angular/material/core';

export const CPANEL_DATEPICKER_FORMATS: MatDateFormats = {
    parse: {
        dateInput: 'L'
    },
    display: {
        dateInput: 'L',
        dateA11yLabel: 'L',
        monthYearLabel: 'MMM YYYY',
        monthYearA11yLabel: 'MMMM YYYY'
    }
};