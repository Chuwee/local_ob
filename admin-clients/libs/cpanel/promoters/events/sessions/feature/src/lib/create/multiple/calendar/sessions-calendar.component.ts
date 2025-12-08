import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { CalendarEvent, CalendarView } from 'angular-calendar';
import moment from 'moment-timezone';
import { Subject } from 'rxjs';

@Component({
    selector: 'app-sessions-calendar',
    templateUrl: './sessions-calendar.component.html',
    styleUrls: ['./sessions-calendar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionsCalendarComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _onDestroy: Subject<void> = new Subject();

    @Input() mainForm: UntypedFormGroup;
    sessionsName: string;
    firstStartDate: moment.Moment;
    lastStartDate: moment.Moment;
    calendarForm: UntypedFormGroup;
    dateTimeFormats = DateTimeFormats;
    view: CalendarView = CalendarView.Month;
    viewDate = moment().toDate();
    calendarEvents: CalendarEvent[] = [];
    calendarEventsByWeek: Record<number, Record<number, CalendarEvent[]>> = {};
    locale = moment.locale();

    ngOnInit(): void {
        this.sessionsName = this.mainForm.get('baseSession.base.name').value;
        this.firstStartDate = this.mainForm.get('repetitions.dates.start').value;
        this.lastStartDate = this.mainForm.get('repetitions.dates.end').value;
        this.viewDate = moment(this.firstStartDate).toDate();

        this.calendarForm = this._fb.group({});
        this.mainForm.setControl('calendar', this.calendarForm);

        this.generateCalendarEvents();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    selectAllEvents(): void {
        Object.keys(this.calendarForm.controls).forEach(key => {
            this.calendarForm.get(key).setValue(true);
        });
    }

    deselectAllEvents(): void {
        this.calendarForm.reset({ emitEvent: false });
    }

    onWeekCheckboxClick(isChecked: boolean, eventStart: Date): void {
        const allWeekEvents = this.getAllWeekEvents(eventStart);
        allWeekEvents.forEach(event => {
            this.calendarForm.get(event.meta).setValue(isChecked);
        });
    }

    isFirstDayOfWeek(date: moment.Moment): boolean {
        return moment(date).day() === moment.localeData().firstDayOfWeek();
    }

    isPartiallySelectedWeek(eventStart: Date): boolean {
        const allWeekEvents = this.getAllWeekEvents(eventStart);
        const selectedWeekEvents = allWeekEvents.filter(event => this.calendarForm.get(event.meta).value).length;
        return selectedWeekEvents > 0 && selectedWeekEvents < allWeekEvents.length;
    }

    isTotallySelectedWeek(eventStart: Date): boolean {
        const allWeekEvents = this.getAllWeekEvents(eventStart);
        const selectedWeekEvents = allWeekEvents.filter(event => this.calendarForm.get(event.meta).value).length;
        return selectedWeekEvents > 0 && selectedWeekEvents === allWeekEvents.length;
    }

    isUnselectableWeek(eventStart: Date): boolean {
        const allWeekEvents = this.getAllWeekEvents(eventStart);
        return allWeekEvents.length === 0;
    }

    private getAllWeekEvents(eventStart: Date): CalendarEvent[] {
        const eventYear = moment(eventStart).isoWeekYear();
        const eventWeek = moment(eventStart).week();
        return this.calendarEventsByWeek[eventYear]?.[eventWeek] ?
            this.calendarEventsByWeek[eventYear][eventWeek] : [];
    }

    private generateCalendarEvents(): void {
        const hourRows = this.mainForm.get('repetitions.hourRows').value;
        const firstDayOfWeek = moment.localeData().firstDayOfWeek();
        const isoWeekdaysWithHours = {
            // eslint-disable-next-line @typescript-eslint/naming-convention
            1: [], 2: [], 3: [], 4: [], 5: [], 6: [], 7: []
        };
        hourRows.forEach((row: { [key: string]: any }) => {
            for (const itemKey in row) {
                if (itemKey !== 'hour') {
                    if (row[itemKey]) {
                        let dayNum = Number(itemKey.slice(1));
                        if (firstDayOfWeek === 0) { // sunday
                            if (dayNum === 1) {
                                dayNum = 7;
                            } else {
                                dayNum = dayNum - 1;
                            }
                        }
                        isoWeekdaysWithHours[dayNum].push({
                            h: row['hour'].hour(),
                            m: row['hour'].minute()
                        });
                    }
                }
            }
        });

        for (
            let i = moment(this.firstStartDate);
            i.isSameOrBefore(this.lastStartDate);
            i = i.clone().add(1, 'd')
        ) {
            const currentDayHours = isoWeekdaysWithHours[i.isoWeekday()];
            currentDayHours.forEach(elem => {
                const startDate = i.clone().set({ h: elem.h, m: elem.m });
                this.calendarForm.addControl(startDate.format(), this._fb.control(true));
                const calendarEvent: CalendarEvent = {
                    start: startDate.toDate(),
                    title: startDate.format(DateTimeFormats.shortTime),
                    color: {
                        primary: '#5abdc5',
                        secondary: '#5abdc5'
                    },
                    meta: startDate.format()
                };
                const durationHours = this.mainForm.get('baseSession.base.durationHours').value || 0;
                const durationMins = this.mainForm.get('baseSession.base.durationMins').value || 0;
                if (durationHours > 0 || durationMins > 0) {
                    calendarEvent.end = startDate.clone().add(durationHours, 'h').add(durationMins, 'm').toDate();
                }
                const eventYear = moment(startDate).isoWeekYear();
                const eventWeek = moment(startDate).week();
                if (!this.calendarEventsByWeek[eventYear]) this.calendarEventsByWeek[eventYear] = {};
                if (!this.calendarEventsByWeek[eventYear][eventWeek]) this.calendarEventsByWeek[eventYear][eventWeek] = [];
                this.calendarEventsByWeek[eventYear][eventWeek].push(calendarEvent);
                this.calendarEvents.push(calendarEvent);
            });
        }
    }
}
