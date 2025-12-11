package es.onebox.event.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import es.onebox.event.catalog.dao.CatalogEventCouchDao;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventRate;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationByPriceZoneDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionPriceZoneOccupationDTO;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.ticket.repository.SessionOccupationRepository;
import es.onebox.event.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.EventLanguageDao;
import es.onebox.event.events.dao.record.EventLanguageRecord;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.domain.VenueTemplateType;
import es.onebox.event.events.dto.BaseEventChannelDTO;
import es.onebox.event.events.dto.EventChannelDTO;
import es.onebox.event.events.dto.EventChannelInfoDTO;
import es.onebox.event.events.dto.EventChannelStatusDTO;
import es.onebox.event.events.dto.EventChannelsDTO;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.dto.EventTemplatePriceDTO;
import es.onebox.event.events.enums.EventChannelStatus;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.service.EventChannelService;
import es.onebox.event.events.service.EventService;
import es.onebox.event.events.service.EventTemplateService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.domain.sessionconfig.DynamicPrice;
import es.onebox.event.sessions.domain.sessionconfig.DynamicPriceZone;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionDynamicPriceConfig;
import es.onebox.event.sessions.dto.ConditionType;
import es.onebox.event.sessions.dto.DynamicPriceDTO;
import es.onebox.event.sessions.dto.DynamicPriceTranslationDTO;
import es.onebox.event.sessions.dto.DynamicPriceZoneDTO;
import es.onebox.event.sessions.dto.DynamicRatesPriceDTO;
import es.onebox.event.sessions.dto.RateDTO;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionDateDTO;
import es.onebox.event.sessions.dto.SessionDynamicPriceConfigDTO;
import es.onebox.event.sessions.dto.SessionSalesType;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.service.SessionDynamicPriceService;
import es.onebox.event.sessions.service.SessionService;
import es.onebox.event.sessions.utils.DynamicPriceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionDynamicPriceServiceTest {

    @Mock
    private SessionConfigCouchDao sessionConfigCouchDao;

    @Mock
    private EventDao eventDao;

    @Mock
    private SessionOccupationRepository sessionOccupationRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private CatalogEventCouchDao catalogEventCouchDao;

    @Mock
    private RefreshDataService refreshDataService;

    @Mock
    private EventLanguageDao eventLanguageDao;

    @Mock
    private EventService eventService;

    @Mock
    private EventChannelService eventChannelService;

    @Mock
    private VenuesRepository venuesRepository;

    @Mock
    private EventTemplateService eventTemplateService;

    @InjectMocks
    private SessionDynamicPriceService sessionDynamicPriceService;

    private final Long sessionId = 1L;
    private final Long idPriceZone = 409283L;
    private final Long eventId = 200L;

    private SessionConfig sessionConfig;
    Map.Entry<EventRecord, List<VenueRecord>> eventVenueRecord;
    private Event event;
    private SessionDTO sessionDTO;
    private EventDTO eventDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sessionConfig = new SessionConfig();
        SessionDynamicPriceConfig sessionDynamicPriceConfig = new SessionDynamicPriceConfig();
        sessionDynamicPriceConfig.setActive(true);

        List<DynamicPriceZone> dynamicPriceZones = new ArrayList<>();

        DynamicPriceZone zone1 = new DynamicPriceZone();
        zone1.setActiveZone(1L);
        zone1.setIdPriceZone(399667L);
        zone1.setDynamicPrices(new ArrayList<>());
        DynamicPrice price = new DynamicPrice();
        price.setOrder(0);
        price.setCapacity(3);

        Set<ConditionType> conditionTypes = new HashSet<>();
        conditionTypes.add(ConditionType.CAPACITY);
        price.setConditionTypes(conditionTypes);
        zone1.getDynamicPrices().add(price);

        DynamicPriceZone zone2 = new DynamicPriceZone();
        zone2.setActiveZone(1L);
        zone2.setIdPriceZone(409283L);
        zone2.setDynamicPrices(new ArrayList<>());
        zone2.getDynamicPrices().add(price);
        dynamicPriceZones.add(zone1);
        dynamicPriceZones.add(zone2);

        sessionDynamicPriceConfig.setDynamicPriceZone(dynamicPriceZones);
        sessionConfig.setSessionDynamicPriceConfig(sessionDynamicPriceConfig);
        sessionConfig.setEventId(1L);

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(eventId.intValue());
        eventRecord.setUseSimplifiedInvoice((byte) 1);
        eventRecord.setEstado(EventStatus.IN_PROGRESS.getId());
        eventRecord.setTipoevento(1);
        VenueRecord venueRecord = new VenueRecord();
        venueRecord.setVenueConfigType(VenueTemplateType.DEFAULT);
        venueRecord.setIdrecinto(1);
        eventVenueRecord = new AbstractMap.SimpleEntry<>(eventRecord, Collections.singletonList(venueRecord));

        event = new Event();
        event.setEventId(1L);
        EventRate rate = new EventRate();
        rate.setId(1L);
        rate.setName("rate");
        event.setRates(Collections.singletonList(rate));

        sessionDTO = new SessionDTO();
        sessionDTO.setId(sessionId);
        sessionDTO.setEventId(eventId);
        sessionDTO.setVenueConfigId(1L);
        sessionDTO.setStatus(SessionStatus.IN_PROGRESS);
        sessionDTO.setSaleType(SessionSalesType.INDIVIDUAL.getType());
        RateDTO rateDTO = new RateDTO();
        rateDTO.setId(1L);
        rateDTO.setName("rate");
        sessionDTO.setRates(Collections.singletonList(rateDTO));

        ZonedDateTimeWithRelative dateWithRelative = ZonedDateTimeWithRelative.of(ZonedDateTime.now());
        SessionDateDTO sessionDate = new SessionDateDTO();
        sessionDate.setSalesStart(dateWithRelative);
        sessionDTO.setDate(sessionDate);

        eventDTO = new EventDTO();
        eventDTO.setId(eventId);
        eventDTO.setUseTieredPricing(false);
        eventDTO.setType(EventType.NORMAL);

        EventTemplatePriceDTO eventTemplatePriceDTO = new EventTemplatePriceDTO();
        eventTemplatePriceDTO.setPrice(1.0);
        eventTemplatePriceDTO.setRateId(1);
        eventTemplatePriceDTO.setPriceTypeId(409283L);

        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        when(eventService.getEvent(anyLong())).thenReturn(eventDTO);
        when(eventLanguageDao.findByEventId(anyLong())).thenReturn(Collections.emptyList());
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(new EventChannelsDTO());
        when(venuesRepository.getPriceTypes(anyLong())).thenReturn(Collections.emptyList());
        when(eventTemplateService.getPrices(any() ,any(), any(), any(), any())).thenReturn(Collections.singletonList(eventTemplatePriceDTO));
        doNothing().when(refreshDataService).refreshSession(anyLong(), anyString());
    }

    @Test
    void testGetActive_Success() {
        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(sessionConfig);
        EventRecord eventRecord = new EventRecord();
        eventRecord.setUseSimplifiedInvoice((byte) 1);
        eventRecord.setEstado(EventStatus.IN_PROGRESS.getId());
        VenueRecord venueRecord = new VenueRecord();
        venueRecord.setVenueConfigType(VenueTemplateType.DEFAULT);

        when(eventDao.findEvent(anyLong())).thenReturn(eventVenueRecord);
        when(catalogEventCouchDao.get(anyString())).thenReturn(event);

        SessionOccupationByPriceZoneDTO occupationDTO = new SessionOccupationByPriceZoneDTO();
        SessionPriceZoneOccupationDTO occupation = new SessionPriceZoneOccupationDTO();
        occupation.setPriceZoneId(idPriceZone);
        occupation.setStatus(Map.of(TicketStatus.SOLD, 50L));
        occupationDTO.setOccupation(Collections.singletonList(occupation));

        when(sessionOccupationRepository.searchOccupationsByPriceZones(any())).thenReturn(Collections.singletonList(occupationDTO));

        DynamicPriceZoneDTO result = sessionDynamicPriceService.getActive(eventId, sessionId, idPriceZone);

        assertNotNull(result);
        assertEquals(idPriceZone, result.getIdPriceZone());
    }

    @Test
    void testGetActive_Success_nextOrder() {
        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(sessionConfig);
        EventRecord eventRecord = new EventRecord();
        eventRecord.setUseSimplifiedInvoice((byte) 1);
        eventRecord.setEstado(EventStatus.IN_PROGRESS.getId());
        VenueRecord venueRecord = new VenueRecord();
        venueRecord.setVenueConfigType(VenueTemplateType.DEFAULT);

        Set<ConditionType> conditionTypes = new HashSet<>();
        conditionTypes.add(ConditionType.CAPACITY);
        DynamicPrice price = new DynamicPrice();
        price.setOrder(0);
        price.setCapacity(51);
        price.setConditionTypes(conditionTypes);
        sessionConfig.getSessionDynamicPriceConfig().getDynamicPriceZone().get(1).getDynamicPrices().add(price);

        when(eventDao.findEvent(anyLong())).thenReturn(eventVenueRecord);
        when(catalogEventCouchDao.get(anyString())).thenReturn(event);

        SessionOccupationByPriceZoneDTO occupationDTO = new SessionOccupationByPriceZoneDTO();
        SessionPriceZoneOccupationDTO occupation = new SessionPriceZoneOccupationDTO();
        occupation.setPriceZoneId(idPriceZone);
        occupation.setStatus(Map.of(TicketStatus.SOLD, 50L));
        occupationDTO.setOccupation(Collections.singletonList(occupation));

        when(sessionOccupationRepository.searchOccupationsByPriceZones(any())).thenReturn(Collections.singletonList(occupationDTO));
        when(sessionOccupationRepository.countSessionOccupationsByPriceZones(anyLong(), anyLong())).thenReturn(4L);

        DynamicPriceZoneDTO result = sessionDynamicPriceService.getActive(eventId, sessionId, idPriceZone);

        assertNotNull(result);
        assertEquals(idPriceZone, result.getIdPriceZone());
        assertEquals(1, result.getActiveZone());
    }

    @Test
    void testGetActive_Success_lastRefundedOrder() {
        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(sessionConfig);
        EventRecord eventRecord = new EventRecord();
        eventRecord.setUseSimplifiedInvoice((byte) 1);
        eventRecord.setEstado(EventStatus.IN_PROGRESS.getId());
        VenueRecord venueRecord = new VenueRecord();
        venueRecord.setVenueConfigType(VenueTemplateType.DEFAULT);

        Set<ConditionType> conditionTypes = new HashSet<>();
        conditionTypes.add(ConditionType.CAPACITY);
        DynamicPrice price = new DynamicPrice();
        price.setOrder(1);
        price.setCapacity(50);
        price.setConditionTypes(conditionTypes);
        sessionConfig.getSessionDynamicPriceConfig().getDynamicPriceZone().get(1).getDynamicPrices().add(price);
        sessionConfig.getSessionDynamicPriceConfig().getDynamicPriceZone().get(1).setActiveZone(1L);
        when(eventDao.findEvent(anyLong())).thenReturn(eventVenueRecord);
        when(catalogEventCouchDao.get(anyString())).thenReturn(event);

        SessionOccupationByPriceZoneDTO occupationDTO = new SessionOccupationByPriceZoneDTO();
        SessionPriceZoneOccupationDTO occupation = new SessionPriceZoneOccupationDTO();
        occupation.setPriceZoneId(idPriceZone);
        occupation.setStatus(Map.of(TicketStatus.SOLD, 2L));
        occupationDTO.setOccupation(Collections.singletonList(occupation));

        when(sessionOccupationRepository.searchOccupationsByPriceZones(any())).thenReturn(Collections.singletonList(occupationDTO));

        DynamicPriceZoneDTO result = sessionDynamicPriceService.getActive(eventId, sessionId, idPriceZone);

        assertNotNull(result);
        assertEquals(idPriceZone, result.getIdPriceZone());
        assertEquals(0, result.getActiveZone());
    }

    @Test
    void testGetActive_SessionConfigNotFound() {
        when(sessionConfigCouchDao.get(anyString())).thenReturn(sessionConfig);
        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.getActive(eventId, sessionId, idPriceZone));
        assertEquals(MsEventErrorCode.SESSION_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testGetActive_EventNotFound() {
        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(sessionConfig);
        when(eventDao.findEvent(eventId)).thenReturn(null);
        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.getActive(eventId, sessionId, idPriceZone));
        assertEquals(MsEventErrorCode.SESSION_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testGetActive_OccupationNotFound() {
        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(sessionConfig);
        when(eventDao.findEvent(anyLong())).thenReturn(eventVenueRecord);
        when(catalogEventCouchDao.get(anyString())).thenReturn(event);
        when(sessionOccupationRepository.searchOccupationsByPriceZones(any())).thenReturn(Collections.emptyList());
        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.getActive(eventId, sessionId, idPriceZone));
        assertEquals(MsEventErrorCode.PRICE_OCCUPATION_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testGetActive_SessionNotFound() {
        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(sessionConfig);
        when(eventDao.findEvent(anyLong())).thenReturn(eventVenueRecord);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(null);
        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.getActive(eventId, sessionId, idPriceZone));
        assertEquals(MsEventErrorCode.SESSION_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testUpdateActivationDynamicPrice_Success() {
        when(eventService.getEvent(eventId)).thenReturn(eventDTO);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(new EventChannelsDTO());
        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(sessionConfig);

        sessionDynamicPriceService.updateActivationDynamicPrice(eventId, sessionId, true);

        verify(sessionConfigCouchDao).upsert(String.valueOf(sessionId), sessionConfig);
        assertTrue(sessionConfig.getSessionDynamicPriceConfig().getActive());
    }

    @Test
    void testUpdateActivationDynamicPrice_TieredPricingActive() {
        eventDTO.setUseTieredPricing(true);
        when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.updateActivationDynamicPrice(eventId, sessionId, true));
        assertEquals(MsEventErrorCode.CANNOT_ACTIVATED_DYNAMIC_PRICE_IF_TIERED_PRICING_ACTIVE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testUpdateActivationDynamicPrice_UnsupportedEventType() {
        eventDTO.setType(EventType.SEASON_TICKET);
        when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.updateActivationDynamicPrice(eventId, sessionId, true));
        assertEquals(MsEventErrorCode.CANNOT_ACTIVATED_DYNAMIC_PRICE_FOR_THIS_TYPE_EVENT.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testUpdateActivationDynamicPrice_SessionReady() {
        when(eventService.getEvent(eventId)).thenReturn(eventDTO);
        sessionDTO.setStatus(SessionStatus.READY);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(new EventChannelsDTO());

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.updateActivationDynamicPrice(eventId, sessionId, true));
        assertEquals(MsEventErrorCode.CANNOT_UPDATE_DYNAMIC_PRICE_FOR_ACTIVE_SESSION.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testGetSessionDynamicPriceConfig_Success() {
        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(sessionConfig);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        List<IdNameCodeDTO> priceTypes = new ArrayList<>();
        when(venuesRepository.getPriceTypes(anyLong())).thenReturn(priceTypes);

        SessionDynamicPriceConfigDTO result = sessionDynamicPriceService.getSessionDynamicPriceConfig(sessionId, false);

        assertNotNull(result);
    }

    @Test
    void testGetSessionDynamicPriceConfig_WithInitialize() {
        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(sessionConfig);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        List<IdNameCodeDTO> priceTypes = new ArrayList<>();
        when(venuesRepository.getPriceTypes(anyLong())).thenReturn(priceTypes);

        SessionDynamicPriceConfigDTO result = sessionDynamicPriceService.getSessionDynamicPriceConfig(sessionId, true);

        assertNotNull(result);
        verify(sessionConfigCouchDao).upsert(String.valueOf(sessionId), sessionConfig);
    }

    @Test
    void testGetDynamicRatePrice_Success() {
        SessionDynamicPriceConfig configWithRates = sessionConfig.getSessionDynamicPriceConfig();
        for (DynamicPriceZone zone : configWithRates.getDynamicPriceZone()) {
            for (DynamicPrice price : zone.getDynamicPrices()) {
                price.setDynamicRatesPrice(new ArrayList<>());
            }
        }

        when(sessionConfigCouchDao.findDynamicPriceBySessionId(sessionId)).thenReturn(configWithRates);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);

        List<DynamicRatesPriceDTO> result = sessionDynamicPriceService.getDynamicRatePrice(sessionId, idPriceZone);

        assertNotNull(result);
    }

    @Test
    void testGetDynamicRatePrice_ZoneNotFound() {
        when(sessionConfigCouchDao.findDynamicPriceBySessionId(sessionId)).thenReturn(sessionConfig.getSessionDynamicPriceConfig());
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.getDynamicRatePrice(sessionId, 999L));
        assertEquals(MsEventErrorCode.DYNAMIC_PRICE_ZONE_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testCreateOrUpdateSessionDynamicPrices_Success() {
        SessionConfig testConfig = new SessionConfig();
        SessionDynamicPriceConfig testDynamicConfig = new SessionDynamicPriceConfig();
        testDynamicConfig.setActive(true);
        testDynamicConfig.setDynamicPriceZone(new ArrayList<>());
        testConfig.setSessionDynamicPriceConfig(testDynamicConfig);

        sessionDTO.setStatus(SessionStatus.IN_PROGRESS);
        ZonedDateTimeWithRelative futureDate = ZonedDateTimeWithRelative.of(ZonedDateTime.now().plusDays(1));
        sessionDTO.getDate().setSalesStart(futureDate);

        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(testConfig);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        when(eventLanguageDao.findByEventId(anyLong())).thenReturn(Collections.emptyList());
        doNothing().when(refreshDataService).refreshSession(anyLong(), anyString());
        when(eventService.getEvent(anyLong())).thenReturn(eventDTO);

        EventChannelsDTO eventChannelsDTO = new EventChannelsDTO();
        eventChannelsDTO.setData(new ArrayList<>());
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(eventChannelsDTO);

        List<DynamicPriceDTO> requests = new ArrayList<>();
        DynamicPriceDTO priceDTO = new DynamicPriceDTO();
        priceDTO.setOrder(0);
        priceDTO.setName("Test Price");
        Set<ConditionType> conditionTypes = new HashSet<>();
        conditionTypes.add(ConditionType.CAPACITY);
        priceDTO.setConditionTypes(conditionTypes);
        priceDTO.setCapacity(10);

        List<DynamicRatesPriceDTO> ratesPrices = new ArrayList<>();
        DynamicRatesPriceDTO ratePrice = new DynamicRatesPriceDTO();
        ratePrice.setId(1L);
        ratePrice.setPrice(100.0);
        ratesPrices.add(ratePrice);
        priceDTO.setDynamicRatesPriceDTO(ratesPrices);

        requests.add(priceDTO);

        sessionDynamicPriceService.createOrUpdateSessionDynamicPrices(sessionId, idPriceZone, requests);

        verify(sessionConfigCouchDao, times(2)).upsert(String.valueOf(sessionId), testConfig);
        verify(refreshDataService).refreshSession(eq(sessionId), anyString());
    }

    @Test
    void testCreateOrUpdateSessionDynamicPrices_InvalidOrderSequence() {
        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(sessionConfig);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        when(eventService.getEvent(anyLong())).thenReturn(eventDTO);

        EventChannelsDTO eventChannelsDTO = new EventChannelsDTO();
        eventChannelsDTO.setData(new ArrayList<>());
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(eventChannelsDTO);

        List<DynamicPriceDTO> requests = new ArrayList<>();
        DynamicPriceDTO price1 = new DynamicPriceDTO();
        price1.setOrder(0);
        DynamicPriceDTO price2 = new DynamicPriceDTO();
        price2.setOrder(2);
        requests.add(price1);
        requests.add(price2);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.createOrUpdateSessionDynamicPrices(sessionId, idPriceZone, requests));
        assertEquals(MsEventErrorCode.INVALID_ORDER_SEQUENCE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testDeleteSessionDynamicPrice_Success() {
        SessionConfig config = new SessionConfig();
        SessionDynamicPriceConfig dynamicPriceConfig = new SessionDynamicPriceConfig();
        dynamicPriceConfig.setDynamicPriceZone(new ArrayList<>());

        DynamicPriceZone zone = new DynamicPriceZone();
        zone.setIdPriceZone(idPriceZone);
        zone.setActiveZone(0L);
        zone.setDynamicPrices(new ArrayList<>());

        DynamicPrice price = new DynamicPrice();
        price.setOrder(1);
        zone.getDynamicPrices().add(price);

        dynamicPriceConfig.getDynamicPriceZone().add(zone);
        config.setSessionDynamicPriceConfig(dynamicPriceConfig);

        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(config);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);

        sessionDynamicPriceService.deleteSessionDynamicPrice(sessionId, idPriceZone, 1);

        verify(sessionConfigCouchDao).upsert(String.valueOf(sessionId), config);
        assertTrue(zone.getDynamicPrices().isEmpty());
    }

    @Test
    void testDeleteSessionDynamicPrice_ConfigNotFound() {
        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(null);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.deleteSessionDynamicPrice(sessionId, idPriceZone, 1));
        assertEquals(MsEventErrorCode.DYNAMIC_PRICE_CONFIG_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testDeleteSessionDynamicPrice_ZoneNotFound() {
        SessionConfig config = new SessionConfig();
        SessionDynamicPriceConfig dynamicPriceConfig = new SessionDynamicPriceConfig();
        dynamicPriceConfig.setDynamicPriceZone(new ArrayList<>());
        config.setSessionDynamicPriceConfig(dynamicPriceConfig);

        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(config);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.deleteSessionDynamicPrice(sessionId, idPriceZone, 1));
        assertEquals(MsEventErrorCode.DYNAMIC_PRICE_ZONE_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testDeleteSessionDynamicPrice_CannotDeletePassDynamicPrice() {
        SessionConfig config = new SessionConfig();
        SessionDynamicPriceConfig dynamicPriceConfig = new SessionDynamicPriceConfig();
        dynamicPriceConfig.setDynamicPriceZone(new ArrayList<>());

        DynamicPriceZone zone = new DynamicPriceZone();
        zone.setIdPriceZone(idPriceZone);
        zone.setActiveZone(2L);
        zone.setDynamicPrices(new ArrayList<>());

        DynamicPrice price = new DynamicPrice();
        price.setOrder(1);
        zone.getDynamicPrices().add(price);

        dynamicPriceConfig.getDynamicPriceZone().add(zone);
        config.setSessionDynamicPriceConfig(dynamicPriceConfig);

        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(config);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.deleteSessionDynamicPrice(sessionId, idPriceZone, 1));
        assertEquals(MsEventErrorCode.CANNOT_DELETE_PASS_DYNAMIC_PRICE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testDeleteSessionDynamicPrice_PriceNotFound() {
        SessionConfig config = new SessionConfig();
        SessionDynamicPriceConfig dynamicPriceConfig = new SessionDynamicPriceConfig();
        dynamicPriceConfig.setDynamicPriceZone(new ArrayList<>());

        DynamicPriceZone zone = new DynamicPriceZone();
        zone.setIdPriceZone(idPriceZone);
        zone.setActiveZone(0L);
        zone.setDynamicPrices(new ArrayList<>());

        DynamicPrice price = new DynamicPrice();
        price.setOrder(1);
        zone.getDynamicPrices().add(price);

        dynamicPriceConfig.getDynamicPriceZone().add(zone);
        config.setSessionDynamicPriceConfig(dynamicPriceConfig);

        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(config);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.deleteSessionDynamicPrice(sessionId, idPriceZone, 2));
        assertEquals(MsEventErrorCode.DYNAMIC_PRICE_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testCreateOrUpdateSessionDynamicPrices_NoConditionType() {
        SessionConfig testConfig = new SessionConfig();
        SessionDynamicPriceConfig testDynamicConfig = new SessionDynamicPriceConfig();
        testDynamicConfig.setActive(true);
        testDynamicConfig.setDynamicPriceZone(new ArrayList<>());

        DynamicPriceZone zone = new DynamicPriceZone();
        zone.setIdPriceZone(idPriceZone);
        zone.setActiveZone(0L);
        zone.setDynamicPrices(new ArrayList<>());
        testDynamicConfig.getDynamicPriceZone().add(zone);

        testConfig.setSessionDynamicPriceConfig(testDynamicConfig);

        sessionDTO.setStatus(SessionStatus.IN_PROGRESS);
        ZonedDateTimeWithRelative futureDate = ZonedDateTimeWithRelative.of(ZonedDateTime.now().plusDays(1));
        sessionDTO.getDate().setSalesStart(futureDate);

        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(testConfig);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        when(eventService.getEvent(anyLong())).thenReturn(eventDTO);

        EventChannelsDTO eventChannelsDTO = new EventChannelsDTO();
        eventChannelsDTO.setData(new ArrayList<>());
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(eventChannelsDTO);

        List<DynamicPriceDTO> requests = new ArrayList<>();
        DynamicPriceDTO priceDTO = new DynamicPriceDTO();
        priceDTO.setOrder(0);
        priceDTO.setName("Test Price");
        requests.add(priceDTO);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.createOrUpdateSessionDynamicPrices(sessionId, idPriceZone, requests));
        assertEquals(MsEventErrorCode.MISSING_CONDITION_TYPE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testCreateOrUpdateSessionDynamicPrices_NoRates() {
        SessionConfig testConfig = new SessionConfig();
        SessionDynamicPriceConfig testDynamicConfig = new SessionDynamicPriceConfig();
        testDynamicConfig.setActive(true);
        testDynamicConfig.setDynamicPriceZone(new ArrayList<>());

        DynamicPriceZone zone = new DynamicPriceZone();
        zone.setIdPriceZone(idPriceZone);
        zone.setActiveZone(0L);
        zone.setDynamicPrices(new ArrayList<>());
        testDynamicConfig.getDynamicPriceZone().add(zone);

        testConfig.setSessionDynamicPriceConfig(testDynamicConfig);

        sessionDTO.setStatus(SessionStatus.IN_PROGRESS);
        ZonedDateTimeWithRelative futureDate = ZonedDateTimeWithRelative.of(ZonedDateTime.now().plusDays(1));
        sessionDTO.getDate().setSalesStart(futureDate);

        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(testConfig);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        when(eventService.getEvent(anyLong())).thenReturn(eventDTO);

        EventChannelsDTO eventChannelsDTO = new EventChannelsDTO();
        eventChannelsDTO.setData(new ArrayList<>());
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(eventChannelsDTO);

        List<DynamicPriceDTO> requests = new ArrayList<>();
        DynamicPriceDTO priceDTO = new DynamicPriceDTO();
        priceDTO.setOrder(0);
        priceDTO.setName("Test Price");
        Set<ConditionType> conditionTypes = new HashSet<>();
        conditionTypes.add(ConditionType.CAPACITY);
        priceDTO.setConditionTypes(conditionTypes);
        priceDTO.setCapacity(10);
        requests.add(priceDTO);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.createOrUpdateSessionDynamicPrices(sessionId, idPriceZone, requests));
        assertEquals(MsEventErrorCode.MISSING_RATES.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testCreateOrUpdateSessionDynamicPrices_CapacityConditionWithoutCapacity() {
        SessionConfig testConfig = new SessionConfig();
        SessionDynamicPriceConfig testDynamicConfig = new SessionDynamicPriceConfig();
        testDynamicConfig.setActive(true);
        testDynamicConfig.setDynamicPriceZone(new ArrayList<>());

        DynamicPriceZone zone = new DynamicPriceZone();
        zone.setIdPriceZone(idPriceZone);
        zone.setActiveZone(0L);
        zone.setDynamicPrices(new ArrayList<>());
        testDynamicConfig.getDynamicPriceZone().add(zone);

        testConfig.setSessionDynamicPriceConfig(testDynamicConfig);

        sessionDTO.setStatus(SessionStatus.IN_PROGRESS);
        ZonedDateTimeWithRelative futureDate = ZonedDateTimeWithRelative.of(ZonedDateTime.now().plusDays(1));
        sessionDTO.getDate().setSalesStart(futureDate);

        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(testConfig);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        when(eventService.getEvent(anyLong())).thenReturn(eventDTO);

        EventChannelsDTO eventChannelsDTO = new EventChannelsDTO();
        eventChannelsDTO.setData(new ArrayList<>());
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(eventChannelsDTO);

        List<DynamicPriceDTO> requests = new ArrayList<>();
        DynamicPriceDTO priceDTO = new DynamicPriceDTO();
        priceDTO.setOrder(0);
        priceDTO.setName("Test Price");
        Set<ConditionType> conditionTypes = new HashSet<>();
        conditionTypes.add(ConditionType.CAPACITY);
        priceDTO.setConditionTypes(conditionTypes);

        List<DynamicRatesPriceDTO> ratesPrices = new ArrayList<>();
        DynamicRatesPriceDTO ratePrice = new DynamicRatesPriceDTO();
        ratePrice.setId(1L);
        ratePrice.setPrice(100.0);
        ratesPrices.add(ratePrice);
        priceDTO.setDynamicRatesPriceDTO(ratesPrices);

        requests.add(priceDTO);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.createOrUpdateSessionDynamicPrices(sessionId, idPriceZone, requests));
        assertEquals(MsEventErrorCode.MISSING_CAPACITY.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testCreateOrUpdateSessionDynamicPrices_DateConditionWithoutDate() {
        SessionConfig testConfig = new SessionConfig();
        SessionDynamicPriceConfig testDynamicConfig = new SessionDynamicPriceConfig();
        testDynamicConfig.setActive(true);
        testDynamicConfig.setDynamicPriceZone(new ArrayList<>());

        DynamicPriceZone zone = new DynamicPriceZone();
        zone.setIdPriceZone(idPriceZone);
        zone.setActiveZone(0L);
        zone.setDynamicPrices(new ArrayList<>());
        testDynamicConfig.getDynamicPriceZone().add(zone);

        testConfig.setSessionDynamicPriceConfig(testDynamicConfig);

        sessionDTO.setStatus(SessionStatus.IN_PROGRESS);
        ZonedDateTimeWithRelative futureDate = ZonedDateTimeWithRelative.of(ZonedDateTime.now().plusDays(1));
        sessionDTO.getDate().setSalesStart(futureDate);

        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(testConfig);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        when(eventService.getEvent(anyLong())).thenReturn(eventDTO);

        EventChannelsDTO eventChannelsDTO = new EventChannelsDTO();
        eventChannelsDTO.setData(new ArrayList<>());
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(eventChannelsDTO);

        List<DynamicPriceDTO> requests = new ArrayList<>();
        DynamicPriceDTO priceDTO = new DynamicPriceDTO();
        priceDTO.setOrder(0);
        priceDTO.setName("Test Price");
        Set<ConditionType> conditionTypes = new HashSet<>();
        conditionTypes.add(ConditionType.DATE);
        priceDTO.setConditionTypes(conditionTypes);

        List<DynamicRatesPriceDTO> ratesPrices = new ArrayList<>();
        DynamicRatesPriceDTO ratePrice = new DynamicRatesPriceDTO();
        ratePrice.setId(1L);
        ratePrice.setPrice(100.0);
        ratesPrices.add(ratePrice);
        priceDTO.setDynamicRatesPriceDTO(ratesPrices);

        requests.add(priceDTO);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.createOrUpdateSessionDynamicPrices(sessionId, idPriceZone, requests));
        assertEquals(MsEventErrorCode.MISSING_DATE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testCreateOrUpdateSessionDynamicPrices_InvalidCapacitySequence() {
        SessionConfig testConfig = new SessionConfig();
        SessionDynamicPriceConfig testDynamicConfig = new SessionDynamicPriceConfig();
        testDynamicConfig.setActive(true);
        testDynamicConfig.setDynamicPriceZone(new ArrayList<>());

        DynamicPriceZone zone = new DynamicPriceZone();
        zone.setIdPriceZone(idPriceZone);
        zone.setActiveZone(0L);
        zone.setDynamicPrices(new ArrayList<>());
        testDynamicConfig.getDynamicPriceZone().add(zone);

        testConfig.setSessionDynamicPriceConfig(testDynamicConfig);

        sessionDTO.setStatus(SessionStatus.IN_PROGRESS);
        ZonedDateTimeWithRelative futureDate = ZonedDateTimeWithRelative.of(ZonedDateTime.now().plusDays(1));
        sessionDTO.getDate().setSalesStart(futureDate);

        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(testConfig);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        when(eventService.getEvent(anyLong())).thenReturn(eventDTO);

        EventChannelsDTO eventChannelsDTO = new EventChannelsDTO();
        eventChannelsDTO.setData(new ArrayList<>());
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(eventChannelsDTO);

        List<DynamicPriceDTO> requests = new ArrayList<>();

        DynamicPriceDTO price1 = new DynamicPriceDTO();
        price1.setOrder(0);
        price1.setName("Test Price 1");
        Set<ConditionType> conditionTypes1 = new HashSet<>();
        conditionTypes1.add(ConditionType.CAPACITY);
        price1.setConditionTypes(conditionTypes1);
        price1.setCapacity(20);

        List<DynamicRatesPriceDTO> ratesPrices1 = new ArrayList<>();
        DynamicRatesPriceDTO ratePrice1 = new DynamicRatesPriceDTO();
        ratePrice1.setId(1L);
        ratePrice1.setPrice(100.0);
        ratesPrices1.add(ratePrice1);
        price1.setDynamicRatesPriceDTO(ratesPrices1);

        DynamicPriceDTO price2 = new DynamicPriceDTO();
        price2.setOrder(1);
        price2.setName("Test Price 2");
        Set<ConditionType> conditionTypes2 = new HashSet<>();
        conditionTypes2.add(ConditionType.CAPACITY);
        price2.setConditionTypes(conditionTypes2);
        price2.setCapacity(10);

        List<DynamicRatesPriceDTO> ratesPrices2 = new ArrayList<>();
        DynamicRatesPriceDTO ratePrice2 = new DynamicRatesPriceDTO();
        ratePrice2.setId(1L);
        ratePrice2.setPrice(120.0);
        ratesPrices2.add(ratePrice2);
        price2.setDynamicRatesPriceDTO(ratesPrices2);

        requests.add(price1);
        requests.add(price2);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.createOrUpdateSessionDynamicPrices(sessionId, idPriceZone, requests));
        assertEquals(MsEventErrorCode.CAPACITY_LESS_THAN_PREVIOUS.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testCreateOrUpdateSessionDynamicPrices_InvalidDateSequence() {
        SessionConfig testConfig = new SessionConfig();
        SessionDynamicPriceConfig testDynamicConfig = new SessionDynamicPriceConfig();
        testDynamicConfig.setActive(true);
        testDynamicConfig.setDynamicPriceZone(new ArrayList<>());

        DynamicPriceZone zone = new DynamicPriceZone();
        zone.setIdPriceZone(idPriceZone);
        zone.setActiveZone(0L);
        zone.setDynamicPrices(new ArrayList<>());
        testDynamicConfig.getDynamicPriceZone().add(zone);

        testConfig.setSessionDynamicPriceConfig(testDynamicConfig);

        sessionDTO.setStatus(SessionStatus.IN_PROGRESS);
        ZonedDateTimeWithRelative futureDate = ZonedDateTimeWithRelative.of(ZonedDateTime.now().plusDays(1));
        sessionDTO.getDate().setSalesStart(futureDate);

        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(testConfig);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);
        when(eventService.getEvent(anyLong())).thenReturn(eventDTO);

        EventChannelsDTO eventChannelsDTO = new EventChannelsDTO();
        eventChannelsDTO.setData(new ArrayList<>());
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(eventChannelsDTO);

        List<DynamicPriceDTO> requests = new ArrayList<>();

        DynamicPriceDTO price1 = new DynamicPriceDTO();
        price1.setOrder(0);
        price1.setName("Test Price 1");
        Set<ConditionType> conditionTypes1 = new HashSet<>();
        conditionTypes1.add(ConditionType.DATE);
        price1.setConditionTypes(conditionTypes1);
        price1.setValidDate(ZonedDateTime.now().plusDays(3));

        List<DynamicRatesPriceDTO> ratesPrices1 = new ArrayList<>();
        DynamicRatesPriceDTO ratePrice1 = new DynamicRatesPriceDTO();
        ratePrice1.setId(1L);
        ratePrice1.setPrice(100.0);
        ratesPrices1.add(ratePrice1);
        price1.setDynamicRatesPriceDTO(ratesPrices1);

        DynamicPriceDTO price2 = new DynamicPriceDTO();
        price2.setOrder(1);
        price2.setName("Test Price 2");
        Set<ConditionType> conditionTypes2 = new HashSet<>();
        conditionTypes2.add(ConditionType.DATE);
        price2.setConditionTypes(conditionTypes2);
        price2.setValidDate(ZonedDateTime.now().plusDays(1));

        List<DynamicRatesPriceDTO> ratesPrices2 = new ArrayList<>();
        DynamicRatesPriceDTO ratePrice2 = new DynamicRatesPriceDTO();
        ratePrice2.setId(1L);
        ratePrice2.setPrice(120.0);
        ratesPrices2.add(ratePrice2);
        price2.setDynamicRatesPriceDTO(ratesPrices2);

        requests.add(price1);
        requests.add(price2);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.createOrUpdateSessionDynamicPrices(sessionId, idPriceZone, requests));
        assertEquals(MsEventErrorCode.DATE_LESS_THAN_PREVIOUS.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testUpdateActivationDynamicPrice_DynamicPricesRequireV4Channel() {
        when(eventService.getEvent(eventId)).thenReturn(eventDTO);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);

        EventChannelsDTO channelsDTO = new EventChannelsDTO();
        List<BaseEventChannelDTO> channels = new ArrayList<>();
        EventChannelDTO channelDTO = new EventChannelDTO();
        EventChannelInfoDTO channel = new EventChannelInfoDTO();
        channel.setV4Enabled(false);
        channelDTO.setChannel(channel);
        EventChannelStatusDTO status = new EventChannelStatusDTO();
        status.setRequest(EventChannelStatus.ACCEPTED);
        channelDTO.setStatus(status);
        channels.add(channelDTO);
        channelsDTO.setData(channels);

        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(channelsDTO);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.updateActivationDynamicPrice(eventId, sessionId, true));
        assertEquals(MsEventErrorCode.DYNAMIC_PRICES_REQUIRE_V4_CHANNEL.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testUpdateActivationDynamicPrice_ActivityWithGroups() {
        eventDTO.setType(EventType.ACTIVITY);
        when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        sessionDTO.setSaleType(SessionSalesType.GROUP.getType());
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);

        EventChannelsDTO eventChannelsDTO = new EventChannelsDTO();
        eventChannelsDTO.setData(new ArrayList<>());
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(eventChannelsDTO);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.updateActivationDynamicPrice(eventId, sessionId, true));
        assertEquals(MsEventErrorCode.CANNOT_ACTIVATED_DYNAMIC_PRICE_FOR_ACTIVITY_WITH_GROUPS.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testInitializeAndValidateTranslations_InvalidLanguageCode() {
        SessionConfig testConfig = new SessionConfig();
        SessionDynamicPriceConfig testDynamicConfig = new SessionDynamicPriceConfig();
        testDynamicConfig.setActive(true);
        testDynamicConfig.setDynamicPriceZone(new ArrayList<>());

        DynamicPriceZone zone = new DynamicPriceZone();
        zone.setIdPriceZone(idPriceZone);
        zone.setActiveZone(0L);
        zone.setDynamicPrices(new ArrayList<>());
        testDynamicConfig.getDynamicPriceZone().add(zone);

        testConfig.setSessionDynamicPriceConfig(testDynamicConfig);

        sessionDTO.setStatus(SessionStatus.IN_PROGRESS);
        ZonedDateTimeWithRelative futureDate = ZonedDateTimeWithRelative.of(ZonedDateTime.now().plusDays(1));
        sessionDTO.getDate().setSalesStart(futureDate);

        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(testConfig);
        when(sessionService.getSessionWithoutEventId(sessionId)).thenReturn(sessionDTO);

        when(eventService.getEvent(anyLong())).thenReturn(eventDTO);
        EventChannelsDTO eventChannelsDTO = new EventChannelsDTO();
        eventChannelsDTO.setData(new ArrayList<>());
        when(eventChannelService.getEventChannels(anyLong(), any())).thenReturn(eventChannelsDTO);

        List<EventLanguageRecord> validLanguages = new ArrayList<>();
        EventLanguageRecord lang = new EventLanguageRecord();
        lang.setCode("es");
        validLanguages.add(lang);
        when(eventLanguageDao.findByEventId(eventId)).thenReturn(validLanguages);

        List<DynamicPriceDTO> requests = new ArrayList<>();
        DynamicPriceDTO priceDTO = new DynamicPriceDTO();
        priceDTO.setOrder(0);
        priceDTO.setName("Test Price");

        List<DynamicPriceTranslationDTO> translations = new ArrayList<>();
        DynamicPriceTranslationDTO invalidTranslation = new DynamicPriceTranslationDTO();
        invalidTranslation.setLanguage("fr");
        invalidTranslation.setValue("Prix de test");
        translations.add(invalidTranslation);
        priceDTO.setTranslationsDTO(translations);

        Set<ConditionType> conditionTypes = new HashSet<>();
        conditionTypes.add(ConditionType.CAPACITY);
        priceDTO.setConditionTypes(conditionTypes);
        priceDTO.setCapacity(10);

        List<DynamicRatesPriceDTO> ratesPrices = new ArrayList<>();
        DynamicRatesPriceDTO ratePrice = new DynamicRatesPriceDTO();
        ratePrice.setId(1L);
        ratePrice.setPrice(100.0);
        ratesPrices.add(ratePrice);
        priceDTO.setDynamicRatesPriceDTO(ratesPrices);

        requests.add(priceDTO);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                sessionDynamicPriceService.createOrUpdateSessionDynamicPrices(sessionId, idPriceZone, requests));
        assertEquals(MsEventErrorCode.INVALID_LANGUAGE_CODE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testGetActive_NoDynamicPriceConfig() {
        SessionConfig sessionConfigWithoutDynamicPrice = new SessionConfig();

        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(sessionConfigWithoutDynamicPrice);

        DynamicPriceZoneDTO result = sessionDynamicPriceService.getActive(eventId, sessionId, idPriceZone);

        assertNotNull(result);
        assertEquals(idPriceZone, result.getIdPriceZone());
        assertFalse(result.getActive());
    }

    @Test
    void testGetActive_DynamicPriceConfigNotActive() {
        SessionConfig sessionConfigWithInactiveDynamicPrice = new SessionConfig();
        SessionDynamicPriceConfig inactiveConfig = new SessionDynamicPriceConfig();
        inactiveConfig.setActive(false);
        sessionConfigWithInactiveDynamicPrice.setSessionDynamicPriceConfig(inactiveConfig);

        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(sessionConfigWithInactiveDynamicPrice);

        DynamicPriceZoneDTO result = sessionDynamicPriceService.getActive(eventId, sessionId, idPriceZone);

        assertNotNull(result);
        assertEquals(idPriceZone, result.getIdPriceZone());
        assertFalse(result.getActive());
    }

    @Test
    void testNextOrder_DateCondition() {
        SessionConfig testConfig = new SessionConfig();
        SessionDynamicPriceConfig testDynamicConfig = new SessionDynamicPriceConfig();
        testDynamicConfig.setActive(true);
        testDynamicConfig.setDynamicPriceZone(new ArrayList<>());

        DynamicPriceZone zone = new DynamicPriceZone();
        zone.setIdPriceZone(idPriceZone);
        zone.setActiveZone(0L);
        zone.setDynamicPrices(new ArrayList<>());

        DynamicPrice price1 = new DynamicPrice();
        price1.setOrder(0);
        Set<ConditionType> conditionTypes = new HashSet<>();
        conditionTypes.add(ConditionType.DATE);
        price1.setConditionTypes(conditionTypes);
        price1.setValidDate(ZonedDateTime.now().minusDays(1));

        zone.getDynamicPrices().add(price1);

        DynamicPrice price2 = new DynamicPrice();
        price2.setOrder(1);
        price2.setConditionTypes(conditionTypes);
        price2.setValidDate(ZonedDateTime.now().plusDays(1));

        zone.getDynamicPrices().add(price2);

        testDynamicConfig.getDynamicPriceZone().add(zone);
        testConfig.setSessionDynamicPriceConfig(testDynamicConfig);

        when(sessionConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(testConfig);
        when(catalogEventCouchDao.get(anyString())).thenReturn(event);

        DynamicPriceZoneDTO result = sessionDynamicPriceService.getActive(eventId, sessionId, idPriceZone);

        assertNotNull(result);
        assertEquals(idPriceZone, result.getIdPriceZone());
        assertEquals(1, result.getActiveZone());
    }

    @Test
    void testValidateReordering_ValidSequence() {
        List<DynamicPrice> validSequence = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            DynamicPrice price = new DynamicPrice();
            price.setOrder(i);
            validSequence.add(price);
        }

        DynamicPriceValidator.validateReordering(validSequence);
    }

    @Test
    void testValidateReordering_NotStartingWithZero() {
        List<DynamicPrice> invalidSequence = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            DynamicPrice price = new DynamicPrice();
            price.setOrder(i);
            invalidSequence.add(price);
        }

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                DynamicPriceValidator.validateReordering(invalidSequence));
        assertEquals(MsEventErrorCode.INVALID_ORDER_SEQUENCE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testValidateReordering_WithGaps() {
        List<DynamicPrice> sequenceWithGaps = new ArrayList<>();

        DynamicPrice price1 = new DynamicPrice();
        price1.setOrder(0);
        sequenceWithGaps.add(price1);

        DynamicPrice price2 = new DynamicPrice();
        price2.setOrder(1);
        sequenceWithGaps.add(price2);

        DynamicPrice price3 = new DynamicPrice();
        price3.setOrder(3);
        sequenceWithGaps.add(price3);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                DynamicPriceValidator.validateReordering(sequenceWithGaps));
        assertEquals(MsEventErrorCode.INVALID_ORDER_SEQUENCE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testValidateReordering_UnsortedButValid() {
        List<DynamicPrice> unsortedSequence = new ArrayList<>();

        DynamicPrice price1 = new DynamicPrice();
        price1.setOrder(2);
        unsortedSequence.add(price1);

        DynamicPrice price2 = new DynamicPrice();
        price2.setOrder(0);
        unsortedSequence.add(price2);

        DynamicPrice price3 = new DynamicPrice();
        price3.setOrder(1);
        unsortedSequence.add(price3);

        DynamicPriceValidator.validateReordering(unsortedSequence);
    }

    @Test
    void testValidateDateSequence_MixedConditionTypes() {
        List<DynamicPriceDTO> mixedSequence = new ArrayList<>();

        DynamicPriceDTO price1 = new DynamicPriceDTO();
        price1.setOrder(0);
        price1.setValidDate(ZonedDateTime.now().minusDays(2));
        price1.setConditionTypes(Collections.singleton(ConditionType.DATE));
        mixedSequence.add(price1);

        DynamicPriceDTO price2 = new DynamicPriceDTO();
        price2.setOrder(1);
        price2.setCapacity(20);
        Set<ConditionType> capacityOnly = new HashSet<>();
        capacityOnly.add(ConditionType.CAPACITY);
        price2.setConditionTypes(capacityOnly);
        mixedSequence.add(price2);

        DynamicPriceValidator.validateDateSequence(mixedSequence);
    }

    @Test
    void testValidateCapacitySequence_ValidIncreasingSequence() {
        List<DynamicPriceDTO> validSequence = new ArrayList<>();

        DynamicPriceDTO price1 = new DynamicPriceDTO();
        price1.setOrder(0);
        price1.setCapacity(10);
        price1.setConditionTypes(Collections.singleton(ConditionType.CAPACITY));
        validSequence.add(price1);

        DynamicPriceDTO price2 = new DynamicPriceDTO();
        price2.setOrder(1);
        price2.setCapacity(20);
        price2.setConditionTypes(Collections.singleton(ConditionType.CAPACITY));
        validSequence.add(price2);

        DynamicPriceDTO price3 = new DynamicPriceDTO();
        price3.setOrder(2);
        price3.setCapacity(30);
        price3.setConditionTypes(Collections.singleton(ConditionType.CAPACITY));
        validSequence.add(price3);

        DynamicPriceValidator.validateCapacitySequence(validSequence);
    }

    @Test
    void testValidateCapacitySequence_MixedConditionTypes() {
        List<DynamicPriceDTO> mixedSequence = new ArrayList<>();

        DynamicPriceDTO price1 = new DynamicPriceDTO();
        price1.setOrder(0);
        price1.setCapacity(20);
        price1.setConditionTypes(Collections.singleton(ConditionType.CAPACITY));
        mixedSequence.add(price1);

        DynamicPriceDTO price2 = new DynamicPriceDTO();
        price2.setOrder(1);
        price2.setValidDate(ZonedDateTime.now().plusDays(1));
        Set<ConditionType> dateOnly = new HashSet<>();
        dateOnly.add(ConditionType.DATE);
        price2.setConditionTypes(dateOnly);
        mixedSequence.add(price2);

        DynamicPriceValidator.validateCapacitySequence(mixedSequence);
    }

    @Test
    void testValidateDateSequence_ValidIncreasingSequence() {
        List<DynamicPriceDTO> validSequence = new ArrayList<>();

        DynamicPriceDTO price1 = new DynamicPriceDTO();
        price1.setOrder(0);
        price1.setValidDate(ZonedDateTime.now().minusDays(2));
        price1.setConditionTypes(Collections.singleton(ConditionType.DATE));
        validSequence.add(price1);

        DynamicPriceDTO price2 = new DynamicPriceDTO();
        price2.setOrder(1);
        price2.setValidDate(ZonedDateTime.now().minusDays(1));
        price2.setConditionTypes(Collections.singleton(ConditionType.DATE));
        validSequence.add(price2);

        DynamicPriceDTO price3 = new DynamicPriceDTO();
        price3.setOrder(2);
        price3.setValidDate(ZonedDateTime.now());
        price3.setConditionTypes(Collections.singleton(ConditionType.DATE));
        validSequence.add(price3);

        DynamicPriceValidator.validateDateSequence(validSequence);
    }

    @Test
    void testValidateDynamicPrice_AllFieldsPresent() {
        DynamicPriceDTO validPrice = new DynamicPriceDTO();
        validPrice.setName("Test Price");
        validPrice.setOrder(0);

        Set<ConditionType> conditionTypes = new HashSet<>();
        conditionTypes.add(ConditionType.CAPACITY);
        validPrice.setConditionTypes(conditionTypes);
        validPrice.setCapacity(10);

        List<DynamicRatesPriceDTO> rates = new ArrayList<>();
        DynamicRatesPriceDTO rate = new DynamicRatesPriceDTO();
        rate.setId(1L);
        rate.setPrice(100.0);
        rates.add(rate);
        validPrice.setDynamicRatesPriceDTO(rates);

        ZonedDateTime creationDate = ZonedDateTime.now();
        ZonedDateTimeWithRelative saleStartDate = ZonedDateTimeWithRelative.of(ZonedDateTime.now().plusDays(1));

        DynamicPriceValidator.validateDynamicPrice(validPrice, creationDate, saleStartDate);
    }

    @Test
    void testValidateReordering_ValidateReordering() {
        List<DynamicPrice> pricesWithGaps = new ArrayList<>();

        DynamicPrice price1 = new DynamicPrice();
        price1.setOrder(0);
        pricesWithGaps.add(price1);

        DynamicPrice price2 = new DynamicPrice();
        price2.setOrder(2);
        pricesWithGaps.add(price2);

        DynamicPrice price3 = new DynamicPrice();
        price3.setOrder(4);
        pricesWithGaps.add(price3);

        pricesWithGaps.sort(Comparator.comparingInt(DynamicPrice::getOrder));

        int newOrder = 0;
        for (DynamicPrice price : pricesWithGaps) {
            price.setOrder(newOrder);
            newOrder++;
        }

        assertEquals(0, pricesWithGaps.get(0).getOrder());
        assertEquals(1, pricesWithGaps.get(1).getOrder());
        assertEquals(2, pricesWithGaps.get(2).getOrder());

        DynamicPriceValidator.validateReordering(pricesWithGaps);
    }

    @Test
    void testCreateOrUpdateDynamicPrices_CanAddAfterFinishedTiers() {
        DynamicPriceZone zone = new DynamicPriceZone();
        zone.setIdPriceZone(idPriceZone);
        zone.setActiveZone(1L);
        zone.setDynamicPrices(new ArrayList<>());
        
        DynamicPrice existingPrice = new DynamicPrice();
        existingPrice.setOrder(0);
        existingPrice.setName("T0");
        existingPrice.setCapacity(50);
        existingPrice.setConditionTypes(Set.of(ConditionType.CAPACITY));
        zone.getDynamicPrices().add(existingPrice);

        SessionConfig config = new SessionConfig();
        SessionDynamicPriceConfig dynamicConfig = new SessionDynamicPriceConfig();
        dynamicConfig.setActive(true);
        dynamicConfig.setDynamicPriceZone(List.of(zone));
        config.setSessionDynamicPriceConfig(dynamicConfig);
        
        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(config);
        
        DynamicPriceDTO request = new DynamicPriceDTO();
        request.setOrder(1);
        request.setName("T1");
        request.setCapacity(100);
        request.setConditionTypes(Set.of(ConditionType.CAPACITY));
        
        DynamicRatesPriceDTO ratePrice = new DynamicRatesPriceDTO();
        ratePrice.setId(1L);
        ratePrice.setPrice(10.0);
        request.setDynamicRatesPriceDTO(List.of(ratePrice));

        sessionDynamicPriceService.createOrUpdateSessionDynamicPrices(sessionId, idPriceZone, Arrays.asList(request));

        ArgumentCaptor<SessionConfig> configCaptor = ArgumentCaptor.forClass(SessionConfig.class);
        verify(sessionConfigCouchDao, times(2)).upsert(eq(String.valueOf(sessionId)), configCaptor.capture());
        
        SessionConfig finalConfig = configCaptor.getAllValues().get(1);
        List<DynamicPrice> savedPrices = finalConfig.getSessionDynamicPriceConfig()
                .getDynamicPriceZone().get(0).getDynamicPrices();
        
        assertEquals(2, savedPrices.size());
        assertEquals(0, savedPrices.get(0).getOrder());
        assertEquals("T0", savedPrices.get(0).getName());
        assertEquals(1, savedPrices.get(1).getOrder());
        assertEquals("T1", savedPrices.get(1).getName());
    }
}
