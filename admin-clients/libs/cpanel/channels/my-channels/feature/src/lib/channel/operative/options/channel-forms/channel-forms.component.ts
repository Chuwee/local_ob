import { ChannelForms, ChannelFormsField, ChannelFormsRules, ChannelFormsType } from '@admin-clients/cpanel/channels/data-access';
import { RulesEditorDialogComponent } from '@admin-clients/cpanel/common/feature/forms';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { booleanAttribute, ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormGroup, FormGroupDirective, UntypedFormArray, UntypedFormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ChannelFormsFieldRules } from '../models/vm-channel-forms-field-rules.model';

interface ColumnSettings {
    hideColumn: boolean;
    disabled: boolean;
}

const columnTypes = {
    visible: 'visible',
    mandatory: 'mandatory',
    uneditable: 'uneditable'
} as const;
type ColumnType = keyof typeof columnTypes;

export type ChannelFormsColumnsSettings = Record<ColumnType, ColumnSettings>;

@Component({
    selector: 'app-channel-forms',
    templateUrl: './channel-forms.component.html',
    styleUrls: ['./channel-forms.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelFormsComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #matDialog = inject(MatDialog);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #changeDetectorRef = inject(ChangeDetectorRef);
    readonly #form = inject(FormGroupDirective);

    form: UntypedFormGroup;
    channelFormsColumns: Partial<Record<ChannelFormsType, string[]>> = {};
    channelFormsArray$: Observable<{ key: string; value: ChannelFormsField[] }[]>;

    readonly $showRules = input(true, { alias: 'showRules', transform: booleanAttribute });

    readonly $channelForms = input.required<Observable<ChannelForms>>({ alias: 'channelForms$' });

    readonly $columnsSettings = input<ChannelFormsColumnsSettings | null>(null, { alias: 'columnsSettings' });

    ngOnInit(): void {
        this.form = this.#form.control;

        this.channelFormsArray$ = this.$channelForms()
            .pipe(map(channelForms => {
                const channelFormsArray = Object.keys(channelForms).map(formType => {
                    this.#mapChannelFormsColumns(formType);
                    const fields = channelForms[formType].map(field => this.#mapChannelFormsFieldToFormRow(field));
                    if (!this.form.get(formType)) {
                        this.form.addControl(formType, this.#fb.array(fields));
                    } else {
                        this.form.setControl(formType, this.#fb.array(fields));
                    }
                    return { key: formType, value: channelForms[formType] };
                });
                this.form.markAsPristine();
                return channelFormsArray;
            }));
    }

    openRulesEditorDialog(formType: string, buyerDataField?: ChannelFormsField): void {
        const fieldValues = this.form.get(formType) as UntypedFormArray;
        const control = fieldValues.controls.find(control => control.value.key === buyerDataField.key);

        const buyerDataFieldRules: ChannelFormsFieldRules = {
            key: buyerDataField.key,
            appliedRules: control.get('rules').value,
            availableRules: buyerDataField.available_rules
        };

        this.#matDialog.open<RulesEditorDialogComponent, ChannelFormsFieldRules, ChannelFormsRules[]>(
            RulesEditorDialogComponent, new ObMatDialogConfig(buyerDataFieldRules)
        )
            .beforeClosed()
            .subscribe(newAppliedRules => {
                if (newAppliedRules) {
                    control.get('rules').setValue(newAppliedRules.concat());
                    control.get('rules').markAsTouched();
                    control.get('rules').markAsDirty();
                    this.#changeDetectorRef.markForCheck();
                }
            });
    }

    #mapChannelFormsColumns(formType: string): void {
        this.channelFormsColumns[formType] = ['name'];

        if (!this.#getColumnSettings(columnTypes.mandatory).hideColumn) this.channelFormsColumns[formType].push('required');
        if (!this.#getColumnSettings(columnTypes.visible).hideColumn) this.channelFormsColumns[formType].unshift('visible');
        if (!this.#getColumnSettings(columnTypes.uneditable).hideColumn && formType === 'member') { this.channelFormsColumns[formType].push('uneditable'); }

        if (this.$showRules()) {
            this.channelFormsColumns[formType].push('rules-badge');
            this.channelFormsColumns[formType].push('rules-editor');
        }
    }

    #mapChannelFormsFieldToFormRow(field: ChannelFormsField): UntypedFormGroup {
        const rowSchema: { [key: string]: { value: boolean; disabled: boolean } } = {
            visible: {
                value: field.visible,
                disabled: !field.mutable || this.#getColumnSettings(columnTypes.visible).disabled
            },
            mandatory: {
                value: field.mandatory,
                disabled: !field.visible || !field.mutable || this.#getColumnSettings(columnTypes.mandatory).disabled
            },
            uneditable: {
                value: field.uneditable,
                disabled: this.#getColumnSettings(columnTypes.uneditable).disabled
            }
        };

        const row = this.#fb.group({
            ...rowSchema,
            key: field.key,
            rules: { value: field.applied_rules, disabled: true }
        });

        row.get('visible').valueChanges.pipe(takeUntilDestroyed(this.#destroyRef))
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

    #getColumnSettings(column: ColumnType): ColumnSettings {
        return this.$columnsSettings()?.[column] ?? { hideColumn: false, disabled: false };
    }
}
