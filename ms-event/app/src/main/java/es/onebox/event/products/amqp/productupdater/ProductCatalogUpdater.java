package es.onebox.event.products.amqp.productupdater;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.config.LocalCache;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.priceengine.simulation.record.ProductRecord;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRange;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRanges;
import es.onebox.event.priceengine.taxes.utils.TaxSimulationUtils;
import es.onebox.event.products.dao.ChannelProductsCouchDao;
import es.onebox.event.products.dao.DeliveryPointDao;
import es.onebox.event.products.dao.ProductAttributeContentsCouchDao;
import es.onebox.event.products.dao.ProductAttributeDao;
import es.onebox.event.products.dao.ProductAttributeValueDao;
import es.onebox.event.products.dao.ProductCatalogCouchDao;
import es.onebox.event.products.dao.ProductChannelDao;
import es.onebox.event.products.dao.ProductCommunicationElementCouchDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductDeliveryPointRelationDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductEventDeliveryPointDao;
import es.onebox.event.products.dao.ProductLanguageDao;
import es.onebox.event.products.dao.ProductPromotionDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.dao.ProductSessionDeliveryPointDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dao.ProductVariantSessionDao;
import es.onebox.event.products.dao.ProductVariantSessionStockCouchDao;
import es.onebox.event.products.dao.couch.ChannelProductDocument;
import es.onebox.event.products.dao.couch.DeliveryPoint;
import es.onebox.event.products.dao.couch.EventSessionsDeliveryPoints;
import es.onebox.event.products.dao.couch.ProductCatalogDocument;
import es.onebox.event.products.dao.couch.ProductCatalogVariant;
import es.onebox.event.products.dao.couch.ProductCatalogVariantPrice;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDocument;
import es.onebox.event.products.dao.couch.ProductContentDocument;
import es.onebox.event.products.domain.DeliveryPointRecord;
import es.onebox.event.products.domain.ProductChannelRecord;
import es.onebox.event.products.domain.ProductDeliveryPointRelationRecord;
import es.onebox.event.products.domain.ProductEventDeliveryPointRecord;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.domain.ProductSessionDeliveryPointRecord;
import es.onebox.event.products.domain.ProductSessionRecord;
import es.onebox.event.products.domain.ProductVariantRecord;
import es.onebox.event.products.dto.ProductTaxInfo;
import es.onebox.event.products.dto.SearchProductVariantsFilterDTO;
import es.onebox.event.products.enums.ProductDeliveryTimeUnitType;
import es.onebox.event.products.enums.ProductDeliveryType;
import es.onebox.event.products.enums.ProductEventStatus;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.products.enums.ProductVariantStatus;
import es.onebox.event.products.enums.SaleRequestsStatus;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.TaxDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.surcharges.product.ProductSurcharges;
import es.onebox.event.surcharges.product.dao.RangeProductSurchargeDao;
import es.onebox.event.surcharges.product.dao.RangeProductSurchargePromotionDao;
import es.onebox.event.taxonomy.dao.BaseTaxonomyDao;
import es.onebox.event.taxonomy.dao.CustomTaxonomyDao;
import es.onebox.jooq.cpanel.tables.records.CpanelChannelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeValueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantSessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPromocionProductoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.math.NumberUtils.DOUBLE_ZERO;

@Service
public class ProductCatalogUpdater {

    private final SessionDao sessionDao;
    private final ProductDao productDao;
    private final TaxDao taxDao;
    private final ProductLanguageDao productLanguagesDao;
    private final DeliveryPointDao deliveryPointDao;
    private final RangeProductSurchargeDao rangeProductSurchargeDao;
    private final RangeProductSurchargePromotionDao rangeProductSurchargePromotionDao;
    private final ProductDeliveryPointRelationDao productDeliveryPointRelationDao;
    private final ProductVariantDao productVariantDao;
    private final ProductVariantSessionDao productVariantSessionDao;
    private final ProductAttributeDao productAttributeDao;
    private final ProductAttributeValueDao productAttributeValueDao;
    private final ProductEventDao productEventDao;
    private final ProductChannelDao productChannelDao;
    private final ProductSessionDao productSessionDao;
    private final ProductCommunicationElementCouchDao productCommunicationElementCouchDao;
    private final ProductCatalogCouchDao productCatalogCouchDao;
    private final ChannelProductsCouchDao channelProductsCouchDao;
    private final ProductSessionDeliveryPointDao productSessionDeliveryPointDao;
    private final ProductEventDeliveryPointDao productEventDeliveryPointDao;
    private final ProductAttributeContentsCouchDao productAttributeContentsCouchDao;
    private final ProductPromotionDao productPromotionDao;
    private final ProductVariantSessionStockCouchDao productVariantSessionStockCouchDao;
    private final StaticDataContainer staticDataContainer;
    private final CacheRepository localCacheRepository;
    private final CustomTaxonomyDao customTaxonomyDao;


