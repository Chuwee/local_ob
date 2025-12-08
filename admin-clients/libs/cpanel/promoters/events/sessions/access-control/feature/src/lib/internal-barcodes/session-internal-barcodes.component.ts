import { Metadata } from '@OneboxTM/utils-state';
import {
    BarcodeStatus, EventSessionsService, GetInternalBarcodesRequest, InternalBarcode, Session
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, ExportDialogComponent, ObMatDialogConfig, SearchTableChangeEvent, SearchTableComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ExportFormat } from '@admin-clients/shared/data-access/models';
import { ObfuscateStringPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable, skip, Subject, switchMap, takeUntil } from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';
import { exportDataWhiteList } from './white-list-export-data';

const PAGE_SIZE = 4;

@Component({
    selector: 'app-session-internal-barcodes',
    templateUrl: './session-internal-barcodes.component.html',
    styleUrls: ['./session-internal-barcodes.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, MaterialModule, TranslatePipe, SearchTableComponent, ObfuscateStringPipe,
        CommonModule
    ]
})
export class SessionInternalBarcodesComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    private readonly _tableSrv = inject(TableColConfigService);

    private _session: Session;
    private _filters: GetInternalBarcodesRequest = { limit: PAGE_SIZE };

    @ViewChild(SearchTableComponent, { static: true }) private _searchTable: SearchTableComponent<unknown>;

    readonly pageSize = PAGE_SIZE;
    readonly columns = ['barcode', 'row', 'seat', 'locator', 'status'];
    readonly barcodeStatus = BarcodeStatus;

    barcodes$: Observable<InternalBarcode[]>;
    metadata$: Observable<Metadata>;
    reqInProgress$: Observable<boolean>;
    totalBarcodes$: Observable<number>;
    isExportOrImportLoading$: Observable<boolean>;

    constructor(
        private _ephemeralMsg: EphemeralMessageService,
        private _sessionsSrv: EventSessionsService,
        private _dialog: MatDialog
    ) { }

    ngOnInit(): void {
        this._sessionsSrv.session.get$()
            .pipe(
                takeUntil(this._onDestroy),
                filter(session => !!session)
            )
            .subscribe(session => this._session = session);

        this.barcodes$ = this._sessionsSrv.getInternalBarcodesList$()
            .pipe(
                filter(Boolean),
                map(data => data.data)
            );

        this.metadata$ = this._sessionsSrv.getInternalBarcodesList$()
            .pipe(
                filter(Boolean),
                map(data => data.metadata)
            );

        this.totalBarcodes$ = this.metadata$
            .pipe(map(md => md ? md.total : 0));

        this.reqInProgress$ = this._sessionsSrv.isInternalBarcodesLoading$();
        this.isExportOrImportLoading$ = booleanOrMerge([
            this._sessionsSrv.isExportWhitelistLoading$(),
            this._sessionsSrv.isBarcodesImportInProgress$()
        ]);
        // reloads barcodes list when selected session changes
        this._sessionsSrv.session.get$()
            .pipe(
                filter(Boolean), map(session => session.id), skip(1), distinctUntilChanged(),
                takeUntil(this._onDestroy)
            )
            .subscribe(() => this._searchTable.clearFilter());
    }

    ngOnDestroy(): void {
        this._sessionsSrv.clearInternalBarcodes();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    barcodeValidated(barcode: InternalBarcode): boolean {
        return barcode.status === BarcodeStatus.validated;
    }

    openExportWhitelistDialog(): void {
        const config = new ObMatDialogConfig({
            exportData: exportDataWhiteList,
            exportFormat: ExportFormat.csv,
            selectedFields: this._tableSrv.getColumns('EXP_SESSION_INTERNAL_BARCODES')
        });
        this._dialog.open(ExportDialogComponent, config)
            .beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap(exportList => {
                    this._tableSrv.setColumns('EXP_SESSION_INTERNAL_BARCODES', exportList.fields.map(resultData => resultData.field));
                    return this._sessionsSrv.exportSessionWhitelist(this._session.event.id, this._session.id, exportList);
                }),
                filter(result => !!result.export_id)
            )
            .subscribe(() =>
                this._ephemeralMsg.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' })
            );
    }

    loadBarcodes(event: SearchTableChangeEvent = null): void {
        this._filters = { ...this._filters, offset: event?.offset, barcode: event?.q };
        this._sessionsSrv.loadInternalBarcodes(this._session.event.id, this._session.id, this._filters);
    }
}
