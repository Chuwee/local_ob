import { CollectiveValidationMethod } from '@admin-clients/cpanel/collectives/data-access';
import { CSV_FILE_PROCESSOR, CsvFileProcessor, CsvFileTemplate, CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';
import { MessageDialogService, EphemeralMessageService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import {
    createCsvCollectiveCodesMappingFields, createCsvCollectiveCodesUserMappingFields,
    createCsvCollectiveCodesUserPassMappingFields,
    CsvCollectiveCodes
} from '../import-collective-codes-mapping-data';

@Component({
    selector: 'app-import-collective-codes-selection',
    templateUrl: './import-collective-codes-selection.component.html',
    styleUrls: ['./import-collective-codes-selection.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{
            provide: CSV_FILE_PROCESSOR,
            useExisting: ImportCollectiveCodesSelectionComponent
        }],
    standalone: false
})
export class ImportCollectiveCodesSelectionComponent implements CsvFileProcessor {
    exampleCsv: CsvFileTemplate;

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    selectionFormGroup: UntypedFormGroup;

    @Input()
    selectionControlName: string;

    @Input()
    set validationMethod(validationMethod: CollectiveValidationMethod) {
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

    private setExampleCsv(validationMethod: CollectiveValidationMethod): void {
        let exampleCsv: CsvHeaderMappingField<CsvCollectiveCodes>[];
        let name: string;

        if (validationMethod === CollectiveValidationMethod.userPassword) {
            exampleCsv = createCsvCollectiveCodesUserPassMappingFields();
            name = 'collective-codes-user-pass-import-template.csv';
        } else if (validationMethod === CollectiveValidationMethod.user) {
            exampleCsv = createCsvCollectiveCodesUserMappingFields();
            name = 'collective-codes-user-import-template.csv';
        } else {
            exampleCsv = createCsvCollectiveCodesMappingFields();
            name = 'collective-codes-import-template.csv';
        }

        this.exampleCsv = {
            name, data: `${this.getHeadersCsv(exampleCsv)}\r\n${this.getExampleCsv(exampleCsv)}\r\n`
        };
    }

    private getHeadersCsv(exampleCsv: CsvHeaderMappingField<CsvCollectiveCodes>[]): string {
        return exampleCsv
            .reduce((acc, value) => acc ?
                acc.concat(';', this._translate.instant(value.header)) :
                this._translate.instant(value.header), ''
            );
    }

    private getExampleCsv(exampleCsv: CsvHeaderMappingField<CsvCollectiveCodes>[]): string {
        return exampleCsv
            .reduce((acc, value) => acc ?
                acc.concat(';', value.example) :
                value.example, ''
            );
    }
}
