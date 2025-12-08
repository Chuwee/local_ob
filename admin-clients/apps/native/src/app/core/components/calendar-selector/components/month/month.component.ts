import { CommonModule } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, EventEmitter, Input, Output, forwardRef
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { CalendarDay, CalendarMonth, CalendarOriginal } from '../../models/calendar-selector.model';

export const MONTH_VALUE_ACCESSOR = {
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => MonthComponent),
    multi: true
};

@Component({
    selector: 'ion-calendar-month',
    imports: [CommonModule],
    providers: [MONTH_VALUE_ACCESSOR],
    styleUrls: ['./month.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './month.component.html'
})
export class MonthComponent implements ControlValueAccessor, AfterViewInit {
    @Output() readonly changeEmitter: EventEmitter<CalendarDay[]> = new EventEmitter();
    @Output() readonly selectStart: EventEmitter<CalendarDay> = new EventEmitter();
    @Output() readonly selectEnd: EventEmitter<CalendarDay> = new EventEmitter();
    @Input() readonly month!: CalendarMonth;

    date: (CalendarDay | null)[] = [null, null];
    isInit = false;

    onChanged!: () => void;
    onTouched!: () => void;

    ngAfterViewInit(): void {
        this.isInit = true;
    }

    get value(): CalendarDay[] {
        return this.date;
    }

    writeValue(obj: CalendarDay[]): void {
        if (Array.isArray(obj)) {
            this.date = obj;
        }
    }

    registerOnChange(fn: () => void): void {
        this.onChanged = fn;
    }

    registerOnTouched(fn: () => void): void {
        this.onTouched = fn;
    }

    trackByTime(item: CalendarOriginal): number {
        return item ? item.time : 0;
    }

    getDayLabel(day: CalendarDay): Date {
        return new Date(day.time);
    }

    isEndSelection(calendarDay: CalendarDay): boolean {
        if (!calendarDay || !this.isInit ||
            this.date[1] === null) return false;

        const lastSelectedDay = new Date(this.date[1].time);
        lastSelectedDay.setHours(0, 0, 0, 0);
        const dayToCheck = new Date(calendarDay.time);
        dayToCheck.setHours(0, 0, 0, 0);

        return lastSelectedDay.getTime() === dayToCheck.getTime();
    }

    isBetween(calendarDay: CalendarDay): boolean {
        if (!calendarDay || !this.isInit) return false;

        if (this.date[0] === null || this.date[1] === null) {
            return false;
        }

        const firstSelectedDay = new Date(this.date[0].time);
        firstSelectedDay.setHours(0, 0, 0, 0);
        const lastSelectedDay = new Date(this.date[1].time);
        lastSelectedDay.setHours(0, 0, 0, 0);

        const dayToCheck = new Date(calendarDay.time);
        dayToCheck.setHours(0, 0, 0, 0);

        return dayToCheck.getTime() < lastSelectedDay.getTime() && dayToCheck.getTime() > firstSelectedDay.getTime();
    }

    isStartSelection(calendarDay: CalendarDay): boolean {
        if (!calendarDay || !this.isInit ||
            this.date[0] === null) return false;

        const firstSelectedDay = new Date(this.date[0].time);
        firstSelectedDay.setHours(0, 0, 0, 0);
        const dayToCheck = new Date(calendarDay.time);
        firstSelectedDay.setHours(0, 0, 0, 0);

        return firstSelectedDay.getTime() === dayToCheck.getTime() && this.date[1] !== null;
    }

    isSelected(time: number): boolean {
        if (Array.isArray(this.date)) {
            const selectedTime = new Date(time);
            selectedTime.setHours(0, 0, 0, 0);

            if (this.date[0] !== null) {
                const startDate = new Date(this.date[0].time);
                startDate.setHours(0, 0, 0, 0);
                return selectedTime.getTime() === startDate.getTime();
            }

            if (this.date[1] !== null) {
                const endDate = new Date(this.date[1].time);
                endDate.setHours(0, 0, 0, 0);
                return selectedTime.getTime() === endDate.getTime();
            }

        } else {
            return false;
        }

        return false;
    }

    onSelected(item: CalendarDay): void {
        item.selected = true;

        if (this.date[0] === null) {
            this.date[0] = item;
            this.selectStart.emit(item);
        } else if (this.date[1] === null) {
            if (this.date[0].time < item.time) {
                this.date[1] = item;
                this.selectEnd.emit(item);
            } else {
                this.date[1] = this.date[0];
                this.selectEnd.emit(this.date[0]);
                this.date[0] = item;
                this.selectStart.emit(item);
            }
        } else {
            this.date[0] = item;
            this.selectStart.emit(item);
            this.date[1] = null;
        }

        this.changeEmitter.emit(this.date);

        return;
    }
}
