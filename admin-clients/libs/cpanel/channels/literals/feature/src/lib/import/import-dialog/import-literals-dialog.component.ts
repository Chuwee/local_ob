import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { PostLiterals } from '@admin-clients/cpanel-channels-literals-data-access';
import { CsvHeaderMapping, CsvHeaderMappingField, CsvErrorEnum, csvValidator, CsvFile } from '@admin-clients/shared/common/feature/csv';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { deepLiteralKeyRestrictions, literalKeyRestrictions } from '@admin-clients/shared/literals/ui';
import { booleanOrMerge, requiredFieldsInOneControl } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
    MAT_DIALOG_DATA,
    MatDialogRef
} from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { distinctUntilChanged, map, startWith, takeUntil } from 'rxjs/operators';
import { ImportLiteralsHeaderMatchComponent } from './header-match/import-literals-header-match.component';
import {
    createCsvLiteralsToImportMappingFields,
    CsvLiteralsToImport, CsvLiteralsToImportValueTypes
} from './import-literals-mapping-data';
import { ImportLiteralsSelectionComponent } from './selection/import-literals-selection.component';

export type ImportLiteralsDialogData = { languages: string[]; deepKeysAllowed?: boolean };

@Component({
    selector: 'app-import-literals-dialog',
    imports: [
        MaterialModule, WizardBarComponent, FlexLayoutModule, AsyncPipe, TranslatePipe,
        ImportLiteralsSelectionComponent, ImportLiteralsHeaderMatchComponent
    ],
    templateUrl: './import-literals-dialog.component.html',
    styleUrls: ['./import-literals-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImportLiteralsDialogComponent implements OnInit, OnDestroy {
    private _onDestroy: Subject<void> = new Subject<void>();
    private _isCsvParserLoadingBS = new BehaviorSubject<boolean>(false);
    private _selectionLoadingBS = new BehaviorSubject<boolean>(false);
    private _headerMatchLoadingBS = new BehaviorSubject<boolean>(false);
    private _currentStepBS = new BehaviorSubject<number>(0);
    private _selectionFormGroupName = 'selection';
    private _headerMatchFormGroupName = 'headerMatch';
    private _parsedHeaders: string[];
    private _file: File;
    private _mappingFields: CsvHeaderMappingField<CsvLiteralsToImport>[];

    private readonly _dialogRef = inject(MatDialogRef<ImportLiteralsDialogComponent>);
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _translate = inject(TranslateService);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _csvParser = inject(Papa);
    private readonly _data = inject<ImportLiteralsDialogData>(MAT_DIALOG_DATA);

    private _setOfLiteralsKeys = new Set<keyof CsvLiteralsToImport>(
        new Set<keyof CsvLiteralsToImport>(createCsvLiteralsToImportMappingFields(this._data.languages)
            .map(csvLiterals => csvLiterals.key))
    );

    @ViewChild(WizardBarComponent, { static: true })
    private _wizardBar: WizardBarComponent;

    form: FormGroup;
    currentStep$: Observable<number>;

    selectionControlName = 'selection';
    headerMatchControlName = 'headerMatch';
    steps: { title: string; form: AbstractControl }[];
    isLoading$: Observable<boolean>;
    isPreviousDisabled$: Observable<boolean>;
    isNextDisabled$: Observable<boolean>;
    nextText$: Observable<string>;

    readonly languages = this._data.languages;

    get selectionFormGroup(): FormGroup {
        return this.form.get(this._selectionFormGroupName) as FormGroup;
    }

    get headerMatchFormGroup(): AbstractControl {
        return this.form.get(this._headerMatchFormGroupName);
    }

    constructor() {
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
            this.importLiterals();
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
                title: 'LITERALS.EXTERNAL.IMPORT_DIALOG.SELECTION.TITLE',
                form: this.selectionFormGroup
            },
            {
                title: 'LITERALS.EXTERNAL.IMPORT_DIALOG.HEADER_MATCH.TITLE',
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
            .subscribe(({ mappingFields }: CsvHeaderMapping<CsvLiteralsToImport>) => {
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

    private sortMappingFields(mapping): CsvHeaderMappingField<CsvLiteralsToImport>[] {
        return mapping.sort((mappingFieldA, mappingFieldB) => {
            if ((mappingFieldA.required && mappingFieldB.required) || (!mappingFieldA.required && !mappingFieldB.required)) {
                return 0;
            } else if (mappingFieldB.required) {
                return 1;
            } else if (mappingFieldA.required) {
                return -1;
            }
            return 0;
        });
    }

    private getInitialMappingFields(): CsvHeaderMappingField<CsvLiteralsToImport>[] {
        return createCsvLiteralsToImportMappingFields(this.languages);
    }

    private setHeaderMatchGroupValue(mappingFields: CsvHeaderMappingField<CsvLiteralsToImport>[]): void {
        const csvHeaderMapping: CsvHeaderMapping<CsvLiteralsToImport> = {
            parsedHeaders: this._parsedHeaders,
            mappingFields
        };

        this.headerMatchFormGroup.get(this.headerMatchControlName).reset(csvHeaderMapping);
    }

    private importLiterals(): void {
        this._isCsvParserLoadingBS.next(true);

        this.parseFile(
            this.getMapOfParsedHeadersWithKeys<CsvLiteralsToImport>(
                (parsedHeader => this._setOfLiteralsKeys.has(parsedHeader))
            ),
            literalsToImport => this.successParse(literalsToImport)
        );
    }

    private successParse(literalsToImport: PostLiterals): void {
        this._isCsvParserLoadingBS.next(false);
        this._dialogRef.close(literalsToImport);
    }

    private getMapOfParsedHeadersWithKeys<T>(condition: (parsedHeader: keyof CsvLiteralsToImport) => boolean): Map<string, keyof T> {
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
        mapOfParsedHeadersBasicWithKeys: Map<string, keyof CsvLiteralsToImport>,
        cb: (importLiterals: PostLiterals) => void
    ): void {
        const platformLanguages = this.languages;
        const literalsToImport =
            platformLanguages.reduce((acc, curr) => (acc[curr] = [], acc), {}) as PostLiterals;

        let errorMessage = null;
        const keyRestrictions = this._data.deepKeysAllowed ? deepLiteralKeyRestrictions : literalKeyRestrictions;
        this._file.text().then(res => {
            this._csvParser.parse(res, {
                delimiter: ';', skipEmptyLines: true, header: true, worker: true, dynamicTyping: false,
                step: (({ data, errors }) => {
                    const literal = this.getImport<CsvLiteralsToImport>(mapOfParsedHeadersBasicWithKeys, data,
                        platformLanguages.reduce((acc, curr) => (acc[curr] = null, acc),
                            { literalKey: null }) as CsvLiteralsToImport
                    );
                    if (keyRestrictions.test(literal.literalKey)) {
                        platformLanguages.forEach(platformLanguage => {
                            literalsToImport[platformLanguage].push({
                                key: literal.literalKey,
                                value: literal[platformLanguage]
                            });
                        });
                    } else {
                        errorMessage = 'LITERALS.EXTERNAL.IMPORT_KEY_ERROR';
                    }

                    if (!errorMessage && errors?.length) {
                        errorMessage = 'CSV.IMPORT.PROCESS_ERROR';
                    }

                }),
                complete: (() => {
                    if (errorMessage) {
                        this.showParseError(errorMessage);
                    } else {
                        cb(literalsToImport);
                    }
                })
            });
        });
    }

    private getImport<T>(
        mapOfParsedHeadersWithKeys: Map<string, keyof T>,
        data: Record<string, CsvLiteralsToImportValueTypes>,
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

    private showParseError(message: string): void {
        this._isCsvParserLoadingBS.next(false);
        const title = this._translate.instant('TITLES.ERROR_DIALOG');
        this._msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message: this._translate.instant(message) });
    }
}
