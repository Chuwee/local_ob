import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import moment from 'moment-timezone/moment-timezone';

export function dateTimeGroupValidator(
    comparationFunction: (date1: moment.Moment, date2: moment.Moment) => boolean,
    errorCode: string,
    date1Key: string,
    date2Key: string
): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        if (control?.get(date1Key)?.enabled && control.get(date2Key)?.enabled) {
            const date1: moment.Moment = parseToMoment(control.get(date1Key));
            const date2: moment.Moment = parseToMoment(control.get(date2Key));
            if (date1?.isValid() && date2?.isValid()) {
                if (!comparationFunction(date1, date2)) {
                    return {
                        [errorCode]: {
                            date1: date1.format(DateTimeFormats.shortDateTime),
                            date2: date2.format(DateTimeFormats.shortDateTime)
                        }
                    };
                }
            }
        }
        return {};
    };
}

export function dateValidator(
    comparationFunction: (date1: moment.Moment, date2: moment.Moment) => boolean,
    errorCode: string,
    dateToCompare: string | moment.Moment | AbstractControl
): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        if (control?.value && dateToCompare) {
            const date2 = parseToMoment(dateToCompare);
            if (date2?.isValid()) {
                setDayStep(date2);
                if (!comparationFunction(parseToMoment(control.value), date2)) {
                    return { [errorCode]: date2.format(DateTimeFormats.shortDate) };
                }
            }
        }
        return null;
    };
}

export function dateTimeValidator(
    comparationFunction: (date1: moment.Moment, date2: moment.Moment) => boolean,
    errorCode: string,
    dateToCompare: string | moment.Moment | AbstractControl,
    errorParamValue?: string
): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        if (control?.value && dateToCompare) {
            const date2 = parseToMoment(dateToCompare);
            if (date2?.isValid()) {
                setMinuteStep(date2);
                if (!comparationFunction(parseToMoment(control.value), date2)) {
                    return { [errorCode]: errorParamValue || date2.format(DateTimeFormats.shortDateTime) };
                }
            }
        }
        return null;
    };
}

export function dateIsAfter(date1: moment.Moment, date2: moment.Moment): boolean {
    return date1.isAfter(date2);
}

export function dateIsBefore(date1: moment.Moment, date2: moment.Moment): boolean {
    return date1.isBefore(date2);
}

export function dateIsSameOrBefore(date1: moment.Moment, date2: moment.Moment): boolean {
    return date1.isSameOrBefore(date2);
}

export function dateIsSameOrAfter(date1: moment.Moment, date2: moment.Moment): boolean {
    return date1.isSameOrAfter(date2);
}

function parseToMoment(value: string | moment.Moment | AbstractControl): moment.Moment {
    let result: moment.Moment = null;
    if (value instanceof AbstractControl) {
        if (value.value) {
            result = moment(value.value);
        }
    } else if (!moment.isMoment(value)) {
        result = moment(value);
    } else {
        result = value;
    }
    if (result) {
        result.set({ seconds: 0, milliseconds: 0 });
    }
    return result;
}

function setDayStep(m: moment.Moment): void {
    m.set({ hours: 0, minutes: 0, seconds: 0, milliseconds: 0 });
}

function setMinuteStep(m: moment.Moment): void {
    m.set({ seconds: 0, milliseconds: 0 });
}
