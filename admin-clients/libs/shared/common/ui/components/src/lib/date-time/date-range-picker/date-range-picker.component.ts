import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, input, Input, OnInit, signal, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButton } from '@angular/material/button';
import { DateRange, MatCalendar, MatDatepickerModule } from '@angular/material/datepicker';
import { MatDivider } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import moment, { DurationInputArg1, DurationInputArg2 } from 'moment-timezone';
import { NgxMaterialTimepickerModule, NgxTimepickerFieldComponent } from 'ngx-material-timepicker';
import { DateRangeShortcut, DateRangeShortcutElement } from '../../models/date-range-shortcut.enum';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        FlexLayoutModule,
        TranslatePipe,
        DateTimePipe,
        MatButton,
        MatDatepickerModule,
        MatDivider,
        NgxMaterialTimepickerModule
    ],
    selector: 'app-date-range-picker',
    templateUrl: './date-range-picker.component.html',
    styleUrls: ['./date-range-picker.component.scss']
})
export class DateRangePickerComponent implements OnInit, AfterViewInit {
    @ViewChild('matCalendar1')
    private _matCalendar1: MatCalendar<moment.Moment>;

    @ViewChild('matCalendar2')
    private _matCalendar2: MatCalendar<moment.Moment>;

    @ViewChild('timePickerFrom')
    private _timePickerFrom: NgxTimepickerFieldComponent;

    @ViewChild('timePickerTo')
    private _timePickerTo: NgxTimepickerFieldComponent;

    readonly shortcuts = [
        {
            id: DateRangeShortcut.today,
            label: 'DATES.TODAY',
            from: moment().startOf('day'),
            to: moment().endOf('day')
        }, {
            id: DateRangeShortcut.yesterday,
            label: 'DATES.YESTERDAY',
            from: moment().subtract(1, 'days').startOf('day'),
            to: moment().subtract(1, 'days').endOf('day')
        }, {
            id: DateRangeShortcut.currentWeek,
            label: 'DATES.CURRENT_WEEK',
            from: moment().startOf('week'),
            to: moment().endOf('week')
        }, {
            id: DateRangeShortcut.lastWeek,
            label: 'DATES.LAST_WEEK',
            from: moment().subtract(1, 'week').startOf('week'),
            to: moment().subtract(1, 'week').endOf('week')
        }, {
            id: DateRangeShortcut.currentMonth,
            label: 'DATES.CURRENT_MONTH',
            from: moment().startOf('month'),
            to: moment().endOf('month')
        }, {
            id: DateRangeShortcut.lastMonth,
            label: 'DATES.LAST_MONTH',
            from: moment().subtract(1, 'month').startOf('month'),
            to: moment().subtract(1, 'month').endOf('month')
        }, {
            id: DateRangeShortcut.currentYear,
            label: 'DATES.CURRENT_YEAR',
            from: moment().startOf('year'),
            to: moment().endOf('year')
        }, {
            id: DateRangeShortcut.lastYear,
            label: 'DATES.LAST_YEAR',
            from: moment().subtract(1, 'year').startOf('year'),
            to: moment().subtract(1, 'year').endOf('year')
        }];

    timeFrom: string;
    timeTo: string;
    dateTimeFormats = DateTimeFormats;
    selectedDate: DateRange<moment.Moment>;
    selectedShortcut: DateRangeShortcut;

    @Input() isUTC = true;
    @Input() extraShortcuts: DateRangeShortcutElement[];

    $maxDateRangeAmount = input<DurationInputArg1>(null, { alias: 'maxDateRangeAmount' });
    $maxDateRangeType = input<DurationInputArg2>(null, { alias: 'maxDateRangeType' });

    $minDate = signal<moment.Moment>(null);
    $maxDate = signal<moment.Moment>(null);

    ngOnInit(): void {
        if (this.extraShortcuts) {
            this.shortcuts.push(...this.extraShortcuts);
        }
    }

    ngAfterViewInit(): void {
        this.moveMonthView(this._matCalendar2, moment().add(1, 'months'));
        this.setDefaultTime();
        const cleanTime = (time: string): string => time === '24:00' ? '00:00' : time;
        this._timePickerFrom.registerOnChange(async (timeFrom: string) => {
            this.timeFrom = cleanTime(timeFrom);
            if (this.selectedShortcut !== DateRangeShortcut.none) {
                this.setSelectedShortcutFromDate(this.selectedDate.start, this.selectedDate.end);
            }
        });
        this._timePickerTo.registerOnChange(async (timeTo: string) => {
            this.timeTo = cleanTime(timeTo);
            if (this.selectedShortcut !== DateRangeShortcut.none) {
                this.setSelectedShortcutFromDate(this.selectedDate.start, this.selectedDate.end);
            }
        });
    }

