import {
    AbstractControl, UntypedFormArray, ValidationErrors, ValidatorFn
} from '@angular/forms';

export enum CsvErrorEnum {
    csvProcessorFileError = 'csvProcessorFileError',
    csvRequiredMapping = 'csvRequiredMapping',
    csvDuplicatedMapping = 'csvDuplicatedMapping'
}

export function csvValidator(csvError: CsvErrorEnum): ValidatorFn {
    return (ctrl: AbstractControl): ValidationErrors | null => {
        if (ctrl.hasError(csvError)) {
            return { [csvError]: true };
        } else {
            return null;
        }
    };
}

export function noDuplicateMappingInFormArray(formArray: UntypedFormArray): ValidatorFn {
    return (nameCtrl: AbstractControl): ValidationErrors | null => {
        const duplicate = !!formArray.controls.find(ctrl =>
            ctrl !== nameCtrl &&
            ctrl.value === nameCtrl.value &&
            Number.isInteger(ctrl.value) &&
            Number.isInteger(nameCtrl.value)
        );
        return duplicate ? { [CsvErrorEnum.csvDuplicatedMapping]: true } : null;
    };
}

