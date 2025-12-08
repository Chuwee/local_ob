
export interface CalendarOriginal {
    time: number;
    date: Date;
    year: number;
    month: number;
    firstWeek: number;
    howManyDays: number;
}

export interface CalendarDay {
    time: number;
    isToday: boolean;
    selected: boolean;
    isLastMonth?: boolean;
    isNextMonth?: boolean;
    title?: string;
    subTitle?: string;
    marked?: boolean;
    style?: {
        title?: string;
        subTitle?: string;
    };
    isFirst?: boolean;
    isLast?: boolean;
}

export class CalendarMonth {
    original!: CalendarOriginal;
    days!: CalendarDay[];
}

export interface DayConfig {
    date: Date;
    marked?: boolean;
    disable?: boolean;
    title?: string;
    subTitle?: string;
    cssClass?: string;
}

export interface CalendarOptions {
    from?: Date | number;
    to?: Date | number;
    weekStart?: number;
    disableWeeks?: number[];
    weekdays?: string[];
    monthFormat?: string;
}

export interface CalendarComponentOptions extends CalendarOptions {
    showToggleButtons?: boolean;
    showMonthPicker?: boolean;
    monthPickerFormat?: string[];
}

export class CalendarResult {
    time!: number;
    unix!: number;
    dateObj!: Date;
    string!: string;
    years!: number;
    months!: number;
    date!: number;
}

export class CalendarComponentMonthChange {
    oldMonth!: CalendarResult;
    newMonth!: CalendarResult;
}

export type DefaultDate = Date | string | number | null;
export type CalendarComponentTypeProperty =
    | 'string'
    | 'js-date'
    | 'moment'
    | 'time'
    | 'object';

export interface SelectedTime {
    timeFrom: number; // datetime in number
    timeTo: number; // datetime in number
}
