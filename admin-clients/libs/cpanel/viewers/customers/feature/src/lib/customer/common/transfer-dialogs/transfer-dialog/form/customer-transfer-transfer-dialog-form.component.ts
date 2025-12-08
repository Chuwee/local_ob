import {
    FormControlErrorsComponent,
    scrollIntoFirstInvalidFieldOrErrorMsg
} from '@OneboxTM/feature-form-control-errors';
import { TransferDataSessionDeliveryMethod } from '@admin-clients/shared/common/data-access';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, input, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, takeUntil } from 'rxjs';
import { CustomerTransferTransferDialogForm } from '../models/customer-transfer-transfer-dialog-form.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, ReactiveFormsModule, MatInputModule, FormControlErrorsComponent,
        MatRadioModule
    ],
    selector: 'app-customer-transfer-transfer-dialog-form',
    styleUrls: ['./customer-transfer-transfer-dialog-form.component.scss'],
    templateUrl: './customer-transfer-transfer-dialog-form.component.html'
})
export class CustomerTransferTransferDialogFormComponent implements OnInit, OnDestroy {
    readonly #onDestroy = new Subject<void>();
    readonly #cdf = inject(ChangeDetectorRef);
    readonly deliveryMethod = TransferDataSessionDeliveryMethod;
    readonly $form = input.required<FormGroup<CustomerTransferTransferDialogForm>>({ alias: 'form' });
    readonly $emailSubmit = input<boolean>(false, { alias: 'emailSubmit' });

    ngOnInit(): void {
        if (!this.$emailSubmit() && this.$form().controls.send_type.value === TransferDataSessionDeliveryMethod.download) {
            this.initMailCtrlAsDisabled();
        }
        this.sendTypeCtrlChangeHandler();
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    markAsTouched(): void {
        this.$form().markAllAsTouched();
        this.#cdf.markForCheck();
        scrollIntoFirstInvalidFieldOrErrorMsg(document);
    }

    private initMailCtrlAsDisabled(): void {
        // Only purpose, is for asterisk when mail control is initially disabled
        setTimeout(() => {
            this.$form().controls.email.disable({ emitEvent: false });
            this.#cdf.markForCheck();
        });
    }

    private sendTypeCtrlChangeHandler(): void {
        this.$form().controls.send_type.valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(value => {
                if (value === TransferDataSessionDeliveryMethod.email) {
                    this.$form().controls.email.enable({ emitEvent: false });
                } else if (value === TransferDataSessionDeliveryMethod.download) {
                    this.$form().controls.email.disable({ emitEvent: false });
                }
            });
    }
}
