import { Pipe, PipeTransform } from '@angular/core';
import { Moment } from 'moment';
import { BaseDateTimePipe } from './base-date-time.pipe';

@Pipe({
    name: 'dateTime',
    pure: true,
    standalone: true
})
export class DateTimePipe extends BaseDateTimePipe implements PipeTransform {
    transform(date: string | Moment, formats: string | string[] = null): string {
        if (date) {
            const momentDate = this.getMomentDate(date);
            return this.getFormatsList(formats)
                .map(formatString => momentDate.format(formatString)).join(' ');
        } else {
            return '';
        }
    }
}
