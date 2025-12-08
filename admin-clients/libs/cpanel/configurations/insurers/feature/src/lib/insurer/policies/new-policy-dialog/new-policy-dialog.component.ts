import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { InsurersService, PostPolicy } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { DialogSize, PercentageInputComponent } from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
    FormBuilder,
    ReactiveFormsModule, Validators
} from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent, MatDialogModule, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-new-policy-dialog',
    templateUrl: './new-policy-dialog.component.html',
    styleUrls: ['./new-policy-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent,
        MatButton, MatIcon, MatDialogModule, MatDialogTitle, MatDialogContent,
        MatDialogActions, MatFormField, MatLabel, MatInput, MatError, MatProgressSpinner,
        MatIconButton, ObFormFieldLabelDirective, PercentageInputComponent
    ]
})
export class NewPolicyDialogComponent implements OnInit {
    readonly #dialogRef = inject(MatDialogRef<NewPolicyDialogComponent, { id: number; password: string }>);
    readonly #operatorsSrv = inject(OperatorsService);
    readonly #insurersSrv = inject(InsurersService);
    readonly #fb = inject(FormBuilder);

    readonly form = this.#fb.group({
        name: [null as string, [Validators.required]],
        number: [null as string, [Validators.required]],
        taxes: [null as number, [Validators.required]]
    });

    readonly $insurer = toSignal(this.#insurersSrv.insurer.get$());

    readonly $reqInProgress = toSignal(booleanOrMerge([
        this.#operatorsSrv.operator.loading$(),
        this.#insurersSrv.insurer.inProgress$()
    ]));

    constructor(
    ) {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.#operatorsSrv.operators.load({ limit: 999 });
    }

    createPolicy(): void {
        if (this.form.valid) {
            const { name, number, taxes } = this.form.value;
            const policy: PostPolicy = {
                name,
                policy_number: number,
                taxes
            };
            this.#insurersSrv.policy.create(this.$insurer().id, policy).subscribe(policyId => {
                const response = {
                    policyId: policyId.id,
                    insurerId: this.$insurer().id
                };

                this.close(response);
            });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    close(response: { policyId: number; insurerId: number } = null): void {
        this.#dialogRef.close(response);
    }
}
