/* eslint-disable @typescript-eslint/naming-convention */
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import {
    CustomerProductToImport, CustomersService, CustomerToImport, PostCustomersToImport
} from '@admin-clients/cpanel-viewers-customers-data-access';
import { EntitiesBaseService, EntitiesBaseState, Entity } from '@admin-clients/shared/common/data-access';
import { CsvHeaderMapping, CsvHeaderMappingField, CsvErrorEnum, csvValidator, CsvFile } from '@admin-clients/shared/common/feature/csv';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, requiredFieldsInOneControl } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, viewChild, OnDestroy } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment';
import { Papa } from 'ngx-papaparse';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { distinctUntilChanged, filter, map, startWith } from 'rxjs/operators';
import { ImportCustomerHeaderMatchComponent } from './header-match/import-customer-header-match.component';
import {
    createCsvCustomerMappingFields, createCsvCustomerProductsMappingFields,
    createCsvCustomerProductsOnlyNotNumberedMappingFields, createCsvCustomerProductsOnlyNumberedMappingFields, CsvCustomer,
    CsvCustomerBasic, CsvCustomerProduct, CsvCustomerValueTypes
} from './import-customer-mapping-data';
import { ImportDateFormats } from './models/import-date-formats.enum';
import { ImportOptionsEnum } from './models/import-options.enum';
import { ImportOptions } from './models/import-options.model';
import { ProductTypeImport } from './models/product-type-import.enum';
import { ImportCustomerOptionsComponent } from './options/import-customer-options.component';
import { ImportCustomerSelectionComponent } from './selection/import-customer-selection.component';

const IMPORT_DATE_FORMAT = 'yyyy-MM-dd';

@Component({
    selector: 'app-import-customer-dialog',
    templateUrl: './import-customer-dialog.component.html',
    styleUrls: ['./import-customer-dialog.component.scss'],
    imports: [
        MatDialogTitle, MatDialogActions, TranslatePipe, MatIcon, MatIconButton, MatButton, WizardBarComponent, AsyncPipe,
        ImportCustomerOptionsComponent, ImportCustomerSelectionComponent, ImportCustomerHeaderMatchComponent, MatProgressSpinner
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        EntitiesBaseState,
        EntitiesBaseService,
        DatePipe
    ]
})
export class ImportCustomerDialogComponent implements OnDestroy {
    readonly #dialogRef = inject(MatDialogRef<ImportCustomerDialogComponent>);
    readonly #translateSrv = inject(TranslateService);
    readonly #fb = inject(FormBuilder);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #csvParser = inject(Papa);
    readonly #customerService = inject(CustomersService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #datePipe = inject(DatePipe);
    readonly #data = inject<{ currentEntity: Entity }>(MAT_DIALOG_DATA);

    private _$wizardBar = viewChild(WizardBarComponent);

    readonly #isCsvParserLoadingBS = new BehaviorSubject<boolean>(false);
    readonly #selectionLoadingBS = new BehaviorSubject<boolean>(false);
    readonly #optionsLoadingBS = new BehaviorSubject<boolean>(false);
    readonly #headerMatchLoadingBS = new BehaviorSubject<boolean>(false);
    readonly #currentStepBS = new BehaviorSubject<number>(0);

    readonly #selectionFormGroupName = 'selection';
    readonly #optionsFormGroupName = 'options';
    readonly #headerMatchFormGroupName = 'headerMatch';
    readonly $hasEntityCustomerTypes = toSignal(this.#entitiesSrv.entityCustomerTypes.get$().pipe(map(cts => !!cts?.length)));
    readonly $entity = toSignal(this.#entitiesSrv.getEntity$());

    readonly $adminCustomerFields = toSignal(this.#customerService.customer.forms.adminCustomer.get$().pipe(
        filter(Boolean),
        map(fields => fields.flat())
    ));

    readonly #importDateFormatMap: Record<ImportDateFormats, string[]> = {
        DMY: ['DD/MM/YYYY', 'DD-MM-YYYY', 'D/M/YY', 'D-M-YY', 'D-M-YYYY'],
        MDY: ['MM/DD/YYYY', 'MM-DD-YYYY', 'M/D/YY', 'M-D-YY'],
        YMD: ['YYYY/MM/DD', 'YYYY-MM-DD', 'YY/M/D', 'YY-M-D']
    };

