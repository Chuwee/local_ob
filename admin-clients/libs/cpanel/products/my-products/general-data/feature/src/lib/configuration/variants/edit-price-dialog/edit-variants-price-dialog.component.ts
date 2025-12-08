import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { CurrencyInputComponent, DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { filter } from 'rxjs';

@Component({
    selector: 'app-edit-variants-price-dialog',
    imports: [
        AsyncPipe, LocalCurrencyPipe, TranslatePipe, MaterialModule, FlexLayoutModule, ReactiveFormsModule, CurrencyInputComponent,
        FormControlErrorsComponent
    ],
    templateUrl: './edit-variants-price-dialog.component.html',
    styleUrls: ['./edit-variants-price-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditVariantsPriceDialogComponent implements OnInit {
    readonly #productsSrv = inject(ProductsService);
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<EditVariantsPriceDialogComponent>);

    readonly data = inject(MAT_DIALOG_DATA);
    readonly form = this.#fb.group({
        price: this.#fb.control<number>(null, [Validators.required, Validators.min(0)])
    });

    readonly currency = this.data.currency;
    readonly #selected = this.data.selected;
    readonly #productId = this.data.productId;
    readonly #variants$ = this.#productsSrv.product.variantsTable.getData$().pipe(filter(Boolean));
    readonly #$variants = toSignal(this.#variants$);

    isInProgress$ = this.#productsSrv.product.inProgress$();

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    close(saved = false): void {
        this.#dialogRef.close(saved);
    }

    savePrice(): void {
        if (this.form.value.price) {
            const selectedIds = this.#selected !== 'allSelected' ? this.#selected.map(selected => selected.id) :
                this.#$variants().map(variant => variant.id);
            this.#productsSrv.product.variants.bulkUpdatePrices(this.#productId, {
                price: this.form.value.price,
                variants: selectedIds
            }).subscribe(() => this.close(true));
        }
    }

}
