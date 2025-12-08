import moment from 'moment-timezone';

export enum DateRangeShortcut {
    today = 'TODAY',
    yesterday = 'YESTERDAY',
    currentWeek = 'CURRENT_WEEK',
    lastWeek = 'LAST_WEEK',
    currentMonth = 'CURRENT_MONTH',
    lastMonth = 'LAST_MONTH',
    currentYear = 'CURRENT_YEAR',
    lastYear = 'LAST_YEAR',
    beginning = 'BEGINNING',
    none = 'NONE',
    custom = 'CUSTOM'
}

export interface DateRangeShortcutElement<T = DateRangeShortcut> {
    id: T;
    label: string;
    info?: string;
    from: moment.Moment;
    to: moment.Moment;
}