    @Autowired
    public ProductCatalogUpdater(SessionDao sessionDao, ProductDao productDao, ProductLanguageDao productLanguagesDao,
                                 DeliveryPointDao deliveryPointDao, RangeProductSurchargeDao rangeProductSurchargeDao,
                                 RangeProductSurchargePromotionDao rangeProductSurchargePromotionDao,
                                 ProductVariantDao productVariantDao, ProductVariantSessionDao productVariantSessionDao,
                                 ProductAttributeDao productAttributeDao,
                                 ProductAttributeValueDao productAttributeValueDao, ProductEventDao productEventDao,
                                 ProductChannelDao productChannelDao, ProductSessionDao productSessionDao,
                                 ProductCommunicationElementCouchDao productCommunicationElementCouchDao,
                                 ProductCatalogCouchDao productCatalogCouchDao,
                                 ProductSessionDeliveryPointDao productSessionDeliveryPointDao,
                                 ProductEventDeliveryPointDao productEventDeliveryPointDao,
                                 ProductDeliveryPointRelationDao productDeliveryPointRelationDao,
                                 ChannelProductsCouchDao channelProductsCouchDao, TaxDao taxDao,
                                 ProductAttributeContentsCouchDao productAttributeContentsCouchDao,
                                 ProductPromotionDao productPromotionDao,
                                 ProductVariantSessionStockCouchDao productVariantSessionStockCouchDao,
                                 StaticDataContainer staticDataContainer, CacheRepository localCacheRepository, CustomTaxonomyDao customTaxonomyDao) {
        this.sessionDao = sessionDao;
        this.productDao = productDao;
        this.productLanguagesDao = productLanguagesDao;
        this.deliveryPointDao = deliveryPointDao;
        this.rangeProductSurchargeDao = rangeProductSurchargeDao;
        this.rangeProductSurchargePromotionDao = rangeProductSurchargePromotionDao;
        this.productVariantDao = productVariantDao;
        this.productVariantSessionDao = productVariantSessionDao;
        this.productAttributeDao = productAttributeDao;
        this.productAttributeValueDao = productAttributeValueDao;
        this.productEventDao = productEventDao;
        this.productChannelDao = productChannelDao;
        this.productSessionDao = productSessionDao;
        this.productCommunicationElementCouchDao = productCommunicationElementCouchDao;
        this.productCatalogCouchDao = productCatalogCouchDao;
        this.productSessionDeliveryPointDao = productSessionDeliveryPointDao;
        this.productEventDeliveryPointDao = productEventDeliveryPointDao;
        this.productDeliveryPointRelationDao = productDeliveryPointRelationDao;
        this.channelProductsCouchDao = channelProductsCouchDao;
        this.taxDao = taxDao;
        this.productAttributeContentsCouchDao = productAttributeContentsCouchDao;
        this.productPromotionDao = productPromotionDao;
        this.productVariantSessionStockCouchDao = productVariantSessionStockCouchDao;
        this.staticDataContainer = staticDataContainer;
        this.localCacheRepository = localCacheRepository;
        this.customTaxonomyDao = customTaxonomyDao;
    }

    public void updateCatalog(Long productId) {
        ProductRecord productRecord = productDao.findProductDetails(productId.intValue());

        ProductCatalogUpdaterCache productCatalogUpdaterCache = loadUpdaterCache(productRecord);

        updateCatalog(productRecord, productCatalogUpdaterCache);
    }

    private ProductCatalogUpdaterCache loadUpdaterCache(CpanelProductRecord productRecord) {
        Long productId = productRecord.getProductid().longValue();
        ProductCatalogUpdaterCache productCatalogUpdaterCache = new ProductCatalogUpdaterCache();
        loadLanguages(productCatalogUpdaterCache, productId);
        loadProductVariants(productCatalogUpdaterCache, productId);
        loadCommunicationElements(productCatalogUpdaterCache, productId);
        loadProductChannels(productCatalogUpdaterCache, productId);
        loadProductAttributes(productCatalogUpdaterCache, productId);
        loadProductAttributeValues(productCatalogUpdaterCache, productId);
        //Load product events
        loadProductEvents(productCatalogUpdaterCache, productId);
        //Delivery points in product
        loadProductDeliveryPoints(productCatalogUpdaterCache, productId);
        //Delivery points in events
        loadProductEventDeliveryPoints(productCatalogUpdaterCache, productId);
        //Delivery points in sessions
        loadProductSessionDeliveryPoints(productCatalogUpdaterCache, productId);
        //Get product sessions
        productCatalogUpdaterCache.setProductSessionRecords(getRestrictedSessions(productId));
        loadTax(productCatalogUpdaterCache, productRecord);
        loadSurcharges(productCatalogUpdaterCache, productRecord, productId);
        loadContents(productCatalogUpdaterCache, productId);
        loadPromotions(productCatalogUpdaterCache, productId);
        loadTaxes(productCatalogUpdaterCache);
        loadVariantsPrice(productCatalogUpdaterCache);
        loadTaxonomies(productCatalogUpdaterCache, productRecord.getTaxonomyid(), productRecord.getCustomtaxonomyid());

        return productCatalogUpdaterCache;
    }

    private void updateCatalog(ProductRecord productRecord, ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        if (ProductState.get(productRecord.getState()).equals(ProductState.DELETED)) {
            removeProduct(productRecord);
        } else {
            saveProduct(productRecord, productCatalogUpdaterCache);
        }
        if(CollectionUtils.isNotEmpty(productCatalogUpdaterCache.getProductChannelRecords())) {
            updateChannelProducts(productCatalogUpdaterCache.getProductChannelRecords());
        }
    }

