import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function includedInArrayValidator(
    array: unknown[]
): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => array.includes(control.value) ? null : { notIncluded: true } ;
}
