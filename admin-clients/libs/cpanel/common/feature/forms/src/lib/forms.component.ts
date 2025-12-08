import { FormsField, FormsRules } from '@admin-clients/cpanel/common/utils';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import {
    booleanAttribute, ChangeDetectionStrategy, ChangeDetectorRef, Component, computed, DestroyRef, inject, input
} from '@angular/core';
import { FormGroup, FormBuilder, ReactiveFormsModule, FormArray } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { FormsFieldRules } from './models/vm-forms-field-rules.model';
import { RulesEditorDialogComponent } from './rules-editor-dialog/rules-editor-dialog.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef,
    MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';

@Component({
    selector: 'app-forms',
    templateUrl: './forms.component.html',
    styleUrls: ['./forms.component.scss'],
    imports: [
        ReactiveFormsModule, TranslatePipe, MatIconModule, MatButtonModule, MatTable,
        MatHeaderCell, MatHeaderCellDef, MatCell, MatCellDef, MatCheckbox, MatColumnDef,
        MatMenuTrigger, MatMenu, MatMenuItem, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class FormsComponent {
    readonly #onDestroy = inject(DestroyRef);
    readonly #matDialog = inject(MatDialog);
    readonly #changeDet = inject(ChangeDetectorRef);
    readonly #fb = inject(FormBuilder);

    readonly $form = input.required<FormGroup>({alias: 'form'});
    readonly $showRules = input(true, {alias: 'showRules'});
    readonly $hideUneditable = input(false, {alias: 'hideUneditable', transform: booleanAttribute});
    readonly $formFields = input.required<FormsField[][]>({alias: 'formFields'});

    readonly $formsColumns = computed(() => [
        'visible', 'name', 'required',
        ...(this.$hideUneditable() ? [] : ['uneditable']),
        ...(this.$showRules() ? ['rules-badge', 'rules-editor'] : [])
    ]);

    readonly $formsArray = computed(() => {
        const forms = this.$formFields();
        if (forms) {
            const formsArray = forms.flat();
            const fields = forms.flat().map(field => this.#mapFormsFieldToFormRow(field));
            if (!this.$form().get('0')) {
                this.$form().addControl('0', this.#fb.array(fields));
            } else {
                this.$form().setControl('0', this.#fb.array(fields));
            }
            this.$form().markAsPristine();
            return formsArray;
        }
        return [];
    });

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

    #mapFormsFieldToFormRow(field: FormsField): FormGroup {
        const row = this.#fb.group({
            key: field.key,
            visible: field.visible,
            mandatory: { value: field.mandatory, disabled: !field.visible },
            uneditable: { value: field.uneditable, disabled: false },
            rules: { value: field.applied_rules, disabled: true }
        });

        row.get('visible').valueChanges.pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(value => {
                const requiredField = row.get('mandatory');
                if (!value) {
                    requiredField.setValue(false);
                    requiredField.disable();
                } else {
                    requiredField.enable();
                }
            });
        return row;
    }
}