    private void updateChannelProducts(List<ProductChannelRecord> productChannels) {
        List<Integer> channelIds = productChannels.stream().map(ProductChannelRecord::getChannelid).collect(Collectors.toList());
        List<CpanelChannelProductRecord> records = productChannelDao.findAcceptedProductSaleRequestByChannelId (channelIds);
        List<ChannelProductDocument> channelProductDocuments = new ArrayList<>();
        for (Integer channelId:channelIds) {
            ChannelProductDocument document = new ChannelProductDocument();
            document.setId(channelId.longValue());
            document.setProductIds(records.stream().filter(record -> channelId.equals(record.getChannelid()))
                    .map(record -> record.getProductid().longValue())
                    .collect(Collectors.toList()));
            channelProductDocuments.add(document);
            channelProductsCouchDao.bulkUpsert(channelProductDocuments);
        }
    }

    private void removeProduct(ProductRecord productRecord) {
        productCatalogCouchDao.remove(productRecord.getProductid().toString());
    }

    private void saveProduct(ProductRecord productRecord, ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        ProductCatalogDocument productCB = buildProduct(productRecord, productCatalogUpdaterCache);
        productCatalogCouchDao.upsert(productRecord.getProductid().toString(), productCB);

        //Stock updated
        if (ProductStockType.SESSION_BOUNDED.equals(ProductStockType.get(productRecord.getStocktype()))
                && stockHasChanged(productRecord.getProductid(), productCatalogUpdaterCache.getProductVariantRecords())) {
            updateStock(productRecord.getProductid(), productCatalogUpdaterCache);
        }
    }

    private void loadLanguages(ProductCatalogUpdaterCache productCatalogUpdaterCache, Long productId) {
        List<ProductLanguageRecord> productLanguageRecords = productLanguagesDao.findByProductId(productId);
        productCatalogUpdaterCache.setProductLanguages(productLanguageRecords);
        ProductLanguageRecord defaultLanguage = productLanguagesDao.findDefaultByProductId(productId);
        if (defaultLanguage != null) {
            productCatalogUpdaterCache.setDefaultLanguage(defaultLanguage);
        }
    }

    private void loadTax(ProductCatalogUpdaterCache productCatalogUpdaterCache, CpanelProductRecord productRecord) {
        if (productRecord.getTaxid() != null) {
            CpanelImpuestoRecord cpanelImpuestoRecord = taxDao.findById(productRecord.getTaxid());
            productCatalogUpdaterCache.setTax(cpanelImpuestoRecord);
        }
        if (productRecord.getSurchagetaxid() != null) {
            CpanelImpuestoRecord surchargeTaxRecord = taxDao.findById(productRecord.getSurchagetaxid());
            productCatalogUpdaterCache.setSurchargeTax(surchargeTaxRecord);
        }
    }

    private void loadSurcharges(ProductCatalogUpdaterCache productCatalogUpdaterCache, CpanelProductRecord productRecord, Long productId) {
        List<CpanelRangoRecord> rangesProduct = Optional.ofNullable(rangeProductSurchargeDao.getByProductId(productId.intValue()))
                .orElse(Collections.emptyList());

        List<CpanelRangoRecord> rangesProductPromotion = Optional.ofNullable(rangeProductSurchargePromotionDao.getByProductId(productRecord.getProductid()))
                .orElse(Collections.emptyList());

        ProductSurcharges productSurchargeList = createProductSurchargeList(rangesProduct, rangesProductPromotion);

        productCatalogUpdaterCache.setSurcharges(productSurchargeList);
    }

    private ProductSurcharges createProductSurchargeList(List<CpanelRangoRecord> rangesProduct, List<CpanelRangoRecord> rangesProductPromotion) {
        ProductSurcharges productSurcharges = new ProductSurcharges();
        SurchargeRanges promoter = new SurchargeRanges();

        promoter.setMain(convertToSurchargeRangeList(rangesProduct));
        promoter.setPromotion(convertToSurchargeRangeList(rangesProductPromotion));

        productSurcharges.setPromoter(promoter);

        return productSurcharges;
    }

    private List<SurchargeRange> convertToSurchargeRangeList(List<CpanelRangoRecord> ranges) {
        return ranges.stream().map(range -> {
            SurchargeRange surchargeRange = new SurchargeRange();
            surchargeRange.setFrom(range.getRangominimo());
            surchargeRange.setTo(NumberUtils.isZero(range.getRangomaximo()) ? Double.MAX_VALUE : range.getRangomaximo());
            surchargeRange.setFixedValue(range.getValor());
            surchargeRange.setMinimumValue(range.getValorminimo());
            surchargeRange.setMaximumValue(range.getValormaximo());
            surchargeRange.setPercentageValue(range.getPorcentaje());
            return surchargeRange;
        }).collect(Collectors.toList());
    }

    private void loadContents(ProductCatalogUpdaterCache productCatalogUpdaterCache, Long productId) {
        for (CpanelProductAttributeRecord productAttributeRecord : productCatalogUpdaterCache.getProductAttributeRecords()) {
            ProductContentDocument productContentDocument = productAttributeContentsCouchDao.get(productId,
                    productAttributeRecord.getAttributeid().longValue());
            if (productContentDocument != null) {
                if (productCatalogUpdaterCache.getAttributeContents() == null) {
                    productCatalogUpdaterCache.setAttributeContents(new ArrayList<>());
                }
                productCatalogUpdaterCache.getAttributeContents().add(productContentDocument);
            }
        }
    }

