import {
    CSV_FILE_PROCESSOR, CsvFileProcessor, CsvFileTemplate, CsvHeaderMappingField, CsvModule
} from '@admin-clients/shared/common/feature/csv';
import { MessageDialogService, EphemeralMessageService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatDialogContent } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { createCsvLiteralsToImportMappingFields, CsvLiteralsToImport } from '../import-literals-mapping-data';

@Component({
    selector: 'app-import-literals-selection',
    imports: [
        MatDialogContent, CsvModule
    ],
    templateUrl: './import-literals-selection.component.html',
    styleUrls: ['./import-literals-selection.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{
            provide: CSV_FILE_PROCESSOR,
            useExisting: ImportLiteralsSelectionComponent
        }]
})
export class ImportLiteralsSelectionComponent implements OnInit, CsvFileProcessor {
    private readonly _translate = inject(TranslateService);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _csvParser = inject(Papa);

    exampleCsv: CsvFileTemplate;

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    selectionFormGroup: UntypedFormGroup;

    @Input()
    selectionControlName: string;

    @Input()
    languages: string[];

    ngOnInit(): void {
        if (this.languages) {
            this.setExampleCsv();
        }
    }

    processFile(
        file: File,
        valueCB: (processedFile: unknown) => void,
        errorsCB?: () => void
    ): void {
        this.isLoading.emit(true);
        file.text().then(res => {
            const { data: headers, errors } = this._csvParser.parse(res, {
                delimiter: ';',
                skipEmptyLines: true,
                preview: 1
            });

            if (errors?.length) {
                errorsCB();
                const title = this._translate.instant('TITLES.ERROR_DIALOG');
                const message = this._translate.instant('CSV.IMPORT.SELECT_ERROR');
                this._msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
            } else {
                valueCB(headers[0]);
                this._ephemeralSrv.showSuccess({ msgKey: 'CSV.IMPORT.SELECT_CONFIRM' });
            }
            this.isLoading.emit(false);
        });
    }

    private setExampleCsv(): void {
        const exampleCsv = createCsvLiteralsToImportMappingFields(this.languages);
        const name = 'literals-import-template.csv';

        this.exampleCsv = {
            name, data: `${this.getHeadersCsv(exampleCsv)}\r\n${this.getExampleCsv(exampleCsv)}\r\n`
        };
    }

    private getHeadersCsv(exampleCsv: CsvHeaderMappingField<CsvLiteralsToImport>[]): string {
        return exampleCsv
            .reduce((acc, value) => acc ?
                acc.concat(';', this._translate.instant(value.header)) :
                this._translate.instant(value.header), ''
            );
    }

    private getExampleCsv(exampleCsv: CsvHeaderMappingField<CsvLiteralsToImport>[]): string {
        return exampleCsv
            .reduce((acc, value) => acc ?
                acc.concat(';', value.example) :
                value.example, ''
            );
    }
}
