import { ListResponse, StateProperty } from '@OneboxTM/utils-state';
import { ContentLinkResponse } from '@admin-clients/cpanel/shared/data-access';
import { FilterOption } from '@admin-clients/shared/data-access/models';
import { ItemCache } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { ProductPromotion } from '../models/get-product-promotions-list.model';
import {
    ProductAttribute, ProductAttributeChannelContents, ProductAttributeValue, ProductAttributeValueListChannelContents
} from '../models/product-attribute.model';
import { ProductChannelImageContent } from '../models/product-channel-content-image.model';
import { ProductChannelTextContent } from '../models/product-channel-content-text.model';
import { ProductChannel } from '../models/product-channel.model';
import { ProductDeliveryConfig } from '../models/product-delivery-config.model';
import { ProductDeliveryPointsRelation } from '../models/product-delivery-points-relation.model';
import { ProductLanguage } from '../models/product-language.model';
import { ProductSurcharge } from '../models/product-surcharge.model';
import { ProductVariant } from '../models/product-variant.model';
import { Product } from '../models/product.model';

@Injectable()
export class ProductsState {
    readonly product = new StateProperty<Product>();
    readonly productsList = new StateProperty<ListResponse<Product>>();
    readonly productCache = new ItemCache<FilterOption>();
    readonly productSurcharges = new StateProperty<ProductSurcharge[]>();
    readonly productLanguages = new StateProperty<ProductLanguage[]>();
    readonly productVariants = new StateProperty<ListResponse<ProductVariant>>();
    // productVariantsTable is a workaround for the variants table and
    // will be removed when angular v19 makes available the routerOutletData to pass data to router-outlet child
    readonly productVariantsTable = new StateProperty<ListResponse<ProductVariant>>();
    readonly productAttributes = new StateProperty<ProductAttribute[]>();
    readonly productAttribute = new StateProperty<ProductAttribute>();
    readonly productAttributeValues = new StateProperty<ListResponse<ProductAttributeValue>>();
    readonly productAttributeValue = new StateProperty<ProductAttributeValue>();
    readonly attributeChannelContents = new StateProperty<ProductAttributeChannelContents[]>();
    readonly attributeValueChannelContents = new StateProperty<ProductAttributeChannelContents[]>();
    readonly attributeValuesChannelContents = new StateProperty<ProductAttributeValueListChannelContents[]>();
    readonly channelTextContents = new StateProperty<ProductChannelTextContent[]>();
    readonly channelImageContents = new StateProperty<ProductChannelImageContent[]>();
    readonly deliveryConfig = new StateProperty<ProductDeliveryConfig>();
    readonly productPromotion = new StateProperty<ProductPromotion>();
    readonly productPromotionsList = new StateProperty<ListResponse<ProductPromotion>>();
    readonly productDeliveryPointRelationList = new StateProperty<ListResponse<ProductDeliveryPointsRelation>>();
    readonly productChannelsList = new StateProperty<Partial<ProductChannel>[]>();
    readonly productChannel = new StateProperty<ProductChannel>();
    readonly productChannelPublishedSessionLinks = new StateProperty<ContentLinkResponse>();
    readonly productChannelUnpublishedSessionLinks = new StateProperty<ContentLinkResponse>();
    readonly requestProductChannel = new StateProperty<void>();
}
