import {
    CSV_FILE_PROCESSOR, CsvFileProcessor, CsvFileTemplate, CsvModule
} from '@admin-clients/shared/common/feature/csv';
import { MessageDialogService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { SupplierName } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, Output, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { Subject } from 'rxjs';
import { MappingToCreate } from '../../../models/mapping.model';
import { createCsvBulkCreateMappingsFields } from '../mappings-bulk-create-data';

@Component({
    imports: [CommonModule, CsvModule],
    selector: 'app-bulk-create-mapping-selection',
    templateUrl: './bulk-create-mapping-selection.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{
        provide: CSV_FILE_PROCESSOR,
        useExisting: BulkCreateMappingSelectionComponent
    }]
})
export class BulkCreateMappingSelectionComponent implements CsvFileProcessor, OnDestroy {
    readonly #onDestroy = new Subject<void>();
    readonly #translate = inject(TranslateService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #csvParser = inject(Papa);

    readonly exampleCsv: CsvFileTemplate = {
        name: 'mappings-to-create-template.csv',
        data: this.getHeadersCsv() + '\r\n' + this.getExampleCsvMappings()
    };

    readonly selectionControlName = 'selection';

    @Output()
    updateMappingsToCreate = new EventEmitter<MappingToCreate[]>();

    @Input()
    formGroup: FormGroup;

    @Input()
    selectionFormGroup: FormGroup;

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    processFile(
        file: File,
        valueCB: (processedFile: unknown) => void,
        errorsCB?: () => void
    ): void {
        file.text().then(res => {
            const { data: rowsFromFile, errors } = this.#csvParser.parse(res, {
                delimiter: ';',
                skipEmptyLines: true
            });
            const headers = this.getHeadersCsv().split(';');
            const headersFromFile = rowsFromFile[0];
            const mappingsToCreate = rowsFromFile?.slice(1)?.map(mappingArray => {
                const [shi_id, supplier_id, supplier, favorite] = mappingArray;
                return {
                    shi_id: Number(shi_id),
                    supplier_id,
                    supplier,
                    favorite
                } as MappingToCreate;
            });
            if (file.type !== 'text/csv') {
                errorsCB();
                this.showError('CSV.IMPORT.BULK_FILE_FORMAT_ERROR');
            } else if (errors?.length) {
                errorsCB();
                this.showError('CSV.IMPORT.PROCESS_ERROR', 'TITLES.ERROR_DIALOG_2');
            } else if (!headersFromFile?.every(column => headers.includes(column))) {
                errorsCB();
                this.showError('CSV.IMPORT.BULK_HEADER_ERROR');
            } else if (mappingsToCreate.length === 0) {
                errorsCB();
                this.showError('CSV.IMPORT.BULK_NO_MAPPINGS_ERROR');
            } else if (!mappingsToCreate.every(
                mapping =>
                    this.containsOnlyNumbers(mapping.shi_id) &&
                    this.isAlphaNumeric(mapping.supplier_id) &&
                    this.isSupplierName(mapping.supplier) &&
                    this.isBooleanString(mapping.favorite)
            )) {
                errorsCB();
                this.showError('CSV.IMPORT.BULK_CREATE_MAPPINGS_FORMAT_ERROR');
            } else {
                valueCB(headersFromFile);
                this.updateMappingsToCreate.emit(mappingsToCreate);
            }
        });
    }

    private containsOnlyNumbers(texto: string | number): boolean {
        const regex = /^[0-9]+$/;
        return regex.test(texto.toString());
    }

    private isAlphaNumeric(texto: string | number): boolean {
        const regex = /^[A-Za-z0-9]+$/;
        return regex.test(texto.toString());
    }

    private isBooleanString(texto: string): boolean {
        const regex = /^(true|false)$/i;
        return regex.test(texto);
    }

    private isSupplierName(value: string): value is SupplierName {
        return Object.values(SupplierName).includes(value as SupplierName);
    }

    private getHeadersCsv(): string {
        return createCsvBulkCreateMappingsFields()
            .reduce((acc, value) => acc ?
                acc.concat(';', this.#translate.instant(value.header)) :
                this.#translate.instant(value.header), ''
            );
    }

    private getExampleCsvMappings(): string {
        return createCsvBulkCreateMappingsFields()
            .reduce((acc, value) => acc ?
                acc.concat(';', value.example) :
                value.example, ''
            );
    }

    private showError(msgKey: string, header?: string): void {
        const title = this.#translate.instant(header || 'TITLES.ERROR_DIALOG');
        const message = this.#translate.instant(msgKey);
        this.#msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
    }
}
