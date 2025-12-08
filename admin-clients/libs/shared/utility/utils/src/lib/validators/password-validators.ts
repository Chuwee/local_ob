import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function passwordValidator(expr: RegExp, errorCode: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        if (control.value && !expr.test(control.value)) {
            return { [errorCode]: true };
        }
        return null;
    };
}

export function checkPasswords(passwordKey: string, confirmPasswordKey: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        const pass = control.get(passwordKey).value;
        const confirmPass = control.get(confirmPasswordKey).value;
        let error = null;
        if (pass !== confirmPass) {
            error = { notSamePassword: true };
        }
        control.get(confirmPasswordKey).setErrors(error);
        return error;
    };
}
