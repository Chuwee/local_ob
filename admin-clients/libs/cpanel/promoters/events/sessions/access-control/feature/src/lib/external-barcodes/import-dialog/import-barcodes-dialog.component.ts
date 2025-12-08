import { ExternalBarcode, PostBarcodesToImport } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { CsvFile, CsvHeaderMapping, CsvHeaderMappingField, CsvErrorEnum, csvValidator } from '@admin-clients/shared/common/feature/csv';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge, requiredFieldsInOneControl } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { BehaviorSubject, combineLatest, distinctUntilChanged, map, Observable, startWith, Subject, takeUntil } from 'rxjs';
import { ImportBarcodesHeaderMatchComponent } from './header-match/import-barcodes-header-match.component';
import { createCsvBarcodeMappingFields, CsvBarcode, CsvBarcodeValueTypes } from './import-barcodes-mapping-data';
import { ImportBarcodesSelectionComponent } from './selection/import-barcodes-selection.component';

@Component({
    selector: 'app-import-barcodes-dialog',
    templateUrl: './import-barcodes-dialog.component.html',
    styleUrls: ['./import-barcodes-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, WizardBarComponent, FlexLayoutModule, CommonModule, TranslatePipe,
        ImportBarcodesHeaderMatchComponent, ImportBarcodesSelectionComponent
    ]
})
export class ImportBarcodesDialogComponent implements OnInit, OnDestroy {
    private _onDestroy: Subject<void> = new Subject<void>();
    private _isCsvParserLoadingBS = new BehaviorSubject<boolean>(false);
    private _selectionLoadingBS = new BehaviorSubject<boolean>(false);
    private _headerMatchLoadingBS = new BehaviorSubject<boolean>(false);
    private _currentStepBS = new BehaviorSubject<number>(0);
    private _file: File;
    private _parsedHeaders: string[];
    private _mappingFields: CsvHeaderMappingField<CsvBarcode>[];
    private _selectionFormGroupName = 'selection';
    private _headerMatchFormGroupName = 'headerMatch';
    private _setOfKeys = new Set<keyof CsvBarcode>(
        new Set<keyof CsvBarcode>([
            'barcode',
            'locator',
            'accessId',
            'row',
            'seat',
            'ATTENDANT_NAME',
            'ATTENDANT_SURNAME',
            'ATTENDANT_ID_NUMBER',
            'ATTENDANT_MAIL'
        ])
    );

    @ViewChild(WizardBarComponent, { static: true })
    private _wizardBar: WizardBarComponent;

    form: UntypedFormGroup;
    selectionControlName = 'selection';
    headerMatchControlName = 'headerMatch';
    isLoading$: Observable<boolean>;
    steps: { title: string; form: AbstractControl }[];
    isPreviousDisabled$: Observable<boolean>;
    isNextDisabled$: Observable<boolean>;
    nextText$: Observable<string>;
    currentStep$: Observable<number>;

    get selectionFormGroup(): UntypedFormGroup {
        return this.form.get(this._selectionFormGroupName) as UntypedFormGroup;
    }

    get headerMatchFormGroup(): AbstractControl {
        return this.form.get(this._headerMatchFormGroupName);
    }

    constructor(
        private _dialogRef: MatDialogRef<ImportBarcodesDialogComponent>,
        private _fb: UntypedFormBuilder,
        private _translate: TranslateService,
        private _msgDialogSrv: MessageDialogService,
        private _csvParser: Papa
    ) {
        this._dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
    }

