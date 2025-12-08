import { ChangeDetectionStrategy, Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import {
    CSV_FILE_PROCESSOR,
    CsvFileProcessor,
    CsvFileTemplate,
    CsvHeaderMappingField,
    CsvModule
} from '@admin-clients/shared/common/feature/csv';
import { MessageDialogService, EphemeralMessageService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { MatDialogContent } from '@angular/material/dialog';
import {
    createCsvPeriodsDatesToImportMappingFields,
    CsvPeriodsDatesToImport
} from '../import-periods-dates-mapping-data';

@Component({
    imports: [
        CsvModule,
        MatDialogContent
    ],
    selector: 'app-import-periods-dates-selection',
    templateUrl: './import-periods-dates-selection.component.html',
    styleUrls: ['./import-periods-dates-selection.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{
            provide: CSV_FILE_PROCESSOR,
            useExisting: ImportPeriodsDatesSelectionComponent
        }]
})
export class ImportPeriodsDatesSelectionComponent implements OnInit, CsvFileProcessor {
    readonly #translate = inject(TranslateService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #csvParser = inject(Papa);

    exampleCsv: CsvFileTemplate;

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    selectionFormGroup: FormGroup;

    @Input()
    selectionControlName: string;

    @Input()
    fields: string[];

    ngOnInit(): void {
        if (this.fields) {
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
            const { data: headers, errors } = this.#csvParser.parse(res, {
                delimiter: ';',
                skipEmptyLines: true,
                preview: 1
            });

            if (errors?.length) {
                errorsCB();
                const title = this.#translate.instant('TITLES.ERROR_DIALOG');
                const message = this.#translate.instant('CSV.IMPORT.SELECT_ERROR');
                this.#msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
            } else {
                valueCB(headers[0]);
                this.#ephemeralSrv.showSuccess({ msgKey: 'CSV.IMPORT.SELECT_CONFIRM' });
            }
            this.isLoading.emit(false);
        });
    }

    private setExampleCsv(): void {
        const exampleCsv = createCsvPeriodsDatesToImportMappingFields();
        const name = 'limit-portal-access-import-template.csv';

        this.exampleCsv = {
            name, data: `${this.getHeadersCsv(exampleCsv)}\r\n${this.getExampleCsv(exampleCsv)}\r\n`
        };
    }

    private getHeadersCsv(exampleCsv: CsvHeaderMappingField<CsvPeriodsDatesToImport>[]): string {
        return exampleCsv
            .reduce((acc, value) => acc ?
                acc.concat(';', this.#translate.instant(value.header)) :
                this.#translate.instant(value.header), ''
            );
    }

    private getExampleCsv(exampleCsv: CsvHeaderMappingField<CsvPeriodsDatesToImport>[]): string {
        return exampleCsv
            .reduce((acc, value) => acc ?
                acc.concat(';', value.example) :
                value.example, ''
            );
    }
}
