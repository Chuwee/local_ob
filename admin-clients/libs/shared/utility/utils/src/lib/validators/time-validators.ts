import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import moment from 'moment-timezone/moment-timezone';

export function timeValidator(
    comparatorFunction: (date1: moment.Moment, date2: moment.Moment) => boolean,
    errorCode: string,
    dateToCompare: string | AbstractControl
): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        if (control?.value && dateToCompare) {
            const date2 = parseTimeToMoment(dateToCompare);
            if (date2?.isValid()) {
                if (!comparatorFunction(parseTimeToMoment(control.value), date2)) {
                    return { [errorCode]: date2.format(DateTimeFormats.shortTime) };
                }
            }
        }
        return {};
    };
}

function parseTimeToMoment(date: string | AbstractControl): moment.Moment {
    return moment(date instanceof AbstractControl ? date.value : date, 'HH:mm')
        .set({ seconds: 0, milliseconds: 0 });
}

