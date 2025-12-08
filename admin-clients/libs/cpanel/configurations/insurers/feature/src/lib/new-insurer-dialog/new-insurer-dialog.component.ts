import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { InsurersService, PostInsurer } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { Operator, OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
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
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { filter } from 'rxjs';

@Component({
    selector: 'app-new-insurer-dialog',
    templateUrl: './new-insurer-dialog.component.html',
    styleUrls: ['./new-insurer-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule, TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent,
        MatButton, MatIcon, MatDialogModule, MatDialogTitle, MatDialogContent,
        MatDialogActions, MatFormField, MatLabel, MatInput, MatSelect, MatOption,
        MatError, MatProgressSpinner, MatIconButton, ObFormFieldLabelDirective
    ]
})
export class NewInsurerDialogComponent implements OnInit {
    readonly #dialogRef = inject(MatDialogRef<NewInsurerDialogComponent, { id: number; password: string }>);
    readonly #operatorsSrv = inject(OperatorsService);
    readonly #insurersSrv = inject(InsurersService);
    readonly #fb = inject(FormBuilder);

    readonly form = this.#fb.group({
        name: [null as string, [Validators.required]],
        operator: [null as Operator, [Validators.required]],
        business: [null as string, [Validators.required]],
        cif: [null as string, [Validators.required]],
        email: [null as string, [Validators.required]],
        address: [null as string, [Validators.required]],
        zip: [null as string, [Validators.required]],
        phone: [null as string, [Validators.required]]
    });

    readonly $operators = toSignal(this.#operatorsSrv.operators.getData$().pipe(filter(Boolean)));

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

    createInsurer(): void {
        if (this.form.valid) {
            const { name, operator, business, cif, email, address, zip, phone } = this.form.value;
            const insurer: PostInsurer = {
                name,
                operator_id: operator.id,
                tax_id: cif,
                tax_name: business,
                shard: operator.shard,
                contact_email: email,
                address,
                zip_code: zip,
                phone
            };
            this.#insurersSrv.insurer.create(insurer).subscribe(response => this.close(response));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    close(insurer: { id: number } = null): void {
        this.#dialogRef.close(insurer?.id);
    }
}
