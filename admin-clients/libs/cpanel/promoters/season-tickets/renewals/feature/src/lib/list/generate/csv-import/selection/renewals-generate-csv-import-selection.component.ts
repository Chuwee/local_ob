import { CsvSeasonTicketRenewalGeneration } from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import {
    CSV_FILE_PROCESSOR, CsvFileProcessor, CsvFileTemplate, CsvHeaderMappingField, CsvModule
} from '@admin-clients/shared/common/feature/csv';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, input, OnInit, output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogContent } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import {
    createCsvRenewalGenerationMappingFields
} from '../renewals-generate-csv-import-mapping-data.component';

@Component({
    selector: 'app-renewals-generate-csv-import-selection',
    imports: [MatDialogContent, CsvModule],
    templateUrl: './renewals-generate-csv-import-selection.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        {
            provide: CSV_FILE_PROCESSOR,
            useExisting: RenewalsGenerateCsvImportSelectionComponent
        }
    ]
})
export class RenewalsGenerateCsvImportSelectionComponent implements CsvFileProcessor, OnInit {

    readonly #translateSrv = inject(TranslateService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #csvParser = inject(Papa);

    readonly $selectionFormGroup = input.required<FormGroup>({ alias: 'selectionFormGroup' });
    readonly $selectionControlName = input.required<string>({ alias: 'selectionControlName' });
    readonly isLoading = output<boolean>();

    exampleCsv: CsvFileTemplate;

    ngOnInit(): void {
        this.#setExampleCsv();
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

    #setExampleCsv(): void {
        const exampleCsv = createCsvRenewalGenerationMappingFields();
        this.exampleCsv = {
            name: 'renewals-csv-import-template.csv',
            data: `${this.#getHeadersCsv(exampleCsv)}\r\n${this.#getExampleCsv(exampleCsv)}\r\n`
        };
    }

    #getHeadersCsv(exampleCsv: CsvHeaderMappingField<CsvSeasonTicketRenewalGeneration>[]): string {
        return exampleCsv
            .reduce((acc, value) => acc ?
                acc.concat(';', this.#translateSrv.instant(value.header)) :
                this.#translateSrv.instant(value.header), ''
            );
    }

    #getExampleCsv(exampleCsv: CsvHeaderMappingField<CsvSeasonTicketRenewalGeneration>[]): string {
        return exampleCsv
            .reduce((acc, value) => acc ?
                acc.concat(';', value.example) :
                value.example, ''
            );
    }
}
