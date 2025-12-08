import { CustomerFriends, CustomerSeatManagementDialogData, CustomersService } from '@admin-clients/cpanel-viewers-customers-data-access';
import {
    PostTicketTransferRequest, TicketTransferData, TicketsBaseService, SeatManagementDataRequestUserType,
    TransferDataSessionDeliveryMethod
} from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, ViewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, of } from 'rxjs';
import { CustomerTransferDialogInfoComponent } from '../info/customer-transfer-dialog-info.component';
import { CustomerTransferTransferDialogFormComponent } from './form/customer-transfer-transfer-dialog-form.component';
import { CustomerTransferTransferDialogForm } from './models/customer-transfer-transfer-dialog-form.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDialogModule, FlexLayoutModule, MatButtonModule, MatIconModule, TranslatePipe, CustomerTransferDialogInfoComponent, AsyncPipe,
        MatProgressSpinnerModule, CustomerTransferTransferDialogFormComponent,
        MatSelectModule, MatFormFieldModule, SelectSearchComponent, ReactiveFormsModule
    ],
    styleUrls: ['./customer-transfer-transfer-dialog.component.scss'],
    templateUrl: './customer-transfer-transfer-dialog.component.html'
})
export class CustomerTransferTransferDialogComponent {
    readonly #dialogRef = inject(MatDialogRef<CustomerTransferTransferDialogComponent, TicketTransferData>);
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly #customersSrv = inject(CustomersService);

    @ViewChild(CustomerTransferTransferDialogFormComponent, { static: true })
    private readonly formComponent: CustomerTransferTransferDialogFormComponent;

    readonly #$currentUser = toSignal(this.#customersSrv.customer.get$().pipe(filter(Boolean)));

    readonly data = inject<CustomerSeatManagementDialogData>(MAT_DIALOG_DATA);
    readonly form = inject(FormBuilder).group<CustomerTransferTransferDialogForm>({
        name: new FormControl(null, Validators.required),
        surname: new FormControl(null, Validators.required),
        send_type: new FormControl(null, Validators.required),
        email: new FormControl(null, [Validators.email, Validators.required])
    });

    readonly friendsSelectorControl = new FormControl<CustomerFriends>(null);
    readonly friendsList$ = of(this.data.friends ?? []);

    readonly isInProgress$ = this.#ticketsSrv.ticketTransfer.loading$();

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        if (this.data.friends?.length) {
            this.#friendsSelectorChangeHandler();
        }
        if (this.data.emailSubmit || this.data.friends?.length) {
            this.form.controls.send_type.setValue(TransferDataSessionDeliveryMethod.email);
        }
    }

    save(): void {
        if (this.form.invalid) {
            this.formComponent?.markAsTouched();
        } else {
            const transferData: TicketTransferData = {
                ...this.form.getRawValue(),
                request_user_type: SeatManagementDataRequestUserType.cpanel,
                request_customer_id: this.#$currentUser()?.id,
                ...(this.data.transferPolicy === 'FRIENDS_AND_FAMILY' && {
                    customer_id: this.friendsSelectorControl.value?.id
                })
            };
            const request: PostTicketTransferRequest = {
                code: this.data.selectedSeat.order.code,
                itemId: this.data.selectedSeat.id,
                session_id: this.data.session.session_id,
                transfer_data: transferData
            };
            this.#ticketsSrv.ticketTransfer.transfer$(request)
                .subscribe(() => this.#dialogRef.close(transferData));
        }
    }

    #friendsSelectorChangeHandler(): void {
        this.friendsSelectorControl.valueChanges.pipe(takeUntilDestroyed()).subscribe((friend: CustomerFriends) => {
            if (friend) {
                this.form.patchValue({
                    name: friend.name,
                    surname: friend.surname,
                    email: friend.email
                });
            }
        });
    }
}
