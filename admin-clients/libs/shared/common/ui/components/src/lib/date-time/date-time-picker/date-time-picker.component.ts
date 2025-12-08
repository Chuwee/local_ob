import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { NgClass } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component,
    DoCheck, HostBinding, HostListener, Inject, Input, numberAttribute, OnDestroy, OnInit, Optional, Self
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    AbstractControl, ControlValueAccessor, UntypedFormControl,
    NgControl, ValidationErrors, ValidatorFn, ReactiveFormsModule
} from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { MatDatepicker, MatDatepickerInput } from '@angular/material/datepicker';
import { MatDivider } from '@angular/material/divider';
import { MatError, MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { LongDateFormatKey } from 'moment';
import moment from 'moment-timezone';
import { BehaviorSubject, distinctUntilChanged, Subject, takeUntil } from 'rxjs';
import { INPUT_TIMEPICKER_FORMAT, TimePickerComponent } from '../time-picker/time-picker.component';

@Component({
    selector: 'app-date-time-picker',
    templateUrl: './date-time-picker.component.html',
    styleUrls: ['./date-time-picker.component.scss'],
    imports: [
        FlexLayoutModule,
        MatDatepicker,
        MatDatepickerInput,
        MatDivider,
        MatError,
        MatFormField,
        MatIcon,
        MatIconButton,
        MatInput,
        MatLabel,
        MatSuffix,
        TimePickerComponent,
        FormControlErrorsComponent,
        ReactiveFormsModule,
        NgClass
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DateTimePickerComponent implements ControlValueAccessor, OnInit, OnDestroy, DoCheck {
    private _onDestroy = new Subject<void>();
    private _dateTimeValueSubj = new BehaviorSubject<string>(null);
    private _disabledSubj = new BehaviorSubject<boolean>(false);
    private _cvaCtrl: UntypedFormControl;
    private _idNum = DateTimePickerComponent.nextId++;
    private _defaultTime: string;
    private _defaultTimeValue: number[];

    static nextId = 0;

    readonly controlType = 'DateTimePickerFieldControl';
    @HostBinding() readonly id = `${this.controlType}-${this._idNum}`;
    readonly dateFormat: string;
    @Input() label: string;
    @Input() required = false;
    @Input({ transform: numberAttribute }) maxErrors: number;
    @Input() min: string | moment.Moment;
    @Input() max: string | moment.Moment;
    currentErrors: string[];
    datePickerCtrl: UntypedFormControl;
    timePickerCtrl: UntypedFormControl;

    get defaultTime(): string {
        return this._defaultTime;
    }

    @Input()
    set defaultTime(value: string) {
        this._defaultTime = value;
        const values = this._defaultTime.split(':')
            .map(v => Number(v))
            .filter(v => !isNaN(v));
        if (values.length === 2) {
            this._defaultTimeValue = values;
        } else {
            this._defaultTimeValue = null;
        }
    }

    constructor(
        @Optional() @Self() public ngControl: NgControl,
        @Inject(MAT_DATE_FORMATS) private readonly formats: MatDateFormats,
        private _ref: ChangeDetectorRef
    ) {
        this.dateFormat = moment.localeData().longDateFormat(this.formats.display.dateInput as LongDateFormatKey).toLowerCase();
        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }
    }

    ngOnInit(): void {
        this._cvaCtrl = this.ngControl?.control as UntypedFormControl;
        this.datePickerCtrl = new UntypedFormControl(
            { value: this._dateTimeValueSubj.value || null, disabled: this._disabledSubj.value },
            this.cvaHasErrors()
        );
        this.timePickerCtrl = new UntypedFormControl(
            { value: null, disabled: this._disabledSubj.value },
            this.cvaHasErrors()
        );

        this._disabledSubj
            .pipe(
                distinctUntilChanged(),
                takeUntil(this._onDestroy)
            )
            .subscribe(isDisabled => {
                if (isDisabled) {
                    this.datePickerCtrl.disable();
                    this.timePickerCtrl.disable();
                } else {
                    this.datePickerCtrl.enable();
                    this.timePickerCtrl.enable();
                }
            });

        // se utiliza para refrescar estado de validez cuando se ejecuta un updateValueAndValidity() desde fuera
        this._cvaCtrl?.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(_ => {
                this.datePickerCtrl.updateValueAndValidity();
                this.timePickerCtrl.updateValueAndValidity();
            });

        // éste se dispara cada vez que hacemos un setValue desde fuera al CVA
        this._dateTimeValueSubj
            .pipe(takeUntil(this._onDestroy))
            .subscribe(dateTimeValue => {
                if (!dateTimeValue) { // dateTimeValue es null; lo propagamos
                    if (this.datePickerCtrl.value) {
                        this.datePickerCtrl.setValue(dateTimeValue, { emitEvent: false });
                    }
                    if (this.timePickerCtrl.value) {
                        this.timePickerCtrl.setValue(dateTimeValue);
                    }
                    this.datePickerCtrl.updateValueAndValidity();
                    this.timePickerCtrl.updateValueAndValidity();
                } else {
                    const dateTimeMomentValue = moment(dateTimeValue);
                    const datePickerMomentValue = moment(this.datePickerCtrl.value);
                    if (!dateTimeMomentValue.isSame(datePickerMomentValue, 'd')) {
                        this.datePickerCtrl.setValue(dateTimeValue, { emitEvent: false });
                    }
                    const timeValue = dateTimeMomentValue.format(INPUT_TIMEPICKER_FORMAT);
                    if (timeValue !== this.timePickerCtrl.value) {
                        this.timePickerCtrl.setValue(timeValue);
                    }
                    this.datePickerCtrl.updateValueAndValidity();
                    this.timePickerCtrl.updateValueAndValidity();
                }
            });

        this.datePickerCtrl.valueChanges
            .pipe(
                distinctUntilChanged(),
                takeUntil(this._onDestroy)
            )
            .subscribe(dateValue => {
                const dateMomentValue = dateValue && moment(dateValue);
                const timeValue = this.timePickerCtrl.value;
                const timeMomentValue = timeValue && moment(timeValue, INPUT_TIMEPICKER_FORMAT);
                if (dateMomentValue?.isValid() && timeMomentValue?.isValid()) {
                    const dateTimeValue = dateMomentValue
                        .set({ h: timeMomentValue.get('h'), m: timeMomentValue.get('m'), s: 0, ms: 0 })
                        .format();
                    this.onChange(dateTimeValue);
                    this.datePickerCtrl.updateValueAndValidity();
                    this.timePickerCtrl.updateValueAndValidity();
                } else if (dateMomentValue && this.timePickerCtrl.value == null && this._defaultTimeValue) {
                    dateMomentValue.set({ h: this._defaultTimeValue[0], m: this._defaultTimeValue[1] });
                    this.timePickerCtrl.setValue(dateMomentValue.format(INPUT_TIMEPICKER_FORMAT));
                } else {
                    this.onChange(null);
                    this.datePickerCtrl.updateValueAndValidity();
                    this.timePickerCtrl.updateValueAndValidity();
                }
            });

        this.timePickerCtrl.valueChanges
            .pipe(
                distinctUntilChanged(),
                takeUntil(this._onDestroy)
            )
            .subscribe(timeValue => {
                const dateValue = this.datePickerCtrl.value;
                const dateMomentValue = dateValue && moment(dateValue);
                const timeMomentValue = timeValue && moment(timeValue, INPUT_TIMEPICKER_FORMAT);

                if (dateMomentValue?.isValid() && timeMomentValue?.isValid()) {
                    const dateTimeValue = dateMomentValue
                        .set({ h: timeMomentValue.get('h'), m: timeMomentValue.get('m'), s: 0, ms: 0 })
                        .format();
                    this.onChange(dateTimeValue);
                    this.datePickerCtrl.updateValueAndValidity();
                    this.timePickerCtrl.updateValueAndValidity();
                } else {
                    this.onChange(null);
                    this.datePickerCtrl.updateValueAndValidity();
                    this.timePickerCtrl.updateValueAndValidity();
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    // HACK necesario para detectar y propagar el estado touched del ControlValueAccessor
    // hacia sus descendientes, ya que hasta la v14 de Angular el CVA no tiene API para ello
    // https://github.com/angular/angular/issues/10887#issuecomment-1079505423
    ngDoCheck(): void {
        if (this._cvaCtrl?.touched) {
            this.datePickerCtrl.markAsTouched();
            this.timePickerCtrl.markAsTouched();
            this._ref.markForCheck();
        }
        if (this.timePickerCtrl.touched) {
            this.datePickerCtrl.markAsTouched();
            this._cvaCtrl?.markAsTouched();
            this._ref.markForCheck();
        }
    }

    @HostListener('blur')
    onBlur(): void {
        this.onTouched();
    }

    writeValue(value: string): void {
        if (moment.isMoment(value)) {
            console.warn(`Warning: value ${value.format()} provided to date-time-picker formControl is a Moment object, ` +
                'it must be a standard formatted dateTime string in order to avoid redundant valueChanges executions ' +
                '(with some undesierd side-effects).');
        }
        const momentValue = value ? moment(value) : null;
        this._dateTimeValueSubj.next(momentValue?.isValid() ? momentValue.format() : null);
    }

    registerOnChange(fn: (time: string) => void): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this._disabledSubj.next(isDisabled);
    }

    private onChange = (_: string): void => { /* noop*/ };
    private onTouched = (): void => { /* noop*/ };

    private cvaHasErrors(): ValidatorFn {
        return (_: AbstractControl): ValidationErrors | null => this.ngControl?.errors;
    }
}
