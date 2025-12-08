import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Rate, RATE_RESTRICTIONS_SERVICE } from '@admin-clients/cpanel/promoters/shared/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { unique } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, ElementRef, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-new-rate-resctriction-dialog',
    imports: [TranslatePipe, ReactiveFormsModule, MatIconModule, MatSpinner, MatFormFieldModule,
        MatInputModule, MatButtonModule, MatSelectModule, FormControlErrorsComponent, MatDialogModule,
        PrefixPipe
    ],
    templateUrl: './new-rate-restrictions-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewRateRestrictionsDialogComponent {
    readonly #ratesRestrictionsSrv = inject(RATE_RESTRICTIONS_SERVICE);
    readonly #dialogRef = inject(MatDialogRef<NewRateRestrictionsDialogComponent, boolean>);
    readonly #fb = inject(FormBuilder);
    readonly #elementRef = inject(ElementRef);
    readonly #data = inject<{
        ratesRestrictions: number[];
        rates: Rate[];
        contextId: number;
    }>(MAT_DIALOG_DATA);

    readonly rates = this.#data.rates;
    readonly filteredRates = this.rates?.filter(rate => !this.#data.ratesRestrictions.includes(rate.id));
    readonly $isLoading = toSignal(this.#ratesRestrictionsSrv.ratesRestrictions.inProgress$());

    readonly form = this.#fb.group({
        rate: [null as number, [Validators.required, unique<number>(this.#data.ratesRestrictions)]]
    });

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    save(): void {
        if (this.form.valid) {
            this.#ratesRestrictionsSrv.ratesRestrictions.update(this.#data.contextId, this.form.value.rate, {}).subscribe(() => {
                this.close(this.form.value.rate);
            });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elementRef.nativeElement);
        }
    }

    close(createdRateId?: number): void {
        this.#dialogRef.close(createdRateId);
    }
}
