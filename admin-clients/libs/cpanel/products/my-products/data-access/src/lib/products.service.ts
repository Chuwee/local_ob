import { getListData, getMetadata, ListResponse, mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import { FilterOption } from '@admin-clients/shared/data-access/models';
import { Injectable, inject } from '@angular/core';
import { map, take, tap } from 'rxjs';
import { ProductsApi } from './api/products.api';
import { GetProductChannelSessionLinksRequest } from './models/get-product-channel-session-links-request';
import { ProductPromotion, ProductPromotionsListReq } from './models/get-product-promotions-list.model';
import { GetProductsRequest } from './models/get-products-request.model';
import { GetVariantsRequest } from './models/get-variants-request.model';
import { PostProduct } from './models/post-product.model';
import {
    ProductAttribute, ProductAttributeChannelContents, ProductAttributeValue, ProductAttributeValueListChannelContents
} from './models/product-attribute.model';
import { ProductChannelContentImageType, ProductChannelImageContent } from './models/product-channel-content-image.model';
import { ProductChannelTextContent } from './models/product-channel-content-text.model';
import { ProductChannel } from './models/product-channel.model';
import { ProductDeliveryConfig } from './models/product-delivery-config.model';
import { GetProductDeliveryPointsRelationReq, PostProductDeliveryPointsRelation } from './models/product-delivery-points-relation.model';
import { ProductSurcharge } from './models/product-surcharge.model';
import { Product } from './models/product.model';
import { PutProductChannel } from './models/put-product-channel.model';
import { PutProductLanguage } from './models/put-product-language.model';
import { PutProductRequest } from './models/put-product-request.model';
import { PutProductVariant, PutProductVariantPrices } from './models/put-product-variant.model';
import { ProductsState } from './state/products.state';

@Injectable()
export class ProductsService {
    readonly #api = inject(ProductsApi);
    readonly #state = inject(ProductsState);

    readonly product = Object.freeze({
        load: (productId: number) => StateManager.load(
            this.#state.product,
            this.#api.getProduct(productId)
        ),
        get$: () => this.#state.product.getValue$(),
        inProgress$: () => this.#state.product.isInProgress$(),
        create: (product: PostProduct) => StateManager.inProgress(
            this.#state.product,
            this.#api.postProduct(product)
        ),
        update: (id: number, product: Partial<PutProductRequest>) => StateManager.inProgress(
            this.#state.product,
            this.#api.putProduct(id, product)
        ),
        delete: (productId: number) => StateManager.inProgress(
            this.#state.product,
            this.#api.deleteProduct(productId)
        ),
        clear: () => this.#state.product.setValue(null),
        error$: () => this.#state.product.getError$(),
        surcharges: Object.freeze({
            load: (productId: number) => StateManager.load(
                this.#state.productSurcharges,
                this.#api.getSurcharges(productId)
            ),
            get$: () => this.#state.productSurcharges.getValue$(),
            inProgress$: () => this.#state.productSurcharges.isInProgress$(),
            post: (productId: number, surcharges: ProductSurcharge[]) => StateManager.inProgress(
                this.#state.productSurcharges,
                this.#api.updateSurcharges(productId, surcharges)
            ),
            clear: () => this.#state.productSurcharges.setValue(null),
            error$: () => this.#state.productSurcharges.getError$()
        }),
        languages: Object.freeze({
            load: (productId: number) => StateManager.load(
                this.#state.productLanguages,
                this.#api.getProductLanguage(productId)
            ),
            get$: () => this.#state.productLanguages.getValue$(),
            inProgress$: () => this.#state.productLanguages.isInProgress$(),
            update: (id: number, languages: PutProductLanguage[]) => StateManager.inProgress(
                this.#state.productLanguages,
                this.#api.updateProductLanguage(id, languages)
            ),
            clear: () => this.#state.productLanguages.setValue(null),
            error$: () => this.#state.productLanguages.getError$()
        }),
        variants: Object.freeze({
            load: (productId: number, filters?: GetVariantsRequest) => StateManager.load(
                this.#state.productVariants,
                this.#api.getProductVariants(productId, filters).pipe(mapMetadata())
            ),
            loadIfNull: (productId: number, filters?: GetVariantsRequest) => StateManager.loadIfNull(
                this.#state.productVariants,
                this.#api.getProductVariants(productId, filters).pipe(mapMetadata())
            ),
            update: (productId: number, variantId: number, variant: PutProductVariant) => StateManager.inProgress(
                this.#state.productVariants,
                this.#api.updateProductVariant(productId, variantId, variant)
            ),
            bulkUpdatePrices: (productId: number, prices: PutProductVariantPrices) => StateManager.inProgress(
                this.#state.productVariants,
                this.#api.bulkUpdateProductVariantPrices(productId, prices)
            ),
            create: (productId: number) => StateManager.inProgress(
                this.#state.productVariants,
                this.#api.postProductVariants(productId)
            ),
            getData$: () => this.#state.productVariants.getValue$().pipe(getListData()),
            getMetadata$: () => this.#state.productVariants.getValue$().pipe(getMetadata()),
            loading$: () => this.#state.productVariants.isInProgress$(),
            clear: () => this.#state.productVariants.setValue(null),
            error$: () => this.#state.productVariants.getError$()
        }),
        // variantsTable is a workaround for the variants table and
        // will be removed when angular v19 makes available the routerOutletData to pass data to router-outlet child
        variantsTable: Object.freeze({
            loadTable: (productId: number, filters?: GetVariantsRequest) => StateManager.load(
                this.#state.productVariantsTable,
                this.#api.getProductVariants(productId, filters).pipe(mapMetadata())
            ),
            getData$: () => this.#state.productVariantsTable.getValue$().pipe(getListData()),
            getMetadata$: () => this.#state.productVariantsTable.getValue$().pipe(getMetadata()),
            loading$: () => this.#state.productVariantsTable.isInProgress$(),
            clear: () => this.#state.productVariantsTable.setValue(null),
            error$: () => this.#state.productVariantsTable.getError$()
        }),
        channelsList: Object.freeze({
            load: (productId: number) => StateManager.load(
                this.#state.productChannelsList,
                this.#api.getProductChannels(productId)
            ),
            post: (productId: number, channels: number[]) => StateManager.inProgress(
                this.#state.productChannelsList,
                this.#api.postProductChannels(productId, channels)
            ),
            delete: (productId: number, channelId: number) => StateManager.inProgress(
                this.#state.productChannelsList,
                this.#api.deleteProductChannel(productId, channelId)
            ),
            get$: () => this.#state.productChannelsList.getValue$(),
            error$: () => this.#state.productChannelsList.getError$(),
            inProgress$: () => this.#state.productChannelsList.isInProgress$(),
            clear: () => this.#state.productChannelsList.setValue(null),
            updateProductChannelStatusOnList: (targetProductChannel: ProductChannel) => this.#state.productChannelsList.getValue$()
                .pipe(take(1))
                .subscribe(list => {
                    const productChannelListElem = list?.find(
                        productChannel => productChannel.channel.id === targetProductChannel.channel.id
                    );
                    if (productChannelListElem) {
                        productChannelListElem.sale_request_status = targetProductChannel.sale_request_status;
                        this.#state.productChannelsList.setValue(list);
                    }
                })
        }),
        channel: Object.freeze({
            load: (productId: number, channelId: number) => StateManager.load(
                this.#state.productChannel,
                this.#api.getProductChannel(productId, channelId)
            ),
            update: (productId: number, channelId: number, reqBody: Partial<PutProductChannel>) => StateManager.inProgress(
                this.#state.productChannel,
                this.#api.putProductChannel(productId, channelId, reqBody)
            ),
            get$: () => this.#state.productChannel.getValue$(),
            error$: () => this.#state.productChannel.getError$(),
            inProgress$: () => this.#state.productChannel.isInProgress$(),
            clear: () => this.#state.productChannel.setValue(null)
        }),
        channelContents: Object.freeze({
            texts: Object.freeze({
                load: (productId: number) => StateManager.load(
                    this.#state.channelTextContents,
                    this.#api.getProductTextContents(productId)
                ),
                get$: () => this.#state.channelTextContents.getValue$(),
                update: (productId: number, contents: ProductChannelTextContent[]) => StateManager.inProgress(
                    this.#state.channelTextContents,
                    this.#api.postProductTextContents(productId, contents)
                ),
                loading$: () => this.#state.channelTextContents.isInProgress$(),
                clear: () => this.#state.channelTextContents.setValue(null)
            }),
            images: Object.freeze({
                load: (productId: number) => StateManager.load(
                    this.#state.channelImageContents,
                    this.#api.getProductImageContents(productId)
                ),
                get$: () => this.#state.channelImageContents.getValue$(),
                update: (productId: number, contents: ProductChannelImageContent[]) => StateManager.inProgress(
                    this.#state.channelImageContents,
                    this.#api.postProductImageContents(productId, contents)
                ),
                delete: (productId: number, lang: string, type: ProductChannelContentImageType, position: number) =>
                    StateManager.inProgress(
                        this.#state.channelImageContents,
                        this.#api.deleteProductImageContent(productId, lang, type, position)
                    ),
                loading$: () => this.#state.channelImageContents.isInProgress$(),
                clear: () => this.#state.channelImageContents.setValue(null)
            })
        }),
        delivery: Object.freeze({
            load: (productId: number) => StateManager.load(
                this.#state.deliveryConfig,
                this.#api.getProductDeliveryConfig(productId)
            ),
            get$: () => this.#state.deliveryConfig.getValue$(),
            update: (productId: number, reqBody: Partial<ProductDeliveryConfig>) => StateManager.inProgress(
                this.#state.deliveryConfig,
                this.#api.putProductDeliveryConfig(productId, reqBody)
            ),
            loading$: () => this.#state.deliveryConfig.isInProgress$(),
            clear: () => this.#state.deliveryConfig.setValue(null)
        }),
        deliveryPointsRelationList: Object.freeze({
            load: (productId: number, request: GetProductDeliveryPointsRelationReq) => StateManager.load(
                this.#state.productDeliveryPointRelationList,
                this.#api.getProductDeliveryPointsRelations(productId, request).pipe(mapMetadata())
            ),
            update: (productId: number, request: PostProductDeliveryPointsRelation) => StateManager.inProgress(
                this.#state.productDeliveryPointRelationList,
                this.#api.postProductDeliveryPointsRelations(productId, request)
            ),
            getData$: () => this.#state.productDeliveryPointRelationList.getValue$().pipe(getListData()),
            getMetadata$: () => this.#state.productDeliveryPointRelationList.getValue$().pipe(getMetadata()),
            loading$: () => this.#state.productDeliveryPointRelationList.isInProgress$(),
            clear: () => this.#state.productDeliveryPointRelationList.setValue(null),
            error$: () => this.#state.productDeliveryPointRelationList.getError$()
        }),
        attributesList: Object.freeze({
            load: (productId: number) => StateManager.load(
                this.#state.productAttributes,
                this.#api.getProductAttributes(productId)
            ),
            get$: () => this.#state.productAttributes.getValue$(),
            loading$: () => this.#state.productAttributes.isInProgress$(),
            clear: () => this.#state.productAttributes.setValue(null),
            error$: () => this.#state.productAttributes.getError$()
        }),
        attribute: Object.freeze({
            create: (productId: number, request: Partial<ProductAttribute>) => StateManager.inProgress(
                this.#state.productAttribute,
                this.#api.postProductAttribute(productId, request)
            ),
            get$: () => this.#state.productAttribute.getValue$(),
            update: (productId: number, attributeId: number, request: Partial<ProductAttribute>) => StateManager.inProgress(
                this.#state.productAttribute,
                this.#api.putProductAttribute(productId, attributeId, request)
            ),
            delete: (productId: number, attributeId: number) => StateManager.inProgress(
                this.#state.productAttribute,
                this.#api.deleteProductAttribute(productId, attributeId)
            ),
            loading$: () => this.#state.productAttribute.isInProgress$()
        }),
        attributeValuesList: Object.freeze({
            load: (productId: number, attributeId: number) => StateManager.load(
                this.#state.productAttributeValues,
                this.#api.getProductAttributeValues(productId, attributeId).pipe(mapMetadata())
            ),
            getData$: () => this.#state.productAttributeValues.getValue$().pipe(getListData()),
            getMetadata$: () => this.#state.productAttributeValues.getValue$().pipe(getMetadata()),
            loading$: () => this.#state.productAttributeValues.isInProgress$(),
            clear: () => this.#state.productAttributeValues.setValue(null)
        }),
        attributeValue: Object.freeze({
            create: (productId: number, attributeId: number, request: Partial<ProductAttributeValue>) => StateManager.inProgress(
                this.#state.productAttributeValue,
                this.#api.postProductAttributeValue(productId, attributeId, request)
            ),
            update: (
                productId: number, attributeId: number, valueId: number, request: Partial<ProductAttributeValue>
            ) => StateManager.inProgress(
                this.#state.productAttributeValue,
                this.#api.putProductAttributeValue(productId, attributeId, valueId, request)
            ),
            delete: (productId: number, attributeId: number, valueId: number) => StateManager.inProgress(
                this.#state.productAttributeValue,
                this.#api.deleteProductAttributeValue(productId, attributeId, valueId)
            ),
            loading$: () => this.#state.productAttributeValue.isInProgress$()
        }),
        attributeChannelContents: Object.freeze({
            load: (productId: number, attributeId: number, language?: string) => StateManager.load(
                this.#state.attributeChannelContents,
                this.#api.getAttributeChannelContents(productId, attributeId, language)
            ),
            getData$: () => this.#state.attributeChannelContents.getValue$(),
            save: (productId: number, attributeId: number, literals: ProductAttributeChannelContents[]) => StateManager.inProgress(
                this.#state.attributeChannelContents,
                this.#api.postAttributeChannelContents(productId, attributeId, literals)
            ),
            loading$: () => this.#state.attributeChannelContents.isInProgress$()
        }),
        attributeValueChannelContents: Object.freeze({
            load: (productId: number, attributeId: number, valueId: number) => StateManager.load(
                this.#state.attributeValueChannelContents,
                this.#api.getAttributeValueChannelContents(productId, attributeId, valueId)
            ),
            getData$: () => this.#state.attributeValueChannelContents.getValue$(),
            save: (productId: number, attributeId: number, valueId: number, literals: ProductAttributeChannelContents[]) =>
                StateManager.inProgress(
                    this.#state.attributeValueChannelContents,
                    this.#api.postAttributeValueChannelContents(productId, attributeId, valueId, literals)
                ),
            loading$: () => this.#state.attributeValueChannelContents.isInProgress$()
        }),
        attributeValuesListChannelContents: Object.freeze({
            load: (productId: number, attributeId: number, language?: string) => StateManager.load(
                this.#state.attributeValuesChannelContents,
                this.#api.getAttributeValuesChannelContents(productId, attributeId, language)
            ),
            getData$: () => this.#state.attributeValuesChannelContents.getValue$(),
            save: (productId: number, attributeId: number, literals: ProductAttributeValueListChannelContents[]) => StateManager.inProgress(
                this.#state.attributeValuesChannelContents,
                this.#api.postAttributeValuesChannelContents(productId, attributeId, literals)
            ),
            loading$: () => this.#state.attributeValuesChannelContents.isInProgress$(),
            clear: () => this.#state.attributeValuesChannelContents.setValue(null)
        }),
        promotion: Object.freeze({
            load: (productId: number, promotionId: number) => StateManager.load(
                this.#state.productPromotion,
                this.#api.getProductPromotion(productId, promotionId)
            ),
            get$: () => this.#state.productPromotion.getValue$(),
            error$: () => this.#state.productPromotion.getError$(),
            loading$: () => this.#state.productPromotion.isInProgress$(),
            create: (productId: number, promotion: ProductPromotion) => StateManager.inProgress(
                this.#state.productPromotion,
                this.#api.postProductPromotion(productId, promotion).pipe(map(result => result.id))
            ),
            update: (productId: number, promotionId: number, promotion: ProductPromotion) => StateManager.inProgress(
                this.#state.productPromotion,
                this.#api.putProductPromotion(productId, promotionId, promotion)
            ),
            delete: (productId: number, promotionId: number) => StateManager.inProgress(
                this.#state.productPromotion,
                this.#api.deleteProductPromotion(productId, promotionId)
            ),
            clone: (productId: number, promotionId: number) => StateManager.inProgress(
                this.#state.productPromotion,
                this.#api.cloneProductPromotion(productId, promotionId).pipe(map(response => response.id))
            ),
            clear: () => this.#state.productPromotion.setValue(null)
        }),
        promotionList: Object.freeze({
            load: (productId: number, request: ProductPromotionsListReq) => StateManager.load(
                this.#state.productPromotionsList,
                this.#api.getProductPromotionsList(productId, request)
            ),
            getData$: () => this.#state.productPromotionsList.getValue$()
                .pipe(map(promotions => promotions?.data)),
            getMetadata$: () => this.#state.productPromotionsList.getValue$()
                .pipe(map(promotions =>
                    promotions?.metadata && Object.assign(new Metadata(), promotions.metadata))),
            loading$: () => this.#state.productPromotionsList.isInProgress$(),
            clear: () => this.#state.productPromotionsList.setValue(null)
        }),
        productChannelRelation: Object.freeze({
            request: (productId: number, channelId: number) => StateManager.inProgress(
                this.#state.requestProductChannel,
                this.#api.postRequestProductChannel(productId, channelId)
            ),
            isInProgress$: () => this.#state.requestProductChannel.isInProgress$()
        })
    });

    readonly productsList = Object.freeze({
        load: (request?: GetProductsRequest) => StateManager.load(
            this.#state.productsList,
            this.#api.getProducts(request).pipe(mapMetadata())
        ),
        getData$: () => this.#state.productsList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.productsList.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.productsList.isInProgress$(),
        clear: () => this.#state.productsList.setValue(null),
        loadMoreAndCache: (request: GetProductsRequest, nextPage = false) => {
            if (nextPage) {
                StateManager.loadMore(
                    request,
                    this.#state.productsList,
                    r => this.#api.getProducts(r).pipe(mapMetadata(),
                        tap(result => this.#cacheProductFilterOptions(result)))
                );
            } else {
                StateManager.load(
                    this.#state.productsList,
                    this.#api.getProducts(request).pipe(mapMetadata(),
                        tap(result => this.#cacheProductFilterOptions(result)))
                );
            }
        },
        getNames$: (ids: string[]) =>
            this.#state.productCache.getItems$(ids, id => this.#api.getProduct(Number(id))
                .pipe(map(product => {
                    const result: FilterOption = {
                        name: product.name,
                        id: String(product.product_id)
                    };
                    return result;
                }))
            )
        ,
        clearCache: () => this.#state.productCache.clear()
    });

    readonly productChannelPublishedSessionLinks = Object.freeze({
        load: (request: GetProductChannelSessionLinksRequest): void => StateManager.load(
            this.#state.productChannelPublishedSessionLinks,
            this.#api.getProductChannelSessionLinks(request).pipe(mapMetadata())
        ),
        getData$: () => this.#state.productChannelPublishedSessionLinks.getValue$()
            .pipe(map(sessionLinks => sessionLinks?.data)),
        getMetadata$: () => this.#state.productChannelPublishedSessionLinks.getValue$()
            .pipe(map(r => r?.metadata)),
        error$: () => this.#state.productChannelPublishedSessionLinks.getError$(),
        inProgress$: () => this.#state.productChannelPublishedSessionLinks.isInProgress$(),
        clear: () => this.#state.productChannelPublishedSessionLinks.setValue(null)
    });

    readonly productChannelUnpublishedSessionLinks = Object.freeze({
        load: (request: GetProductChannelSessionLinksRequest): void => StateManager.load(
            this.#state.productChannelUnpublishedSessionLinks,
            this.#api.getProductChannelSessionLinks(request).pipe(mapMetadata())
        ),
        getData$: () => this.#state.productChannelUnpublishedSessionLinks.getValue$()
            .pipe(map(sessionLinks => sessionLinks?.data)),
        getMetadata$: () => this.#state.productChannelUnpublishedSessionLinks.getValue$()
            .pipe(map(r => r?.metadata)),
        error$: () => this.#state.productChannelUnpublishedSessionLinks.getError$(),
        inProgress$: () => this.#state.productChannelUnpublishedSessionLinks.isInProgress$(),
        clear: () => this.#state.productChannelUnpublishedSessionLinks.setValue(null)
    });

    #cacheProductFilterOptions(result: ListResponse<Product>): void {
        this.#state.productCache.cacheItems(
            result.data.map(product => {
                const result: FilterOption = { name: product.name, id: String(product.product_id) };
                return result;
            }));
    }
}
