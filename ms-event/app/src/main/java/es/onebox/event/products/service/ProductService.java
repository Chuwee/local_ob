package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.dto.ProducerDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.record.ProductDetailRecord;
import es.onebox.event.priceengine.simulation.record.ProductRecord;
import es.onebox.event.products.converter.ProductRecordConverter;
import es.onebox.event.products.converter.ProductVariantConverter;
import es.onebox.event.products.dao.ProductChannelDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductDeliveryPointRelationDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductEventDeliveryPointDao;
import es.onebox.event.products.dao.ProductLanguageDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dao.ProductVariantStockCouchDao;
import es.onebox.event.products.domain.ProductChannelRecord;
import es.onebox.event.products.domain.ProductDeliveryPointRelationRecord;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.dto.CreateProductDTO;
import es.onebox.event.products.dto.ProductDTO;
import es.onebox.event.products.dto.ProductsDTO;
import es.onebox.event.products.dto.SearchProductFilterDTO;
import es.onebox.event.products.dto.UpdateProductDTO;
import es.onebox.event.products.enums.ProductDeliveryType;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.products.enums.ProductVariantStatus;
import es.onebox.event.products.helper.ProductHelper;
import es.onebox.event.sessions.dao.TaxDao;
import es.onebox.event.surcharges.product.ProductSurchargesService;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final EntitiesRepository entitiesRepository;
    private final ProductDao productDao;
    private final TaxDao taxDao;
    private final ProductVariantDao productVariantDao;
    private final ProductVariantStockCouchDao productVariantStockCouchDao;
    private final ProductLanguageDao productLanguageDao;
    private final ProductChannelDao productChannelDao;
    private final ProductEventDao productEventDao;
    private final ProductSessionDao productSessionDao;
    private final ProductDeliveryPointRelationDao productDeliveryPointRelationDao;
    private final ProductEventDeliveryPointDao productEventDeliveryPointDao;
    private final RefreshDataService refreshDataService;
    private final ProductSurchargesService productSurchargesService;
    private final WebhookService webhookService;
    private final ProductHelper productHelper;


    @Autowired
    public ProductService(EntitiesRepository entitiesRepository, ProductDao productDao, ProductVariantDao productVariantDao,
                          ProductVariantStockCouchDao productVariantStockCouchDao, TaxDao taxDao, ProductLanguageDao productLanguageDao,
                          ProductChannelDao productChannelDao, ProductEventDao productEventDao, ProductSessionDao productSessionDao,
                          ProductDeliveryPointRelationDao productDeliveryPointRelationDao,
                          ProductEventDeliveryPointDao productEventDeliveryPointDao, RefreshDataService refreshDataService,
                          ProductSurchargesService productSurchargesService, WebhookService webhookService, ProductHelper productHelper) {
        this.entitiesRepository = entitiesRepository;
        this.productDao = productDao;
        this.productVariantDao = productVariantDao;
        this.productVariantStockCouchDao = productVariantStockCouchDao;
        this.taxDao = taxDao;
        this.productLanguageDao = productLanguageDao;
        this.productChannelDao = productChannelDao;
        this.productEventDao = productEventDao;
        this.productSessionDao = productSessionDao;
        this.productDeliveryPointRelationDao = productDeliveryPointRelationDao;
        this.productEventDeliveryPointDao = productEventDeliveryPointDao;
        this.refreshDataService = refreshDataService;
        this.productSurchargesService = productSurchargesService;
        this.webhookService = webhookService;
        this.productHelper = productHelper;

    }

    @MySQLWrite
    public Long createProduct(CreateProductDTO product) {
        validateCreation(product);
        CpanelProductRecord cpanelProductRecord = ProductRecordConverter.toRecord(product);
        CpanelProductRecord newProduct = productDao.insert(cpanelProductRecord);
        if (ProductType.SIMPLE.getId() == newProduct.getType()) {
            CpanelProductVariantRecord productVariantRecord = ProductVariantConverter.createDefaultVariant(
                    newProduct.getProductid(), newProduct.getName());
            CpanelProductVariantRecord newProductVariant = productVariantDao.insert(productVariantRecord);
            if (ProductStockType.BOUNDED.equals(product.getStockType())) {
                productVariantStockCouchDao.insert(newProduct.getProductid().longValue(),
                        newProductVariant.getVariantid().longValue());
            }
        }
        productSurchargesService.initProductSurcharges(cpanelProductRecord);
        postUpdateProduct(newProduct.getProductid().longValue(), false);
        return newProduct.getProductid().longValue();
    }

    @MySQLWrite
    public void updateProduct(Long productId, UpdateProductDTO updateProductDTO) {
        CpanelProductRecord productRecord = getAndCheckProductRecord(productId);
        List<Long> taxesList = taxDao.getTaxesByEntity(productRecord.getEntityid().longValue());
        List<CpanelProductVariantRecord> productVariants = productVariantDao.getProductVariantsByProductId(productId);

        boolean postUpdatePublish = false;

        if (updateProductDTO.getProductState() != null && !productRecord.getState().equals(updateProductDTO.getProductState().getId())) {
            postUpdatePublish = true;
            if (updateProductDTO.getProductState().equals(ProductState.ACTIVE)) {
                validateProductActivation(productRecord);
            }
            if (updateProductDTO.getProductState().equals(ProductState.DELETED)) {
                checkProductSales(productId);
            }
        }

        if (updateProductDTO.getName() != null) {
            if (ProductType.SIMPLE.getId() == productRecord.getType()) {
                if (productVariants == null || productVariants.size() != 1) {
                    throw new OneboxRestException(MsEventErrorCode.ILLEGAL_VARIANT_AMOUNT);
                }

                CpanelProductVariantRecord variant = productVariants.get(0);
                variant.setName(updateProductDTO.getName());
                productVariantDao.update(variant);

            }
            productRecord.setName(updateProductDTO.getName());
        }

        if (updateProductDTO.getTaxId() != null) {
            if (productRecord.getState().equals(ProductState.ACTIVE.getId())) {
                throw new OneboxRestException(MsEventErrorCode.TAX_NOT_UPDATABLE_ON_PRODUCT);
            }

            Long newTax = taxesList.stream()
                    .filter(tax -> tax.equals(updateProductDTO.getTaxId()))
                    .findFirst()
                    .orElse(null);

            if (newTax != null) {
                productRecord.setTaxid(newTax.intValue());
            } else {
                throw OneboxRestException.builder(MsEventErrorCode.INVALID_ENTITY_TAX).build();
            }
        }
        if (updateProductDTO.getSurchargeTaxId() != null) {
            if (productRecord.getState().equals(ProductState.ACTIVE.getId())) {
                throw new OneboxRestException(MsEventErrorCode.TAX_NOT_UPDATABLE_ON_PRODUCT);
            }

            Long newSurchargeTax = taxesList.stream()
                    .filter(surcharge_tax -> surcharge_tax.equals(updateProductDTO.getSurchargeTaxId()))
                    .findFirst()
                    .orElse(null);

            if (newSurchargeTax != null) {
                productRecord.setSurchagetaxid(newSurchargeTax.intValue());
            } else {
                throw OneboxRestException.builder(MsEventErrorCode.INVALID_ENTITY_SURCHARGE_TAX).build();
            }
        }


        List<Long> variantIds = productVariants.stream().map(variant -> variant.getVariantid().longValue()).collect(Collectors.toList());
        boolean hasSales = productHelper.checkProductOrVariantSales(variantIds);
        if (updateProductDTO.getCurrencyId() != null) {
            if (productRecord.getState().equals(ProductState.ACTIVE.getId()) || hasSales) {
                throw new OneboxRestException(MsEventErrorCode.CURRENCY_NOT_UPDATABLE_ON_PRODUCT);
            }
            productRecord.setIdcurrency(updateProductDTO.getCurrencyId().intValue());
        }

        if (hasSales && updateProductDTO.getTaxMode() != null && !updateProductDTO.getTaxMode().getId().equals(productRecord.getTaxmode())) {
            throw new OneboxRestException(MsEventErrorCode.TAX_MODE_NOT_UPDATABLE_ON_PRODUCT);
        }

        if (updateProductDTO.getProductState() != null) {
            productRecord.setState(updateProductDTO.getProductState().getId());
        }

        if (updateProductDTO.getTicketTemplateId() != null) {
            productRecord.setTickettemplateid(updateProductDTO.getTicketTemplateId().intValue());
        }

        if (updateProductDTO.getHideDeliveryPoint() != null) {
            productRecord.setHidedeliverypoint(ConverterUtils.isTrueAsByte(BooleanUtils.isTrue(updateProductDTO.getHideDeliveryPoint())));
        }
        if (updateProductDTO.getHideDeliveryDateTime() != null) {
            productRecord.setHidedeliverydatetime(ConverterUtils.isTrueAsByte(BooleanUtils.isTrue(updateProductDTO.getHideDeliveryDateTime())));
        }

        if (updateProductDTO.getCategory() != null && updateProductDTO.getCategory().getId() != null) {
            productRecord.setTaxonomyid(updateProductDTO.getCategory().getId().intValue());
        }
        if (updateProductDTO.getCustomCategory() != null && updateProductDTO.getCustomCategory().getId() != null) {
            productRecord.setCustomtaxonomyid(updateProductDTO.getCustomCategory().getId().intValue());
        }
        if (updateProductDTO.getTaxMode() != null) {
            if (productRecord.getState().equals(ProductState.ACTIVE.getId()) && !updateProductDTO.getTaxMode().getId().equals(productRecord.getTaxmode())) {
                throw new OneboxRestException(MsEventErrorCode.PRODUCT_UPDATE_TAX_MODE_INVALID_PRODUCT_STATUS);
            }
            productRecord.setTaxmode(updateProductDTO.getTaxMode().getId());
        }

        productDao.update(productRecord);
        postUpdateProduct(productId, postUpdatePublish);
    }

    @MySQLRead
    public ProductDTO getProduct(Long productId) {
        ProductDetailRecord productRecord = productDao.findProductDetails(productId.intValue());
        if (productRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }

        return ProductRecordConverter.fromEntity(productRecord);
    }

    @MySQLRead
    public ProductsDTO searchProducts(SearchProductFilterDTO searchProductFilterDTO) {
        ProductsDTO productsDTO = new ProductsDTO();
        List<ProductRecord> products = productDao.getProducts(searchProductFilterDTO);
        if (products != null) {
            productsDTO.setData(ProductRecordConverter.toDTOs(products));
            Long total = productDao.getTotalProducts(searchProductFilterDTO);
            productsDTO.setMetadata(MetadataBuilder.build(searchProductFilterDTO, total));

            return productsDTO;
        }

        return null;
    }

    private void validateCreation(CreateProductDTO product) {
        if (ProductStockType.SESSION_BOUNDED.equals(product.getStockType()) && !ProductType.SIMPLE.equals(product.getProductType())) {
            throw OneboxRestException.builder(MsEventErrorCode.PRODUCT_INVALID_TYPE_FOR_SESSION_BOUNDED).build();
        }
        //entity validations
        EntityDTO entity = entitiesRepository.getEntity(product.getEntityId().intValue());
        if (entity == null) {
            throw OneboxRestException.builder(MsEventErrorCode.ENTITY_NOT_FOUND).build();
        }

        //producer validations
        ProducerDTO producer = entitiesRepository.getProducer(product.getProducerId().intValue());
        if (producer == null) {
            throw OneboxRestException.builder(MsEventErrorCode.PRODUCER_NOT_FOUND).build();
        }
        List<CpanelProductRecord> currentProducts = productDao.findProducts(product.getEntityId(), product.getName());
        if (currentProducts != null && !currentProducts.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NAME_ALREADY_IN_USE);
        }
    }

    @MySQLRead
    public CpanelProductRecord getAndCheckProductRecord(Long productId) {
        CpanelProductRecord productRecord = productDao.findById(productId.intValue());
        if (productRecord == null || productRecord.getState() == ProductState.DELETED.getId()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        return productRecord;
    }

    private void validateProductActivation(CpanelProductRecord productRecord) {
        if (productRecord.getTaxmode() == null) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_PRODUCT_TAX_MODE);
        }

        if (productRecord.getTaxid() == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_TAX_NOT_FOUND);
        }

        if (productRecord.getSurchagetaxid() == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_SURCHARGE_TAX_NOT_FOUND);
        }

        if (productRecord.getDeliverytype() == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_DELIVERY_TYPE_REQUIRED);
        }
        ProductDeliveryType pdt = ProductDeliveryType.get(productRecord.getDeliverytype());
        if (pdt == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_DELIVERY_TYPE_REQUIRED);
        }

        if ((productRecord.getDeliverystarttimeunit() == null || productRecord.getDeliveryendtimeunit() == null ||
                productRecord.getDeliverystarttimevalue() == null || productRecord.getDeliveryendtimevalue() == null)
                && !productRecord.getDeliverytype().equals(ProductDeliveryType.FIXED_DATES.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_DELIVERY_PERIOD_REQUIRED);
        }

        if ((productRecord.getDeliverydatefrom() == null || productRecord.getDeliverydateto() == null) &&
                productRecord.getDeliverytype().equals(ProductDeliveryType.FIXED_DATES.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_DELIVERY_PERIOD_REQUIRED);
        }

        if (ProductDeliveryType.PURCHASE.equals(pdt)) {
            List<ProductDeliveryPointRelationRecord> deliveryPoints = productDeliveryPointRelationDao.findByProductId(
                    productRecord.getProductid().longValue());
            if (CollectionUtils.isEmpty(deliveryPoints)) {
                throw new OneboxRestException(MsEventErrorCode.PRODUCT_DELIVERY_POINT_RELATION_NOT_FOUND);
            }
        }

        if (ProductDeliveryType.SESSION.equals(pdt)) {
            if (productEventDeliveryPointDao.existsProductEventWithoutDelivery(productRecord.getProductid())) {
                throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_DELIVERY_POINT_DEFAULT_REQUIRED);
            }
        }

        List<ProductLanguageRecord> productLanguageRecords = productLanguageDao.findByProductId(productRecord.getProductid().longValue());
        if (CollectionUtils.isEmpty(productLanguageRecords)) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGE_DEFAULT_REQUIRED);
        }
        productLanguageRecords.stream()
                .filter(pl -> ConverterUtils.isByteAsATrue(pl.getDefaultlanguage()))
                .findAny().orElseThrow(() -> new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGE_DEFAULT_REQUIRED));

        List<ProductChannelRecord> productChannelRecords = productChannelDao.findByProductId(productRecord.getProductid().longValue());
        if (CollectionUtils.isEmpty(productChannelRecords)) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_CHANNELS_NOT_FOUND);
        }

        List<ProductEventRecord> productEvents = productEventDao.findByProductId(productRecord.getProductid(), false);
        if (CollectionUtils.isEmpty(productEvents)) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENTS_REQUIRED);
        }

        List<CpanelProductVariantRecord> productVariantRecords = productVariantDao.getProductVariantsByProductId(productRecord.getProductid().longValue());

        if (CollectionUtils.isEmpty(productVariantRecords)) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_NOT_FOUND);
        }

        boolean hasActiveVariant = productVariantRecords.stream()
                .anyMatch(v -> v.getStatus().equals(ProductVariantStatus.ACTIVE.getId()));

        if (ProductType.VARIANT.getId() == productRecord.getType() && !hasActiveVariant) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_MISSING_ACTIVE_VARIANT);
        }

        if (ProductType.SIMPLE.getId() == productRecord.getType() && productVariantRecords.size() > 1) {
            throw new OneboxRestException(MsEventErrorCode.ILLEGAL_VARIANT_AMOUNT);
        }

        for (CpanelProductVariantRecord productVariantRecord : productVariantRecords) {
            if (productVariantRecord.getPrice() == null) {
                throw new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_PRICE_REQUIRED);
            }
        }
    }

    @MySQLWrite
    public void deleteProduct(Long productId) {
        CpanelProductRecord cpanelProductRecord = getAndCheckProductRecord(productId);
        checkProductSales(productId);

        cpanelProductRecord.setState(ProductState.DELETED.getId());
        productDao.update(cpanelProductRecord);
        postUpdateProduct(productId, true);
    }

    private void checkProductSales(Long productId) {
        CpanelProductRecord productRecord = productDao.findById(productId.intValue());
        if (productRecord.getState().equals(ProductState.ACTIVE.getId())) {
            List<CpanelProductVariantRecord> productVariants = productVariantDao.getProductVariantsByProductId(productId);
            if (productVariants != null && !productVariants.isEmpty()) {
                List<Integer> variantIds = productVariants.stream().map(CpanelProductVariantRecord::getVariantid).collect(Collectors.toList());
                List<Long> variantIdsLng = variantIds.stream().map(Long::valueOf).collect(Collectors.toList());
                boolean hasSales = productHelper.checkProductOrVariantSales(variantIdsLng);
                if (hasSales) {
                    throw new OneboxRestException(MsEventErrorCode.PRODUCT_HAS_SALES);
                }
            }
        }
    }

    private void postUpdateProduct(Long productId, boolean postUpdatePublish) {
        // update products catalog
        refreshDataService.refreshProduct(productId);

        // update channel-session published products
        if (postUpdatePublish) {
            List<ProductEventRecord> productEventRecords = productEventDao.findByProductId(productId.intValue(), false);
            List<Long> eventIds = productEventRecords.stream().map(ProductEventRecord::getEventid).map(Integer::longValue).collect(Collectors.toList());
            Set<Integer> validatedEventIds = productSessionDao.findRelatedEvents(productId, eventIds, null);
            for (Integer eventId : validatedEventIds) {
                refreshDataService.refreshEvent(eventId.longValue(), "productService.postUpdateProduct");
            }

            Map<Long, List<Long>> publishedSessions = productSessionDao.findPublishedSessions(productId, null, eventIds, null);
            if (MapUtils.isNotEmpty(publishedSessions)) {
                for (Map.Entry<Long, List<Long>> sessionsByEvent : publishedSessions.entrySet()) {
                    refreshDataService.refreshSessions(sessionsByEvent.getKey(), sessionsByEvent.getValue(), "productService.postUpdateProduct");
                }
            }
        }

        webhookService.sendProductNotification(productId, NotificationSubtype.PRODUCT_GENERAL_DATA);
    }
}
