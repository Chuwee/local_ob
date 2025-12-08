import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment';
import { Papa } from 'ngx-papaparse';
import { BehaviorSubject, combineLatest, distinctUntilChanged, map, Observable, startWith, Subject, takeUntil } from 'rxjs';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { PostVoucher, VoucherGroupValidationMethod, VoucherLimitlessValue } from '@admin-clients/cpanel-vouchers-data-access';
import { CsvFile, CsvHeaderMapping, CsvHeaderMappingField, CsvErrorEnum, csvValidator } from '@admin-clients/shared/common/feature/csv';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, requiredFieldsInOneControl } from '@admin-clients/shared/utility/utils';
import {
    createCsvVoucherCodesMappingFields, createCsvVoucherCodesWithPinMappingFields, CsvVoucherCodes, CsvVoucherCodesBasic,
    CsvVoucherCodesValueTypes, CsvVoucherCodesWithPin
} from './import-voucher-codes-mapping-data';

@Component({
    selector: 'app-import-voucher-codes-dialog',
    templateUrl: './import-voucher-codes-dialog.component.html',
    styleUrls: ['./import-voucher-codes-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ImportVoucherCodesDialogComponent implements OnInit, OnDestroy {
    private _onDestroy: Subject<void> = new Subject<void>();
    private _isCsvParserLoadingBS = new BehaviorSubject<boolean>(false);
    private _selectionLoadingBS = new BehaviorSubject<boolean>(false);
    private _headerMatchLoadingBS = new BehaviorSubject<boolean>(false);
    private _currentStepBS = new BehaviorSubject<number>(0);
    private _selectionFormGroupName = 'selection';
    private _headerMatchFormGroupName = 'headerMatch';
    private _parsedHeaders: string[];
    private _file: File;
    private _mappingFields: CsvHeaderMappingField<CsvVoucherCodes>[];
    private readonly _dateFormat = moment.localeData().longDateFormat('L');

    private _setOfBasicKeys = new Set<keyof CsvVoucherCodes>(
        new Set<keyof CsvVoucherCodesBasic>(createCsvVoucherCodesMappingFields()
            .map(csvVoucherCodes => csvVoucherCodes.key))
    );

    private _setOfCodeAndPinKeys = new Set<keyof CsvVoucherCodes>(
        new Set<keyof CsvVoucherCodesWithPin>(createCsvVoucherCodesWithPinMappingFields()
            .map(csvVoucherCodes => csvVoucherCodes.key))
    );

    @ViewChild(WizardBarComponent, { static: true })
    private _wizardBar: WizardBarComponent;

    validationMethod: VoucherGroupValidationMethod;
    isLoading$: Observable<boolean>;
    form: UntypedFormGroup;
    selectionControlName = 'selection';
    headerMatchControlName = 'headerMatch';
    steps: { title: string; form: AbstractControl }[];
    currentStep$: Observable<number>;
    nextText$: Observable<string>;
    isPreviousDisabled$: Observable<boolean>;
    isNextDisabled$: Observable<boolean>;

    get selectionFormGroup(): UntypedFormGroup {
        return this.form.get(this._selectionFormGroupName) as UntypedFormGroup;
    }

    get headerMatchFormGroup(): AbstractControl {
        return this.form.get(this._headerMatchFormGroupName);
    }

    constructor(
        private _dialogRef: MatDialogRef<ImportVoucherCodesDialogComponent>,
        private _translate: TranslateService,
        private _msgDialogSrv: MessageDialogService,
        private _fb: UntypedFormBuilder,
        private _csvParser: Papa,
        @Inject(MAT_DIALOG_DATA) private _data: { validationMethod: VoucherGroupValidationMethod }
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
            this.importVoucherCodes();
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
                title: 'VOUCHER.CODES.IMPORT_DIALOG.SELECTION',
                form: this.selectionFormGroup
            },
            {
                title: 'VOUCHER.CODES.IMPORT_DIALOG.HEADER_MATCH',
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
            .subscribe(({ mappingFields }: CsvHeaderMapping<CsvVoucherCodes>) => {
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

    private sortMappingFields(mapping): CsvHeaderMappingField<CsvVoucherCodes>[] {
        return mapping.sort((mappingFieldA, mappingFieldB) => {
            if ((mappingFieldA.required && mappingFieldB.required) || (!mappingFieldA.required && !mappingFieldB.required)) {
                return 0;
            } else if (mappingFieldB.required) {
                return 1;
            } else if (mappingFieldA.required) {
                return -1;
            }
        });
    }

    private getInitialMappingFields(): CsvHeaderMappingField<CsvVoucherCodes>[] {
        let initialMapping: CsvHeaderMappingField<CsvVoucherCodes>[];

        if (this.validationMethod === VoucherGroupValidationMethod.code) {
            initialMapping = createCsvVoucherCodesMappingFields();
        } else {
            initialMapping = createCsvVoucherCodesWithPinMappingFields();
        }
        return initialMapping;
    }

    private setHeaderMatchGroupValue(mappingFields: CsvHeaderMappingField<CsvVoucherCodes>[]): void {
        const csvHeaderMapping: CsvHeaderMapping<CsvVoucherCodes> = {
            parsedHeaders: this._parsedHeaders,
            mappingFields
        };

        this.headerMatchFormGroup.get(this.headerMatchControlName).reset(csvHeaderMapping);
    }

    private importVoucherCodes(): void {
        this._isCsvParserLoadingBS.next(true);

        this.parseFile(
            this.getMapOfParsedHeadersWithKeys<CsvVoucherCodesBasic>(
                (parsedHeader => this._setOfBasicKeys.has(parsedHeader))
            ),
            this.getMapOfParsedHeadersWithKeys<CsvVoucherCodesWithPin>(
                (parsedHeader => this._setOfCodeAndPinKeys.has(parsedHeader))
            ),
            collectiveCodesToImport => this.successParse(collectiveCodesToImport)
        );
    }

    private getMapOfParsedHeadersWithKeys<T>(condition: (parsedHeader: keyof CsvVoucherCodes) => boolean): Map<string, keyof T> {
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

    private successParse(voucherCodesToImport: PostVoucher[]): void {
        this._isCsvParserLoadingBS.next(false);
        this._dialogRef.close(voucherCodesToImport);
    }

    private parseFile(
        mapOfParsedHeadersBasicWithKeys: Map<string, keyof CsvVoucherCodesBasic>,
        mapOfParsedHeadersPinWithKeys: Map<string, keyof CsvVoucherCodesWithPin>,
        cb: (importVoucherCodes: PostVoucher[]) => void
    ): void {
        const voucherCodesToImport: PostVoucher[] = [];
        const balanceHeader = this.getHeader(mapOfParsedHeadersBasicWithKeys, 'balance');
        const pinHeader = this.getHeader(mapOfParsedHeadersPinWithKeys, 'pin');
        let hasErrors = false;
        let hasFieldsError = false;
        this._file.text().then(res => {
            this._csvParser.parse(res, {
                delimiter: ';', skipEmptyLines: true, header: true, worker: true, dynamicTyping: true,
                step: (({ data, errors }) => {
                    let voucherCode: Partial<CsvVoucherCodes>;

                    if (this.validationMethod === VoucherGroupValidationMethod.code) {
                        voucherCode = this.getImport<CsvVoucherCodesBasic>(mapOfParsedHeadersBasicWithKeys, data,
                            { balance: null });
                    } else {
                        voucherCode = this.getImport<CsvVoucherCodesWithPin>(mapOfParsedHeadersPinWithKeys, data,
                            { balance: null, pin: null });
                    }

                    if (!hasFieldsError && (data[balanceHeader] === null || data[pinHeader] === null)) {
                        hasFieldsError = true;
                    }

                    if (!hasErrors && errors?.length) {
                        hasErrors = true;
                    }
                    voucherCodesToImport.push(this.getVoucherCodePost(voucherCode));
                }),
                complete: (() => {
                    if (hasErrors) {
                        this.showParseError('CSV.IMPORT.PROCESS_ERROR');
                    } else if (hasFieldsError) {
                        this.showParseError('CSV.IMPORT.REQUIRED_FIELD_ERROR');
                    } else {
                        cb(voucherCodesToImport);
                    }
                })
            });
        });
    }

    private getHeader(
        mapOfParsedHeadersWithKeys: Map<string, keyof CsvVoucherCodesBasic> | Map<string, keyof CsvVoucherCodesWithPin>,
        field: string
    ): string {
        let header: string;
        mapOfParsedHeadersWithKeys.forEach(((value, key) => {
            if (value === field) {
                header = key;
            }
        }));
        return header;
    }

    private getImport<T>(
        mapOfParsedHeadersWithKeys: Map<string, keyof T>,
        data: Record<string, CsvVoucherCodesValueTypes>,
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

    private getVoucherCodePost(voucherCode: Partial<CsvVoucherCodes>): PostVoucher {
        const { usage_limit: usageLimit, balance, email, expiration: expirationDate, pin } = { ...voucherCode };
        const expiration = moment(expirationDate, this._dateFormat).toISOString() || null;
        const usage: PostVoucher['usage_limit'] = !usageLimit
            ? { type: VoucherLimitlessValue.unlimited }
            : { type: VoucherLimitlessValue.fixed, value: usageLimit };

        const voucherToPost: PostVoucher = {
            balance,
            expiration,
            pin,
            email,
            usage_limit: usage
        };

        return voucherToPost;
    }

    private showParseError(message: string): void {
        this._isCsvParserLoadingBS.next(false);
        this._msgDialogSrv.showAlert({
            size: DialogSize.SMALL,
            title: 'TITLES.ERROR_DIALOG',
            message
        });
    }
}
