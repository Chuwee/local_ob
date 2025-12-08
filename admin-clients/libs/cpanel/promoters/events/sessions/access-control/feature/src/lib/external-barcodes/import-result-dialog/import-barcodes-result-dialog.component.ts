import { BarcodeImportResultDialog, Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-import-barcodes-result-dialog',
    templateUrl: './import-barcodes-result-dialog.component.html',
    styleUrls: ['./import-barcodes-result-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, LocalDateTimePipe, CommonModule, FlexLayoutModule
    ]
})
export class ImportBarcodesResultDialogComponent implements OnInit {

    created: number;
    errors: number;
    session: Session;

    dateTimeFormats = DateTimeFormats;

    constructor(
        private _dialogRef: MatDialogRef<ImportBarcodesResultDialogComponent, void>,
        @Inject(MAT_DIALOG_DATA) private _data: BarcodeImportResultDialog
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.created = this._data.result.data.created;
        this.errors = this._data.result.data.errors;
        this.session = this._data.session;
    }

    close(): void {
        this._dialogRef.close();
    }
}
