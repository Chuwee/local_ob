import { CustomerFormField } from '@admin-clients/cpanel-viewers-customers-data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import {
    CSV_FILE_PROCESSOR, CsvFileProcessor, CsvFileTemplate, CsvHeaderMappingField, CsvModule
} from '@admin-clients/shared/common/feature/csv';
import { MessageDialogService, DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { DownloadFileDirective } from '@admin-clients/shared/utility/directives';
import { ChangeDetectionStrategy, Component, OnInit, inject, output, input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDialogContent } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import {
    createCsvCustomerMappingFields, createCsvCustomerProductsMappingFields,
    createCsvCustomerProductsOnlyNotNumberedMappingFields, createCsvCustomerProductsOnlyNumberedMappingFields, CsvCustomer
} from '../import-customer-mapping-data';
import { ImportOptionsEnum } from '../models/import-options.enum';
import { ImportOptions } from '../models/import-options.model';
import { ProductTypeImport } from '../models/product-type-import.enum';

const CSV_IMPORT_INFO_LINK = 'https://docs.google.com/document/d/1cwh4YJwA0cSuUFYMIQCxz2q6V43ZivFqfw5CNc-yMfM';

@Component({
    selector: 'app-import-customer-selection',
    imports: [MatDialogContent, CsvModule, TranslatePipe, MatIcon, DownloadFileDirective, MatButton],
    templateUrl: './import-customer-selection.component.html',
    styleUrls: ['./import-customer-selection.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{ provide: CSV_FILE_PROCESSOR, useExisting: ImportCustomerSelectionComponent }]
})
export class ImportCustomerSelectionComponent implements CsvFileProcessor, OnInit {
    readonly #translateSrv = inject(TranslateService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #csvParser = inject(Papa);

    readonly $selectionFormGroup = input.required<UntypedFormGroup>({ alias: 'selectionFormGroup' });
    readonly $selectionControlName = input.required<string>({ alias: 'selectionControlName' });
    readonly $importOptions = input.required<ImportOptions>({ alias: 'importOptions' });
    readonly $hasEntityCustomerTypes = input.required<boolean>({ alias: 'hasEntityCustomerTypes' });
    readonly $entity = input.required<Entity>({ alias: 'entity' });
    readonly $customerFields = input.required<CustomerFormField[]>({ alias: 'customerFields' });
    readonly isLoading = output<boolean>();

    readonly csvImportInfoLink = CSV_IMPORT_INFO_LINK;
    exampleCsv: CsvFileTemplate;

    ngOnInit(): void {
        this.#setExampleCsv(this.$importOptions());
    }

    processFile(file: File, valueCB: (processedFile: unknown) => void, errorsCB?: () => void): void {
        this.isLoading.emit(true);
        file.text().then(res => {
            const { data: headers, errors } = this.#csvParser.parse(res, {
                delimiter: ';',
                skipEmptyLines: true,
                preview: 1
            });

            if (errors?.length) {
                errorsCB();
                const title = this.#translateSrv.instant('TITLES.ERROR_DIALOG');
                const message = this.#translateSrv.instant('CSV.IMPORT.SELECT_ERROR');
                this.#msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
            } else {
                valueCB(headers[0]);
                this.#ephemeralMessageService.showSuccess({ msgKey: 'CSV.IMPORT.SELECT_CONFIRM' });
            }
            this.isLoading.emit(false);
        });
    }

    #setExampleCsv(importOptions: ImportOptions): void {
        const exampleCsv = this.#getCsvCustomerHeaderMappingField(importOptions);
        this.exampleCsv = {
            name: 'customers-import-template.csv',
            data: `${this.#getHeadersCsv(exampleCsv)}\r\n${this.#getExampleCsv(exampleCsv)}\r\n`
        };
    }

    #getCsvCustomerHeaderMappingField(importOptions: ImportOptions): CsvHeaderMappingField<CsvCustomer>[] {
        const vendorConfig = { ...this.$entity().settings?.external_integration?.auth_vendor };
        vendorConfig.enabled = vendorConfig?.enabled && !!importOptions.isProductsWithVendor && !!importOptions.productsVendor;
        let exampleCsv = createCsvCustomerMappingFields(this.$hasEntityCustomerTypes(), this.$customerFields(),
            this.$importOptions().isProductsImport, vendorConfig);
        exampleCsv = this.#getCsvProductHeaderMappingsFields(importOptions, exampleCsv);
        return exampleCsv;
    }

    #getCsvProductHeaderMappingsFields(
        importOptions: ImportOptions,
        exampleCsv: CsvHeaderMappingField<CsvCustomer>[]
    ): CsvHeaderMappingField<CsvCustomer>[] {
        if (!importOptions[ImportOptionsEnum.isProductsImport]) {
            return exampleCsv;
        }

        let result: CsvHeaderMappingField<CsvCustomer>[];

        if (importOptions[ImportOptionsEnum.productsType] === ProductTypeImport.numbered) {
            result = exampleCsv.concat(createCsvCustomerProductsOnlyNumberedMappingFields());
        } else if (importOptions[ImportOptionsEnum.productsType] === ProductTypeImport.notNumbered) {
            result = exampleCsv.concat(createCsvCustomerProductsOnlyNotNumberedMappingFields());
        } else if (importOptions[ImportOptionsEnum.productsType] === ProductTypeImport.numberedAndNotNumbered) {
            result = exampleCsv.concat(createCsvCustomerProductsMappingFields());
        }
        return result;
    }

    #getHeadersCsv(exampleCsv: CsvHeaderMappingField<CsvCustomer>[]): string {
        return exampleCsv
            .reduce((acc, value) => {
                const translatedHeader = this.#translateSrv.instant(value.header);
                const headerWithRequired = value.required ? `${translatedHeader}*` : translatedHeader;
                return acc ? acc.concat(';', headerWithRequired) : headerWithRequired;
            }, '');
    }

    #getExampleCsv(exampleCsv: CsvHeaderMappingField<CsvCustomer>[]): string {
        return exampleCsv
            .reduce((acc, value) => acc ?
                acc.concat(';', value.example) :
                value.example, ''
            );
    }
}
