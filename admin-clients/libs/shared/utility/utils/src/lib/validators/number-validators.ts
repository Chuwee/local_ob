import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Non zero value validator
 */
export function nonZeroValidator(control: AbstractControl): ValidationErrors | null {
    return control.value === 0 ? { nonZeroRequired: true } : null;
}

/**
 * Greater than number validator
 */
export function greaterThanValidator(min: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => control.value <= min ? { moreThanMin: min } : null;
}

/**
 * Diff than number validator
 */
export function diffThanValidator(numberValue: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => control.value === numberValue ?
        { diffThanNumber: numberValue } :
        null;
}

/**
 * Range validator (min < max)
 */
export function rangeValidator(minValueKey: string, maxValueKey: string, equal: boolean = true ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        const start = control.get(minValueKey).value;
        const end = control.get(maxValueKey).value;
        if (start !== null && end !== null) {
            const isValid = equal ? start <= end : start < end;
            return isValid ? null : { range: true };
        }
        return null;
    };
}

/**
 * Max number of decimals validator
 */
export function maxDecimalLength(maxLength: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        const value = control.value;
        if (value === null || value === undefined || value === '') {
            return null;
        }

        const decimalCount = countDecimals(Number(value));
        if (decimalCount > maxLength) {
            return maxLength === 0 ? { noDecimalsAllowed: true } : { maxDecimalLength: { maxLength } };
        }
        return null;
    };
}

function countDecimals(value: number): number {
    if (Number.isInteger(value)) {
        return 0;
    }

    let decimals = 0;
    while (!Number.isInteger(value)) {
        value *= 10;
        decimals++;
    }
    return decimals;
}
