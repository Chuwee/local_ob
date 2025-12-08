import {
    ReimbursementFormsSchema, ReimbursementInfo, ReimbursementType, RetryReimbursementRequestModel, OrdersService
} from '@admin-clients/cpanel-sales-data-access';
import { EphemeralMessageService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-reimburse-order-dialog',
    templateUrl: './reimburse-order-dialog.component.html',
    styleUrls: ['./reimburse-order-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ReimburseOrderDialogComponent implements OnInit, OnDestroy {
    private _onDestroy: Subject<void> = new Subject();
    private _orderCode: string;
    private _reimbursement: ReimbursementInfo;

    readonly manualFormSchema: ReimbursementFormsSchema['whateverForm'];
    readonly types = ReimbursementType;

    form: UntypedFormGroup;
    disableDefault: boolean;

    get manualForm(): UntypedFormGroup {
        return this.form.get('manualForm') as UntypedFormGroup;
    }

    constructor(
        private _ordersService: OrdersService,
        private _dialogRef: MatDialogRef<ReimburseOrderDialogComponent>,
        private _fb: UntypedFormBuilder,
        private _ephemeralMessage: EphemeralMessageService,
        @Inject(MAT_DIALOG_DATA) data: {
            reimbursement: ReimbursementInfo; manualFormSchema: ReimbursementFormsSchema['whateverForm']; disableDefault: boolean;
        }) {
        this._dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this._dialogRef.disableClose = false;
        this._reimbursement = data.reimbursement;
        this.manualFormSchema = data.manualFormSchema;
        this.disableDefault = data.disableDefault;
    }

    ngOnInit(): void {
        this.initForms();
        this.form.controls['type'].valueChanges.pipe(
            takeUntil(this._onDestroy)
        ).subscribe(value => {
            if (value === this.types.manual) {
                this.manualForm.enable();
            } else {
                this.manualForm.disable();
            }
        });

        this._ordersService.getOrderDetail$().pipe(
            filter(value => value !== null),
            takeUntil(this._onDestroy)
        ).subscribe(orderDetail => {
            this._orderCode = orderDetail.code;
        });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(): void {
        this._dialogRef.close(false);
    }

    submit(): void {
        if (this.form.valid) {
            const params = { gateway_properties: { oC: 'false' } } as RetryReimbursementRequestModel;

            if (this.manualForm.enabled) {
                params.gateway_properties = { ...this.manualForm.value, oC: 'true' };
            }
            this._dialogRef.close(true);
            this._ordersService.reimburseOrder(this._orderCode, this._reimbursement.transaction_id, params)
                .subscribe(() => {
                    this._ephemeralMessage.showSuccess({ msgKey: 'ACTIONS.REIMBURSEMENT.OK.MESSAGE' });
                    this._ordersService.loadOrderDetail(this._orderCode);
                    this._dialogRef.close(true);
                }, () => {
                    this._ordersService.loadOrderDetail(this._orderCode);
                });
        } else {
            this.form.markAllAsTouched();
        }
    }

    private initForms(): void {
        const manualForm = this._fb.group({});
        let type = this.types.default;
        if (this.manualFormSchema?.length) {
            this.manualFormSchema.forEach(fieldSchema => {
                const fieldControl = new UntypedFormControl();
                if (fieldSchema.required) {
                    fieldControl.addValidators(Validators.required);
                }
                if (fieldSchema.pattern) {
                    fieldControl.addValidators(Validators.pattern(fieldSchema.pattern));
                }
                manualForm.addControl(fieldSchema.name, fieldControl);
            });
            type = this.types.manual;
        } else {
            manualForm.disable();
        }
        this.form = this._fb.group({
            type: [type, [Validators.required]],
            manualForm
        });
    }
}
