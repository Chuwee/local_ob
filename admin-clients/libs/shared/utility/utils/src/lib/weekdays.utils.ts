import { Weekdays } from '@admin-clients/shared-utility-models';
import moment from 'moment';

/**
 * builds an array of weeksdays in locale order with labels
 * @returns an array of weekdays with its label
 */
export const weekdays = (): { key: Weekdays; label: string }[] => {
    const wdKeys = Object.values(Weekdays);
    const wdLabels = moment.weekdaysMin();
    const firstDayOfWeek = moment.localeData().firstDayOfWeek();
    const weekdays: { key: Weekdays; label: string }[] = [];
    for (let i = firstDayOfWeek; i < (firstDayOfWeek + 7); i++) {
        weekdays.push({
            key: wdKeys[i % 7],
            label: wdLabels[i % 7].toUpperCase()
        });
    }
    return weekdays;
};
