import { buildHttpParams } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import { ContentLinkResponse } from '@admin-clients/cpanel/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetProductChannelSessionLinksRequest } from '../models/get-product-channel-session-links-request';
import { ProductPromotionsListReq, ProductPromotion } from '../models/get-product-promotions-list.model';
import { GetProductsRequest } from '../models/get-products-request.model';
import { GetVariantsRequest } from '../models/get-variants-request.model';
import { PostProduct } from '../models/post-product.model';
import {
    ProductAttribute, ProductAttributeChannelContents, ProductAttributeValue, ProductAttributeValueListChannelContents
} from '../models/product-attribute.model';
import { ProductChannelContentImageType, ProductChannelImageContent } from '../models/product-channel-content-image.model';
import { ProductChannelTextContent } from '../models/product-channel-content-text.model';
import { ProductChannel } from '../models/product-channel.model';
import { ProductDeliveryConfig } from '../models/product-delivery-config.model';
import {
    GetProductDeliveryPointsRelationReq, PostProductDeliveryPointsRelation, ProductDeliveryPointsRelation
} from '../models/product-delivery-points-relation.model';
import { ProductLanguage } from '../models/product-language.model';
import { ProductSurcharge } from '../models/product-surcharge.model';
import { ProductVariant } from '../models/product-variant.model';
import { Product } from '../models/product.model';
import { PutProductChannel } from '../models/put-product-channel.model';
import { PutProductLanguage } from '../models/put-product-language.model';
import { PutProductRequest } from '../models/put-product-request.model';
import { PutProductVariant, PutProductVariantPrices } from '../models/put-product-variant.model';

@Injectable()
export class ProductsApi {
    readonly #PRODUCTS_API = `${inject(APP_BASE_API)}/mgmt-api/v1/products`;
    readonly #PRICES_PATH = 'prices';
    readonly #SURCHARGES_PATH = 'surcharges';
    readonly #LANGS_PATH = 'languages';
    readonly #VARIANTS_PATH = 'variants';
    readonly #ATTRIBUTES_PATH = 'attributes';
    readonly #ATTRIBUTE_VALUES_PATH = 'values';
    readonly #CHANNEL_CONTENTS_TEXTS_PATH = 'channel-contents/texts';
    readonly #COMMS_SEGMENT = 'communication-elements';
    readonly #TEXTS_SEGMENT = 'texts';
    readonly #IMAGES_SEGMENT = 'images';
    readonly #TYPES_SEGMENT = 'types';
    readonly #POSITION_SEGMENT = 'positions';
    readonly #DELIVERY_CONFIG_SEGMENT = 'delivery';
    readonly #CHANNELS_SEGMENT = 'channels';
    readonly #PROMOTIONS_SEGMENT = 'promotions';
    readonly #DELIVERY_POINTS_SEGMENT = 'delivery-points';

    readonly #http = inject(HttpClient);

    postProduct(event: PostProduct): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(this.#PRODUCTS_API, event);
    }

    getProduct(productId: number): Observable<Product> {
        return this.#http.get<Product>(`${this.#PRODUCTS_API}/${productId}`);
    }

    putProduct(productId: number, product: Partial<PutProductRequest>): Observable<void> {
        return this.#http.put<void>(`${this.#PRODUCTS_API}/${productId}`, product);
    }

    deleteProduct(productId: number): Observable<{ id: number }> {
        return this.#http.delete<{ id: number }>(`${this.#PRODUCTS_API}/${productId}`);
    }

    getProducts(request: GetProductsRequest): Observable<ListResponse<Product>> {
        const params = buildHttpParams({
            q: request.q,
            sort: request.sort,
            limit: request.limit,
            offset: request.offset,
            product_state: request.status,
            stock_type: request.stock,
            entity_id: request.entityId,
            product_type: request.type,
            currency_code: request.currency
        });
        return this.#http.get<ListResponse<Product>>(this.#PRODUCTS_API, { params });
    }

    getSurcharges(productId: number): Observable<ProductSurcharge[]> {
        return this.#http.get<ProductSurcharge[]>(`${this.#PRODUCTS_API}/${productId}/${this.#SURCHARGES_PATH}`);
    }

