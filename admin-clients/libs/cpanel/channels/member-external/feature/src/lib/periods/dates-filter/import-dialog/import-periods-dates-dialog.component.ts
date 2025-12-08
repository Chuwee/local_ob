import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
    MAT_DIALOG_DATA, MatDialogActions,
    MatDialogRef, MatDialogTitle
} from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { distinctUntilChanged, map, startWith } from 'rxjs/operators';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { CsvHeaderMapping, CsvHeaderMappingField, CsvErrorEnum, csvValidator, CsvFile } from '@admin-clients/shared/common/feature/csv';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, requiredFieldsInOneControl } from '@admin-clients/shared/utility/utils';
import {
    createCsvPeriodsDatesToImportMappingFields, CsvPeriodsDatesToImport, CsvPeriodsDatesToImportValueTypes
} from './import-periods-dates-mapping-data';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { MatButton, MatIconButton } from '@angular/material/button';
import { AsyncPipe, NgIf } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { ImportPeriodsDatesSelectionComponent } from './selection/import-periods-dates-selection.component';
import { ImportPeriodsDatesHeaderMatchComponent } from './header-match/import-periods-dates-header-match.component';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { PeriodsDates } from '@admin-clients/cpanel-channels-member-external-data-access';

@Component({
    selector: 'app-import-periods-dates-dialog',
    templateUrl: './import-periods-dates-dialog.component.html',
    styleUrls: ['./import-periods-dates-dialog.component.scss'],
    imports: [
        MatDialogTitle,
        FlexModule,
        WizardBarComponent,
        MatIconButton,
        TranslatePipe,
        AsyncPipe,
        MatIcon,
        ImportPeriodsDatesSelectionComponent,
        ImportPeriodsDatesHeaderMatchComponent,
        MatDialogActions,
        MatButton,
        FlexLayoutModule,
        MatProgressSpinner,
        NgIf
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImportPeriodsDatesDialogComponent implements OnInit {
    readonly #onDestroy = inject(DestroyRef);
    #isCsvParserLoadingBS = new BehaviorSubject<boolean>(false);
    private _selectionLoadingBS = new BehaviorSubject<boolean>(false);
    #headerMatchLoadingBS = new BehaviorSubject<boolean>(false);
    #currentStepBS = new BehaviorSubject<number>(0);
    #selectionFormGroupName = 'selection';
    #headerMatchFormGroupName = 'headerMatch';
    #parsedHeaders: string[];
    #file: File;
    #mappingFields: CsvHeaderMappingField<CsvPeriodsDatesToImport>[];

    readonly #dialogRef = inject(MatDialogRef<ImportPeriodsDatesDialogComponent>);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #translate = inject(TranslateService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #csvParser = inject(Papa);
    readonly #data = inject<{ fields: string[] }>(MAT_DIALOG_DATA);

    private _setOfLiteralsKeys = new Set<keyof CsvPeriodsDatesToImport>(
        new Set<keyof CsvPeriodsDatesToImport>(createCsvPeriodsDatesToImportMappingFields()
            .map(csvPeriodsDates => csvPeriodsDates.key))
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

    readonly fields = this.#data.fields;

    get selectionFormGroup(): FormGroup {
        return this.form.get(this.#selectionFormGroupName) as FormGroup;
    }

    get headerMatchFormGroup(): AbstractControl {
        return this.form.get(this.#headerMatchFormGroupName);
    }

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
    }

    ngOnInit(): void {
        this.initForm();
        this.setLoading();
        this.setSteps();
        this.selectionChangeHandler();
        this.headerMatchChangeHandler();
    }

    selectionLoadingHandler(isLoading: boolean): void {
        this._selectionLoadingBS.next(isLoading);
    }

    headerMatchLoadingHandler(isLoading: boolean): void {
        this.#headerMatchLoadingBS.next(isLoading);
    }

    goToStep(step: number): void {
        this.setStep(step);
    }

    nextStep(): void {
        if (this.#currentStepBS.value === this.steps.length - 1) {
            this.importLiterals();
        } else {
            this.setStep(this.#currentStepBS.value + 1);
        }
    }

    previousStep(): void {
        this.setStep(this.#currentStepBS.value - 1);
    }

    mapToStepsTitles(steps: { title: string; form: AbstractControl }[]): string[] {
        return steps.map(step => step.title);
    }

    close(): void {
        this.#dialogRef.close();
    }

    private initForm(): void {
        const csvFileSelector = { file: null, processedFile: null };

        const selectionFormGroup = this.#fb.group({
            [this.selectionControlName]: [
                csvFileSelector,
                [
                    requiredFieldsInOneControl(Object.keys(csvFileSelector)),
                    csvValidator(CsvErrorEnum.csvProcessorFileError)
                ]
            ]
        });

        const headerMatchFormGroup = this.#fb.group({
            [this.headerMatchControlName]: null
        });

        this.form = this.#fb.group({
            [this.#selectionFormGroupName]: selectionFormGroup,
            [this.#headerMatchFormGroupName]: headerMatchFormGroup
        });
    }

    private setLoading(): void {
        this.isLoading$ = booleanOrMerge([
            this._selectionLoadingBS.asObservable(),
            this.#headerMatchLoadingBS.asObservable(),
            this.#isCsvParserLoadingBS.asObservable()
        ]);
    }

    private setSteps(): void {
        this.steps = [
            {
                title: 'MEMBER_EXTERNAL.LIMIT_PORTAL_ACCESS.IMPORT_DIALOG.SELECTION.TITLE',
                form: this.selectionFormGroup
            },
            {
                title: 'MEMBER_EXTERNAL.LIMIT_PORTAL_ACCESS.IMPORT_DIALOG.HEADER_MATCH.TITLE',
                form: this.headerMatchFormGroup
            }
        ];

        this.currentStep$ = this.#currentStepBS.asObservable();

        this.nextText$ = this.#currentStepBS.asObservable()
            .pipe(
                map(currentStep => {
                    if (currentStep === this.steps.length - 1) {
                        return this.#translate.instant('FORMS.ACTIONS.IMPORT');
                    } else {
                        return this.#translate.instant('FORMS.ACTIONS.NEXT');
                    }
                }),
                distinctUntilChanged()
            );

        this.isPreviousDisabled$ = combineLatest([
            this.#currentStepBS.asObservable(),
            this.isLoading$
        ]).pipe(
            map(([currentStep, isLoading]) => currentStep === 0 || isLoading),
            distinctUntilChanged()
        );

        this.isNextDisabled$ = combineLatest([
            this.#currentStepBS.asObservable(),
            this.isLoading$,
            this.form.statusChanges.pipe(startWith(null as unknown))
        ]).pipe(
            map(([currentStep, isLoading]) => isLoading || this.steps[currentStep].form.invalid),
            distinctUntilChanged()
        );
    }

    private setStep(step: number): void {
        this._wizardBar.setActiveStep(step);
        this.#currentStepBS.next(step);
    }

    private headerMatchChangeHandler(): void {
        this.headerMatchFormGroup.get(this.headerMatchControlName).valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(({ mappingFields }: CsvHeaderMapping<CsvPeriodsDatesToImport>) => {
                this.#mappingFields = mappingFields;
            });
    }

    private selectionChangeHandler(): void {
        this.selectionFormGroup.get(this.selectionControlName).valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe((csvFileProcessed: CsvFile) => {
                const { processedFile, file } = csvFileProcessed;
                this.#file = file;
                this.#parsedHeaders = processedFile as string[];
                this.initHeaderMatch();
            });
    }

    private initHeaderMatch(): void {
        const initialMapping = this.getInitialMappingFields();
        const sortedMapping = this.sortMappingFields(initialMapping);
        this.setHeaderMatchGroupValue(sortedMapping);
    }

    private sortMappingFields(mapping): CsvHeaderMappingField<CsvPeriodsDatesToImport>[] {
        return mapping.sort((mappingFieldA, mappingFieldB) => {
            if ((mappingFieldA.required && mappingFieldB.required) || (!mappingFieldA.required && !mappingFieldB.required)) {
                return 0;
            } else if (mappingFieldB.required) {
                return 1;
            } else if (mappingFieldA.required) {
                return -1;
            } else {
                return 0;
            }
        });
    }

    private getInitialMappingFields(): CsvHeaderMappingField<CsvPeriodsDatesToImport>[] {
        return createCsvPeriodsDatesToImportMappingFields();
    }

    private setHeaderMatchGroupValue(mappingFields: CsvHeaderMappingField<CsvPeriodsDatesToImport>[]): void {
        const csvHeaderMapping: CsvHeaderMapping<CsvPeriodsDatesToImport> = {
            parsedHeaders: this.#parsedHeaders,
            mappingFields
        };

        this.headerMatchFormGroup.get(this.headerMatchControlName).reset(csvHeaderMapping);
    }

    private importLiterals(): void {
        this.#isCsvParserLoadingBS.next(true);

        this.parseFile(
            this.getMapOfParsedHeadersWithKeys<CsvPeriodsDatesToImport>(
                (parsedHeader => this._setOfLiteralsKeys.has(parsedHeader))
            ),
            periodsDatesToImport => this.successParse(periodsDatesToImport)
        );
    }

    private successParse(periodsDatesToImport: PeriodsDates[]): void {
        this.#isCsvParserLoadingBS.next(false);
        this.#dialogRef.close(periodsDatesToImport);
    }

    private getMapOfParsedHeadersWithKeys<T>(condition: (parsedHeader: keyof CsvPeriodsDatesToImport) => boolean): Map<string, keyof T> {
        return new Map<string, keyof T>(
            this.#parsedHeaders
                .reduce((acc, parsedHeader, parsedIndex) => {
                    const mappingField = this.#mappingFields
                        .find(mappingField => mappingField.columnIndex === parsedIndex && condition(mappingField.key));
                    if (mappingField) {
                        acc.push([parsedHeader, mappingField.key]);
                    }
                    return acc;
                }, [])
        );
    }

    private parseFile(
        mapOfParsedHeadersWithKeys: Map<string, keyof CsvPeriodsDatesToImport>,
        cb: (importPeriodsDates: PeriodsDates[]) => void
    ): void {
        const periodsDatesToImport: PeriodsDates[] = [];
        let hasErrors = false;
        this.#file.text().then(res => {
            this.#csvParser.parse(res, {
                delimiter: ';', skipEmptyLines: true, header: true, worker: true, dynamicTyping: false,
                step: (({ data, errors }) => {
                    const periodsDateToImport: PeriodsDates = this.getImport(
                        mapOfParsedHeadersWithKeys,
                        data,
                        {} as CsvPeriodsDatesToImport
                    );
                    periodsDatesToImport.push(periodsDateToImport);

                    if ((!hasErrors && errors?.length)) {
                        hasErrors = true;
                    }
                }),
                complete: (() => {
                    if (hasErrors) {
                        this.showParseError('CSV.IMPORT.PROCESS_ERROR');
                    } else {
                        cb(periodsDatesToImport);
                    }
                })
            });
        });
    }

    private getImport<T>(
        mapOfParsedHeadersWithKeys: Map<string, keyof T>,
        data: Record<string, CsvPeriodsDatesToImportValueTypes>,
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
        this.#isCsvParserLoadingBS.next(false);
        const title = this.#translate.instant('TITLES.ERROR_DIALOG');
        this.#msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message: this.#translate.instant(message) });
    }
}
