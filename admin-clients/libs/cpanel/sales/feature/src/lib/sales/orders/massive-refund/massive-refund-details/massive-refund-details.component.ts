import { MassiveRefundSelectionType, OrdersService } from '@admin-clients/cpanel-sales-data-access';
import { CsvFileTemplate, CsvFileProcessor, CSV_FILE_PROCESSOR } from '@admin-clients/shared/common/feature/csv';
import { MessageDialogService, EphemeralMessageService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, FilterOption } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { map, Observable } from 'rxjs';

@Component({
    selector: 'app-massive-refund-details',
    templateUrl: './massive-refund-details.component.html',
    styleUrls: ['./massive-refund-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        {
            provide: CSV_FILE_PROCESSOR,
            useExisting: MassiveRefundDetailsComponent
        }
    ],
    standalone: false
})
export class MassiveRefundDetailsComponent implements OnInit, CsvFileProcessor {
    private readonly PAGE_LIMIT = 100;

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    refundDetailsForm: UntypedFormGroup;

    exampleCsv: CsvFileTemplate = {
        name: 'massive-refund-orders-template.csv',
        data: `${this._translate.instant('ORDERS.MASSIVE_REFUND.DIALOG.ORDERS_LIST.ORDER_CODE')}\r\n123\r\n`
    };

    entities$: Observable<FilterOption[]>;
    moreEntitiesAvailable$: Observable<boolean>;
    events$: Observable<FilterOption[]>;
    moreEventsAvailable$: Observable<boolean>;
    sessions$: Observable<FilterOption[]>;
    moreSessionsAvailable$: Observable<boolean>;
    channels$: Observable<FilterOption[]>;
    moreChannelsAvailable$: Observable<boolean>;
    massiveRefundSelectionType = MassiveRefundSelectionType;
    massiveRefundSelectionTypeList = Object.values(this.massiveRefundSelectionType);
    selectionControlName = 'barcodesCsvSelection';
    readonly dateTimeFormats = DateTimeFormats;

    constructor(
        private _translate: TranslateService,
        private _ordersService: OrdersService,
        private _ephemeralSrv: EphemeralMessageService,
        private _msgDialogSrv: MessageDialogService,
        private _csvParser: Papa
    ) { }

    ngOnInit(): void {
        this.entities$ = this._ordersService.getFilterEventEntityListData$();
        this.moreEntitiesAvailable$ = this._ordersService.getFilterEventEntityListMetadata$().pipe(map(md => !!md?.next_cursor));
        this.events$ = this._ordersService.getFilterEventListData$();
        this.moreEventsAvailable$ = this._ordersService.getFilterEventListMetadata$().pipe(map(md => !!md?.next_cursor));
        this.sessions$ = this._ordersService.getFilterSessionListData$();
        this.moreSessionsAvailable$ = this._ordersService.getFilterSessionListMetadata$().pipe(map(md => !!md?.next_cursor));
        this.channels$ = this._ordersService.getFilterChannelListData$();
        this.moreChannelsAvailable$ = this._ordersService.getFilterChannelListMetadata$().pipe(map(md => !!md?.next_cursor));
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
            } else {
                valueCB(headers[0]);
                this._ephemeralSrv.showSuccess({ msgKey: 'CSV.IMPORT.SELECT_CONFIRM' });
            }
            this.isLoading.emit(false);
        });
    }

    loadEntities(q: string, next = false): void {
        this._ordersService.loadFilterEventEntityList({ q, limit: this.PAGE_LIMIT }, next);
    }

    loadEvents(q: string, next = false): void {
        const entitySelected = this.refundDetailsForm.get('entity').value;
        if (entitySelected?.id) {
            this._ordersService.loadFilterEventList({ q, limit: this.PAGE_LIMIT, event_entity_id: entitySelected.id }, next);
        }
    }

    loadSessions(q: string, next = false): void {
        const entitySelected = this.refundDetailsForm.get('entity').value;
        const eventSelected = this.refundDetailsForm.get('event').value;
        if (eventSelected?.id) {
            this._ordersService.loadFilterSessionList({
                q,
                limit: this.PAGE_LIMIT,
                event_id: eventSelected.id,
                event_entity_id: entitySelected.id
            }, next);
        }
    }

    loadChannels(q: string, next = false): void {
        const sessionSelected = this.refundDetailsForm.get('session').value;
        const entitySelected = this.refundDetailsForm.get('entity').value;
        const eventSelected = this.refundDetailsForm.get('event').value;
        if (sessionSelected?.id) {
            this._ordersService.loadFilterChannelList({
                q,
                limit: this.PAGE_LIMIT,
                event_entity_id: entitySelected.id,
                event_id: eventSelected.id
            }, next);
        }
    }

    private showError(msgKey: string): void {
        const title = this._translate.instant('TITLES.ERROR_DIALOG');
        const message = this._translate.instant(msgKey);
        this._msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
    }

}