    updateSurcharges(productId: number, surcharges: ProductSurcharge[]): Observable<void> {
        return this.#http.post<void>(`${this.#PRODUCTS_API}/${productId}/${this.#SURCHARGES_PATH}`, surcharges);
    }

    getProductLanguage(productId: number): Observable<ProductLanguage[]> {
        return this.#http.get<ProductLanguage[]>(`${this.#PRODUCTS_API}/${productId}/${this.#LANGS_PATH}`);
    }

    updateProductLanguage(productId: number, languages: PutProductLanguage[]): Observable<void> {
        return this.#http
            .put<void>(`${this.#PRODUCTS_API}/${productId}/${this.#LANGS_PATH}`, languages);
    }

    // VARIANTS
    getProductVariants(productId: number, request?: GetVariantsRequest): Observable<ListResponse<ProductVariant>> {
        const params = request ? buildHttpParams(request) : {};
        return this.#http.get<ListResponse<ProductVariant>>(`${this.#PRODUCTS_API}/${productId}/${this.#VARIANTS_PATH}`, { params });
    }

    updateProductVariant(productId: number, variantId: number, variant: PutProductVariant): Observable<void> {
        return this.#http.put<void>(`${this.#PRODUCTS_API}/${productId}/${this.#VARIANTS_PATH}/${variantId}`, variant);
    }

    postProductVariants(productId: number): Observable<void> {
        return this.#http.post<void>(`${this.#PRODUCTS_API}/${productId}/${this.#VARIANTS_PATH}`, null);
    }

    bulkUpdateProductVariantPrices(productId: number, prices: PutProductVariantPrices): Observable<void> {
        return this.#http.put<void>(`${this.#PRODUCTS_API}/${productId}/${this.#VARIANTS_PATH}/${this.#PRICES_PATH}`, prices);
    }

    // ATTRIBUTES
    getProductAttributes(productId: number): Observable<ProductAttribute[]> {
        return this.#http.get<ProductAttribute[]>(`${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}`);
    }

    getProductAttribute(productId: number, attributeId: number): Observable<ProductAttribute> {
        return this.#http.get<ProductAttribute>(`${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}`);
    }

