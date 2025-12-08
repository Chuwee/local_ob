import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { InsurersService } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { RangeTableComponent, EphemeralMessageService, MessageDialogService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-policy-prices',
    imports: [
        TranslatePipe, MatProgressSpinner, FormContainerComponent, CommonModule, MatInputModule,
        ReactiveFormsModule, MatFormFieldModule, RangeTableComponent
    ],
    templateUrl: './policy-prices.component.html',
    styleUrls: ['./policy-prices.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class PolicyPricesComponent {
    readonly #insurerSrv = inject(InsurersService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #messageDialogService = inject(MessageDialogService);

    readonly form = this.#fb.group({});

    readonly $policy = toSignal(this.#insurerSrv.policy.get$());

    readonly $policyRanges = toSignal(this.#insurerSrv.policyRanges.get$().pipe(filter(Boolean),
        //Implemented to work properly with the backend, we convert all 0's to nulls
        map(ranges => {
            if (ranges.length === 0) {
                return [{ from: 0, values: { fixed: null, percentage: null, min: null, max: null } }];
            } else {
                return ranges.map(range => ({
                    from: range.from === null ? 0 : range.from,
                    values: {
                        fixed: range.values.fixed === 0 ? null : range.values.fixed,
                        percentage: range.values.percentage === 0 ? null : range.values.percentage,
                        min: range.values.min === 0 ? null : range.values.min,
                        max: range.values.max === 0 ? null : range.values.max
                    }
                }));
            }
        }
        )
    ));

    readonly $isInProgress = toSignal(this.#insurerSrv.policyRanges.inProgress$());

    cancel(): void {
        this.#insurerSrv.policyRanges.load(this.$policy().insurer_id, this.$policy().id);
        this.form.markAsPristine();
    }

    save(): void {
        this.save$().subscribe({
            next: () => {
                this.#ephemeralSrv.showSaveSuccess();
            },
            error: (error: HttpErrorResponse) => {
                if (error.error.code === 'FIX_OR_PERCENTAGE_ERROR') {
                    const errorMessage = [];
                    errorMessage.push(error.error.message);
                    this.#messageDialogService.showAlert({
                        size: DialogSize.SMALL,
                        title: 'INSURERS.POLICIES.ADD_RESOURCE_ERROR.TITLE_' + error.error.code,
                        message: 'INSURERS.POLICIES.ADD_RESOURCE_ERROR.' + error.error.code,
                        subMessages: errorMessage
                    });
                }
            }
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const range = this.form.get('ranges')?.value as RangeElement[];

            return this.#insurerSrv.policyRanges.post(this.$policy().insurer_id, this.$policy().id, range)
                .pipe(tap(() => this.form.markAsPristine()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

}
