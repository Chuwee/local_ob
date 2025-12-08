
import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    ProductPromotionFieldRestrictions, ProductPromotionType, productsProviders, ProductsService
} from '@admin-clients/cpanel/products/my-products/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-new-product-promotion-dialog',
    templateUrl: './new-product-promotion-dialog.component.html',
    styleUrls: ['./new-product-promotion-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, MatDialogModule, MatIconModule, TranslatePipe, MatInputModule, MatButtonModule,
        MatFormFieldModule, MatDividerModule, MatRadioModule, FormControlErrorsComponent, MatProgressSpinnerModule
    ],
    providers: [productsProviders]
})
export class NewProductPromotionDialogComponent {
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<NewProductPromotionDialogComponent>);
    readonly #data = inject<{ productId: number }>(MAT_DIALOG_DATA);
    readonly #productsSrv = inject(ProductsService);

    readonly nameRestrictions = ProductPromotionFieldRestrictions;
    readonly creationTypes = ProductPromotionType;

    readonly $loading = toSignal(this.#productsSrv.product.promotion.loading$());

    form = this.#fb.group({
        name: [null as string, [
            Validators.required,
            Validators.minLength(this.nameRestrictions.minNameLength),
            Validators.maxLength(this.nameRestrictions.maxNameLength)
        ]],
        //TODO: fix when manual promotions are ready
        type: ['AUTOMATIC' as ProductPromotionType, [Validators.required]]
    });

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    close(): void {
        this.#dialogRef.close();
    }

    create(): void {
        this.#productsSrv.product.promotion.create(this.#data.productId, this.form.value)
            .subscribe(id => this.#dialogRef.close(id));
    }
}