    private void loadPromotions(ProductCatalogUpdaterCache productCatalogUpdaterCache, Long productId) {
        List<CpanelPromocionProductoRecord> productPromotions = productPromotionDao.getEnabledProductPromotions(productId);
        productCatalogUpdaterCache.setPromotions(productPromotions);
    }

    private void loadProductEvents(ProductCatalogUpdaterCache productCatalogUpdaterCache, Long productId) {
        List<ProductEventRecord> productEventRecords = productEventDao.findByProductId(productId.intValue(), false);
        //Get only Active product events
        productEventRecords = productEventRecords.stream() //TODO: refactor, delegate responsibility to the query
                .filter(productEventRecord -> ProductEventStatus.ACTIVE.getId() == productEventRecord.getStatus())
                .toList();

        productCatalogUpdaterCache.setProductEventRecords(productEventRecords);
    }

    private List<ProductSessionRecord> getRestrictedSessions(Long productId) {
        return productSessionDao.findRestrictedSessionsByProductId(productId.intValue());
    }

    private void loadProductChannels(ProductCatalogUpdaterCache productCatalogUpdaterCache, Long productId) {
        List<ProductChannelRecord> activeChannels = productChannelDao.findByProductId(productId).stream()
                .filter(pc -> Objects.equals(pc.getProductSaleRequestsStatusId(), SaleRequestsStatus.ACCEPTED.getId()))
                .toList();

        productCatalogUpdaterCache.setProductChannelRecords(activeChannels);
    }

    private void loadProductAttributes(ProductCatalogUpdaterCache productCatalogUpdaterCache, Long productId) {
        List<CpanelProductAttributeRecord> productAttributeRecords = productAttributeDao.findByProductId(productId);

        productCatalogUpdaterCache.setProductAttributeRecords(productAttributeRecords);
    }

    private void loadProductAttributeValues(ProductCatalogUpdaterCache productCatalogUpdaterCache, Long productId) {
        List<CpanelProductAttributeValueRecord> productAttributeValueRecords = productAttributeValueDao.findByProductId(productId);
        productCatalogUpdaterCache.setProductAttributeValueRecords(productAttributeValueRecords);
    }

    private void loadProductSessionDeliveryPoints(ProductCatalogUpdaterCache productCatalogUpdaterCache, Long productId) {
        List<ProductSessionDeliveryPointRecord> productSessionDeliveryPointRecords =
                productSessionDeliveryPointDao.findActivesByProductId(productId);

        loadSessionsDeliveryPoints(productCatalogUpdaterCache, productSessionDeliveryPointRecords);
    }

    private void loadProductDeliveryPoints(ProductCatalogUpdaterCache productCatalogUpdaterCache, Long productId) {
        List<ProductDeliveryPointRelationRecord> productDeliveryPointRelations =
                productDeliveryPointRelationDao.findByProductId(productId);

        loadProductDeliveryPoints(productCatalogUpdaterCache, productDeliveryPointRelations);
    }

    private void loadProductEventDeliveryPoints(ProductCatalogUpdaterCache productCatalogUpdaterCache, Long productId) {
        List<ProductEventDeliveryPointRecord> productEventsDeliveryPointRecords =
                productEventDeliveryPointDao.findActivesByProductId(productId);

        loadEventsDeliveryPoints(productCatalogUpdaterCache, productEventsDeliveryPointRecords);
    }

    private void loadEventsDeliveryPoints(ProductCatalogUpdaterCache productCatalogUpdaterCache,
                                          List<ProductEventDeliveryPointRecord> productEventDeliveryPointRecords) {
        Map<Long, List<DeliveryPointRecord>> eventDeliveryPoints = new HashMap<>();

        for (ProductEventDeliveryPointRecord productEventDeliveryPointRecord : productEventDeliveryPointRecords) {
            long eventId = productEventDeliveryPointRecord.getEventId().longValue();
            long deliveryPointId = productEventDeliveryPointRecord.getDeliverypointid().longValue();

            eventDeliveryPoints.computeIfAbsent(eventId, key -> new ArrayList<>()).addAll(
                    deliveryPointDao.getProductDeliveryPoints(null, deliveryPointId));
        }

        productCatalogUpdaterCache.setEventDeliveryPoints(eventDeliveryPoints);
    }

    private void loadSessionsDeliveryPoints(ProductCatalogUpdaterCache productCatalogUpdaterCache,
                                            List<ProductSessionDeliveryPointRecord> productSessionDeliveryPointRecords) {
        Map<Long, List<DeliveryPointRecord>> sessionDeliveryPoints = new HashMap<>();

        Map<Long, List<DeliveryPointRecord>> deliveryPointsCache = new HashMap<>();
        for (ProductSessionDeliveryPointRecord productSessionDeliveryPointRecord : productSessionDeliveryPointRecords) {
            long sessionId = productSessionDeliveryPointRecord.getSessionid().longValue();
            long deliveryPointId = productSessionDeliveryPointRecord.getDeliverypointid().longValue();

            deliveryPointsCache.computeIfAbsent(deliveryPointId, key ->
                    deliveryPointDao.getProductDeliveryPoints(null, key));
            sessionDeliveryPoints.computeIfAbsent(sessionId, key -> new ArrayList<>()).addAll(deliveryPointsCache.get(deliveryPointId));
        }

        productCatalogUpdaterCache.setSessionDeliveryPoints(sessionDeliveryPoints);
    }