    setParams(params: { startDate: string; endDate: string }): boolean {
        if (this.$maxDateRangeAmount() && this.$maxDateRangeType()) {
            this.$maxDate.set(null);
            this.$minDate.set(null);
        }
        if (params.startDate && params.endDate) {
            const startMoment = moment(params.startDate);
            const endMoment = moment(params.endDate);
            if (startMoment.isValid() && endMoment.isValid()) {
                this.setSelectedShortcutFromDate(startMoment, endMoment);
                this.setRangeDateAndMoveCalendars(startMoment, endMoment);
                this.setTime(startMoment.format('HH:mm'), endMoment.format('HH:mm'));

                return true;
            }
        }
        this.selectedShortcut = DateRangeShortcut.none;
        return false;
    }

    getDateRange(): DateRange<string> {
        if (this.isUTC) {
            const start = moment(this.selectedDate.start.format('YYYY-MM-DD') + 'T' + this.timeFrom + ':00.000Z')
                .subtract(this.selectedDate.start.utcOffset(), 'minutes');
            const end = moment(this.selectedDate.end.format('YYYY-MM-DD') + 'T' + this.timeTo + ':59.999Z')
                .subtract(this.selectedDate.end.utcOffset(), 'minutes');
            return new DateRange(
                start.utc().toISOString(),
                end.utc().toISOString()
            );
        } else {
            return new DateRange(
                this.selectedDate.start.format(`YYYY-MM-DDT${this.timeFrom}:00.000Z`),
                this.selectedDate.end.format(`YYYY-MM-DDT${this.timeTo}:59.999Z`)
            );
        }
    }

    getDateRangeFormatted(): string {
        const start = this.selectedDate.start.format('L') + ' ' + this.timeFrom;
        const end = this.selectedDate.end.format('L') + ' ' + this.timeTo;
        return start + ' - ' + end;
    }

    resetParams(): void {
        this.selectedDate = new DateRange(null, null);
        this.setDefaultTime();
    }

    selectDate(date: moment.Moment): void {
        if (this.selectedDate?.end) {
            this.selectedDate = new DateRange(date.startOf('day'), null);
            if (this.$maxDateRangeAmount() && this.$maxDateRangeType()) {
                this.$maxDate.set(moment(date).add(this.$maxDateRangeAmount(), this.$maxDateRangeType()));
                this.$minDate.set(moment(date).subtract(this.$maxDateRangeAmount(), this.$maxDateRangeType()));
            }
        } else if (this.selectedDate?.start) {
            if (date < this.selectedDate?.start) {
                this.selectedDate = new DateRange(date.startOf('day'), this.selectedDate?.start.endOf('day'));
            } else {
                this.selectedDate = new DateRange(this.selectedDate?.start, date.endOf('day'));
                this.$minDate.set(null);
                this.$maxDate.set(null);
            }
        } else {
            this.selectedDate = new DateRange(date.startOf('day'), null);
        }
        this.setSelectedShortcutFromDate(this.selectedDate.start, this.selectedDate.end);
    }

    useShortcut(id: DateRangeShortcut): void {
        const shortcut = this.shortcuts.find(shortcut => shortcut.id === id);
        if (shortcut) {
            this.selectedShortcut = shortcut.id;
            this.setRangeDateAndMoveCalendars(shortcut.from, shortcut.to);
            this.setDefaultTime();
        }
    }

    private setSelectedShortcutFromDate(from: moment.Moment, to: moment.Moment): void {
        let selectedShortcut;
        if (this.timeFrom === '00:00' && this.timeTo === '23:59') {
            selectedShortcut = this.shortcuts.find(shortcut =>
                shortcut.from && shortcut.to && from && to &&
                shortcut.from.toISOString() === from.toISOString() &&
                shortcut.to.toISOString() === to.toISOString())?.id;
        }
        this.selectedShortcut = selectedShortcut || DateRangeShortcut.custom;
    }

    private moveMonthView(calendar: MatCalendar<moment.Moment>, month: moment.Moment): void {
        if (calendar.monthView.activeDate) {
            calendar.monthView.activeDate = month;
            calendar.monthView.activeDateChange.emit(month);
        }
    }

    private setDefaultTime(): void {
        this.setTime('00:00', '23:59');
    }

    private setTime(from: string, to: string): void {
        const fromParts = from.split(':');
        const toParts = to.split(':');
        this.timeFrom = from;
        this.timeTo = to;
        this._timePickerFrom.changeHour(Number(fromParts[0]));
        this._timePickerFrom.changeMinute(Number(fromParts[1]));
        this._timePickerTo.changeHour(Number(toParts[0]));
        this._timePickerTo.changeMinute(Number(toParts[1]));
    }

    private setRangeDateAndMoveCalendars(from: moment.Moment, to: moment.Moment): void {
        this.selectedDate = new DateRange(from, to);
        if (from) {
            if (to && from.get('month') !== to.get('month')) {
                this.moveMonthView(this._matCalendar1, from);
                this.moveMonthView(this._matCalendar2, to);
            } else {
                /* When from and to have equal month, shows the dates month in the right calendar (calendar2), and the
                previous month in the left one (calendar1), making it more usefull to choose a past operations date. */
                this.moveMonthView(this._matCalendar1, from.clone().subtract(1, 'months'));
                this.moveMonthView(this._matCalendar2, from);
            }
        }
    }
}
