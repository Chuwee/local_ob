import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { CustomerForms, EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, ViewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable, shareReplay, tap, throwError } from 'rxjs';
import { EntityCustomerFormsComponent } from '../customer-forms/entity-customer-forms.component';

@Component({
    selector: 'app-entity-customer-content-admin',
    templateUrl: './entity-customer-content-admin.component.html',
    imports: [TranslatePipe, MaterialModule, AsyncPipe, FormContainerComponent, EntityCustomerFormsComponent, ReactiveFormsModule],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityCustomerContentAdminComponent implements OnDestroy {
    @ViewChild('customerForms') readonly customerForms: EntityCustomerFormsComponent;

    readonly #entitySrv = inject(EntitiesService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);

    #adminPayload: CustomerForms;
    readonly #$entityId = toSignal(this.#entitySrv.getEntity$().pipe(
        filter(Boolean),
        map(entity => entity.id),
        tap(id => this.#entitySrv.customerAdminForm.load(id)))
    );

    readonly form = this.#fb.group({});
    readonly $isInProgress = toSignal(this.#entitySrv.customerAdminForm.inProgress$());
    readonly adminForms$ = this.#entitySrv.customerAdminForm.get$().pipe(
        filter(Boolean),
        takeUntilDestroyed(),
        tap(customerForms => this.#adminPayload = customerForms),
        shareReplay(1)
    );

    ngOnDestroy(): void {
        this.#entitySrv.customerAdminForm.clear();
    }

    save(): void {
        this.#save$().subscribe(_ => {
            this.#entitySrv.customerAdminForm.load(this.#$entityId());
            this.#ephemeralMessageService.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' });
        });
    }

    cancel(): void {
        this.#entitySrv.customerAdminForm.load(this.#$entityId());
    }

    #save$(): Observable<unknown> {
        if (this.form.valid && this.form.dirty) {
            const formsValue = this.customerForms.getCustomerFormsValue(this.#adminPayload);

            return this.#entitySrv.customerAdminForm.update(this.#$entityId(), formsValue);
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'Invalid form');
        }
    }
}
