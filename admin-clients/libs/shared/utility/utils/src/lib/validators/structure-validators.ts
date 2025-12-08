import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

/**
 * There's at least one element inside the array contained in the formControl
 * (suitable for FormControl's linked to <mat-selection-list> elements)
 */
export function atLeastOneRequiredInArray() {
    return <T extends []>(ctrl: AbstractControl<T>): ValidationErrors | null => {
        const hasError = !(ctrl.value?.length > 0);
        return hasError ? { atLeastOneRequired: true } : null;
    };
}

/**
 * There's at least one FormControl with a value inside the FormGroup
 */
export function atLeastOneRequiredInFormGroup(errorKey = 'atLeastOneRequired') {
    return <T>(group: FormGroup<{ [x: string]: AbstractControl<T> }>): ValidationErrors | null => {
        const hasError = !Object.values(group.controls).some(control => !!control.value);
        return hasError ? { [errorKey]: true } : null;
    };
}

/**
 * If one FormControl inside the FormGroup has value, then all FormControls in the FormGroup are required
 */
export function allRequiredIfOneHasValueInFormGroup() {
    return <T>(group: FormGroup<{ [x: string]: AbstractControl<T> }>): ValidationErrors | null => {
        const controls = group.controls;
        const controlHasValue = Object.values(controls).some(control => control.value);
        const everyControlHasValue = Object.values(controls).every(control => control.value);
        const everyRequired = Object.values(controls).every(control => control.hasValidator(Validators.required));
        if (!everyRequired) {
            if (controlHasValue && !everyControlHasValue) {
                Object.values(controls).forEach(control => {
                    if (!control.value) {
                        control.setErrors({
                            ...control.errors,
                            required: true
                        });
                    }
                });
                return { required: true };
            } else {
                Object.values(controls).forEach(control => {
                    if (control.hasError('required')) {
                        control.updateValueAndValidity();
                    }
                });
                return null;
            }
        } else {
            return null;
        }
    };
}

/**
 * Multiple Fields in one single FormControl with required values
 */
export function requiredFieldsInOneControl(requiredFields: string[]): ValidatorFn {
    return <T extends []>(ctrl: AbstractControl<T>): ValidationErrors | null => {
        if (ctrl.value == null || ctrl.value.length === 0) {
            return { requiredFields: true };
        }
        return requiredFields.some(field => ctrl.value[field] == null || ctrl.value.length === 0) ? { requiredFields: true } : null;
    };
}

/**
 * Some user chosen field that must have at least one true value in array of values
 */
export function atLeastOneOfSelectedField(fieldName: string): ValidatorFn {
    return <T extends []>(control: AbstractControl<T>): ValidationErrors | null => {
        if (!control?.value?.some(field => field[fieldName])) {
            return { noValueForSelectedField: true };
        }
        return null;
    };
}

/**
 * maximum of selected items that an array can have in a control
 */
export function maxSelectedItems(maxSelectedItems: number): ValidatorFn {
    return <T extends []>(control: AbstractControl<T>): ValidationErrors | null => {
        if (control.value?.length > maxSelectedItems) {
            return { maxSelectedItems };
        }
        return null;
    };
}

/**
 * There's at least one control with value true inside the FormRecord
 */
export function atLeastOneRequiredTrueInFormRecord(): ValidatorFn {
    return (control: AbstractControl<boolean>): ValidationErrors | null => {
        if (!Object.keys(control.value) || Object.keys(control.value).some(key => control.value[key])) {
            return null;
        }
        return { required: true };
    };
}
