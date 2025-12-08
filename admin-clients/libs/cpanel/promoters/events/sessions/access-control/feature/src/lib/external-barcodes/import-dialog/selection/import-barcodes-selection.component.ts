import { CsvFileProcessor, CSV_FILE_PROCESSOR, CsvFileTemplate, CsvHeader, CsvModule } from '@admin-clients/shared/common/feature/csv';
import { MessageDialogService, EphemeralMessageService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component, Input, Output, EventEmitter } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';

@Component({
    selector: 'app-import-barcodes-selection',
    templateUrl: './import-barcodes-selection.component.html',
    styleUrls: ['./import-barcodes-selection.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        {
            provide: CSV_FILE_PROCESSOR,
            useExisting: ImportBarcodesSelectionComponent
        }
    ],
    imports: [
        CsvModule, MaterialModule
    ]
})
export class ImportBarcodesSelectionComponent implements CsvFileProcessor {
    exampleCsv: CsvFileTemplate;

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    selectionFormGroup: UntypedFormGroup;

    @Input()
    selectionControlName: string;

    constructor(
        private _translate: TranslateService,
        private _msgDialogSrv: MessageDialogService,
        private _csvParser: Papa,
        private _ephemeralSrv: EphemeralMessageService
    ) {
        this.setExampleCsv();
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
                this.showError('CSV.IMPORT.SELECT_ERROR');
            } else if (headers.some(arr => arr.includes('')) || headers.some(arr => arr.some((header, i) => i !== arr.indexOf(header)))) {
                errorsCB();
                this.showError('CSV.IMPORT.HEADERS_ERROR');
            } else {
                valueCB(headers[0]);
                this._ephemeralSrv.showSuccess({ msgKey: 'CSV.IMPORT.SELECT_CONFIRM' });
            }
            this.isLoading.emit(false);
        });
    }

    private setExampleCsv(): void {
        const exampleHeadersCsv: CsvHeader[] = [
            { header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.BARCODE' },
            { header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.LOCATOR' },
            { header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ROW' },
            { header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.SEAT' },
            { header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ACCESS_ID' },
            { header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ATTENDANT_NAME' },
            { header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ATTENDANT_SURNAME' },
            { header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ATTENDANT_ID_NUMBER' },
            { header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ATTENDANT_MAIL' }
        ];
        this.exampleCsv = {
            name: 'barcodes-import-template.csv',
            data: `${this.getHeadersExampleCsv(exampleHeadersCsv)}\r\n`
        };
    }

    private getHeadersExampleCsv(exampleHeadersCsv: CsvHeader[]): string {
        return exampleHeadersCsv
            .reduce((acc, value) => acc ?
                acc.concat(';', this._translate.instant(value.header)) :
                this._translate.instant(value.header), ''
            );
    }

    private showError(msgKey: string): void {
        const title = this._translate.instant('TITLES.ERROR_DIALOG');
        const message = this._translate.instant(msgKey);
        this._msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
    }
}