    #file: File;
    #matchedMappingFields: CsvHeaderMappingField<CsvCustomer>[];
    #csvHeaderMapping: CsvHeaderMapping<CsvCustomer> = {
        parsedHeaders: [],
        mappingFields: []
    };

    form: UntypedFormGroup;
    importOptions: ImportOptions = {
        [ImportOptionsEnum.entity]: null,
        [ImportOptionsEnum.isProductsImport]: false,
        [ImportOptionsEnum.productsType]: ProductTypeImport.numberedAndNotNumbered,
        [ImportOptionsEnum.productsWithVendor]: false,
        [ImportOptionsEnum.productsVendor]: null,
        [ImportOptionsEnum.isOverride]: false,
        [ImportOptionsEnum.dateFormat]: 'YMD'
    };

    overrideControlName = ImportOptionsEnum.isOverride;
    productsControlName = ImportOptionsEnum.isProductsImport;
    dateFormatControlName = ImportOptionsEnum.dateFormat;
    productsTypeControlName = ImportOptionsEnum.productsType;
    productsWithVendorControlName = ImportOptionsEnum.productsWithVendor;
    productVendorControlName = ImportOptionsEnum.productsVendor;
    entityControlName = ImportOptionsEnum.entity;
    selectionControlName = 'selection';
    headerMatchControlName = 'headerMatch';
    currentStep$: Observable<number>;
    steps: { title: string; form: AbstractControl }[];
    isLoading$: Observable<boolean>;
    isPreviousDisabled$: Observable<boolean>;
    isNextDisabled$: Observable<boolean>;
    nextText$: Observable<string>;

    get selectionFormGroup(): UntypedFormGroup {
        return this.form.get(this.#selectionFormGroupName) as UntypedFormGroup;
    }

    get optionsFormGroup(): UntypedFormGroup {
        return this.form.get(this.#optionsFormGroupName) as UntypedFormGroup;
    }

    get headerMatchFormGroup(): UntypedFormGroup {
        return this.form.get(this.#headerMatchFormGroupName) as UntypedFormGroup;
    }

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.LARGE);
        this.#initForm();
        this.#setLoading();
        this.#setSteps();
        this.#optionsFormChangeHandler();
        this.#selectionChangeHandler();
        this.#headerMatchChangeHandler();
        this.optionsFormGroup.get(this.entityControlName).setValue(this.#data.currentEntity);
    }

    ngOnDestroy(): void {
        this.#customerService.customer.forms.adminCustomer.clear();
    }

    selectionLoadingHandler(isLoading: boolean): void {
        this.#selectionLoadingBS.next(isLoading);
    }

    optionsLoadingHandler(isLoading: boolean): void {
        this.#optionsLoadingBS.next(isLoading);
    }

    headerMatchLoadingHandler(isLoading: boolean): void {
        this.#headerMatchLoadingBS.next(isLoading);
    }

    goToStep(step: number): void {
        this.#setStep(step);
    }

    nextStep(): void {
        if (this.#currentStepBS.value === this.steps.length - 1) {
            this.#importCustomers();
        } else {
            this.#setStep(this.#currentStepBS.value + 1);
        }
    }

    previousStep(): void {
        this.#setStep(this.#currentStepBS.value - 1);
    }

    mapToStepsTitles(steps: { title: string; form: AbstractControl }[]): string[] {
        return steps.map(step => step.title);
    }

    close(): void {
        this.#dialogRef.close();
    }

    #initForm(): void {
        const csvFileSelector: CsvFile = { file: null, processedFile: null };

        const optionsFormGroup = this.#fb.group({
            [this.entityControlName]: [this.importOptions[ImportOptionsEnum.entity], Validators.required],
            [this.overrideControlName]: this.importOptions[ImportOptionsEnum.isOverride],
            [this.productsControlName]: this.importOptions[ImportOptionsEnum.isProductsImport],
            [this.productsWithVendorControlName]: this.importOptions[ImportOptionsEnum.productsWithVendor],
            [this.productVendorControlName]: [{ value: this.importOptions[ImportOptionsEnum.productsVendor], disabled: true }, Validators.required],
            [this.dateFormatControlName]: [this.importOptions[ImportOptionsEnum.dateFormat], Validators.required],
            [this.productsTypeControlName]: { value: this.importOptions[ImportOptionsEnum.productsType], disabled: true }
        });

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
            [this.#optionsFormGroupName]: optionsFormGroup,
            [this.#selectionFormGroupName]: selectionFormGroup,
            [this.#headerMatchFormGroupName]: headerMatchFormGroup
        });

        this.form.get(`${this.#optionsFormGroupName}.${this.entityControlName}`).valueChanges.pipe(
            takeUntilDestroyed()
        ).subscribe((entity: Entity) => {
            this.#entitiesSrv.loadEntity(entity.id);
            this.#entitiesSrv.entityCustomerTypes.clear();
            this.#entitiesSrv.entityCustomerTypes.load(entity.id);
            this.#customerService.customer.forms.adminCustomer.clear();
            this.#customerService.customer.forms.adminCustomer.load(entity.id);
        });

    }

    #setLoading(): void {
        this.isLoading$ = booleanOrMerge([
            this.#selectionLoadingBS.asObservable(),
            this.#optionsLoadingBS.asObservable(),
            this.#headerMatchLoadingBS.asObservable(),
            this.#isCsvParserLoadingBS.asObservable(),
            this.#customerService.customer.forms.adminCustomer.loading$()
        ]);
    }

    #setSteps(): void {
        this.steps = [
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

        this.currentStep$ = this.#currentStepBS.asObservable();

        this.nextText$ = this.#currentStepBS.asObservable()
            .pipe(
                map(currentStep => {
                    if (currentStep === this.steps.length - 1) {
                        return this.#translateSrv.instant('FORMS.ACTIONS.IMPORT');
                    } else {
                        return this.#translateSrv.instant('FORMS.ACTIONS.NEXT');
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

    #setStep(step: number): void {
        this._$wizardBar()?.setActiveStep(step);
        this.#currentStepBS.next(step);
    }

    #optionsFormChangeHandler(): void {
        combineLatest([
            this.optionsFormGroup.valueChanges,
            this.#customerService.customer.forms.adminCustomer.get$(),
            this.#entitiesSrv.entityCustomerTypes.get$()
        ])
            .pipe(
                filter(([options, adminFields]) => !!options && !!adminFields),
                takeUntilDestroyed()
            )
            .subscribe(([options]) => {
                this.importOptions = options;
                const mappingFieldsByOptions = this.#getMappingFieldsByOptions(options);
                const matchedMappingFields = this.#getMatchedMappingFields(mappingFieldsByOptions);
                const sortedMappingFields = this.#getSortedMappingFields(matchedMappingFields);
                this.#setCsvHeaderMappingBS({
                    mappingFields: sortedMappingFields,
                    parsedHeaders: this.#csvHeaderMapping.parsedHeaders
                });
            });
    }

    #getMappingFieldsByOptions(options: ImportOptions): CsvHeaderMappingField<CsvCustomer>[] {
        const vendorConfig = { ...this.$entity()?.settings?.external_integration?.auth_vendor };
        vendorConfig.enabled = options[ImportOptionsEnum.productsWithVendor] && vendorConfig?.enabled;
        let mappingFields: CsvHeaderMappingField<CsvCustomer>[] = createCsvCustomerMappingFields(this.$hasEntityCustomerTypes(),
            this.$adminCustomerFields(), options[ImportOptionsEnum.isProductsImport], vendorConfig);

        if (options[ImportOptionsEnum.isProductsImport]) {
            if (options[ImportOptionsEnum.productsType] === ProductTypeImport.numbered) {
                mappingFields = mappingFields.concat(createCsvCustomerProductsOnlyNumberedMappingFields());
            } else if (options[ImportOptionsEnum.productsType] === ProductTypeImport.notNumbered) {
                mappingFields = mappingFields.concat(createCsvCustomerProductsOnlyNotNumberedMappingFields());
            } else if (options[ImportOptionsEnum.productsType] === ProductTypeImport.numberedAndNotNumbered) {
                mappingFields = mappingFields.concat(createCsvCustomerProductsMappingFields());
            }

        }
        return mappingFields;
    }

    #getMatchedMappingFields(
        mappingFieldsByConfig: CsvHeaderMappingField<CsvCustomer>[]
    ): CsvHeaderMappingField<CsvCustomer>[] {
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

    #getSortedMappingFields(mapping): CsvHeaderMappingField<CsvCustomer>[] {
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

    #setCsvHeaderMappingBS(csvHeaderMapping: CsvHeaderMapping<CsvCustomer>): void {
        this.#csvHeaderMapping = csvHeaderMapping;
        if (this.headerMatchFormGroup.touched) {
            this.headerMatchFormGroup.get(this.headerMatchControlName).setValue(csvHeaderMapping);
        } else {
            this.headerMatchFormGroup.get(this.headerMatchControlName).reset(csvHeaderMapping);
        }
    }

    #selectionChangeHandler(): void {
        this.selectionFormGroup.get(this.selectionControlName).valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe((csvFileProcessed: CsvFile) => {
                const { processedFile, file } = csvFileProcessed;
                this.#file = file;
                this.#setCsvHeaderMappingBS({
                    mappingFields: this.#csvHeaderMapping.mappingFields,
                    parsedHeaders: processedFile as string[]
                });
            });
    }

    #headerMatchChangeHandler(): void {
        this.headerMatchFormGroup.get(this.headerMatchControlName).valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(({ mappingFields }: CsvHeaderMapping<CsvCustomer>) => {
                this.#matchedMappingFields = mappingFields;
            });
    }

    #importCustomers(): void {
        this.#isCsvParserLoadingBS.next(true);

        const mapOfBasicParsedHeadersWithKeys = this.#getMapOfBasicParsedHeaderWithKeys();
        const mapOfProductParsedHeadersWithKeys = this.#getMapOfProductParsedHeadersWithKeys();

        this.#parseFile(
            mapOfBasicParsedHeadersWithKeys,
            mapOfProductParsedHeadersWithKeys,
            customersToImport => this.#successParse(customersToImport)
        );
    }

    #getMapOfBasicParsedHeaderWithKeys(): Map<string, keyof CsvCustomerBasic> {
        const vendorConfig = this.$entity()?.settings?.external_integration?.auth_vendor;
        const setOfBasicKeys = new Set<keyof CsvCustomer>(
            new Set<keyof CsvCustomerBasic>(createCsvCustomerMappingFields(this.$hasEntityCustomerTypes(), this.$adminCustomerFields(),
                this.importOptions[ImportOptionsEnum.isProductsImport], vendorConfig)
                .map(csvCustomerField => csvCustomerField.key))
        );

        return this.#getMapOfParsedHeadersWithKeys<CsvCustomerBasic>(
            (parsedHeader => setOfBasicKeys.has(parsedHeader))
        );
    }

    #getMapOfProductParsedHeadersWithKeys(): Map<string, keyof CsvCustomerProduct> {
        if (!this.importOptions[ImportOptionsEnum.isProductsImport]) {
            return new Map();
        }

        let result: Map<string, keyof CsvCustomerProduct>;

        if (this.importOptions[ImportOptionsEnum.productsType] === ProductTypeImport.numbered) {
            const setOfProductOnlyNumberedKeys = new Set<keyof CsvCustomer>(
                new Set<keyof CsvCustomerProduct>(createCsvCustomerProductsOnlyNumberedMappingFields()
                    .map(csvCustomers => csvCustomers.key))
            );

            result = this.#getMapOfParsedHeadersWithKeys<CsvCustomerProduct>(
                (parsedHeader => setOfProductOnlyNumberedKeys.has(parsedHeader))
            );
        } else if (this.importOptions[ImportOptionsEnum.productsType] === ProductTypeImport.notNumbered) {
            const setOfProductOnlyNotNumberedKeys = new Set<keyof CsvCustomer>(
                new Set<keyof CsvCustomerProduct>(createCsvCustomerProductsOnlyNotNumberedMappingFields()
                    .map(csvCustomers => csvCustomers.key))
            );

            result = this.#getMapOfParsedHeadersWithKeys<CsvCustomerProduct>(
                (parsedHeader => setOfProductOnlyNotNumberedKeys.has(parsedHeader))
            );
        } else if (this.importOptions[ImportOptionsEnum.productsType] === ProductTypeImport.numberedAndNotNumbered) {
            const setOfProductKeys = new Set<keyof CsvCustomer>(
                new Set<keyof CsvCustomerProduct>(createCsvCustomerProductsMappingFields()
                    .map(csvCustomers => csvCustomers.key))
            );

            result = this.#getMapOfParsedHeadersWithKeys<CsvCustomerProduct>(
                (parsedHeader => setOfProductKeys.has(parsedHeader))
            );
        }

        return result;
    }

    #getMapOfParsedHeadersWithKeys<T>(condition: (parsedHeader: keyof CsvCustomer) => boolean): Map<string, keyof T> {
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
        mapOfBasicParsedHeadersWithKeys: Map<string, keyof CsvCustomerBasic>,
        mapOfProductParsedHeadersWithKeys: Map<string, keyof CsvCustomerProduct>,
        cb: (customersToImport: CustomerToImport[]) => void
    ): void {
        const customersToImport: CustomerToImport[] = [];
        const mapOfCustomers = new Map<CsvCustomer['email'], number>();
        const emailHeader = this.#getEmailHeader(mapOfBasicParsedHeadersWithKeys);
        let hasErrors = false;

        this.#readFileWithEncodingFallback(this.#file).then(csvText => {
            this.#csvParser.parse(csvText, {
                delimiter: ';', skipEmptyLines: true, header: true, worker: true, dynamicTyping: false,
                step: ({ data, errors }) => {
                    const email = data[emailHeader] as string;
                    const cleanedEmail = email?.toLowerCase().trim();
                    if (!mapOfCustomers.has(cleanedEmail) || !cleanedEmail) {
                        mapOfCustomers.set(cleanedEmail, customersToImport.length);

                        let customerToImport = this.#getCustomerToImportWithBasicInfo(
                            mapOfBasicParsedHeadersWithKeys,
                            data
                        );

                        customerToImport = this.#getCustomerToImportWithProductInfo(
                            customerToImport,
                            mapOfProductParsedHeadersWithKeys,
                            data
                        );

                        customersToImport.push(customerToImport);
                    } else {
                        const customerToImport = customersToImport[mapOfCustomers.get(cleanedEmail)];

                        this.#updateCustomerToImportWithProductInfo(
                            customerToImport,
                            mapOfProductParsedHeadersWithKeys,
                            data
                        );
                    }

                    if (!hasErrors && errors?.length) {
                        hasErrors = true;
                    }
                },
                complete: () => {
                    if (hasErrors) {
                        this.#showParseError();
                    } else {
                        cb(customersToImport);
                    }
                }
            });
        }).catch(err => {
            console.error('Error reading CSV file:', err);
            this.#showParseError();
        });
    }

    #readFileWithEncodingFallback(file: File): Promise<string> {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();

            reader.onload = () => {
                const text = reader.result as string;
                if (text.includes('\uFFFD') || text.includes('�')) {
                    const reader1252 = new FileReader();
                    reader1252.onload = () => resolve(reader1252.result as string);
                    reader1252.onerror = reject;
                    reader1252.readAsText(file, 'windows-1252');
                } else {
                    resolve(text);
                }
            };

            reader.onerror = reject;
            reader.readAsText(file, 'utf-8');
        });
    }

    #getEmailHeader(mapOfBasicParsedHeadersWithKeys: Map<string, keyof CsvCustomerBasic>): string {
        let emailHeader: string;
        mapOfBasicParsedHeadersWithKeys.forEach(((value, key) => {
            if (value === 'email') {
                emailHeader = key;
            }
        }));
        return emailHeader;
    }

    #getCustomerToImportWithBasicInfo(
        mapOfBasicParsedHeadersWithKeys: Map<string, keyof CsvCustomerBasic>,
        data: Record<string, CsvCustomerValueTypes>
    ): CustomerToImport {
        const csvCustomerBasic = this.#getParsedCsvInfo<CsvCustomerBasic>(
            mapOfBasicParsedHeadersWithKeys,
            data,
            {}
        );

        return this.#getCustomerBasicInfoFromCsvInfo(csvCustomerBasic);
    }

    #getParsedCsvInfo<T>(
        mapOfParsedHeadersWithKeys: Map<string, keyof T>,
        data: Record<string, CsvCustomerValueTypes>,
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

    #getCustomerToImportWithProductInfo(
        customerToImport: CustomerToImport,
        mapOfProductParsedHeadersWithKeys: Map<string, keyof CsvCustomerProduct>,
        data: Record<string, CsvCustomerValueTypes>
    ): CustomerToImport {
        if (!mapOfProductParsedHeadersWithKeys.size) {
            return customerToImport;
        }

        const csvCustomerProduct = this.#getCsvCustomerProduct(mapOfProductParsedHeadersWithKeys, data);
        if (csvCustomerProduct) {
            const customerProductToImport = this.#getCustomerProductFromCsvInfo(csvCustomerProduct);
            return {
                ...customerToImport,
                products: [customerProductToImport]
            };
        } else {
            return customerToImport;
        }
    }

    #getCsvCustomerProduct(
        mapOfProductParsedHeadersWithFields: Map<string, keyof CsvCustomerProduct>,
        data: Record<string, CsvCustomerValueTypes>
    ): CsvCustomerProduct {
        const result = this.#getParsedCsvInfo<CsvCustomerProduct>(
            mapOfProductParsedHeadersWithFields,
            data,
            {}
        );

        if (Object.keys(result).length) {
            return result;
        } else {
            return null;
        }
    }

    #updateCustomerToImportWithProductInfo(
        customerToImport: CustomerToImport,
        mapOfProductParsedHeadersWithKeys: Map<string, keyof CsvCustomerProduct>,
        data: Record<string, CsvCustomerValueTypes>
    ): void {
        if (!mapOfProductParsedHeadersWithKeys.size) {
            return;
        }

        const csvCustomerProduct = this.#getCsvCustomerProduct(mapOfProductParsedHeadersWithKeys, data);
        if (csvCustomerProduct && customerToImport.products) {
            const customerProductToImport = this.#getCustomerProductFromCsvInfo(csvCustomerProduct);
            customerToImport.products.push(customerProductToImport);
        } else if (csvCustomerProduct && !customerToImport.products) {
            const customerProductToImport = this.#getCustomerProductFromCsvInfo(csvCustomerProduct);
            customerToImport.products = [customerProductToImport];
        }
    }

    #getCustomerBasicInfoFromCsvInfo(csvCustomer: CsvCustomer): CustomerToImport {
        const entityVendorConfig = this.$entity()?.settings.external_integration?.auth_vendor;
        const isProductWithVendorEnabled = this.importOptions[ImportOptionsEnum.productsWithVendor];

        const customerBasicInfo: CustomerToImport = {
            location: {
                address: csvCustomer.address || null,
                city: csvCustomer.city || null,
                country: csvCustomer.country || null,
                postal_code: csvCustomer.postal_code || null,
                country_subdivision: csvCustomer.country_subdivision || null
            },
            email: csvCustomer.email || null,
            birthday: this.#parseAndFormatDate(csvCustomer.birthday, this.importOptions[ImportOptionsEnum.dateFormat]),
            gender: csvCustomer.gender || null,
            title: (csvCustomer.title as string) === '' ? null : csvCustomer.title,
            name: csvCustomer.name || null,
            id_card: csvCustomer.id_card || null,
            phone: csvCustomer.phone || null,
            phone_2: csvCustomer.phone_2 === '' ? null : csvCustomer.phone_2,
            address_2: csvCustomer.address_2 === '' ? null : csvCustomer.address_2,
            surname: csvCustomer.surname || null,
            member_id: csvCustomer.member_id || null,
            manager_email: csvCustomer.manager_email || null,
            ...((this.$hasEntityCustomerTypes()) && {
                customer_types: (() => {
                    const types = csvCustomer.customer_types?.toString()?.split(',')?.map(type => type.trim()).filter(type => type !== '');
                    return types?.length ? types : null;
                })()
            }),
            iban: csvCustomer.iban || null,
            bic: csvCustomer.bic || null,
            membership_start_date: this.#parseAndFormatDate(csvCustomer.membership_start_date, this.importOptions[ImportOptionsEnum.dateFormat])
        };

        if (isProductWithVendorEnabled && entityVendorConfig?.enabled && csvCustomer.external_id) {
            customerBasicInfo.auth_vendors = [{
                id: csvCustomer.external_id,
                vendor: this.importOptions[ImportOptionsEnum.productsVendor]
            }];
        }
        return customerBasicInfo;
    }

    #getCustomerProductFromCsvInfo(csvCustomer: CsvCustomer): CustomerProductToImport {
        return {
            event: {
                id: this.#normalizeNumberField(String(csvCustomer.event_id)),
                name: csvCustomer.event_name || null,
                type: csvCustomer.event_type || null
            },
            price_zone_name: csvCustomer.price_zone_name || null,
            purchase_date: this.#parseAndFormatDate(csvCustomer.purchase_date, this.importOptions[ImportOptionsEnum.dateFormat]),
            rate_name: csvCustomer.rate_name || null,
            row_name: csvCustomer.row_name || null,
            seat_name: csvCustomer.seat_name || null,
            not_numbered_zone_name: csvCustomer.not_numbered_zone_name || null,
            sector_name: csvCustomer.sector_name || null,
            product_client_id: csvCustomer.product_client_id || null,
            auto_renewal: this.#convertToBoolean(csvCustomer.auto_renewal)
        };
    }

    #showParseError(): void {
        this.#isCsvParserLoadingBS.next(false);
        const title = this.#translateSrv.instant('TITLES.ERROR_DIALOG');
        const message = this.#translateSrv.instant('CSV.IMPORT.PROCESS_ERROR');
        this.#msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
    }

    #successParse(customersToImport: CustomerToImport[]): void {
        const postCustomersToImport: PostCustomersToImport = {
            entity_id: (this.optionsFormGroup.get(this.entityControlName).value as Entity).id,
            import_products: this.importOptions[ImportOptionsEnum.isProductsImport],
            overwrite_data: this.optionsFormGroup.get(this.overrideControlName).value,
            customers: customersToImport
        };
        this.#isCsvParserLoadingBS.next(false);
        this.#dialogRef.close(postCustomersToImport);
    }

    #parseAndFormatDate(dateStr: string, formatCode: ImportDateFormats): string | null {
        const formats = this.#importDateFormatMap[formatCode];
        const parsed = moment(dateStr, formats, true);
        if (!parsed.isValid()) return null;
        return this.#datePipe.transform(parsed.toDate(), IMPORT_DATE_FORMAT);
    }

    #convertToBoolean(value: boolean | string | null | undefined): boolean {
        return typeof value === 'boolean' ? value :
            typeof value === 'string' ? value.toLowerCase().trim() === 'true' :
                false;
    }

    #normalizeNumberField(value: string): number | null {
        if (!value || value === '') return null;
        const num = Number(value);
        return isNaN(num) ? null : num;
    }
}
