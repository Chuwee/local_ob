import { FormControlErrorsBase } from '@OneboxTM/feature-form-control-errors';

export class FormErrors extends FormControlErrorsBase {
    formControlErrors = FormControlErrors;
    errorsParameterMap = errorsParameterMap;
    prefix = 'FORMS.ERRORS.';
}

export enum FormControlErrors {
    default = 'INVALID_FIELD',
    required = 'REQUIRED_FIELD',
    atLeastOneRequired = 'AT_LEAST_ONE',
    max = 'HIGHER_THAN',
    min = 'LOWER_THAN',
    maxlength = 'MAX_LENGTH_EXCEEDED',
    minlength = 'MIN_LENGTH',
    maxLengthWithoutHTML = 'MAX_LENGTH_EXCEEDED',
    richTextAreaMaxLength = 'MAX_LENGTH_EXCEEDED_WITH_FORMAT',
    email = 'INVALID_CHARACTERS',
    pattern = 'INVALID_CHARACTERS',
    notUniqueName = 'NAME_ALREADY_IN_USE',
    notSamePassword = 'NOT_SAME_PASSWORD',
    maxDecimalLength = 'MAX_DECIMAL_LENGTH',
    diffThanNumber = 'DIFF_THAN_NUMBER',
    noBlankSpaces = 'NO_BLANK_SPACES',
    duplicateValue = 'REPEATED_ELEMENT',
    // DATETIME
    startDateAfterEndDate = 'START_DATE_AFTER_THAN_END_DATE',
    endDateBeforeStartDate = 'DATE_BEFORE_THAN_FEMALE',
    startAfterEnd = 'DATE_AFTER_THAN_MALE',
    endAfterStart = 'DATE_AFTER_THAN_MALE',
    dateIsFuture = 'DATE_IS_FUTURE'
}

export const errorsParameterMap = {
    maxlength: { requiredLength: 'value' },
    minlength: { requiredLength: 'value' },
    max: { max: 'value' },
    min: { min: 'value' },
    maxLengthWithoutHTML: { requiredLength: 'value' },
    maxDecimalLength: { maxLength: 'value' },
    richTextAreaMaxLength: { requiredLength: 'value' },
    diffThanNumber: 'value',
    startDateAfterEndDate: 'date',
    endDateBeforeStartDate: 'date',
    startAfterEnd: 'date',
    endAfterStart: 'date'
};
