import { DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AbstractControl, FormControl, UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { Observable, tap } from 'rxjs';

export class FormControlHandler {

    static checkAndRefreshDirtyState(control: AbstractControl, originalValue: any, deepNestedComparision = false): void {
        if (control) {
            if (Array.isArray(originalValue)) {
                if (control.dirty) {
                    if (control.value && (control.value as []).sort().join() === originalValue.sort().join() ||
                        (!control.value && !originalValue)) {
                        control.markAsPristine();
                    }
                }
            } else if (!deepNestedComparision && control.dirty &&
                (control.value === originalValue ||
                    (!control.value && !originalValue))) {
                control.markAsPristine();
            } else if (control.dirty &&
                JSON.stringify(control.value) === JSON.stringify(originalValue)) {
                control.markAsPristine();
            }
        }
    }

    static markAllControlsAsTouched(control: AbstractControl): void {
        if (control instanceof UntypedFormGroup || control instanceof UntypedFormArray) {
            Object.keys(control.controls)
                .map(field => control.get(field))
                .forEach(subControl => this.markAllControlsAsTouched(subControl));
        } else {
            control.markAsTouched();
        }
    }

    static getInvalidForms(control: AbstractControl): UntypedFormGroup[] {
        let errorFormGroup: UntypedFormGroup[] = [];
        if (control instanceof UntypedFormGroup) {
            if (!control.valid) {
                errorFormGroup.push(control);
            }
            Object.keys(control.controls)
                .map(field => control.get(field))
                .forEach(subControl => errorFormGroup = errorFormGroup.concat(this.getInvalidForms(subControl)));
        }
        return errorFormGroup;
    }

    static getDirtyValues(form: UntypedFormGroup | UntypedFormArray): Record<string, any> {
        const dirtyValues: Record<string, any> = {};

        for (const key in form.controls) {
            const currentControl = form.controls[key];
            if (currentControl?.dirty) {
                if (currentControl?.controls) {
                    dirtyValues[key] = this.getDirtyValues(currentControl);
                } else {
                    dirtyValues[key] = currentControl.value;
                }
            }

        }

        return dirtyValues;
    }

    static reflectControlValue(
        sourceControl: FormControl<string>, targetControl: FormControl<string>, maxLength?: number
    ): Observable<string> {
        return sourceControl.valueChanges
            .pipe(
                tap(sourceValue => {
                    if (!targetControl.touched || !targetControl.value) {
                        const newValue = maxLength ? sourceValue.substring(0, maxLength) : sourceValue;
                        targetControl.setValue(newValue);
                        targetControl.markAsUntouched();
                    }
                })
            );
    }

    /**
     *
     * @param formControl the control that returns the value changes observable source
     * @param [destroyRef] Optional, must be set if it's used outside a constructor
     */
    static getValueChanges<T>(formControl: FormControl<T>, destroyRef?: DestroyRef): Observable<T> {
        return formControl.valueChanges.pipe(takeUntilDestroyed(destroyRef));
    }
}
