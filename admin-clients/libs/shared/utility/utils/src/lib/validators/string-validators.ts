import { AbstractControl, AsyncValidatorFn, UntypedFormArray, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Observable, take } from 'rxjs';
import { map } from 'rxjs/operators';

// checks empty string and string with spaces only
export function notEmpty(error = 'empty'): ValidatorFn {
    return control => {
        if (control.value && control.value.trim() === '') {
            return { error };
        }
        return null;
    };
}

export function noDuplicateValuesValidatorForm(ratesList: UntypedFormArray): ValidatorFn {
    return control => ratesList.controls.filter(c => c !== control).map(c => c.value)
        .includes(control.value) ? { duplicateValue: { value: control.value } } : null;
}

export function noDuplicateValuesValidatorStatic(values: string[]): ValidatorFn {
    return control => noDuplicateValuesValidatorFn(control, values);
}

export function noDuplicateValuesValidatorFn(control: AbstractControl, values: string[]): ValidationErrors | null {
    return values.includes(control.value) ? { duplicateValue: control.value } : null;
}

export function noDuplicateValuesAsyncValidator(values$: Observable<string[]>): AsyncValidatorFn {
    return control => values$.pipe(
        take(1),
        map(values => values.includes(control.value) ? { duplicateValue: control.value } : null)
    );
}

export function requiredLength(requiredLength: number): ValidatorFn {
    return (control: AbstractControl<string>): ValidationErrors | null => {
        if (!control || control.value?.length === requiredLength) return null;
        return { requiredLength: { requiredLength } };
    };
}

// works like max length, but the error that shows indicates that style tags and special chars also count
export function htmlMaxLengthValidator(maxLength: number): ValidatorFn {
    return control => control?.value?.length > maxLength ? { richTextAreaMaxLength: { requiredLength: maxLength } } : null;
}

// for html texts with style tags and special chars, it only counts content chars, no tags, no special chars
export function htmlContentMaxLengthValidator(maxLength: number): ValidatorFn {
    const span = document.createElement('span');
    return control => {
        if (control?.value) {
            span.innerHTML = control.value;
            if (span.textContent.length > maxLength) {
                return { maxlength: { requiredLength: maxLength } };
            }
        }
        return null;
    };
}

// to prevent the user from entering characters that are not URL friendly
export function urlFriendlyValidator(control: AbstractControl): ValidationErrors | null {
    return encodeURIComponent(control.value) === control.value ? null : { urlFriendly: true };
}

export function noWhitespaceValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        if (control.value?.includes(' ')) {
            return { noBlankSpaces: true };
        }
        return null;
    };
}

export function ibanValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        const iban = control.value;

        if (!iban || iban.trim() === '') {
            return null;
        }

        if (iban.includes(' ')) {
            return { invalidFormat: true };
        }

        const cleanedIban = iban.toUpperCase();
        if (cleanedIban.length < 15 || cleanedIban.length > 34 || !cleanedIban.match(/^[A-Z]{2}[A-Z0-9]+$/)) {
            return { invalidFormat: true };
        }
        return null;
    };
}