
import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Collective, CollectivesService, CollectiveStatus } from '@admin-clients/cpanel/collectives/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    productPromotionActivatorTypes, ProductPromotion, ProductPromotionActivatorType, ProductsService
} from '@admin-clients/cpanel/products/my-products/data-access';
import { ProductPromotionFieldRestrictions } from '@admin-clients/cpanel-products-my-products-events-data-access';
import { EphemeralMessageService, SelectServerSearchComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, QueryList, ViewChildren } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, firstValueFrom, combineLatestWith, map } from 'rxjs';

@Component({
    selector: 'app-product-promotion-general-data',
    templateUrl: './product-promotion-general-data.component.html',
    styleUrls: ['./product-promotion-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, ReactiveFormsModule, MatExpansionModule, MatFormFieldModule,
        TranslatePipe, FormControlErrorsComponent, MatInputModule, MatCheckboxModule,
        FlexLayoutModule, MatProgressSpinnerModule, SelectServerSearchComponent,
        MatTooltipModule, MatIconModule, MatRadioModule, MatSelectModule, EllipsifyDirective
    ]
})
export class ProductPromotionGeneralDataComponent {
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanels: QueryList<MatExpansionPanel>;

    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly #productsSrv = inject(ProductsService);
    readonly #collectivesSrv = inject(CollectivesService);
    readonly #authService = inject(AuthenticationService);
    readonly #destroyRef = inject(DestroyRef);

    readonly fieldRestrictions = ProductPromotionFieldRestrictions;
    readonly activatorType = productPromotionActivatorTypes;

    readonly $loading = toSignal(this.#productsSrv.product.promotion.loading$());
    readonly $product = toSignal(this.#productsSrv.product.get$());

    readonly isOperator$ = this.#authService.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS]);
    readonly collectives$ = this.#collectivesSrv.getCollectivesListData$().pipe(filter(Boolean));

    readonly form = this.#fb.group({
        name: [null as string, [
            Validators.required,
            Validators.minLength(this.fieldRestrictions.minNameLength),
            Validators.maxLength(this.fieldRestrictions.maxNameLength)
        ]],
        activator: this.#fb.group({
            type: [null as ProductPromotionActivatorType, [Validators.required]],
            collective: [null as Partial<Collective>]
        })
    });

    readonly $promotion = toSignal(this.#productsSrv.product.promotion.get$().pipe(
        filter(Boolean),
        combineLatestWith(this.collectives$),
        map(([promotion]) => {
            if (this.form.pristine) {
                this.#updateForm(promotion);
            }
            return promotion;
        })
    ));

    constructor() {
        this.loadCollectives();
        this.#handleActivatorTypeChanges();
    }

    cancel(): void {
        this.form.markAsPristine();
        this.#loadPromotion();
    }

    save(): void {
        if (this.form.valid) {
            const reqBody = {
                name: this.form.value.name,
                activator: {
                    type: this.form.value.activator.type,
                    ...(this.form.value.activator.type === this.activatorType.collective && {
                        id: this.form.value.activator.collective?.id
                    })
                }
            };

            this.#productsSrv.product.promotion.update(this.$product().product_id, this.$promotion().id, reqBody)
                .subscribe({
                    next: () => {
                        this.form.markAsPristine();
                        this.#loadPromotionsList();
                        this.#loadPromotion();
                        this.#ephemeralMsgSrv.showSaveSuccess();
                    }
                });
        } else {
            this.#showValidationErrors();
        }
    }

    async loadCollectives(q: string = null): Promise<void> {
        const product = this.$product();
        const isOperator = await firstValueFrom(this.isOperator$);

        this.#collectivesSrv.loadCollectivesList({
            entity_id: isOperator ? product.entity.id : undefined,
            status: CollectiveStatus.active,
            limit: 50,
            sort: 'name:asc',
            q
        });
    }

    #handleActivatorTypeChanges(): void {
        this.form.controls.activator.controls.type.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(type => {
                const collectiveControl = this.form.controls.activator.controls.collective;
                if (type === this.activatorType.collective) {
                    collectiveControl.setValidators([Validators.required]);
                } else {
                    collectiveControl.clearValidators();
                    collectiveControl.setValue(null, { emitEvent: false });
                }
                collectiveControl.updateValueAndValidity({ emitEvent: false });
            });
    }

    #updateForm(promotion: ProductPromotion): void {
        this.form.reset({
            name: promotion.name,
            activator: {
                type: promotion.activator?.type,
                collective: promotion.activator?.collective?.id && promotion.activator?.collective
            }
        });
        this.form.markAsPristine();
    }

    #loadPromotion(): void {
        this.#productsSrv.product.promotion.load(this.$product().product_id, this.$promotion().id);
    }

    #loadPromotionsList(): void {
        this.#productsSrv.product.promotionList.load(this.$product().product_id, {
            limit: 999, offset: 0, sort: 'name:asc'
        });
    }

    #showValidationErrors(): void {
        this.form.markAllAsTouched();
        scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
    }
}
