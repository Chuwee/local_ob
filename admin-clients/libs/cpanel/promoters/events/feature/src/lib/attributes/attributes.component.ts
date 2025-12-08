import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AttributeSelectionType, AttributeType, AttributeWithValues, PutAttribute } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { BooleanInput } from '@angular/cdk/coercion';
import { AsyncPipe, NgFor, NgIf, NgSwitch, NgSwitchCase } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { Observable, Subject } from 'rxjs';
import { filter, takeUntil, tap } from 'rxjs/operators';

type FormValue = { [id: string]: string | number | number[] | { [optionId: string]: boolean } };

@Component({
    selector: 'app-attributes',
    templateUrl: './attributes.component.html',
    styleUrls: ['./attributes.component.scss'],
    providers: [LocalCurrencyPipe],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, NgIf, NgSwitch, NgSwitchCase, NgFor, AsyncPipe,
        FlexLayoutModule, ReactiveFormsModule, FormControlErrorsComponent
    ]
})
export class AttributesComponent implements OnInit, OnDestroy {
    private _onDestroy: Subject<void>;
    private _attributes: AttributeWithValues[];
    @ViewChildren(MatExpansionPanel) private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    attributeList$: Observable<AttributeWithValues[]>;
    attributeType = AttributeType;
    attributeSelectionType = AttributeSelectionType;
    formAttributes: UntypedFormGroup;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;
    @Input() attributes$: Observable<AttributeWithValues[]>;
    @Input() set disabled(disabled: BooleanInput) {
        if (disabled) {
            this.formAttributes.disable();
        } else {
            this.formAttributes.enable();
        }
    }

    constructor(
        private _fb: UntypedFormBuilder
    ) { }

    ngOnInit(): void {
        this._onDestroy = new Subject<void>();
        this.formAttributes = this._fb.group({});
        this.form.setControl('attributes', this.formAttributes);

        this.attributeList$ = this.attributes$.pipe(
            filter(attributes => !!attributes),
            tap(attributes => {
                this.formAttributes = this._fb.group({});
                this.form.setControl('attributes', this.formAttributes);
                attributes.forEach(attr => this.addFormControl(this.formAttributes, attr));
                this._attributes = attributes;
                if (this.disabled) {
                    this.formAttributes.disable();
                }
            }),
            takeUntil(this._onDestroy)
        );
    }

    ngOnDestroy(): void {
        this.form?.removeControl('attributes');
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    data(): PutAttribute[] {
        if (this.formAttributes.valid) {
            return this.parseFormData(this.formAttributes.value, this._attributes);
        } else {
            this.formAttributes.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return null;
        }
    }

    private parseFormData(formData: FormValue, attributes: AttributeWithValues[]): PutAttribute[] {
        const data: PutAttribute[] = Object.entries(formData).map(([id, value]) => {
            const attribute = attributes.find(attr => attr.id === Number(id));
            const result: PutAttribute = { id: attribute.id };

            if (attribute.type === AttributeType.defined &&
                attribute.selection_type === AttributeSelectionType.single) {
                result.selected = value && [Number(value)];
            } else if (attribute.type === AttributeType.defined &&
                attribute.selection_type === AttributeSelectionType.multiple) {
                result.selected = value && typeof value === 'object' && Object.entries(value)
                    .filter(([, option]) => !!option) // gets all options checked
                    .map(([optionId]) => Number(optionId)); // returns an array of option ids
            } else {
                result.value = (typeof value === 'number' || typeof value === 'string') && value;
            }

            return result;
        });
        return data;
    }

    private addFormControl(
        form: UntypedFormGroup, { id, value, selected, type, selection_type: selectionType, ...props }: AttributeWithValues
    ): void {
        switch (type) {
            case AttributeType.numeric:
                form.addControl(`${id}`, this._fb.control(value, [Validators.max(props.max)]));
                break;

            case AttributeType.alphanumeric:
                form.addControl(`${id}`, this._fb.control(value, [Validators.maxLength(props.max)]));
                break;

            case (selectionType === AttributeSelectionType.single) && AttributeType.defined:
                form.addControl(`${id}`, this._fb.control(selected?.[0]));
                break;

            case (selectionType === AttributeSelectionType.multiple) && AttributeType.defined:
                form.addControl(`${id}`, this._fb.group(props.texts.values.reduce((group, option) =>
                    (group[option.id] = selected?.includes(option.id), group), {})));
                break;
        }
    }

}
