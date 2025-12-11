package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.record.ProductRecord;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dao.ProductVariantSessionDao;
import es.onebox.event.products.dao.ProductVariantSessionStockCouchDao;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.domain.ProductSessionRecord;
import es.onebox.event.products.dto.ProductSessionBaseDTO;
import es.onebox.event.products.dto.ProductSessionDTO;
import es.onebox.event.products.dto.ProductSessionSearchFilter;
import es.onebox.event.products.dto.ProductSessionVariantDTO;
import es.onebox.event.products.dto.ProductSessionsDTO;
import es.onebox.event.products.dto.ProductSessionsPublishingDTO;
import es.onebox.event.products.dto.UpdateProductSessionDTO;
import es.onebox.event.products.dto.UpdateProductSessionsDTO;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionDateDTO;
import es.onebox.event.sessions.dto.SessionsDTO;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.sessions.service.SessionService;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductSessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantSessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductSessionServiceTest {
    @Mock
    ProductEventDao productEventDao;
    @Mock
    SessionDao sessionDao;
    @Mock
    ProductSessionDao productSessionDao;
    @Mock
    ProductDao productDao;
    @Mock
    SessionService sessionService;
    @Mock
    RefreshDataService refreshDataService;
    @Mock
    ProductVariantDao productVariantDao;
    @Mock
    ProductVariantSessionDao productVariantSessionDao;
    @Mock
    ProductVariantSessionStockCouchDao productVariantSessionStockCouchDao;
    @Mock
    WebhookService webhookService;
    @InjectMocks
    ProductSessionService productSessionService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPublishingSessions() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        // product not found
        try {
            productSessionService.getPublishingSessions(ObjectRandomizer.randomLong(),
                    ObjectRandomizer.randomLong(), null);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        // Channel not found
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(new ProductRecord());
        try {
            productSessionService.getPublishingSessions(ObjectRandomizer.randomLong(),
                    ObjectRandomizer.randomLong(), null);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_EVENT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        // Return all sessions
        ProductEventRecord expectedProductEvent = new ProductEventRecord();
        expectedProductEvent.setEventName("New event");
        expectedProductEvent.setProductName("New product");
        expectedProductEvent.setSessionsselectiontype(SelectionType.ALL.getId());

        Mockito.when(productEventDao.findByProductIdAndEventId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(expectedProductEvent);

        ProductSessionsPublishingDTO productSessionsPublishingDTO = productSessionService.getPublishingSessions(
                ObjectRandomizer.randomLong(),
                ObjectRandomizer.randomLong(), null);

        assertNull(productSessionsPublishingDTO.getSessions());
        assertEquals(SelectionType.ALL, productSessionsPublishingDTO.getType());

        // Return restricted sessions
        expectedProductEvent.setSessionsselectiontype(1);

        // Product session
        ProductSessionRecord productSessionRecord = new ProductSessionRecord();

        // Session
        CpanelSesionRecord sessionRecord = new CpanelSesionRecord();
        sessionRecord.setIdsesion(1);
        sessionRecord.setNombre("New event");
        sessionRecord.setFechainiciosesion(new Timestamp(new Date().getTime()));
        productSessionRecord.setSession(sessionRecord);
        productSessionRecord.setSessionid(1);
        productSessionRecord.setUsecustomstock((byte) 0);

        // Sessions restricted
        List<ProductSessionRecord> productSessionRecords = new ArrayList<>();
        productSessionRecords.add(productSessionRecord);

        Mockito.when(
                        productSessionDao.findProductSessionsByProductId(Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(productSessionRecords);
        CpanelProductRecord product = new CpanelProductRecord();
        product.setProductid(8);
        product.setType(ProductType.VARIANT.getId());
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(product);

        productSessionsPublishingDTO = productSessionService.getPublishingSessions(ObjectRandomizer.randomLong(),
                ObjectRandomizer.randomLong(), null);

        ProductSessionBaseDTO sessionExpected = productSessionsPublishingDTO.getSessions().iterator().next();

        assertNotNull(productSessionsPublishingDTO.getSessions());
        assertEquals(1, productSessionsPublishingDTO.getSessions().size());
        assertEquals(SelectionType.RESTRICTED, productSessionsPublishingDTO.getType());
        assertEquals(sessionRecord.getNombre(), sessionExpected.getName());
        assertEquals(productSessionRecord.getSessionid(), sessionExpected.getId().intValue());
    }

    @Test
    void updatePublishingSessions() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);

        // product not found
        try {
            productSessionService.updatePublishingSessions(ObjectRandomizer.randomLong(),
                    ObjectRandomizer.randomLong(), new UpdateProductSessionsDTO());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        // Channel not found
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(new ProductRecord());
        try {
            productSessionService.updatePublishingSessions(ObjectRandomizer.randomLong(),
                    ObjectRandomizer.randomLong(), new UpdateProductSessionsDTO());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_EVENT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord product = new ProductRecord();
        product.setStocktype(ProductStockType.BOUNDED.getId());
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(product);

        // Update events not valid
        UpdateProductSessionsDTO updateProductSessionsDTO = new UpdateProductSessionsDTO();
        updateProductSessionsDTO.setType(SelectionType.RESTRICTED);

        Set<Long> sessions = new HashSet<>();
        sessions.add(1L);
        sessions.add(2L);
        sessions.add(3L);

        updateProductSessionsDTO.setSessions(sessions);

        // Events recovered in db. Checking inconsistency
        List<SessionRecord> sessionRecords = new ArrayList<>();
        SessionRecord sessionRecord1 = new SessionRecord();
        sessionRecord1.setIdsesion(1);
        SessionRecord sessionRecord2 = new SessionRecord();
        sessionRecord2.setIdsesion(2);
        SessionRecord sessionRecord3 = new SessionRecord();
        sessionRecord3.setIdsesion(3);

        sessionRecords.add(sessionRecord1);
        sessionRecords.add(sessionRecord2);
        sessionRecords.add(sessionRecord3);

        Mockito.when(productEventDao.findByProductIdAndEventId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(new ProductEventRecord());

        Mockito.when(sessionDao.findSessionsByIDs(sessions)).thenReturn(sessionRecords);

        try {
            productSessionService.updatePublishingSessions(ObjectRandomizer.randomLong(),
                    ObjectRandomizer.randomLong(), updateProductSessionsDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.SESSIONS_NOT_FOUND_OR_INVALID_SESSION_STATE.getErrorCode(), e.getErrorCode());
        }

        // Sessions recovered in db are okay
        SessionRecord sessionRecord4 = new SessionRecord();
        sessionRecord4.setIdsesion(3);

        sessionRecords.add(sessionRecord4);

        productSessionService.updatePublishingSessions(ObjectRandomizer.randomLong(),
                ObjectRandomizer.randomLong(), updateProductSessionsDTO);

    }

    @Test
    void getProductSessions_productEventNotFound() {
        Long productId = 1L;
        Long eventId = 1L;
        ProductSessionSearchFilter filter = new ProductSessionSearchFilter();

        when(productEventDao.findByProductIdAndEventId(productId, eventId)).thenReturn(null);

        OneboxRestException exception = assertThrows(OneboxRestException.class,
                () -> productSessionService.getProductSessions(productId, eventId, filter));

        assertEquals(MsEventErrorCode.PRODUCT_EVENT_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void getProductSessions_success_withVariantOverrides() {
        Long productId = 1L;
        Long eventId = 1L;
        Long sessionId = 1L;
        Long variantId = 1L;
        ProductSessionSearchFilter filter = new ProductSessionSearchFilter();

        // Setup ProductEvent
        ProductEventRecord productEvent = new ProductEventRecord();
        productEvent.setProducteventid(1);
        productEvent.setEventid(eventId.intValue());
        productEvent.setSessionsselectiontype(SelectionType.ALL.getId());
        when(productEventDao.findByProductIdAndEventId(productId, eventId)).thenReturn(productEvent);

        // Setup SessionService response
        SessionsDTO sessionsDTO = new SessionsDTO();
        List<SessionDTO> sessionsList = new ArrayList<>();
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(sessionId);
        sessionDTO.setName("Test Session");

        // Setup session date to avoid NPE
        SessionDateDTO sessionDate = new SessionDateDTO();
        sessionDate.setStart(ZonedDateTime.now());
        sessionDTO.setDate(sessionDate);

        sessionsList.add(sessionDTO);
        sessionsDTO.setData(sessionsList);
        when(sessionService.searchSessions(eq(eventId), any(SessionSearchFilter.class))).thenReturn(sessionsDTO);

        // Setup ProductVariants
        List<CpanelProductVariantRecord> variants = new ArrayList<>();
        CpanelProductVariantRecord variant = new CpanelProductVariantRecord();
        variant.setVariantid(variantId.intValue());
        variant.setStock(100);
        variant.setPrice(25.0);
        variants.add(variant);
        when(productVariantDao.getProductVariantsByProductId(productId)).thenReturn(variants);

        // Setup variant override by session
        List<CpanelProductVariantSessionRecord> variantSessions = new ArrayList<>();
        CpanelProductVariantSessionRecord variantSession = new CpanelProductVariantSessionRecord();
        variantSession.setSessionid(sessionId.intValue());
        variantSession.setVariantid(variantId.intValue());
        variantSession.setUsecustomstock((byte) 1);
        variantSession.setUsecustomprice((byte) 1);
        variantSession.setStock(50);
        variantSession.setPrice(30.0);
        variantSessions.add(variantSession);
        when(productVariantSessionDao.getProductVariantSessions(productId)).thenReturn(variantSessions);

        // Setup stock counter
        when(productVariantSessionStockCouchDao.get(productId, variantId, sessionId)).thenReturn(45L);

        ProductSessionsDTO result = productSessionService.getProductSessions(productId, eventId, filter);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());

        ProductSessionDTO resultSession = result.getData().get(0);
        assertEquals(sessionId, resultSession.getId());
        assertNotNull(resultSession.getVariants());
        assertEquals(1, resultSession.getVariants().size());

        ProductSessionVariantDTO resultVariant = resultSession.getVariants().get(0);
        assertEquals(variantId, resultVariant.getId());
        assertTrue(resultVariant.getUseCustomStock());
        assertEquals(45L, resultVariant.getStock()); // Stock from counter, not from override
        assertTrue(resultVariant.getUseCustomPrice());
        assertEquals(30.0, resultVariant.getPrice());
    }

    @Test
    void getProductSessions_success_withoutOverrides() {
        Long productId = 1L;
        Long eventId = 1L;
        Long sessionId = 1L;
        Long variantId = 1L;
        ProductSessionSearchFilter filter = new ProductSessionSearchFilter();

        // Setup ProductEvent
        ProductEventRecord productEvent = new ProductEventRecord();
        productEvent.setProducteventid(1);
        productEvent.setEventid(eventId.intValue());
        productEvent.setSessionsselectiontype(SelectionType.RESTRICTED.getId());
        when(productEventDao.findByProductIdAndEventId(productId, eventId)).thenReturn(productEvent);

        // Setup restricted sessions
        List<ProductSessionRecord> productSessions = new ArrayList<>();
        ProductSessionRecord productSession = new ProductSessionRecord();
        productSession.setSessionid(sessionId.intValue());
        productSessions.add(productSession);
        when(productSessionDao.findProductSessionsByProductId(productId.intValue(), eventId.intValue()))
                .thenReturn(productSessions);

        // Setup SessionService response
        SessionsDTO sessionsDTO = new SessionsDTO();
        List<SessionDTO> sessionsList = new ArrayList<>();
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(sessionId);
        sessionDTO.setName("Test Session");

        // Setup session date to avoid NPE
        SessionDateDTO sessionDate = new SessionDateDTO();
        sessionDate.setStart(ZonedDateTime.now());
        sessionDTO.setDate(sessionDate);

        sessionsList.add(sessionDTO);
        sessionsDTO.setData(sessionsList);
        when(sessionService.searchSessions(eq(eventId), any(SessionSearchFilter.class))).thenReturn(sessionsDTO);

        // Setup ProductVariants without overrides
        List<CpanelProductVariantRecord> variants = new ArrayList<>();
        CpanelProductVariantRecord variant = new CpanelProductVariantRecord();
        variant.setVariantid(variantId.intValue());
        variant.setStock(100);
        variant.setPrice(25.0);
        variants.add(variant);
        when(productVariantDao.getProductVariantsByProductId(productId)).thenReturn(variants);

        // No variant overrides by session
        when(productVariantSessionDao.getProductVariantSessions(productId)).thenReturn(new ArrayList<>());

        // No custom stock counter
        when(productVariantSessionStockCouchDao.get(productId, variantId, sessionId)).thenReturn(null);

        ProductSessionsDTO result = productSessionService.getProductSessions(productId, eventId, filter);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());

        ProductSessionDTO resultSession = result.getData().get(0);
        assertEquals(sessionId, resultSession.getId());
        assertNotNull(resultSession.getVariants());
        assertEquals(1, resultSession.getVariants().size());

        ProductSessionVariantDTO resultVariant = resultSession.getVariants().get(0);
        assertEquals(variantId, resultVariant.getId());
        assertFalse(resultVariant.getUseCustomStock());
        assertEquals(100L, resultVariant.getStock()); // Base variant stock
        assertFalse(resultVariant.getUseCustomPrice());
        assertEquals(25.0, resultVariant.getPrice()); // Base variant price
    }

    @Test
    void updateProductSession_productEventNotFound() {
        Long productId = 1L;
        Long eventId = 1L;
        Long sessionId = 1L;
        UpdateProductSessionDTO request = new UpdateProductSessionDTO();

        when(productEventDao.findByProductIdAndEventId(productId, eventId)).thenReturn(null);

        OneboxRestException exception = assertThrows(OneboxRestException.class,
                () -> productSessionService.updateProductSession(productId, eventId, sessionId, request));

        assertEquals(MsEventErrorCode.PRODUCT_EVENT_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void updateProductSession_variantNotFound() {
        Long productId = 1L;
        Long eventId = 1L;
        Long sessionId = 1L;

        // Setup valid ProductEvent
        ProductEventRecord productEvent = new ProductEventRecord();
        productEvent.setProducteventid(1);
        productEvent.setEventid(eventId.intValue());
        when(productEventDao.findByProductIdAndEventId(productId, eventId)).thenReturn(productEvent);

        // Setup product variants
        List<CpanelProductVariantRecord> variants = new ArrayList<>();
        CpanelProductVariantRecord variant = new CpanelProductVariantRecord();
        variant.setVariantid(1);
        variants.add(variant);
        when(productVariantDao.getProductVariantsByProductId(productId)).thenReturn(variants);

        // Request with non-existent variant
        UpdateProductSessionDTO request = new UpdateProductSessionDTO();
        List<ProductSessionVariantDTO> requestVariants = new ArrayList<>();
        ProductSessionVariantDTO requestVariant = new ProductSessionVariantDTO();
        requestVariant.setId(999L); // Variant ID that doesn't exist
        requestVariants.add(requestVariant);
        request.setVariants(requestVariants);

        OneboxRestException exception = assertThrows(OneboxRestException.class,
                () -> productSessionService.updateProductSession(productId, eventId, sessionId, request));

        assertEquals(MsEventErrorCode.PRODUCT_VARIANT_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void updateProductSession_stockMandatoryWhenUsingCustomStock() {
        Long productId = 1L;
        Long eventId = 1L;
        Long sessionId = 1L;

        // Setup valid ProductEvent
        ProductEventRecord productEvent = new ProductEventRecord();
        productEvent.setProducteventid(1);
        productEvent.setEventid(eventId.intValue());
        when(productEventDao.findByProductIdAndEventId(productId, eventId)).thenReturn(productEvent);

        // Setup product variants
        List<CpanelProductVariantRecord> variants = new ArrayList<>();
        CpanelProductVariantRecord variant = new CpanelProductVariantRecord();
        variant.setVariantid(1);
        variants.add(variant);
        when(productVariantDao.getProductVariantsByProductId(productId)).thenReturn(variants);

        // Request with useCustomStock = true but without stock
        UpdateProductSessionDTO request = new UpdateProductSessionDTO();
        List<ProductSessionVariantDTO> requestVariants = new ArrayList<>();
        ProductSessionVariantDTO requestVariant = new ProductSessionVariantDTO();
        requestVariant.setId(1L);
        requestVariant.setUseCustomStock(true);
        requestVariant.setStock(null); // Missing stock
        requestVariants.add(requestVariant);
        request.setVariants(requestVariants);

        OneboxRestException exception = assertThrows(OneboxRestException.class,
                () -> productSessionService.updateProductSession(productId, eventId, sessionId, request));

        assertEquals(MsEventErrorCode.PRODUCT_EVENT_SESSION_BAD_REQUEST_STOCK_MANDATORY.getErrorCode(),
                exception.getErrorCode());
    }

    @Test
    void updateProductSession_priceMandatoryWhenUsingCustomPrice() {
        Long productId = 1L;
        Long eventId = 1L;
        Long sessionId = 1L;

        // Setup valid ProductEvent
        ProductEventRecord productEvent = new ProductEventRecord();
        productEvent.setProducteventid(1);
        productEvent.setEventid(eventId.intValue());
        when(productEventDao.findByProductIdAndEventId(productId, eventId)).thenReturn(productEvent);

        // Setup product variants
        List<CpanelProductVariantRecord> variants = new ArrayList<>();
        CpanelProductVariantRecord variant = new CpanelProductVariantRecord();
        variant.setVariantid(1);
        variants.add(variant);
        when(productVariantDao.getProductVariantsByProductId(productId)).thenReturn(variants);

        // Request with useCustomPrice = true but without price
        UpdateProductSessionDTO request = new UpdateProductSessionDTO();
        List<ProductSessionVariantDTO> requestVariants = new ArrayList<>();
        ProductSessionVariantDTO requestVariant = new ProductSessionVariantDTO();
        requestVariant.setId(1L);
        requestVariant.setUseCustomPrice(true);
        requestVariant.setPrice(null); // Missing price
        requestVariants.add(requestVariant);
        request.setVariants(requestVariants);

        OneboxRestException exception = assertThrows(OneboxRestException.class,
                () -> productSessionService.updateProductSession(productId, eventId, sessionId, request));

        assertEquals(MsEventErrorCode.PRODUCT_EVENT_SESSION_BAD_REQUEST_PRICE_MANDATORY.getErrorCode(),
                exception.getErrorCode());
    }

    @Test
    void updateProductSession_success_createNewVariantOverride() {
        Long productId = 1L;
        Long eventId = 1L;
        Long sessionId = 1L;
        Long variantId = 1L;

        // Setup valid ProductEvent
        ProductEventRecord productEvent = new ProductEventRecord();
        productEvent.setProducteventid(1);
        productEvent.setEventid(eventId.intValue());
        when(productEventDao.findByProductIdAndEventId(productId, eventId)).thenReturn(productEvent);

        // Setup product variants
        List<CpanelProductVariantRecord> variants = new ArrayList<>();
        CpanelProductVariantRecord variant = new CpanelProductVariantRecord();
        variant.setVariantid(variantId.intValue());
        variant.setStock(100);
        variants.add(variant);
        when(productVariantDao.getProductVariantsByProductId(productId)).thenReturn(variants);

        // No previous overrides exist for this variant/session
        when(productVariantSessionDao.getProductVariantSessions(productId, List.of(sessionId.intValue()))).thenReturn(new ArrayList<>());

        // Setup existing ProductSession for migration
        CpanelProductSessionRecord productSessionRecord = new CpanelProductSessionRecord();
        productSessionRecord.setUsecustomstock((byte) 1);
        when(productSessionDao.findProductEventSession(productEvent.getProducteventid(), sessionId.intValue()))
                .thenReturn(productSessionRecord);

        // Valid request
        UpdateProductSessionDTO request = new UpdateProductSessionDTO();
        List<ProductSessionVariantDTO> requestVariants = new ArrayList<>();
        ProductSessionVariantDTO requestVariant = new ProductSessionVariantDTO();
        requestVariant.setId(variantId);
        requestVariant.setUseCustomStock(true);
        requestVariant.setStock(50L);
        requestVariant.setUseCustomPrice(true);
        requestVariant.setPrice(30.0);
        requestVariants.add(requestVariant);
        request.setVariants(requestVariants);

        // Execute
        productSessionService.updateProductSession(productId, eventId, sessionId, request);

        // Verify that a new record was inserted
        verify(productVariantSessionDao).insert(any(CpanelProductVariantSessionRecord.class));
        verify(productVariantSessionDao, never()).update(any(CpanelProductVariantSessionRecord.class));

        // Verify that stock counter was updated
        String expectedCounterKey = ProductVariantSessionStockCouchDao.concatKeys(productId, variantId, sessionId);
        verify(productVariantSessionStockCouchDao).resetCounter(expectedCounterKey, 50L);

        // Verify ProductSession update for migration
        verify(productSessionDao).update(any(CpanelProductSessionRecord.class));
    }

    @Test
    void updateProductSession_success_updateExistingVariantOverride() {
        Long productId = 1L;
        Long eventId = 1L;
        Long sessionId = 1L;
        Long variantId = 1L;

        // Setup valid ProductEvent
        ProductEventRecord productEvent = new ProductEventRecord();
        productEvent.setProducteventid(1);
        productEvent.setEventid(eventId.intValue());
        when(productEventDao.findByProductIdAndEventId(productId, eventId)).thenReturn(productEvent);

        // Setup product variants
        List<CpanelProductVariantRecord> variants = new ArrayList<>();
        CpanelProductVariantRecord variant = new CpanelProductVariantRecord();
        variant.setVariantid(variantId.intValue());
        variant.setStock(100);
        variants.add(variant);
        when(productVariantDao.getProductVariantsByProductId(productId)).thenReturn(variants);

        // Previous override exists for this variant/session with custom stock enabled
        List<CpanelProductVariantSessionRecord> existingOverrides = new ArrayList<>();
        CpanelProductVariantSessionRecord existingOverride = new CpanelProductVariantSessionRecord();
        existingOverride.setVariantid(variantId.intValue());
        existingOverride.setSessionid(sessionId.intValue());
        existingOverride.setUsecustomstock((byte) 1); // Had custom stock enabled
        existingOverride.setStock(40);
        existingOverrides.add(existingOverride);
        when(productVariantSessionDao.getProductVariantSessions(productId,  List.of(sessionId.intValue()))).thenReturn(existingOverrides);

        // Setup existing ProductSession for migration
        CpanelProductSessionRecord productSessionRecord = new CpanelProductSessionRecord();
        productSessionRecord.setUsecustomstock((byte) 0);
        when(productSessionDao.findProductEventSession(productEvent.getProducteventid(), sessionId.intValue()))
                .thenReturn(productSessionRecord);

        // Valid request - keeping custom stock enabled but changing the stock value
        UpdateProductSessionDTO request = new UpdateProductSessionDTO();
        List<ProductSessionVariantDTO> requestVariants = new ArrayList<>();
        ProductSessionVariantDTO requestVariant = new ProductSessionVariantDTO();
        requestVariant.setId(variantId);
        requestVariant.setUseCustomStock(true); // Keep custom stock enabled
        requestVariant.setStock(80L); // New custom stock value
        requestVariants.add(requestVariant);
        request.setVariants(requestVariants);

        // Execute
        productSessionService.updateProductSession(productId, eventId, sessionId, request);

        // Verify that existing record was updated
        verify(productVariantSessionDao).update(any(CpanelProductVariantSessionRecord.class));
        verify(productVariantSessionDao, never()).insert(any(CpanelProductVariantSessionRecord.class));

        // Verify that counter was reset with new custom stock value
        String expectedCounterKey = ProductVariantSessionStockCouchDao.concatKeys(productId, variantId, sessionId);
        verify(productVariantSessionStockCouchDao).resetCounter(expectedCounterKey, 80L);

        // Verify ProductSession update for migration
        verify(productSessionDao).update(any(CpanelProductSessionRecord.class));
    }
}
