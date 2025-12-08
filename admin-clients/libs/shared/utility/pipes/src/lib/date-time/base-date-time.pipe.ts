import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import moment from 'moment-timezone';

export abstract class BaseDateTimePipe {
    protected getMomentDate(date: string | moment.Moment): moment.Moment {
        if (moment.isMoment(date)) {
            return date;
        } else {
            return moment(date);
        }
    }

    protected getFormatsList(formats: string | string[]): string[] {
        let formatsList: string[];
        if (formats) {
            if (formats instanceof Array) {
                formatsList = formats;
            } else {
                formatsList = [formats];
            }
        } else {
            formatsList = [DateTimeFormats.shortDate];
        }
        return formatsList;
    }
}
