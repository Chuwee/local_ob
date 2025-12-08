import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { Product, ProductStatus, PutProductRequest } from '@admin-clients/cpanel/products/my-products/data-access';
import { CurrencySingleSelectorComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, effect, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { first, map } from 'rxjs';

const disabledStatus = [ProductStatus.active];

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        CurrencySingleSelectorComponent
    ],
    selector: 'app-product-principal-info-currency',
    styleUrls: ['./product-principal-info-currency.component.scss'],
    templateUrl: './product-principal-info-currency.component.html'
})
export class ProductPrincipalInfoCurrencyComponent implements OnInit {
    readonly currencyControl = inject(FormBuilder).control(null as string, Validators.required);
    readonly #auth = inject(AuthenticationService);
    readonly #destroyRef = inject(DestroyRef);

    readonly currencies$ = this.#auth.getLoggedUser$().pipe(first(), map(AuthenticationService.operatorCurrencies));

    $putProductCtrl = input.required<FormControl<Partial<PutProductRequest>>>({ alias: 'putProductCtrl' });
    $product = input.required<Product>({ alias: 'product' });
    $form = input.required<FormGroup>({ alias: 'form' });
    $canWrite = input.required<boolean>({ alias: 'canWrite' });
    $disabledStatus = computed(() => {
        const product = this.$product();
        return disabledStatus.includes(product.product_state);
    });

    constructor() {
        effect(() => {
            const product = this.$product();

            this.currencyControl.reset(product.currency_code, { emitEvent: false });

            if (disabledStatus.includes(product.product_state) || !this.$canWrite()) {
                this.currencyControl.disable({ emitEvent: false });
            } else {
                this.currencyControl.enable({ emitEvent: false });
            }
        });
    }

    ngOnInit(): void {
        this.$form().addControl('currency', this.currencyControl, { emitEvent: false });

        this.$putProductCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putProduct => {
                if (this.$form().invalid) return;

                if (this.currencyControl.enabled && this.currencyControl.dirty) {
                    putProduct.currency_code = this.currencyControl.value;
                    this.$putProductCtrl().setValue(putProduct, { emitEvent: false });
                }
            });
    }
}
