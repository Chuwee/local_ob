import { AutomaticSale } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    CsvHeaderMappingField, CSV_FILE_PROCESSOR, CsvFileProcessor, CsvFileTemplate, CsvHeader, CsvModule
} from '@admin-clients/shared/common/feature/csv';
import { MessageDialogService, EphemeralMessageService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, Input, OnChanges, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { createCsvAutomaticSalesToImportMappingFields } from '../import-automatic-sale-mapping-data';

@Component({
    selector: 'app-import-automatic-sale',
    templateUrl: './import-automatic-sale.component.html',
    styleUrls: ['./import-automatic-sale.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [CsvModule],
    providers: [{
            provide: CSV_FILE_PROCESSOR,
            useExisting: ImportAutomaticSaleComponent
        }]
})
export class ImportAutomaticSaleComponent implements OnInit, OnChanges, CsvFileProcessor {
    private readonly _translate = inject(TranslateService);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _csvParser = inject(Papa);

    exampleCsv: CsvFileTemplate;

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    config: {
        sort?: boolean;
        use_seat_mappings?: boolean;
        use_ob_ids_for_seat_mappings?: boolean;
        default_purchase_language?: boolean;
        skip_add_attendant?: boolean;
        allow_break_adjacent_seats?: boolean;
        use_locators?: boolean;
        force_multi_ticket?: boolean;
    };

    @Input()
    automaticSaleFormGroup: UntypedFormGroup;

    @Input()
    automaticSaleControlName: string;

    @Input()
    mandatoryFields: [];

    ngOnInit(): void {
        this.setExampleCsv();
    }

    ngOnChanges(): void {
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
                delimiter: ',',
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

    #getHeaders(): CsvHeaderMappingField<AutomaticSale>[] {
        return createCsvAutomaticSalesToImportMappingFields(this.config, this.mandatoryFields);
    }

    private setExampleCsv(): void {
        this.exampleCsv = {
            name: 'automatic-sale-import-template.csv',
            data: `${this.getHeadersExampleCsv(this.#getHeaders())}\r\n`
        };
    }

    private getHeadersExampleCsv(exampleHeadersCsv: CsvHeader[]): string {
        return exampleHeadersCsv
            .reduce((acc, value) => acc ?
                acc.concat(',', this._translate.instant(value.header)) :
                value.header, ''
            );
    }
}
