package es.onebox.event.events.service;

import com.oneboxtds.datasource.s3.S3Repository;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.attendants.AttendantsConfigService;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.datasources.ms.channel.dto.ChannelConfigDTO;
import es.onebox.event.datasources.ms.channel.repository.ChannelsRepository;
import es.onebox.event.datasources.ms.entity.MsEntityDatasource;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.events.amqp.emailnotification.EmailNotificationService;
import es.onebox.event.events.dao.ChannelCurrenciesDao;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.ChannelEventSurchargeRangeDao;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.SaleGroupDao;
import es.onebox.event.events.dao.SalesGroupAssignmentDao;
import es.onebox.event.events.dto.EventChannelDTO;
import es.onebox.event.events.dto.UpdateEventChannelDTO;
import es.onebox.event.events.dto.UpdateEventChannelSettingsDTO;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.EventChannelStatus;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.postbookingquestions.service.PostBookingQuestionsService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.event.secondarymarket.dao.EventSecondaryMarketConfigCouchDao;
import es.onebox.event.secondarymarket.domain.EventSecondaryMarketConfig;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCuposConfigRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static es.onebox.utils.ObjectRandomizer.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventChannelServiceTest {

    @InjectMocks
    private EventChannelService eventChannelService;
    @Mock
    private EventChannelEraserService eventChannelEraserService;
    @Mock
    private ChannelEventEraserService channelEventEraserService;
    @Mock
    private AttendantsConfigService attendantsConfigService;
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private ChannelEventElasticDao channelEventElasticDao;
    @Mock
    private ChannelSessionElasticDao channelSessionElasticDao;
    @Mock
    private ChannelEventDao channelEventDao;
    @Mock
    private SessionDao sessionDao;
    @Mock
    private EventDao eventDao;
    @Mock
    private ChannelDao channelDao;
    @Mock
    private ChannelCurrenciesDao channelCurrenciesDao;
    @Mock
    private EmailNotificationService emailNotificationService;
    @Mock
    private DefaultProducer externalEventConsumeNotificationProducer;
    @Mock
    private MsEntityDatasource msEntityDatasource;
    @Mock
    private SalesGroupAssignmentDao salesGroupAssignmentDao;
    @Mock
    private S3Repository s3Repository;
    @Mock
    private SaleGroupDao saleGroupDao;
    @Mock
    private EntityDao entityDao;
    @Mock
    private EventSecondaryMarketConfigCouchDao eventSecondaryMarketConfigCouchDao;
    @Mock
    private ChannelEventSurchargeRangeDao channelEventSurchargeRangeDao;
    @Mock
    private SessionValidationHelper sessionValidationHelper;
    @Mock
    private ChannelsRepository channelsRepository;
    @Mock
    private SessionConfigCouchDao sessionConfigCouchDao;
    @Mock
    private PostBookingQuestionsService postBookingQuestionsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createEventChannel_ok() {
        CpanelCanalRecord channel = new CpanelCanalRecord();
        channel.setIdcanal(1);
        channel.setIdentidad(1);
        channel.setIdsubtipocanal(1);

        List<Long> currencies = new ArrayList<>();
        currencies.add(1L);
        CpanelEventoRecord cpanelEvent = new CpanelEventoRecord();

        CpanelEntidadRecord cpanelEntidadRecord = new CpanelEntidadRecord();
        cpanelEntidadRecord.setUsemulticurrency((byte) 0);


        when(entityDao.getById(any())).thenReturn(cpanelEntidadRecord);
        when(entityDao.getEntityInfo(any())).thenReturn(random(EntityDao.EntityInfo.class));
        when(eventDao.getById(anyInt())).thenReturn(cpanelEvent);
        when(channelCurrenciesDao.getCurrenciesByChannelId(anyLong())).thenReturn(currencies);
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.empty());
        when(channelEventDao.insert(any())).thenReturn(new CpanelCanalEventoRecord());
        when(channelDao.getById(anyInt())).thenReturn(channel);
        when(eventDao.findById(anyInt())).thenReturn(new CpanelEventoRecord());

        eventChannelService.createEventChannel(1L, 1L);
    }

    @Test
    public void createEventChannel_nullIds() {
        try {
            eventChannelService.createEventChannel(null, 1L);
            fail("Exception should be thrown on null event id");
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(((OneboxRestException) e).getErrorCode(), MsEventErrorCode.EVENT_ID_MANDATORY.getErrorCode());
        }
        try {
            eventChannelService.createEventChannel(1L, null);
            fail("Exception should be thrown on null channel id");
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(((OneboxRestException) e).getErrorCode(), MsEventErrorCode.CHANNEL_ID_MANDATORY.getErrorCode());
        }
    }

    @Test
    public void createEventChannel_existingRelation() {
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.of(new CpanelCanalEventoRecord()));
        try {
            eventChannelService.createEventChannel(1L, 1L);
            fail("Exception should be thrown on existing channel-event relation");
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(((OneboxRestException) e).getErrorCode(), MsEventErrorCode.EVENT_CHANNEL_EXISTS.getErrorCode());
        }
    }

    @Test
    public void createEventChannel_eventNotFound() {
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.empty());
        when(eventDao.findById(anyInt())).thenReturn(null);
        try {
            eventChannelService.createEventChannel(1L, 1L);
            fail("Exception should be thrown on event not found");
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(((OneboxRestException) e).getErrorCode(), MsEventErrorCode.EVENT_NOT_FOUND.getErrorCode());
        }
    }

    @Test
    public void createEventChannel_channelNotFound() {
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.empty());
        when(eventDao.findById(anyInt())).thenReturn(new CpanelEventoRecord());
        when(channelDao.getById(anyInt())).thenReturn(null);
        try {
            eventChannelService.createEventChannel(1L, 1L);
            fail("Exception should be thrown on channel not found");
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(((OneboxRestException) e).getErrorCode(), MsEventErrorCode.CHANNEL_NOT_FOUND.getErrorCode());
        }
    }

    @Test
    public void createEventChannel_addChannelToAttendantsCalled_whenTypeIsPortal() {
        CpanelCanalRecord channel = new CpanelCanalRecord();
        channel.setIdcanal(1);
        channel.setIdentidad(1);
        channel.setIdsubtipocanal(7);
        List<Long> currencies = new ArrayList<>();
        currencies.add(1L);
        CpanelEntidadRecord entity = new CpanelEntidadRecord();
        entity.setUsemulticurrency((byte) 0);

        when(entityDao.getById(any())).thenReturn(entity);
        when(entityDao.getEntityInfo(any())).thenReturn(random(EntityDao.EntityInfo.class));
        when(channelCurrenciesDao.getCurrenciesByChannelId(anyLong())).thenReturn(currencies);
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.empty());
        when(channelEventDao.insert(any())).thenReturn(new CpanelCanalEventoRecord());
        when(channelDao.getById(anyInt())).thenReturn(channel);
        when(eventDao.findById(anyInt())).thenReturn(new CpanelEventoRecord());

        eventChannelService.createEventChannel(1L, 1L);

        verify(attendantsConfigService).addChannelToAttendantsConfig(anyLong(), anyLong());
    }

    @Test
    public void createEventChannel_currencyNotMatch() {
        CpanelCanalRecord channel = new CpanelCanalRecord();
        channel.setIdcanal(1);
        channel.setIdentidad(1);
        channel.setIdsubtipocanal(7);
        channel.setCurrency(1);
        CpanelEntidadRecord entity = new CpanelEntidadRecord();
        entity.setUsemulticurrency((byte) 1);

        when(entityDao.getById(any())).thenReturn(entity);
        when(entityDao.getEntityInfo(any())).thenReturn(random(EntityDao.EntityInfo.class));
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.empty());
        when(eventDao.findById(anyInt())).thenReturn(new CpanelEventoRecord());
        when(channelDao.getById(anyInt())).thenReturn(null);
        when(channelDao.getById(anyInt())).thenReturn(channel);
        try {
            eventChannelService.createEventChannel(1L, 1L);
            fail("Event currency not match with channel currencies");
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(((OneboxRestException) e).getErrorCode(), MsEventErrorCode.EVENT_CHANNEL_CURRENCY_NOT_MATCH.getErrorCode());
        }
    }

    @Test
    public void requestChannelApproval_ok() throws Exception {
        CpanelCanalEventoRecord record = new CpanelCanalEventoRecord();
        record.setEstadorelacion(3);
        record.setIdcanaleevento(1);
        List<Long> currencies = new ArrayList<>();
        currencies.add(1L);
        CpanelEventoRecord evento = new CpanelEventoRecord();
        evento.setIdcurrency(1);
        CpanelEntidadRecord entity = new CpanelEntidadRecord();
        entity.setUsemulticurrency((byte) 1);
        CpanelCanalRecord canal = new CpanelCanalRecord();
        canal.setCurrency(1);

        when(entityDao.getById(any())).thenReturn(entity);
        when(channelDao.getById(any())).thenReturn(canal);
        when(entityDao.getEntityInfo(any())).thenReturn(random(EntityDao.EntityInfo.class));
        when(eventDao.getById(anyInt())).thenReturn(evento);
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.of(record));
        when(channelCurrenciesDao.getCurrenciesByChannelId(anyLong())).thenReturn(currencies);
        eventChannelService.requestChannelApproval(1L, 1L, 1L);
        verify(channelEventDao).getChannelEvent(anyInt(), anyInt());
        verify(channelEventDao).update(any());
        verify(emailNotificationService).sendEmailNotification(any(), anyInt(), anyInt(), any());
        verify(externalEventConsumeNotificationProducer).sendMessage(any());
    }

    @Test
    public void requestChannelApproval_invalidIds() {
        try {
            eventChannelService.requestChannelApproval(null, 1L, 1L);
            fail("Exception should be thrown on null event id");
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(((OneboxRestException) e).getErrorCode(), CoreErrorCode.BAD_PARAMETER.getErrorCode());
        }
        try {
            eventChannelService.requestChannelApproval(1L, null, 1L);
            fail("Exception should be thrown on null channel id");
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(((OneboxRestException) e).getErrorCode(), CoreErrorCode.BAD_PARAMETER.getErrorCode());
        }
        try {
            eventChannelService.requestChannelApproval(1L, 1L, null);
            fail("Exception should be thrown on null user id");
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(((OneboxRestException) e).getErrorCode(), CoreErrorCode.BAD_PARAMETER.getErrorCode());
        }
    }

    @Test
    public void requestChannelApproval_channelEventNotFound() {
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.empty());
        try {
            eventChannelService.requestChannelApproval(1L, 1L, 1L);
            fail("Exception should be thrown when channel event not found");
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(((OneboxRestException) e).getErrorCode(), MsEventErrorCode.EVENT_CHANNEL_NOT_FOUND.getErrorCode());
        }
    }

    @Test
    public void requestChannelApproval_statusIsNotPendingRequest() throws Exception {
        CpanelCanalEventoRecord record = new CpanelCanalEventoRecord();
        record.setEstadorelacion(1);
        record.setIdcanaleevento(1);
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.of(record));
        try {
            eventChannelService.requestChannelApproval(1L, 1L, 1L);
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(((OneboxRestException) e).getErrorCode(), MsEventErrorCode.REQUEST_NOT_PENDING.getErrorCode());
        }
        verify(channelEventDao, times(0)).update(any());
        verify(emailNotificationService, times(0)).sendEmailNotification(any(), anyInt(), anyInt(), any());
        verify(externalEventConsumeNotificationProducer, times(0)).sendMessage(any());
    }

    @Test
    public void requestChannelApproval_ko_currencies() {
        CpanelCanalEventoRecord record = new CpanelCanalEventoRecord();
        record.setEstadorelacion(3);
        record.setIdcanaleevento(1);
        List<Long> currencies = new ArrayList<>();
        CpanelEntidadRecord entity = new CpanelEntidadRecord();
        entity.setUsemulticurrency((byte) 1);
        CpanelEventoRecord evento = new CpanelEventoRecord();
        evento.setIdcurrency(2);
        CpanelCanalRecord channel = new CpanelCanalRecord();
        channel.setCurrency(2);

        when(entityDao.getById(any())).thenReturn(entity);
        when(entityDao.getEntityInfo(any())).thenReturn(random(EntityDao.EntityInfo.class));
        when(eventDao.getById(anyInt())).thenReturn(evento);
        when(channelDao.getById(any())).thenReturn(channel);
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.of(record));
        when(channelCurrenciesDao.getCurrenciesByChannelId(anyLong())).thenReturn(currencies);
        try {
            eventChannelService.requestChannelApproval(1L, 1L, 1L);
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(MsEventErrorCode.EVENT_CHANNEL_CURRENCY_NOT_MATCH.getErrorCode(), ((OneboxRestException) e).getErrorCode());
        }
    }

    @Test
    public void getEventChannel_ok() {
        EventChannelRecord record = random(EventChannelRecord.class);
        record.setEventStatus(EventStatus.PLANNED.getId());
        record.setRequestStatus(EventChannelStatus.ACCEPTED.getId());
        record.setChannelType(ChannelSubtype.PORTAL_WEB.getIdSubtipo());
        ReflectionTestUtils.setField(eventChannelService, "s3domain", "http://domain.com");
        ReflectionTestUtils.setField(eventChannelService, "fileBasePath", "/path/");
        CpanelEntidadRecord entity = new CpanelEntidadRecord();
        entity.setAllowsecmkt((byte) 1);
        ChannelConfigDTO channelConfig = new ChannelConfigDTO();
        channelConfig.setId(record.getChannelId());
        channelConfig.setChannelType(ChannelSubtype.PORTAL_WEB.getIdSubtipo());
        channelConfig.setV4Enabled(false);
        channelConfig.setV4ConfigEnabled(true);

        when(eventSecondaryMarketConfigCouchDao.get(anyString())).thenReturn(new EventSecondaryMarketConfig(new ArrayList<>()));
        when(channelEventDao.getChannelEventDetailed(anyInt(), anyInt())).thenReturn(record);
        when(entityDao.getEntityByChannelId(anyLong())).thenReturn(entity);
        when(msEntityDatasource.getEntity(anyInt())).thenReturn(new EntityDTO());
        when(salesGroupAssignmentDao.getChannelEventQuotaIds(anyInt())).thenReturn(Collections.emptyList());
        when(channelsRepository.getChannelConfigCached(anyLong())).thenReturn(channelConfig);

        EventChannelDTO eventChannel = eventChannelService.getEventChannel(1L, 1L);
        assertNotNull(eventChannel);
    }

    @Test
    public void getEventChannel_eventChannelNotFound() {
        when(channelEventDao.getChannelEventDetailed(anyInt(), anyInt())).thenReturn(null);
        try {
            eventChannelService.getEventChannel(1L, 1L);
        } catch (Exception e) {
            assertTrue(e instanceof OneboxRestException);
            assertEquals(MsEventErrorCode.EVENT_CHANNEL_NOT_FOUND.getErrorCode(), ((OneboxRestException) e).getErrorCode());
        }
    }

    @Test
    public void updateEventChannel_ok() {
        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdcanaleevento(1);
        channelEvent.setIdcanal(10);
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setIdevento(1);
        event.setPermitereservas((byte) 1);
        event.setTipoevento(1);
        CpanelCuposConfigRecord record = new CpanelCuposConfigRecord();
        record.setIdcupo(1);
        CpanelEntidadRecord cpanelEntidadRecord = new CpanelEntidadRecord();
        cpanelEntidadRecord.setAllowsecmkt((byte) 1);

        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.of(channelEvent));
        when(eventDao.getById(anyInt())).thenReturn(event);
        when(saleGroupDao.getByEventId(anyLong())).thenReturn(Collections.singletonList(record));
        when(entityDao.getEntityByChannelId(anyLong())).thenReturn(cpanelEntidadRecord);

        eventChannelService.updateEventChannel(1L, 1L, buildUpdateData());

        verify(channelEventDao).getChannelEvent(anyInt(), anyInt());
        verify(channelEventDao).update(any());
        verify(salesGroupAssignmentDao).deleteByChannelEventId(anyInt());
        verify(salesGroupAssignmentDao).bulkInsertByChannelEvent(anyList(), anyInt());
    }

    @Test
    public void updateEventChannel_nullParams() {
        try {
            eventChannelService.updateEventChannel(null, 1L, new UpdateEventChannelDTO());
            fail("Exception should be thrown on null event id");
        } catch (OneboxRestException e) {
            assertEquals(e.getErrorCode(), MsEventErrorCode.EVENT_ID_MANDATORY.getErrorCode());
        }
        try {
            eventChannelService.updateEventChannel(1L, null, new UpdateEventChannelDTO());
            fail("Exception should be thrown on null channel id");
        } catch (OneboxRestException e) {
            assertEquals(e.getErrorCode(), MsEventErrorCode.CHANNEL_ID_MANDATORY.getErrorCode());
        }
    }

    @Test
    public void updateEventChannel_validationsTest() {
        CpanelCuposConfigRecord record = new CpanelCuposConfigRecord();
        record.setIdcupo(1);
        CpanelEntidadRecord cpanelEntidadRecord = new CpanelEntidadRecord();
        cpanelEntidadRecord.setAllowsecmkt((byte) 1);
        when(saleGroupDao.getByEventId(anyLong())).thenReturn(Collections.singletonList(record));
        UpdateEventChannelDTO updateData = new UpdateEventChannelDTO();
        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdcanaleevento(1);
        channelEvent.setIdcanal(10);
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.of(channelEvent));

        CpanelEventoRecord cpanelEventoRecord = new CpanelEventoRecord();
        cpanelEventoRecord.setPermitereservas((byte) 1);
        when(eventDao.getById(anyInt())).thenReturn(cpanelEventoRecord);
        try {
            updateData.setSettings(new UpdateEventChannelSettingsDTO());
            updateData.getSettings().setSecondaryMarketEnabled(true);
            updateData.getSettings().setUseEventDates(true);
            when(entityDao.getEntityByChannelId(anyLong())).thenReturn(null);
            eventChannelService.updateEventChannel(1L, 1L, updateData);
            fail("Exception should be thrown when channel is invalid");
        } catch (OneboxRestException e) {
            assertEquals(e.getErrorCode(), MsEventErrorCode.CHANNEL_NOT_FOUND.getErrorCode());
        }
        try {
            cpanelEntidadRecord.setAllowsecmkt((byte) 0);
            when(entityDao.getEntityByChannelId(anyLong())).thenReturn(cpanelEntidadRecord);
            eventChannelService.updateEventChannel(1L, 1L, updateData);
            fail("Exception should be thrown when secondary market not supported");
        } catch (OneboxRestException e) {
            assertEquals(e.getErrorCode(), MsEventErrorCode.SECONDARY_MARKET_NOT_ALLOWED_BY_ENTITY.getErrorCode());
        }
        try {
            updateData.getSettings().setUseEventDates(false);
            updateData.getSettings().setSecondaryMarketEnabled(false);
            updateData.getSettings().setSaleStartDate(ZonedDateTime.now());
            updateData.getSettings().setSaleEndDate(ZonedDateTime.now().minusDays(1L));
            updateData.getSettings().setReleaseDate(ZonedDateTime.now());
            updateData.getSettings().setBookingStartDate(ZonedDateTime.now());
            updateData.getSettings().setBookingEndDate(ZonedDateTime.now().plusDays(1L));
            eventChannelService.updateEventChannel(1L, 1L, updateData);
            fail("Exception should be thrown on start date greater than end date");
        } catch (OneboxRestException e) {
            assertEquals(e.getErrorCode(), MsEventErrorCode.START_GREATER_END_DATE.getErrorCode());
        }
    }

    @Test
    public void updateEventChannel_eventChannelNotFound() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setPermitereservas((byte) 1);
        CpanelCuposConfigRecord record = new CpanelCuposConfigRecord();
        record.setIdcupo(1);
        CpanelEntidadRecord cpanelEntidadRecord = new CpanelEntidadRecord();
        cpanelEntidadRecord.setAllowsecmkt((byte) 1);
        when(saleGroupDao.getByEventId(anyLong())).thenReturn(Collections.singletonList(record));
        when(eventDao.getById(anyInt())).thenReturn(event);
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.empty());
        when(entityDao.getEntityByChannelId(anyLong())).thenReturn(cpanelEntidadRecord);
        try {
            eventChannelService.updateEventChannel(1L, 1L, buildUpdateData());
            fail("Exception should be thrown on start date greater than end date");
        } catch (OneboxRestException e) {
            assertEquals(e.getErrorCode(), MsEventErrorCode.EVENT_CHANNEL_NOT_FOUND.getErrorCode());
        }
    }

    private UpdateEventChannelDTO buildUpdateData() {
        UpdateEventChannelDTO updateData = new UpdateEventChannelDTO();
        updateData.setUseAllSaleGroups(false);
        updateData.setSaleGroups(new ArrayList<>());
        updateData.getSaleGroups().add(1L);
        updateData.setSettings(new UpdateEventChannelSettingsDTO());
        updateData.getSettings().setUseEventDates(false);
        updateData.getSettings().setSaleEnabled(true);
        updateData.getSettings().setReleaseEnabled(true);
        updateData.getSettings().setBookingEnabled(true);
        updateData.getSettings().setSaleStartDate(ZonedDateTime.now());
        updateData.getSettings().setSaleEndDate(ZonedDateTime.now().plusDays(1L));
        updateData.getSettings().setReleaseDate(ZonedDateTime.now());
        updateData.getSettings().setBookingStartDate(ZonedDateTime.now());
        updateData.getSettings().setBookingEndDate(ZonedDateTime.now().plusDays(1L));
        return updateData;
    }


}
