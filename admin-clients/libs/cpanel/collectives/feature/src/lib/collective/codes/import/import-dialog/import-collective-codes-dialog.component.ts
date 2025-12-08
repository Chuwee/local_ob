import { CollectiveValidationMethod, PostCollectiveCode } from '@admin-clients/cpanel/collectives/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { CsvHeaderMapping, CsvHeaderMappingField, CsvFile, CsvErrorEnum, csvValidator } from '@admin-clients/shared/common/feature/csv';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, requiredFieldsInOneControl } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment';
import { Papa } from 'ngx-papaparse';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { distinctUntilChanged, map, startWith, takeUntil } from 'rxjs/operators';
import {
    createCsvCollectiveCodesMappingFields, createCsvCollectiveCodesUserMappingFields, createCsvCollectiveCodesUserPassMappingFields,
    CsvCollectiveCodes, CsvCollectiveCodesBasic, CsvCollectiveCodesUser, CsvCollectiveCodesUserPass, CsvCollectiveCodesValueTypes
} from './import-collective-codes-mapping-data';

@Component({
    selector: 'app-import-collective-codes-dialog',
    templateUrl: './import-collective-codes-dialog.component.html',
    styleUrls: ['./import-collective-codes-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ImportCollectiveCodesDialogComponent implements OnInit, OnDestroy {
    private _onDestroy: Subject<void> = new Subject<void>();
    private _isCsvParserLoadingBS = new BehaviorSubject<boolean>(false);
    private _selectionLoadingBS = new BehaviorSubject<boolean>(false);
    private _headerMatchLoadingBS = new BehaviorSubject<boolean>(false);
    private _currentStepBS = new BehaviorSubject<number>(0);
    private _selectionFormGroupName = 'selection';
    private _headerMatchFormGroupName = 'headerMatch';
    private _parsedHeaders: string[];
    private _file: File;
    private _mappingFields: CsvHeaderMappingField<CsvCollectiveCodes>[];
    private readonly _dateFormat = moment.localeData().longDateFormat('L');

    private _setOfBasicKeys = new Set<keyof CsvCollectiveCodes>(
        new Set<keyof CsvCollectiveCodesBasic>(createCsvCollectiveCodesMappingFields()
            .map(csvCollectiveCodes => csvCollectiveCodes.key))
    );

    private _setOfUserKeys = new Set<keyof CsvCollectiveCodes>(
        new Set<keyof CsvCollectiveCodesUser>(createCsvCollectiveCodesUserMappingFields()
            .map(csvCollectiveCodes => csvCollectiveCodes.key))
    );

    private _setOfUserPassKeys = new Set<keyof CsvCollectiveCodes>(
        new Set<keyof CsvCollectiveCodesUserPass>(createCsvCollectiveCodesUserPassMappingFields()
            .map(csvCollectiveCodes => csvCollectiveCodes.key))
    );

    @ViewChild(WizardBarComponent, { static: true })
    private _wizardBar: WizardBarComponent;

    form: UntypedFormGroup;
    currentStep$: Observable<number>;
    validationMethod: CollectiveValidationMethod;

    selectionControlName = 'selection';
    headerMatchControlName = 'headerMatch';
    steps: { title: string; form: AbstractControl }[];
    isLoading$: Observable<boolean>;
    isPreviousDisabled$: Observable<boolean>;
    isNextDisabled$: Observable<boolean>;
    nextText$: Observable<string>;

    get selectionFormGroup(): UntypedFormGroup {
        return this.form.get(this._selectionFormGroupName) as UntypedFormGroup;
    }

    get headerMatchFormGroup(): AbstractControl {
        return this.form.get(this._headerMatchFormGroupName);
    }

    constructor(
        private _dialogRef: MatDialogRef<ImportCollectiveCodesDialogComponent>,
        private _fb: UntypedFormBuilder,
        private _translate: TranslateService,
        private _msgDialogSrv: MessageDialogService,
        private _csvParser: Papa,
        @Inject(MAT_DIALOG_DATA) private _data: { validationMethod: CollectiveValidationMethod }
    ) {
        this.validationMethod = _data.validationMethod;
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

    goToStep(step: number): void {
        this.setStep(step);
    }

    nextStep(): void {
        if (this._currentStepBS.value === this.steps.length - 1) {
            this.importCollectiveCodes();
        } else {
            this.setStep(this._currentStepBS.value + 1);
        }
    }

    previousStep(): void {
        this.setStep(this._currentStepBS.value - 1);
    }

    mapToStepsTitles(steps: { title: string; form: AbstractControl }[]): string[] {
        return steps.map(step => step.title);
    }

    close(): void {
        this._dialogRef.close();
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
                title: 'COLLECTIVE.CODES.IMPORT_DIALOG.SELECTION.TITLE',
                form: this.selectionFormGroup
            },
            {
                title: 'COLLECTIVE.CODES.IMPORT_DIALOG.HEADER_MATCH.TITLE',
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
            .subscribe(({ mappingFields }: CsvHeaderMapping<CsvCollectiveCodes>) => {
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
            });
    }

    private initHeaderMatch(): void {
        const initialMapping = this.getInitialMappingFields();
        const sortedMapping = this.sortMappingFields(initialMapping);
        this.setHeaderMatchGroupValue(sortedMapping);
    }

    private sortMappingFields(mapping: CsvHeaderMappingField<CsvCollectiveCodes>[]): CsvHeaderMappingField<CsvCollectiveCodes>[] {
        return mapping.sort((a, b) => {
            if ((a.required && b.required) || (!a.required && !b.required)) {
                return 0;
            } else if (b.required) {
                return 1;
            } else if (a.required) {
                return -1;
            }
            return null;
        });
    }

    private getInitialMappingFields(): CsvHeaderMappingField<CsvCollectiveCodes>[] {
        let initialMapping: CsvHeaderMappingField<CsvCollectiveCodes>[];

        if (this.validationMethod === CollectiveValidationMethod.userPassword) {
            initialMapping = createCsvCollectiveCodesUserPassMappingFields();
        } else if (this.validationMethod === CollectiveValidationMethod.user) {
            initialMapping = createCsvCollectiveCodesUserMappingFields();
        } else {
            initialMapping = createCsvCollectiveCodesMappingFields();
        }
        return initialMapping;
    }

    private setHeaderMatchGroupValue(mappingFields: CsvHeaderMappingField<CsvCollectiveCodes>[]): void {
        const csvHeaderMapping: CsvHeaderMapping<CsvCollectiveCodes> = {
            parsedHeaders: this._parsedHeaders,
            mappingFields
        };

        this.headerMatchFormGroup.get(this.headerMatchControlName).reset(csvHeaderMapping);
    }

    private importCollectiveCodes(): void {
        this._isCsvParserLoadingBS.next(true);

        this.parseFile(
            this.getMapOfParsedHeadersWithKeys<CsvCollectiveCodesBasic>(
                (parsedHeader => this._setOfBasicKeys.has(parsedHeader))
            ),
            this.getMapOfParsedHeadersWithKeys<CsvCollectiveCodesUser>(
                (parsedHeader => this._setOfUserKeys.has(parsedHeader))
            ),
            this.getMapOfParsedHeadersWithKeys<CsvCollectiveCodesUserPass>(
                (parsedHeader => this._setOfUserPassKeys.has(parsedHeader))
            ),
            collectiveCodesToImport => this.successParse(collectiveCodesToImport)
        );
    }

    private successParse(collectiveCodesToImport: PostCollectiveCode[]): void {
        this._isCsvParserLoadingBS.next(false);
        this._dialogRef.close(collectiveCodesToImport);
    }

    private getMapOfParsedHeadersWithKeys<T>(condition: (parsedHeader: keyof CsvCollectiveCodes) => boolean): Map<string, keyof T> {
        return new Map<string, keyof T>(
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
    }

    private parseFile(
        mapOfParsedHeadersBasicWithKeys: Map<string, keyof CsvCollectiveCodesBasic>,
        mapOfParsedHeadersUserWithKeys: Map<string, keyof CsvCollectiveCodesUser>,
        mapOfParsedHeadersUserPassWithKeys: Map<string, keyof CsvCollectiveCodesUserPass>,
        cb: (importCollectiveCodes: PostCollectiveCode[]) => void
    ): void {
        const collectiveCodesToImport: PostCollectiveCode[] = [];

        let hasErrors = false;
        this._file.text().then(res => {
            this._csvParser.parse(res, {
                delimiter: ';', skipEmptyLines: true, header: true, worker: true, dynamicTyping: false,
                step: (({ data, errors }) => {
                    let collectiveCode: Partial<CsvCollectiveCodes>;
                    if (this.validationMethod === CollectiveValidationMethod.user) {
                        collectiveCode = this.getImport<CsvCollectiveCodesUser>(mapOfParsedHeadersUserWithKeys, data,
                            { user: null });
                        collectiveCode.code = collectiveCode.user;
                    } else if (this.validationMethod === CollectiveValidationMethod.userPassword) {
                        collectiveCode = this.getImport<CsvCollectiveCodesUserPass>(mapOfParsedHeadersUserPassWithKeys, data,
                            { user: null, key: null });
                        collectiveCode.code = collectiveCode.user;
                    } else {
                        collectiveCode = this.getImport<CsvCollectiveCodesBasic>(mapOfParsedHeadersBasicWithKeys, data,
                            { code: null });
                        collectiveCode.code = typeof collectiveCode.code ===
                            'string' ? collectiveCode.code.toUpperCase() : collectiveCode.code;
                    }

                    if (!hasErrors && errors?.length) {
                        hasErrors = true;
                    }
                    collectiveCodesToImport.push(this.getCollectiveCodePost(collectiveCode));

                }),
                complete: (() => {
                    if (hasErrors) {
                        this.showParseError();
                    } else {
                        cb(collectiveCodesToImport);
                    }
                })
            });
        });
    }

    private getImport<T>(
        mapOfParsedHeadersWithKeys: Map<string, keyof T>,
        data: Record<string, CsvCollectiveCodesValueTypes>,
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

    private getCollectiveCodePost(collectiveCode: Partial<CsvCollectiveCodes>): PostCollectiveCode {
        const { usage_limit: usageLimit, code, key } = { ...collectiveCode };
        const from = moment(collectiveCode.validity_from, this._dateFormat).toISOString();
        const to = moment(collectiveCode.validity_to, this._dateFormat).toISOString();

        return {
            code, key,
            validity_period: { from, to },
            usage_limit: Number(usageLimit) || 0
        };
    }

    private showParseError(): void {
        this._isCsvParserLoadingBS.next(false);
        const title = this._translate.instant('TITLES.ERROR_DIALOG');
        const message = this._translate.instant('CSV.IMPORT.PROCESS_ERROR');
        this._msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
    }
}
