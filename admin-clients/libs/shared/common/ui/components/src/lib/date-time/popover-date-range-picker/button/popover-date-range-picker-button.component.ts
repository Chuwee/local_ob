import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { DateRange } from '@angular/material/datepicker';
import moment from 'moment-timezone';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule, DateTimePipe
    ],
    selector: 'app-popover-date-ranger-picker-button',
    templateUrl: './popover-date-range-picker-button.component.html'
})
export class PopoverDateRangePickerButtonComponent {
    private readonly FORMATS = inject(MAT_DATE_FORMATS);

    readonly dateTimeFormats = DateTimeFormats;
    readonly dateFormat = moment.localeData().longDateFormat(this.FORMATS.display.dateInput).toLowerCase();

    @Input() selectedDate: DateRange<moment.Moment>;
}
