import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { InsurersService } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { CurrencyInputComponent, EphemeralMessageService, PercentageInputComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, Observable, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-policy-general-data',
    imports: [
        TranslatePipe, MatProgressSpinner, MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle,
        FormContainerComponent, MatFormField, MatLabel, CommonModule, MatInputModule,
        PercentageInputComponent, CurrencyInputComponent, ReactiveFormsModule,
        MatFormFieldModule, MatRadioGroup, MatRadioButton, MatIcon, MatCheckbox
    ],
    templateUrl: './policy-general-data.component.html',
    styleUrls: ['./policy-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PolicyGeneralDataComponent implements WritingComponent {
    readonly #insurerSrv = inject(InsurersService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    readonly form = this.#fb.group({
        name: [null as string, Validators.required],
        taxes: [null as number, [Validators.required, Validators.min(0)]],
        days_ahead_limit: [null as number, Validators.required],
        insurer_benefits_fix: [null as number, Validators.min(0)],
        insurer_benefits_percent: [null as number, Validators.min(0)],
        operator_benefits_fix: [null as number, Validators.min(0)],
        operator_benefits_percent: [null as number, Validators.min(0)],
        commission_type_insurer: null as 'fix' | 'percent',
        commission_type_operator: null as 'fix' | 'percent',
        default_allowed: [false as boolean]
    });

    readonly $policy = toSignal(this.#insurerSrv.policy.get$().pipe(
        filter(Boolean),
        tap(policy => {
            this.form.reset();
            this.form.patchValue(policy);

            if (policy.insurer_benefits_fix > 0) {
                this.form.controls.commission_type_insurer.setValue('fix');
            } else {
                this.form.controls.commission_type_insurer.setValue('percent');
            }
            if (policy.operator_benefits_fix > 0) {
                this.form.controls.commission_type_operator.setValue('fix');
            } else {
                this.form.controls.commission_type_operator.setValue('percent');
            }

            this.form.updateValueAndValidity();
        })
    ));

    readonly $isInProgress = toSignal(this.#insurerSrv.policy.inProgress$());

    constructor() {
        this.form.controls.commission_type_insurer.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(type => {
                const fix = this.form.controls.insurer_benefits_fix;
                const percent = this.form.controls.insurer_benefits_percent;

                if (type === 'fix') {
                    fix.setValidators([Validators.required, Validators.min(0)]);
                    percent.setValidators([Validators.min(0)]);
                } else {
                    percent.setValidators([Validators.required, Validators.min(0)]);
                    fix.setValidators([Validators.min(0)]);
                }

                fix.updateValueAndValidity();
                percent.updateValueAndValidity();
            });

        this.form.controls.commission_type_operator.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(type => {
                const fix = this.form.controls.operator_benefits_fix;
                const percent = this.form.controls.operator_benefits_percent;

                if (type === 'fix') {
                    fix.setValidators([Validators.required, Validators.min(0)]);
                    percent.setValidators([Validators.min(0)]);
                } else {
                    percent.setValidators([Validators.required, Validators.min(0)]);
                    fix.setValidators([Validators.min(0)]);
                }

                fix.updateValueAndValidity();
                percent.updateValueAndValidity();
            });
    }

    cancel(): void {
        this.#insurerSrv.policy.clear();
        this.#insurerSrv.policy.load(this.$policy().insurer_id, this.$policy().id);
        this.form.markAsPristine();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#insurerSrv.policiesList.load(this.$policy().insurer_id);
            this.#ephemeralSrv.showSaveSuccess();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {

            if (this.form.controls.commission_type_insurer?.value === 'fix') {
                this.form.controls.insurer_benefits_percent.setValue(0);
            } else {
                this.form.controls.insurer_benefits_fix.setValue(0);
            }

            if (this.form.controls.commission_type_operator?.value === 'fix') {
                this.form.controls.operator_benefits_percent.setValue(0);
            } else {
                this.form.controls.operator_benefits_fix.setValue(0);
            }

            const values = this.form.getRawValue();
            const payload = {
                name: values.name,
                taxes: values.taxes,
                days_ahead_limit: values.days_ahead_limit,
                insurer_benefits_fix: values.insurer_benefits_fix ?? 0,
                insurer_benefits_percent: values.insurer_benefits_percent ?? 0,
                operator_benefits_fix: values.operator_benefits_fix ?? 0,
                operator_benefits_percent: values.operator_benefits_percent ?? 0,
                default_allowed: values.default_allowed
            };

            return this.#insurerSrv.policy.update(this.$policy().insurer_id, this.$policy().id, payload)
                .pipe(tap(() => this.form.markAsPristine()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

}
