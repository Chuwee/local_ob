import { AbstractControl, UntypedFormControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function minMaxRangeValidator(minCtrl: UntypedFormControl, maxCtrl: UntypedFormControl): ValidatorFn {
    return (_: AbstractControl): ValidationErrors | null => {
        const minValue = minCtrl.value;
        const maxValue = maxCtrl.value;
        if (minValue && maxValue && minValue >= maxValue) {
            return { minGreaterThanMax: true };
        }
        return null;
    };
}

export function atLeastOneRequired(nameCtrls: UntypedFormControl[]): ValidatorFn {
    return (_: AbstractControl): ValidationErrors | null => {
        const isValid = nameCtrls.some(nameCtrl => nameCtrl.value !== null);

        nameCtrls.forEach(ctrl => {
            const errors = { ...ctrl.errors };

            if (isValid) {
                delete errors['atLeastOneRequired'];
            } else {
                errors['atLeastOneRequired'] = true;
            }

            const hasErrors = Object.keys(errors).length > 0;
            ctrl.setErrors(hasErrors ? errors : null);
        });

        return isValid ? null : { atLeastOneRequired: true };
    };

}
