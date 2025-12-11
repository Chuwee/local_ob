package es.onebox.event.catalog.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.couchbase.core.Key;
import es.onebox.event.catalog.converter.CatalogProductConverter;
import es.onebox.event.catalog.dao.CatalogEventCouchDao;
import es.onebox.event.catalog.dao.CatalogSeasonTicketCouchDao;
import es.onebox.event.catalog.dao.CatalogSessionCouchDao;
import es.onebox.event.catalog.dto.filter.ProductCatalogFilter;
import es.onebox.event.catalog.dto.product.ProductCatalogDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogsDTO;
import es.onebox.event.catalog.dto.product.ProductStockSearchDTO;
import es.onebox.event.catalog.dto.product.ProductVariantStock;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.seasonticket.SeasonTicket;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.indexer.ChannelSessionDataIndexer;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.catalog.elasticsearch.service.VenueDescriptorService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductCatalogCouchDao;
import es.onebox.event.products.dao.ProductVariantSessionStockCouchDao;
import es.onebox.event.products.dao.ProductVariantStockCouchDao;
import es.onebox.event.products.dao.couch.ProductCatalogDocument;
import es.onebox.event.products.dao.couch.ProductCatalogVariant;
import es.onebox.event.products.dao.couch.ProductStockStatus;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.sessions.dto.SessionsDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class CatalogService {

    private final CatalogEventCouchDao catalogEventCouchDao;
    private final CatalogSessionCouchDao catalogSessionCouchDao;
    private final VenueDescriptorService venueDescriptorService;
    private final ProductVariantStockCouchDao productVariantStockCouchDao;
    private final ProductCatalogCouchDao productCatalogCouchDao;
    private final ChannelSessionDataIndexer channelSessionDataIndexer;
    private final StaticDataContainer staticDataContainer;
    private final ProductVariantSessionStockCouchDao productVariantSessionStockCouchDao;
    private final CatalogSeasonTicketCouchDao catalogSeasonTicketCouchDao;

    @Autowired
    public CatalogService(CatalogEventCouchDao catalogEventCouchDao, CatalogSessionCouchDao catalogSessionCouchDao,
                          VenueDescriptorService venueDescriptorService, ProductVariantStockCouchDao productVariantStockCouchDao,
                          ProductCatalogCouchDao productCatalogCouchDao,
                          ChannelSessionDataIndexer channelSessionDataIndexer, StaticDataContainer staticDataContainer,
                          ProductVariantSessionStockCouchDao productVariantSessionStockCouchDao, CatalogSeasonTicketCouchDao catalogSeasonTicketCouchDao) {
        this.catalogEventCouchDao = catalogEventCouchDao;
        this.catalogSessionCouchDao = catalogSessionCouchDao;
        this.venueDescriptorService = venueDescriptorService;
        this.productVariantStockCouchDao = productVariantStockCouchDao;
        this.productCatalogCouchDao = productCatalogCouchDao;
        this.channelSessionDataIndexer = channelSessionDataIndexer;
        this.staticDataContainer = staticDataContainer;
        this.productVariantSessionStockCouchDao = productVariantSessionStockCouchDao;
        this.catalogSeasonTicketCouchDao = catalogSeasonTicketCouchDao;
    }

    public Event getEvent(Integer id) {
        return this.catalogEventCouchDao.get(String.valueOf(id));
    }


    public Session getSession(Integer id) {
        var session = this.catalogSessionCouchDao.get(String.valueOf(id));
        if (session == null) {
            return null;
        }
        var event = this.catalogEventCouchDao.get(String.valueOf(session.getEventId()));
        if (event == null) {
            return null;
        }
        session.setEventDefaultLanguage(event.getEventDefaultLanguage());
        session.setEventLanguages(event.getEventLanguages());

        return session;
    }

	public List<Session> getSessions(Set<Integer> ids) {
		var sessions = catalogSessionCouchDao.bulkGet(ids.stream().map(Long::valueOf).toList());
		if (!CollectionUtils.isEmpty(sessions)) {
			List<Long> sessionIdsFound = sessions.stream().map(Session::getEventId).distinct().toList();
			var possibleEvents = Optional.ofNullable(catalogEventCouchDao.bulkGet(sessionIdsFound))
					.map(e -> e.stream().collect(toMap(Event::getEventId, Function.identity())));
			possibleEvents.ifPresent(events -> sessions.forEach(session -> {
                session.setEventDefaultLanguage(
                        events.getOrDefault(session.getEventId(), new Event()).getEventDefaultLanguage());
                session.setEventLanguages(
                        events.getOrDefault(session.getEventId(), new Event()).getEventLanguages());
            }));
		}

        return sessions;
	}

    public SeasonTicket getSeasonTicket(Integer id) {
        return catalogSeasonTicketCouchDao.get(String.valueOf(id));
    }

    public VenueDescriptor getSessionVenueDescriptor(Long sessionId) {
        var session = this.catalogSessionCouchDao.get(String.valueOf(sessionId));
        return this.venueDescriptorService.getVenueDescriptor(session.getVenueConfigId());
    }

    public VenueDescriptor getVenueDescriptor(Long venueTemplateId) {
        return this.venueDescriptorService.getVenueDescriptor(venueTemplateId);
    }

    public void updateProductVariantStock(Long productId, Long variantId, ProductVariantStock productVariantStock) {
        updateProductStock(productId, variantId, productVariantStock, null);
    }

    public void updateProductStock(Long productId, Long variantId, ProductVariantStock productVariantStock, Long sessionId) {
        if ((productVariantStock.getDecrementStock() == null || productVariantStock.getDecrementStock().equals(0L)) && (productVariantStock.getIncrementStock() == null || productVariantStock.getIncrementStock().equals(0L))) {
            throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER);
        }
        if (productVariantStock.getIncrementStock() != null && productVariantStock.getIncrementStock() > 0L && productVariantStock.getDecrementStock() != null && productVariantStock.getDecrementStock() > 0L) {
            throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER);
        }
        ProductCatalogDocument productCatalog = productCatalogCouchDao.get(productId.toString());
        if (productCatalog == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        } else if (productCatalog.getStockType().equals(ProductStockType.UNBOUNDED)) {
            return;
        }

        if (productVariantStock.getIncrementStock() != null && productVariantStock.getIncrementStock() > 0L) {
            if (ProductStockType.BOUNDED.equals(productCatalog.getStockType())) {
                productVariantStockCouchDao.increment(productId, variantId, productVariantStock.getIncrementStock());
            } else if (ProductStockType.SESSION_BOUNDED.equals(productCatalog.getStockType())) { //For simple product for the moment
                safeCreationAndInitProductVariantSessionCounterIfNotExists(productId, variantId, sessionId, productCatalog);
                productVariantSessionStockCouchDao.increment(productId, variantId, sessionId, productVariantStock.getIncrementStock());

                Long sessionSB = getRelatedSBSessionId(sessionId);
                if (sessionSB != null) {
                    safeCreationAndInitProductVariantSessionCounterIfNotExists(productId, variantId, sessionSB, productCatalog);
                    productVariantSessionStockCouchDao.increment(productId, variantId, sessionSB, productVariantStock.getIncrementStock());
                }

            }
        }
        if (productVariantStock.getDecrementStock() != null && productVariantStock.getDecrementStock() > 0L) {
            try {
                if (ProductStockType.SESSION_BOUNDED.equals(productCatalog.getStockType())) {
                    changeProductVariantSessionStock(productId, variantId, sessionId, productVariantStock, productCatalog);

                    Long sessionSB = getRelatedSBSessionId(sessionId);
                    if (sessionSB != null) {
                        changeProductVariantSessionStock(productId, variantId, sessionSB, productVariantStock, productCatalog);
                    }
                } else {
                    changeProductStock(productId, variantId, productVariantStock);
                }
            } catch (OneboxRestException e) {
                throw e;
            } catch (Exception e) {
                throw new OneboxRestException(MsEventErrorCode.GENERIC_ERROR, e);
            }
        }
    }

    private Long getRelatedSBSessionId(Long sessionId) {
        if (sessionId != null) {
            Session session = this.catalogSessionCouchDao.get(String.valueOf(sessionId));
            return session != null && session.getRelatedSessionId() != null ? session.getRelatedSessionId() : null;
        }
        return null;
    }

    private void safeCreationAndInitProductVariantSessionCounterIfNotExists(Long productId, Long variantId, Long sessionId, ProductCatalogDocument productCatalog) {
        if (!productVariantSessionStockCouchDao.exists(productId, variantId, sessionId)) {
            createAndInitProductVariantSessionCounterIfNotExists(productId, variantId, sessionId, productCatalog);
        }
    }

    private synchronized void createAndInitProductVariantSessionCounterIfNotExists(Long productId, Long variantId, Long sessionId, ProductCatalogDocument productCatalog) {
        if (!productVariantSessionStockCouchDao.exists(productId, variantId, sessionId)) {
            Long initialStock = productCatalog.getVariants().get(0).getInitialStock().longValue();
            productVariantSessionStockCouchDao.createAndInit(productId, variantId, sessionId, initialStock);
        }
    }

    private Long changeProductStock(Long productId, Long variantId, ProductVariantStock productVariantStock) {
        Long currentStock = productVariantStockCouchDao.get(productId, variantId);
        if (currentStock < productVariantStock.getDecrementStock()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_NOT_ENOUGH_STOCK);
        }
        return productVariantStockCouchDao.decrement(productId, variantId, productVariantStock.getDecrementStock());
    }

    private Long changeProductVariantSessionStock(Long productId, Long variantId, Long sessionId,
                                                  ProductVariantStock productVariantStock, ProductCatalogDocument productCatalog) {

        safeCreationAndInitProductVariantSessionCounterIfNotExists(productId, variantId, sessionId, productCatalog);
        Long currentStock = productVariantSessionStockCouchDao.get(productId, variantId, sessionId);
        if (currentStock < productVariantStock.getDecrementStock()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_NOT_ENOUGH_STOCK);
        }
        return productVariantSessionStockCouchDao.decrement(productId, variantId, sessionId, productVariantStock.getDecrementStock());
    }

    public ProductCatalogsDTO findSessionProducts(Long channelId, ProductCatalogFilter productCatalogFilter) {
        List<ChannelSession> channelSessions = channelSessionDataIndexer.findSessionChannelProducts(productCatalogFilter.getSessionIds(), channelId);

        if (channelSessions == null || channelSessions.isEmpty()) {
            return new ProductCatalogsDTO();
        }
        Set<Long> productIds = new HashSet<>();
        channelSessions.forEach(cs -> {
                    if (cs.getProductIds() != null) {
                        productIds.addAll(cs.getProductIds());
                    }
                }
        );

        List<Key> keys = productIds.stream()
                .distinct()
                .map(sId -> new Key(new String[]{String.valueOf(sId)}))
                .toList();

        List<ProductCatalogDocument> documents = productCatalogCouchDao.bulkGet(keys);

        Predicate<ProductCatalogDocument> filterByCurrencyIfPresent = p -> productCatalogFilter.getCurrencyId() == null ||
                productCatalogFilter.getCurrencyId().equals(p.getCurrencyId());

        List<ProductCatalogDocument> availableProducts = documents.stream()
                .filter(p -> ProductState.ACTIVE.equals(p.getState()))
                .filter(filterByCurrencyIfPresent)
                .filter(p -> filterStandaloneEnabled(p, channelId, productCatalogFilter.getStandaloneEnabled()))
                .filter(p -> filterCheckoutSuggestionEnabled(p, channelId, productCatalogFilter.getCheckoutSuggestionEnabled()))
                .toList();

        Map<Long, Map<Long, ProductStockStatus>> productsAvailability = getProductsAvailability(availableProducts, productCatalogFilter.getSessionIds());

        return CatalogProductConverter.convertList(availableProducts, channelSessions, staticDataContainer.getS3Repository(),
                productsAvailability);
    }

    public Map<Long, Map<Long, ProductStockStatus>> getProductsAvailability(List<ProductCatalogDocument> documents) {
        return getProductsAvailability(documents, null);
    }

    //TODO pipe, this method is not using productVariantSessionStock if session use custom stock
    public Map<Long, Map<Long, ProductStockStatus>> getProductsAvailability(List<ProductCatalogDocument> documents, List<Long> sessionIds) {
        //Get stocks per product checking their variants
        Map<Long, Map<Long, ProductStockStatus>> productsAvailability = new HashMap<>();

        for (ProductCatalogDocument document : documents) {
            Long productId = document.getId();

            if (document.getStockType().equals(ProductStockType.UNBOUNDED)) {
                for (ProductCatalogVariant productCatalogVariant : document.getVariants()) {
                    if (!productsAvailability.containsKey(productId)) {
                        productsAvailability.put(productId, new HashMap<>());
                    }
                    productsAvailability.get(productId).put(productCatalogVariant.getId(), ProductStockStatus.ON_SALE);
                }

                continue;
            } else if (document.getStockType().equals(ProductStockType.SESSION_BOUNDED) && CollectionUtils.isNotEmpty(sessionIds)) { //Quickly fix, only for 1 session
                for (ProductCatalogVariant productCatalogVariant : document.getVariants()) {
                    if (!productsAvailability.containsKey(productId)) {
                        productsAvailability.put(productId, new HashMap<>());
                    }
                    Long stock = getProductVariantSessionStock(document, productCatalogVariant.getId(), sessionIds.get(0));
                    ProductStockStatus status = stock != null && stock > 0 ? ProductStockStatus.ON_SALE : ProductStockStatus.SOLD_OUT;
                    productsAvailability.get(productId).put(productCatalogVariant.getId(), status);
                }
                continue;
            }

            List<Long> variantIds = document.getVariants().stream()
                    .map(ProductCatalogVariant::getId)
                    .toList();

            for (Long variantId : variantIds) {
                if (!productsAvailability.containsKey(productId)) {
                    productsAvailability.put(productId, new HashMap<>());
                }
                Long productVariantStock = productVariantStockCouchDao.get(productId, variantId);
                productsAvailability.get(productId).put(variantId, productVariantStock != null && productVariantStock > 0 ? ProductStockStatus.ON_SALE : ProductStockStatus.SOLD_OUT);
            }
        }

        return productsAvailability;
    }

    public ProductCatalogDTO findCatalogProduct(Long productId) {
        ProductCatalogDocument document = productCatalogCouchDao.get(productId.toString());
        if (document == null) {
            return null;
        }

        Map<Long, Map<Long, ProductStockStatus>> productsAvailability = getProductsAvailability(List.of(document));

        return CatalogProductConverter.convert(document, new ArrayList<>(), staticDataContainer.getS3Repository(),
                productsAvailability);
    }

    public void updateProductVariantSessionStock(Long productId, Long variantId, ProductVariantStock productVariantStock, Long sessionId) {
        updateProductStock(productId, variantId, productVariantStock, sessionId);
    }


    public Long getProductVariantStock(Long productId, Long variantId) {
        return getProductVariantSessionStock(productId, variantId, null);
    }

    public Long getProductVariantSessionStock(Long productId, Long variantId, Long sessionId) {
        return getCurrentStock(productId, variantId, sessionId);
    }

    private Long getCurrentStock(Long productId, Long variantId, Long sessionId) {
        ProductCatalogDocument productCatalog = productCatalogCouchDao.get(productId.toString());
        if (productCatalog == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        return getProductVariantSessionStock(productCatalog, variantId, sessionId);
    }

    private Long getProductVariantSessionStock(ProductCatalogDocument productCatalog, Long variantId, Long sessionId) {
        Long productId = productCatalog.getId();
        Long currentStock;
        if (ProductStockType.SESSION_BOUNDED.equals(productCatalog.getStockType())) {
            Long stock = productVariantSessionStockCouchDao.get(productId, variantId, sessionId);
            currentStock = stock != null ? stock : getStockFromVariant(productCatalog.getVariants(), variantId);
        } else {
            currentStock = productVariantStockCouchDao.get(productId, variantId);
        }
        return currentStock;
    }

    private Long getStockFromVariant(List<ProductCatalogVariant> variants, Long variantId) {
        return variants.stream().filter(v -> v.getId().equals(variantId))
                .findFirst()
                .filter(item -> Objects.nonNull(item.getInitialStock()))
                .map(ProductCatalogVariant::getInitialStock)
                .map(Integer::longValue)
                .orElse(null);
    }

    private boolean filterCheckoutSuggestionEnabled(ProductCatalogDocument product, Long channelId, Boolean checkoutSuggestionEnabled) {
        if (checkoutSuggestionEnabled == null) {
            return true;
        } else {
            if (CollectionUtils.isEmpty(product.getChannels())) {
                return false;
            }
            return product.getChannels().stream().anyMatch(channel -> channelId.equals(channel.getId())
                    && checkoutSuggestionEnabled.equals(channel.getCheckoutSuggestionEnabled()));
        }
    }

    private boolean filterStandaloneEnabled(ProductCatalogDocument product, Long channelId, Boolean standaloneEnabled) {
        if (standaloneEnabled == null) {
            return true;
        } else {
            if (CollectionUtils.isEmpty(product.getChannels())) {
                return false;
            }
            return product.getChannels().stream().anyMatch(channel -> channelId.equals(channel.getId())
                    && standaloneEnabled.equals(channel.getStandaloneEnabled()));
        }
    }

    //Product, Session - Stock
    public Map<Long, Map<Long, Long>> getProductVariantStockSessions(ProductStockSearchDTO request) {

        List<ProductCatalogDocument> products = getProductsCatalog(request);
        if (CollectionUtils.isEmpty(products)) {
            return null;
        }

        Map<Long, Map<Long, Long>>  productsSessionsStock = new HashMap<>();

        for (ProductCatalogDocument product : products) {
            Long productId = product.getId();
            Long variantId = request.getProductsVariant().get(product.getId());
            if (ProductStockType.SESSION_BOUNDED.equals(product.getStockType())) {
                //Can not make bulk get because return only stock not stock - sessionId relationship
                Map<Long, Long> stockBySessionId = new HashMap<>(); //sessionId - Stock
                request.getSessionIds().forEach( sessionId
                        -> stockBySessionId.put(sessionId, getProductVariantSessionStock(product, variantId, sessionId)));
                productsSessionsStock.put(productId, stockBySessionId);
            } else {
                //one validation
                Long stock = productVariantStockCouchDao.get(productId, variantId);
                Map<Long, Long> stockBySessionId = new HashMap<>(); //sessionId - Stock
                request.getSessionIds().forEach( sessionId -> stockBySessionId.put(sessionId, stock));
                productsSessionsStock.put(productId, stockBySessionId);
            }
        }

        return productsSessionsStock;
    }

    private List<ProductCatalogDocument> getProductsCatalog(ProductStockSearchDTO request) {
        Set<Long> productsId = request.getProductsVariant().keySet();
        if (CollectionUtils.isEmpty(productsId)) {
            return null;
        }
        List<Key> productDocument = getProductsKeys(productsId);
        return  productCatalogCouchDao.bulkGet(productDocument);
    }

    private static List<Key> getProductsKeys(Set<Long> productIds) {
        return productIds.stream().map(sId -> {
            Key key = new Key();
            key.setKey(new String[]{sId.toString()});
            return key;
        }).collect(Collectors.toList());
    }
}
