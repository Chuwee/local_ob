package es.onebox.event.products.service;

import com.google.common.base.Functions;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductSessionConverter;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dao.ProductVariantSessionDao;
import es.onebox.event.products.dao.ProductVariantSessionStockCouchDao;
import es.onebox.event.products.domain.ProductSessionRecord;
import es.onebox.event.products.dto.ProductSessionDTO;
import es.onebox.event.products.dto.ProductSessionSearchFilter;
import es.onebox.event.products.dto.ProductSessionVariantDTO;
import es.onebox.event.products.dto.ProductSessionsDTO;
import es.onebox.event.products.dto.ProductSessionsPublishingDTO;
import es.onebox.event.products.dto.ProductSessionsPublishingFilterDTO;
import es.onebox.event.products.dto.UpdateProductSessionDTO;
import es.onebox.event.products.dto.UpdateProductSessionsDTO;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionsDTO;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.sessions.service.SessionService;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelProductEventRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductSessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantSessionRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductSessionService {
    private final SessionDao sessionDao;
    private final ProductDao productDao;
    private final SessionService sessionService;
    private final ProductEventDao productEventDao;
    private final ProductSessionDao productSessionDao;
    private final RefreshDataService refreshDataService;
    private final ProductVariantDao productVariantDao;
    private final ProductVariantSessionStockCouchDao productVariantSessionStockCouchDao;
    private final ProductVariantSessionDao productVariantSessionDao;
    private final WebhookService webhookService;

    @Autowired
    public ProductSessionService(ProductDao productDao, SessionService sessionService, ProductEventDao productEventDao,
                                 ProductSessionDao productSessionDao, SessionDao sessionDao,
                                 RefreshDataService refreshDataService, ProductVariantDao productVariantDao,
                                 ProductVariantSessionStockCouchDao productVariantSessionStockCouchDao,
                                 ProductVariantSessionDao productVariantSessionDao,
                                 WebhookService webhookService) {
        this.productDao = productDao;
        this.sessionService = sessionService;
        this.productEventDao = productEventDao;
        this.productSessionDao = productSessionDao;
        this.sessionDao = sessionDao;
        this.refreshDataService = refreshDataService;
        this.productVariantDao = productVariantDao;
        this.productVariantSessionStockCouchDao = productVariantSessionStockCouchDao;
        this.productVariantSessionDao = productVariantSessionDao;
        this.webhookService = webhookService;
    }

    @MySQLRead
    public ProductSessionsPublishingDTO getPublishingSessions(Long productId, Long eventId, ProductSessionsPublishingFilterDTO filter) {
        CpanelProductEventRecord productEventRecord = checkProductAndProductEvent(productId, eventId);

        if (SelectionType.ALL.equals(SelectionType.get(productEventRecord.getSessionsselectiontype()))) {
            return ProductSessionConverter.allSessionsScope();
        }
        List<ProductSessionRecord> productSessionRecords =
                productSessionDao.findProductSessionsByProductId(productId.intValue(), eventId.intValue(), filter);

        return ProductSessionConverter.toProductSessions(productSessionRecords);
    }

    @MySQLWrite
    public void updatePublishingSessions(Long productId, Long eventId, UpdateProductSessionsDTO request) {
        CpanelProductRecord product = getAndCheckProduct(productId);
        CpanelProductEventRecord productEventRecord = getAndCheckProductEvent(productId, eventId);

        if (SelectionType.ALL.equals(request.getType())) {
            if (!isProductSessionBoundStockType(product)) {
                this.productSessionDao.deleteByProductEventId(productEventRecord.getProducteventid());
            }
            this.productEventDao.updateTargetType(productEventRecord.getProducteventid(),
                    Map.of(ProductEventDao.FIELD_SESSION_SELECTION_TYPE, SelectionType.ALL.getId()));

            webhookService.sendProductSessionNotification(productId, eventId, NotificationSubtype.PRODUCT_SESSIONS);
            return;
        }

        if (CommonUtils.isEmpty(request.getSessions())) {
            removeProductVariantSessionCounters(product, productEventRecord);
            this.productSessionDao.deleteByProductEventId(productEventRecord.getProducteventid());
        } else {
            //Validate sessions to update exist in the db
            List<SessionRecord> sessions = sessionDao.findSessionsByIDs(request.getSessions());

            List<Long> sessionIds = sessions.stream().map(s -> (long) s.getIdsesion()).toList();
            if (CollectionUtils.isEmpty(sessionIds) || !new HashSet<>(sessionIds).containsAll(request.getSessions())) {
                throw ExceptionBuilder.build(MsEventErrorCode.SESSIONS_NOT_FOUND_OR_INVALID_SESSION_STATE);
            }

            Set<CpanelProductSessionRecord> records = request.getSessions().stream()
                    .map(s -> {
                        CpanelProductSessionRecord record = new CpanelProductSessionRecord();
                        record.setProducteventid(productEventRecord.getProducteventid());
                        record.setSessionid(s.intValue());
                        record.setUsecustomstock(getSelectionTypeOrDefaultByStockType(s, product, productEventRecord.getProducteventid()));
                        record.setCreateDate(new Timestamp(ZonedDateTime.now().toEpochSecond()));
                        record.setUpdateDate(new Timestamp(ZonedDateTime.now().toEpochSecond()));
                        return record;
                    })
                    .collect(Collectors.toSet());

            removeProductVariantSessionCounters(product, productEventRecord);
            productSessionDao.deleteByProductEventId(productEventRecord.getProducteventid());
            productSessionDao.insertBatch(records);
            productEventDao.updateTargetType(productEventRecord.getProducteventid(),
                    Map.of(ProductEventDao.FIELD_SESSION_SELECTION_TYPE, SelectionType.RESTRICTED.getId()));
        }
        postUpdateProduct(productId, request.getSessions(), productEventRecord.getEventid());
        webhookService.sendProductSessionNotification(productId, eventId, NotificationSubtype.PRODUCT_SESSIONS);
    }

    @MySQLRead
    public ProductSessionsDTO getProductSessions(Long productId, Long eventId, ProductSessionSearchFilter filter) {
        CpanelProductEventRecord productEventRecord = getAndCheckProductEvent(productId, eventId);
        SessionSearchFilter sessionFilter = prepareSessionSearchFilter(productId, eventId, filter, productEventRecord);
        SessionsDTO sessions = sessionService.searchSessions(productEventRecord.getEventid().longValue(), sessionFilter);
        ProductSessionsDTO sessionsResponse = ProductSessionConverter.toProductSessionsResponse(sessions);

        // Default product variants
        List<CpanelProductVariantRecord> productVariants = productVariantDao.getProductVariantsByProductId(productId);

        // Override product variants by session
        List<CpanelProductVariantSessionRecord> productVariantSessions = productVariantSessionDao.getProductVariantSessions(productId);
        Map<Integer, Map<Integer, CpanelProductVariantSessionRecord>> overridesBySessionAndVariant =
                productVariantSessions.stream().collect(Collectors.groupingBy(
                        CpanelProductVariantSessionRecord::getSessionid, LinkedHashMap::new, Collectors.toMap(
                                CpanelProductVariantSessionRecord::getVariantid, v -> v,
                                (v1, v2) -> v1, LinkedHashMap::new)
                ));

        for (ProductSessionDTO session : sessionsResponse.getData()) {
            session.setVariants(new ArrayList<>());
            Map<Integer, CpanelProductVariantSessionRecord> sessionVariants = overridesBySessionAndVariant.get(session.getId().intValue());
            for (CpanelProductVariantRecord variant : productVariants) {
                ProductSessionVariantDTO dto = new ProductSessionVariantDTO();
                Integer variantId = variant.getVariantid();
                dto.setId(variantId.longValue());
                dto.setUseCustomStock(false);
                dto.setStock(variant.getStock() != null ? variant.getStock().longValue() : null);
                dto.setUseCustomPrice(false);
                dto.setPrice(variant.getPrice());
                if (MapUtils.isNotEmpty(sessionVariants) && sessionVariants.containsKey(variantId)) {
                    CpanelProductVariantSessionRecord variantOverride = sessionVariants.get(variantId);
                    if (CommonUtils.isTrue(variantOverride.getUsecustomstock())) {
                        dto.setUseCustomStock(true);
                        dto.setStock(variantOverride.getStock().longValue());
                    }
                    if (CommonUtils.isTrue(variantOverride.getUsecustomprice())) {
                        dto.setUseCustomPrice(true);
                        dto.setPrice(variantOverride.getPrice());
                    }
                }

                Long sessionStockCounter = productVariantSessionStockCouchDao.get(productId, variantId.longValue(), session.getId());
                if (sessionStockCounter != null) {
                    dto.setStock(sessionStockCounter);
                }

                //TODO remove after migration
                if (session.getUseCustomStock() == null) {
                    session.setUseCustomStock(dto.getUseCustomStock());
                    session.setStock(dto.getStock());
                }
                session.getVariants().add(dto);
            }
        }
        return sessionsResponse;
    }

    @MySQLWrite
    public void updateProductSession(Long productId, Long eventId, Long sessionId, UpdateProductSessionDTO request) {
        CpanelProductEventRecord productEvent = getAndCheckProductEvent(productId, eventId);

        Long sessionRelatedSB =  checkAndGetSBSessionId(sessionId, request);
        List<Long> sessions = new ArrayList<>();
        sessions.add(sessionId);
        if (sessionRelatedSB != null) {
            sessions.add(sessionRelatedSB);
        }

        List<CpanelProductVariantRecord> variants = productVariantDao.getProductVariantsByProductId(productId);
        Map<Integer, CpanelProductVariantRecord> variantsById = variants.stream()
                .collect(Collectors.toMap(CpanelProductVariantRecord::getVariantid, Function.identity()));

        validateUpdate(request, variantsById);

        List<CpanelProductVariantSessionRecord> productVariantSessions = productVariantSessionDao.getProductVariantSessions(productId, sessions.stream().map(Long::intValue).toList());
        Map<Integer, List<CpanelProductVariantSessionRecord>> variantOverrides = productVariantSessions.stream()
                .collect(Collectors.groupingBy(CpanelProductVariantSessionRecord::getVariantid));

        sessions.forEach(session -> updateProductVariant(productId, session, request, variantOverrides, variantsById, productEvent));


        postUpdateProduct(productId, new HashSet<>(sessions), productEvent.getEventid());

        webhookService.sendProductSessionNotification(productId, eventId, NotificationSubtype.PRODUCT_SESSIONS);
    }

    private void updateProductVariant(Long productId, Long sessionId, UpdateProductSessionDTO request,
                                      Map<Integer, List<CpanelProductVariantSessionRecord>>  variantOverrides,
                                      Map<Integer, CpanelProductVariantRecord> variantsById,
                                      CpanelProductEventRecord productEvent) {
        for (ProductSessionVariantDTO requestVariant : request.getVariants()) {
            Long variantId = requestVariant.getId();
            List<CpanelProductVariantSessionRecord> sessionsOverride = variantOverrides.get(variantId.intValue());
            CpanelProductVariantSessionRecord sessionOverride = null;
            if (CollectionUtils.isNotEmpty(sessionsOverride)) {
                sessionOverride = sessionsOverride.stream()
                        .filter(session -> session.getSessionid().equals(sessionId.intValue()))
                        .findFirst().orElse(null);
            }

            CpanelProductVariantSessionRecord record = sessionOverride;
            if (record == null) {
                record = new CpanelProductVariantSessionRecord();
                record.setVariantid(variantId.intValue());
                record.setSessionid(sessionId.intValue());
            }
            if (requestVariant.getUseCustomStock() != null) {
                record.setUsecustomstock((byte) CommonUtils.isTrueAsInt(requestVariant.getUseCustomStock()));

                String counterKey = ProductVariantSessionStockCouchDao.concatKeys(productId, variantId, sessionId);
                if (BooleanUtils.isTrue(requestVariant.getUseCustomStock())) {
                    record.setStock(requestVariant.getStock().intValue());
                    productVariantSessionStockCouchDao.resetCounter(counterKey, requestVariant.getStock());
                } else if (sessionOverride == null || CommonUtils.isTrue(sessionOverride.getUsecustomstock())) {
                    CpanelProductVariantRecord baseVariant = variantsById.get(variantId.intValue());
                    productVariantSessionStockCouchDao.resetCounter(counterKey, NumberUtils.zeroIfNull(baseVariant.getStock()).longValue());
                }

                //TODO remove after migration
                updateProductSession(sessionId, productEvent, record);
            }
            if (requestVariant.getUseCustomPrice() != null) {
                record.setUsecustomprice((byte) CommonUtils.isTrueAsInt(requestVariant.getUseCustomPrice()));
                if (BooleanUtils.isTrue(requestVariant.getUseCustomPrice())) {
                    record.setPrice(requestVariant.getPrice());
                }
            }
            if (sessionOverride != null) {
                productVariantSessionDao.update(record);
            } else {
                productVariantSessionDao.insert(record);
            }
        }
    }

    private Long checkAndGetSBSessionId(Long sessionId, UpdateProductSessionDTO request) {
        if (BooleanUtils.isTrue(request.getUseCustomStock()) && request.getStock() != null) {
            // Control the stock between SA/SB Sessions. The stock should be the same in both
            SessionRecord sessionRecord = sessionDao.findSession(sessionId);
            if (sessionRecord != null && sessionRecord.getSbsesionrelacionada() != null) {
                return sessionRecord.getSbsesionrelacionada().longValue();
            }
        }
        return null;
    }

    private void validateUpdate(UpdateProductSessionDTO request, Map<Integer, CpanelProductVariantRecord> variantsById) {

        if (CollectionUtils.isNotEmpty(request.getVariants())) {
            for (ProductSessionVariantDTO requestVariant : request.getVariants()) {
                if (BooleanUtils.isTrue(requestVariant.getUseCustomStock()) && requestVariant.getStock() == null) {
                    throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_SESSION_BAD_REQUEST_STOCK_MANDATORY);
                }
                if (BooleanUtils.isTrue(requestVariant.getUseCustomPrice()) && requestVariant.getPrice() == null) {
                    throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_SESSION_BAD_REQUEST_PRICE_MANDATORY);
                }
                if (requestVariant.getId() == null || !variantsById.containsKey(requestVariant.getId().intValue())) {
                    throw new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_NOT_FOUND);
                }
            }
        }

        //TODO remove after migration
        if (BooleanUtils.isTrue(request.getUseCustomStock()) && request.getStock() == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_SESSION_BAD_REQUEST_STOCK_MANDATORY);
        }
        if (request.getUseCustomStock() != null && CollectionUtils.isEmpty(request.getVariants())) {
            CpanelProductVariantRecord defaultVariant = variantsById.values().stream()
                    .findFirst()
                    .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_NOT_FOUND));

            ProductSessionVariantDTO compatibleRequest = new ProductSessionVariantDTO();
            compatibleRequest.setId(defaultVariant.getVariantid().longValue());
            compatibleRequest.setUseCustomStock(request.getUseCustomStock());
            compatibleRequest.setStock(request.getStock());
            request.setVariants(List.of(compatibleRequest));
        }
    }

    //TODO remove after migration
    private void updateProductSession(Long sessionId, CpanelProductEventRecord productEvent, CpanelProductVariantSessionRecord record) {
        CpanelProductSessionRecord sessionRecord = productSessionDao.findProductEventSession(productEvent.getProducteventid(), sessionId.intValue());
        if (sessionRecord == null) {
            sessionRecord = new CpanelProductSessionRecord();
            sessionRecord.setSessionid(sessionId.intValue());
            sessionRecord.setProducteventid(productEvent.getProducteventid());
            sessionRecord.setUsecustomstock(record.getUsecustomstock());
            productSessionDao.insert(sessionRecord);
        } else {
            sessionRecord.setUsecustomstock(record.getUsecustomstock());
            productSessionDao.update(sessionRecord);
        }
    }

    private SessionSearchFilter prepareSessionSearchFilter(Long productId, Long eventId,
                                                           ProductSessionSearchFilter filter,
                                                           CpanelProductEventRecord productEventRecord) {
        SessionSearchFilter sessionFilter = ProductSessionConverter.convertToSessionFilter(filter);
        if (SelectionType.RESTRICTED.equals(SelectionType.get(productEventRecord.getSessionsselectiontype()))) {
            List<ProductSessionRecord> productSessions = productSessionDao.findProductSessionsByProductId(productId.intValue(), eventId.intValue());
            if (CollectionUtils.isNotEmpty(productSessions)) {
                sessionFilter.setIds(productSessions.stream().map(CpanelProductSessionRecord::getSessionid).map(Integer::longValue).toList());
            }
        }
        return sessionFilter;
    }

    private void removeProductVariantSessionCounters(CpanelProductRecord product, CpanelProductEventRecord
            productEventRecord) {
        if (isProductSessionBoundStockType(product)) {
            List<CpanelProductVariantRecord> variants = productVariantDao.getProductVariantsByProductId(product.getProductid().longValue());
            if (CollectionUtils.isNotEmpty(variants)) {
                CpanelProductVariantRecord defaultVariant = variants.stream().findFirst().orElse(null);
                if (defaultVariant != null) {
                    List<CpanelProductSessionRecord> productSessions = productSessionDao.findByProductEventId(productEventRecord.getProducteventid());
                    if (CollectionUtils.isNotEmpty(productSessions)) {
                        productSessions.forEach(s
                                -> productVariantSessionStockCouchDao.remove(ProductVariantSessionStockCouchDao.concatKeys(product.getProductid().longValue(),
                                defaultVariant.getVariantid().longValue(), s.getSessionid().longValue())));
                    }
                }
            }
        }
    }

    private Byte getSelectionTypeOrDefaultByStockType(Long sessionId, CpanelProductRecord product, Integer productEventId) {
        if (isProductSessionBoundStockType(product)) {
            CpanelProductSessionRecord sessionRecord = productSessionDao.findProductEventSession(productEventId, sessionId.intValue());
            if (sessionRecord != null) {
                return sessionRecord.getUsecustomstock();
            }
        }
        return (byte) 0;
    }

    private static boolean isProductSessionBoundStockType(CpanelProductRecord product) {
        return ProductStockType.SESSION_BOUNDED.equals(ProductStockType.get(product.getStocktype()));
    }

    private CpanelProductEventRecord checkProductAndProductEvent(Long productId, Long eventId) {
        getAndCheckProduct(productId);
        return getAndCheckProductEvent(productId, eventId);
    }

    private CpanelProductRecord getAndCheckProduct(Long productId) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        return cpanelProductRecord;
    }

    private CpanelProductEventRecord getAndCheckProductEvent(Long productId, Long eventId) {
        CpanelProductEventRecord productEventRecord =
                productEventDao.findByProductIdAndEventId(productId, eventId);
        if (productEventRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_NOT_FOUND);
        }
        return productEventRecord;
    }

    private void postUpdateProduct(Long productId, Set<Long> sessionIds, Integer eventId) {
        // update products catalog
        refreshDataService.refreshProduct(productId);

        // update channel-session published products
        if (sessionIds.isEmpty()) {
            Set<Integer> validatedEventIds = productSessionDao.findRelatedEvents(productId, List.of(eventId.longValue()), null);
            if (validatedEventIds != null && !validatedEventIds.isEmpty()) {
                for (Integer validatedEventId : validatedEventIds) {
                    refreshDataService.refreshEvent(validatedEventId.longValue(), "productSessionService.postUpdateProduct");
                }
            }
        } else {
            Map<Long, List<Long>> publishedSessions = productSessionDao.findPublishedSessions(productId, sessionIds, null, null);
            if (MapUtils.isNotEmpty(publishedSessions)) {
                for (Map.Entry<Long, List<Long>> sessionsByEvent : publishedSessions.entrySet()) {
                    refreshDataService.refreshSessions(sessionsByEvent.getKey(), sessionsByEvent.getValue(), "productSessionService.postUpdateProduct");
                }
            }
        }

    }

}
