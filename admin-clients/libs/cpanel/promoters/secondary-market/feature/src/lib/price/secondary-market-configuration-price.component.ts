/* eslint-disable @typescript-eslint/dot-notation */
import { SecondaryMarketConfig } from '@admin-clients/cpanel/promoters/secondary-market/data-access';
import { CurrencyInputComponent, PercentageInputComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { LocalNumberPipe, LocalCurrencyPipe, ErrorMessage$Pipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, input, signal, OnInit, AfterViewInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators
} from '@angular/forms';
import { MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';

enum PriceRestrictionModifier {
    sameAs = 'SAME_AS',
    moreThan = 'MORE_THAN',
    lessThan = 'LESS_THAN'
}

function calculateValue(priceRestrictionModifier: PriceRestrictionModifier, value: number): number {
    let result = 0;
    switch (priceRestrictionModifier) {
        case PriceRestrictionModifier.sameAs:
            result = 100;
            break;
        case PriceRestrictionModifier.moreThan:
            result = 100 + value;
            break;
        case PriceRestrictionModifier.lessThan:
            result = 100 - value;
            break;
    }
    return result;
}

function addMinGreaterThanMaxError(formGroup: FormGroup): void {
    const error = { minGreaterThanMax: true };
    if (formGroup.dirty) {
        Object.values(formGroup.controls).map(control => {
            control.markAsTouched();
            control.setErrors({ ...(control.errors ?? {}), ...error });
        });
        formGroup.setErrors(error);
    }
}

function substractMinGreaterThanMaxError(formGroup: FormGroup): void {
    formGroup.setErrors(null);
    Object.values(formGroup.controls).map(control => {
        const controlErrors = structuredClone(control.errors);
        if (Object.keys(controlErrors ?? {}).includes('minGreaterThanMax')) {
            delete controlErrors['minGreaterThanMax'];
            control.setErrors(Object.keys(controlErrors ?? {}).length ? controlErrors : null);
        }
        if (control.value) {
            (control as AbstractControl<number | PriceRestrictionModifier>)
                .setValue(control.value, { emitEvent: false, onlySelf: true });
            control.markAsTouched();
        }
    });
}

function minMaxRangeValidator(ref: ChangeDetectorRef, form: FormGroup): ValidatorFn {
    return (ctrl: AbstractControl): ValidationErrors | null => {
        if (ctrl?.value?.min?.modifier === undefined ||
            ctrl?.value?.max?.modifier === undefined) {
            return null;
        }

        const minGroup = ctrl.get('min') as FormGroup<{
            modifier: FormControl<PriceRestrictionModifier>;
            modifierValue: FormControl<number>;
        }>;
        const maxGroup = ctrl.get('max') as FormGroup<{
            modifier: FormControl<PriceRestrictionModifier>;
            modifierValue: FormControl<number>;
        }>;
        const groups = [minGroup, maxGroup];
        const minValue = calculateValue(ctrl.value.min.modifier, ctrl.value.min.modifierValue ?? 0);
        const maxValue = calculateValue(ctrl.value.max.modifier, ctrl.value.max.modifierValue ?? 0);
        if (minValue > maxValue) {
            const error = { minGreaterThanMax: true };
            groups.map(group => addMinGreaterThanMaxError(group));
            form.get('restrictions').setErrors({ ...form.get('restrictions').errors, ...error });
            return error;
        }
        groups.map(group => substractMinGreaterThanMaxError(group));
        const errors = structuredClone(form.get('restrictions').errors);
        if ((Object.keys(errors ?? {}).includes('minGreaterThanMax'))) {
            delete errors['minGreaterThanMax'];
            form.get('restrictions').setErrors(errors);
        }
        ref.markForCheck();
        return null;
    };
}

@Component({
    selector: 'ob-secondary-market-configuration-price',
    templateUrl: './secondary-market-configuration-price.component.html',
    styleUrls: ['./secondary-market-configuration-price.component.css'],
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule, LocalNumberPipe, NgIf, PercentageInputComponent, AsyncPipe,
        CurrencyInputComponent, ErrorMessage$Pipe, ErrorIconDirective
    ],
    providers: [LocalCurrencyPipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SecondaryMarketConfigPriceComponent implements OnInit, AfterViewInit {
    readonly #currencyPipe = inject(LocalCurrencyPipe);
    readonly #fb = inject(FormBuilder);
    readonly #destroy = inject(DestroyRef);
    readonly #ref = inject(ChangeDetectorRef);
    readonly $isSeasonTicket = input(false, { alias: 'isSeasonTicket' });
    readonly priceRestrictionModifier = PriceRestrictionModifier;
    readonly simulationCtrl = this.#fb.control<number>(30, {
        validators: [Validators.min(0)]
    });

    readonly minModifierForm = this.#fb.group({
        modifier: this.#fb.control(null as PriceRestrictionModifier, {
            validators: [Validators.required]
        }),
        modifierValue: this.#fb.control(null as number, {
            validators: [Validators.required, Validators.min(0.01), Validators.max(100)]
        })
    });

    readonly maxModifierForm = this.#fb.group({
        modifier: this.#fb.control(null as PriceRestrictionModifier, {
            validators: [Validators.required]
        }),
        modifierValue: this.#fb.control(null as number, {
            validators: [Validators.required, Validators.min(0.01)]
        })
    });

    readonly modifiersForm = this.#fb.group({
        min: this.minModifierForm,
        max: this.maxModifierForm
    });

    readonly $form = input.required<FormGroup>({ alias: 'form' });

    readonly showMaxModifierValue = signal(true);
    readonly showMinModifierValue = signal(true);

    ngOnInit(): void {
        this.modifiersForm.addValidators(minMaxRangeValidator(this.#ref, this.$form()));
        // toggle restrictions if type
        this.$form().get('type').valueChanges.pipe(
            takeUntilDestroyed(this.#destroy)
        ).subscribe(type => {
            if (type === 'PRICE_WITH_RESTRICTIONS') {
                this.$form().get('restrictions').enable({ emitEvent: false });
                this.modifiersForm.enable({ emitEvent: false });

            } else {
                this.$form().get('restrictions').disable({ emitEvent: false });
                this.modifiersForm.disable({ emitEvent: false });
            }
        });
        const maxFormElements = { modifier: this.maxModifierForm, show: this.showMaxModifierValue, controlName: 'max' };
        const minFormElements = { modifier: this.minModifierForm, show: this.showMinModifierValue, controlName: 'min' };
        [maxFormElements, minFormElements].map(formElements => formElements.modifier.valueChanges
            .pipe(takeUntilDestroyed(this.#destroy)).subscribe(value => {
                switch (value.modifier) {
                    case PriceRestrictionModifier.sameAs:
                        this.$form().get(`restrictions.${formElements.controlName}`).patchValue(100);
                        this.$form().get(`restrictions.${formElements.controlName}`).markAsDirty();
                        formElements.show.set(false);
                        if (formElements.modifier.controls.modifierValue.enabled) {
                            formElements.modifier.controls.modifierValue.disable();
                        }
                        break;
                    case PriceRestrictionModifier.moreThan:
                        this.$form().get(`restrictions.${formElements.controlName}`).patchValue(100 + (value.modifierValue ?? 0));
                        this.$form().get(`restrictions.${formElements.controlName}`).markAsDirty();
                        formElements.show.set(true);
                        if (formElements.modifier.controls.modifierValue.disabled) {
                            formElements.modifier.controls.modifierValue.enable();
                        }
                        break;
                    case PriceRestrictionModifier.lessThan:
                        this.$form().get(`restrictions.${formElements.controlName}`).patchValue(100 - (value.modifierValue ?? 0));
                        this.$form().get(`restrictions.${formElements.controlName}`).markAsDirty();
                        formElements.show.set(true);
                        if (formElements.modifier.controls.modifierValue.disabled) {
                            formElements.modifier.controls.modifierValue.enable();
                        }
                        break;
                }
            }));
    }

    ngAfterViewInit(): void {
        this.prepareModifierForms({ price: this.$form().getRawValue() as SecondaryMarketConfig['price'] });
    }

    prepareModifierForms(value: SecondaryMarketConfig): void {
        const maxFormElements = { modifier: this.maxModifierForm, show: this.showMaxModifierValue, value: value?.price?.restrictions?.max };
        const minFormElements = { modifier: this.minModifierForm, show: this.showMinModifierValue, value: value?.price?.restrictions?.min };
        [maxFormElements, minFormElements].map(formElements => {
            if (value?.price?.type === 'ORIGINAL_PRICE') {
                formElements.modifier.reset({ modifier: null, modifierValue: null }, { emitEvent: false });
                formElements.show.set(true);
                return;
            }
            if (value?.price?.type === 'PRICE_WITH_RESTRICTIONS') {
                this.$form().get('restrictions').enable({ emitEvent: false });
                this.modifiersForm.enable({ emitEvent: false });
            }
            if (formElements.value !== null && formElements.value !== undefined) {
                if (formElements.value > 100) {
                    formElements.modifier.reset({
                        modifier: PriceRestrictionModifier.moreThan,
                        modifierValue: formElements.value - 100
                    }, { emitEvent: false });
                    formElements.show.set(true);
                }
                if (formElements.value < 100) {
                    formElements.modifier.reset({
                        modifier: PriceRestrictionModifier.lessThan,
                        modifierValue: 100 - formElements.value
                    }, { emitEvent: false });
                    formElements.show.set(true);
                }
                if (formElements.value === 100) {
                    formElements.modifier.reset({
                        modifier: PriceRestrictionModifier.sameAs,
                        modifierValue: 100
                    }, { emitEvent: false });
                    formElements.show.set(false);
                }
            }
        });
        setTimeout(() => {
            this.$form().markAsPristine();
        });

    }

    touchModifierForms(): void {
        this.modifiersForm.markAsTouched();
        this.modifiersForm.patchValue(this.modifiersForm.value);
        Object.values(this.maxModifierForm.controls).map(ctrl => {
            ctrl.markAsTouched();
            ctrl.markAsDirty();
        });
        Object.values(this.minModifierForm.controls).map(ctrl => {
            ctrl.markAsTouched();
            ctrl.markAsDirty();
        });
    }

    calculatePriceRange(original: number, minModifier: MatSelect, minValue: number,
        maxModifier: MatSelect, maxValue: number): Record<string, string> {
        const min = (original ?? 0) * calculateValue(minModifier.value, minValue) / 100;
        const max = (original ?? 0) * calculateValue(maxModifier.value, maxValue) / 100;
        return {
            min: this.#currencyPipe.transform(parseFloat(min.toFixed(2))),
            max: this.#currencyPipe.transform(parseFloat(max.toFixed(2)))
        };
    }
}
