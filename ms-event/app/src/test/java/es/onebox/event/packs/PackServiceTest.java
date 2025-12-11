package es.onebox.event.packs;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogDTO;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.service.CatalogService;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.event.packs.dao.PackCommunicationElementDao;
import es.onebox.event.packs.dao.PackDao;
import es.onebox.event.packs.dao.PackItemSubsetDao;
import es.onebox.event.packs.dao.PackItemsDao;
import es.onebox.event.packs.dao.PackItemsPriceTypeDao;
import es.onebox.event.packs.dao.PackPriceTypeMappingDao;
import es.onebox.event.packs.dto.CreatePackItemDTO;
import es.onebox.event.packs.dto.CreatePackItemsDTO;
import es.onebox.event.packs.dto.PackCreateRequest;
import es.onebox.event.packs.dto.PackDTO;
import es.onebox.event.packs.dto.PackDetailDTO;
import es.onebox.event.packs.dto.PackItemDTO;
import es.onebox.event.packs.dto.PackItemPriceTypeRequest;
import es.onebox.event.packs.dto.PackItemPriceTypesResponseDTO;
import es.onebox.event.packs.dto.PackItemSubsetsFilter;
import es.onebox.event.packs.dto.PackItemSubsetsResponseDTO;
import es.onebox.event.packs.dto.PackUpdateRequest;
import es.onebox.event.packs.dto.PriceTypeRange;
import es.onebox.event.packs.dto.UpdatePackItemDTO;
import es.onebox.event.packs.dto.UpdatePackItemSubitemsRequestDTO;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.packs.enums.PackStatus;
import es.onebox.event.packs.service.PackRateAndPricesService;
import es.onebox.event.packs.service.PackService;
import es.onebox.event.products.service.ProductEventService;
import es.onebox.event.products.service.ProductSessionService;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.TaxDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemSubsetRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemZonaPrecioRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static es.onebox.event.exception.MsEventErrorCode.INVALID_ENTITY_TAX;
import static es.onebox.event.exception.MsEventPackErrorCode.PACK_ITEM_INVALID_FOR_SUBSETS;
import static es.onebox.event.exception.MsEventPackErrorCode.PACK_ITEM_NOT_FOUND;
import static es.onebox.event.exception.MsEventPackErrorCode.PACK_ITEM_PRICE_TYPES_INVALID_MAPPING;
import static es.onebox.event.exception.MsEventPackErrorCode.PACK_ITEM_SUBSETS_NOT_FOUND;
import static es.onebox.event.exception.MsEventPackErrorCode.PACK_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class PackServiceTest extends DaoImplTest {

    private static final String PATH_BASE = "dao/";

    @InjectMocks
    private PackDao packDao;
    @InjectMocks
    private PackItemsDao packItemsDao;
    @InjectMocks
    private PackItemSubsetDao packItemSubsetDao;
    @Mock
    private SessionDao sessionDao;
    @Mock
    private PackCommunicationElementDao packCommunicationElementDao;
    @Mock
    private PackItemsPriceTypeDao packItemsPriceTypeDao;
    @Mock
    private PackPriceTypeMappingDao packPriceTypeMappingDao;
    @Mock
    private TaxDao taxDao;
    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private VenuesRepository venuesRepository;
    @Mock
    private PackRateAndPricesService packRateAndPricesService;
    @Mock
    private ProductEventService productEventService;
    @Mock
    private ProductSessionService productSessionService;
    @Mock
    private CatalogService catalogService;

    private PackService service;

    @Override
    protected String getDatabaseFile() {
        return PATH_BASE + "PackItemSubsetDaoTest.sql";
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
        MockitoAnnotations.openMocks(this);
        service = new PackService(
                packDao,
                packItemsDao,
                sessionDao,
                packItemSubsetDao,
                packCommunicationElementDao,
                packItemsPriceTypeDao,
                packPriceTypeMappingDao,
                taxDao,
                entitiesRepository,
                venuesRepository,
                packRateAndPricesService,
                productEventService,
                productSessionService,
                catalogService
        );
    }

    @Test
    void createPack_WithEventMainItemAndSubItems_ShouldCreatePackWithSubsets() {
        PackCreateRequest request = new PackCreateRequest();
        CreatePackItemDTO mainItem = new CreatePackItemDTO();
        mainItem.setItemId(1L);
        mainItem.setType(PackItemType.EVENT);
        mainItem.setSubItemIds(List.of(10,11,12));
        mainItem.setVenueTemplateId(3);
        request.setName("Pack Test");
        request.setMainItem(mainItem);
        request.setEntityId(1L);
        request.setTaxId(1L);
        when(taxDao.getTaxesByEntity(1L)).thenReturn(List.of(1L));

        List<SessionRecord> sessionRecord = new ArrayList<>();
        SessionRecord session = new SessionRecord();
        SessionRecord session2 = new SessionRecord();
        SessionRecord session3 = new SessionRecord();
        session.setIdsesion(10);
        session2.setIdsesion(11);
        session3.setIdsesion(12);
        sessionRecord.add(session);
        sessionRecord.add(session2);
        sessionRecord.add(session3);
        when(sessionDao.findSessions(any(), any())).thenReturn(sessionRecord);


        PackDTO result = service.createPack(request);

        Assertions.assertNotNull(result);

        CpanelPackItemRecord mainItemRecord = packItemsDao.getPackMainItemRecordById(result.getId().intValue());
        Assertions.assertNotNull(mainItemRecord);

        List<CpanelPackItemSubsetRecord> insertedSubsets = packItemSubsetDao.getSubsetsByPackItemId(mainItemRecord.getIdpackitem());

        Assertions.assertEquals(3, insertedSubsets.size());
        Assertions.assertTrue(insertedSubsets.stream().anyMatch(s -> s.getIdsubitem().equals(10)));
        Assertions.assertTrue(insertedSubsets.stream().anyMatch(s -> s.getIdsubitem().equals(11)));
        Assertions.assertTrue(insertedSubsets.stream().anyMatch(s -> s.getIdsubitem().equals(12)));
    }

    @Test
    void getPackItemSubsets_WithExistingSubsets_ShouldReturnSubsetDtos() {
        Long packId = 1L;
        Long packItemId = 20L;

        PackItemSubsetsFilter filter = new PackItemSubsetsFilter();
        filter.setLimit(5L);
        filter.setOffset(0L);

        es.onebox.event.sessions.domain.Session s1 = new es.onebox.event.sessions.domain.Session();
        s1.setSessionId(100L);
        s1.setName("Session A");
        s1.setSessionStartDate(ZonedDateTime.of(2024,12,2,3,0,0,0, ZonedDateTime.now().getZone()));

        es.onebox.event.sessions.domain.Session s2 = new es.onebox.event.sessions.domain.Session();
        s2.setSessionId(101L);
        s2.setName("Session B");
        s2.setSessionStartDate(ZonedDateTime.of(2024,12,2,3,0,0,0, ZonedDateTime.now().getZone()));

        es.onebox.event.sessions.domain.Session s3 = new es.onebox.event.sessions.domain.Session();
        s3.setSessionId(102L);
        s3.setName("Session C");
        s3.setSessionStartDate(ZonedDateTime.of(2024,12,2,3,0,0,0, ZonedDateTime.now().getZone()));

        when(sessionDao.findSessionsById(anyList())).thenReturn(List.of(s1, s2, s3));

        PackItemSubsetsResponseDTO result = service.getPackItemSubsets(packId, packItemId, filter);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getData());
        Assertions.assertNotNull(result.getMetadata());

        Assertions.assertEquals(3, result.getData().size());
        Assertions.assertEquals(3L, result.getMetadata().getTotal());
        Assertions.assertEquals(5L, result.getMetadata().getLimit());
        Assertions.assertEquals(0L, result.getMetadata().getOffset());

        Assertions.assertTrue(result.getData().stream().anyMatch(i -> i.getId().equals(100L) && i.getName().equals("Session A")));
        Assertions.assertTrue(result.getData().stream().anyMatch(i -> i.getId().equals(101L) && i.getName().equals("Session B")));
        Assertions.assertTrue(result.getData().stream().anyMatch(i -> i.getId().equals(102L) && i.getName().equals("Session C")));
    }

    @Test
    void getPackById_shouldReturnPackDetailDTO() {
        Long packId = 1L;

        PackDetailDTO dto = service.getPackById(packId);

        assertNotNull(dto);
        assertEquals(packId, dto.getId());
        assertTrue(dto.getActive());
    }

    @Test
    void getPackItems_shouldReturnItems() {
        Long packId = 1L;

        List<PackItemDTO> result = service.getPackItems(packId);

        assertNotNull(result);
        assertEquals(7, result.size());
    }

    @Test
    void createPack_shouldInsertPackAndMainItem() {
        PackCreateRequest request = new PackCreateRequest();
        request.setName("Test Pack");
        CreatePackItemDTO mainItem = new CreatePackItemDTO();
        mainItem.setItemId(1L);
        mainItem.setType(PackItemType.EVENT);
        mainItem.setVenueTemplateId(10);
        request.setMainItem(mainItem);
        request.setEntityId(1L);
        request.setTaxId(1L);
        when(taxDao.getTaxesByEntity(1L)).thenReturn(List.of(1L));
        
        IdNameCodeDTO priceType = new IdNameCodeDTO();
        priceType.setId(1L);
        when(venuesRepository.getPriceTypes(any())).thenReturn(List.of(priceType));

        PackDTO dto = service.createPack(request);

        assertNotNull(dto);
        assertNotNull(dto.getId());
        CpanelPackRecord insertedPack = packDao.getPackRecordById(dto.getId().intValue());
        assertNotNull(insertedPack);
        CpanelPackItemRecord mainItemRecord = packItemsDao.getPackMainItemRecordById(dto.getId().intValue());
        assertNotNull(mainItemRecord);
    }

    @Test
    void deletePack_shouldDeleteItemsAndPack() {
        int packId = 2;

        service.deletePack((long) packId);

        CpanelPackRecord deletedPack = packDao.getPackRecordById(packId);
        assertTrue(deletedPack == null || Objects.equals(deletedPack.getEstado(), PackStatus.DELETED.getId()));
    }

    @Test
    void deletePack_shouldDeleteSubsetsOfMainItem() {
        Long packId = 1L;

        CpanelPackItemRecord mainItem = packItemsDao.getPackMainItemRecordById(packId.intValue());
        assertNotNull(mainItem);

        service.deletePack(packId);

        List<CpanelPackItemSubsetRecord> subsetsAfter = packItemSubsetDao.getSubsetsByPackItemId(mainItem.getIdpackitem());
        assertEquals(0, subsetsAfter.size());

        CpanelPackRecord deletedPack = packDao.getPackRecordById(packId.intValue());
        assertTrue(deletedPack == null || Objects.equals(deletedPack.getEstado(), PackStatus.DELETED.getId()));
    }

    @Test
    void deletePackItem_shouldDeleteItem() {
        Long packId = 2L;
        Long itemId = 200L;

        service.deletePackItem(packId, itemId);

        CpanelPackItemRecord deletedItem = packItemsDao.getPackItemRecordById(itemId.intValue());
        assertNull(deletedItem);
    }

    @Test
    void getPackMainItemEventId_shouldReturnEvent_whenTypeIsEvent() {
        Integer packId = 1;

        Session session = new Session();
        session.setSessionId(1L);
        session.setEventId(10L);
        when(catalogService.getSession(1)).thenReturn(session);

        Long result = service.getPackMainItemEventId(packId);

        assertEquals(10L, result);
    }

    @Test
    void getPackMainItemEventId_shouldResolveSessionToEvent() {
        Integer packId = 1;

        Session mockSession = new Session();
        mockSession.setSessionId(1L);
        mockSession.setEventId(99L);
        when(catalogService.getSession(1)).thenReturn(mockSession);

        Long result = service.getPackMainItemEventId(packId);

        assertEquals(99L, result);
    }

    @Test
    void createPackItems_shouldInsertMultipleItems() {
        Long packId = 1L;

        Session mainSession = new Session();
        mainSession.setSessionId(1L);
        mainSession.setEventId(100L);
        when(catalogService.getSession(eq(1))).thenReturn(mainSession);

        Event event = new Event();
        event.setCurrency(1);
        when(catalogService.getEvent(eq(100))).thenReturn(event);

        CreatePackItemsDTO request = new CreatePackItemsDTO();
        CreatePackItemDTO item1 = new CreatePackItemDTO();
        item1.setItemId(3L);
        item1.setType(PackItemType.SESSION);
        CreatePackItemDTO item2 = new CreatePackItemDTO();
        item2.setItemId(2L);
        item2.setType(PackItemType.PRODUCT);
        request.add(item1);
        request.add(item2);

        Session session1 = new Session();
        session1.setSessionId(3L);
        session1.setEventId(100L);
        when(catalogService.getSession(eq(3))).thenReturn(session1);

        ProductCatalogDTO product = new ProductCatalogDTO();
        product.setCurrencyId(1);
        when(catalogService.findCatalogProduct(eq(2L))).thenReturn(product);

        service.createPackItems(packId, request);

        List<CpanelPackItemRecord> items = packItemsDao.getPackItemRecordsById(packId.intValue());
        assertEquals(9, items.size());
    }

    @Test
    void updatePackItem_shouldUpdateFieldsCorrectly() {
        Long packId = 1L;
        Long packItemId = 201L;

        UpdatePackItemDTO dto = new UpdatePackItemDTO();
        dto.setPriceTypeId(100);
        dto.setDisplayItemInChannels(true);
        dto.setPriceTypeRange(PriceTypeRange.ALL);
        dto.setInformativePrice(12.34);

        service.updatePackItem(packId, packItemId, dto);

        CpanelPackItemRecord updatedItem = packItemsDao.getPackItemRecordById(packItemId.intValue());
        assertNotNull(updatedItem);
    }

    @Test
    void updatePackItem_shouldHandleProductItemWithAutomaticSubtype() {
        Long packId = 1L;
        Long packItemId = 202L;

        UpdatePackItemDTO dto = new UpdatePackItemDTO();
        dto.setVariantId(5);
        dto.setDeliveryPointId(10);
        dto.setSharedBarcode(true);
        dto.setDisplayItemInChannels(false);
        dto.setPriceTypeRange(PriceTypeRange.RESTRICTED);
        dto.setInformativePrice(19.99);

        service.updatePackItem(packId, packItemId, dto);

        CpanelPackItemRecord updatedItem = packItemsDao.getPackItemRecordById(packItemId.intValue());
        assertNotNull(updatedItem);
    }

    @Test
    void getPackItems_shouldThrowWhenPackNotFound() {
        Long packId = 999L;

        assertThrows(OneboxRestException.class, () -> service.getPackItems(packId));
    }

    @Test
    void getPackItemPriceTypes_shouldReturnAllType() {
        Long packId = 1L;
        Long packItemId = 203L;

        PackItemPriceTypesResponseDTO result = service.getPackItemPriceTypes(packId, packItemId);

        assertNotNull(result);
        assertEquals(PriceTypeRange.ALL, result.getSelectionType());
    }

    @Test
    void getPackItemPriceTypes_shouldReturnRestrictedType() {
        Long packId = 1L;
        Long packItemId = 204L;

        Session session = new Session();
        session.setSessionId(1L);
        session.setVenueConfigId(10L);
        when(catalogService.getSession(eq(1))).thenReturn(session);

        IdNameCodeDTO priceType = new IdNameCodeDTO();
        priceType.setId(1L);
        when(venuesRepository.getPriceTypes(any())).thenReturn(List.of(priceType));

        CpanelPackItemZonaPrecioRecord zoneRecord = new CpanelPackItemZonaPrecioRecord();
        zoneRecord.setIdzonaprecio(1);
        when(packItemsPriceTypeDao.getPackItemPriceTypesById(packItemId.intValue())).thenReturn(List.of(zoneRecord));

        PackItemPriceTypesResponseDTO result = service.getPackItemPriceTypes(packId, packItemId);

        assertNotNull(result);
        assertEquals(PriceTypeRange.RESTRICTED, result.getSelectionType());
    }

    @Test
    void updatePackItemPriceTypes_shouldUpdateAndInsertRestrictedTypes() {
        Long packId = 1L;
        Long packItemId = 205L;

        Session session = new Session();
        session.setSessionId(1L);
        session.setVenueConfigId(10L);
        when(catalogService.getSession(eq(1))).thenReturn(session);

        IdNameCodeDTO dto = new IdNameCodeDTO();
        dto.setId(100L);
        when(venuesRepository.getPriceTypes(any())).thenReturn(List.of(dto));

        PackItemPriceTypeRequest request = new PackItemPriceTypeRequest();
        request.setSelectionType(PriceTypeRange.RESTRICTED);
        request.setPriceTypeIds(List.of(100));

        service.updatePackItemPriceTypes(packId, packItemId, request);

        verify(packItemsPriceTypeDao).deletePackItemPriceTypesByConfigIdAndPackId(packItemId.intValue());
        verify(packItemsPriceTypeDao).bulkInsert(anyList());
    }

    @Test
    void updatePackTax_InvalidEntityTax() {
        Long packId = 1L;

        PackUpdateRequest request = new PackUpdateRequest();
        request.setTaxId(1L);
        when(taxDao.getTaxesByEntity(1L)).thenReturn(List.of(2L));

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> service.updatePack(packId, request));
        assertEquals(ex.getErrorCode(), INVALID_ENTITY_TAX.getErrorCode());
    }

    @Test
    void createPackItems_shouldSucceed_whenSessionItemHasValidPriceTypeMapping() {
        Long packId = 1L;
        setupValidPackEnvironment();

        CreatePackItemsDTO request = new CreatePackItemsDTO();
        CreatePackItemDTO sessionItem = createSessionItem(1L);

        Map<Integer, List<Integer>> priceTypeMapping = new HashMap<>();
        priceTypeMapping.put(100, List.of(200));
        sessionItem.setPriceTypeMapping(priceTypeMapping);
        request.add(sessionItem);

        Session session = new Session();
        session.setVenueConfigId(10L);
        session.setSessionId(1L);
        session.setEventId(100L);
        when(catalogService.getSession(eq(1))).thenReturn(session);

        IdNameCodeDTO priceType = new IdNameCodeDTO();
        priceType.setId(200L);
        when(venuesRepository.getPriceTypes(10L)).thenReturn(List.of(priceType));

        Event event = new Event();
        event.setCurrency(1);
        when(catalogService.getEvent(eq(100))).thenReturn(event);

        service.createPackItems(packId, request);

        List<CpanelPackItemRecord> items = packItemsDao.getPackItemRecordsById(packId.intValue());
        assertEquals(8, items.size());
        verify(packPriceTypeMappingDao, times(1)).insert(any());
    }

    @Test
    void createPackItems_shouldIgnoreValidation_whenItemIsNotSession() {
        Long packId = 1L;
        setupValidPackEnvironment();

        CreatePackItemsDTO request = new CreatePackItemsDTO();
        CreatePackItemDTO productItem = new CreatePackItemDTO();
        productItem.setItemId(1L);
        productItem.setType(PackItemType.PRODUCT);
        productItem.setDisplayItemInChannels(true);

        Map<Integer, List<Integer>> priceTypeMapping = new HashMap<>();
        priceTypeMapping.put(100, List.of());
        productItem.setPriceTypeMapping(priceTypeMapping);
        request.add(productItem);

        ProductCatalogDTO product = new ProductCatalogDTO();
        product.setCurrencyId(1);
        when(catalogService.findCatalogProduct(eq(1L))).thenReturn(product);

        service.createPackItems(packId, request);

        List<CpanelPackItemRecord> items = packItemsDao.getPackItemRecordsById(packId.intValue());
        assertEquals(8, items.size());
    }

    @Test
    void createPackItems_shouldSucceed_whenSessionItemHasNoPriceTypeMapping() {
        Long packId = 1L;
        setupValidPackEnvironment();

        CreatePackItemsDTO request = new CreatePackItemsDTO();
        CreatePackItemDTO sessionItem = createSessionItem(1L);
        request.add(sessionItem);

        Session session = new Session();
        session.setSessionId(1L);
        session.setEventId(100L);
        when(catalogService.getSession(eq(1))).thenReturn(session);

        Event event = new Event();
        event.setCurrency(1);
        when(catalogService.getEvent(eq(100))).thenReturn(event);

        service.createPackItems(packId, request);

        List<CpanelPackItemRecord> items = packItemsDao.getPackItemRecordsById(packId.intValue());
        assertEquals(8, items.size());
    }

    @Test
    void createPackItems_shouldFail_whenSessionItemHasEmptyPriceTypeMapping() {
        Long packId = 1L;
        setupValidPackEnvironment();

        CreatePackItemsDTO request = new CreatePackItemsDTO();
        CreatePackItemDTO sessionItem = createSessionItem(1L);

        Map<Integer, List<Integer>> priceTypeMapping = new HashMap<>();
        priceTypeMapping.put(100, List.of());
        sessionItem.setPriceTypeMapping(priceTypeMapping);
        request.add(sessionItem);

        Session session = new Session();
        session.setVenueConfigId(10L);
        session.setSessionId(1L);
        session.setEventId(100L);
        when(catalogService.getSession(eq(1))).thenReturn(session);

        Event event = new Event();
        event.setCurrency(1);
        when(catalogService.getEvent(eq(100))).thenReturn(event);

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> service.createPackItems(packId, request));
        assertEquals(ex.getErrorCode(), PACK_ITEM_PRICE_TYPES_INVALID_MAPPING.getErrorCode());
    }

    @Test
    void createPackItems_shouldFail_whenSessionItemHasMultiplePriceTypesInMapping() {
        Long packId = 1L;
        setupValidPackEnvironment();

        CreatePackItemsDTO request = new CreatePackItemsDTO();
        CreatePackItemDTO sessionItem = createSessionItem(1L);

        Map<Integer, List<Integer>> priceTypeMapping = new HashMap<>();
        priceTypeMapping.put(100, List.of(200, 201));
        sessionItem.setPriceTypeMapping(priceTypeMapping);
        request.add(sessionItem);

        Session session = new Session();
        session.setVenueConfigId(10L);
        session.setSessionId(1L);
        session.setEventId(100L);
        when(catalogService.getSession(eq(1))).thenReturn(session);

        Event event = new Event();
        event.setCurrency(1);
        when(catalogService.getEvent(eq(100))).thenReturn(event);

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> service.createPackItems(packId, request));
        assertEquals(ex.getErrorCode(), PACK_ITEM_PRICE_TYPES_INVALID_MAPPING.getErrorCode());
    }

    @Test
    void createPackItems_shouldFail_whenSessionItemHasInvalidPriceTypeInMapping() {
        Long packId = 1L;
        setupValidPackEnvironment();

        CreatePackItemsDTO request = new CreatePackItemsDTO();
        CreatePackItemDTO sessionItem = createSessionItem(1L);

        Map<Integer, List<Integer>> priceTypeMapping = new HashMap<>();
        priceTypeMapping.put(100, List.of(999));
        sessionItem.setPriceTypeMapping(priceTypeMapping);
        request.add(sessionItem);

        Session session = new Session();
        session.setVenueConfigId(10L);
        session.setSessionId(1L);
        session.setEventId(100L);
        when(catalogService.getSession(eq(1))).thenReturn(session);

        IdNameCodeDTO priceType = new IdNameCodeDTO();
        priceType.setId(200L);
        when(venuesRepository.getPriceTypes(10L)).thenReturn(List.of(priceType));

        Event event = new Event();
        event.setCurrency(1);
        when(catalogService.getEvent(eq(100))).thenReturn(event);

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> service.createPackItems(packId, request));
        assertEquals(ex.getErrorCode(), PACK_ITEM_PRICE_TYPES_INVALID_MAPPING.getErrorCode());
    }

    @Test
    void createPackItems_shouldSucceed_whenMultipleSessionItemsHaveValidPriceTypeMappings() {
        Long packId = 1L;
        setupValidPackEnvironment();

        CreatePackItemsDTO request = new CreatePackItemsDTO();

        CreatePackItemDTO sessionItem1 = createSessionItem(1L);
        Map<Integer, List<Integer>> priceTypeMapping1 = new HashMap<>();
        priceTypeMapping1.put(100, List.of(200));
        sessionItem1.setPriceTypeMapping(priceTypeMapping1);
        request.add(sessionItem1);

        CreatePackItemDTO sessionItem2 = createSessionItem(2L);
        Map<Integer, List<Integer>> priceTypeMapping2 = new HashMap<>();
        priceTypeMapping2.put(101, List.of(201));
        sessionItem2.setPriceTypeMapping(priceTypeMapping2);
        request.add(sessionItem2);

        Session session1 = new Session();
        session1.setVenueConfigId(10L);
        session1.setSessionId(1L);
        session1.setEventId(100L);
        when(catalogService.getSession(eq(1))).thenReturn(session1);

        Session session2 = new Session();
        session2.setVenueConfigId(11L);
        session2.setSessionId(2L);
        session2.setEventId(100L);
        when(catalogService.getSession(eq(2))).thenReturn(session2);

        IdNameCodeDTO priceType1 = new IdNameCodeDTO();
        priceType1.setId(200L);
        when(venuesRepository.getPriceTypes(10L)).thenReturn(List.of(priceType1));

        IdNameCodeDTO priceType2 = new IdNameCodeDTO();
        priceType2.setId(201L);
        when(venuesRepository.getPriceTypes(11L)).thenReturn(List.of(priceType2));

        Event event = new Event();
        event.setCurrency(1);
        when(catalogService.getEvent(eq(100))).thenReturn(event);

        service.createPackItems(packId, request);

        List<CpanelPackItemRecord> items = packItemsDao.getPackItemRecordsById(packId.intValue());
        assertEquals(9, items.size());
        verify(packPriceTypeMappingDao, times(2)).insert(any());
    }

    private void setupValidPackEnvironment() {
        Session mainSession = new Session();
        mainSession.setSessionId(1L);
        mainSession.setEventId(100L);
        when(catalogService.getSession(eq(1))).thenReturn(mainSession);

        Event mainEvent = new Event();
        mainEvent.setCurrency(1);
        when(catalogService.getEvent(eq(100))).thenReturn(mainEvent);
    }

    private CreatePackItemDTO createSessionItem(Long itemId) {
        CreatePackItemDTO item = new CreatePackItemDTO();
        item.setItemId(itemId);
        item.setType(PackItemType.SESSION);
        item.setDisplayItemInChannels(true);
        return item;
    }

    @Test
    void updatePackItemSubsets_WithValidSubItems_ShouldUpdateSuccessfully() {
        Long packId = 1L;
        Long packItemId = 20L;
        
        UpdatePackItemSubitemsRequestDTO request = new UpdatePackItemSubitemsRequestDTO();
        request.setSubitemIds(List.of(100, 101, 102));

        List<SessionRecord> sessionRecords = new ArrayList<>();
        SessionRecord session1 = new SessionRecord();
        session1.setIdsesion(100);
        SessionRecord session2 = new SessionRecord();
        session2.setIdsesion(101);
        SessionRecord session3 = new SessionRecord();
        session3.setIdsesion(102);
        sessionRecords.add(session1);
        sessionRecords.add(session2);
        sessionRecords.add(session3);
        
        when(sessionDao.findSessions(any(), any())).thenReturn(sessionRecords);

        service.updatePackItemSubsets(packId, packItemId, request);
        
        List<CpanelPackItemSubsetRecord> insertedSubsets = packItemSubsetDao.getSubsetsByPackItemId(packItemId.intValue());
        assertEquals(3, insertedSubsets.size());
        assertTrue(insertedSubsets.stream().anyMatch(s -> s.getIdsubitem().equals(100)));
        assertTrue(insertedSubsets.stream().anyMatch(s -> s.getIdsubitem().equals(101)));
        assertTrue(insertedSubsets.stream().anyMatch(s -> s.getIdsubitem().equals(102)));
        
        assertTrue(insertedSubsets.stream().allMatch(s -> List.of(100, 101, 102).contains(s.getIdsubitem())));
    }

    @Test
    void updatePackItemSubsets_WithEmptyList_ShouldDeleteAllSubsets() {
        Long packId = 1L;
        Long packItemId = 20L;
        
        UpdatePackItemSubitemsRequestDTO request = new UpdatePackItemSubitemsRequestDTO();
        request.setSubitemIds(List.of());

        service.updatePackItemSubsets(packId, packItemId, request);
        
        List<CpanelPackItemSubsetRecord> subsets = packItemSubsetDao.getSubsetsByPackItemId(packItemId.intValue());
        assertEquals(0, subsets.size());
    }

    @Test
    void updatePackItemSubsets_WithNonExistentPack_ShouldThrowException() {
        Long packId = 999L;
        Long packItemId = 20L;
        
        UpdatePackItemSubitemsRequestDTO request = new UpdatePackItemSubitemsRequestDTO();
        request.setSubitemIds(List.of(100));

        OneboxRestException exception = assertThrows(OneboxRestException.class, () -> {
            service.updatePackItemSubsets(packId, packItemId, request);
        });

        assertEquals(PACK_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void updatePackItemSubsets_WithNonExistentPackItem_ShouldThrowException() {
        Long packId = 1L;
        Long packItemId = 999L;
        
        UpdatePackItemSubitemsRequestDTO request = new UpdatePackItemSubitemsRequestDTO();
        request.setSubitemIds(List.of(100));

        OneboxRestException exception = assertThrows(OneboxRestException.class, () -> {
            service.updatePackItemSubsets(packId, packItemId, request);
        });

        assertEquals(PACK_ITEM_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void updatePackItemSubsets_WithNonEventPackItem_ShouldThrowException() {
        Long packId = 1L;
        Long packItemId = 200L;
        
        UpdatePackItemSubitemsRequestDTO request = new UpdatePackItemSubitemsRequestDTO();
        request.setSubitemIds(List.of(100));

        OneboxRestException exception = assertThrows(OneboxRestException.class, () -> {
            service.updatePackItemSubsets(packId, packItemId, request);
        });

        assertEquals(PACK_ITEM_INVALID_FOR_SUBSETS.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void updatePackItemSubsets_WithInvalidSubItemIds_ShouldThrowException() {
        Long packId = 1L;
        Long packItemId = 20L;
        
        UpdatePackItemSubitemsRequestDTO request = new UpdatePackItemSubitemsRequestDTO();
        request.setSubitemIds(List.of(100, 999));

        List<SessionRecord> sessionRecords = new ArrayList<>();
        SessionRecord session1 = new SessionRecord();
        session1.setIdsesion(100);
        sessionRecords.add(session1);
        
        when(sessionDao.findSessions(any(), any())).thenReturn(sessionRecords);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () -> {
            service.updatePackItemSubsets(packId, packItemId, request);
        });

        assertEquals(PACK_ITEM_SUBSETS_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

}
