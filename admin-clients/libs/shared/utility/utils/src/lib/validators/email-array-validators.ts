import { UntypedFormControl, ValidatorFn, Validators } from '@angular/forms';

export function emailsValidator(separator = ','): ValidatorFn {
    const formControl = new UntypedFormControl('', Validators.email);
    return control => {
        if (control?.value) {
            const error = control.value
                .split(separator)
                .map(email => email.trim())
                .some(email => {
                    formControl.setValue(email);
                    return formControl.errors;
                });
            return error ? { toAddress: { value: control.value } } : null;
        }
        return null;
    };
}

export function maxEmailsExceedValidator(max = 10, separator = ','): ValidatorFn {
    return control => (control.value || '').split(separator).length > max ? { maxEmailsExceeded: true } : null;
}