    postProductAttribute(productId: number, request: Partial<ProductAttribute>): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(`${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}`, request);
    }

    putProductAttribute(productId: number, attributeId: number, request: Partial<ProductAttribute>): Observable<void> {
        return this.#http.put<void>(`${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}`, request);
    }

    deleteProductAttribute(productId: number, attributeId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}`);
    }

    // ATTRIBUTE VALUES

    getProductAttributeValues(productId: number, attributeId: number): Observable<ListResponse<ProductAttributeValue>> {
        return this.#http.get<ListResponse<ProductAttributeValue>>(
            `${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}/${this.#ATTRIBUTE_VALUES_PATH}`
        );
    }

    getProductAttributeValue(productId: number, attributeId: number, valueId: number): Observable<ProductAttributeValue> {
        return this.#http.get<ProductAttributeValue>(
            `${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}/${this.#ATTRIBUTE_VALUES_PATH}/${valueId}`
        );
    }

    postProductAttributeValue(productId: number, attributeId: number, request: Partial<ProductAttributeValue>): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(
            `${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}/${this.#ATTRIBUTE_VALUES_PATH}`,
            request
        );
    }

    putProductAttributeValue(
        productId: number, attributeId: number, valueId: number, request: Partial<ProductAttributeValue>
    ): Observable<void> {
        return this.#http.put<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}/${this.#ATTRIBUTE_VALUES_PATH}/${valueId}`,
            request
        );
    }

    deleteProductAttributeValue(productId: number, attributeId: number, valueId: number): Observable<void> {
        return this.#http.delete<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}/${this.#ATTRIBUTE_VALUES_PATH}/${valueId}`
        );
    }

    // ATTRIBUTES & VALUES LITERALS

    getAttributeChannelContents(productId: number, attributeId: number, language: string): Observable<ProductAttributeChannelContents[]> {
        const params = buildHttpParams({ language });
        return this.#http.get<ProductAttributeChannelContents[]>(
            `${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}/${this.#CHANNEL_CONTENTS_TEXTS_PATH}`, { params }
        );
    }

    postAttributeChannelContents(productId: number, attributeId: number, literalValues: ProductAttributeChannelContents[]):
        Observable<void> {
        return this.#http.post<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}/${this.#CHANNEL_CONTENTS_TEXTS_PATH}`,
            literalValues
        );
    }

    getAttributeValueChannelContents(productId: number, attributeId: number, valueId: number):
        Observable<ProductAttributeChannelContents[]> {
        return this.#http.get<ProductAttributeChannelContents[]>(
            `${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}/` +
            `${this.#ATTRIBUTE_VALUES_PATH}/${valueId}/${this.#CHANNEL_CONTENTS_TEXTS_PATH}`
        );
    }

    postAttributeValueChannelContents(
        productId: number, attributeId: number, valueId: number, literalValues: ProductAttributeChannelContents[]
    ): Observable<void> {
        return this.#http.post<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}/` +
            `${this.#ATTRIBUTE_VALUES_PATH}/${valueId}/${this.#CHANNEL_CONTENTS_TEXTS_PATH}`,
            literalValues
        );
    }

    getAttributeValuesChannelContents(productId: number, attributeId: number, language?: string):
        Observable<ProductAttributeValueListChannelContents[]> {
        const params = buildHttpParams({ language });
        return this.#http.get<ProductAttributeValueListChannelContents[]>(
            `${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}/` +
            `${this.#ATTRIBUTE_VALUES_PATH}/${this.#CHANNEL_CONTENTS_TEXTS_PATH}`,
            { params }
        );
    }

    postAttributeValuesChannelContents(
        productId: number, attributeId: number, literalValues: ProductAttributeValueListChannelContents[]
    ): Observable<void> {
        return this.#http.post<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#ATTRIBUTES_PATH}/${attributeId}/` +
            `${this.#ATTRIBUTE_VALUES_PATH}/${this.#CHANNEL_CONTENTS_TEXTS_PATH}`,
            literalValues
        );
    }

    // CHANNEL CONTENTS
    getProductTextContents(productId: number): Observable<ProductChannelTextContent[]> {
        return this.#http.get<ProductChannelTextContent[]>(
            `${this.#PRODUCTS_API}/${productId}/${this.#COMMS_SEGMENT}/${this.#TEXTS_SEGMENT}`
        );
    }

    postProductTextContents(productId: number, contents: ProductChannelTextContent[]): Observable<void> {
        return this.#http.post<void>(`${this.#PRODUCTS_API}/${productId}/${this.#COMMS_SEGMENT}/${this.#TEXTS_SEGMENT}`, contents);
    }

    getProductImageContents(productId: number): Observable<ProductChannelImageContent[]> {
        return this.#http
            .get<ProductChannelImageContent[]>(`${this.#PRODUCTS_API}/${productId}/${this.#COMMS_SEGMENT}/${this.#IMAGES_SEGMENT}`);
    }

    postProductImageContents(productId: number, contents: ProductChannelImageContent[]): Observable<void> {
        return this.#http.post<void>(`${this.#PRODUCTS_API}/${productId}/${this.#COMMS_SEGMENT}/${this.#IMAGES_SEGMENT}`, contents);
    }

    deleteProductImageContent(
        productId: number, language: string, type: ProductChannelContentImageType, position: number
    ): Observable<void> {
        return this.#http.delete<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#COMMS_SEGMENT}/${this.#IMAGES_SEGMENT}` +
            `/${this.#LANGS_PATH}/${language}/${this.#TYPES_SEGMENT}/${type}/${this.#POSITION_SEGMENT}/${position}`
        );
    }

    // DELIVERY CONFIG

    getProductDeliveryConfig(productId: number): Observable<ProductDeliveryConfig> {
        return this.#http.get<ProductDeliveryConfig>(`${this.#PRODUCTS_API}/${productId}/${this.#DELIVERY_CONFIG_SEGMENT}`);
    }

    putProductDeliveryConfig(productId: number, reqBody: Partial<ProductDeliveryConfig>): Observable<ProductDeliveryConfig> {
        return this.#http.put<ProductDeliveryConfig>(`${this.#PRODUCTS_API}/${productId}/${this.#DELIVERY_CONFIG_SEGMENT}`, reqBody);
    }

    // PRODUCT DELIVERY POINTS RELATION
    getProductDeliveryPointsRelations(
        productId: number, request: GetProductDeliveryPointsRelationReq
    ): Observable<ListResponse<ProductDeliveryPointsRelation>> {
        const params = buildHttpParams(request);
        return this.#http.get<ListResponse<ProductDeliveryPointsRelation>>(
            `${this.#PRODUCTS_API}/${productId}/${this.#DELIVERY_POINTS_SEGMENT}`, { params }
        );
    }

    postProductDeliveryPointsRelations(productId: number, request: PostProductDeliveryPointsRelation): Observable<void> {
        return this.#http.post<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#DELIVERY_POINTS_SEGMENT}`, request
        );
    }

    // PRODUCT CHANNELS
    getProductChannels(productId: number): Observable<Partial<ProductChannel>[]> {
        return this.#http.get<Partial<ProductChannel>[]>(`${this.#PRODUCTS_API}/${productId}/${this.#CHANNELS_SEGMENT}`);
    }

    getProductChannel(productId: number, channelId: number): Observable<ProductChannel> {
        return this.#http.get<ProductChannel>(`${this.#PRODUCTS_API}/${productId}/${this.#CHANNELS_SEGMENT}/${channelId}`);
    }

    postProductChannels(productId: number, channels: number[]): Observable<{ channel_ids: number[] }> {
        const body = { channel_ids: channels };
        return this.#http.post<{ channel_ids: number[] }>(
            `${this.#PRODUCTS_API}/${productId}/${this.#CHANNELS_SEGMENT}`,
            body
        );
    }

    deleteProductChannel(productId: number, channelId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#PRODUCTS_API}/${productId}/${this.#CHANNELS_SEGMENT}/${channelId}`);
    }

    putProductChannel(productId: number, channelId: number, reqBody: Partial<PutProductChannel>): Observable<void> {
        return this.#http.put<void>(`${this.#PRODUCTS_API}/${productId}/${this.#CHANNELS_SEGMENT}/${channelId}`, reqBody);
    }

    postRequestProductChannel(productId: number, channelId: number): Observable<void> {
        return this.#http.post<void>(`${this.#PRODUCTS_API}/${productId}/${this.#CHANNELS_SEGMENT}/${channelId}/request-approval`, {});
    }

    // PRODUCT PROMOTIONS
    getProductPromotionsList(productId: number, request: ProductPromotionsListReq): Observable<ListResponse<ProductPromotion>> {
        const params = buildHttpParams(request);
        return this.#http.get<ListResponse<ProductPromotion>>(
            `${this.#PRODUCTS_API}/${productId}/${this.#PROMOTIONS_SEGMENT}`, { params }
        );
    }

    getProductPromotion(productId: number, promotionId: number): Observable<ProductPromotion> {
        return this.#http.get<ProductPromotion>(`${this.#PRODUCTS_API}/${productId}/${this.#PROMOTIONS_SEGMENT}/${promotionId}`);
    }

    postProductPromotion(productId: number, promotion: ProductPromotion): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(`${this.#PRODUCTS_API}/${productId}/${this.#PROMOTIONS_SEGMENT}`, promotion);
    }

    putProductPromotion(productId: number, promotionId: number, promotion: ProductPromotion): Observable<void> {
        return this.#http.put<void>(`${this.#PRODUCTS_API}/${productId}/${this.#PROMOTIONS_SEGMENT}/${promotionId}`, promotion);
    }

    deleteProductPromotion(productId: number, promotionId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#PRODUCTS_API}/${productId}/${this.#PROMOTIONS_SEGMENT}/${promotionId}`);
    }

    cloneProductPromotion(productId: number, promotionId: number): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(`${this.#PRODUCTS_API}/${productId}/${this.#PROMOTIONS_SEGMENT}/${promotionId}/clone`, {});
    }

    // LINKS
    getProductChannelSessionLinks(request: GetProductChannelSessionLinksRequest): Observable<ContentLinkResponse> {
        const params = buildHttpParams({
            limit: request.limit,
            q: request.q,
            offset: request.offset,
            sort: request.sort,
            session_status: request.session_status
        });
        return this.#http.get<ContentLinkResponse>(
            `${this.#PRODUCTS_API}/${request.productId}/${this.#CHANNELS_SEGMENT}/${request.channelId}/language/${request.language}/product-links`,
            { params }
        );
    }
}
