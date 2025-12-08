import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import { NgTemplateOutlet } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, ContentChild, DoCheck, Input, OnDestroy, OnInit, Self, TemplateRef, inject, input
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    AbstractControl,
    ControlValueAccessor, FormBuilder, NgControl, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, takeUntil } from 'rxjs';
import { CurrencyInputComponent } from '../currency-input/currency-input.component';

@Component({
    selector: 'app-collection-input',
    imports: [
        TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent, CurrencyInputComponent, LocalCurrencyPipe,
        NgTemplateOutlet, MatFormFieldModule, MatInputModule, FlexLayoutModule, MatButtonModule
    ],
    templateUrl: './collection-input.component.html',
    styleUrls: ['./collection-input.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CollectionInputComponent implements ControlValueAccessor, OnInit, OnDestroy, DoCheck {
    private readonly _fb = inject(FormBuilder);
    private readonly _ref = inject(ChangeDetectorRef);

    private _onDestroy = new Subject<void>();
    private _disabled = false;
    private _avoidNegativeNumbers = false;
    private _currencyType = false;

    readonly createElement = this._fb.control(null as string | number);

    @Input() placeholder: string;
    @Input() buttonLabel: string;
    @Input() currencyType: string;
    @Input() label: string;
    @Input() maxLength?: number;

    @Input()
    get avoidNegativeNumbers(): boolean {
        return this._avoidNegativeNumbers;
    }

    set avoidNegativeNumbers(value: BooleanInput) {
        this._avoidNegativeNumbers = coerceBooleanProperty(value);
    }

    @ContentChild('elementsCollectionTemplate') readonly elementsCollectionTemplateRef?: TemplateRef<unknown>;

    @Input()
    get disabled(): boolean {
        return this._disabled;
    }

    set disabled(value: BooleanInput) {
        this._disabled = coerceBooleanProperty(value);
        this._disabled ? this.createElement.disable() : this.createElement.enable();
    }

    readonly $requiredElements = input<boolean>(false, { alias: 'required' });

    constructor(@Self() public ngControl: NgControl) {
        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }
    }

    ngOnInit(): void {
        if (this.ngControl.control.hasValidator(Validators.required) || this.$requiredElements()) {
            this.createElement.setValidators([this.atLeastOneInCollection()]);
        }
        if (this._avoidNegativeNumbers) {
            this.createElement.addValidators([this.nonNegativeNumbers()]);
        }
        if (this.maxLength) {
            this.createElement.addValidators([this.maxLengthValidator()]);
        }
        //Remove element
        this.ngControl.control?.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(_ => {
                this.createElement.markAsTouched();
                this.createElement.updateValueAndValidity();
            });
    }

    nonNegativeNumbers(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (control.value < 0) {
                return { min: 0, actual: control.value };
            }
            return null;
        };
    }

    maxLengthValidator(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (control.value?.length === this.maxLength) {
                return { maxlength: true };
            }
            return null;
        };
    }

    atLeastOneInCollection(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!control.value && !this.ngControl.control.value?.length) {
                return { atLeastOneInCollection: true };
            }
            return null;
        };
    }

    ngDoCheck(): void {
        //Hack for update touched state
        if (this.ngControl.touched) {
            this.createElement.markAsTouched();
            this._ref.markForCheck();
        }
        if (this.createElement.touched) {
            this.ngControl.control.markAsTouched();
            this._ref.markForCheck();
        }
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    onChange = (_: unknown): void => null;
    onTouched = (): void => null;

    addElement(): void {
        if (this.createElement.valid) {
            const elements = this.ngControl.control.value || [];
            elements.push(this.createElement.value);
            this.onChange(elements);
            this.createElement.reset(null, { emitEvent: false });
        } else {
            this.createElement.markAsTouched();
        }
    }

    writeValue(): void {
        /* noop*/
    }

    registerOnChange(fn: () => unknown): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this.onTouched = fn;
    }

    setDisabledState?(isDisabled: boolean): void {
        this.disabled = isDisabled;
        this._ref.markForCheck();
    }
}
