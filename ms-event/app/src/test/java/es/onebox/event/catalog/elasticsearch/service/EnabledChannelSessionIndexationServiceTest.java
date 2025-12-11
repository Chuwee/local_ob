package es.onebox.event.catalog.elasticsearch.service;

import es.onebox.event.attendants.AttendantsConfigService;
import es.onebox.event.attendants.dto.EventAttendantsConfigDTO;
import es.onebox.event.attendants.dto.SessionAttendantsConfigDTO;
import es.onebox.event.catalog.dao.SBSessionsCouchDao;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.dto.venue.container.VenueQuota;
import es.onebox.event.catalog.elasticsearch.context.BaseIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionPriceZones;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionAgencyElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.SessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.ticket.dto.SessionWithQuotasDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationByPriceZoneDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationsSearchRequest;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionPriceZoneOccupationDTO;
import es.onebox.event.datasources.ms.ticket.enums.CapacityType;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.ticket.repository.SessionOccupationRepository;
import es.onebox.event.datasources.ms.ticket.repository.SessionRepository;
import es.onebox.event.events.dao.EventCommunicationElementDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.domain.VenueTemplateType;
import es.onebox.event.events.enums.ChannelEventStatus;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.ChannelSurchargesTaxesOrigin;
import es.onebox.event.events.enums.EventChannelSurchargesTaxesOrigin;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.event.secondarymarket.dao.EventSecondaryMarketConfigCouchDao;
import es.onebox.event.secondarymarket.dao.SessionSecondaryMarketConfigCouchDao;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.event.sessions.dao.record.PresaleRecord;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class EnabledChannelSessionIndexationServiceTest {

    private static final Long EVENT_ID = 88L;
    private static final Long SESSION_ID = 5621L;
    private static final Long CHANNEL_ID = 125L;
    private static final Long PRICE_ZONE_1 = 3L;
    private static final Long PRICE_ZONE_2 = 5L;
    private static final Long QUOTA_1 = 12L;
    private static final Long QUOTA_2 = 23L;
    public static final Integer VALIDATOR_ID = 1;
    public static final Integer VALIDATOR_TYPE = 1;
    public static final Integer RANGE_TYPE_ALL = 0;
    public static final Integer RANGE_TYPE_DATE = 1;
    private static final Integer EVENT_CHANNEL_ID = 123;

    public static final Long CHANNEL_CS_TAX_ID = 123L;
    public static final String CHANNEL_CS_TAX_NAME = "Channel Channel Surcharge Tax";
    public static final Double CHANNEL_CS_TAX_VALUE = 15.42;
    public static final Long EVENT_CHANNEL_CS_TAX_ID = 456L;
    public static final String EVENT_CHANNEL_CS_TAX_NAME = "Event Channel Channel Surcharge Tax";
    public static final Double EVENT_CHANNEL_CS_TAX_VALUE = 20.18;
    public static final Long SESSION_CS_TAX_ID = 789L;
    public static final String SESSION_CS_TAX_NAME = "Session Channel Surcharge Tax";
    public static final Double SESSION_CS_TAX_VALUE = 18.21;
    
    @Mock
    private SessionDao sessionDao;
    @Mock
    private SessionConfigCouchDao sessionConfigCouchDao;
    @Mock
    private SessionRateDao sessionRateDao;
    @Mock
    private SeasonSessionDao seasonSessionDao;
    @Mock
    private EventCommunicationElementDao eventCommElemDao;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SessionOccupationRepository sessionOccupationRepository;
    @Mock
    private ChannelSessionAgencyElasticDao channelAgencySessionElasticDao;
    @Mock
    private ChannelSessionElasticDao channelSessionElasticDao;
    @Mock
    private AttendantsConfigService attendantsConfigService;
    @Mock
    private SessionElasticDao sessionElasticDao;
    @Mock
    private SBSessionsCouchDao sbSessionsCouchDao;
    @Mock
    private SessionSecondaryMarketConfigCouchDao sessionSecondaryMarketConfigCouchDao;
    @Mock
    private EventSecondaryMarketConfigCouchDao eventSecondaryMarketConfigCouchDao;

    private ChannelSessionIndexationService channelSessionIndexationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        channelSessionIndexationService = new ChannelSessionIndexationService(sessionElasticDao, sessionConfigCouchDao,
                sessionRateDao, seasonSessionDao, eventCommElemDao, sessionRepository, sessionOccupationRepository,
                channelSessionElasticDao, attendantsConfigService, sbSessionsCouchDao,
                sessionSecondaryMarketConfigCouchDao, eventSecondaryMarketConfigCouchDao, channelAgencySessionElasticDao);
    }

    @Test
    void testEventPrepareChannelSessions() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyChannelSessions(ctx, PRICE_ZONE_2);
    }

    @Test
    void testEventPrepareChannelSessionsWhenNoSession() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        ctx.setSessions(Collections.emptyList());
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyNoChannelSessions(ctx);
    }

    @Test
    void testEventPrepareChannelSessionsWhenNoChannelEvent() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        ctx.setChannelEvents(Collections.emptyList());
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyNoChannelSessions(ctx);
    }

    @Test
    void testEventPrepareChannelSessionsWhenNoEventChannel() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        ctx.setEventChannels(Collections.emptyList());
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyNoChannelSessions(ctx);
    }

    @Test
    void testEventPrepareChannelSessionsWhenEventIsNotReady() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.PLANNED);
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyNoChannelSessions(ctx);
    }

    @Test
    void testEventPrepareChannelSessionsWhenSessionIsNotReady() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        ctx.setSessions(Collections.singletonList(prepareSession(SessionStatus.PLANNED, true, false)));
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyNoChannelSessions(ctx);
    }

    @Test
    void testEventPrepareChannelSessionsWhenChannelIsNotAccepted() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        ctx.setChannelEvents(Collections.singletonList(prepareChannelEvent(ChannelEventStatus.REQUESTED, true)));
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyNoChannelSessions(ctx);
    }

    @Test
    void testIndexChannelSessionsWhenRestrictedByQuotas() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        ctx.setQuotasByChannel(Collections.singletonMap(CHANNEL_ID, Collections.singletonList(0L)));
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyNoChannelSessions(ctx);
    }

    @Test
    void testIndexChannelSessionsWhenNoRestrictedByQuotas() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        ctx.setQuotasByChannel(Collections.singletonMap(CHANNEL_ID, Collections.singletonList(QUOTA_1)));
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyChannelSessions(ctx, PRICE_ZONE_1);
    }

    @Test
    void testIndexChannelSessionsWhenNoRestrictedByQuotasAndPresales() {
        EventIndexationContext ctx = prepareEventContextWithPresaleConfig(true);
        ctx.setQuotasByChannel(Collections.singletonMap(CHANNEL_ID, Collections.singletonList(QUOTA_1)));
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyChannelSessions(ctx, PRICE_ZONE_1);
        assertNotNull(ctx.getChannelSessionsToIndex().get(0).getPresales());
        assertEquals(Boolean.TRUE, ctx.getChannelSessionsToIndex().get(0).getPresales());
    }

    @Test
    void testIndexChannelSessionsWhenNoRestrictedByQuotasAndNotPresales() {
        EventIndexationContext ctx = prepareEventContextWithPresaleConfig(false);
        ctx.setQuotasByChannel(Collections.singletonMap(CHANNEL_ID, Collections.singletonList(QUOTA_1)));
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyChannelSessions(ctx, PRICE_ZONE_1);
        assertNull(ctx.getChannelSessionsToIndex().get(0).getPresales());
    }

    @Test
    void testIndexChannelSessionsWhenSessionNoPublished() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        ctx.setSessions(Collections.singletonList(prepareSession(SessionStatus.READY, false, false)));
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyNoChannelSessions(ctx);
    }

    @Test
    void testIndexChannelSessionsWhenSessionIsPreview() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        ctx.setSessions(Collections.singletonList(prepareSession(SessionStatus.READY, true, true)));
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyNoChannelSessions(ctx);
    }

    @Test
    void testIndexChannelSessionsWhenChannelEventNoPublished() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        ctx.setChannelEvents(Collections.singletonList(prepareChannelEvent(ChannelEventStatus.ACCEPTED, false)));
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyNoChannelSessions(ctx);
    }

    @Test
    void testOccupationPrepareChannelSessions() {
        OccupationIndexationContext ctx = prepareOccupationContext(SESSION_ID);
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyChannelSessions(ctx, PRICE_ZONE_2);
    }

    @Test
    void testOccupationPrepareChannelSessionsWhenSessionNotFound() {
        OccupationIndexationContext ctx = prepareOccupationContext(0L);
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyNoChannelSessions(ctx);
    }

    @Test
    void testOccupationPrepareChannelSessionsWhenNoSpecificSession() {
        OccupationIndexationContext ctx = prepareOccupationContext(null);
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyChannelSessions(ctx, PRICE_ZONE_2);
    }

    @Test
    void testOccupationPrepareChannelSessionsWhenChannelHasQuotas() {
        OccupationIndexationContext ctx = prepareOccupationContext(SESSION_ID);
        ctx.setQuotasByChannel(Collections.singletonMap(CHANNEL_ID, Collections.singletonList(QUOTA_1)));
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyChannelSessions(ctx, PRICE_ZONE_1);
    }

    @Test
    void testEventPrepareChannelSessions_channelSurchargesTaxes() {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        assertTrue(ctx.getChannelSessionsToIndex().get(0).getChannelSurchargesTaxes().isEmpty());

        ctx = prepareEventContext(EventStatus.READY);
        ctx.getEventChannel(CHANNEL_ID.intValue()).get().setSurchargestaxesorigin(EventChannelSurchargesTaxesOrigin.CHANNEL.getId());
        ctx.getChannels().get(CHANNEL_ID).setSurchargesTaxesOrigin(ChannelSurchargesTaxesOrigin.EVENT.getId());
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyChannelSurchargesTaxes(ctx, SESSION_CS_TAX_ID, SESSION_CS_TAX_NAME, SESSION_CS_TAX_VALUE);

        ctx = prepareEventContext(EventStatus.READY);
        ctx.getEventChannel(CHANNEL_ID.intValue()).get().setSurchargestaxesorigin(EventChannelSurchargesTaxesOrigin.CHANNEL.getId());
        ctx.getChannels().get(CHANNEL_ID).setSurchargesTaxesOrigin(ChannelSurchargesTaxesOrigin.CHANNEL.getId());
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyChannelSurchargesTaxes(ctx, CHANNEL_CS_TAX_ID, CHANNEL_CS_TAX_NAME, CHANNEL_CS_TAX_VALUE);

        ctx = prepareEventContext(EventStatus.READY);
        ctx.getEventChannel(CHANNEL_ID.intValue()).get().setSurchargestaxesorigin(EventChannelSurchargesTaxesOrigin.EVENT.getId());
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyChannelSurchargesTaxes(ctx, SESSION_CS_TAX_ID, SESSION_CS_TAX_NAME, SESSION_CS_TAX_VALUE);

        ctx = prepareEventContext(EventStatus.READY);
        ctx.getEventChannel(CHANNEL_ID.intValue()).get().setSurchargestaxesorigin(EventChannelSurchargesTaxesOrigin.SALE_REQUEST.getId());
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        verifyChannelSurchargesTaxes(ctx, EVENT_CHANNEL_CS_TAX_ID, EVENT_CHANNEL_CS_TAX_NAME, EVENT_CHANNEL_CS_TAX_VALUE);
    }

    private static void verifyChannelSurchargesTaxes(EventIndexationContext ctx, Long id, String name, Double value) {
        List<ChannelTaxInfo> channelSurchargesTaxes = ctx.getChannelSessionsToIndex().get(0).getChannelSurchargesTaxes();
        assertEquals(id.intValue(), channelSurchargesTaxes.get(0).getId());
        assertEquals(name, channelSurchargesTaxes.get(0).getName());
        assertEquals(value, channelSurchargesTaxes.get(0).getValue());
    }

    private EventIndexationContext prepareEventContext(EventStatus status) {
        EventIndexationContext ctx = new EventIndexationContext(prepareEvent(status));
        ctx.setSessions(Collections.singletonList(prepareSession(SessionStatus.READY, true, false)));
        ctx.setChannelEvents(Collections.singletonList(prepareChannelEvent(ChannelEventStatus.ACCEPTED, true)));
        ctx.setEventChannels(Collections.singletonList(prepareEventChannel()));
        ctx.setQuotasByChannel(Collections.emptyMap());
        ctx.setChannels(Collections.singletonMap(CHANNEL_ID, prepareChannel()));
        ctx.setEventAttendantsConfig(prepareAttendantsConfig());
        ctx.setVenueTemplatesBySession(Collections.singletonMap(SESSION_ID, 1L));

        var venueDescriptor = new VenueDescriptor();
        venueDescriptor.setVenueConfigId(1);
        venueDescriptor.setType(VenueTemplateType.DEFAULT.getId());
        var venueQuota = new VenueQuota();
        venueQuota.setDefaultQuota(Boolean.TRUE);
        venueQuota.setId(1);
        venueDescriptor.setQuotas(Collections.singletonList(venueQuota));
        ctx.setVenueDescriptor(Collections.singletonMap(1, venueDescriptor));

        var entity = new EntityDTO();
        entity.setId(1);
        ctx.setEntity(entity);
        mockSessionAttendantsConfig();
        mockQuotas();
        mockOccupation(Collections.emptyList(), Arrays.asList(preparePriceZoneOccupation(PRICE_ZONE_1, false, false),
                preparePriceZoneOccupation(PRICE_ZONE_2, true, false)));
        mockOccupation(Collections.singletonList(QUOTA_1), Arrays.asList(preparePriceZoneOccupation(PRICE_ZONE_1, false, true),
                preparePriceZoneOccupation(PRICE_ZONE_2, null, false)));
        return ctx;
    }

    private EventIndexationContext prepareEventContextWithPresaleConfig(boolean withValidPresales) {
        EventIndexationContext ctx = prepareEventContext(EventStatus.READY);
        Map<Long, List<PresaleRecord>> sessionPresleConfigMap = new HashMap<>();
        List<PresaleRecord> presalesRecords = new ArrayList<>();
        ZonedDateTime zdt = ZonedDateTime.now();

        if (withValidPresales) {
            presalesRecords.add(createPresale(1, 1, RANGE_TYPE_DATE, zdt.minusDays(1), zdt.plusDays(1), List.of(CHANNEL_ID, 999L))); //Valid
            presalesRecords.add(createPresale(4, 1, RANGE_TYPE_ALL, null, null, List.of(CHANNEL_ID))); //Valid
        }
        presalesRecords.add(createPresale(2, 1, RANGE_TYPE_DATE, zdt.minusDays(1), zdt.plusDays(1), List.of(8888L))); //Not valid for channelID
        presalesRecords.add(createPresale(3, 0, RANGE_TYPE_DATE, zdt.minusDays(1), zdt.plusDays(1), List.of(CHANNEL_ID))); //State -> disabled
        presalesRecords.add(createPresale(5, 0, RANGE_TYPE_ALL, null, null, List.of(CHANNEL_ID))); //Not Valid -> status Disabled
        presalesRecords.add(createPresale(6, 1, RANGE_TYPE_ALL, null, null, List.of(8888L))); //Not Valid for channelId
        presalesRecords.add(createPresale(7, 1, RANGE_TYPE_ALL, null, null, null)); //Not Valid for channelId
        presalesRecords.add(createPresale(8, 1, RANGE_TYPE_DATE, zdt.minusDays(1), zdt.plusDays(1), List.of(999L))); //Not Valid for channelId
        sessionPresleConfigMap.put(SESSION_ID, presalesRecords);
        ctx.setSessionPresaleConfigMap(sessionPresleConfigMap);
        return ctx;
    }

    private PresaleRecord createPresale(int id, int state, int rangeType, ZonedDateTime from, ZonedDateTime to, List<Long> channels) {
        PresaleRecord result = new PresaleRecord();
        result.setEstado(state);
        result.setIdsesion(SESSION_ID.intValue());
        result.setIdpreventa(id);
        result.setIdvalidador(VALIDATOR_ID);
        result.setTipovalidador(VALIDATOR_TYPE);
        result.setTiporangovalidacion(rangeType);
        if (from != null && to != null) {
            result.setFechafinpreventa(Timestamp.from(to.toInstant()));
            result.setFechainiciopreventa(Timestamp.from(from.toInstant()));
        }
        if (CollectionUtils.isNotEmpty(channels)) {
            result.setChannelIds(channels);
        }
        return result;
    }

    private OccupationIndexationContext prepareOccupationContext(Long sessionId) {
        OccupationIndexationContext ctx = new OccupationIndexationContext(prepareEvent(EventStatus.READY), sessionId);
        ctx.setQuotasByChannel(Collections.emptyMap());
        ctx.setChannels(Collections.singletonMap(CHANNEL_ID, prepareChannel()));

        mockChannelSessionBySession();
        mockChannelSessionByEvent();
        mockOccupation(Collections.emptyList(), Arrays.asList(preparePriceZoneOccupation(PRICE_ZONE_1, false, false),
                preparePriceZoneOccupation(PRICE_ZONE_2, true, false)));
        mockOccupation(Collections.singletonList(QUOTA_1), Arrays.asList(preparePriceZoneOccupation(PRICE_ZONE_1, false, true),
                preparePriceZoneOccupation(PRICE_ZONE_2, null, false)));

        return ctx;
    }

    private CpanelEventoRecord prepareEvent(EventStatus status) {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setIdevento(EVENT_ID.intValue());
        event.setEstado(status.getId());
        event.setTipoevento(EventType.NORMAL.getId());
        return event;
    }

    private SessionForCatalogRecord prepareSession(SessionStatus status, boolean published, boolean preview) {
        SessionForCatalogRecord session = new SessionForCatalogRecord();
        session.setIdsesion(SESSION_ID.intValue());
        session.setIdevento(EVENT_ID.intValue());
        session.setEstado(status.getId());
        session.setPublicado((byte) (published ? 1 : 0));
        session.setIspreview(preview);
        session.setSurchargesTaxId(SESSION_CS_TAX_ID);
        session.setSurchargesTaxName(SESSION_CS_TAX_NAME);
        session.setSurchargesTaxValue(SESSION_CS_TAX_VALUE);
        return session;
    }

    private CpanelCanalEventoRecord prepareChannelEvent(ChannelEventStatus status, boolean published) {
        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdevento(EVENT_ID.intValue());
        channelEvent.setIdcanal(CHANNEL_ID.intValue());
        channelEvent.setEstadorelacion(status.getId());
        channelEvent.setPublicado((byte) (published ? 1 : 0));

        return channelEvent;
    }

    private EventChannelForCatalogRecord prepareEventChannel() {
        EventChannelForCatalogRecord eventChannel = new EventChannelForCatalogRecord();
        eventChannel.setIdeventocanal(EVENT_CHANNEL_ID);
        eventChannel.setIdevento(EVENT_ID.intValue());
        eventChannel.setIdcanal(CHANNEL_ID.intValue());
        ChannelTaxInfo channelTaxInfo = new ChannelTaxInfo();
        channelTaxInfo.setId(EVENT_CHANNEL_CS_TAX_ID);
        channelTaxInfo.setName(EVENT_CHANNEL_CS_TAX_NAME);
        channelTaxInfo.setValue(EVENT_CHANNEL_CS_TAX_VALUE);
        eventChannel.setSurchargesTaxes(List.of(channelTaxInfo));
        return eventChannel;
    }

    private ChannelInfo prepareChannel() {
        ChannelTaxInfo channelTaxInfo = new ChannelTaxInfo();
        channelTaxInfo.setId(CHANNEL_CS_TAX_ID);
        channelTaxInfo.setName(CHANNEL_CS_TAX_NAME);
        channelTaxInfo.setValue(CHANNEL_CS_TAX_VALUE);
        ChannelInfo channelInfo = new ChannelInfo(CHANNEL_ID, "", 1L, ChannelSubtype.PORTAL_WEB.getIdSubtipo(), null);
        channelInfo.setSurchargesTaxes(List.of(channelTaxInfo));
        return channelInfo;
    }

    private EventAttendantsConfigDTO prepareAttendantsConfig() {
        EventAttendantsConfigDTO eventAttendantsConfig = new EventAttendantsConfigDTO();
        eventAttendantsConfig.setEventId(EVENT_ID);
        eventAttendantsConfig.setActive(true);
        eventAttendantsConfig.setAutofill(false);
        eventAttendantsConfig.setAllChannelsActive(true);
        return eventAttendantsConfig;
    }

    private SessionPriceZoneOccupationDTO preparePriceZoneOccupation(Long priceZoneId, Boolean available, boolean unlimited) {
        SessionPriceZoneOccupationDTO priceZoneOccupation = new SessionPriceZoneOccupationDTO();
        priceZoneOccupation.setPriceZoneId(priceZoneId);
        priceZoneOccupation.setUnlimited(unlimited);
        priceZoneOccupation.setStatus(available == null ? Collections.emptyMap() : Collections.singletonMap(TicketStatus.AVAILABLE, available ? 1L : 0L));
        return priceZoneOccupation;
    }

    private ChannelSessionData prepareChannelSession() {
        ChannelSessionData channelSessionData = new ChannelSessionData();
        ChannelSession channelSession = new ChannelSession();
        channelSession.setSessionId(SESSION_ID);
        channelSession.setChannelId(CHANNEL_ID);
        channelSession.setEventId(EVENT_ID);
        channelSessionData.setChannelSession(channelSession);

        return channelSessionData;
    }

    private Map<Integer, List<CpanelImpuestoRecord>> prepareEventChannelChannelSurchargesTaxes() {
        return Collections.singletonMap(EVENT_CHANNEL_ID, buildImpuestoRecords(EVENT_CHANNEL_CS_TAX_ID, EVENT_CHANNEL_CS_TAX_NAME, EVENT_CHANNEL_CS_TAX_VALUE));
    }

    private Map<Integer, List<CpanelImpuestoRecord>> prepareChannelChannelSurchargesTaxes() {
        return Collections.singletonMap(CHANNEL_ID.intValue(), buildImpuestoRecords(CHANNEL_CS_TAX_ID, CHANNEL_CS_TAX_NAME, CHANNEL_CS_TAX_VALUE));
    }

    private List<CpanelImpuestoRecord> buildImpuestoRecords(Long id, String name, Double value) {
        CpanelImpuestoRecord record = new CpanelImpuestoRecord();
        record.setIdimpuesto(id.intValue());
        record.setNombre(name);
        record.setValor(value);
        return List.of(record);
    }

    private void mockSessionAttendantsConfig() {
        SessionAttendantsConfigDTO sessionAttendantsConfig = new SessionAttendantsConfigDTO();
        sessionAttendantsConfig.setSessionId(SESSION_ID);
        sessionAttendantsConfig.setActive(true);
        sessionAttendantsConfig.setAutofill(false);
        sessionAttendantsConfig.setAllChannelsActive(true);
        when(attendantsConfigService.getSessionAttendantsConfig(SESSION_ID)).thenReturn(sessionAttendantsConfig);
    }

    private void mockQuotas() {
        when(sessionRepository.getSessionQuotas(SESSION_ID, CapacityType.NORMAL, false)).thenReturn(Arrays.asList(QUOTA_1, QUOTA_2));
    }

    private void mockOccupation(List<Long> quotas, List<SessionPriceZoneOccupationDTO> occupation) {
        SessionWithQuotasDTO sessionWithQuotas = new SessionWithQuotasDTO();
        sessionWithQuotas.setSessionId(SESSION_ID);
        sessionWithQuotas.setQuotas(quotas);
        SessionOccupationByPriceZoneDTO sessionOccupation = new SessionOccupationByPriceZoneDTO();
        sessionOccupation.setSession(sessionWithQuotas);
        sessionOccupation.setOccupation(occupation);
        SessionOccupationsSearchRequest request = new SessionOccupationsSearchRequest();
        request.setEventType(EventType.NORMAL);
        request.setSessions(Collections.singletonList(sessionWithQuotas));


        when(sessionOccupationRepository.searchOccupationsByPriceZones(request)).thenReturn(Collections.singletonList(sessionOccupation));
        SessionData sessionData = new SessionData();
        Session session = new Session();
        session.setVenueTemplateType(VenueTemplateType.DEFAULT.getId());
        sessionData.setSession(session);
        when(sessionElasticDao.get(SESSION_ID, EVENT_ID)).thenReturn(sessionData);
    }

    private void mockChannelSessionBySession() {
        when(channelSessionElasticDao.getBySessionId(SESSION_ID, EVENT_ID)).thenReturn(Collections.singletonList(prepareChannelSession()));
    }

    private void mockChannelSessionByEvent() {
        when(channelSessionElasticDao.getByEventId(EVENT_ID)).thenReturn(Collections.singletonList(prepareChannelSession()));
    }

    private <T extends ChannelSessionPriceZones, E extends ChannelSessionPriceZones> void verifyNoChannelSessions(BaseIndexationContext<T, E> ctx) {
        assertNotNull(ctx);
        List<T> channelSessions = ctx.getChannelSessionsToIndex();
        assertNotNull(channelSessions);
        var indexableSesions = channelSessions.stream().filter(ChannelSessionPriceZones::getMustBeIndexed).collect(Collectors.toList());
        assertTrue(indexableSesions.isEmpty());
    }

    private <T extends ChannelSessionPriceZones, E extends ChannelSessionPriceZones> void verifyChannelSessions(BaseIndexationContext<T, E> ctx, Long priceZoneAvailable) {
        assertNotNull(ctx);
        List<T> channelSessions = ctx.getChannelSessionsToIndex();
        assertNotNull(channelSessions);
        assertEquals(1, channelSessions.size());
        T channelSession = channelSessions.get(0);
        assertNotNull(channelSession);
        assertEquals(SESSION_ID, channelSession.getSessionId());
        assertEquals(CHANNEL_ID, channelSession.getChannelId());
        assertEquals(Arrays.asList(PRICE_ZONE_1, PRICE_ZONE_2), channelSession.getPriceZones());
        assertEquals(Collections.singletonList(priceZoneAvailable), channelSession.getPriceZonesWithAvailability());
    }
}
