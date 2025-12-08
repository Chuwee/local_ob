import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { VoucherGroupValidationMethod } from '@admin-clients/cpanel-vouchers-data-access';
import { CSV_FILE_PROCESSOR, CsvFileProcessor, CsvFileTemplate, CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';
import { MessageDialogService, EphemeralMessageService, DialogSize } from '@admin-clients/shared/common/ui/components';
import {
    createCsvVoucherCodesMappingFields,
    createCsvVoucherCodesWithPinMappingFields,
    CsvVoucherCodes
} from '../import-voucher-codes-mapping-data';

@Component({
    selector: 'app-import-voucher-codes-selection',
    templateUrl: './import-voucher-codes-selection.component.html',
    styleUrls: ['./import-voucher-codes-selection.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{
            provide: CSV_FILE_PROCESSOR,
            useExisting: ImportVoucherCodesSelectionComponent
        }],
    standalone: false
})
export class ImportVoucherCodesSelectionComponent implements CsvFileProcessor {
    exampleCsv: CsvFileTemplate;

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    selectionFormGroup: UntypedFormGroup;

    @Input()
    selectionControlName: string;

    @Input()
    set validationMethod(validationMethod: VoucherGroupValidationMethod) {
        this.setExampleCsv(validationMethod);
    }

    constructor(
        private _translate: TranslateService,
        private _ephemeralSrv: EphemeralMessageService,
        private _msgDialogSrv: MessageDialogService,
        private _csvParser: Papa
    ) { }

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
            } else if (headers[0].includes('') || headers[0].some((header, i) => i !== headers[0].indexOf(header))) {
                errorsCB();
                this.showError('CSV.IMPORT.HEADERS_ERROR');
            } else {
                valueCB(headers[0]);
                this._ephemeralSrv.showSuccess({ msgKey: 'CSV.IMPORT.SELECT_CONFIRM' });
            }
            this.isLoading.emit(false);
        });
    }

    private setExampleCsv(validationMethod: VoucherGroupValidationMethod): void {
        let exampleCsv: CsvHeaderMappingField<CsvVoucherCodes>[];
        let name: string;

        if (validationMethod === VoucherGroupValidationMethod.code) {
            exampleCsv = createCsvVoucherCodesMappingFields();
            name = 'voucher-codes-import-template.csv';
        } else {
            exampleCsv = createCsvVoucherCodesWithPinMappingFields();
            name = 'voucher-codes-pin-import-template.csv';
        }

        this.exampleCsv = {
            name, data: `${this.getHeadersCsv(exampleCsv)}\r\n${this.getExampleCsv(exampleCsv)}\r\n`
        };
    }

    private getHeadersCsv(exampleCsv: CsvHeaderMappingField<CsvVoucherCodes>[]): string {
        return exampleCsv
            .reduce((acc, value) => acc ?
                acc.concat(';', this._translate.instant(value.header)) :
                this._translate.instant(value.header), ''
            );
    }

    private getExampleCsv(exampleCsv: CsvHeaderMappingField<CsvVoucherCodes>[]): string {
        return exampleCsv
            .reduce((acc, value) => acc ?
                acc.concat(';', value.example) :
                value.example, ''
            );
    }

    private showError(msgKey: string): void {
        const title = this._translate.instant('TITLES.ERROR_DIALOG');
        const message = this._translate.instant(msgKey);
        this._msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
    }
}
