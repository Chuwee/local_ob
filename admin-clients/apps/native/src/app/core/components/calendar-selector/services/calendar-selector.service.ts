/* eslint-disable @typescript-eslint/naming-convention */
import { Injectable } from '@angular/core';
import moment from 'moment';
import { CalendarDay, CalendarMonth, CalendarOriginal, CalendarResult } from '../models/calendar-selector.model';

const defaults = {
    DATE_FORMAT: 'YYYY-MM-DD',
    WEEKS_FORMAT: ['M', 'T', 'W', 'T', 'F', 'S', 'S'],
    MONTH_FORMAT: [
        'JAN',
        'FEB',
        'MAR',
        'APR',
        'MAY',
        'JUN',
        'JUL',
        'AUG',
        'SEP',
        'OCT',
        'NOV',
        'DEC'
    ]
};

@Injectable()
export class CalendarService {
    static readonly DEFAULT_DATE: Date = new Date(1971, 0, 1);

    get DEFAULT_STEP(): number {
        return 4;
    }

    createOriginalCalendar(time: number): CalendarOriginal {
        const date = new Date(time);
        const year = date.getFullYear();
        const month = date.getMonth();
        const firstWeek = new Date(year, month, 1).getDay();
        const howManyDays = moment(time).daysInMonth();

        return {
            year,
            month,
            firstWeek,
            howManyDays,
            time: new Date(year, month, 1).getTime(),
            date
        };
    }

    createCalendarDay(
        time: number,
        month?: number
    ): CalendarDay {
        const _time = moment(time);
        const date = moment(time);
        const isToday = moment().isSame(_time, 'days');
        const title = new Date(time).getDate().toString();

        return {
            time,
            isToday,
            title,
            selected: false,
            isLastMonth: date.month() < month,
            isNextMonth: date.month() > month,
            isFirst: date.date() === 1,
            isLast: date.date() === date.daysInMonth()
        };
    }

    createCalendarMonth(
        original: CalendarOriginal
    ): CalendarMonth {
        const days: CalendarDay[] = new Array(6).fill(null);
        const len = original.howManyDays;
        for (let i = original.firstWeek; i < len + original.firstWeek; i++) {
            const itemTime = new Date(
                original.year,
                original.month,
                i - original.firstWeek + 1
            ).getTime();
            days[i] = this.createCalendarDay(itemTime);
        }

        days[0] === null ? days.shift() : days.unshift(...new Array(6).fill(null));

        return {
            days,
            original
        };
    }

    createMonthsByPeriod(
        startTime: number,
        monthsNum: number
    ): CalendarMonth[] {
        const _array: CalendarMonth[] = [];
        const _start = new Date(startTime);
        const _startMonth = new Date(
            _start.getFullYear(),
            _start.getMonth(),
            1
        ).getTime();

        for (let i = 0; i < monthsNum; i++) {
            const time = moment(_startMonth).add(i, 'M').valueOf();

            const originalCalendar = this.createOriginalCalendar(time);
            _array.push(this.createCalendarMonth(originalCalendar));
        }

        return _array;
    }

    multiFormat(time: number): CalendarResult {
        const _moment = moment(time);
        return {
            time: _moment.valueOf(),
            unix: _moment.unix(),
            dateObj: _moment.toDate(),
            string: _moment.format(defaults.DATE_FORMAT),
            years: _moment.year(),
            months: _moment.month() + 1,
            date: _moment.date()
        };
    }
}
