import {
    CsvSeasonTicketRenewalGeneration, PostSeasonTicketRenewalsGeneration
} from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import {
    CsvErrorEnum, CsvFile, CsvHeaderMapping, CsvHeaderMappingField, csvValidator
} from '@admin-clients/shared/common/feature/csv';
import { DialogSize, MessageDialogService, ObDialog } from '@admin-clients/shared/common/ui/components';
import { requiredFieldsInOneControl } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, computed, inject, signal, viewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogActions, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { filter } from 'rxjs/operators';
import {
    RenewalsGenerateCsvImportHeaderMatchComponent
} from './header-match/renewals-generate-csv-import-header-match.component';
import { RenewalsGenerateCsvImportOptionsComponent } from './options/renewals-generate-csv-import-options.component';
import {
    createCsvRenewalGenerationMappingFields, CsvSeasonTicketRenewalGenerationValueTypes
} from './renewals-generate-csv-import-mapping-data.component';
import {
    RenewalsGenerateCsvImportSelectionComponent
} from './selection/renewals-generate-csv-import-selection.component';

@Component({
    selector: 'app-renewals-generate-csv-import',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDialogTitle, MatIconButton, TranslatePipe, MatIcon, WizardBarComponent, MatDialogActions, MatButton,
        MatProgressSpinner, RenewalsGenerateCsvImportOptionsComponent, RenewalsGenerateCsvImportSelectionComponent,
        RenewalsGenerateCsvImportHeaderMatchComponent
    ],
    templateUrl: './renewals-generate-csv-import.component.html'
})
export class RenewalsGenerateCsvImportComponent
    extends ObDialog<RenewalsGenerateCsvImportComponent, null, PostSeasonTicketRenewalsGeneration> {

    readonly #fb = inject(FormBuilder);
    readonly #csvParser = inject(Papa);
    readonly #translateSrv = inject(TranslateService);
    readonly #msgDialogSrv = inject(MessageDialogService);

    private _$wizardBar = viewChild(WizardBarComponent);

    readonly #$isCsvParserLoading = signal(false);
    readonly #$selectionLoading = signal(false);
    readonly #$optionsLoading = signal(false);
    readonly #$headerMatchLoading = signal(false);

    readonly #selectionFormName = 'selection';
    readonly #optionsFormName = 'options';
    readonly #headerMatchFormName = 'headerMatch';

    #file: File;
    #matchedMappingFields: CsvHeaderMappingField<CsvSeasonTicketRenewalGeneration>[];
    #csvHeaderMapping: CsvHeaderMapping<CsvSeasonTicketRenewalGeneration> = {
        parsedHeaders: [],
        mappingFields: []
    };

    readonly form = this.#fb.group({
        [this.#optionsFormName]: this.#fb.group({
            channel: [null as number, Validators.required]
        }),
        [this.#selectionFormName]: this.#fb.group({
            [this.#selectionFormName]: [
                { file: null, processedFile: null } as CsvFile,
                [
                    requiredFieldsInOneControl(Object.keys({ file: null, processedFile: null } as CsvFile)),
                    csvValidator(CsvErrorEnum.csvProcessorFileError)
                ]
            ]
        }),
        [this.#headerMatchFormName]: this.#fb.group({
            [this.#headerMatchFormName]: null
        })
    });

    readonly $currentStep = signal(0);
    readonly steps: { title: string; form: FormGroup }[] = [
        {
            title: 'CUSTOMER.IMPORT_DIALOG.OPTIONS.TITLE',
            form: this.optionsFormGroup
        },
        {
            title: 'CUSTOMER.IMPORT_DIALOG.SELECTION.TITLE',
            form: this.selectionFormGroup
        },
        {
            title: 'CUSTOMER.IMPORT_DIALOG.HEADER_MATCH.TITLE',
            form: this.headerMatchFormGroup
        }
    ];

    readonly $isLoading = computed(() => this.#$selectionLoading() || this.#$optionsLoading() ||
        this.#$headerMatchLoading() || this.#$isCsvParserLoading());

    readonly $statusChanges = toSignal(this.form.statusChanges);

    readonly $isPreviousDisabled = computed(() => this.$currentStep() === 0 || this.$isLoading());
    readonly $isNextDisabled = computed(() => {
        this.$statusChanges();
        return this.$isLoading() || this.steps[this.$currentStep()].form.invalid;
    });

    readonly $nextText = computed(() => (this.$currentStep() === this.steps.length - 1) ?
        this.#translateSrv.instant('FORMS.ACTIONS.GENERATE') : this.#translateSrv.instant('FORMS.ACTIONS.NEXT'));

    get selectionFormGroup(): FormGroup {
        return this.form.get(this.#selectionFormName) as FormGroup;
    }

    get optionsFormGroup(): FormGroup {
        return this.form.get(this.#optionsFormName) as FormGroup;
    }

    get headerMatchFormGroup(): FormGroup {
        return this.form.get(this.#headerMatchFormName) as FormGroup;
    }

    constructor() {
        super(DialogSize.EXTRA_LARGE);
        this.#optionsFormChangeHandler();
        this.#selectionChangeHandler();
        this.#headerMatchChangeHandler();
    }

    selectionLoadingHandler(isLoading: boolean): void {
        this.#$selectionLoading.set(isLoading);
    }

    optionsLoadingHandler(isLoading: boolean): void {
        this.#$optionsLoading.set(isLoading);
    }

    headerMatchLoadingHandler(isLoading: boolean): void {
        this.#$headerMatchLoading.set(isLoading);
    }

    goToStep(step: number): void {
        this.#setStep(step);
    }

    nextStep(): void {
        if (this.$currentStep() === this.steps.length - 1) {
            this.#importCsvRenewalGeneration();
        } else {
            this.#setStep(this.$currentStep() + 1);
        }
    }

    previousStep(): void {
        this.#setStep(this.$currentStep() - 1);
    }

    mapToStepsTitles(steps: { title: string; form: FormGroup }[]): string[] {
        return steps.map(step => step.title);
    }

    close(): void {
        this.dialogRef.close();
    }

    #setStep(step: number): void {
        this._$wizardBar()?.setActiveStep(step);
        this.$currentStep.set(step);
    }

    #optionsFormChangeHandler(): void {
        this.optionsFormGroup.valueChanges
            .pipe(
                filter(Boolean),
                takeUntilDestroyed()
            )
            .subscribe(() => {
                const mappingFieldsByOptions = createCsvRenewalGenerationMappingFields();
                const matchedMappingFields = this.#getMatchedMappingFields(mappingFieldsByOptions);
                const sortedMappingFields = this.#getSortedMappingFields(matchedMappingFields);
                this.#setCsvHeaderMapping({
                    mappingFields: sortedMappingFields,
                    parsedHeaders: this.#csvHeaderMapping.parsedHeaders
                });
            });
    }

    #getMatchedMappingFields(
        mappingFieldsByConfig: CsvHeaderMappingField<CsvSeasonTicketRenewalGeneration>[]
    ): CsvHeaderMappingField<CsvSeasonTicketRenewalGeneration>[] {
        if (!this.#matchedMappingFields) {
            return mappingFieldsByConfig;
        }

        const mapOfKeysWithColumnIndexes = new Map(this.#matchedMappingFields
            .map(mappingField => [mappingField.key, mappingField.columnIndex]));
        return mappingFieldsByConfig
            .map(mappingField => {
                if (mapOfKeysWithColumnIndexes.has(mappingField.key)) {
                    return {
                        ...mappingField,
                        columnIndex: mapOfKeysWithColumnIndexes.get(mappingField.key)
                    };
                } else {
                    return mappingField;
                }
            });
    }

    #getSortedMappingFields(mapping): CsvHeaderMappingField<CsvSeasonTicketRenewalGeneration>[] {
        return mapping.sort((mappingFieldA, mappingFieldB) => {
            if ((mappingFieldA.required && mappingFieldB.required) || (!mappingFieldA.required && !mappingFieldB.required)) {
                return 0;
            } else if (mappingFieldB.required) {
                return 1;
            } else if (mappingFieldA.required) {
                return -1;
            }
            return null;
        });
    }

    #setCsvHeaderMapping(csvHeaderMapping: CsvHeaderMapping<CsvSeasonTicketRenewalGeneration>): void {
        this.#csvHeaderMapping = csvHeaderMapping;
        if (this.headerMatchFormGroup.touched) {
            this.headerMatchFormGroup.get(this.#headerMatchFormName).setValue(csvHeaderMapping);
        } else {
            this.headerMatchFormGroup.get(this.#headerMatchFormName).reset(csvHeaderMapping);
        }
    }

    #selectionChangeHandler(): void {
        this.selectionFormGroup.get(this.#selectionFormName).valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe((csvFileProcessed: CsvFile) => {
                const { processedFile, file } = csvFileProcessed;
                this.#file = file;
                this.#setCsvHeaderMapping({
                    mappingFields: this.#csvHeaderMapping.mappingFields,
                    parsedHeaders: processedFile as string[]
                });
            });
    }

    #headerMatchChangeHandler(): void {
        this.headerMatchFormGroup.get(this.#headerMatchFormName).valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(({ mappingFields }: CsvHeaderMapping<CsvSeasonTicketRenewalGeneration>) => {
                this.#matchedMappingFields = mappingFields;
            });
    }

    #importCsvRenewalGeneration(): void {
        this.#$isCsvParserLoading.set(true);

        const setOfBasicKeys = new Set<keyof CsvSeasonTicketRenewalGeneration>(
            new Set<keyof CsvSeasonTicketRenewalGeneration>(createCsvRenewalGenerationMappingFields()
                .map(csvCustomerField => csvCustomerField.key))
        );

        const mapParsedHeadersWithKeys = this.#getMapOfParsedHeadersWithKeys<CsvSeasonTicketRenewalGeneration>(
            (parsedHeader => setOfBasicKeys.has(parsedHeader))
        );

        this.#parseFile(
            mapParsedHeadersWithKeys,
            renewalsToImport => this.#successParse(renewalsToImport)
        );
    }

    #getMapOfParsedHeadersWithKeys<T>(condition: (parsedHeader: keyof CsvSeasonTicketRenewalGeneration) => boolean): Map<string, keyof T> {
        return new Map<string, keyof T>(
            this.#csvHeaderMapping.parsedHeaders
                .reduce((acc, parsedHeader, parsedIndex) => {
                    const mappingField = this.#matchedMappingFields
                        .find(mappingField => mappingField.columnIndex === parsedIndex && condition(mappingField.key));
                    if (mappingField) {
                        acc.push([parsedHeader, mappingField.key]);
                    }
                    return acc;
                }, [])
        );
    }

    #parseFile(
        mapOfParsedHeadersWithKeys: Map<string, keyof CsvSeasonTicketRenewalGeneration>,
        cb: (customersToImport: CsvSeasonTicketRenewalGeneration[]) => void
    ): void {
        const renewalsToImport: CsvSeasonTicketRenewalGeneration[] = [];
        const mapOfRenewals = new Map<CsvSeasonTicketRenewalGeneration['renewal_id'], number>();
        const renewalIdHeader = this.#getRenewalIdHeader(mapOfParsedHeadersWithKeys);
        let hasErrors = false;
        this.#file.text().then(res => {
            this.#csvParser.parse(res, {
                delimiter: ';', skipEmptyLines: true, header: true, worker: true, dynamicTyping: false,
                step: (({ data, errors }) => {
                    if (!mapOfRenewals.has(data[renewalIdHeader])) {
                        mapOfRenewals.set(data[renewalIdHeader], renewalsToImport.length);

                        const renewalToImport: CsvSeasonTicketRenewalGeneration = this.#getRenewalToImport(
                            mapOfParsedHeadersWithKeys,
                            data
                        );
                        renewalsToImport.push(renewalToImport);
                    }

                    if ((!hasErrors && errors?.length)) {
                        hasErrors = true;
                    }
                }),
                complete: (() => {
                    if (hasErrors) {
                        this.#showParseError();
                    } else {
                        cb(renewalsToImport);
                    }
                })
            });
        });
    }

    #getRenewalIdHeader(mapOfParsedHeadersWithKeys: Map<string, keyof CsvSeasonTicketRenewalGeneration>): string {
        let renewalIdHeader: string;
        mapOfParsedHeadersWithKeys.forEach(((value, key) => {
            if (value === 'renewal_id') {
                renewalIdHeader = key;
            }
        }));
        return renewalIdHeader;
    }

    #getRenewalToImport(
        mapOfBasicParsedHeadersWithKeys: Map<string, keyof CsvSeasonTicketRenewalGeneration>,
        data: Record<string, CsvSeasonTicketRenewalGenerationValueTypes>
    ): CsvSeasonTicketRenewalGeneration {
        return this.#getImport<CsvSeasonTicketRenewalGeneration>(
            mapOfBasicParsedHeadersWithKeys,
            data,
            {}
        );
    }

    #getImport<T>(
        mapOfParsedHeadersWithKeys: Map<string, keyof T>,
        data: Record<string, CsvSeasonTicketRenewalGenerationValueTypes>,
        initialImport: T
    ): T {
        return [...mapOfParsedHeadersWithKeys.keys()]
            .reduce<T>((acc, header) => {
                if (data[header] !== null) {
                    acc[mapOfParsedHeadersWithKeys.get(header) as string] = data[header];
                }
                return acc;
            }, initialImport);
    }

    #showParseError(): void {
        this.#$isCsvParserLoading.set(false);
        const title = this.#translateSrv.instant('TITLES.ERROR_DIALOG');
        const message = this.#translateSrv.instant('CSV.IMPORT.PROCESS_ERROR');
        this.#msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
    }

    #successParse(renewalsToImport: CsvSeasonTicketRenewalGeneration[]): void {
        const postRenewalsToImport: PostSeasonTicketRenewalsGeneration = {
            channelId: this.optionsFormGroup.get('channel').value.id as number,
            data: renewalsToImport
        };
        this.#$isCsvParserLoading.set(false);
        this.dialogRef.close(postRenewalsToImport);
    }
}
