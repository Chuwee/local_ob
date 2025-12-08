import { AUTHENTICATION_SERVICE, AuthService } from '@admin-clients/shared/core/data-access';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { FocusMonitor } from '@angular/cdk/a11y';
import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, HostBinding, HostListener, Inject, Input, OnDestroy,
    OnInit, Optional, Self
} from '@angular/core';
import { ControlValueAccessor, UntypedFormBuilder, UntypedFormControl, NgControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldControl } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { Subject } from 'rxjs';
import { first } from 'rxjs/operators';

@Component({
    imports: [ReactiveFormsModule, LocalCurrencyPipe, MatInput],
    selector: 'app-currency-input',
    templateUrl: './currency-input.component.html',
    styleUrls: ['./currency-input.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{ provide: MatFormFieldControl, useExisting: CurrencyInputComponent }]
})
export class CurrencyInputComponent implements ControlValueAccessor, MatFormFieldControl<number>, OnInit, OnDestroy {
    private _placeholder: string;
    private _required = false;
    private _disabled = false;
    private _currencyCode: string;

    static nextId = 0;

    numberControl: UntypedFormControl;
    stateChanges = new Subject<void>();
    focused = false;
    get errorState(): boolean {
        return this.ngControl.errors !== null && !!this.ngControl.touched;
    }

    controlType = 'currency-input';
    currency: string;

    @HostBinding()
    id = `currency-input-${CurrencyInputComponent.nextId++}`;

    @HostBinding('attr.aria-describedby')
    describedBy = '';

    @HostBinding('class.floating-label')
    get shouldLabelFloat(): boolean { return this.focused || !this.empty; }

    @HostBinding()
    tabindex = 0;

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

    @Input()
    get currencyCode(): string {
        return this._currencyCode;
    }

    set currencyCode(value: string) {
        this._currencyCode = value;
        this.currency = this._currencyCode;
    }

    @Input() currencyFormat: 'wide' | 'narrow';

    constructor(
        fb: UntypedFormBuilder,
        private _focusMonitor: FocusMonitor,
        private _elementRef: ElementRef<HTMLElement>,
        @Inject(AUTHENTICATION_SERVICE) private _authSrv: AuthService,
        private _ref: ChangeDetectorRef,
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

    ngOnInit(): void {
        if (!this.currency) {
            this._authSrv.getLoggedUser$()
                .pipe(first(user => user !== null))
                .subscribe(user => this.currency = user.currency);
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

    writeValue(num: number | null): void {
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

    handleInput(): void {
        this.onChange(this.value);
    }

    focus(): void {
        this.setFocusToInput();
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
