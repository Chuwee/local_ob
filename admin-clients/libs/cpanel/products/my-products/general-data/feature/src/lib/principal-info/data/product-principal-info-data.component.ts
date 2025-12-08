import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { Product, ProductFieldsRestriction, PutProductRequest } from '@admin-clients/cpanel/products/my-products/data-access';
import { ChangeDetectionStrategy, Component, DestroyRef, effect, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-product-principal-info-data',
    imports: [
        MatFormFieldModule, MatInputModule,
        TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent,
        FlexModule, FlexLayoutModule
    ],
    templateUrl: './product-principal-info-data.component.html',
    styleUrls: ['./product-principal-info-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductPrincipalInfoDataComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);

    readonly dataForm = inject(FormBuilder).nonNullable.group({
        name: [null as string, [Validators.required, Validators.maxLength(ProductFieldsRestriction.productNameLength)]]
    });

    $putProductCtrl = input.required<FormControl<Partial<PutProductRequest>>>({ alias: 'putProductCtrl' });
    $product = input.required<Product>({ alias: 'product' });
    $form = input.required<FormGroup>({ alias: 'form' });
    $canWrite = input.required<boolean>({ alias: 'canWrite' });

    constructor() {
        effect(() => {
            const product = this.$product();
            this.dataForm.reset({ name: product.name }, { emitEvent: false });

            this.$canWrite()
                ? this.dataForm.controls.name.enable({ emitEvent: false })
                : this.dataForm.controls.name.disable({ emitEvent: false });
        });
    }

    ngOnInit(): void {
        this.$form().addControl('data', this.dataForm, { emitEvent: false });

        this.$putProductCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putProduct => {
                if (this.$form().invalid) return;

                const { name } = this.dataForm.controls;

                if (name.dirty) {
                    putProduct.name = name.value;
                }

                this.$putProductCtrl().setValue(putProduct, { emitEvent: false });
            });
    }
}