    private void loadProductDeliveryPoints(ProductCatalogUpdaterCache productCatalogUpdaterCache,
                                           List<ProductDeliveryPointRelationRecord> productDeliveryPointRelationRecords) {

        List<Integer> deliveryPointIds = productDeliveryPointRelationRecords.stream()
                .map(ProductDeliveryPointRelationRecord::getDeliverypointid)
                .toList();

        List<DeliveryPointRecord> deliveryPointRecords = deliveryPointDao.getProductDeliveryPointsByIds(deliveryPointIds);

        productCatalogUpdaterCache.setProductDeliveryPoints(deliveryPointRecords);
    }

    private void loadCommunicationElements(ProductCatalogUpdaterCache productCatalogUpdaterCache, Long productId) {
        ProductCommunicationElementDocument productCommunicationElements = productCommunicationElementCouchDao.get(productId.toString());
        productCatalogUpdaterCache.setCommunicationElements(productCommunicationElements);
    }


    private void loadProductVariants(ProductCatalogUpdaterCache productCatalogUpdaterCache, Long productId) {
        SearchProductVariantsFilterDTO filter = new SearchProductVariantsFilterDTO();
        filter.setLimit(1000L);
        List<ProductVariantRecord> activeProductVariantRecords = productVariantDao.searchProductVariants(productId, filter).stream()
                .filter(pv -> pv.getStatus().equals(ProductVariantStatus.ACTIVE.getId()))
                .toList();
        productCatalogUpdaterCache.getProductVariantRecords().addAll(activeProductVariantRecords);

        List<CpanelProductVariantSessionRecord> variantSessions = productVariantSessionDao.getProductVariantSessions(productId);
        productCatalogUpdaterCache.setProductVariantSessionRecords(variantSessions.stream()
                .collect(Collectors.groupingBy(CpanelProductVariantSessionRecord::getVariantid)));
    }

    private void loadVariantsPrice(ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        for (CpanelProductVariantRecord productVariantRecord : productCatalogUpdaterCache.getProductVariantRecords()) {
            Double variantPrice = NumberUtils.zeroIfNull(productVariantRecord.getPrice());
            productCatalogUpdaterCache.getVariantPrices().put(productVariantRecord.getVariantid(),
                    calculateProductVariantPrice(productCatalogUpdaterCache, variantPrice));

            List<CpanelProductVariantSessionRecord> variantSession = productCatalogUpdaterCache
                    .getProductVariantSessionRecords().get(productVariantRecord.getVariantid());
            if (CollectionUtils.isNotEmpty(variantSession)) {
                Map<Long, ProductCatalogVariantPrice> sessionPrices = new HashMap<>();
                for (CpanelProductVariantSessionRecord variantSessionRecord : variantSession) {
                    if (CommonUtils.isTrue(variantSessionRecord.getUsecustomprice())) {
                        Double sessionVariantPrice = NumberUtils.zeroIfNull(variantSessionRecord.getPrice());
                        sessionPrices.put(variantSessionRecord.getSessionid().longValue(),
                                calculateProductVariantPrice(productCatalogUpdaterCache, sessionVariantPrice));
                    }
                }
                productCatalogUpdaterCache.getVariantSessionPrices().put(productVariantRecord.getVariantid(), sessionPrices);
            }
        }
    }

    private void loadTaxes(ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        CpanelImpuestoRecord recordTax = productCatalogUpdaterCache.getTax();
        CpanelImpuestoRecord recordSurchargeTax = productCatalogUpdaterCache.getSurchargeTax();

        if (recordTax != null) {
            productCatalogUpdaterCache.setTaxes(List.of(
                    TaxSimulationUtils.createTaxInfo(
                            recordTax.getIdimpuesto().longValue(),
                            recordTax.getValor(),
                            recordTax.getNombre(),
                            ProductTaxInfo::new
                    )
            ));
        } else {
            productCatalogUpdaterCache.setTaxes(List.of(
                    TaxSimulationUtils.createTaxInfo(
                            null,
                            DOUBLE_ZERO,
                            null,
                            ProductTaxInfo::new
                    )
            ));
        }

        if (recordSurchargeTax != null) {
            productCatalogUpdaterCache.setSurchargesTaxes(List.of(
                    TaxSimulationUtils.createTaxInfo(
                            recordSurchargeTax.getIdimpuesto().longValue(),
                            recordSurchargeTax.getValor(),
                            recordSurchargeTax.getNombre(),
                            ProductTaxInfo::new
                    )
            ));
        } else {
            productCatalogUpdaterCache.setSurchargesTaxes(List.of(
                    TaxSimulationUtils.createTaxInfo(
                            null,
                            DOUBLE_ZERO,
                            null,
                            ProductTaxInfo::new
                    )
            ));
        }
    }


