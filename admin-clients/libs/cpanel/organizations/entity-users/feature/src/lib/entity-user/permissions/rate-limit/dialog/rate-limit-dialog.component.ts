import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    DEFAULT_RATE_LIMIT_RULE, EntityUsersService, type RateLimitQuota, type RateLimitRule
} from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { ObTimeUnit } from '@admin-clients/shared/data-access/models';
import { includedInArrayValidator, unique } from '@admin-clients/shared/utility/utils';
import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';

export type DialogType = 'CREATE' | 'EDIT';
type DialogData = { index: number; userId: number; type: DialogType };
const PATH_REGEX = /^\/(?:(?:[a-zA-Z0-9?_-]+)|\*{1,2})(?:\/(?:[a-zA-Z0-9?_-]+|\*{1,2}))*$/;

@Component({
    selector: 'ob-rate-limit-dialog',
    templateUrl: 'rate-limit-dialog.component.html',
    imports: [
        MatDialogTitle, MatDialogContent, MatDialogActions, MatButton, MatIcon, MatIconButton, TranslatePipe, MatOption, TitleCasePipe,
        ReactiveFormsModule, TranslatePipe, MatLabel, MatFormField, MatInput, MatSelect, MatError, FormControlErrorsComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RateLimitDialogComponent extends ObDialog<RateLimitDialogComponent, DialogData, boolean> {
    readonly #dialogRef = inject(MatDialogRef<RateLimitDialogComponent>);
    readonly #fb = inject(FormBuilder);
    readonly #data: DialogData = inject(MAT_DIALOG_DATA);
    readonly #userSrv = inject(EntityUsersService);

    readonly $rateLimit = toSignal(this.#userSrv.userRateLimit.get$());
    readonly $loading = toSignal(this.#userSrv.userRateLimit.inProgress$());

    readonly #type = this.#data.type;
    readonly #index = this.#data.index;
    readonly #rules = this.$rateLimit()?.rules ?? [];
    readonly #rule = this.#rules[this.#index] ?? DEFAULT_RATE_LIMIT_RULE;
    readonly #existingPatterns = this.#rules.filter((_, index) => index !== this.#index).map(rule => rule.pattern);

    readonly dialogTypedCreate = this.#type === 'CREATE';
    readonly timeUnitOptions: ObTimeUnit[] = [ObTimeUnit.seconds, ObTimeUnit.minutes, ObTimeUnit.hours, ObTimeUnit.days];
    readonly form = this.#fb.group({
        pattern: this.#fb.control<string>(
            this.#rule.pattern, [Validators.required, unique(this.#existingPatterns), Validators.pattern(PATH_REGEX)]
        ),
        quotas: this.#fb.array(
            this.#rule.quotas.map(quota => this.#createQuotaFormGroup(quota))
        )
    });

    constructor() {
        super(DialogSize.MEDIUM, true);
    }

    close(success = false): void {
        this.#dialogRef.close(success);
    }

    addQuota(): void {
        this.form.controls.quotas.push(this.#createQuotaFormGroup());
    }

    removeQuota(index: number): void {
        this.form.controls.quotas.removeAt(index);
        this.form.markAsDirty();
    }

    save(): void {
        this.save$().subscribe({ next: () => this.close(true) });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const updatedRule = this.form.getRawValue() as RateLimitRule;
            const updatedRules = [...this.#rules];

            if (this.#type === 'EDIT') {
                updatedRules[this.#data.index] = updatedRule;
            } else if (this.#type === 'CREATE') {
                updatedRules.unshift(updatedRule);
            }

            return this.#userSrv.userRateLimit.update(
                this.#data.userId,
                { ...this.$rateLimit(), rules: updatedRules }
            );
        } else {
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    #createQuotaFormGroup(quota: RateLimitQuota = null): FormGroup {
        return this.#fb.group({
            time_unit: this.#fb.control<string>(quota?.time_unit, [Validators.required, includedInArrayValidator(this.timeUnitOptions)]),
            period: this.#fb.control<number>(quota?.period, [Validators.required, Validators.min(1)]),
            limit: this.#fb.control<number>(quota?.limit, [Validators.required, Validators.min(1)])
        });
    }
}
