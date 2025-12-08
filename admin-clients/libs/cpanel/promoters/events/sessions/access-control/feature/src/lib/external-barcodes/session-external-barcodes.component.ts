import { Metadata } from '@OneboxTM/utils-state';
import {
    BarcodeImportResultDialog,
    EventSessionsService, ExternalBarcode, ExternalBarcodeStatus, GetExternalBarcodesRequest, PostBarcodesToImport, Session,
    WsBarcodesImportResult
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, ExportDialogComponent, MessageDialogService, ObMatDialogConfig, SearchTableChangeEvent,
    SearchTableComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { Topic, WebsocketsService, WsMsgStatus } from '@admin-clients/shared/core/data-access';
import { DateTimeFormats, ExportFormat } from '@admin-clients/shared/data-access/models';
import { ObfuscateStringPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';
import {
    combineLatest, combineLatestWith, delay, filter, first, map, mapTo, mergeMap, Observable, skip, startWith, Subject, switchMap,
    takeUntil, tap
} from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';
import { exportDataExternalBarcodes } from './external-barcodes-export-data';
import { ImportBarcodesDialogComponent } from './import-dialog/import-barcodes-dialog.component';
import { ImportBarcodesResultDialogComponent } from './import-result-dialog/import-barcodes-result-dialog.component';

const PAGE_SIZE = 4;

@Component({
    selector: 'app-session-external-barcodes',
    templateUrl: './session-external-barcodes.component.html',
    styleUrls: ['./session-external-barcodes.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, MaterialModule, CommonModule, SearchTableComponent, TranslatePipe,
        ObfuscateStringPipe
    ]
})
export class SessionExternalBarcodesComponent implements OnInit, OnDestroy {
    private readonly _tableSrv = inject(TableColConfigService);

    private readonly _onDestroy = new Subject<void>();
    private readonly _imports = new Map<Session['id'], number>();
    private readonly _importsUpdated = new Subject<void>();
    private _filters: GetExternalBarcodesRequest = { limit: PAGE_SIZE };

    private readonly _importsUpdated$ = this._importsUpdated.pipe(startWith(null));

    private _session: Session;

    @ViewChild(SearchTableComponent, { static: true }) private _searchTable: SearchTableComponent<unknown>;

    readonly columns = ['barcode', 'row', 'seat', 'locator', 'access', 'status'];
    readonly pageSize = PAGE_SIZE;

    barcodes$: Observable<ExternalBarcode[]>;
    metadata$: Observable<Metadata>;
    isImportLoading$: Observable<boolean>;
    isExportOrImportLoading$: Observable<boolean>;
    reqInProgress$: Observable<boolean>;
    totalImported$: Observable<number>;
    hasImportedBarcodes$: Observable<boolean>;

    constructor(
        private _ws: WebsocketsService,
        private _sessionsSrv: EventSessionsService,
        private _dialog: MatDialog,
        private _ephemeralMsg: EphemeralMessageService,
        private _message: MessageDialogService
    ) { }

    ngOnInit(): void {
        this.isImportLoading$ = combineLatest([
            this._importsUpdated$.pipe(mapTo(this._imports)),
            this._sessionsSrv.session.get$(),
            this._sessionsSrv.isBarcodesImportInProgress$()
        ]).pipe(map(([imports, session, inProgress]) => imports.has(session.id) || !!inProgress));

        this.isExportOrImportLoading$ = booleanOrMerge([
            this._sessionsSrv.isExportWhitelistLoading$(),
            this.isImportLoading$
        ]);

        this.reqInProgress$ = this._sessionsSrv.isUploadedExternalBarcodesInProgress$();

        this._sessionsSrv.session.get$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(session => this._session = session);

        this.barcodes$ = this._sessionsSrv.getUploadedExternalBarcodes$()
            .pipe(
                filter(Boolean),
                map(data => data.data)
            );

        this.metadata$ = this._sessionsSrv.getUploadedExternalBarcodes$()
            .pipe(
                filter(Boolean),
                map(data => data.metadata)
            );

        this.totalImported$ = combineLatest([
            this.metadata$,
            this._importsUpdated$.pipe(delay(1000)) // used as a trigger
        ])
            .pipe(map(([md]) => md?.total ?? 0));

        this.hasImportedBarcodes$ = this.metadata$
            .pipe(
                filter(Boolean),
                map(md => !!this._filters.barcode || md?.total > 0)
            );

        this._sessionsSrv.getBarcodesImportId$().pipe(
            combineLatestWith(this._sessionsSrv.session.get$()),
            filter(data => data?.every(val => !!val)),
            // add importId to imports map
            filter(([, session]) => !this._imports.has(session.id)),
            tap(([id, session]) => this._imports.set(session.id, id)),
            tap(() => this._sessionsSrv.clearBarcodesImportId()),
            // subscribe to Web Socket
            mergeMap(([id, session]) =>
                this._ws.getMessages$<WsBarcodesImportResult>(Topic.barcode, id)
                    .pipe(map(result => ({ result, id, session })))),
            // if we have a result response from WS with same id as this pipe has
            filter(data => data?.result?.data.importProcess === data.id),
            takeUntil(this._onDestroy)
        ).subscribe(data => {
            this._imports.delete(data.session.id);
            this._importsUpdated.next();
            this._ws.unsubscribeMessages(Topic.barcode, data.id);

            switch (data.result.status) {
                case WsMsgStatus.done:
                    this.showImportDone(data);
                    break;
                case WsMsgStatus.error:
                    this.showImportError(data);
                    break;
            }
        });
        // reloads barcodes list when selected session changes
        this._sessionsSrv.session.get$()
            .pipe(
                filter(Boolean), map(session => session.id), skip(1), distinctUntilChanged(),
                takeUntil(this._onDestroy)
            )
            .subscribe(() => this._searchTable.clearFilter());
    }

    ngOnDestroy(): void {
        this._sessionsSrv.clearUploadedExternalBarcodes();
        this._onDestroy.next();
        this._onDestroy.complete();
        this.unsubscribeWebSocket();
    }

    barcodeValidated(barcode: ExternalBarcode): boolean {
        return barcode.status === ExternalBarcodeStatus.validated;
    }

    openImportBarcodesDialog(): void {
        this._dialog.open(ImportBarcodesDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .pipe(first(barcodes => !!barcodes))
            .subscribe((barcodes: PostBarcodesToImport) =>
                this._sessionsSrv.importBarcodes(this._session.event.id, this._session.id, barcodes)
            );
    }

    openExportExternalBarcodesDialog(): void {
        const config = new ObMatDialogConfig({
            exportData: exportDataExternalBarcodes,
            exportFormat: ExportFormat.csv,
            selectedFields: this._tableSrv.getColumns('EXP_SESSION_EXTERNAL_BARCODES')
        });
        this._dialog.open(ExportDialogComponent, config)
            .beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap(exportList => {
                    this._tableSrv.setColumns('EXP_SESSION_EXTERNAL_BARCODES', exportList.fields.map(resultData => resultData.field));
                    return this._sessionsSrv.exportExternalBarcodes(this._session.event.id, this._session.id, exportList);
                }),
                filter(Boolean)
            )
            .subscribe(() =>
                this._ephemeralMsg.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' })
            );
    }

    loadBarcodes(event: SearchTableChangeEvent = null): void {
        this._filters = { ...this._filters, offset: event?.offset, barcode: event?.q };
        this._sessionsSrv.loadUploadedExternalBarcodes(this._session.event.id, this._session.id, this._filters);
    }

    private unsubscribeWebSocket(): void {
        if (!this._imports.size) {
            return;
        }
        this._imports.forEach(importId => this._ws.unsubscribeMessages(Topic.barcode, importId));
        this._sessionsSrv.clearBarcodesImportId();
    }

    private showImportDone(result: BarcodeImportResultDialog): void {
        const config = new ObMatDialogConfig(result);
        this._dialog.open(ImportBarcodesResultDialogComponent, config)
            .beforeClosed()
            .subscribe(() => this._sessionsSrv.loadUploadedExternalBarcodes(this._session.event.id, this._session.id, this._filters));
    }

    private showImportError(result: BarcodeImportResultDialog): void {
        this._message.showAlert({
            size: DialogSize.SMALL,
            title: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ERROR_TITLE',
            message: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ERROR_MESSAGE',
            messageParams: {
                sessionName: result.session.name,
                sessionDate: moment(result.session.start_date).format(DateTimeFormats.shortDateTimeWithWeek)
            }
        });
    }
}
