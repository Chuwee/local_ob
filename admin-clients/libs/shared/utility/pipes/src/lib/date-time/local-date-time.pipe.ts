import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { Pipe, PipeTransform } from '@angular/core';
import moment from 'moment-timezone';
import { BaseDateTimePipe } from './base-date-time.pipe';

@Pipe({
    name: 'localDateTime',
    pure: true,
    standalone: true
})
export class LocalDateTimePipe extends BaseDateTimePipe implements PipeTransform {
    transform(date: string | moment.Moment, formats: string | string[] = null): string {
        if (date) {
            const zonedDate = this.getMomentDate(date);
            const localDate = moment.parseZone(date);
            const formatStrings = this.getFormatsList(formats);
            return this.getFormatsListWithOptTZ(
                formatStrings,
                zonedDate,
                localDate
            )
                .map(formatString => localDate.format(formatString)).join(' ');
        } else {
            return '';
        }
    }

    private getFormatsListWithOptTZ(formatStrings: string[], zonedDate: moment.Moment, userTimeZoneDate: moment.Moment): string[] {
        if (formatStrings.find(formatString => formatString.includes(DateTimeFormats.shortTime))
            && userTimeZoneDate.utcOffset() !== zonedDate.utcOffset()) {
            formatStrings.push('(Z)');
        }
        return formatStrings;
    }
}
