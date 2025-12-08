import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { CustomersService, PostLoyaltyPoints } from '@admin-clients/cpanel-viewers-customers-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { maxDecimalLength, nonZeroValidator } from '@admin-clients/shared/utility/utils';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-loyalty-points-edit-dialog',
    templateUrl: './loyalty-points-edit-dialog.component.html',
    styleUrls: ['./loyalty-points-edit-dialog.component.scss'],
    imports: [TranslatePipe, MaterialModule, ReactiveFormsModule, FormControlErrorsComponent, UpperCasePipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoyaltyPointsEditDialogComponent {
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<LoyaltyPointsEditDialogComponent>);
    readonly #customerSrv = inject(CustomersService);

    readonly form = this.#fb.group({
        amount: [null as number, [Validators.required, nonZeroValidator, maxDecimalLength(0)]],
        description: [null as string, Validators.required]
    });

    readonly #data = inject<{
        actualPoints: number;
        customerId: string;
        entityId: number;
    }>(MAT_DIALOG_DATA);

    readonly $inProgress = toSignal(this.#customerSrv.customerLoyaltyPoints.loading$());
    readonly $currentAmount = signal(this.#data.actualPoints);
    readonly $amount = toSignal(this.form.controls.amount.valueChanges, { initialValue: null });
    readonly $newAmount = computed(() =>
        this.$amount() && !this.form.controls.amount.errors ? this.$currentAmount() + this.$amount() : null
    );

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    save(): void {
        if (this.form.valid) {
            const formValues = this.form.value;
            const requestBody: PostLoyaltyPoints = {
                points: formValues.amount,
                description: formValues.description
            };

            this.#customerSrv.customerLoyaltyPoints.create(this.#data.customerId, requestBody, this.#data.entityId)
                .subscribe(() => this.close(true));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    close(actionPerformed = false): void {
        this.#dialogRef.close(actionPerformed);
    }
}
