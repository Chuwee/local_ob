import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { AsyncPipe } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import {
    DeleteTicketTransferRequest, PostTicketReleaseRequest,
    ReleaseAction, SeatManagementDataRequestUserType, TicketReleaseData,
    TicketsBaseService
} from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { CustomerReleaseDialogInfoComponent } from '../info/customer-release-dialog-info.component';
import { CustomerSeatManagementDialogData } from '@admin-clients/cpanel-viewers-customers-data-access';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDialogModule,
        FlexLayoutModule,
        MatButtonModule,
        MatIconModule,
        TranslatePipe,
        CustomerReleaseDialogInfoComponent,
        AsyncPipe,
        MatProgressSpinnerModule
    ],
    templateUrl: './customer-release-recover-dialog.component.html'
})
export class CustomerReleaseRecoverDialogComponent implements OnInit {
    readonly #dialogRef = inject(MatDialogRef<CustomerReleaseRecoverDialogComponent, boolean>);
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly data = inject<CustomerSeatManagementDialogData>(MAT_DIALOG_DATA);
    readonly isInProgress$ = this.#ticketsSrv.ticketTransfer.deleteLoading$();

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    submit(): void {
        if (this.data.action == ReleaseAction.recover) {
            const request: DeleteTicketTransferRequest = {
                code: this.data.selectedSeat.order.code,
                itemId: this.data.selectedSeat.id,
                session_id: this.data.session.session_id
            };
            this.#ticketsSrv.ticketRelease.delete$(request)
                .subscribe(() => this.#dialogRef.close(true));
        } else {
            const releaseData: TicketReleaseData = {
                request_user_type: SeatManagementDataRequestUserType.cpanel
            };
            const request: PostTicketReleaseRequest = {
                code: this.data.selectedSeat.order.code,
                itemId: this.data.selectedSeat.id,
                session_id: this.data.session.session_id,
                release_data: releaseData
            };
            this.#ticketsSrv.ticketRelease.release$(request)
                .subscribe(() => this.#dialogRef.close(releaseData));
        }

    }
}
