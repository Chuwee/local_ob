import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { B2bClientDepositType, B2bClientOperationType, B2bClientOperation, B2bService } from '@admin-clients/cpanel/b2b/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { AbstractControl, FormBuilder, ValidationErrors, Validators } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import moment from 'moment';
import { Observable, Subject, map } from 'rxjs';
import { VmB2bClientBalanceOperation } from './b2b-client-balance-operation.model';

@Component({
    selector: 'app-b2b-client-balance-operation-dialog',
    templateUrl: './b2b-client-balance-operation-dialog.component.html',
    styleUrls: ['./b2b-client-balance-operation-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bClientBalanceOperationDialogComponent
    extends ObDialog<B2bClientBalanceOperationDialogComponent, VmB2bClientBalanceOperation, boolean>
    implements OnInit, OnDestroy {

    private _fb = inject(FormBuilder);
    private _b2bSrv = inject(B2bService);
    readonly #FORMATS = inject(MAT_DATE_FORMATS);
    private _onDestroy = new Subject<void>();

    readonly dateFormat = moment.localeData().longDateFormat(this.#FORMATS.display.dateInput).toLowerCase();

    form = this._fb.group({
        amount: [null as number, Validators.required],
        type: [
            { value: null as B2bClientDepositType, disabled: this.data.operationType !== B2bClientOperationType.deposit },
            Validators.required
        ],
        transactionCode: [{ value: '', disabled: this.data.operationType !== B2bClientOperationType.deposit }],
        notes: '',
        effectiveDate: [new Date(), Validators.required]
    });

    inProgress$: Observable<boolean>;
    operationType: B2bClientOperationType;
    currentAmount: number;
    newAmount$: Observable<number>;
    readonly operationTypes = B2bClientOperationType;
    readonly depositTypes = Object.values(B2bClientDepositType);

    constructor() { super(DialogSize.MEDIUM); }

    ngOnInit(): void {
        this.initForm();
        this.model();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    saveConditions(): void {
        if (this.form.valid) {
            const formValues = this.form.value;
            const requestBody: B2bClientOperation = {
                amount: formValues.amount,
                notes: formValues.notes,
                entity_id: this.data.entityId,
                effective_date: formValues.effectiveDate,
                ...(this.operationType === B2bClientOperationType.deposit ?
                    {
                        additional_info: {
                            deposit_transaction_id: formValues.transactionCode,
                            deposit_type: formValues.type
                        }
                    } : {}),
                currency_code: this.data.currency
            };
            this._b2bSrv.b2bClientBalanceOperation.save(this.data.clientId, this.data.operationType, requestBody)
                .subscribe(() => this.close(true));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    close(actionPerformed = false): void {
        this.dialogRef.close(actionPerformed);
    }

    private initForm(): void {
        if (this.data.operationType === B2bClientOperationType.cashAdjustment) {
            this.form.get('amount').addValidators(this.availableBalanceValidator);
        } else {
            this.form.get('amount').addValidators(Validators.min(0));
        }
    }

    private model(): void {
        this.inProgress$ = this._b2bSrv.b2bClientBalanceOperation.isInProgress$();
        this.operationType = this.data.operationType;
        const clientbl = this.data.clientBalance;

        if (this.operationType === B2bClientOperationType.creditLimit) {
            this.currentAmount = clientbl.credit_limit;
            this.newAmount$ = this.form.get('amount').valueChanges
                .pipe(map(val => val && !this.form.get('amount').errors ? val : null));
        } else {
            this.currentAmount = clientbl.balance;
            this.newAmount$ = this.form.get('amount').valueChanges
                .pipe(map(val => val && !this.form.get('amount').errors ? this.currentAmount + val : null));
        }

    }

    private availableBalanceValidator = (control: AbstractControl<number>): ValidationErrors | null => {
        if (control.value && control.value + this.data.clientBalance.total_available < 0) {
            return { availableBalanceExceeded: true };
        }
        return null;
    };
}
