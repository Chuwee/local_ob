import {
    SessionsDateMode, SessionRelativeTimeUnits, SessionRelativeTimeMoments
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { UntypedFormGroup } from '@angular/forms';
import moment from 'moment';

export class SessionUtils {
    static formatDate(date: string, timeZone: string): string {
        return date ? moment(date).tz(timeZone, true).format() : null;
    }

    static getFixedDateTime(baseSessionForm: UntypedFormGroup, startDateTime: string, groupPrefix: string, timeZone: string): string {
        const dateMode = baseSessionForm.get(`${groupPrefix}.dateMode`).value;
        if (dateMode === SessionsDateMode.fixed) {
            const date = baseSessionForm.get(`${groupPrefix}.date`).value;
            return SessionUtils.formatDate(date, timeZone);
        } else { // relative
            const duration = baseSessionForm.get(`${groupPrefix}.relativeDate.duration`).value;
            const timeUnit = baseSessionForm.get(`${groupPrefix}.relativeDate.timeUnit`).value;
            const momentTimeUnit = Object.keys(SessionRelativeTimeUnits)
                .find(key => SessionRelativeTimeUnits[key] === timeUnit) as moment.unitOfTime.DurationConstructor;
            const relativeMoment =
                baseSessionForm.get(`${groupPrefix}.relativeDate.relativeTimeMoment`)?.value
                || SessionRelativeTimeMoments.before;
            const date = relativeMoment === SessionRelativeTimeMoments.before
                ? moment(startDateTime).subtract(duration, momentTimeUnit)
                : moment(startDateTime).add(duration, momentTimeUnit);
            if (timeUnit !== SessionRelativeTimeUnits.minutes && timeUnit !== SessionRelativeTimeUnits.hours) {
                const addTime = baseSessionForm.get(`${groupPrefix}.relativeDate.addTime`).value;
                if (addTime) {
                    const fixedTime = baseSessionForm.get(`${groupPrefix}.relativeDate.fixedTime`).value;
                    date.set({
                        h: moment(fixedTime, 'HH:mm').get('h'),
                        m: moment(fixedTime, 'HH:mm').get('m')
                    });
                }
            }
            return SessionUtils.formatDate(date.format(), timeZone);
        }
    }

}
