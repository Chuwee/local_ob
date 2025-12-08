import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { CustomerTypeTrigger, EntityCustomerType } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, HelpButtonComponent, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { atLeastOneRequiredInFormGroup, noWhitespaceValidator } from '@admin-clients/shared/utility/utils';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatError, MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter } from 'rxjs';

@Component({
    selector: 'app-create-update-entity-custom-type-dialog',
    imports: [MatIconModule, TranslatePipe, ReactiveFormsModule, MatFormFieldModule, FormControlErrorsComponent,
        MatSpinner, MatDialogModule, MatButtonModule, MatInputModule, MatTooltipModule, HelpButtonComponent,
        MatRadioModule, MatCheckboxModule, UpperCasePipe, MatError
    ],
    templateUrl: './create-update-entity-customer-type-dialog.component.html',
    styleUrl: './create-update-entity-customer-type-dialog.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreateUpdateEntityCustomerTypeDialogComponent {
    readonly #dialogRef = inject(MatDialogRef);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #fb = inject(FormBuilder);
    readonly #elementRef = inject(ElementRef);
    readonly #data = inject<{ customerType: EntityCustomerType; allCustomerTypes: EntityCustomerType[] }>(MAT_DIALOG_DATA);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #ephemeralService = inject(EphemeralMessageService);
    readonly #$entity = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean)));

    readonly actionMode: 'CREATE' | 'UPDATE';
    readonly customerType = this.#data?.customerType;
    readonly customerTypeTriggers = Object.values(this.customerType?.triggers || {}).filter(triger => triger.selected);

    readonly $isLoading = toSignal(this.#entitiesSrv.entityCustomerTypes.inProgress$());

    readonly form = this.#fb.group({
        name: [null as string, Validators.required],
        code: [null as string, [Validators.required, noWhitespaceValidator()]],
        assignation_type: [null as EntityCustomerType['assignation_type'], [Validators.required]],
        triggers: this.#fb.group({
            registration: [false],
            login: [false],
            purchase: [false]
        }, { validators: [atLeastOneRequiredInFormGroup('required')] })
    });

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
        if (this.#data?.customerType) {
            this.actionMode = 'UPDATE';
            this.form.patchValue({
                ...this.#data.customerType,
                ...(this.#data.customerType.triggers && {
                    triggers: this.#data.customerType.triggers?.reduce((acc, trigger) => {
                        acc[trigger.trigger.toLowerCase()] = trigger.selected || false;
                        return acc;
                    }, {})
                })
            });
            this.form.controls.code.disable();
            this.form.controls.assignation_type.disable();
            this.form.controls.triggers.disable();
        } else {
            this.actionMode = 'CREATE';
            this.#handleAssignationTypeChange();
        }
    }

    createOrUpdateCustomerType(): void {
        const isCustomerTypeUnique = this.#checkCustomerTypeUnique();
        if (this.form.valid && isCustomerTypeUnique) {
            const entityId = this.#$entity().id;
            const formValue = this.form.getRawValue();

            const body = {
                name: formValue.name,
                code: formValue.code,
                assignation_type: formValue.assignation_type,
                triggers: Object.entries(formValue.triggers)
                    .filter(([, value]) => value)
                    .map(([key]) => key.toUpperCase() as CustomerTypeTrigger)
            };

            const action = (this.actionMode === 'CREATE')
                ? this.#entitiesSrv.entityCustomerTypes.create(entityId, body)
                : this.#entitiesSrv.entityCustomerTypes.update(entityId, this.#data.customerType.id, body);

            action.subscribe(() => {
                this.#ephemeralService.showSuccess({
                    msgKey: this.actionMode === 'CREATE' ? 'ENTITY.CUSTOMER_TYPES.CREATE_SUCCESS' : 'ENTITY.CUSTOMER_TYPES.SAVE_SUCCESS'
                });
                this.close(true);
            });

        } else {
            if (!isCustomerTypeUnique) {
                this.#msgDialogService.showAlert({
                    title: 'ENTITY.CUSTOMER_TYPES.UNIQUE_ERROR_TITLE',
                    message: 'ENTITY.CUSTOMER_TYPES.UNIQUE_ERROR_DESCRIPTION'
                });
            } else {
                this.form.markAllAsTouched();
                this.form.patchValue(this.form.value);
                scrollIntoFirstInvalidFieldOrErrorMsg(this.#elementRef.nativeElement);
            }
        }
    }

    close(success?: boolean): void {
        this.#dialogRef.close(success);
    }

    #checkCustomerTypeUnique(): boolean {
        const formValues = this.form.getRawValue();
        return !this.#data.allCustomerTypes
            ?.some(customType =>
                (customType.code?.trim() === formValues.code?.trim() || customType.name?.trim() === formValues.name?.trim())
                && this.#data.customerType?.id !== customType?.id);
    }

    #handleAssignationTypeChange(): void {
        this.form.controls.assignation_type.valueChanges.pipe(
            takeUntilDestroyed()
        ).subscribe(value => {
            if (value === 'AUTOMATIC') this.form.controls.triggers.enable();
            else this.form.controls.triggers.disable();
        });
    }

}
