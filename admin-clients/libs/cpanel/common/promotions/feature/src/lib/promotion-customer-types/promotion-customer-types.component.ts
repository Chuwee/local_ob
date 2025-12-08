import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { BasePromotion } from '@admin-clients/cpanel/promoters/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { HelpButtonComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, DestroyRef, effect, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDivider } from '@angular/material/divider';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { TranslateModule } from '@ngx-translate/core';
import { filter, map } from 'rxjs';

type CustomerTypesCondition = BasePromotion['applicable_conditions']['customer_types_condition'];

type CustomerTypesForm = {
    type: FormControl<CustomerTypesCondition['type']>;
    selected: FormControl<number[]>;
};

@Component({
    selector: 'app-promotion-customer-types',
    templateUrl: './promotion-customer-types.component.html',
    styleUrls: ['./promotion-customer-types.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: true,
    imports: [TranslateModule, ReactiveFormsModule, MatRadioButton, MatRadioGroup, MatSelectModule,
        MatDivider, HelpButtonComponent, FormControlErrorsComponent
    ]
})
export class PromotionCustomerTypesComponent implements OnInit {

    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #destroyRef = inject(DestroyRef);

    readonly $customerTypesForm = input.required<FormGroup<CustomerTypesForm>>({ alias: 'customerTypesForm' });
    readonly $promotion = input.required<BasePromotion>({ alias: 'promotion' });

    readonly entity$ = this.#entitiesService.getEntity$().pipe(filter(Boolean));
    readonly $customerTypes = toSignal(this.#entitiesService.entityCustomerTypes.get$().pipe(map(cts => cts ?? [])));

    constructor() {
        effect(() => {
            const promotionCustomerTypes = this.$promotion()?.applicable_conditions?.customer_types_condition || null;
            if (this.$customerTypes()?.length) {
                this.$customerTypesForm()?.enable();
                if (promotionCustomerTypes) this.#patchFormValues(promotionCustomerTypes);
                if (promotionCustomerTypes?.type === 'RESTRICTED') this.$customerTypesForm()?.controls.selected.enable();
                else this.$customerTypesForm()?.controls.selected.disable();
            } else {
                this.$customerTypesForm()?.disable();
            }
        });
    }

    ngOnInit(): void {
        this.#handleTypeChange();
        this.#loadCustomerTypes();
    }

    #loadCustomerTypes(): void {
        this.entity$.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(entity => {
            this.#entitiesService.entityCustomerTypes.load(entity.id);
        });
    }

    #handleTypeChange(): void {
        this.$customerTypesForm()?.controls.type.valueChanges.pipe(
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(type => {
            if (type === 'ALL' || !type) {
                this.$customerTypesForm()?.controls.selected.disable();
            } else if (type === 'RESTRICTED') {
                this.$customerTypesForm()?.controls.selected.enable();
            }
        });
    }

    #patchFormValues(promoCustomerTypes: CustomerTypesCondition): void {
        this.$customerTypesForm()?.patchValue({
            type: promoCustomerTypes.type,
            selected: promoCustomerTypes?.customer_types?.map(ct => ct.id) || []
        });
    }
}