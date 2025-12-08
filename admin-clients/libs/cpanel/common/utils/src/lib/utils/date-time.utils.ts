import moment from 'moment-timezone';

/**
 * Transform timezone to default without modifying date
 * @returns an string with new offset
 */
export function forceToDefaultTimezone(date: string): string {
    if (date) {
        /* moment.parseZone() parses the date keeping the offset provided
        tz(moment().tz()) overwrites the timezone of the object keeping the same time
        */
        return moment.parseZone(date).tz(moment().tz(), true).format();
    }
    return undefined;
}

/**
 * For a given data, check each key for a date value and transform it to specified timezone
 */
export function forceDatesTimezone(data: unknown, timeZone: string, key: string = null): void {
    if (key) {
        if (data[key]) {
            if (data[key] instanceof Object) {
                forceDatesTimezone(data[key], timeZone);
            } else {
                const momentDate = moment(data[key], 'YYYY-MM-DDTHH:mm:ssZZ', true);
                if (momentDate.isValid()) {
                    data[key] = momentDate.tz(timeZone, true).format();
                }
            }
        }
    } else if (data instanceof Object) {
        Object.keys(data).forEach(subKey => forceDatesTimezone(data, timeZone, subKey));
    }
}
