import {
    MIN_ATTRIBUTE_VALUES,
    ProductAttribute, ProductAttributeValue, ProductsService, ProductsState,
    ProductVariantsDialogAction
} from '@admin-clients/cpanel/products/my-products/data-access';
import { booleanOrMerge, noDuplicateValuesValidatorForm } from '@admin-clients/shared/utility/utils';
import { CdkDropList, CdkDrag, CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, input, OnInit, output, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormArray, FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, finalize, firstValueFrom, forkJoin, map, Observable, take } from 'rxjs';

@Component({
    selector: 'app-product-attribute-values-list',
    imports: [
        AsyncPipe,
        MatProgressSpinnerModule, MatIconModule, MatButtonModule, MatListModule, MatTooltipModule, MatInputModule,
        TranslatePipe, ReactiveFormsModule, FlexLayoutModule, CdkDropList, CdkDrag
    ],
    templateUrl: './product-attribute-values-list.component.html',
    styleUrls: ['./product-attribute-values-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        ProductsService, ProductsState
    ]
})
export class ProductAttributeValuesListComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #productsSrv = inject(ProductsService);

    readonly attributeValuesForm = this.#fb.group({ valuesList: this.#fb.array<string[]>([]) });
    readonly newAttributeValue = new FormControl<string>('', [noDuplicateValuesValidatorForm(this.valuesList)]);
    readonly $productId = input.required<number>({ alias: 'productId' });
    readonly $attribute = input.required<ProductAttribute>({ alias: 'attribute' });
    readonly $maxVariantsReached = input.required<boolean>({ alias: 'maxVariantsReached' });
    readonly $disableValueCreation = input.required<boolean>({ alias: 'disableValueCreation' });
    readonly $maxSelection = input.required<number>({ alias: 'maxSelection' });
    readonly $action = input.required<ProductVariantsDialogAction>({ alias: 'action' });
    readonly $attributeValues = signal<ProductAttributeValue[]>([]);
    readonly $invalidList = signal(false);
    readonly $disableNewValueInput = computed(() => {
        const disable = this.$attributeValues() && (this.$maxVariantsReached() || this.$disableValueCreation());
        disable ? this.newAttributeValue.disable() : this.newAttributeValue.enable();
        return disable;
    });

    readonly currentValuesList = output<number>();
    readonly someActionDone = output<void>();

    readonly minValues = MIN_ATTRIBUTE_VALUES;

    readonly isInProgress$ = booleanOrMerge([
        this.#productsSrv.product.attributeValuesList.loading$(),
        this.#productsSrv.product.attributeValue.loading$()
    ]);

    get valuesList(): FormArray<FormControl<string>> {
        return this.attributeValuesForm.controls.valuesList;
    }

    ngOnInit(): void {
        this.#productsSrv.product.attributeValuesList.load(this.$productId(), this.$attribute().attribute_id);
        this.#productsSrv.product.attributeValuesList.getMetadata$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(metadata => {
                metadata.total < this.minValues ? this.$invalidList.set(true) : this.$invalidList.set(false);
                this.currentValuesList.emit(metadata.total);
            });
        this.#productsSrv.product.attributeValuesList.getData$()
            .pipe(
                filter(Boolean),
                map(values => {
                    const sortedValues = values.sort((a, b) => a.position - b.position);
                    this.mapValuesToControls(sortedValues);
                    return sortedValues;
                }),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(values => this.$attributeValues.set(values));
    }

    addNewValue(): void {
        if (this.newAttributeValue.valid) {
            const attributeId = this.$attribute().attribute_id;
            const position = this.$attributeValues().length
                ? this.$attributeValues()[this.$attributeValues().length - 1].position + 1
                : 0;
            const request = {
                name: this.newAttributeValue.value,
                position
            };
            this.#productsSrv.product.attributeValue.create(this.$productId(), attributeId, request)
                .subscribe(response => {
                    this.#productsSrv.product.attributeValuesList.load(this.$productId(), attributeId);
                    this.newAttributeValue.reset();
                    this.scrollToNewValue(response.id);
                    this.someActionDone.emit();
                });
        } else {
            this.newAttributeValue.markAllAsTouched();
        }
    }

    updateValue(arrayIndex: number): void {
        const ctrl = this.valuesList.get(arrayIndex.toString());
        if (ctrl.valid && ctrl.dirty) {
            const attributeId = this.$attribute().attribute_id;
            const attributeValue = this.$attributeValues()[arrayIndex];
            const request = { name: ctrl.value };
            this.#productsSrv.product.attributeValue.update(this.$productId(), attributeId, attributeValue.value_id, request)
                .subscribe(() => {
                    this.#productsSrv.product.attributeValuesList.load(this.$productId(), attributeId);
                    this.someActionDone.emit();
                });
        }
    }

    deleteValue(valueId: number | string): void {
        const attributeId = this.$attribute().attribute_id;
        if (typeof valueId === 'number') { //DELETE
            this.#productsSrv.product.attributeValue.delete(this.$productId(), attributeId, valueId)
                .subscribe(() => {
                    this.#productsSrv.product.attributeValuesList.load(this.$productId(), attributeId);
                    this.valuesList.markAllAsTouched();
                    this.someActionDone.emit();
                });
        }
    }

    async onListDrop(event: CdkDragDrop<ProductAttributeValue[]>): Promise<void> {
        const isLoading = await firstValueFrom(this.isInProgress$);
        const positionHasChanged = event.currentIndex !== event.previousIndex;

        if (this.valuesList.valid && !isLoading && this.valuesList.length > 1 && positionHasChanged) {
            const obs$ = [] as Observable<void>[];
            const attributeId = this.$attribute().attribute_id;

            moveItemInArray(this.$attributeValues(), event.previousIndex, event.currentIndex);
            this.mapValuesToControls(this.$attributeValues()); //Necessary for updating the formArray and the view

            this.$attributeValues().forEach((attributeValue, index) => {
                const request = { position: index };
                obs$.push(
                    this.#productsSrv.product.attributeValue.update(this.$productId(), attributeId, attributeValue.value_id, request)
                );
            });

            forkJoin(obs$)
                .pipe(
                    take(1),
                    finalize(() => this.#productsSrv.product.attributeValuesList.load(this.$productId(), attributeId))
                )
                .subscribe(() => this.someActionDone.emit());
        }
    }

    private mapValuesToControls(values: ProductAttributeValue[]): void {
        this.valuesList.clear({ emitEvent: false });
        values.forEach(value => {
            const valueCtrl = this.#fb.control(value.name, {
                updateOn: 'blur',
                validators: [Validators.required, noDuplicateValuesValidatorForm(this.valuesList)]
            });
            this.valuesList.push(valueCtrl, { emitEvent: false });
        });
    }

    private scrollToNewValue(valueId: number | string): void {
        setTimeout(() => {
            const element = document.getElementById(valueId.toString());
            if (element) {
                element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }, 500);
    }
}
