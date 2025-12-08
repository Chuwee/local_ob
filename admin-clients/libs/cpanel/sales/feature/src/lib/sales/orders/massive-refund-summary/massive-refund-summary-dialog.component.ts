import { PostMassiveRefundOrdersResponse } from '@admin-clients/cpanel-sales-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';

@Component({
    selector: 'app-massive-refund-summary',
    templateUrl: './massive-refund-summary-dialog.component.html',
    styleUrls: ['./massive-refund-summary-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MassiveRefundSummaryDialogComponent implements OnDestroy {
    private _onDestroy = new Subject<void>();

    readonly dateTimeFormats = DateTimeFormats;

    summary: PostMassiveRefundOrdersResponse;

    constructor(
        private _dialogRef: MatDialogRef<MassiveRefundSummaryDialogComponent, void>,
        @Inject(MAT_DIALOG_DATA) data: {
            summary: PostMassiveRefundOrdersResponse;
        }
    ) {
        this.summary = data.summary;
        this._dialogRef.addPanelClass(DialogSize.LARGE);
        this._dialogRef.disableClose = false;
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(): void {
        this._dialogRef.close();
    }

}
