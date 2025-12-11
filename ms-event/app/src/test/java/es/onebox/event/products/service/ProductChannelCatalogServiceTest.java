package es.onebox.event.products.service;

import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.utils.ObjectRandomizer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductChannelDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.domain.ProductChannelRecord;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.domain.ProductSessionRecord;
import es.onebox.event.products.dto.CreateProductChannelDTO;
import es.onebox.event.products.dto.ProductChannelSessionDTO;
import es.onebox.event.products.dto.ProductChannelSessionsFilter;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionsDTO;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.sessions.service.SessionService;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductChannelRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.utils.ObjectRandomizer;

class ProductEnabledChannelServiceTest {

    @InjectMocks
    ProductService productService;
    @Mock
    ProductChannelDao productChannelDao;
    @Mock
    ProductDao productDao;
    @Mock
    ChannelDao channelDao;
    @Mock
    ProductSessionDao productSessionDao;
    @Mock
    ProductEventDao productEventDao;
    @Mock
    SessionDao sessionDao;
    @Mock
    SessionService sessionService;
    @Mock
    RefreshDataService refreshDataService;
    @Mock
    WebhookService webhookService;

    @InjectMocks
    ProductChannelService productChannelService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(productChannelService, "productService", productService);
    }

    @Test
    void getProductChannels() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productChannelService.getProductChannels(ObjectRandomizer.randomLong());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("dfsdf");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        Mockito.when(productChannelDao.findByProductId(Mockito.anyLong())).thenReturn(null);
        try {
            productChannelService.getProductChannels(ObjectRandomizer.randomLong());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_CHANNELS_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        List<ProductChannelRecord> cpanelProductChannelRecords = new ArrayList<>();
        ProductChannelRecord productChannelRecord = new ProductChannelRecord();
        productChannelRecord.setChannelName("sdgfsdf");
        productChannelRecord.setProductName("dfgdsgfdsfg");
        productChannelRecord.setProductSaleRequestsStatusId(1);
        productChannelRecord.setProductid(2);
        productChannelRecord.setChannelid(3);
        cpanelProductChannelRecords.add(productChannelRecord);
        Mockito.when(productChannelDao.findByProductId(Mockito.anyLong())).thenReturn(cpanelProductChannelRecords);
        productChannelService.getProductChannels(ObjectRandomizer.randomLong());
    }

    @Test
    void createProductChannels() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productChannelService.createProductChannels(ObjectRandomizer.randomLong(),
                    ObjectRandomizer.random(CreateProductChannelDTO.class));
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("dfsdf");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        CpanelProductChannelRecord cpanelProductChannelRecord = new CpanelProductChannelRecord();
        cpanelProductChannelRecord.setChannelid(1);
        cpanelProductChannelRecord.setProductid(2);
        Mockito.when(productChannelDao.insert(Mockito.any())).thenReturn(cpanelProductChannelRecord);

        Mockito.when(productChannelDao.findByProductId(Mockito.anyLong())).thenReturn(null);

        CreateProductChannelDTO createProductChannelDTO = new CreateProductChannelDTO();
        createProductChannelDTO.setChannelIds(new ArrayList<>());
        createProductChannelDTO.getChannelIds().add(2);

        try {
            productChannelService.createProductChannels(ObjectRandomizer.randomLong(), createProductChannelDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.CHANNEL_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        List<ProductChannelRecord> productChannelRecords = new ArrayList<>();
        ProductChannelRecord productChannelRecord = new ProductChannelRecord();
        productChannelRecord.setProductName("sdfsdfsd");
        productChannelRecord.setChannelName("dfgdsgfdasfg");
        productChannelRecord.setProductid(2);
        productChannelRecord.setChannelid(3);
        productChannelRecords.add(productChannelRecord);

        Mockito.when(productChannelDao.findByProductId(Mockito.anyLong())).thenReturn(productChannelRecords);

        List<ChannelInfo> channelRecords = new ArrayList<>();
        ChannelInfo channelInfo = new ChannelInfo(3L, "name", 2L, 0, null);
        channelRecords.add(channelInfo);

        Mockito.when(channelDao.getByIdsNotDeleted(Mockito.anyList())).thenReturn(channelRecords);

        productChannelService.createProductChannels(ObjectRandomizer.randomLong(), createProductChannelDTO);
    }

    @Test
    void deleteProductChannelProductNotFound() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productChannelService.deleteProductChannel(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void deleteProductChannelChannelNotFound() {
        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setState(1);

        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);
        Mockito.when(channelDao.getByIdsNotDeleted(Mockito.anyList())).thenReturn(null);
        try {
            productChannelService.deleteProductChannel(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.CHANNEL_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void deleteProductChannelNotFound() {
        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setState(1);

        ProductChannelRecord productChannelRecord = new ProductChannelRecord();
        productChannelRecord.setProductid(2);
        productChannelRecord.setChannelid(3);

        CpanelCanalRecord channelRecord = new CpanelCanalRecord();
        channelRecord.setIdcanal(1);
        channelRecord.setEstado(1);

        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);
        Mockito.when(channelDao.getById(Mockito.anyInt())).thenReturn(channelRecord);
        Mockito.when(productChannelDao.findByProductIdAndChannelId(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(productChannelRecord);

        try {
            productChannelService.deleteProductChannel(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_CHANNEL_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void getProductChannelSessionsProductNotFound() {
        Long productId = 1L;
        Long channelId = 1L;
        ProductChannelSessionsFilter filter = new ProductChannelSessionsFilter();

        when(productDao.findById(productId.intValue())).thenReturn(null);

        try {
            productChannelService.getProductChannelSessions(productId, channelId, filter);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void getProductChannelSessionsProductChannelNotFound() {
        Long productId = 1L;
        Long channelId = 1L;
        ProductChannelSessionsFilter filter = new ProductChannelSessionsFilter();

        CpanelProductRecord productRecord = createProductRecord(productId.intValue());
        when(productDao.findById(productId.intValue())).thenReturn(productRecord);
        when(productChannelDao.findByProductIdAndChannelId(productId, channelId)).thenReturn(null);

        try {
            productChannelService.getProductChannelSessions(productId, channelId, filter);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_CHANNEL_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void getProductChannelSessionsStandaloneDisabled() {
        Long productId = 1L;
        Long channelId = 1L;
        ProductChannelSessionsFilter filter = new ProductChannelSessionsFilter();

        CpanelProductRecord productRecord = createProductRecord(productId.intValue());
        ProductChannelRecord productChannelRecord = createProductChannelRecord(productId, channelId, (byte) 0);

        when(productDao.findById(productId.intValue())).thenReturn(productRecord);
        when(productChannelDao.findByProductIdAndChannelId(productId, channelId)).thenReturn(productChannelRecord);

        ListWithMetadata<ProductChannelSessionDTO> result = productChannelService.getProductChannelSessions(productId,
                channelId, filter);

        assertNotNull(result);
        assertTrue(result.getData() == null || result.getData().isEmpty());
    }

    @Test
    void getProductChannelSessionsAllEvents() {
        Long productId = 1L;
        Long channelId = 1L;
        ProductChannelSessionsFilter filter = new ProductChannelSessionsFilter();

        CpanelProductRecord productRecord = createProductRecord(productId.intValue());
        ProductChannelRecord productChannelRecord = createProductChannelRecord(productId, channelId, (byte) 1);

        List<ProductEventRecord> productEvents = Arrays.asList(
                createProductEventRecord(1, SelectionType.ALL.getId()),
                createProductEventRecord(2, SelectionType.ALL.getId()));

        // Mock SessionDao para cada evento ALL
        when(sessionDao.findSessionsByEventId(1))
                .thenReturn(createMockSessionRecords(Arrays.asList(101L, 102L)));
        when(sessionDao.findSessionsByEventId(2))
                .thenReturn(createMockSessionRecords(Arrays.asList(201L, 202L, 203L)));

        // Mock para la consulta final
        when(sessionService.searchSessions(argThat(f -> f != null && f.getIds() != null && f.getIds().size() == 5)))
                .thenReturn(new SessionsDTO());

        when(productDao.findById(productId.intValue())).thenReturn(productRecord);
        when(productChannelDao.findByProductIdAndChannelId(productId, channelId)).thenReturn(productChannelRecord);
        when(productEventDao.findByProductId(productId.intValue(), false)).thenReturn(productEvents);

        ListWithMetadata<ProductChannelSessionDTO> result = productChannelService.getProductChannelSessions(productId,
                channelId, filter);

        assertNotNull(result);

        verify(sessionService).searchSessions(argThat(f -> f != null && f.getIds() != null && f.getIds().size() == 5 &&
                f.getIds().containsAll(Arrays.asList(101L, 102L, 201L, 202L, 203L))));
    }

    @Test
    void getProductChannelSessionsRestrictedEvents() {
        Long productId = 1L;
        Long channelId = 1L;
        ProductChannelSessionsFilter filter = new ProductChannelSessionsFilter();

        CpanelProductRecord productRecord = createProductRecord(productId.intValue());
        ProductChannelRecord productChannelRecord = createProductChannelRecord(productId, channelId, (byte) 1);

        List<ProductEventRecord> productEvents = Arrays.asList(
                createProductEventRecord(1, SelectionType.RESTRICTED.getId()));

        List<ProductSessionRecord> productSessions = Arrays.asList(
                createProductSessionRecord(101),
                createProductSessionRecord(102));

        SessionsDTO sessionsDto = new SessionsDTO();

        when(productDao.findById(productId.intValue())).thenReturn(productRecord);
        when(productChannelDao.findByProductIdAndChannelId(productId, channelId)).thenReturn(productChannelRecord);
        when(productEventDao.findByProductId(productId.intValue(), false)).thenReturn(productEvents);
        when(productSessionDao.findProductSessionsByProductId(productId.intValue(), 1)).thenReturn(productSessions);
        when(sessionService.searchSessions(any(SessionSearchFilter.class))).thenReturn(sessionsDto);

        ListWithMetadata<ProductChannelSessionDTO> result = productChannelService.getProductChannelSessions(productId,
                channelId, filter);

        assertNotNull(result);

        verify(sessionService).searchSessions(argThat(f -> f != null && f.getIds() != null && f.getIds().size() == 2 &&
                f.getIds().contains(101L) && f.getIds().contains(102L) &&
                (f.getEventId() == null || f.getEventId().isEmpty())));
    }

    @Test
    void getProductChannelSessionsMultipleRestrictedEvents() {
        Long productId = 1L;
        Long channelId = 1L;
        ProductChannelSessionsFilter filter = new ProductChannelSessionsFilter();

        CpanelProductRecord productRecord = createProductRecord(productId.intValue());
        ProductChannelRecord productChannelRecord = createProductChannelRecord(productId, channelId, (byte) 1);

        List<ProductEventRecord> productEvents = Arrays.asList(
                createProductEventRecord(1, SelectionType.RESTRICTED.getId()),
                createProductEventRecord(2, SelectionType.RESTRICTED.getId()));

        List<ProductSessionRecord> productSessionsEvent1 = Arrays.asList(
                createProductSessionRecord(101),
                createProductSessionRecord(102));

        List<ProductSessionRecord> productSessionsEvent2 = Arrays.asList(
                createProductSessionRecord(201),
                createProductSessionRecord(202),
                createProductSessionRecord(203));

        SessionsDTO sessionsDto = new SessionsDTO();

        when(productDao.findById(productId.intValue())).thenReturn(productRecord);
        when(productChannelDao.findByProductIdAndChannelId(productId, channelId)).thenReturn(productChannelRecord);
        when(productEventDao.findByProductId(productId.intValue(), false)).thenReturn(productEvents);
        when(productSessionDao.findProductSessionsByProductId(productId.intValue(), 1))
                .thenReturn(productSessionsEvent1);
        when(productSessionDao.findProductSessionsByProductId(productId.intValue(), 2))
                .thenReturn(productSessionsEvent2);
        when(sessionService.searchSessions(any(SessionSearchFilter.class))).thenReturn(sessionsDto);

        ListWithMetadata<ProductChannelSessionDTO> result = productChannelService.getProductChannelSessions(productId,
                channelId, filter);

        assertNotNull(result);

        verify(sessionService).searchSessions(argThat(f -> f != null && f.getIds() != null && f.getIds().size() == 5 &&
                f.getIds().containsAll(Arrays.asList(101L, 102L, 201L, 202L, 203L)) &&
                (f.getEventId() == null || f.getEventId().isEmpty())));
    }

    @Test
    void getProductChannelSessionsMixedEvents() {
        Long productId = 1L;
        Long channelId = 1L;
        ProductChannelSessionsFilter filter = new ProductChannelSessionsFilter();

        CpanelProductRecord productRecord = createProductRecord(productId.intValue());
        ProductChannelRecord productChannelRecord = createProductChannelRecord(productId, channelId, (byte) 1);

        List<ProductEventRecord> productEvents = Arrays.asList(
                createProductEventRecord(1, SelectionType.ALL.getId()),
                createProductEventRecord(2, SelectionType.RESTRICTED.getId()),
                createProductEventRecord(3, SelectionType.ALL.getId()));

        List<ProductSessionRecord> productSessionsEvent2 = Arrays.asList(
                createProductSessionRecord(201),
                createProductSessionRecord(202));

        // Mock SessionDao para eventos ALL
        when(sessionDao.findSessionsByEventId(1))
                .thenReturn(createMockSessionRecords(Arrays.asList(101L, 102L)));
        when(sessionDao.findSessionsByEventId(3))
                .thenReturn(createMockSessionRecords(Arrays.asList(301L, 302L, 303L)));

        // Mock para la consulta final
        when(sessionService.searchSessions(argThat(f -> f != null && f.getIds() != null && f.getIds().size() == 7)))
                .thenReturn(new SessionsDTO());

        when(productDao.findById(productId.intValue())).thenReturn(productRecord);
        when(productChannelDao.findByProductIdAndChannelId(productId, channelId)).thenReturn(productChannelRecord);
        when(productEventDao.findByProductId(productId.intValue(), false)).thenReturn(productEvents);
        when(productSessionDao.findProductSessionsByProductId(productId.intValue(), 2))
                .thenReturn(productSessionsEvent2);

        ListWithMetadata<ProductChannelSessionDTO> result = productChannelService.getProductChannelSessions(productId,
                channelId, filter);

        assertNotNull(result);

        verify(sessionService).searchSessions(argThat(f -> f != null && f.getIds() != null && f.getIds().size() == 7 &&
                f.getIds().containsAll(Arrays.asList(101L, 102L, 201L, 202L, 301L, 302L, 303L)) &&
                (f.getEventId() == null || f.getEventId().isEmpty())));
    }

    private CpanelProductRecord createProductRecord(int productId) {
        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(productId);
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("Test Product");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        return cpanelProductRecord;
    }

    private ProductChannelRecord createProductChannelRecord(Long productId, Long channelId, byte standaloneEnabled) {
        ProductChannelRecord productChannelRecord = new ProductChannelRecord();
        productChannelRecord.setProductid(productId.intValue());
        productChannelRecord.setChannelid(channelId.intValue());
        productChannelRecord.setStandaloneenabled(standaloneEnabled);
        productChannelRecord.setCheckoutsuggestionenabled((byte) 1);
        return productChannelRecord;
    }

    private ProductEventRecord createProductEventRecord(int eventId, int selectionType) {
        ProductEventRecord productEventRecord = new ProductEventRecord();
        productEventRecord.setEventid(eventId);
        productEventRecord.setSessionsselectiontype(selectionType);
        productEventRecord.setEventName("Test Event " + eventId);
        return productEventRecord;
    }

    private ProductSessionRecord createProductSessionRecord(int sessionId) {
        ProductSessionRecord productSessionRecord = new ProductSessionRecord();
        productSessionRecord.setSessionid(sessionId);
        return productSessionRecord;
    }

    private SessionsDTO createMockSessionsDTO(List<Long> sessionIds) {
        SessionsDTO sessionsDto = new SessionsDTO();
        List<SessionDTO> sessions = new ArrayList<>();

        for (Long sessionId : sessionIds) {
            SessionDTO session = new SessionDTO();
            session.setId(sessionId);
            sessions.add(session);
        }

        sessionsDto.setData(sessions);
        return sessionsDto;
    }

    private List<SessionRecord> createMockSessionRecords(List<Long> sessionIds) {
        return sessionIds.stream()
                .map(id -> {
                    SessionRecord session = new SessionRecord();
                    session.setIdsesion(id.intValue());
                    session.setEstado(1); // Estado activo
                    return session;
                })
                .toList();
    }
}
