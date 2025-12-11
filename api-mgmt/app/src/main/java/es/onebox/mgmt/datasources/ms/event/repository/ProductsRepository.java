package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.ProductLiterals;
import es.onebox.mgmt.datasources.ms.event.dto.ProductTicketLiterals;
import es.onebox.mgmt.datasources.ms.event.dto.ProductValueLiterals;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.event.ProductEventsFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.AddProductEvents;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateDeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProduct;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductAttribute;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductAttributeValue;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductChannels;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductChannelsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductCommunicationElementImage;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductCommunicationElementsText;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannel;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannelSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannelSessionsFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSurcharge;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductChannel;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSession;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpsertProductDeliveryPointRelation;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttribute;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributeValue;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributeValues;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributes;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannels;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductCommunicationElementsImage;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductCommunicationElementsText;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDelivery;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDeliveryPointRelation;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDeliveryPointRelations;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductEventDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductEvents;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductLanguages;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductPublishingSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessionDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductVariant;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductVariants;
import es.onebox.mgmt.datasources.ms.event.dto.products.Products;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchDeliveryPointFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductDeliveryPointRelationFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductVariantsFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateDeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProduct;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductEvent;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductEventDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductLanguages;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSessionDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductVariant;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductVariantPrices;
import es.onebox.mgmt.products.dto.ProductSessionDeliveryPointsFilterDTO;
import es.onebox.mgmt.products.dto.ProductSessionSearchFilterDTO;
import es.onebox.mgmt.products.dto.SearchProductAttributeValueFilterDTO;
import es.onebox.mgmt.products.enums.ProductCommunicationElementsImagesType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductsRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public ProductsRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public Long createProduct(CreateProduct product) {
        return msEventDatasource.createProduct(product);
    }

    public Product getProduct(Long productId) {
        return msEventDatasource.getProduct(productId);
    }

    public Long createDeliveryPoint(CreateDeliveryPoint createDeliveryPoint) {
        return msEventDatasource.createDeliveryPoint(createDeliveryPoint);
    }

    public DeliveryPoint getDeliveryPoint(Long deliveryPointId) {
        return msEventDatasource.getDeliveryPoint(deliveryPointId);
    }

    public DeliveryPoints searchDeliveryPoint(SearchDeliveryPointFilter searchDeliveryPointFilter) {
        return msEventDatasource.searchDeliveryPoint(searchDeliveryPointFilter);
    }

    public void deleteDeliveryPoint(Long deliveryPointId) {
        msEventDatasource.deleteDeliveryPoint(deliveryPointId);
    }

    public DeliveryPoint updateDeliveryPoint(Long deliveryPointId, UpdateDeliveryPoint updateDeliveryPoint) {
        return msEventDatasource.updateDeliveryPoint(deliveryPointId, updateDeliveryPoint);
    }

    public void updateProduct(Long productId, UpdateProduct updateProduct) {
        msEventDatasource.updateProduct(productId, updateProduct);
    }

    public ProductLanguages getProductLanguages(Long productId) {
        return msEventDatasource.getProductLanguages(productId);
    }

    public ProductLanguages updateProductLanguages(Long productId, UpdateProductLanguages updateProductLanguages) {
        return msEventDatasource.updateProductLanguages(productId, updateProductLanguages);
    }

    public ProductCommunicationElementsText createProductCommunicationElementsText(Long productId, CreateProductCommunicationElementsText createProductCommunicationElementsText) {
        return msEventDatasource.createProductCommunicationElementsText(productId, createProductCommunicationElementsText);
    }

    public ProductCommunicationElementsText getProductCommunicationElementsText(Long productId) {
        return msEventDatasource.getProductCommunicationElementsText(productId);
    }

    public void createProductCommunicationElementsImage(Long productId, List<CreateProductCommunicationElementImage> createProductCommunicationElementImages) {
        msEventDatasource.createProductCommunicationElementsImage(productId, createProductCommunicationElementImages);
    }

    public ProductCommunicationElementsImage getProductCommunicationElementsImages(Long productId) {
        return msEventDatasource.getProductCommunicationElementsImages(productId);
    }

    public void deleteProductCommunicationElementImage(Long productId, String language, ProductCommunicationElementsImagesType type, Integer position) {
        msEventDatasource.deleteProductCommunicationElementImage(productId, language, type, position);
    }

    public ProductVariants searchProductVariants(Long productId, SearchProductVariantsFilter filter) {
        return msEventDatasource.searchProductVariants(productId, filter);
    }

    public ProductVariant getProductVariant(Long productId, Long variantId) {
        return msEventDatasource.getProductVariant(productId, variantId);
    }

    public void updateProductVariant(Long productId, Long variantId, UpdateProductVariant productVariant) {
        msEventDatasource.updateProductVariant(productId, variantId, productVariant);
    }

    public void updateProductVariantPrices(Long productId, UpdateProductVariantPrices updateProductVariantPrices) {
        msEventDatasource.updateProductVariantPrices(productId, updateProductVariantPrices);
    }

    public void deleteProduct(Long productId) {
        msEventDatasource.deleteProduct(productId);
    }

    public Products searchProducts(SearchProductFilter searchProductFilter) {
        return msEventDatasource.searchProducts(searchProductFilter);
    }

    public ProductChannels getProductChannels(Long productId) {
        return msEventDatasource.getProductChannels(productId);
    }

    public ProductChannel getProductChannel(Long productId, Long channelId) {
        return msEventDatasource.getProductChannel(productId, channelId);
    }

    public CreateProductChannelsResponse createProductChannels(Long productId, CreateProductChannels createProductChannels) {
        return msEventDatasource.createProductChannels(productId, createProductChannels);
    }

    public void updateProductChannel(Long productId, Long channelId, UpdateProductChannel updateProductChannel) {
        msEventDatasource.updateProductChannel(productId, channelId, updateProductChannel);
    }

    public void deleteProductChannel(Long productId, Long channelId) {
        msEventDatasource.deleteProductChannel(productId, channelId);
    }

    public ProductDelivery getProductDelivery(Long productId) {
        return msEventDatasource.getProductDelivery(productId);
    }

    public ProductDelivery updateProductDelivery(Long productId, ProductDelivery productDelivery) {
        return msEventDatasource.updateProductDelivery(productId, productDelivery);
    }

    public void upsertProductDeliveryPointRelation(Long productId, UpsertProductDeliveryPointRelation upsertProductDeliveryPointRelation) {
        msEventDatasource.upsertProductDeliveryPointRelation(productId, upsertProductDeliveryPointRelation);
    }

    public ProductDeliveryPointRelation getProductDeliveryPointRelation(Long productId, Long deliveryPointId) {
        return msEventDatasource.getProductDeliveryPointRelation(productId, deliveryPointId);
    }

    public ProductDeliveryPointRelations searchProductDeliveryPointRelations(Long productId, SearchProductDeliveryPointRelationFilter searchProductDeliveryPointRelationFilter) {
        return msEventDatasource.searchProductDeliveryPointRelations(productId, searchProductDeliveryPointRelationFilter);
    }

    public Long createProductAttribute(Long productId, CreateProductAttribute productAttribute) {
        return msEventDatasource.createProductAttribute(productId, productAttribute);
    }

    public ProductAttribute getProductAttribute(Long productId, Long attributeId) {
        return msEventDatasource.getProductAttribute(productId, attributeId);
    }

    public ProductAttributes getProductAttributes(Long productId) {
        return msEventDatasource.getProductAttributes(productId);
    }

    public void updateProductAttribute(Long productId, Long attributeId, ProductAttribute productAttribute) {
        msEventDatasource.updateProductAttribute(productId, attributeId, productAttribute);
    }

    public void deleteProductAttribute(Long productId, Long attributeId) {
        msEventDatasource.deleteProductAttribute(productId, attributeId);
    }

    public ProductEventDeliveryPoints getProductEventDeliveryPoints(Long productId, Long eventId) {
        return msEventDatasource.getProductEventDeliveryPoints(productId, eventId);
    }

    public ProductEventDeliveryPoints updateProductEventDeliveryPoints(Long productId, Long eventId, UpdateProductEventDeliveryPoints updateProductEventDeliveryPoints) {
        return msEventDatasource.updateProductEventDeliveryPoints(productId, eventId, updateProductEventDeliveryPoints);
    }

    public ProductEvents addProductEvents(Long productId, AddProductEvents productEvents) {
        return msEventDatasource.createProductEvents(productId, productEvents);
    }

    public void deleteProductEvent(Long productId, Long eventId) {
        msEventDatasource.deleteProductEvent(productId, eventId);
    }

    public ProductEvents getProductEvents(Long productId, ProductEventsFilter filter) {
        return msEventDatasource.getProductEvents(productId, filter);
    }

    public void updateProductEvents(Long productId, Long eventId, UpdateProductEvent updateProductEvent) {
        msEventDatasource.updateProductEvents(productId, eventId, updateProductEvent);
    }

    public ProductSessionDeliveryPoints getProductSessionDeliveryPoints(Long productId, Long eventId,
                                                                        ProductSessionDeliveryPointsFilterDTO filter) {
        return msEventDatasource.getProductSessionDeliveryPoints(productId, eventId, filter);
    }

    public ProductSessionDeliveryPoints updateProductSessionDeliveryPoints(Long productId, Long eventId, UpdateProductSessionDeliveryPoints updateProductSessionDeliveryPoints) {
        return msEventDatasource.updateProductSessionDeliveryPoints(productId, eventId, updateProductSessionDeliveryPoints);
    }

    public void updateProductSessions(Long productId, Long eventId,
                                      UpdateProductSessions updateProductSessions) {
        msEventDatasource.updateProductSessions(productId, eventId, updateProductSessions);
    }

    public ProductPublishingSessions getProductPublishingSessions(Long productId, Long eventId) {
        return msEventDatasource.getProductPublishingSessions(productId, eventId);
    }

    public ProductChannelSessions getProductChannelSessions(Long productId, Long channelId, ProductChannelSessionsFilter filter) {
        return msEventDatasource.getProductChannelSessions(productId, channelId, filter);
    }

    public void updateProductSession(Long productId, Long eventId, Long sessionId, UpdateProductSession updateProductSession) {
        msEventDatasource.updateProductSession(productId, eventId, sessionId, updateProductSession);
    }

    public ProductSessions getProductSessions(Long productId, Long eventId, ProductSessionSearchFilterDTO filter) {
        return msEventDatasource.getProductSessions(productId, eventId, filter);
    }

    public ProductAttributeValue getProductAttributeValue(Long productId, Long attributeId, Long valueId) {
        return msEventDatasource.getProductAttributeValue(productId, attributeId, valueId);
    }

    public Long createProductAttributeValue(Long productId, Long attributeId, CreateProductAttributeValue productAttributeValue) {
        return msEventDatasource.createProductAttributeValue(productId, attributeId, productAttributeValue);
    }

    public ProductAttributeValues getProductAttributeValues(Long productId, Long attributeId,
                                                            SearchProductAttributeValueFilterDTO searchProductAttributeValueFilterDTO) {
        return msEventDatasource.getProductAttributeValues(productId, attributeId, searchProductAttributeValueFilterDTO);
    }

    public void updateProductAttributeValue(Long productId, Long attributeId, Long valueId, ProductAttributeValue productAttributeValue) {
        msEventDatasource.updateProductAttributeValue(productId, attributeId, valueId, productAttributeValue);
    }

    public void deleteProductAttributeValue(Long productId, Long attributeId, Long valueId) {
        msEventDatasource.deleteProductAttributeValue(productId, attributeId, valueId);
    }

    public List<IdNameDTO> createVariantProductVariants(Long productId) {
        return msEventDatasource.createVariantProductVariants(productId);
    }

    public ProductLiterals getProductAttributeLiterals(Long productId, Long attributeId, String languageCode) {
        return this.msEventDatasource.getProductAttributeLiterals(productId, attributeId, languageCode);
    }

    public void createOrUpdateProductAttributeLiterals(Long productId, Long attributeId, ProductLiterals body) {
        this.msEventDatasource.createOrUpdateProductAttributeLiterals(productId, attributeId, body);
    }

    public ProductLiterals getProductValueLiterals(Long productId, Long attributeId, Long valueId, String languageCode) {
        return this.msEventDatasource.getProductValueLiterals(productId, attributeId, valueId, languageCode);
    }

    public ProductValueLiterals getProductBulkValueLiterals(Long productId, Long attributeId, String languageCode) {
        return this.msEventDatasource.getProductBulkValueLiterals(productId, attributeId, languageCode);
    }

    public void createOrUpdateProductValueLiterals(Long productId, Long attributeId, Long valueId, ProductLiterals body) {
        this.msEventDatasource.createOrUpdateProductValueLiterals(productId, attributeId, valueId, body);
    }

    public void createOrUpdateProductBulkValueLiterals(Long productId, Long attributeId, ProductValueLiterals body) {
        this.msEventDatasource.createOrUpdateProductBulkValueLiterals(productId, attributeId, body);
    }

    public ProductTicketLiterals getProductTicketLiterals(Long productId, String languageCode, String key) {
        return this.msEventDatasource.getProductTicketLiterals(productId, languageCode, key);
    }

    public void createOrUpdateProductTicketLiterals(Long productId, String languageCode, ProductTicketLiterals body) {
        this.msEventDatasource.createOrUpdateProductTicketLiterals(productId, languageCode, body);
    }

    public void setSurcharge(Long productId, List<ProductSurcharge> requests) {
        msEventDatasource.setProductSurcharge(productId, requests);
    }

    public List<ProductSurcharge> getSurcharges(Long productId, List<SurchargeTypeDTO> types) {
        return msEventDatasource.getProductSurcharges(productId, types);
    }
}
