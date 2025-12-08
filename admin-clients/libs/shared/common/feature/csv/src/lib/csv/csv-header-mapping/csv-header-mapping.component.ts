import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, Self } from '@angular/core';
import { ControlValueAccessor, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, NgControl, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, merge, Observable, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { CsvHeaderMapping } from '../models/csv-header-mapping.model';
import { CsvErrorEnum, csvValidator, noDuplicateMappingInFormArray } from '../validators/csv-validators';

interface Rows {
    expectedHeader: string;
    required: boolean;
}

interface FormValueChanges {
    value: number;
    index: number;
}

@Component({
    selector: 'app-csv-header-mapping',
    templateUrl: './csv-header-mapping.component.html',
    styleUrls: ['./csv-header-mapping.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CsvHeaderMappingComponent implements OnInit, OnDestroy, ControlValueAccessor {
    private _onDestroy = new Subject<void>();
    private _onWrite = new Subject<void>();
    private _onTouch: () => void;
    private _onChange: (value: CsvHeaderMapping<unknown>) => void;
    private _rowsBS = new BehaviorSubject<Rows[]>(null);
    private _parsedHeadersBS = new BehaviorSubject<CsvHeaderMapping<unknown>['parsedHeaders']>(null);
    private _csvHeaderMapping: CsvHeaderMapping<unknown>;
    private _isInitiated = false;

    rows$: Observable<Rows[]>;
    displayedColumns = ['expected', 'parsed'];
    form: UntypedFormGroup;
    parsedHeaders$: Observable<CsvHeaderMapping<unknown>['parsedHeaders']>;

    get mappingFormArray(): UntypedFormArray {
        return this.form.get('headersMapping') as UntypedFormArray;
    }

    constructor(
        private _fb: UntypedFormBuilder,
        private _translate: TranslateService,
        @Self() public ngControl: NgControl
    ) {
        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }
    }

    ngOnInit(): void {
        this.initForm();
        this.rows$ = this._rowsBS.asObservable();
        this.parsedHeaders$ = this._parsedHeadersBS.asObservable();
        this.initControl();
        this.setTableRows();
        this.setFormArray();
        this.formArrayChangeHandler();
        this.markFormArrayAsTouched();
        this._isInitiated = true;
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._onWrite.complete();
    }

    getMappingValue(headerIndex: number): number {
        return headerIndex === 0 ? null : headerIndex - 1;
    }

    clickHandler(): void {
        this.onTouch();
    }

    // CONTROL VALUE ACCESSOR
    registerOnChange(fn: (value: CsvHeaderMapping<unknown>) => void): void {
        this._onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this._onTouch = fn;
    }

    writeValue(csvHeaderMapping: CsvHeaderMapping<unknown>): void {
        this._csvHeaderMapping = csvHeaderMapping;
        // Not in use.
        // In case the control is manipulated via form from the parent formGroup,
        // check the implementation and delete this comment
        if (this._isInitiated) {
            this._onWrite.next();
            this.initControlValue();
            this.setTableRows();
            this.setFormArray();
            this.formArrayChangeHandler();
        }
    }

    private initControl(): void {
        this.initControlValue();
        this.setControlValidators();
        this.setControlRequiredMappingError();
        this.onChange(this._csvHeaderMapping);
    }

    private initControlValue(): void {
        this._csvHeaderMapping = {
            ...this._csvHeaderMapping,
            mappingFields: this._csvHeaderMapping.mappingFields
                .map(mappingField => {
                    const { header } = mappingField;
                    const matchIndex = this.getMatchWithParsedHeaderIndex(header);
                    if (matchIndex >= 0) {
                        return {
                            ...mappingField,
                            columnIndex: matchIndex
                        };
                    } else {
                        return mappingField;
                    }
                })
        };
    }

    private getMatchWithParsedHeaderIndex(fieldKey: string): number {
        const translatedHeader = this._translate.instant(fieldKey);

        return this._csvHeaderMapping.parsedHeaders
            .findIndex(parsedHeader => {
                const normalizedParsedHeader = parsedHeader?.replace(/\*$/, '');
                return normalizedParsedHeader === translatedHeader;
            });
    }

    private setControlRequiredMappingError(): void {
        let isRequiredMapping = null;
        this._csvHeaderMapping.mappingFields
            .forEach(({ columnIndex, required }) => {
                if (required && !Number.isInteger(columnIndex)) {
                    isRequiredMapping = true;
                }
            });
        if (isRequiredMapping) {
            this.ngControl.control.setErrors({
                ...this.ngControl.control.errors,
                [CsvErrorEnum.csvRequiredMapping]: true
            });
        } else if (this.ngControl.control.errors?.[CsvErrorEnum.csvRequiredMapping]) {
            this.ngControl.control.setErrors({
                ...this.ngControl.control.errors,
                [CsvErrorEnum.csvRequiredMapping]: null
            });
        }

    }

    private setControlValidators(): void {
        this.ngControl.control.setValidators(
            Validators.compose([
                this.ngControl.control.validator,
                csvValidator(CsvErrorEnum.csvRequiredMapping),
                csvValidator(CsvErrorEnum.csvDuplicatedMapping)
            ])
        );
    }

    private updateControl(newValue: number, newValueIndex: number): void {
        this.updateControlValue(newValue, newValueIndex);
        this.updateControlErrors();
        this.onChange(this._csvHeaderMapping);
    }

    private updateControlValue(newValue: number, newValueIndex: number): void {
        this._csvHeaderMapping = {
            ...this._csvHeaderMapping,
            mappingFields: this._csvHeaderMapping.mappingFields
                .map((mappingField, index) => {
                    if (newValueIndex === index) {
                        return {
                            ...this._csvHeaderMapping.mappingFields[index],
                            columnIndex: newValue
                        };
                    } else {
                        return mappingField;
                    }
                })
        };
    }

    private updateControlErrors(): void {
        this.setControlRequiredMappingError();
        this.setControlDuplicatedMappingError();
    }

    private setControlDuplicatedMappingError(): void {
        const differentValues = new Set();
        const mapping = this._csvHeaderMapping.mappingFields.filter(({ columnIndex }) => Number.isInteger(columnIndex));
        mapping.forEach(({ columnIndex }) => {
            differentValues.add(columnIndex);
        });
        if (differentValues.size !== mapping.length) {
            this.ngControl.control.setErrors({
                ...this.ngControl.control.errors,
                [CsvErrorEnum.csvDuplicatedMapping]: true
            });
        } else if (this.ngControl.control.errors?.[CsvErrorEnum.csvDuplicatedMapping]) {
            this.ngControl.control.setErrors({
                ...this.ngControl.control.errors,
                [CsvErrorEnum.csvDuplicatedMapping]: null
            });
        }
    }

    private onChange(value: CsvHeaderMapping<unknown>): void {
        if (this._onChange) {
            this._onChange(value);
        }
    }

    private onTouch(): void {
        if (this._onTouch) {
            this._onTouch();
        }
    }

    // Component
    private setTableRows(): void {
        const { mappingFields, parsedHeaders } = this._csvHeaderMapping;
        const rows: Rows[] = mappingFields.map(mappingField => ({
            expectedHeader: this._translate.instant(mappingField.header),
            required: mappingField.required
        }));

        this._parsedHeadersBS.next(parsedHeaders);
        this._rowsBS.next(rows);
    }

    // Form Array
    private initForm(): void {
        this.form = this._fb.group({
            headersMapping: this._fb.array([])
        });
    }

    private setFormArray(): void {
        this.mappingFormArray.clear();
        this._csvHeaderMapping.mappingFields
            .forEach(({ columnIndex, required }) => {
                if (required) {
                    this.mappingFormArray.push(this._fb.control(
                        columnIndex,
                        [
                            Validators.required,
                            noDuplicateMappingInFormArray(this.mappingFormArray)
                        ]
                    ));
                } else {
                    this.mappingFormArray.push(this._fb.control(
                        columnIndex,
                        noDuplicateMappingInFormArray(this.mappingFormArray)
                    ));
                }
            });
    }

    private formArrayChangeHandler(): void {
        const valueChangesArray: Observable<FormValueChanges>[] = this.mappingFormArray.controls
            .map((control, index) => control.valueChanges
                .pipe(map(value => ({ value, index }))));

        merge(...valueChangesArray)
            .pipe(takeUntil(this._onDestroy), takeUntil(this._onWrite))
            .subscribe(({ value, index }) => {
                this.updateControl(value, index);
                this.updateFormArray(value, index);
            });
    }

    private updateFormArray(newValue: number, newValueIndex: number): void {
        this.addOneFormArrayDuplicatedMappingError(newValue);
        this.clearOneFormArrayDuplicatedError(newValueIndex);
    }

    private clearOneFormArrayDuplicatedError(newValueIndex: number): void {
        const pastValue = this.mappingFormArray.value[newValueIndex];
        if (this.mappingFormArray.controls.filter(control => control.value === pastValue).length === 1) {
            const control = this.mappingFormArray.controls.find(control => control.value === pastValue);
            control.updateValueAndValidity({ emitEvent: false });
        }
    }

    private addOneFormArrayDuplicatedMappingError(newValue: number): void {
        if ((this.mappingFormArray.value as number[]).filter(oldValue => oldValue === newValue).length === 1) {
            const foundIndex = (this.mappingFormArray.value as number[]).findIndex(oldValue => oldValue === newValue).toString();
            this.mappingFormArray.get(foundIndex).setErrors({ [CsvErrorEnum.csvDuplicatedMapping]: true });
            this.mappingFormArray.get(foundIndex).markAsTouched();
        }
    }

    private markFormArrayAsTouched(): void {
        if (this.ngControl.control.touched) {
            this.mappingFormArray.markAllAsTouched();
        }
    }
}