    private void loadTaxonomies(ProductCatalogUpdaterCache productCatalogUpdaterCache, Integer taxonomyid, Integer customtaxonomyid) {
        if (taxonomyid != null) {
            BaseTaxonomyDao.TaxonomyInfo baseTaxonomy = staticDataContainer.getBaseTaxonomy(taxonomyid);
            if (baseTaxonomy != null) {
                productCatalogUpdaterCache.setTaxonomy(baseTaxonomy);
                if (baseTaxonomy.parentId() != null) {
                    productCatalogUpdaterCache.setParentTaxonomy(staticDataContainer.getBaseTaxonomy(baseTaxonomy.parentId()));
                }
            }
        }
        if (customtaxonomyid != null) {
            BaseTaxonomyDao.TaxonomyInfo customTaxonomy = localCacheRepository.cached(LocalCache.TAXONOMY_KEY, LocalCache.TAXONOMY_TTL, TimeUnit.SECONDS,
                    () -> customTaxonomyDao.getTaxonomyInfo(customtaxonomyid), new Object[]{customtaxonomyid});
            if (customTaxonomy != null) {
                productCatalogUpdaterCache.setCustomTaxonomy(customTaxonomy);
                if (customTaxonomy.parentId() != null) {
                    productCatalogUpdaterCache.setCustomParentTaxonomy(localCacheRepository.cached(LocalCache.TAXONOMY_KEY, LocalCache.TAXONOMY_TTL, TimeUnit.SECONDS,
                            () -> customTaxonomyDao.getTaxonomyInfo(customTaxonomy.parentId() ), new Object[]{customTaxonomy.parentId()}));
                }
            }
        }
    }

    private ProductCatalogVariantPrice calculateProductVariantPrice(ProductCatalogUpdaterCache productCatalogUpdaterCache, Double price) {
        return ProductCatalogPriceConverter.buildProductCatalogVariantPrice(
                price,
                productCatalogUpdaterCache.getSurcharges().getPromoter(),
                productCatalogUpdaterCache.getTaxes(),
                productCatalogUpdaterCache.getSurchargesTaxes()
        );
    }


    protected ProductCatalogDocument buildProduct(ProductRecord productRecord, ProductCatalogUpdaterCache productCatalogUpdaterCache) {

        ProductCatalogDocument productCatalogDocument = new ProductCatalogDocument();

        //Product details
        setProductDetails(productRecord, productCatalogDocument, productCatalogUpdaterCache);

        // attributes
        if (ProductType.VARIANT.getId() == productRecord.getType()) {
            List<CpanelProductVariantRecord> productVariants = productCatalogUpdaterCache.getProductVariantRecords();

            //Fill attribute1
            productVariants.stream()
                    .map(CpanelProductVariantRecord::getVariantoption1)
                    .filter(Objects::nonNull)
                    .findAny()
                    .ifPresent(attributeId ->
                            productCatalogDocument.setAttribute1(ProductCatalogConverter.attribute(attributeId, productCatalogUpdaterCache)));

            //Fill attribute2
            productVariants.stream()
                    .map(CpanelProductVariantRecord::getVariantoption2)
                    .filter(Objects::nonNull)
                    .findAny()
                    .ifPresent(attributeId ->
                            productCatalogDocument.setAttribute2(ProductCatalogConverter.attribute(attributeId, productCatalogUpdaterCache)));
        }

        //communicationElements
        productCatalogDocument.setCommElements(ProductCatalogConverter.commElement(productCatalogUpdaterCache));
        //channels
        productCatalogDocument.setChannels(ProductCatalogConverter.productChannels(productCatalogUpdaterCache));

        //events, sessions and delivery points
        List<EventSessionsDeliveryPoints> eventSessionsAndDeliveryPoints =
                ProductCatalogConverter.getProductEventSessionsAndDeliveryPoints(productCatalogUpdaterCache);
        productCatalogDocument.setEvents(eventSessionsAndDeliveryPoints);

        //variants
        productCatalogDocument.setVariants(ProductCatalogConverter.variants(productCatalogUpdaterCache));

        //Delivery points
        Set<DeliveryPoint> deliveryPoints =
                ProductCatalogConverter.getDeliveryPoints(productCatalogUpdaterCache, productRecord.getDeliverytype());
        productCatalogDocument.setDeliveryPoints(deliveryPoints);

        //languages
        if (productCatalogUpdaterCache.getDefaultLanguage() != null) {
            productCatalogDocument.setDefaultLanguage(productCatalogUpdaterCache.getDefaultLanguage().getCode());
        }
        if (CollectionUtils.isNotEmpty(productCatalogUpdaterCache.getProductLanguages())) {
            productCatalogDocument.setLanguages(productCatalogUpdaterCache.getProductLanguages().stream().map(ProductLanguageRecord::getCode).collect(Collectors.toList()));
        }

        //promotions
        productCatalogDocument.setPromotions(ProductCatalogConverter.productPromotions(productCatalogUpdaterCache));

        if (productRecord.getTickettemplateid() != null) {
            productCatalogDocument.setTicketTemplateId(productRecord.getTickettemplateid().longValue());
        }

        //taxonomies
        fillTaxonomies(productCatalogDocument, productCatalogUpdaterCache);

        productCatalogDocument.setUseExternalBarcodes(ConverterUtils.isByteAsATrue(productRecord.getUseexternalbarcode()));
        productCatalogDocument.setHideDeliveryPoint(ConverterUtils.isByteAsATrue(productRecord.getHidedeliverypoint()));
        productCatalogDocument.setHideDeliveryDateTime(ConverterUtils.isByteAsATrue(productRecord.getHidedeliverydatetime()));


        return productCatalogDocument;
    }

