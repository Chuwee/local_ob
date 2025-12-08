import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { FormsComponent } from '@admin-clients/cpanel/common/feature/forms';
import { FormsField } from '@admin-clients/cpanel/common/utils';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, Observable, shareReplay, switchMap, tap, throwError } from 'rxjs';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
    selector: 'app-entity-payout-data',
    templateUrl: './entity-payout-data.component.html',
    imports: [AsyncPipe, FormContainerComponent, ReactiveFormsModule, TranslatePipe, FormsComponent, MatProgressSpinner],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityPayoutDataComponent implements OnDestroy {
    readonly #entitySrv = inject(EntitiesService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);

    #entity: Entity;
    readonly entity$ = this.#entitySrv.getEntity$()
        .pipe(
            filter(Boolean),
            tap(entity => this.#entity = entity)
        );

    readonly payoutForm = this.#fb.group({});
    readonly $isInProgress = toSignal(this.#entitySrv.payoutForm.inProgress$());
    readonly payoutForm$ = this.entity$
        .pipe(
            first(Boolean),
            switchMap(entity => {
                this.#entitySrv.payoutForm.load(entity.id);
                return this.#entitySrv.payoutForm.get$();
            }),
            shareReplay(1)
        );

    ngOnDestroy(): void {
        this.#entitySrv.payoutForm.clear();
    }

    save$(): Observable<unknown> {
        if (this.payoutForm.valid && this.payoutForm.dirty) {
            return this.#saveForm$(this.#entity).pipe(
                tap(() => this.#ephemeralMessageService.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' }))
            );
        } else {
            this.payoutForm.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'Invalid form');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    cancel(): void {
        this.entity$.pipe(first()).subscribe(entity => {
            this.#entitySrv.payoutForm.load(entity.id);
        });
    }

    #getFormValues(form: FormsField[][], formsValue: FormsField[]): FormsField[][] {
        return form?.map(formField => formField.map(field =>
            formsValue.find(formField => formField.key === field.key)));
    }

    #saveForm$(entity: Entity): Observable<void> {
        return this.payoutForm$.pipe(
            first(),
            switchMap(payoutForm => {
                const value = this.payoutForm.getRawValue()[0];
                const payoutData = this.#getFormValues(payoutForm, value);
                return this.#entitySrv.payoutForm.update(entity.id, payoutData);
            }),
            tap(() => this.#entitySrv.payoutForm.load(entity.id))
        );
    }
}

