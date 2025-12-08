import {
    GetVariantsRequest, ProductsService,
    ProductStockType, ProductVariantStatus
} from '@admin-clients/cpanel/products/my-products/data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, Output, viewChild } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import { filter } from 'rxjs';

@Component({
    selector: 'app-config-variants-filter',
    templateUrl: './config-variants-filter.component.html',
    styleUrls: ['./config-variants-filter.component.scss'],
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule, SatPopoverModule, FlexModule, SelectSearchComponent, AsyncPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductConfigVariantsFilterComponent {
    readonly #fb = inject(FormBuilder);
    readonly #productSrv = inject(ProductsService);

    readonly filterPopover = viewChild<SatPopoverComponent>('filterPopover');
    readonly productStatus = [ProductVariantStatus.active, ProductVariantStatus.inactive];
    readonly variantsList$ = this.#productSrv.product.variants.getData$().pipe(filter(Boolean));
    readonly productIsStocked$ = this.#productSrv.product.get$().pipe(filter(product => product.stock_type === ProductStockType.bounded));
    #appliedFilters: GetVariantsRequest = {};

    readonly form = this.#fb.group({
        ids: [null as number[]],
        status: null as ProductVariantStatus,
        stock: null as number
    });

    @Output() filterChange = new EventEmitter<GetVariantsRequest>();

    apply(): void {
        this.#appliedFilters = {
            ids: this.form.value.ids,
            status: this.form.value.status,
            stock: this.form.value.stock
        };
        this.filterChange.emit(this.#appliedFilters);
        this.close();
    }

    clear(): void {
        this.form.reset();
        this.apply();
    }

    close(): void {
        this.filterPopover().close();
    }

    open(): void {
        this.form.reset({
            ...this.#appliedFilters
        });
        this.filterPopover().open();
    }

    get activeFilters(): number {
        return Object.values(this.#appliedFilters).filter(Boolean)?.length || 0;
    }

}
