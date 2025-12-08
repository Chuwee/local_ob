import {
    CSV_FILE_PROCESSOR, CsvFileProcessor, CsvFileTemplate, CsvModule
} from '@admin-clients/shared/common/feature/csv';
import { MessageDialogService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { createCsvBulkManageBlacklistListingsMappingFields } from '../listings-list-mapping-data';

@Component({
    imports: [CommonModule, CsvModule],
    selector: 'app-import-selection',
    templateUrl: './import-selection.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{
        provide: CSV_FILE_PROCESSOR,
        useExisting: ImportSelectionComponent
    }]
})
export class ImportSelectionComponent implements CsvFileProcessor {
    readonly #translate = inject(TranslateService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #csvParser = inject(Papa);

    readonly exampleCsv: CsvFileTemplate = {
        name: 'manage-blacklist-template.csv',
        data: this.getHeadersCsv() + '\r\n' + this.getExampleCsvIds()
    };

    readonly selectionControlName = 'selection';

    @Output()
    updateCodesToManage = new EventEmitter<string[]>();

    @Input()
    formGroup: FormGroup;

    @Input()
    selectionFormGroup: FormGroup;

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
            const rows = rowsFromFile?.map(e => e[0]);
            const header = rows[0] || null;
            const codesToManage = rows.slice(1);
            if (file.type !== 'text/csv') {
                errorsCB();
                this.showError('CSV.IMPORT.BULK_FILE_FORMAT_ERROR');
            } else if (errors?.length) {
                errorsCB();
                this.showError('CSV.IMPORT.PROCESS_ERROR', 'TITLES.ERROR_DIALOG_2');
            } else if (!header || header !== this.getHeadersCsv()) {
                errorsCB();
                this.showError('CSV.IMPORT.BULK_HEADER_ERROR');
            } else if (codesToManage.length === 0) {
                errorsCB();
                this.showError('CSV.IMPORT.BULK_CODES_ERROR');
            } else if (!codesToManage.every(code => this.containsOnlyNumbersOrLetters(code))) {
                errorsCB();
                this.showError('CSV.IMPORT.BULK_CODES_FORMAT_ERROR');
            } else {
                valueCB(header);
                this.updateCodesToManage.emit(codesToManage);
            }
        });
    }

    private containsOnlyNumbersOrLetters(texto: string): boolean {
        const regex = /^[A-Z0-9]+$/;
        return regex.test(texto);
    }

    private getHeadersCsv(): string {
        return createCsvBulkManageBlacklistListingsMappingFields()
            .reduce((acc, value) => acc ?
                acc.concat(';', this.#translate.instant(value.header)) :
                this.#translate.instant(value.header), ''
            );
    }

    private getExampleCsvIds(): string {
        return createCsvBulkManageBlacklistListingsMappingFields()
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
