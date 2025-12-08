import { BuyerData } from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';

@Component({
    selector: 'app-order-profile-attributes-dialog',
    templateUrl: './order-profile-attributes-dialog.component.html',
    styleUrls: ['./order-profile-attributes-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class OrderProfileAttributesDialogComponent implements OnDestroy {
    private _onDestroy: Subject<void> = new Subject();

    attributes: BuyerData['profile_data']['attributes'];

    constructor(
        private _dialogRef: MatDialogRef<OrderProfileAttributesDialogComponent>,
        @Inject(MAT_DIALOG_DATA) data: BuyerData['profile_data']['attributes']
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this.attributes = data;
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(): void {
        this._dialogRef.close();
    }
}