    private void fillTaxonomies(ProductCatalogDocument productCatalogDocument, ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        if (productCatalogUpdaterCache.getTaxonomy() != null) {
            productCatalogDocument.setTaxonomyId(productCatalogUpdaterCache.getTaxonomy().id());
            productCatalogDocument.setTaxonomyCode(productCatalogUpdaterCache.getTaxonomy().code());
            productCatalogDocument.setTaxonomyDescription(productCatalogUpdaterCache.getTaxonomy().desc());
        }

        if (productCatalogUpdaterCache.getParentTaxonomy() != null) {
            productCatalogDocument.setTaxonomyParentId(productCatalogUpdaterCache.getParentTaxonomy().id());
            productCatalogDocument.setTaxonomyParentCode(productCatalogUpdaterCache.getParentTaxonomy().code());
            productCatalogDocument.setTaxonomyParentDescription(productCatalogUpdaterCache.getParentTaxonomy().desc());
        }

        if (productCatalogUpdaterCache.getCustomTaxonomy() != null) {
            productCatalogDocument.setCustomTaxonomyId(productCatalogUpdaterCache.getCustomTaxonomy().id());
            productCatalogDocument.setCustomTaxonomyCode(productCatalogUpdaterCache.getCustomTaxonomy().code());
            productCatalogDocument.setCustomTaxonomyDescription(productCatalogUpdaterCache.getCustomTaxonomy().desc());
        }

        if (productCatalogUpdaterCache.getCustomParentTaxonomy() != null) {
            productCatalogDocument.setCustomParentTaxonomyId(productCatalogUpdaterCache.getCustomParentTaxonomy().id());
            productCatalogDocument.setCustomParentTaxonomyCode(productCatalogUpdaterCache.getCustomParentTaxonomy().code());
            productCatalogDocument.setCustomParentTaxonomyDescription(productCatalogUpdaterCache.getCustomParentTaxonomy().desc());
        }
    }

    private void updateStock(Integer productId, ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        if (CollectionUtils.isNotEmpty(productCatalogUpdaterCache.getProductVariantRecords())) {
            //we are in a context of simple product
            CpanelProductVariantRecord variantProductSimple = productCatalogUpdaterCache.getProductVariantRecords().get(0);
            if (variantProductSimple != null && variantProductSimple.getStock() != null) {

                List<ProductEventRecord> productEvents = productEventDao.findByProductId(productId, true);
                if (CollectionUtils.isNotEmpty(productEvents)) {
                    updateStockSelectedSessions(variantProductSimple, productEvents);
                    updateStockEventAllSessions(variantProductSimple, productEvents);
                }
                //fetch and update
            }
        }
    }

    private void updateStockEventAllSessions(CpanelProductVariantRecord productVariantSimple, List<ProductEventRecord> productEvents) {
        if (CollectionUtils.isNotEmpty(productEvents)) {
            List<ProductEventRecord> productEventsAllSessions = getProductEventsBySessionSelectionType(productEvents, SelectionType.ALL);
            if (CollectionUtils.isNotEmpty(productEventsAllSessions)) {
                List<Long> eventIds = productEventsAllSessions.stream().map(ProductEventRecord::getEventid).map(Integer::longValue).toList();
                SessionSearchFilter sessionFilter = new SessionSearchFilter();
                sessionFilter.setEventId(eventIds);
                sessionFilter.setEventStatus(List.of(EventStatus.IN_PROGRAMMING, EventStatus.IN_PROGRESS, EventStatus.PLANNED, EventStatus.READY));
                sessionFilter.setStatus(List.of(SessionStatus.READY, SessionStatus.SCHEDULED, SessionStatus.IN_PROGRESS, SessionStatus.PREVIEW, SessionStatus.PLANNED));
                Long total = sessionDao.countByFilter(sessionFilter);
                if (total > 0) {
                    Long productId = productVariantSimple.getProductid().longValue();
                    Long variantId = productVariantSimple.getVariantid().longValue();
                    Long stockToUpdate = productVariantSimple.getStock().longValue();
                    long offset = 0L;
                    long limit = 100L;
                    while (offset <= total) {
                        sessionFilter.setOffset(offset);
                        sessionFilter.setLimit(limit);
                        List<SessionRecord> sessions = sessionDao.findSessions(sessionFilter, null);
                        if (CollectionUtils.isNotEmpty(sessions)) {
                            sessions.forEach(s -> productVariantSessionStockCouchDao.resetIfExists(productId, variantId, s.getIdsesion().longValue(), stockToUpdate));
                        }
                        offset += limit;
                    }
                }
            }
        }
    }

