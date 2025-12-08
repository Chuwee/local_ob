import { FocusMonitor } from '@angular/cdk/a11y';
import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, HostBinding, HostListener, Input, OnDestroy, Optional, Self
} from '@angular/core';
import { ControlValueAccessor, UntypedFormBuilder, UntypedFormControl, NgControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldControl, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { Subject } from 'rxjs';

@Component({
    imports: [ReactiveFormsModule, MatInput, MatSuffix],
    selector: 'app-percentage-input',
    templateUrl: './percentage-input.component.html',
    styleUrls: ['./percentage-input.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{ provide: MatFormFieldControl, useExisting: PercentageInputComponent }]
})
export class PercentageInputComponent implements ControlValueAccessor, MatFormFieldControl<number>, OnDestroy {

    private _placeholder: string;
    private _required = false;
    private _disabled = false;

    static nextId = 0;

    numberControl: UntypedFormControl;
    stateChanges = new Subject<void>();
    focused = false;
    controlType = 'percentage-input';
    userCurrency: string;

    @HostBinding()
    id = `currency-input-${PercentageInputComponent.nextId++}`;

    @HostBinding('attr.aria-describedby')
    describedBy = '';

    @HostBinding('class.floating-label')
    get shouldLabelFloat(): boolean { return this.focused || !this.empty; }

    @HostBinding()
    tabindex = 0;

    get errorState(): boolean {
        return this.ngControl.errors !== null && !!this.ngControl.touched;
    }

    get empty(): boolean {
        return !this.numberControl.value;
    }

    @Input()
    get placeholder(): string {
        return this._placeholder;
    }

    set placeholder(value: string) {
        this._placeholder = value;
        this.stateChanges.next(null);
    }

    @Input()
    get required(): boolean {
        return this._required;
    }

    set required(value: BooleanInput) {
        this._required = coerceBooleanProperty(value);
        this.stateChanges.next(null);
    }

    @Input()
    get disabled(): boolean {
        return this._disabled;
    }

    set disabled(value: BooleanInput) {
        this._disabled = coerceBooleanProperty(value);
        this._disabled ? this.numberControl.disable() : this.numberControl.enable();
        this.stateChanges.next(null);
    }

    @Input()
    get value(): number | null {
        return this.numberControl.value;
    }

    set value(num: number | null) {
        this.numberControl.setValue(num);
        this.stateChanges.next(null);
    }

    constructor(
        private _focusMonitor: FocusMonitor,
        private _elementRef: ElementRef<HTMLElement>,
        private _ref: ChangeDetectorRef,
        fb: UntypedFormBuilder,
        @Optional() @Self() public ngControl: NgControl
    ) {
        this.numberControl = fb.control(null);

        _focusMonitor.monitor(_elementRef, true).subscribe(origin => {
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

    @HostListener('focus')
    onFocus(): void {
        this.setFocusToInput();
    }

    @HostListener('wheel', ['$event'])
    onWheel(event: Event): void {
        event.preventDefault();
    }

    onChange = (_: unknown): void => null;
    onTouched = (): void => null;

    ngOnDestroy(): void {
        this.stateChanges.complete();
        this._focusMonitor.stopMonitoring(this._elementRef);
    }

    setDescribedByIds(ids: string[]): void {
        this.describedBy = ids.join(' ');
    }

    onContainerClick(event: MouseEvent): void {
        if ((event.target as Element).tagName.toLowerCase() !== 'input') {
            this.setFocusToInput();
        }
    }

    writeValue(num: number | null): void {
        this.value = num;
        this._ref.markForCheck();
    }

    registerOnChange(fn: (((_: unknown) => void))): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: (() => void)): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this.disabled = isDisabled;
    }

    handleInput(): void {
        this.onChange(this.value);
    }

    private setFocusToInput(): void {
        if (!this.disabled) {
            this.focused = true;
            this._ref.markForCheck();
            const input = this._elementRef.nativeElement.querySelector('input');
            setTimeout(() => {
                this._focusMonitor.focusVia(input, 'program');
                input.select();
            });
        }
    }
}
