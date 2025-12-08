// ADD THIS MODULE IN YOUR PROJECT, AND LOAD IT IN THE MAIN CLASS
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { CurrencyInputComponent } from './currency-input/currency-input.component';
import { DateTimePickerComponent } from './date-time/date-time-picker/date-time-picker.component';
import { TimePickerComponent } from './date-time/time-picker/time-picker.component';
import { PercentageInputComponent } from './percentage-input/percentage-input.component';
import { RichTextAreaComponent } from './rich-text-area/rich-text-area.component';
import { SelectServerSearchComponent } from './select-server-search/select-server-search.component';

/**
 * Fix for the MatInput required asterisk.
 */
const requiredImplementation: PropertyDescriptor & ThisType<any> = {
    get(): boolean {
        if (this._required) {
            return this._required;
        }

        // The required attribute is set
        // when the control return an error from validation with an empty value
        if (this.ngControl?.control?.validator) {
            const emptyValueControl = Object.assign({}, this.ngControl.control);
            (emptyValueControl).value = null;
            return 'required' in (this.ngControl.control.validator(emptyValueControl) || {});
        }
        return false;
    },
    set(value: boolean): void {
        this._required = coerceBooleanProperty(value);
    }
};

export function patchRequiredInputs(): void {
    Object.defineProperty(MatInput.prototype, 'required', requiredImplementation);
    Object.defineProperty(MatSelect.prototype, 'required', requiredImplementation);
    Object.defineProperty(CurrencyInputComponent.prototype, 'required', requiredImplementation);
    Object.defineProperty(PercentageInputComponent.prototype, 'required', requiredImplementation);
    Object.defineProperty(RichTextAreaComponent.prototype, 'required', requiredImplementation);
    Object.defineProperty(SelectServerSearchComponent.prototype, 'required', requiredImplementation);
    Object.defineProperty(DateTimePickerComponent.prototype, 'required', requiredImplementation);
    Object.defineProperty(TimePickerComponent.prototype, 'required', requiredImplementation);
}
