import { CustomerTypeAssignation } from '@admin-clients/cpanel/promoters/shared/data-access';
import { EntityCustomerType } from '@admin-clients/shared/common/data-access';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, computed, effect, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule,
        TranslatePipe,
        MatFormFieldModule,
        MatSelectModule,
        MatCheckbox,
        PrefixPipe
    ],
    styles: `
        .form-box {
            background-color: var(--ob-theme-color-bg-1);
            padding: 24px;
        }
    `,
    selector: 'app-customer-types-assignation',
    templateUrl: './customer-types-assignation.component.html'
})
export class CustomerTypesAssignationComponent implements OnInit {
    readonly #fb = inject(FormBuilder);

    readonly $form = input.required<UntypedFormGroup>({ alias: 'form' });;
    readonly $customerTypesAssigned = input.required<CustomerTypeAssignation[]>({ alias: 'customerTypesAssigned' });
    readonly $customerTypes = input.required<EntityCustomerType[]>({ alias: 'customerTypes' });

    readonly $automaticCustomerTypesWithBuy = computed(() =>
        this.$customerTypes()?.filter(customerType =>
            !!customerType.triggers?.find(trigger => trigger.trigger === 'PURCHASE')?.selected) ?? []);

    readonly customerTypesAssignationForm = this.#fb.group({
        customerTypesToAdd: [null as number[]],
        customerTypesToRemoveEnabled: [false],
        customerTypesToRemove: [null as number[], [Validators.required]]
    });

    constructor() {
        this.#handleCustomerTypesToRemoveEnabledChange();
        effect(() => this.#hanldeCustomerTypesAssignedChange(this.$customerTypesAssigned()));
    }

    ngOnInit(): void {
        this.$form()?.addControl('customerTypesAssignation', this.customerTypesAssignationForm, { emitEvent: false });
    }

    #hanldeCustomerTypesAssignedChange(customerTypesAssigned: CustomerTypeAssignation[]): void {
        const customerTypesToAdd = [];
        const customerTypesToRemove = [];

        customerTypesAssigned?.forEach(customerType => {
            if (customerType.mode === 'ADD') customerTypesToAdd.push(customerType.customer_type.id);
            else customerTypesToRemove.push(customerType.customer_type.id);
        });

        this.customerTypesAssignationForm.patchValue({
            customerTypesToAdd,
            customerTypesToRemove,
            customerTypesToRemoveEnabled: !!customerTypesToRemove.length
        });
    }

    #handleCustomerTypesToRemoveEnabledChange(): void {
        this.customerTypesAssignationForm.controls.customerTypesToRemoveEnabled.valueChanges.pipe(
            takeUntilDestroyed()
        ).subscribe(enabled => {
            if (enabled) {
                this.customerTypesAssignationForm.controls.customerTypesToRemove.enable();
            } else {
                this.customerTypesAssignationForm.controls.customerTypesToRemove.disable();
            }
        });
    }
}
