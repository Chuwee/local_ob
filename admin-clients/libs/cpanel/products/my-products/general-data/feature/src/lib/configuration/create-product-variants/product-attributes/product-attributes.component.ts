import { ProductsService, ProductAttribute, MAX_PRODUCT_ATTRIBUTES } from '@admin-clients/cpanel/products/my-products/data-access';
import { booleanOrMerge, noDuplicateValuesValidatorForm } from '@admin-clients/shared/utility/utils';
import { CdkDrag, CdkDragDrop, CdkDropList, moveItemInArray } from '@angular/cdk/drag-drop';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormArray, FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, finalize, firstValueFrom, forkJoin, map, Observable, shareReplay, take } from 'rxjs';

@Component({
    selector: 'app-product-attributes',
    imports: [
        AsyncPipe,
        MatDialogModule, MatTooltipModule, MatListModule, MatIconModule, MatButtonModule, MatFormFieldModule, MatInputModule,
        TranslatePipe, ReactiveFormsModule, CdkDropList, CdkDrag,
        FlexLayoutModule
    ],
    templateUrl: './product-attributes.component.html',
    styleUrls: ['./product-attributes.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductAttributesComponent {
    readonly #fb = inject(FormBuilder);
    readonly #productsSrv = inject(ProductsService);

    readonly isInProgress$ = booleanOrMerge([
        this.#productsSrv.product.attributesList.loading$(),
        this.#productsSrv.product.attribute.loading$()
    ]);

    readonly productAttributesForm = this.#fb.group({ attributesList: this.#fb.array<string[]>([]) });
    readonly newProductAttribute = new FormControl<string>('', [noDuplicateValuesValidatorForm(this.attributesList)]);

    readonly productAttributes$ = this.#productsSrv.product.attributesList.get$()
        .pipe(
            filter(Boolean),
            map(attributes => {
                const sortedAttributes = attributes.sort((a, b) => a.position - b.position);
                this.mapAttributesToControls(sortedAttributes);
                return sortedAttributes;
            }),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly $currentAttributes = toSignal(this.#productsSrv.product.attributesList.get$()
        .pipe(
            filter(Boolean),
            map(attributes => {
                attributes.length === MAX_PRODUCT_ATTRIBUTES
                    ? this.newProductAttribute.disable()
                    : this.newProductAttribute.enable();
                return attributes.length;
            })
        ), { initialValue: 0 });

    readonly $maxSelection = input.required<number>({ alias: 'maxSelection' });

    get attributesList(): FormArray<FormControl<string>> {
        return this.productAttributesForm.controls.attributesList;
    }

    async updateAttribute(arrayIndex: number): Promise<void> {
        const ctrl = this.attributesList.get(arrayIndex.toString());
        if (ctrl.valid && ctrl.dirty) {
            const product = await firstValueFrom(this.#productsSrv.product.get$());
            const productId = product.product_id;
            const attributes = await firstValueFrom(this.#productsSrv.product.attributesList.get$());
            const attributeToUpdate = attributes[arrayIndex];

            const request = { name: ctrl.value };
            this.#productsSrv.product.attribute.update(productId, attributeToUpdate.attribute_id, request)
                .subscribe(() => this.#productsSrv.product.attributesList.load(productId));
        }
    }

    async addNewAttribute(): Promise<void> {
        const name = this.newProductAttribute.value;
        if (this.newProductAttribute.valid) {
            const product = await firstValueFrom(this.#productsSrv.product.get$());
            const productId = product.product_id;
            const attributes = await firstValueFrom(this.#productsSrv.product.attributesList.get$());
            const position = attributes.length ? attributes[attributes.length - 1].position + 1 : 0;

            this.#productsSrv.product.attribute.create(productId, { name, position })
                .subscribe(() => {
                    this.#productsSrv.product.attributesList.load(productId);
                    this.newProductAttribute.reset();
                });
        } else {
            this.newProductAttribute.markAsTouched();
        }
    }

    async deleteAttribute(attributeId: number): Promise<void> {
        const product = await firstValueFrom(this.#productsSrv.product.get$());
        const productId = product.product_id;
        this.#productsSrv.product.attribute.delete(productId, attributeId)
            .subscribe(() => this.#productsSrv.product.attributesList.load(productId));
    }

    async onListDrop(event: CdkDragDrop<ProductAttribute[]>): Promise<void> {
        const isLoading = await firstValueFrom(this.isInProgress$);
        const positionHasChanged = event.currentIndex !== event.previousIndex;

        if (this.attributesList.valid && !isLoading && this.attributesList.length > 1 && positionHasChanged) {
            const obs$ = [] as Observable<void>[];
            const product = await firstValueFrom(this.#productsSrv.product.get$());
            const productId = product.product_id;
            const productAttributes = await firstValueFrom(this.productAttributes$);

            moveItemInArray(productAttributes, event.previousIndex, event.currentIndex);
            this.mapAttributesToControls(productAttributes); //Necessary for updating the formArray and the view

            productAttributes.forEach((productAttribute, index) => {
                const request = { position: index };
                obs$.push(this.#productsSrv.product.attribute.update(productId, productAttribute.attribute_id, request));
            });

            forkJoin(obs$)
                .pipe(
                    take(1),
                    finalize(() => this.#productsSrv.product.attributesList.load(productId))
                )
                .subscribe();
        }
    }

    private mapAttributesToControls(attributes: ProductAttribute[]): void {
        this.attributesList.clear({ emitEvent: false });
        attributes.forEach(attribute => {
            const attributeCtrl = this.#fb.control(attribute.name, {
                updateOn: 'blur',
                validators: [Validators.required, noDuplicateValuesValidatorForm(this.attributesList)]
            });
            this.attributesList.push(attributeCtrl, { emitEvent: false });
        });
    }
}
