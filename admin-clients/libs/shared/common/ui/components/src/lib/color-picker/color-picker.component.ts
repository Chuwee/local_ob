
import { FocusMonitor } from '@angular/cdk/a11y';
import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component, ElementRef,
    HostBinding, HostListener, inject,
    Input, OnDestroy, Optional, Self
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    ControlValueAccessor, FormBuilder,
    NgControl, ReactiveFormsModule
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldControl } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import { ColorEvent } from 'ngx-color';
import { ColorChromeModule } from 'ngx-color/chrome';
import { ColorCircleModule } from 'ngx-color/circle';
import { Subject } from 'rxjs';
import { DefaultColors } from './default-colors.enum';

@Component({
    imports: [
        CommonModule,
        FlexLayoutModule,
        TranslatePipe,
        MatButtonModule,
        MatIconModule,
        ReactiveFormsModule,
        ColorChromeModule,
        ColorCircleModule,
        SatPopoverModule
    ],
    selector: 'app-color-picker',
    templateUrl: './color-picker.component.html',
    styleUrls: ['./color-picker.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{ provide: MatFormFieldControl, useExisting: ColorPickerComponent }]
})
export class ColorPickerComponent implements ControlValueAccessor, MatFormFieldControl<string>, OnDestroy {
    private _fb = inject(FormBuilder);
    private _focusMonitor = inject(FocusMonitor);
    private _elementRef = inject(ElementRef<HTMLElement>);
    private _ref = inject(ChangeDetectorRef);
    private _placeholder: string;
    private _required = false;
    private _disabled = false;

    static nextId = 0;

    readonly defaultColors = Object.values(DefaultColors);

    @Input()
    customColors: string[];

    @Input()
    allowEmptyColor: boolean;

    tempColor = '#000';

    colorControl = this._fb.control(null as string);
    stateChanges = new Subject<void>();
    focused = false;
    get errorState(): boolean {
        return this.ngControl.errors !== null && !!this.ngControl.touched;
    }

    controlType = 'color-picker-input';

    @HostBinding()
    id = `color-picker-${ColorPickerComponent.nextId++}`;

    @HostBinding('attr.aria-describedby')
    describedBy = '';

    @HostBinding('class.floating-label')
    get shouldLabelFloat(): boolean { return this.focused || !this.empty; }

    @HostBinding()
    tabindex = 0;

    get empty(): boolean {
        return !this.colorControl.value;
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
        this._disabled ? this.colorControl.disable() : this.colorControl.enable();
        this.stateChanges.next(null);
    }

    @Input()
    get value(): string | null {
        return this.colorControl.value;
    }

    set value(color: string | null) {
        if (color) {
            this.tempColor = color;
        }
        this.colorControl.setValue(color);
        this.stateChanges.next(null);
    }

    constructor(@Optional() @Self() public ngControl: NgControl) {
        this._focusMonitor.monitor(this._elementRef, true)
            .subscribe(origin => {
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

    commitColor(): void {
        this.writeValue(this.tempColor);
        this.onChange(this.tempColor);
    }

    changeHandler($event: ColorEvent | string): void {
        if (typeof $event === 'string') {
            this.tempColor = $event;
        } else {
            this.tempColor = $event.color.hex;
        }
    }

    ngOnDestroy(): void {
        this.stateChanges.complete();
        this._focusMonitor.stopMonitoring(this._elementRef);
    }

    @HostListener('focus')
    onFocus(): void {
        this.setFocusToInput();
    }

    onChange = (_: unknown): void => { /* noop */ };
    onTouched = (): void => { /* noop */ };

    setDescribedByIds(ids: string[]): void {
        this.describedBy = ids.join(' ');
    }

    onContainerClick(event: MouseEvent): void {
        if ((event.target as Element).tagName.toLowerCase() !== 'input') {
            this.setFocusToInput();
        }
    }

    writeValue(num: string | null): void {
        this.value = num;
        this._ref.markForCheck();
    }

    registerOnChange(fn: (_: unknown) => void): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this.disabled = isDisabled;
    }

    focus(): void {
        this.setFocusToInput();
    }

    private setFocusToInput(): void {
        if (!this.disabled) {
            this.focused = true;
            this._ref.markForCheck();
        }
    }
}
