import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { customerTypeRestrictions, CustomerTypeRestrictions, EntitiesService, EntityCustomerTypeRestriction } from '@admin-clients/cpanel/organizations/entities/data-access';
import { EntityCustomerType } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, ElementRef, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, take } from 'rxjs';

@Component({
    selector: 'app-create-update-entity-custom-types-restrictions-dialog',
    imports: [
        MatIconModule, TranslatePipe, ReactiveFormsModule, MatFormFieldModule, FormControlErrorsComponent,
        MatProgressSpinner, MatDialogModule, MatButtonModule, MatInputModule, FlexLayoutModule,
        MatTooltipModule, MatSelectModule, MatOptionModule
    ],
    templateUrl: './create-update-entity-customer-type-restrictions-dialog.component.html',
    styleUrl: './create-update-entity-customer-type-restrictions-dialog.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreateUpdateEntityCustomerTypeRestrictionsDialogComponent {
    readonly #dialogRef = inject(MatDialogRef);
    readonly #fb = inject(FormBuilder);
    readonly #elementRef = inject(ElementRef);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #ephemeralService = inject(EphemeralMessageService);
    readonly #$entity = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean), take(1)));
    readonly #data = inject<{
        restriction: EntityCustomerTypeRestriction;
        allCustomerTypes: EntityCustomerType[];
        allRestrictions: EntityCustomerTypeRestriction[];
    }>(MAT_DIALOG_DATA);

    readonly form = this.#fb.group({
        key: [null as CustomerTypeRestrictions, Validators.required],
        restricted_customer_types: [[] as number[], [Validators.required]]
    });

    readonly $isLoading = toSignal(this.#entitiesSrv.entityCustomerTypesRestrictions.inProgress$());
    readonly actionMode: 'CREATE' | 'UPDATE';
    readonly customerTypes = this.#data?.allCustomerTypes;
    restrictionTypes: CustomerTypeRestrictions[] = [...customerTypeRestrictions];

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
        if (this.#data?.restriction) {
            this.actionMode = 'UPDATE';
            this.form.patchValue(this.#data.restriction);
            this.form.controls.key.disable();
        } else {
            this.actionMode = 'CREATE';
            this.restrictionTypes = customerTypeRestrictions.filter(ctRestriction => !this.#data?.allRestrictions
                .map(restr => restr.key).includes(ctRestriction));
        }
    }

    createOrUpdateCustomerTypesRestriction(): void {
        if (this.form.valid) {
            const body = {
                entity_id: this.#$entity().id,
                restrictions: [
                    ...this.#data.allRestrictions.filter(restr => restr.key !== this.form.getRawValue().key),
                    this.form.getRawValue()
                ]
            };

            this.#entitiesSrv.entityCustomerTypesRestrictions.save(body.entity_id, body).subscribe(() => {
                this.#ephemeralService.showSuccess({
                    msgKey: this.actionMode === 'CREATE' ?
                        'ENTITY.CUSTOMER_TYPE.RESTRICTIONS.CREATE_SUCCESS' : 'ENTITY.CUSTOMER_TYPE.RESTRICTIONS.SAVE_SUCCESS'
                });
                this.close(true);
            });

        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elementRef.nativeElement);
        }
    }

    close(success?: boolean): void {
        this.#dialogRef.close(success);
    }

}
