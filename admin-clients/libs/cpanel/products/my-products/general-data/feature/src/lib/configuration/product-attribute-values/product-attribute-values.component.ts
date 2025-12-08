import { ProductsService, ProductVariantsDialogAction } from '@admin-clients/cpanel/products/my-products/data-access';
import { ChangeDetectionStrategy, Component, inject, input, output, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map } from 'rxjs';
import { ProductAttributeValuesListComponent } from './product-attribute-values-list/product-attribute-values-list.component';

@Component({
    selector: 'app-product-attribute-values',
    imports: [
        MatDialogModule, MatDividerModule, MatIconModule,
        TranslatePipe, ReactiveFormsModule, FlexLayoutModule,
        ProductAttributeValuesListComponent
    ],
    templateUrl: './product-attribute-values.component.html',
    styleUrls: ['./product-attribute-values.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductAttributeValuesComponent {
    readonly #productsSrv = inject(ProductsService);

    readonly $productAttributes = toSignal(this.#productsSrv.product.attributesList.get$()
        .pipe(
            filter(Boolean),
            map(attributes => attributes.sort((a, b) => a.position - b.position))
        )
    );

    readonly $productId = toSignal(this.#productsSrv.product.get$().pipe(first(Boolean), map(product => product.product_id)));
    readonly $maxSelection = input.required<number>({ alias: 'maxSelection' });
    readonly $maxVariantsReached = input.required<boolean>({ alias: 'maxVariantsReached' });
    readonly $action = input.required<ProductVariantsDialogAction>({ alias: 'action' });
    readonly $disableAttributeValueCreation = signal<Map<number, boolean>>(null);

    readonly currentValues = new Map<number, number>();
    readonly disableAttributeValueCreation = new Map<number, boolean>();
    readonly currentValuesChanged = output<Map<number, number>>();
    readonly someActionDone = output<void>();

    updateValues(values: number, attributeId: number): void {
        this.currentValues.set(attributeId, values);

        //Only check when all the attributes are setted in the map (prevent unnecessary checks when first rendering the component)
        if (this.currentValues.size === this.$productAttributes()?.length) {
            for (const attributeId of this.currentValues.keys()) {
                this.disableAttributeValueCreation.set(
                    attributeId, !this.moreValuesAllowedForAttribute(attributeId, new Map(this.currentValues))
                );
                this.$disableAttributeValueCreation.set(this.disableAttributeValueCreation);
            }
        }

        this.currentValuesChanged.emit(this.currentValues);
    }

    //Checks for each attribute if we can add one more value to it
    moreValuesAllowedForAttribute(attributeId: number, currentValues: Map<number, number>): boolean {
        //Fakes a new attribute value for the attribute passed as parameter
        const attributeCurrentValues = currentValues.get(attributeId);
        currentValues.set(attributeId, attributeCurrentValues + 1);

        //With this new number of values, checks if we pass the max number of variants allowed
        const numberOfValuesCreated = Array.from(currentValues.values());
        const variantsToCreate = numberOfValuesCreated.reduce((acc, number) => acc * number, 1);
        //Returns if the creation of a new value doesn't result on a number of variants greater than allowed
        return variantsToCreate <= this.$maxSelection();
    }
}
