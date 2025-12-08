import { MAX_PRODUCT_ATTRIBUTES, MAX_VARIANTS, ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnDestroy, OnInit, signal, ViewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, firstValueFrom, map } from 'rxjs';
import { ProductAttributeValuesComponent } from '../product-attribute-values/product-attribute-values.component';
import { ProductAttributesComponent } from './product-attributes/product-attributes.component';

@Component({
    selector: 'app-create-product-variants-dialog',
    imports: [
        AsyncPipe,
        MatDialogModule, MatIconModule, MatProgressSpinnerModule, MatButtonModule,
        TranslatePipe, FlexLayoutModule,
        WizardBarComponent, ProductAttributesComponent, ProductAttributeValuesComponent
    ],
    templateUrl: './create-product-variants-dialog.component.html',
    styleUrls: ['./create-product-variants-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreateProductVariantsDialogComponent implements OnInit, OnDestroy {
    readonly #dialogRef = inject(MatDialogRef<CreateProductVariantsDialogComponent>);
    readonly #productsSrv = inject(ProductsService);

    @ViewChild(WizardBarComponent, { static: true }) private readonly _wizardBar: WizardBarComponent;

    readonly isInProgress$ = booleanOrMerge([
        this.#productsSrv.product.attributesList.loading$(),
        this.#productsSrv.product.attribute.loading$()
    ]);

    readonly $currentAttributes = toSignal(this.#productsSrv.product.attributesList.get$()
        .pipe(
            filter(Boolean),
            map(attributes => attributes.length)
        ), { initialValue: 0 }
    );

    readonly $variantsToCreate = signal(0);
    readonly $minValuesPerAttribute = signal(true);

    readonly $disableNextStepButton = computed(() => {
        const currentStep = this.$currentStep();
        const currentAttributes = this.$currentAttributes();
        const minValuesPerAttribute = this.$minValuesPerAttribute();
        if (currentStep === 0) {
            return currentAttributes === 0;
        } else {
            return !minValuesPerAttribute;
        }
    });

    readonly steps = ['PRODUCT.VARIANTS.PRODUCT_ATTRIBUTES', 'PRODUCT.VARIANTS.ATTRIBUTE_VALUES'];
    readonly maxAttributesSelection = MAX_PRODUCT_ATTRIBUTES;
    readonly maxVariantsFromValuesCombination = MAX_VARIANTS;
    readonly $currentStep = signal(0);
    readonly $maxVariantsReached = computed(() => this.$variantsToCreate() === this.maxVariantsFromValuesCombination);

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.LARGE);
        this.#dialogRef.disableClose = false;
        this.#productsSrv.product.get$()
            .pipe(first(Boolean))
            .subscribe(product => this.#productsSrv.product.attributesList.load(product.product_id));
    }

    ngOnDestroy(): void {
        this.#productsSrv.product.attributesList.clear();
    }

    close(variantsCreated = false): void {
        this.#dialogRef.close(variantsCreated);
    }

    goToStep(step: number): void {
        this.$currentStep.set(step);
        this._wizardBar.setActiveStep(step);
    }

    async nextStep(): Promise<void> {
        if (this.$currentStep() < this.steps.length - 1) {
            this.goToStep(this.$currentStep() + 1);
        } else {
            const product = await firstValueFrom(this.#productsSrv.product.get$());
            const productId = product.product_id;
            this.#productsSrv.product.variants.create(productId).subscribe(() => this.close(true));
        }
    }

    currentValuesChanged(mappedAttributeValues: Map<number, number>): void {
        //Each attribute must have at least two values
        const numberOfValuesCreated = Array.from(mappedAttributeValues.values());
        this.$minValuesPerAttribute.set(numberOfValuesCreated.every(values => values >= MAX_PRODUCT_ATTRIBUTES));

        //Check number of variants that will be created
        const variantsToCreate = numberOfValuesCreated.reduce((acc, number) => acc * number, 1);
        this.$variantsToCreate.set(variantsToCreate);
    }
}
