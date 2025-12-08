import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe
    ],
    selector: 'app-season-ticket-capacity-dialog',
    templateUrl: './season-ticket-capacity-dialog.component.html',
    styleUrls: ['./season-ticket-capacity-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeasonTicketCapacityDialogComponent {
    reasons: ReasonData[] = [];

    constructor(
        private _dialogRef: MatDialogRef<SeasonTicketCapacityDialogComponent>,
        @Inject(MAT_DIALOG_DATA) private _data: {
            notLinkedReasons: Set<ReasonData>;
        }
    ) {
        this._dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this._dialogRef.disableClose = false;
        this.reasons = [...this._data.notLinkedReasons.values()];
    }

    close(sessionId: number = null): void {
        this._dialogRef.close(sessionId);
    }
}

interface ReasonData {
    messageKey: string;
    data?: string | number;
}
