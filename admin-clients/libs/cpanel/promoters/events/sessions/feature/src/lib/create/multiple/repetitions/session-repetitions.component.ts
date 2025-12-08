import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { dateIsSameOrAfter, dateIsSameOrBefore, dateTimeValidator, joinCrossValidations } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import moment from 'moment-timezone';
import { combineLatest, Observable, Subject } from 'rxjs';
import { first, takeUntil } from 'rxjs/operators';
import { AddSessionsHourMode } from '../../models/add-sessions-hour-mode.enum';
import { NewHourDialogComponent } from './new-hour/new-hour-dialog.component';

@Component({
    selector: 'app-session-repetitions',
    templateUrl: './session-repetitions.component.html',
    styleUrls: ['./session-repetitions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionRepetitionsComponent implements OnInit, OnDestroy {
    private _sessionsService = inject(EventSessionsService);
    private _fb = inject(UntypedFormBuilder);
    private _ref = inject(ChangeDetectorRef);
    private readonly formats = inject(MAT_DATE_FORMATS);
    private _matDialog = inject(MatDialog);

    private _onDestroy = new Subject<void>();
    private _isExistingData = false;
    private _disabledWeekdaysCodes = new Set<string>();
    private get _hourRows(): UntypedFormArray {
        return this.repetitionsForm.get('hourRows') as UntypedFormArray;
    }

    @Input() lastSessionId: number;
    @Input() mainForm: UntypedFormGroup;
    @Input() refreshChanges$: Observable<void>;
    readonly dateFormat = moment.localeData().longDateFormat(this.formats.display.dateInput).toLowerCase();
    repetitionsForm: UntypedFormGroup;
    tableColumns = ['weekCheck', 'hour', 'd1', 'd2', 'd3', 'd4', 'd5', 'd6', 'd7'];
    weekDays: string[];
    get sessionsHour(): moment.Moment[] {
        return this._hourRows.controls.map(hourGroup => hourGroup.get('hour').value);
    }

    get disabledWeekaysCodes(): string[] {
        return Array.from(this._disabledWeekdaysCodes);
    }

    dateTimeFormats = DateTimeFormats;

    constructor(
    ) { }

    ngOnInit(): void {
        // FormGroup creation
        if (!this.mainForm.get('repetitions')) {
            this.repetitionsForm = this._fb.group({
                dates: this._fb.group({
                    start: null,
                    end: null
                }),
                hourRows: this._fb.array([])
            });
            this.mainForm.addControl('repetitions', this.repetitionsForm);
        } else {
            this.repetitionsForm = this.mainForm.get('repetitions') as UntypedFormGroup;
            this._isExistingData = true;
        }

        combineLatest([
            this.repetitionsForm.get('dates.start').valueChanges,
            this.repetitionsForm.get('dates.end').valueChanges
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([startDate, endDate]: [moment.Moment, moment.Moment]) => {
                if (moment.isMoment(startDate) && moment.isMoment(endDate)) {
                    this.refreshDisabledControls(startDate, endDate);
                }
            });

        this.weekDays = moment.weekdaysShort(true);
        if (!this._isExistingData) {
            if (this.lastSessionId) {
                this._sessionsService.session.get$()
                    .pipe(first(s => s !== null))
                    .subscribe(session => {
                        this.repetitionsForm.get('dates.start').setValue(
                            moment(session.start_date).set({ h: 0, m: 0, s: 0, ms: 0 }).add(1, 'd')
                        );
                        this.repetitionsForm.get('dates.end').setValue(
                            moment(session.start_date).set({ h: 0, m: 0, s: 0, ms: 0 }).add(7, 'd')
                        );
                        this.addHourRow(moment().set({
                            h: moment(session.start_date).get('h'),
                            m: moment(session.start_date).get('m'),
                            s: 0,
                            ms: 0
                        }));
                    });
            } else {
                this.repetitionsForm.get('dates.start').setValue(
                    moment().set({ h: 0, m: 0, s: 0, ms: 0 })
                );
                this.repetitionsForm.get('dates.end').setValue(
                    moment().set({ h: 0, m: 0, s: 0, ms: 0 }).add(6, 'd')
                );
                const defaultDate = moment().set({ h: 10, m: 0, s: 0, ms: 0 });
                this.addHourRow(defaultDate);
            }
        }

        // DATE VALIDATORS
        this.repetitionsForm.get('dates.start').setValidators([
            Validators.required,
            dateTimeValidator(dateIsSameOrBefore, 'startDateAfterEndDate', this.repetitionsForm.get('dates.end'))
        ]);
        this.repetitionsForm.get('dates.end').setValidators([
            Validators.required,
            dateTimeValidator(dateIsSameOrAfter, 'endDateBeforeStartDate', this.repetitionsForm.get('dates.start'))
        ]);
        joinCrossValidations([
            this.repetitionsForm.get('dates.start'),
            this.repetitionsForm.get('dates.end')
        ], this._onDestroy);

        this.refreshChanges$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this._ref.detectChanges();
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    isPartiallySelectedWeek(hourIndex: number): boolean {
        const row = this._hourRows.at(hourIndex) as UntypedFormGroup;
        const totalSelected = this.getWeekTotalSelectedCtrls(row.controls);
        return totalSelected > 0 && totalSelected < 7;
    }

    isTotallySelectedWeek(hourIndex: number): boolean {
        const row = this._hourRows.at(hourIndex) as UntypedFormGroup;
        const totalSelected = this.getWeekTotalSelectedCtrls(row.controls);
        return totalSelected === 7;
    }

    onWeekCheckboxClick(isChecked: boolean, hourIndex: number): void {
        const row = this._hourRows.at(hourIndex) as UntypedFormGroup;
        Object.keys(row.controls).forEach((dayCtrlName: string, i: number) => {
            if (i > 0) {
                row.controls[dayCtrlName].setValue(isChecked);
            }
        });
    }

    isPartiallySelectedDay(columnName: string): boolean {
        const totalSelected = this.getDayTotalSelectedCtrls(this._hourRows.controls, columnName);
        return totalSelected > 0 && totalSelected < this._hourRows.controls.length;
    }

    isTotallySelectedDay(columnName: string): boolean {
        const totalSelected = this.getDayTotalSelectedCtrls(this._hourRows.controls, columnName);
        return totalSelected === this._hourRows.controls.length;
    }

    onDayCheckboxClick(isChecked: boolean, columnName: string): void {
        this._hourRows.controls.forEach((row: any) => {
            row.get(columnName).setValue(isChecked);
        });
    }

    openNewHourDialog(): void {
        this._matDialog.open(NewHourDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe((result: AddHourResult) => {
                if (result) {
                    if (result.addHourMode === AddSessionsHourMode.fixed) {
                        const time = moment(result.fixedTime, 'HH:mm');
                        if (!this.sessionsHour.find(dt => dt.isSame(time))) {
                            this.addHourRow(time);
                        }
                    } else {
                        for (
                            let i = moment(result.interval.startTime, 'HH:mm');
                            i.isSameOrBefore(moment(result.interval.endTime, 'HH:mm'));
                            i = i.clone().add(result.interval.minuteInterval, 'm')
                        ) {
                            if (!this.sessionsHour.find(dt => dt.isSame(i))) {
                                this.addHourRow(i);
                            }
                        }
                    }
                    const startDate = this.repetitionsForm.get('dates.start').value;
                    const endDate = this.repetitionsForm.get('dates.end').value;
                    this.refreshDisabledControls(startDate, endDate);
                    this._ref.markForCheck();
                }
            });
    }

    deleteRow(rowIndex: number): void {
        this._hourRows.removeAt(rowIndex);
    }

    private refreshDisabledControls(startDate: moment.Moment, endDate: moment.Moment): void {
        const firstDayOfWeek = moment.localeData().firstDayOfWeek();
        const daysDifference = endDate.diff(startDate, 'days');
        const isoWeekdaysNums = [];
        const repStartDateWeekday = startDate.isoWeekday();
        for (let i = 0; i <= daysDifference; i++) {
            if (repStartDateWeekday + i <= 7) {
                isoWeekdaysNums.push(repStartDateWeekday + i);
            } else {
                isoWeekdaysNums.push((repStartDateWeekday + i) - 7);
            }
        }
        this._disabledWeekdaysCodes.clear();
        this._hourRows.controls.forEach((row: any) => {
            Object.keys(row.controls).forEach((ctrlName: string) => {
                if (ctrlName !== 'hour') {
                    let dayNum = Number(ctrlName.slice(1));
                    if (firstDayOfWeek === 0) { // sunday
                        if (dayNum === 1) {
                            dayNum = 7;
                        } else {
                            dayNum = dayNum - 1;
                        }
                    }
                    if (isoWeekdaysNums.includes(dayNum)) {
                        row.controls[ctrlName].enable();
                    } else {
                        this._disabledWeekdaysCodes.add(ctrlName);
                        row.controls[ctrlName].disable();
                        row.controls[ctrlName].setValue(false);
                    }
                }
            });
        });
    }

    private addHourRow(time: moment.Moment): void {
        const addRow = (i: number, hour: moment.Moment): void => {
            this._hourRows.insert(i, this._fb.group({
                hour,
                d1: false,
                d2: false,
                d3: false,
                d4: false,
                d5: false,
                d6: false,
                d7: false
            }));
        };

        if (!this._hourRows.controls.length) {
            addRow(0, time);
        } else {
            for (let i = 0; i < this._hourRows.controls.length; i++) {
                if (this._hourRows.controls[i].value.hour.isAfter(time)) {
                    addRow(i, time);
                    break;
                } else if (i === this._hourRows.controls.length - 1) {
                    addRow(i + 1, time);
                    break;
                }
            }
        }
    }

    private getWeekTotalSelectedCtrls(ctrls: { [key: string]: AbstractControl }): number {
        let totalSelected = 0;
        Object.keys(ctrls).forEach((dayCtrlName: string, i: number) => {
            if (i > 0 && ctrls[dayCtrlName].value) {
                totalSelected++;
            }
        });
        return totalSelected;
    }

    private getDayTotalSelectedCtrls(ctrls: AbstractControl[], columnName: string): number {
        let totalSelected = 0;
        ctrls.forEach((row: any) => {
            if (row.get(columnName).value) {
                totalSelected++;
            }
        });
        return totalSelected;
    }
}

interface AddHourResult {
    addHourMode: AddSessionsHourMode;
    fixedTime: string;
    interval: {
        startTime: string;
        endTime: string;
        minuteInterval: number;
    };
}
