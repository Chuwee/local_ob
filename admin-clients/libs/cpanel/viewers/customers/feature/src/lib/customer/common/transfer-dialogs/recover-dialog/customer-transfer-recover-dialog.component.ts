import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { TicketsBaseService, DeleteTicketTransferRequest } from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { CustomerTransferDialogInfoComponent } from '../info/customer-transfer-dialog-info.component';
import { AsyncPipe } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CustomerSeatManagementDialogData } from '@admin-clients/cpanel-viewers-customers-data-access';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDialogModule,
        FlexLayoutModule,
        MatButtonModule,
        MatIconModule,
        TranslatePipe,
        CustomerTransferDialogInfoComponent,
        AsyncPipe,
        MatProgressSpinnerModule
    ],
    styleUrls: ['./customer-transfer-recover-dialog.component.scss'],
    templateUrl: './customer-transfer-recover-dialog.component.html'
})
export class CustomerTransferRecoverDialogComponent implements OnInit {
    readonly #dialogRef = inject(MatDialogRef<CustomerTransferRecoverDialogComponent, boolean>);
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly data = inject<CustomerSeatManagementDialogData>(MAT_DIALOG_DATA);
    readonly isInProgress$ = this.#ticketsSrv.ticketTransfer.deleteLoading$();

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    recover(): void {
        const request: DeleteTicketTransferRequest = {
            code: this.data.selectedSeat.order.code,
            itemId: this.data.selectedSeat.id,
            session_id: this.data.session.session_id
        };
        this.#ticketsSrv.ticketTransfer.delete$(request)
            .subscribe(() => this.#dialogRef.close(true));
    }
}