    ngOnInit(): void {
        this.initForm();
        this.setLoading();
        this.setSteps();
        this.selectionChangeHandler();
        this.headerMatchChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    selectionLoadingHandler(isLoading: boolean): void {
        this._selectionLoadingBS.next(isLoading);
    }

    headerMatchLoadingHandler(isLoading: boolean): void {
        this._headerMatchLoadingBS.next(isLoading);
    }

    close(): void {
        this._dialogRef.close();
    }

    mapToStepsTitles(steps: { title: string; form: AbstractControl }[]): string[] {
        return steps.map(step => step.title);
    }

    goToStep(step: number): void {
        this.setStep(step);
    }

    previousStep(): void {
        this.setStep(this._currentStepBS.value - 1);
    }

    nextStep(): void {
        if (this._currentStepBS.value === this.steps.length - 1) {
            this.importBarcodes();
        } else {
            this.setStep(this._currentStepBS.value + 1);
        }
    }

    private initForm(): void {
        const csvFileSelector: CsvFile = { file: null, processedFile: null };

        const selectionFormGroup = this._fb.group({
            [this.selectionControlName]: [
                csvFileSelector,
                [
                    requiredFieldsInOneControl(Object.keys(csvFileSelector)),
                    csvValidator(CsvErrorEnum.csvProcessorFileError)
                ]
            ]
        });

        const headerMatchFormGroup = this._fb.group({
            [this.headerMatchControlName]: null
        });

        this.form = this._fb.group({
            [this._selectionFormGroupName]: selectionFormGroup,
            [this._headerMatchFormGroupName]: headerMatchFormGroup
        });
    }

    private setLoading(): void {
        this.isLoading$ = booleanOrMerge([
            this._selectionLoadingBS.asObservable(),
            this._headerMatchLoadingBS.asObservable(),
            this._isCsvParserLoadingBS.asObservable()
        ]);
    }

    private setSteps(): void {
        this.steps = [
            {
                title: 'EVENTS.SESSION.WHITE_LIST.IMPORT.SELECTION.TITLE',
                form: this.selectionFormGroup
            },
            {
                title: 'EVENTS.SESSION.WHITE_LIST.IMPORT.HEADER_MATCH.TITLE',
                form: this.headerMatchFormGroup
            }
        ];

        this.currentStep$ = this._currentStepBS.asObservable();

        this.nextText$ = this._currentStepBS.asObservable()
            .pipe(
                map(currentStep => {
                    if (currentStep === this.steps.length - 1) {
                        return this._translate.instant('FORMS.ACTIONS.IMPORT');
                    } else {
                        return this._translate.instant('FORMS.ACTIONS.NEXT');
                    }
                }),
                distinctUntilChanged()
            );

        this.isPreviousDisabled$ = combineLatest([
            this._currentStepBS.asObservable(),
            this.isLoading$
        ]).pipe(
            map(([currentStep, isLoading]) => currentStep === 0 || isLoading),
            distinctUntilChanged()
        );

        this.isNextDisabled$ = combineLatest([
            this._currentStepBS.asObservable(),
            this.isLoading$,
            this.form.statusChanges.pipe(startWith(null as unknown))
        ]).pipe(
            map(([currentStep, isLoading]) => isLoading || this.steps[currentStep].form.invalid),
            distinctUntilChanged()
        );
    }

    private setStep(step: number): void {
        this._wizardBar.setActiveStep(step);
        this._currentStepBS.next(step);
    }

    private headerMatchChangeHandler(): void {
        this.headerMatchFormGroup.get(this.headerMatchControlName).valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(({ mappingFields }: CsvHeaderMapping<CsvBarcode>) => {
                this._mappingFields = mappingFields;
            });
    }

    private selectionChangeHandler(): void {
        this.selectionFormGroup.get(this.selectionControlName).valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((csvFileProcessed: CsvFile) => {
                const { processedFile, file } = csvFileProcessed;
                this._file = file;
                this._parsedHeaders = processedFile as string[];
                this.initHeaderMatch();
                this.updateHeaderMatch();
            });
    }

    private initHeaderMatch(): void {
        const initialMapping = createCsvBarcodeMappingFields();
        const sortedMapping = this.sortMappingFields(initialMapping);
        this.setHeaderMatchGroupValue(sortedMapping);
    }

    private updateHeaderMatch(): void {
        const initialMapping = createCsvBarcodeMappingFields();
        const updatedMapping = this.getUpdatedMappingFields(initialMapping);
        const sortedMapping = this.sortMappingFields(updatedMapping);
        this.setHeaderMatchGroupValue(sortedMapping);
    }

    private getUpdatedMappingFields(
        initialMapping: CsvHeaderMappingField<CsvBarcode>[]
    ): CsvHeaderMappingField<CsvBarcode>[] {
        const mapOfMappingValues = new Map(this._mappingFields.map(mappingField => [mappingField.key, mappingField.columnIndex]));
        const updatedMapping: CsvHeaderMappingField<CsvBarcode>[] = initialMapping
            .map(mappingField => {
                if (mapOfMappingValues.has(mappingField.key)) {
                    return {
                        ...mappingField,
                        columnIndex: mapOfMappingValues.get(mappingField.key)
                    };
                } else {
                    return mappingField;
                }
            });
        return updatedMapping;
    }

    private sortMappingFields(mapping): CsvHeaderMappingField<CsvBarcode>[] {
        return mapping.sort((mappingFieldA, mappingFieldB) => {
            if ((mappingFieldA.required && mappingFieldB.required) || (!mappingFieldA.required && !mappingFieldB.required)) {
                return 0;
            } else if (mappingFieldB.required) {
                return 1;
            } else if (mappingFieldA.required) {
                return -1;
            } else {
                return undefined;
            }
        });
    }

    private setHeaderMatchGroupValue(mappingFields: CsvHeaderMappingField<CsvBarcode>[]): void {
        const csvHeaderMapping: CsvHeaderMapping<CsvBarcode> = {
            parsedHeaders: this._parsedHeaders,
            mappingFields
        };

        this.headerMatchFormGroup.get(this.headerMatchControlName).reset(csvHeaderMapping);
    }

    private importBarcodes(): void {
        this._isCsvParserLoadingBS.next(true);

        const mapOfParsedHeadersWithKeys = this.getMapOfParsedHeadersWithKeys<CsvBarcode>(
            (parsedHeader => this._setOfKeys.has(parsedHeader))
        );

        this.parseFile(
            mapOfParsedHeadersWithKeys,
            barcodesToImport => this.successParse(barcodesToImport)
        );

    }

    private successParse(customersToImport: ExternalBarcode[]): void {
        const postBarcodesToImport: PostBarcodesToImport = {
            barcodes: customersToImport
        };

        this._isCsvParserLoadingBS.next(false);
        this._dialogRef.close(postBarcodesToImport);
    }

    private getMapOfParsedHeadersWithKeys<T>(condition: (parsedHeader: keyof CsvBarcode) => boolean): Map<string, keyof T> {
        const result = new Map<string, keyof T>(
            this._parsedHeaders
                .reduce((acc, parsedHeader, parsedIndex) => {
                    const mappingField = this._mappingFields
                        .find(mappingField => mappingField.columnIndex === parsedIndex && condition(mappingField.key));
                    if (mappingField) {
                        acc.push([parsedHeader, mappingField.key]);
                    }
                    return acc;
                }, [])
        );
        return result;
    }

    private parseFile(
        mapOfParsedHeadersWithKeys: Map<string, keyof CsvBarcode>,
        cb: (importBarcodes: ExternalBarcode[]) => void
    ): void {
        const barcodesToImport: CsvBarcode[] = [];
        const mapOfBarcodes = new Map<CsvBarcode['barcode'], number>();
        const barcodeHeader = this.getBarcodeHeader(mapOfParsedHeadersWithKeys);
        let hasErrors = false;
        let noBarcodeError = false;
        this._file.text().then(res => {
            this._csvParser.parse(res, {
                delimiter: ';', skipEmptyLines: true, header: true, worker: true, dynamicTyping: false,
                step: (({ data, errors }) => {
                    if (!mapOfBarcodes.has(data[barcodeHeader])) {
                        mapOfBarcodes.set(data[barcodeHeader], barcodesToImport.length);

                        const barcodeToImport: CsvBarcode = this.getBarcodeToImport(
                            mapOfParsedHeadersWithKeys,
                            data
                        );
                        barcodesToImport.push(barcodeToImport);
                    }

                    if (!data[barcodeHeader]) {
                        noBarcodeError = true;
                    }

                    if ((!hasErrors && errors?.length)) {
                        hasErrors = true;
                    }
                }),
                complete: (() => {
                    if (hasErrors) {
                        this.showProcessError('CSV.IMPORT.PROCESS_ERROR');
                    } else if (noBarcodeError) {
                        this.showProcessError('CSV.IMPORT.NO_BARCODE_ERROR');
                    } else {
                        this.prepareBarcodesToImportReqBody(barcodesToImport);
                        cb(barcodesToImport);
                    }
                })
            });
        });
    }

    private getBarcodeToImport(
        mapOfParsedHeadersWithKeys: Map<string, keyof CsvBarcode>,
        data: Record<string, CsvBarcodeValueTypes>
    ): CsvBarcode {
        const initialBarcodeToImport: CsvBarcode = {
            barcode: null,
            locator: null,
            row: null,
            seat: null
        };
        const result = this.getImport<CsvBarcode>(
            mapOfParsedHeadersWithKeys,
            data,
            initialBarcodeToImport
        );
        return result;
    }

    private getImport<T>(
        mapOfParsedHeadersWithKeys: Map<string, keyof T>,
        data: Record<string, CsvBarcodeValueTypes>,
        initialImport: T
    ): T {
        const result = [...mapOfParsedHeadersWithKeys.keys()]
            .reduce<T>((acc, header) => {
                if (data[header] !== null) {
                    acc[mapOfParsedHeadersWithKeys.get(header) as string] = data[header];
                }
                return acc;
            }, initialImport);
        return result;
    }

    private getBarcodeHeader(mapOfParsedHeadersWithKeys: Map<string, keyof CsvBarcode>): string {
        let barcodeHeader: string;
        mapOfParsedHeadersWithKeys.forEach(((value, key) => {
            if (value === 'barcode') {
                barcodeHeader = key;
            }
        }));
        return barcodeHeader;
    }

    private showProcessError(msgKey: string): void {
        this._isCsvParserLoadingBS.next(false);
        const title = this._translate.instant('TITLES.ERROR_DIALOG');
        const message = this._translate.instant(msgKey);
        this._msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
    }

    //The attendant data fields must be under a specific property in the request body
    private prepareBarcodesToImportReqBody(barcodesToImport: CsvBarcode[]): void {
        const attendantSubstring = 'ATTENDANT_';
        if (barcodesToImport.some(barcode => Object.keys(barcode).some(key => key.includes(attendantSubstring)))) {
            barcodesToImport.map(barcode => {
                barcode.attendantData = {};
                Object.keys(barcode).forEach(key => {
                    if (key.includes(attendantSubstring)) {
                        barcode.attendantData[key] = barcode[key];
                        delete barcode[key];
                    }
                });
                if (barcode.accessId) {
                    const accessId = parseInt(`${barcode.accessId}`);
                    if (isNaN(accessId)) {
                        delete barcode.accessId;
                    } else {
                        barcode.accessId = accessId;
                    }
                }
            });
        }
    }
}
