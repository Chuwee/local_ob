import { ChangeDetectionStrategy, Component, inject, input, OnInit, ChangeDetectorRef, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    AbstractControl,
    ControlValueAccessor, FormBuilder, FormGroup, NgControl, ReactiveFormsModule, ValidatorFn, Validators
} from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

export interface TrackingCredentialField {
    name: string;
    label: string;
    placeholder: string;
    type?: 'text' | 'password' | 'email' | 'number' | 'url';
    required?: boolean;
    validators?: ValidatorFn[];
}

export interface TrackingCredentialConfig {
    fields: TrackingCredentialField[];
}

export interface TrackingCredential {
    [key: string]: string;
}

@Component({
    selector: 'app-tracking-credentials',
    templateUrl: './tracking-credentials.component.html',
    styleUrl: `./tracking-credentials.component.css`,
    imports: [
        ReactiveFormsModule, TranslatePipe,
        MatFormFieldModule, MatInput, MatIcon,
        MatIconButton, MatDivider, MatTooltip
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrackingCredentialsComponent implements ControlValueAccessor, OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #cdr = inject(ChangeDetectorRef);
    readonly #destroyRef = inject(DestroyRef);
    readonly #ngControl = inject(NgControl, { optional: true, self: true });

    readonly max = input<number>(5);
    readonly config = input.required<TrackingCredentialConfig>();
    readonly credentialsForm = this.#fb.array<FormGroup<{ [key: string]: AbstractControl<string> }>>([]);

    constructor() {
        if (this.#ngControl) {
            this.#ngControl.valueAccessor = this;
        }
    }

    ngOnInit(): void {
        if (this.credentialsForm.length === 0) {
            this.credentialsForm.push(this.createCredentialGroup());
        }
        // Subscribe to value changes to emit updates
        this.credentialsForm.valueChanges.pipe(
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(value => {
            if (this.credentialsForm.dirty) {
                this.onChange(value);
            }
        });
    }

    writeValue(value: TrackingCredential[]): void {
        this.credentialsForm.clear({ emitEvent: false });
        if (value && Array.isArray(value) && value.length > 0) {
            value.forEach(credential => {
                this.credentialsForm.push(this.createCredentialGroup(credential));
            });
        } else {
            this.credentialsForm.push(this.createCredentialGroup());
        }
        this.credentialsForm.updateValueAndValidity({ emitEvent: false });
        this.#cdr.detectChanges();
    }

    registerOnChange(fn: (value: TrackingCredential[]) => void): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        if (isDisabled) {
            this.credentialsForm.disable();
        } else {
            this.credentialsForm.enable();
        }
    }

    /**
     * Deletes a credential at the specified index.
     * Cannot delete the last credential to maintain minimum of 1.
     */
    delete(index: number): void {
        if (this.credentialsForm.disabled) return;
        if (this.credentialsForm.length <= 1) return;
        this.credentialsForm.removeAt(index, { emitEvent: false });
        this.onTouched();
        this.onChange(this.credentialsForm.getRawValue());
    }

    /**
     * Adds a new empty credential.
     * Respects the maximum limit set by the max input.
     */
    add(): void {
        if (this.credentialsForm.disabled) return;
        if (this.credentialsForm.length >= this.max()) return;
        this.credentialsForm.push(this.createCredentialGroup(), { emitEvent: false });
        this.onTouched();
        this.onChange(this.credentialsForm.getRawValue());
    }

    /**
     * Creates a form group for a single credential based on the field configuration.
     * Applies required and custom validators as specified in the field config.
     */
    private createCredentialGroup(value?: TrackingCredential): FormGroup {
        const config = this.config();
        const groupControls: { [key: string]: AbstractControl } = {};

        config.fields.forEach(field => {
            const validators: ValidatorFn[] = [];

            // Add required validator if field is required
            if (field.required) {
                validators.push(Validators.required);
            }

            // Add custom validators if provided
            if (field.validators && field.validators.length > 0) {
                validators.push(...field.validators);
            }

            validators.push(
                (control: AbstractControl) => {
                    if (control.errors && control.touched && this.#ngControl?.control) {
                        this.#ngControl.control.setErrors(control.errors);
                    }
                    return null;
                }
            );

            groupControls[field.name] = this.#fb.control({
                value: value?.[field.name] || '',
                disabled: this.credentialsForm.disabled
            }, validators);
        });

        const group = this.#fb.group(groupControls);

        return group;
    }

    private onChange: (value: TrackingCredential[]) => void = () => { };
    private onTouched: () => void = () => { };

}
