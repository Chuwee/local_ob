import {
    CSV_FILE_PROCESSOR, CsvFileProcessor, CsvFileTemplate, CsvModule
} from '@admin-clients/shared/common/feature/csv';
import { MessageDialogService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, Output, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { Subject } from 'rxjs';
import { createCsvEventListingsMappingFields } from '../mappings-list-mapping-data';

@Component({
    imports: [CommonModule, CsvModule],
    selector: 'app-import-events-selection',
    templateUrl: './import-events-selection.component.html',
    styleUrls: ['./import-events-selection.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{
        provide: CSV_FILE_PROCESSOR,
        useExisting: ImportEventsSelectionComponent
    }]
})
export class ImportEventsSelectionComponent implements CsvFileProcessor, OnDestroy {
    readonly #onDestroy = new Subject<void>();
    readonly #translate = inject(TranslateService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #csvParser = inject(Papa);

    readonly exampleCsv: CsvFileTemplate = {
        name: 'mappings-to-clean-template.csv',
        data: this.getHeadersCsv() + '\r\n' + this.getExampleCsvIds()
    };

    readonly selectionControlName = 'selection';

    @Output()
    updateIdsToClean = new EventEmitter<number[]>();

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
            const rows = rowsFromFile?.map(e => e[0]);
            const header = rows[0] || null;
            const idsToClean = rows.slice(1);
            if (file.type !== 'text/csv') {
                errorsCB();
                this.showError('CSV.IMPORT.BULK_FILE_FORMAT_ERROR');
            } else if (errors?.length) {
                errorsCB();
                this.showError('CSV.IMPORT.PROCESS_ERROR', 'TITLES.ERROR_DIALOG_2');
            } else if (!header || header !== this.getHeadersCsv()) {
                errorsCB();
                this.showError('CSV.IMPORT.BULK_HEADER_ERROR');
            } else if (idsToClean.length === 0) {
                errorsCB();
                this.showError('CSV.IMPORT.BULK_NO_IDS_ERROR');
            } else if (!idsToClean.every(id => this.containsOnlyNumbers(id))) {
                errorsCB();
                this.showError('CSV.IMPORT.BULK_IDS_FORMAT_ERROR');
            } else {
                valueCB(header);
                this.updateIdsToClean.emit(idsToClean.map(Number));
            }
        });
    }

    private containsOnlyNumbers(id: number): boolean {
        const regex = /^[0-9]+$/;
        return regex.test(id.toString());
    }

    private getHeadersCsv(): string {
        return createCsvEventListingsMappingFields()
            .reduce((acc, value) => acc ?
                acc.concat(';', this.#translate.instant(value.header)) :
                this.#translate.instant(value.header), ''
            );
    }

    private getExampleCsvIds(): string {
        return createCsvEventListingsMappingFields()
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
