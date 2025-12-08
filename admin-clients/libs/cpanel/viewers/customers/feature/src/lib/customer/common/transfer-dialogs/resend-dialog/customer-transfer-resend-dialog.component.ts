import { CustomerSeatManagementDialogData } from '@admin-clients/cpanel-viewers-customers-data-access';
import { TicketsBaseService } from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { CustomerTransferDialogInfoComponent } from '../info/customer-transfer-dialog-info.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDialogModule,
        FlexLayoutModule,
        MatButtonModule,
        MatIconModule,
        CustomerTransferDialogInfoComponent,
        TranslatePipe,
        AsyncPipe,
        MatProgressSpinnerModule
    ],
    styleUrls: ['./customer-transfer-resend-dialog.component.scss'],
    templateUrl: './customer-transfer-resend-dialog.component.html'
})
export class CustomerTransferResendDialogComponent implements OnInit {
    readonly #dialogRef = inject(MatDialogRef<CustomerTransferResendDialogComponent>);
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly data = inject<CustomerSeatManagementDialogData>(MAT_DIALOG_DATA);
    readonly isInProgress$ = booleanOrMerge([
        this.#ticketsSrv.ticketTransfer.deleteLoading$(),
        this.#ticketsSrv.ticketTransfer.resendEmailLoading$()
    ]);

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    resend(): void {
        const code = this.data.selectedSeat.order.code;
        const itemId = this.data.selectedSeat.id;
        const sessionId = this.data.session.session_id;

        this.#ticketsSrv.ticketTransfer.resendEmail$(code, itemId, sessionId)
            .subscribe(() => this.#dialogRef.close(true));
    }
}