    private void updateStockSelectedSessions(CpanelProductVariantRecord productVariantSimple, List<ProductEventRecord> productEvents) {
        if (CollectionUtils.isEmpty(productEvents)) {
            return;
        }
        List<ProductEventRecord> productsSessionSelection = getProductEventsBySessionSelectionType(productEvents, SelectionType.RESTRICTED);
        if (CollectionUtils.isEmpty(productsSessionSelection)) {
            return;
        }
        Long productId = productVariantSimple.getProductid().longValue();
        Long variantId = productVariantSimple.getVariantid().longValue();
        Long stockToUpdate = productVariantSimple.getStock().longValue();
        for (ProductEventRecord productEventRecord : productsSessionSelection) {
            List<ProductSessionRecord> productSessions = productSessionDao.findProductSessionsByProductId(productId.intValue(), productEventRecord.getEventid());
            if (CollectionUtils.isNotEmpty(productSessions)) {
                productSessions.stream()
                        .filter(session -> BooleanUtils.isFalse(BooleanUtils.toBoolean(session.getUsecustomstock())))
                        .forEach(ps -> productVariantSessionStockCouchDao.resetIfExists(productId, variantId, ps.getSessionid().longValue(), stockToUpdate));
            }
        }
    }

    private List<ProductEventRecord> getProductEventsBySessionSelectionType(List<ProductEventRecord> productEventRecords, SelectionType selectionType) {
        if (CollectionUtils.isNotEmpty(productEventRecords)) {
            return productEventRecords.stream()
                    .filter(pe -> selectionType.equals(SelectionType.get(pe.getSessionsselectiontype())))
                    .toList();
        }
        return null;
    }

    private boolean stockHasChanged(Integer productId, List<CpanelProductVariantRecord> productVariantRecords) {
        ProductCatalogDocument catalogProduct = productCatalogCouchDao.get(productId.toString());
        if (catalogProduct != null && CollectionUtils.isNotEmpty(productVariantRecords)) {
            //we are in a context of simple product
            CpanelProductVariantRecord variantProductSimple = productVariantRecords.get(0);
            ProductCatalogVariant catalogVariant = catalogProduct.getVariants().get(0);
            if (catalogVariant != null && catalogVariant.getInitialStock() != null
                    && variantProductSimple != null && variantProductSimple.getStock() != null) {
                return !variantProductSimple.getStock().equals(catalogVariant.getInitialStock());
            }
        }
        return Boolean.FALSE;
    }

    private void setProductDetails(ProductRecord productRecord,
                                   ProductCatalogDocument productCatalogDocument,
                                   ProductCatalogUpdaterCache productCatalogUpdaterCache) {


        //Product details
        productCatalogDocument.setId(productRecord.getProductid().longValue());
        productCatalogDocument.setName(productRecord.getName());
        productCatalogDocument.setCurrencyId(productRecord.getIdcurrency());
        productCatalogDocument.setStockType(ProductStockType.get(productRecord.getStocktype()));
        productCatalogDocument.setType(ProductType.get(productRecord.getType()));
        productCatalogDocument.setEntity(new IdNameDTO(productRecord.getEntityid().longValue(), productRecord.getEntityName()));
        productCatalogDocument.setProducer(new IdNameDTO(productRecord.getProducerid().longValue(), productRecord.getProducerName()));
        productCatalogDocument.setDeliveryType(productRecord.getDeliverytype() != null ? ProductDeliveryType.get(productRecord.getDeliverytype()) : null);
        productCatalogDocument.setStartTimeUnit(productRecord.getDeliverystarttimeunit() != null ? ProductDeliveryTimeUnitType.get(productRecord.getDeliverystarttimeunit()) : null);
        productCatalogDocument.setStartTimeValue(productRecord.getDeliverystarttimevalue() != null ? productRecord.getDeliverystarttimevalue().longValue() : null);
        productCatalogDocument.setEndTimeUnit(productRecord.getDeliveryendtimeunit() != null ? ProductDeliveryTimeUnitType.get(productRecord.getDeliveryendtimeunit()) : null);
        productCatalogDocument.setEndTimeValue(productRecord.getDeliveryendtimevalue() != null ? productRecord.getDeliveryendtimevalue().longValue() : null);
        productCatalogDocument.setDeliveryDateFrom(productRecord.getDeliverydatefrom() != null ? CommonUtils.timestampToZonedDateTime(productRecord.getDeliverydatefrom()) : null);
        productCatalogDocument.setDeliveryDateTo(productRecord.getDeliverydateto() != null ? CommonUtils.timestampToZonedDateTime(productRecord.getDeliverydateto()) : null);
        productCatalogDocument.setPrice(ProductCatalogPriceConverter.buildProductCatalogPrice(productCatalogUpdaterCache));

        //TODO: prepared to multi tax scenario
        CpanelImpuestoRecord tax = productCatalogUpdaterCache.getTax();
        if (tax != null) {
            productCatalogDocument.setTax(tax.getValor());
            productCatalogDocument.setTaxes(productCatalogUpdaterCache.getTaxes());
        }
        CpanelImpuestoRecord surchargeTax = productCatalogUpdaterCache.getSurchargeTax();
        if (surchargeTax != null) {
            productCatalogDocument.setSurchargeTax(surchargeTax.getValor());
            productCatalogDocument.setSurchargesTaxes(productCatalogUpdaterCache.getSurchargesTaxes());
        }

        productCatalogDocument.setSurcharges(productCatalogUpdaterCache.getSurcharges());
        productCatalogDocument.setState(ProductState.get(productRecord.getState()));
    }
}
