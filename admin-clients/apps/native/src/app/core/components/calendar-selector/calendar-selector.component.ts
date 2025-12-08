import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, EventEmitter, inject, Input, OnInit, Output, ViewChild
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InfiniteScrollCustomEvent, IonContent, IonicModule, ModalController, NavParams } from '@ionic/angular';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment';
import { TimePickerComponent } from '../time-picker/time-picker.component';
import { CalendarWeekComponent } from './components/calendar-week/calendar-week.component';
import { MonthComponent } from './components/month/month.component';
import { CalendarDay, CalendarMonth, SelectedTime } from './models/calendar-selector.model';
import { CalendarService } from './services/calendar-selector.service';

@Component({
    selector: 'calendar-selector',
    imports: [
        CommonModule, IonicModule, FormsModule, MonthComponent, CalendarWeekComponent, TranslatePipe, TimePickerComponent, DateTimePipe
    ],
    providers: [NavParams, CalendarService],
    templateUrl: './calendar-selector.component.html',
    styleUrls: ['./calendar-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CalendarSelectorComponent implements OnInit {
    private readonly _modalCtrl = inject(ModalController);
    private readonly _ref = inject(ChangeDetectorRef);
    private readonly _calSvc = inject(CalendarService);
    private readonly _translate = inject(TranslateService);
    private readonly _defaultDates = {
        yesterday: [...this.getYesterday()],
        today: [...this.getToday()],
        thisWeek: [...this.getCurrentWeek()],
        lastWeek: [...this.getLastWeek()],
        thisMonth: [...this.getCurrentMonth()],
        lastMonth: [...this.getLastMonth()],
        thisYear: [...this.getCurrentYear()],
        lastYear: [...this.getLastYear()]
    };

    @ViewChild(IonContent) private readonly _content!: IonContent;
    @ViewChild('months', { read: ElementRef }) private readonly _monthsEle: ElementRef;
    @ViewChild(MonthComponent) private readonly _monthComponent!: MonthComponent;
    @ViewChild('chipsOfTime') private readonly _chipsOfTime!: ElementRef;

    //TODO: What does this do???
    private _scrolledToInitialPosition = false;

    @Output() readonly selectDate: EventEmitter<SelectedTime> = new EventEmitter<SelectedTime>();
    readonly dateTimeFormats = DateTimeFormats;

    datesTemp: CalendarDay[] = [null, null];
    calendarMonths!: CalendarMonth[];
    isSelectingTime = false;

    @Input()
    selectedTime: SelectedTime = {
        timeFrom: null,
        timeTo: null
    };

    ngOnInit(): void {
        const timeToday = moment().valueOf();
        const firstMonthToLoad = moment(timeToday)
            .subtract(24, 'month')
            .valueOf();
        const totalMonthsAfterThefirstOne = 30;
        this.calendarMonths = this._calSvc.createMonthsByPeriod(
            firstMonthToLoad,
            totalMonthsAfterThefirstOne
        );
    }

    async checkContent(): Promise<void> {
        const momentToday = moment();
        const currentMonth = this.getTheTopElementByTime(momentToday.valueOf());

        if (currentMonth) {
            this.checkIfAnyChipIsSelected();
            this.selectedTime.timeFrom
                ? this.scrollToDate(moment(this.selectedTime.timeFrom))
                : this.scrollToDate(momentToday);

            if (this.selectedTime.timeFrom && this.selectedTime.timeTo) {
                const dayFrom = this._calSvc.createCalendarDay(
                    this.selectedTime.timeFrom
                );
                dayFrom.selected = true;

                const dayTo = this._calSvc.createCalendarDay(
                    this.selectedTime.timeTo
                );
                dayTo.selected = true;

                this.datesTemp =
                    moment(this.selectedTime.timeFrom).date() ===
                        moment(this.selectedTime.timeTo).date()
                        ? (this.datesTemp = [dayFrom, null])
                        : (this.datesTemp = [dayFrom, dayTo]);

                this._ref.detectChanges();
            }
        }
    }

    get stringDateFrom(): string {
        return moment(this.selectedTime.timeFrom).toJSON();
    }

    get stringDateTo(): string {
        return moment(this.selectedTime.timeTo).toJSON();
    }

    onChange(data: CalendarDay[]): void {
        const [dateFrom, dateTo] = data;

        const dayFromInMiliseconds = moment(dateFrom.time).valueOf();
        const dayToInMiliseconds = dateTo
            ? moment(dateTo.time).valueOf()
            : moment(dateFrom.time).valueOf();

        const timeFrom = {
            hours: this.selectedTime.timeFrom
                ? moment(this.selectedTime.timeFrom).hours()
                : moment(dayFromInMiliseconds).startOf('day').hours(),
            minutes: this.selectedTime.timeFrom
                ? moment(this.selectedTime.timeFrom).minutes()
                : moment(dayToInMiliseconds).startOf('day').minutes()
        };

        this.selectedTime.timeFrom = moment(dayFromInMiliseconds)
            .hours(timeFrom.hours)
            .minutes(timeFrom.minutes)
            .valueOf();

        const timeTo = {
            hours: this.selectedTime.timeTo
                ? moment(this.selectedTime.timeTo).hours()
                : moment(dayToInMiliseconds).endOf('day').hours(),
            minutes: this.selectedTime.timeTo
                ? moment(this.selectedTime.timeTo).minutes()
                : moment(dayToInMiliseconds).endOf('day').minutes()
        };

        this.selectedTime.timeTo = moment(dayToInMiliseconds)
            .hours(timeTo.hours)
            .minutes(timeTo.minutes).endOf('minutes')
            .valueOf();

        if (
            moment(this.selectedTime.timeTo).isBefore(
                moment(this.selectedTime.timeFrom)
            )
        ) {
            this.selectedTime.timeTo = moment(
                this.selectedTime.timeFrom
            ).valueOf();
        }

        this.datesTemp = data;
        this._ref.detectChanges();
    }

    canClear(): boolean {
        return !!this.datesTemp[0];
    }

    loadMoreMonths(event: InfiniteScrollCustomEvent, scrollDirection: 'top' | 'bottom'): void {

        const firstMonth = this.calendarMonths[0];
        const lastMonth = this.calendarMonths.slice(-1)[0];

        if (scrollDirection === 'top') {
            const previousTime = moment(firstMonth.original.time)
                .subtract(1, 'month')
                .valueOf();
            this.calendarMonths = [...this._calSvc.createMonthsByPeriod(previousTime, 1), ...this.calendarMonths];

        } else if (scrollDirection === 'bottom') {
            const nextTime = moment(lastMonth.original.time)
                .add(1, 'month')
                .valueOf();
            this.calendarMonths = [...this.calendarMonths, ...this._calSvc.createMonthsByPeriod(nextTime, 1)];

        }
        this._ref.detectChanges();

        event.target.complete();
    }

    async scrollToDate(date: moment.Moment): Promise<void> {
        const element = this.getTheTopElementByTime(date.valueOf());

        if (!element) {
            return;
        }

        try {
            await this.waitForElementTop(element);
            const defaultMonthScrollPosition = element.offsetTop + 90;
            await this._content.scrollToPoint(0, defaultMonthScrollPosition, 10);
            this._scrolledToInitialPosition = true;
        } catch (e) {
            this._scrolledToInitialPosition = true;
            console.error('Could not scroll to month');
        }
    }

    monthFormat(date: Date): string {
        const month = this._translate.instant(
            'MONTHS.SHORT-' + moment(date).format('MM').toUpperCase()
        );
        const year = moment(date).format('YYYY');

        return `${month}. ${year}`;
    }

    trackByIndex(index: number, momentDate: CalendarMonth): number {
        return momentDate.original ? momentDate.original.time : index;
    }

    onClickChip(event): void {
        const currentChip: HTMLElement = event.target;
        const currentAttribute = currentChip.getAttribute('date-type');

        document
            .querySelectorAll('.calendar-selector__date-chip')
            .forEach(async chip => {
                chip.classList.remove('calendar-selector__date-chip--selected');

                if (chip === currentChip) {
                    currentChip.classList.add(
                        'calendar-selector__date-chip--selected'
                    );
                    if (currentAttribute === 'thisYear') {
                        await this.loadFullCurrentYear();
                    }
                    if (currentAttribute === 'lastYear') {
                        await this.loadFullLastYear();
                    }
                    this.selectedTime.timeFrom =
                        this._defaultDates[currentAttribute][0];
                    this.selectedTime.timeTo =
                        this._defaultDates[currentAttribute][1];

                    this.refreshCalendarWithCurrentSelections(
                        this._defaultDates[currentAttribute][0],
                        this._defaultDates[currentAttribute][1]
                    );
                }
            });
    }

    clearChips(): void {
        document
            .querySelectorAll('.calendar-selector__date-chip')
            .forEach(chip => {
                chip.classList.remove('calendar-selector__date-chip--selected');
            });
    }

    openTimePicker(): void {
        this.isSelectingTime = true;
    }

    setTime(data: SelectedTime): void {
        this.goBack();
        this.selectedTime = data;
    }

    closeModal(): void {
        this._modalCtrl.dismiss(null, 'clear');
    }

    goBack(): void {
        this.isSelectingTime = false;
    }

    applySelection(): void {
        this.selectDate.emit(this.selectedTime);
        this.closeModal();
    }

    private checkIfAnyChipIsSelected(): void {
        this._chipsOfTime.nativeElement
            .querySelectorAll('.calendar-selector__date-chip')
            .forEach(async (chip: HTMLElement) => {
                const attribute = chip.getAttribute('date-type');
                const defaultDate = this._defaultDates[attribute];

                if (defaultDate) {
                    if (
                        moment(this.selectedTime.timeFrom).isSame(
                            moment(defaultDate[0]),
                            'day'
                        ) &&
                        moment(this.selectedTime.timeTo).isSame(
                            moment(defaultDate[1]),
                            'day'
                        )
                    ) {
                        chip.classList.add(
                            'calendar-selector__date-chip--selected'
                        );
                    }
                }
            });
    }

    private async waitForElementTop(element: HTMLElement, timeout = 2000): Promise<void> {
        const start = Date.now();
        let now = 0;

        return new Promise((resolve, reject): void => {
            const interval = setInterval((): void => {
                if (element.offsetTop) {
                    clearInterval(interval);
                    resolve();
                }

                now = Date.now();

                if (now - start >= timeout) {
                    reject(`Could not find the element  within ${timeout} ms`);
                }
            }, 50);
        });
    }

    private async refreshCalendarWithCurrentSelections(
        startTime: number,
        endTime: number
    ): Promise<void> {
        const selectedDays = [];
        this._monthComponent.date = [null, null];

        this.calendarMonths.forEach(month => {
            month.days.forEach(day => {
                if (day) {
                    const startDate = moment(startTime);
                    const endDate = moment(endTime);
                    const dayToCheck = moment(day.time);

                    if (
                        dayToCheck.isSameOrAfter(startDate, 'day') &&
                        dayToCheck.isSameOrBefore(endDate, 'day')
                    ) {
                        selectedDays.push(day);
                    }
                }
            });
        });

        if (selectedDays.length > 1) {
            this._monthComponent.onSelected(selectedDays[0]);
            this._monthComponent.onSelected(
                selectedDays[selectedDays.length - 1]
            );
        } else {
            this._monthComponent.onSelected(selectedDays[0]);
        }

        this.scrollToDate(moment(selectedDays[0].time));
    }

    private getYesterday(): number[] {
        const yesterday = moment().subtract(1, 'day');
        return [
            yesterday.startOf('day').valueOf(),
            yesterday.endOf('day').valueOf()
        ];
    }

    private getToday(): number[] {
        const today = moment();
        return [today.startOf('day').valueOf(), today.endOf('day').valueOf()];
    }

    private getCurrentWeek(): number[] {
        const currentWeekStart = moment().startOf('isoWeek').valueOf();
        const currentWeekEnd = moment(currentWeekStart)
            .add(6, 'days')
            .endOf('day')
            .valueOf();
        return [currentWeekStart, currentWeekEnd];
    }

    private getLastWeek(): number[] {
        const previousWeekStart = moment()
            .startOf('isoWeek')
            .subtract(1, 'week')
            .valueOf();
        const previousWeekEnd = moment(previousWeekStart)
            .add(6, 'days')
            .endOf('day')
            .valueOf();
        return [previousWeekStart, previousWeekEnd];
    }

    private getCurrentMonth(): number[] {
        const currentMonthStart = moment().startOf('month').valueOf();
        const currentMonthEnd = moment().endOf('month').valueOf();
        return [currentMonthStart, currentMonthEnd];
    }

    private getLastMonth(): number[] {
        const previousMonthStart = moment()
            .subtract(1, 'month')
            .startOf('month')
            .valueOf();
        const previousMonthEnd = moment()
            .subtract(1, 'month')
            .endOf('month')
            .valueOf();
        return [previousMonthStart, previousMonthEnd];
    }

    private getCurrentYear(): number[] {
        const currentYearStart = moment().startOf('year').valueOf();
        const currentYearEnd = moment().endOf('year').valueOf();
        return [currentYearStart, currentYearEnd];
    }

    private getLastYear(): number[] {
        const lastYearStart = moment()
            .subtract(1, 'year')
            .startOf('year')
            .valueOf();
        const lastYearEnd = moment()
            .subtract(1, 'year')
            .endOf('year')
            .valueOf();
        return [lastYearStart, lastYearEnd];
    }

    private async loadFullCurrentYear(): Promise<void> {
        const timeOfFirstMonth = moment().startOf('year').valueOf();
        const totalMonthsToShow = 12;

        this.calendarMonths = this._calSvc.createMonthsByPeriod(
            timeOfFirstMonth,
            totalMonthsToShow
        );

        await this.waitForNewCalendarMonths();
    }

    private async loadFullLastYear(): Promise<void> {
        const timeOfFirstMonth = moment()
            .subtract(10, 'months')
            .startOf('year')
            .valueOf();

        const totalMonthsToShow = moment().diff(timeOfFirstMonth, 'months') + 1;
        this.calendarMonths = this._calSvc.createMonthsByPeriod(
            timeOfFirstMonth,
            totalMonthsToShow
        );

        await this.waitForNewCalendarMonths();
    }

    private getTheTopElementByTime(time: number): HTMLElement {
        const monthSelector = moment(time).format('YYYY-MM');

        return this._monthsEle.nativeElement?.querySelector(
            `[data-month="${monthSelector}"]`
        );
    }

    private waitForNewCalendarMonths(): Promise<boolean> {
        return new Promise((resolve, reject) => {
            const interval = setInterval(() => {
                const numberOfcurrentMonths =
                    document.querySelectorAll('.month-box').length;

                if (numberOfcurrentMonths === this.calendarMonths.length) {
                    clearInterval(interval);
                    resolve(true);
                } else {
                    reject(`Months are not loaded`);
                }
            }, 50);
        });
    }

    get selectedTimePicker(): SelectedTime {
        return this.selectedTime;
    }

}
