import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FocusMonitor } from '@angular/cdk/a11y';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import {
    AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, EventEmitter, HostBinding, inject,
    Input, OnDestroy, Output, ViewChild, OnInit, DestroyRef
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ControlValueAccessor, NgControl } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { DateRange } from '@angular/material/datepicker';
import { MatFormFieldControl } from '@angular/material/form-field';
import moment from 'moment-timezone';
import { Subject } from 'rxjs';
import { DateRangeShortcutElement } from '../../models/date-range-shortcut.enum';
import { PopoverComponent } from '../../popover/popover.component';
import { DateRangePickerComponent } from '../date-range-picker/date-range-picker.component';
import { DateTimeModule } from '../date-time.module';
import {
    PopoverDateRangePickerButtonComponent
} from './button/popover-date-range-picker-button.component';

interface VmDateRangePicker {
    startDate: string;
    endDate: string;
}

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{ provide: MatFormFieldControl, useExisting: PopoverDateRangePickerComponent }],
    imports: [
        PopoverComponent, DateTimeModule, FlexLayoutModule, PopoverDateRangePickerButtonComponent, DateRangePickerComponent
    ],
    selector: 'app-popover-date-range-picker',
    templateUrl: './popover-date-range-picker.component.html'
})
export class PopoverDateRangePickerComponent implements MatFormFieldControl<VmDateRangePicker>, ControlValueAccessor, AfterViewInit, OnDestroy, OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #focusMonitor = inject(FocusMonitor);
    readonly #elementRef = inject<ElementRef<HTMLElement>>(ElementRef);
    readonly #FORMATS = inject(MAT_DATE_FORMATS);
    #placeholder: string;
    #required = false;
    #disabled = false;
    #value: VmDateRangePicker;
    #externalTriggerRef: HTMLElement;

    @ViewChild(DateRangePickerComponent, { static: true }) private readonly _dateRangePicker: DateRangePickerComponent;
    @ViewChild(PopoverComponent, { static: true }) private readonly _popover: PopoverComponent;
    static nextId = 0;

    readonly ngControl = inject(NgControl, { optional: true, self: true }); //for form driven use cases
    focused = false;
    readonly stateChanges = new Subject<void>();
    @HostBinding() id = `app-popover-date-range-picker-${PopoverDateRangePickerComponent.nextId++}`;
    @HostBinding('attr.aria-describedby') describedBy = '';
    @HostBinding('class.floating-label')
    get shouldLabelFloat(): boolean { return this.focused || !this.empty; }

    readonly controlType = 'app-popover-date-range-picker';
    errorState = false;

    readonly dateFormat = moment.localeData().longDateFormat(this.#FORMATS.display.dateInput).toLowerCase();
    readonly dateTimeFormats = DateTimeFormats;
    selectedDate: DateRange<moment.Moment>;
    afterViewInit = false;

    @Input() initialDate: { startDate: string; endDate: string }; //for non form driven use cases
    @Input() contentNoPadding = false;
    @Input() isUTC = true;
    @Input() extraShortcuts: DateRangeShortcutElement[];
    @Input() set externalTriggerRef(value: HTMLElement) {
        if (!value) return;

        if (this.#externalTriggerRef) {
            this.#focusMonitor.stopMonitoring(this.#externalTriggerRef);
        }

        this.#externalTriggerRef = value;

        this.#focusMonitor.monitor(this.#externalTriggerRef, true).subscribe(origin => {
            if (!this.disabled) {
                if (this.focused && !origin) {
                    this.onTouched();
                }
                this.focused = !!origin;
                this.stateChanges.next(null);
            }
        });
    }

    @Output() changeEmitter = new EventEmitter<VmDateRangePicker>();

    constructor() {
        this.#focusMonitor.monitor(this.#elementRef, true).subscribe(origin => {
            if (!this.disabled) {
                if (this.focused && !origin) {
                    this.onTouched();
                }
                this.focused = !!origin;
                this.stateChanges.next(null);
            }
        });

        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }
    }

    onContainerClick(): void {
        this.focused = true;
    }

    onChange = (_: VmDateRangePicker): void => { /* noop */ };
    onTouched = (): void => { /* noop */ };

    get value(): VmDateRangePicker {
        return this.#value;
    }

    set value(value: VmDateRangePicker) {
        this.#value = value;
        this.onChange(value);
        this.stateChanges.next();
        this.#ref.markForCheck();
    }

    get placeholder(): string {
        return this.#placeholder;
    }

    set placeholder(placeHolder: string) {
        this.#placeholder = placeHolder;
        this.stateChanges.next();
    }

    get empty(): boolean {
        return !this.value;
    }

    @Input()
    get required(): boolean {
        return this.#required;
    }

    set required(req: boolean) {
        this.#required = coerceBooleanProperty(req);
        this.stateChanges.next();
    }

    @Input()
    get disabled(): boolean {
        return this.#disabled;
    }

    set disabled(dis: boolean) {
        this.#disabled = coerceBooleanProperty(dis);
        this.#ref.markForCheck();
    }

    ngOnDestroy(): void {
        this.stateChanges.complete();
        this.#focusMonitor.stopMonitoring(this.#elementRef);
        if (this.#externalTriggerRef) {
            this.#focusMonitor.stopMonitoring(this.#externalTriggerRef);
        }
    }

    //for form driven use cases
    writeValue(value: VmDateRangePicker): void {
        if (value && this.afterViewInit) {
            this._dateRangePicker.setParams(value);
            this.selectedDate = this._dateRangePicker.selectedDate;
        } else if (this.afterViewInit) {
            this._dateRangePicker.resetParams();
            this.selectedDate = null;
        }
        this.value = value ?
            {
                startDate: this._dateRangePicker.getDateRange().start,
                endDate: this._dateRangePicker.getDateRange().end
            } : null;
    }

    registerOnChange(fn: (_: VmDateRangePicker) => void): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this.disabled = isDisabled;
    }

    setDescribedByIds(ids: string[]): void {
        this.describedBy = ids.join(' ');
    }

    ngOnInit(): void {
        //for form driven use cases
        this.ngControl?.control.statusChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                const newState = this.ngControl ? this.ngControl.invalid && (this.ngControl.dirty || this.ngControl.touched) : false;
                if (newState !== this.errorState) {
                    this.errorState = newState;
                    this.stateChanges.next();
                }
            });
    }

    ngAfterViewInit(): void {
        this.afterViewInit = true;
        if (this.ngControl && this.value) { //for form driven use cases
            this._dateRangePicker.setParams(this.value);
            this.selectedDate = this._dateRangePicker.selectedDate;
        } else if (!this.ngControl && this.initialDate) { //for non form driven use cases
            this._dateRangePicker.setParams(this.initialDate);
            this.selectedDate = this._dateRangePicker.selectedDate;
        }
    }

    onApplyBtnClick(): void {
        if (this._dateRangePicker.selectedDate?.end) {
            //for form driven use cases
            this.value = {
                startDate: this._dateRangePicker.getDateRange().start,
                endDate: this._dateRangePicker.getDateRange().end
            };
            //for non form driven use cases
            this.changeEmitter.emit({
                startDate: this._dateRangePicker.getDateRange().start,
                endDate: this._dateRangePicker.getDateRange().end
            });

            this._dateRangePicker.setParams(this.value);
            this.selectedDate = this._dateRangePicker.selectedDate;
        } else {
            this._dateRangePicker.resetParams();
            this.selectedDate = null;
            //for form driven use cases
            this.value = null;
            //for non form driven use cases
            this.changeEmitter.emit(null);
        }
    }

    removeDate(): void {
        this._dateRangePicker.resetParams();
        this.selectedDate = null;
        //for form driven use cases
        this.value = null;
        //for non form driven use cases
        this.changeEmitter.emit(null);
    }

    open(): void {
        this._popover.openPopover();
    }
}
