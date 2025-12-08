import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { DialogSize, EphemeralMessageService, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-new-operator-tax-dialog',
    templateUrl: './new-operator-tax-dialog.component.html',
    styleUrls: ['./new-operator-tax-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        ReactiveFormsModule,
        FormControlErrorsComponent
    ]
})
export class NewOperatorTaxDialogComponent extends ObDialog<NewOperatorTaxDialogComponent, { operatorId: number }, boolean> {
    private readonly _operatorsSrv = inject(OperatorsService);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);

    readonly form = inject(UntypedFormBuilder).group({
        name: ['', [Validators.required]],
        value: [null, [Validators.required, Validators.min(0), Validators.max(100)]]
    });

    readonly reqInProgress$ = this._operatorsSrv.operatorTaxes.saving$();

    constructor() {
        super(DialogSize.MEDIUM, undefined);
    }

    createTax(): void {
        if (this.form.valid) {
            const value = this.form.getRawValue();
            this._operatorsSrv.operatorTaxes
                .create(this.data.operatorId, { name: value.name, value: value.value })
                .subscribe(() => {
                    this._ephemeralSrv.showSuccess({ msgKey: 'OPERATOR.ADD_TAX_SUCCESS' });
                    this.dialogRef.close(true);
                });
        } else {
            this.form.markAllAsTouched();
        }
    }
}
