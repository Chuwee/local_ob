import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Unique validator, it checks that the value doesn't exist in an array of values
 */
export function unique<T>(
    values: T[],
    compareBy?: (valuesElement: T, currentValue: unknown) => boolean,
    error = 'notUnique'
): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        if (compareBy) {
            return values.find(elem => compareBy(elem, control.value)) ? { [error]: true } : null;
        } else {
            return values.includes(control.value) ? { [error]: true } : null;
        }
    };
}

export function dynamicUnique<T>(
    getValues: () => T[],
    compareBy?: (valuesElement: T, currentValue: unknown) => boolean,
    error = 'notUnique'
): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        if (compareBy) {
            return getValues().find(elem => compareBy(elem, control.value)) ? { [error]: true } : null;
        } else {
            return getValues().includes(control.value) ? { [error]: true } : null;
        }
    };
}
