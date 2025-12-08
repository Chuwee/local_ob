import { RulesEditorDialogComponent, FormsFieldRules } from '@admin-clients/cpanel/common/feature/forms';
import { FormsField, FormsRules } from '@admin-clients/cpanel/common/utils';
import { CustomerForms, CustomerFormsField, VisibleKey } from '@admin-clients/cpanel/organizations/entities/data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, computed, DestroyRef, inject, input } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-entity-customer-forms',
    templateUrl: './entity-customer-forms.component.html',
    styleUrl: './entity-customer-forms.component.scss',
    imports: [ReactiveFormsModule, TranslatePipe, MatIconModule, MatButtonModule, MaterialModule, MatTooltipModule],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityCustomerFormsComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #matDialog = inject(MatDialog);
    readonly #fb = inject(FormBuilder);
    readonly #changeDet = inject(ChangeDetectorRef);

    readonly $form = input.required<FormGroup>({ alias: 'form' });
    readonly $customerForms = input.required<CustomerForms>({ alias: 'customerForms' });

    readonly #requiredColumns = ['name', 'surname', 'email'];
    readonly #identificationKeys = ['identification', 'identification_with_type'];

    readonly tableColumns = [
        'external_field', 'name', 'visible-sign-in', 'visible-profile', 'required', 'uneditable', 'unique', 'rules-badge', 'rules-editor'
    ];

    readonly $customerFormsArray = computed(() => {
        const mergedFormFields = [
            ...this.#assignVisibleField(this.$customerForms().profile, 'visibleProfile'),
            ...this.#assignVisibleField(this.$customerForms().signIn, 'visibleSignIn')
        ];
        const formsArray = [this.#mergeFormFields(mergedFormFields)].flat();
        const orderedFormsArray = this.#sortByRequiredKeys(formsArray, this.#requiredColumns);
        const formFields = orderedFormsArray.map(field => this.#mapFormsFieldToFormRow(field));

        if (!this.$form().get('0')) {
            this.$form().addControl('0', this.#fb.array(formFields));
        } else {
            this.$form().setControl('0', this.#fb.array(formFields));
        }
        this.$form().markAsPristine();

        return orderedFormsArray;
    });

    getCustomerFormsValue(payload: CustomerForms): CustomerForms {
        const valuesForm: CustomerFormsField[] = this.$form().getRawValue()[0];
        const signIn = this.#createPayload(valuesForm, payload.signIn, 'visibleSignIn');
        const profile = this.#createPayload(valuesForm, payload.profile, 'visibleProfile');

        return { signIn, profile };
    }

    openRulesEditorDialog(fieldKey: string, formField?: FormsField): void {
        const fieldValues = this.$form().get(fieldKey) as FormArray;
        const control = fieldValues.controls.find(control => control.value.key === formField.key);
        const fieldRules: FormsFieldRules = {
            key: formField.key,
            appliedRules: control.get('rules').value,
            availableRules: formField.available_rules
        };

        this.#matDialog.open<RulesEditorDialogComponent, FormsFieldRules, FormsRules[]>(
            RulesEditorDialogComponent, new ObMatDialogConfig(fieldRules)
        )
            .beforeClosed()
            .subscribe(newAppliedRules => {
                if (newAppliedRules) {
                    control.get('rules').setValue(newAppliedRules.concat());
                    control.get('rules').markAsTouched();
                    control.get('rules').markAsDirty();
                    this.#changeDet.markForCheck();
                }
            });
    }

    isCheckboxDisabled(key: string, index: number): boolean {
        const controlForm = (this.$form().get('0') as FormArray).controls[index].get(key);
        return controlForm.disabled && !controlForm.value;
    }

    #sortByRequiredKeys(array: CustomerFormsField[], requiredKeys: string[]): CustomerFormsField[] {
        const requiredItems = array.filter(item => requiredKeys.includes(item.key));
        const sortedRequiredItems = requiredItems.sort((a, b) => requiredKeys.indexOf(a.key) - requiredKeys.indexOf(b.key));
        const otherItems = array.filter(item => !this.#requiredColumns.includes(item.key));

        return [...sortedRequiredItems, ...otherItems];
    }

    #mapFormsFieldToFormRow(field: CustomerFormsField): FormGroup {
        const isKeyRequired = !!this.#requiredColumns.find(key => key === field.key);
        const row = this.#fb.group({
            key: field.key,
            visible: field.visible,
            visibleSignIn: { value: isKeyRequired ? true : field.visibleSignIn, disabled: isKeyRequired },
            visibleProfile: { value: isKeyRequired ? true : field.visibleProfile, disabled: isKeyRequired },
            mandatory: { value: isKeyRequired ? true : field.mandatory, disabled: isKeyRequired },
            uneditable: { value: field.uneditable, disabled: !field.visibleProfile },
            unique: { value: field.unique, disabled: !this.#identificationKeys.includes(field.key) },
            rules: { value: field.applied_rules, disabled: true }
        });

        row.get('visibleProfile').valueChanges.pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => {
                const field = row.get('uneditable');
                if (!value) {
                    field.setValue(false);
                    field.disable();
                } else {
                    field.enable();
                }
            });

        row.get('visibleSignIn').valueChanges.pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => {
                if (value) {
                    row.get('visibleProfile').setValue(true);
                }
            });

        return row;
    }

    #assignVisibleField(formsFields: FormsField[][], key: VisibleKey): CustomerFormsField[] {
        return formsFields.flat().map(formField => ({ ...formField, [key]: formField.visible }));
    }

    #mergeFormFields(formFields: CustomerFormsField[]): CustomerFormsField[] {
        const booleanFields = ['visibleSignIn', 'visibleProfile', 'uneditable', 'unique', 'mandatory'];
        return formFields.reduce((state, formField) => {
            const formFieldIndex = state.findIndex(f => f.key === formField.key);
            if (formFieldIndex !== -1) {
                booleanFields.forEach(field => state[formFieldIndex][field] = !!formField[field] || !!state[formFieldIndex][field]);
            } else {
                state.push(formField);
            }
            return state;
        }, []);
    }

    #createPayload(valuesForm: CustomerFormsField[], formsFields: FormsField[][], visibleKey: VisibleKey): FormsField[][] {
        const payloadForm: FormsField[][] = formsFields.map(formFields => {
            const payloadForm = formFields.map(formField => {
                const valueFormField = valuesForm.find(field => field.key === formField.key);
                return this.#createFormField(valueFormField, visibleKey);
            });
            return payloadForm;
        });

        valuesForm.forEach(valueFormField => {
            const existFormField = formsFields.flat().find(formField => formField.key === valueFormField.key);
            if (!existFormField && valueFormField[visibleKey]) {
                payloadForm.push([this.#createFormField(valueFormField, visibleKey)]);
            }
        });

        return payloadForm;
    }

    #createFormField(valueFormField: CustomerFormsField, visibleKey: VisibleKey): CustomerFormsField {
        return {
            key: valueFormField.key,
            visible: valueFormField[visibleKey],
            mandatory: valueFormField[visibleKey] && valueFormField.mandatory,
            uneditable: valueFormField.uneditable,
            unique: valueFormField.unique,
            rules: valueFormField.rules ?? []
        };
    }
}
