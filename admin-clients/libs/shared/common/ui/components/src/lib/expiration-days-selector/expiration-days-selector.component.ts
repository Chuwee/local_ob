import { MinNumberPipe } from '@admin-clients/shared/utility/pipes';
import { FocusMonitor } from '@angular/cdk/a11y';
import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef,
    EventEmitter, HostBinding, HostListener, Input, OnDestroy, Optional, Output, Self
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ControlValueAccessor, NgControl, ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl } from '@angular/forms';
import { MatFormFieldControl } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { ExpirationDaysSelectorOptions, ExpirationDaysSelectorTranslationKeys } from './expiration-days-selector.model';

@Component({
    selector: 'app-expiration-days-selector',
    templateUrl: './expiration-days-selector.component.html',
    styleUrls: ['./expiration-days-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{ provide: MatFormFieldControl, useExisting: ExpirationDaysSelectorComponent }],
    imports: [
        MinNumberPipe,
        MatInput,
        MatOption,
        MatSelect,
        FlexLayoutModule,
        ReactiveFormsModule,
        TranslatePipe
    ]
})
export class ExpirationDaysSelectorComponent implements ControlValueAccessor, MatFormFieldControl<number>, OnDestroy {
    private _placeholder: string;
    private _required = false;
    private _disabled = false;

    static nextId = 0;

    days: UntypedFormControl;
    selector: UntypedFormControl;
    stateChanges = new Subject<void>();
    focused = false;
    controlType = 'expiration-days-selector';

    readonly selectorOptions = ExpirationDaysSelectorOptions;

    @Output()
    valueChanged: EventEmitter<void> = new EventEmitter();

    @HostBinding()
    id = `expiration-days-selector-${ExpirationDaysSelectorComponent.nextId++}`;

    @HostBinding('attr.aria-describedby')
    describedBy = '';

    @HostBinding('class.floating-label')
    get shouldLabelFloat(): boolean { return this.focused || !this.empty; }

    get errorState(): boolean {
        return this.ngControl.errors !== null && !!this.ngControl.touched;
    }

    get empty(): boolean {
        return !this.days.value;
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
        this._disabled ? this.days.disable() : this.days.enable();
        this._disabled ? this.selector.disable() : this.selector.enable();
        this.stateChanges.next(null);
    }

    @Input()
    get value(): number | null {
        return this.days.value;
    }

    set value(num: number | null) {
        this.days.patchValue(num);
        if (num === 0 && this.selector.value !== this.selectorOptions.default) {
            this.selector.patchValue(this.selectorOptions.default);
        } else if (num > 0 && this.selector.value !== this.selectorOptions.custom) {
            this.selector.patchValue(this.selectorOptions.custom);
        }
        this.stateChanges.next(null);
    }

    @Input()
    translationKeys: ExpirationDaysSelectorTranslationKeys;

    constructor(
        private _focusMonitor: FocusMonitor,
        private _elementRef: ElementRef<HTMLElement>,
        private _ref: ChangeDetectorRef,
        fb: UntypedFormBuilder,
        @Optional() @Self() public ngControl: NgControl
    ) {
        this.days = fb.control(null);
        this.selector = fb.control(null);

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
        if ((event.target as Element).tagName.toLowerCase() !== 'input'
            && this.selector.value !== this.selectorOptions.default) {
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
        this.valueChanged.emit();
    }

    selectorChanged(): void {
        if (this.selector.value === this.selectorOptions.default) {
            this.value = 0;
        } else if (this.selector.value === this.selectorOptions.custom) {
            this.value = null;
        }
        this.onChange(this.value);
        this.valueChanged.emit();
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
