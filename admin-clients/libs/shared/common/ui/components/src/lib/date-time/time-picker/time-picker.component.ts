import { AsyncPipe, NgIf } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, DoCheck, HostBinding, HostListener, Input, OnDestroy, OnInit, Optional, Self
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    AbstractControl, ControlValueAccessor,
    UntypedFormControl, NgControl, ValidationErrors, ValidatorFn, ReactiveFormsModule
} from '@angular/forms';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatInput } from '@angular/material/input';
import moment from 'moment-timezone';
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';
import { BehaviorSubject, distinctUntilChanged, Subject, takeUntil } from 'rxjs';

export const INPUT_TIMEPICKER_FORMAT = 'HH:mm';

@Component({
    selector: 'app-time-picker',
    templateUrl: './time-picker.component.html',
    styleUrls: ['./time-picker.component.scss'],
    imports: [
        NgxMaterialTimepickerModule,
        MatFormField,
        MatIcon,
        MatIconButton,
        MatInput,
        MatLabel,
        MatSuffix,
        ReactiveFormsModule,
        NgIf,
        AsyncPipe,
        FlexLayoutModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimePickerComponent implements ControlValueAccessor, OnInit, OnDestroy, DoCheck {
    private _onDestroy = new Subject<void>();
    private _timeValueSubj = new BehaviorSubject<string>(null);
    private _disabledSubj = new BehaviorSubject<boolean>(false);
    private _label: string;
    private _cvaCtrl: UntypedFormControl;
    private _idNum = TimePickerComponent.nextId++;

    static nextId = 0;

    readonly controlType = 'TimePickerFieldControl';
    @HostBinding() readonly id = `${this.controlType}-${this._idNum}`;

    timeValue$ = this._timeValueSubj.asObservable();
    disabled$ = this._disabledSubj.asObservable();
    timePickerCtrl: UntypedFormControl;
    showsAPM = moment.localeData().longDateFormat('LT').indexOf('A') !== -1;

    @Input() isLabelSet = false;
    @Input()
    set label(value: string) {
        if (value) {
            this._label = value;
            this.isLabelSet = true;
        }
    }

    get label(): string {
        return this._label;
    }

    constructor(
        @Optional() @Self() public ngControl: NgControl,
        private _ref: ChangeDetectorRef
    ) {
        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }
    }

    ngOnInit(): void {
        this._cvaCtrl = this.ngControl?.control as UntypedFormControl;
        this.timePickerCtrl = new UntypedFormControl(
            { value: this._timeValueSubj.value || null, disabled: this._disabledSubj.value },
            this.cvaHasErrors()
        );

        this._disabledSubj
            .pipe(
                distinctUntilChanged(),
                takeUntil(this._onDestroy)
            )
            .subscribe(isDisabled => {
                if (isDisabled) {
                    this.timePickerCtrl.disable();
                } else {
                    this.timePickerCtrl.enable();
                }
            });

        this._cvaCtrl?.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(timeValue => {
                this.timePickerCtrl.setValue(timeValue);
            });

        this.timePickerCtrl.valueChanges
            .pipe(
                distinctUntilChanged(),
                takeUntil(this._onDestroy)
            )
            .subscribe(inputValue => {
                const momentTimeValue = inputValue && moment(inputValue, inputValue.split(' ').length > 1 ? 'h:mm A' : 'H:mm');

                if ((inputValue && momentTimeValue.format('LT')) !== this.timePickerCtrl.value) {
                    this.timePickerCtrl.setValue(
                        inputValue && momentTimeValue.isValid() ? momentTimeValue.format('LT') : null
                    );
                }
                if ((inputValue && momentTimeValue.format(INPUT_TIMEPICKER_FORMAT)) !== this.ngControl.control.value) {
                    this.onChange(inputValue && momentTimeValue.isValid() ? momentTimeValue.format(INPUT_TIMEPICKER_FORMAT) : null);
                }
                this.timePickerCtrl.updateValueAndValidity();
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
            this.timePickerCtrl.markAsTouched();
            this._ref.markForCheck();
        }
    }

    @HostListener('blur')
    onBlur(): void {
        this.onTouched();
    }

    // ControlValueAccessor methods impl:

    writeValue(value: string): void {
        const valueIsValid = value && /^([01][0-9]|2[0-3]):[0-5][0-9]$/.test(value);
        if (value && !valueIsValid) {
            console.warn(`${this.id} write value [${value}] is not in a valid format`);
        }
        const nextValue = valueIsValid ? moment(value, INPUT_TIMEPICKER_FORMAT).format('LT') : null;
        this._timeValueSubj.next(nextValue);
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
