import { MAX_VARIANTS, ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { first } from 'rxjs';
import { ProductAttributeValuesComponent } from '../product-attribute-values/product-attribute-values.component';

@Component({
    selector: 'app-edit-product-variants-dialog',
    imports: [
        AsyncPipe,
        MatDialogModule, MatIconModule, MatProgressSpinnerModule, MatButtonModule,
        TranslatePipe, FlexLayoutModule,
        ProductAttributeValuesComponent
    ],
    templateUrl: './edit-product-variants-dialog.component.html',
    styleUrls: ['./edit-product-variants-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditProductVariantsDialogComponent implements OnInit, OnDestroy {
    readonly #dialogRef = inject(MatDialogRef<EditProductVariantsDialogComponent>);
    readonly #productsSrv = inject(ProductsService);

    readonly isInProgress$ = this.#productsSrv.product.attributesList.loading$();
    readonly $variantsToCreate = signal(0);
    readonly maxVariantsFromValuesCombination = MAX_VARIANTS;
    readonly $maxVariantsReached = computed(() => this.$variantsToCreate() === this.maxVariantsFromValuesCombination);

    someActionDone = false;

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

    close(): void {
        this.#dialogRef.close(this.someActionDone);
    }

    currentValuesChanged(mappedAttributeValues: Map<number, number>): void {
        const numberOfValuesCreated = Array.from(mappedAttributeValues.values());
        //Check number of variants that will be created
        const variantsToCreate = numberOfValuesCreated.reduce((acc, number) => acc * number, 1);
        this.$variantsToCreate.set(variantsToCreate);
    }
}
