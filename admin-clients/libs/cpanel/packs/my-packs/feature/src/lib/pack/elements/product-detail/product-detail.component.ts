import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { PackItem, PacksService, PutPackItem } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { GetProductsDeliveryPointsRequest, productsDeliveryPointsProviders, ProductsDeliveryPointsService } from '@admin-clients/cpanel/products/delivery-points/data-access';
import { productsProviders, ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { CurrencyInputComponent, EphemeralMessageService, HelpButtonComponent, SelectServerSearchComponent } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { throwError } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';

@Component({
    selector: 'app-product-detail',
    templateUrl: './product-detail.component.html',
    styleUrls: ['./product-detail.component.scss'],
    imports: [
        FlexLayoutModule, AsyncPipe, TranslatePipe, MatProgressSpinnerModule, MatIconModule, MatButton, MatExpansionModule,
        MatButtonModule, MatDividerModule, MatFormFieldModule, MatSelectModule, MatRadioModule, ReactiveFormsModule, MatCheckboxModule,
        CurrencyInputComponent, LocalCurrencyPipe, HelpButtonComponent, SelectServerSearchComponent
    ],
    providers: [productsProviders, productsDeliveryPointsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductDetailComponent {
    readonly #packsSrv = inject(PacksService);
    readonly #fb = inject(FormBuilder);
    readonly #productsSrv = inject(ProductsService);
    readonly #deliveryPointsSrv = inject(ProductsDeliveryPointsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #PAGE_LIMIT = 20;

    readonly dateTimeFormats = DateTimeFormats;
    readonly productDeliveryPointsReq = new GetProductsDeliveryPointsRequest();
    readonly productVariants$ = this.#productsSrv.product.variants.getData$();
    readonly deliveryPoints$ = this.#deliveryPointsSrv.productsDeliveryPointsList.getData$().pipe(filter(Boolean));

    readonly moreDeliveryPointsAvailable$ = this.#deliveryPointsSrv.productsDeliveryPointsList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly $pack = toSignal(this.#packsSrv.pack.get$());
    readonly currency$ = this.#authSrv.getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user => user.currency)
        );

    readonly form = this.#fb.group({
        variant: [null as number, Validators.required],
        delivery_point: [null as { id: number }, Validators.required],
        shared_barcode: [null as boolean, Validators.required],
        display_item_in_channels: null as boolean,
        informative_price_check: null as boolean,
        informative_price: null as number
    });

    isFormInitialized = false;
    item!: Partial<PackItem>;
    variantsLength: number;
    deliveryPointsLength: number;
    eId: number;

    @Input() set entityId(value: number) {
        this.eId = value;
    };

    @Input() channelId: number;
    @Input() packId: number;
    @Input() packType: string;
    @Input() set product(value: Partial<PackItem>) {
        value.editing = false;
        this.item = value;
        this.form.disable();
        this.#loadProductVariants(value.item_id);

        this.form.setValue({
            variant: value.product_data?.variant?.id ?? null,
            delivery_point: value.product_data?.delivery_point?.id && { id: value.product_data?.delivery_point?.id } || null,
            shared_barcode: value.product_data?.shared_barcode ?? null,
            display_item_in_channels: !!value.display_item_in_channels,
            informative_price_check: value.informative_price ? true : false,
            informative_price: value.informative_price || 0
        });

        this.#ref.detectChanges();
        this.form.markAsPristine();
    }

    edit(): void {
        if (this.item.editing) {
            this.item.editing = !this.item.editing;
            this.form.disable();
        } else {
            if (this.$pack().has_sales) {
                this.form.controls.display_item_in_channels.enable();
                this.form.controls.informative_price_check.enable();
                this.form.controls.informative_price.enable();
                this.item.editing = !this.item.editing;
            } else {
                this.form.enable();
                if (this.variantsLength === 1) { this.form.controls.variant.disable(); }
                if (this.deliveryPointsLength === 1) {
                    this.form.controls.delivery_point.disable();
                }
                this.item.editing = !this.item.editing;
                this.#ref.detectChanges();
            }
        }
    }

    cancel(): void {
        this.item.editing = false;
        this.form.disable();
    }

    save(): void {
        if (this.form.valid) {
            this.item.editing = false;
            this.form.disable();

            const {
                shared_barcode: sharedBarcode,
                display_item_in_channels: displayItemInChannels,
                informative_price_check: informativePriceCheck,
                informative_price: informativePrice
            } = this.form.value;

            const data: PutPackItem = {
                shared_barcode: sharedBarcode,
                display_item_in_channels: displayItemInChannels,
                informative_price: informativePriceCheck ? informativePrice : 0,
                ...(this.packType === 'AUTOMATIC' && {
                    ...(this.item.product_data?.variant?.id && { variant_id: this.form.controls.variant.value }),
                    delivery_point_id: this.form.controls.delivery_point.value.id
                })
            };

            this.#packsSrv.packItems.update(this.packId, this.item.id, data).subscribe(() => {
                this.#ephemeralSrv.showSaveSuccess();
                if (this.item.product_data.variant) { this.item.product_data.variant.id = this.form.controls.variant.value; }
                if (this.item.product_data.delivery_point) {
                    this.item.product_data.delivery_point.id = this.form.controls.delivery_point.value.id;
                }
                this.item.product_data.shared_barcode = this.form.controls.shared_barcode.value;
            });
        } else {
            throwError(() => new Error('Invalid form'));
        }
    }

    loadProductDeliveryPoints(q: string, next = false): void {
        const request: GetProductsDeliveryPointsRequest = {
            offset: 0,
            limit: this.#PAGE_LIMIT,
            sort: 'name:asc',
            entityId: this.eId,
            status: null,
            q
        };
        if (!next) {
            this.#deliveryPointsSrv.productsDeliveryPointsList.load(request);
        } else {
            this.#deliveryPointsSrv.productsDeliveryPointsList.loadMore(request);
        }
    }

    #loadProductVariants(productId: number): void {
        this.#productsSrv.product.variants.load(productId);
    }
}
