package es.onebox.event.sessions.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.scheduler.TaskInfo;
import es.onebox.core.scheduler.TaskService;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.DateUtils;
import es.onebox.dal.dto.couch.enums.OrderState;
import es.onebox.event.attendants.AttendantsConfigService;
import es.onebox.event.attendants.domain.SessionAttendantsConfig;
import es.onebox.event.attendants.dto.SessionAttendantsConfigDTO;
import es.onebox.event.common.ExternalDataConstants;
import es.onebox.event.common.amqp.channelsuggestionscleanup.ChannelSuggestionsCleanUpService;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.common.request.PriceTypeBaseFilter;
import es.onebox.event.common.services.CommonRatesGroup;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.common.utils.CronUtils;
import es.onebox.event.datasources.integration.avet.config.dto.SessionMatch;
import es.onebox.event.datasources.integration.avet.config.repository.IntAvetConfigRepository;
import es.onebox.event.datasources.integration.dispatcher.dto.ExternalSession;
import es.onebox.event.datasources.integration.dispatcher.repository.IntAvetConnectorRepository;
import es.onebox.event.datasources.integration.dispatcher.repository.IntDispatcherRepository;
import es.onebox.event.datasources.ms.crm.dto.SubscriptionDTO;
import es.onebox.event.datasources.ms.crm.repository.SubscriptionsRepository;
import es.onebox.event.datasources.ms.entity.dto.ExternalBarcodeEntityConfigDTO;
import es.onebox.event.datasources.ms.entity.dto.ExternalEntityConfig;
import es.onebox.event.datasources.ms.entity.dto.InvoicePrefix;
import es.onebox.event.datasources.ms.entity.dto.ProducerInvoiceProvider;
import es.onebox.event.datasources.ms.entity.dto.RequestStatus;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.datasources.ms.ticket.repository.SessionRepository;
import es.onebox.event.datasources.ms.venue.dto.VenueTemplate;
import es.onebox.event.datasources.ms.venue.dto.VenueTemplateType;
import es.onebox.event.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.event.events.amqp.whitelistgeneration.WhitelistGenerationService;
import es.onebox.event.events.converter.RateConverter;
import es.onebox.event.events.converter.RateGroupConverter;
import es.onebox.event.events.dao.EventAvetConfigCouchDao;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.RateGroupDao;
import es.onebox.event.events.dao.record.RateGroupRecord;
import es.onebox.event.events.dao.record.RateGroupSessionRecord;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.dto.ExternalBarcodeEventConfigDTO;
import es.onebox.event.events.dto.RateGroupDTO;
import es.onebox.event.events.dto.RateZoneDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.events.enums.SessionState;
import es.onebox.event.events.prices.EventPricesDao;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.events.service.EventExternalBarcodeConfigService;
import es.onebox.event.events.service.EventRateService;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.loyaltypoints.sessions.converter.SessionLoyaltyPointsConverter;
import es.onebox.event.loyaltypoints.sessions.dao.SessionLoyaltyPointsConfigCouchDao;
import es.onebox.event.loyaltypoints.sessions.domain.SessionLoyaltyPointsConfig;
import es.onebox.event.loyaltypoints.sessions.dto.UpdateSessionLoyaltyPointsConfigDTO;
import es.onebox.event.secondarymarket.converter.SessionSecondaryMarketConverter;
import es.onebox.event.secondarymarket.domain.SessionSecondaryMarketDates;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigExtended;
import es.onebox.event.secondarymarket.service.SessionSecondaryMarketConfigService;
import es.onebox.event.secondarymarket.utils.SecondaryMarketUtils;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.amqp.avetavailability.AvetAvailabilityMatchScheduleService;
import es.onebox.event.sessions.amqp.seatgeneration.GenerateSeatService;
import es.onebox.event.sessions.amqp.seatremove.SeatRemoveService;
import es.onebox.event.sessions.amqp.sessionclone.SessionCloneService;
import es.onebox.event.sessions.converter.PriceTypeConverter;
import es.onebox.event.sessions.converter.SessionConverter;
import es.onebox.event.sessions.converter.SessionPreSaleConfigConverter;
import es.onebox.event.sessions.dao.InvitationCounterCouchDao;
import es.onebox.event.sessions.dao.PresaleChannelDao;
import es.onebox.event.sessions.dao.PresaleCustomTypeDao;
import es.onebox.event.sessions.dao.PresaleDao;
import es.onebox.event.sessions.dao.PresaleLoyaltyProgramDao;
import es.onebox.event.sessions.dao.PriceTypeLabelDao;
import es.onebox.event.sessions.dao.PriceTypeLabelSessionDao;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.SessionGroupDao;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.event.sessions.dao.SessionTaxesDao;
import es.onebox.event.sessions.dao.TaxDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dao.record.SessionsGroupDataRecord;
import es.onebox.event.sessions.dao.record.ZonaPreciosConfigRecord;
import es.onebox.event.sessions.domain.SessionRate;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionDynamicPriceConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionExternalConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionPresalesConfig;
import es.onebox.event.sessions.domain.sessionconfig.StreamingVendorConfig;
import es.onebox.event.sessions.dto.AccessHourType;
import es.onebox.event.sessions.dto.CloneSessionDTO;
import es.onebox.event.sessions.dto.CreateSessionDTO;
import es.onebox.event.sessions.dto.CreateSessionLoyaltyPointsConfigDTO;
import es.onebox.event.sessions.dto.ExternalBarcodeSessionConfigDTO;
import es.onebox.event.sessions.dto.GenerationStatusSessionRequestDTO;
import es.onebox.event.sessions.dto.LinkedSessionDTO;
import es.onebox.event.sessions.dto.PresalesLinkMode;
import es.onebox.event.sessions.dto.PresalesRedirectionPolicyDTO;
import es.onebox.event.sessions.dto.PriceTypeDTO;
import es.onebox.event.sessions.dto.PriceTypeRequestDTO;
import es.onebox.event.sessions.dto.PriceTypesDTO;
import es.onebox.event.sessions.dto.RateDTO;
import es.onebox.event.sessions.dto.SeatDeleteStatus;
import es.onebox.event.sessions.dto.SessionConfigDTO;
import es.onebox.event.sessions.dto.SessionCounterDTO;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionExternalConfigDTO;
import es.onebox.event.sessions.dto.SessionGenerationStatus;
import es.onebox.event.sessions.dto.SessionGroupConfigDTO;
import es.onebox.event.sessions.dto.SessionPackDTO;
import es.onebox.event.sessions.dto.SessionPackDateDTO;
import es.onebox.event.sessions.dto.SessionRatesDTO;
import es.onebox.event.sessions.dto.SessionSalesType;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.dto.SessionStreamingDTO;
import es.onebox.event.sessions.dto.UpdateSessionTaxDTO;
import es.onebox.event.sessions.dto.SessionsDTO;
import es.onebox.event.sessions.dto.SessionsGroupsDTO;
import es.onebox.event.sessions.dto.StreamingVendor;
import es.onebox.event.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.event.sessions.dto.UpdateSessionsRequestDTO;
import es.onebox.event.sessions.enums.SessionTaxesType;
import es.onebox.event.sessions.enums.SessionType;
import es.onebox.event.sessions.enums.SessionVirtualQueueVersion;
import es.onebox.event.sessions.quartz.SessionStreamingEmailJob;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.sessions.request.SessionsGroupsSearchFilter;
import es.onebox.event.sessions.utils.SessionValidator;
import es.onebox.event.venues.dao.BlockingReasonDao;
import es.onebox.event.venues.dao.EntityVenueTemplateDao;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.event.venues.dao.VenueTemplateDao;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelAsignacionZonaPreciosRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigSesionGruposRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecintoConfigRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPreventaLoyaltyProgramRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPreventaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRazonBloqueoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSessionTaxesRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPrecioEtiquetaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPrecioEtiquetaSesionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static es.onebox.event.common.utils.ConverterUtils.updateField;
import static es.onebox.event.exception.MsEventErrorCode.INVALID_NAME_FORMAT;
import static es.onebox.event.exception.MsEventErrorCode.INVALID_VENUE_TEMPLATE;
import static es.onebox.event.exception.MsEventErrorCode.SEASON_LOCK;
import static es.onebox.event.exception.MsEventSessionErrorCode.BULK_SEASON_UPDATE_FORBIDEN;
import static es.onebox.event.exception.MsEventSessionErrorCode.SESSION_NOT_MATCH_EVENT;
import static es.onebox.event.exception.MsEventSessionErrorCode.SESSION_WITH_BOOKED_SEAT;
import static es.onebox.event.sessions.quartz.SessionStreamingEmailJob.SESSION_STREAMING_EMAIL;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@Service
public class SessionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionService.class);

    private static final Byte ONE = (byte) 1;

    private static final int BULK_SESSION_CREATE_LIMIT = 1000;
    private static final int DEFAULT_STREAMING_SCHEDULER_BEFORE_START = 15;
    private static final int DEFAULT_GROUP_MIN = 0;
    private static final String SMART_BOOKING_DEFAULT_TEMPLATE_NAME = "Smart booking template";
    private static final String CHANNEL_SUGGESTIONS_CLEAN_UP = "[CHANNEL SUGGESTIONS CLEAN UP]";

    private final SessionRateDao sessionRateDao;
    private final EventDao eventDao;
    private final PriceTypeConfigDao priceZoneConfigDao;
    private final RateDao rateDao;
    private final RateGroupDao rateGroupDao;
    private final SessionDao sessionDao;
    private final SeasonSessionDao seasonSessionDao;
    private final EntityVenueTemplateDao entityVenueTemplateDao;
    private final SessionConfigCouchDao sessionConfigCouchDao;
    private final EventAvetConfigCouchDao eventAvetConfigCouchDao;
    private final EventConfigCouchDao eventConfigCouchDao;
    private final BlockingReasonDao blockingReasonDao;
    private final TaxDao taxDao;
    private final GenerateSeatService generateSeatService;
    private final SessionCloneService sessionCloneService;
    private final RefreshDataService refreshDataService;
    private final AttendantsConfigService attendantsConfigService;
    private final SeatRemoveService seatRemoveService;
    private final AvetAvailabilityMatchScheduleService avetAvailabilityMatchScheduleService;
    private final SessionRepository sessionRepository;
    private final IntAvetConfigRepository intAvetConfigRepository;
    private final OrdersRepository ordersRepository;
    private final TaskService taskService;
    private final SessionCommunicationElementsService sessionCommunicationElementService;
    private final EventRateService eventRateService;
    private final VenuesRepository venuesRepository;
    private final SessionValidationHelper sessionValidationHelper;
    private final PriceTypeLabelSessionDao priceTypeLabelSessionDao;
    private final PriceTypeLabelDao priceTypeLabelDao;
    private final EntitiesRepository entitiesRepository;
    private final SubscriptionsRepository subscriptionsRepository;
    private final SessionGroupDao sessionGroupDao;
    private final SessionRefundConditionsService sessionRefundConditionsService;
    private final EventExternalBarcodeConfigService eventExternalBarcodeConfigService;
    private final SessionExternalBarcodeConfigService sessionExternalBarcodeConfigService;
    private final VenueTemplateDao venueTemplateDao;
    private final PriceZoneAssignmentDao priceZoneAssignmentDao;
    private final WhitelistGenerationService whitelistGenerationService;
    private final WebhookService webhookService;
    private final EventPricesDao eventPricesDao;
    private final ChannelSuggestionsCleanUpService channelSuggestionsCleanUpService;
    private final PresaleDao presaleDao;
    private final PresaleChannelDao presaleChannelDao;
    private final IntAvetConnectorRepository intAvetConnectorRepository;
    private final SessionSecondaryMarketConfigService sessionSecondaryMarketConfigService;
    private final SessionLoyaltyPointsConfigCouchDao sessionLoyaltyPointsConfigCouchDao;
    private final PresaleCustomTypeDao presaleCustomTypeDao;
    private final PresaleLoyaltyProgramDao presaleLoyaltyProgramDao;
    private final IntDispatcherRepository intDispatcherRepository;
    private final SessionTaxesDao sessionTaxesDao;
    private final InvitationCounterCouchDao invitationCounterCouchDao;

    @Autowired
    public SessionService(SessionRateDao sessionRateDao,
                          EventDao eventDao,
                          PriceTypeConfigDao priceZoneConfigDao,
                          RateDao rateDao,
                          RateGroupDao rateGroupDao,
                          SessionDao sessionDao,
                          SeasonSessionDao seasonSessionDao,
                          EntityVenueTemplateDao entityVenueTemplateDao,
                          SessionRepository sessionRepository,
                          GenerateSeatService generateSeatService,
                          SessionCloneService sessionCloneService,
                          RefreshDataService refreshDataService,
                          OrdersRepository ordersRepository,
                          AttendantsConfigService attendantsConfigService,
                          TaskService taskService,
                          SeatRemoveService seatRemoveService,
                          AvetAvailabilityMatchScheduleService avetAvailabilityMatchScheduleService,
                          SessionConfigCouchDao sessionConfigCouchDao,
                          EventAvetConfigCouchDao eventAvetConfigCouchDao,
                          EventConfigCouchDao eventConfigCouchDao,
                          IntAvetConfigRepository intAvetConfigRepository,
                          SessionCommunicationElementsService sessionCommunicationElementService,
                          BlockingReasonDao blockingReasonDao,
                          TaxDao taxDao,
                          EventRateService eventRateService,
                          VenuesRepository venuesRepository,
                          SessionValidationHelper sessionValidationHelper,
                          PriceTypeLabelSessionDao priceTypeLabelSessionDao,
                          PriceTypeLabelDao priceTypeLabelDao,
                          SubscriptionsRepository subscriptionsRepository,
                          EntitiesRepository entitiesRepository,
                          SessionGroupDao sessionGroupDao,
                          SessionRefundConditionsService sessionRefundConditionsService,
                          EventExternalBarcodeConfigService eventExternalBarcodeConfigService,
                          SessionExternalBarcodeConfigService sessionExternalBarcodeConfigService,
                          WhitelistGenerationService whitelistGenerationService,
                          VenueTemplateDao venueTemplateDao,
                          PriceZoneAssignmentDao priceZoneAssignmentDao,
                          WebhookService webhookService,
                          EventPricesDao eventPricesDao,
                          ChannelSuggestionsCleanUpService channelSuggestionsCleanUpService,
                          PresaleDao presaleDao, PresaleChannelDao presaleChannelDao,
                          IntAvetConnectorRepository intAvetConnectorRepository,
                          SessionSecondaryMarketConfigService sessionSecondaryMarketConfigService,
                          SessionLoyaltyPointsConfigCouchDao sessionLoyaltyPointsConfigCouchDao,
                          PresaleCustomTypeDao presaleCustomTypeDao, PresaleLoyaltyProgramDao presaleLoyaltyProgramDao,
                          IntDispatcherRepository intDispatcherRepository, SessionTaxesDao sessionTaxesDao,
                          InvitationCounterCouchDao invitationCounterCouchDao) {
        this.sessionRateDao = sessionRateDao;
        this.eventDao = eventDao;
        this.priceZoneConfigDao = priceZoneConfigDao;
        this.rateDao = rateDao;
        this.rateGroupDao = rateGroupDao;
        this.sessionDao = sessionDao;
        this.seasonSessionDao = seasonSessionDao;
        this.entityVenueTemplateDao = entityVenueTemplateDao;
        this.intAvetConfigRepository = intAvetConfigRepository;
        this.blockingReasonDao = blockingReasonDao;
        this.taxDao = taxDao;
        this.sessionConfigCouchDao = sessionConfigCouchDao;
        this.eventAvetConfigCouchDao = eventAvetConfigCouchDao;
        this.eventConfigCouchDao = eventConfigCouchDao;
        this.generateSeatService = generateSeatService;
        this.refreshDataService = refreshDataService;
        this.attendantsConfigService = attendantsConfigService;
        this.seatRemoveService = seatRemoveService;
        this.sessionCloneService = sessionCloneService;
        this.avetAvailabilityMatchScheduleService = avetAvailabilityMatchScheduleService;
        this.taskService = taskService;
        this.ordersRepository = ordersRepository;
        this.sessionRepository = sessionRepository;
        this.sessionCommunicationElementService = sessionCommunicationElementService;
        this.eventRateService = eventRateService;
        this.venuesRepository = venuesRepository;
        this.sessionValidationHelper = sessionValidationHelper;
        this.priceTypeLabelSessionDao = priceTypeLabelSessionDao;
        this.priceTypeLabelDao = priceTypeLabelDao;
        this.entitiesRepository = entitiesRepository;
        this.subscriptionsRepository = subscriptionsRepository;
        this.sessionGroupDao = sessionGroupDao;
        this.sessionRefundConditionsService = sessionRefundConditionsService;
        this.eventExternalBarcodeConfigService = eventExternalBarcodeConfigService;
        this.sessionExternalBarcodeConfigService = sessionExternalBarcodeConfigService;
        this.venueTemplateDao = venueTemplateDao;
        this.priceZoneAssignmentDao = priceZoneAssignmentDao;
        this.whitelistGenerationService = whitelistGenerationService;
        this.webhookService = webhookService;
        this.eventPricesDao = eventPricesDao;
        this.channelSuggestionsCleanUpService = channelSuggestionsCleanUpService;
        this.presaleDao = presaleDao;
        this.presaleChannelDao = presaleChannelDao;
        this.intAvetConnectorRepository = intAvetConnectorRepository;
        this.sessionSecondaryMarketConfigService = sessionSecondaryMarketConfigService;
        this.sessionLoyaltyPointsConfigCouchDao = sessionLoyaltyPointsConfigCouchDao;
        this.presaleCustomTypeDao = presaleCustomTypeDao;
        this.presaleLoyaltyProgramDao = presaleLoyaltyProgramDao;
        this.intDispatcherRepository = intDispatcherRepository;
        this.sessionTaxesDao = sessionTaxesDao;
        this.invitationCounterCouchDao = invitationCounterCouchDao;
    }

    @MySQLRead
    public SessionDTO getSession(Long eventId, Long sessionId) {
        SessionRecord sessionRecord = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        SessionSecondaryMarketConfigDTO secondaryMarket =
                sessionSecondaryMarketConfigService.getSessionSecondaryMarketConfigDTOSafely(sessionRecord.getEntityId(), sessionId);

        SessionDTO sessionDTO = convertSession(sessionRecord, secondaryMarket);
        setExternalData(sessionId, sessionDTO);
        return sessionDTO;
    }

    @MySQLRead
    public SessionStatus getSessionStatus(Long sessionId) {
        SessionRecord sessionRecord = sessionDao.findSession(sessionId);
        if (sessionRecord == null) {
            return null;
        }
        return SessionStatus.byId(sessionRecord.getEstado());
    }

    @MySQLRead
    public Map<Long, SessionStatus> getSessionStatuses(List<Long> sessionIds) {
        if (sessionIds == null || sessionIds.isEmpty()) {
            return Collections.emptyMap();
        }
        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setIds(sessionIds);
        List<SessionRecord> sessions = sessionDao.findSessions(filter, null);
        return sessions.stream()
                .collect(Collectors.toMap(
                        s -> s.getIdsesion().longValue(),
                        s -> SessionStatus.byId(s.getEstado())
                ));
    }

    private void setExternalData(Long sessionId, SessionDTO sessionDTO) {
        EventConfig eventConfig = eventConfigCouchDao.get(String.valueOf(sessionDTO.getEventId()));
        if (nonNull(sessionDTO.getExternalId()) && BooleanUtils.isTrue(sessionDTO.getExternal())) {
            decorateSessionWithAvetConfig(sessionDTO, sessionId);
        } else if (nonNull(eventConfig) && Provider.SGA.equals(eventConfig.getInventoryProvider()) && !EventType.ACTIVITY.equals(sessionDTO.getEventType())) {
            decorateSessionWithSGAConfig(sessionDTO, sessionId);
        }
    }

    private void decorateSessionWithSGAConfig(SessionDTO sessionDTO, Long sessionId) {
        try {
            ExternalSession externalSession = intDispatcherRepository.getExternalSession(sessionDTO.getEntityId(), sessionDTO.getEventId(), sessionId);
            Map<String, Object> externalData = new HashMap<>();
            externalData.put(ExternalDataConstants.SESSION_MATCH_ID, externalSession.getId());
            externalData.put(ExternalDataConstants.SESSION_MATCH_NAME, externalSession.getName());
            sessionDTO.setExternalData(externalData);
        } catch (Exception e) {
            LOGGER.warn("Can not retrieve SGA external session info", e);
        }
    }

    private void decorateSessionWithAvetConfig(SessionDTO sessionDTO, Long sessionId) {
        SessionMatch avetSessionMatch = intAvetConfigRepository.getSessionMatch(sessionId);
        if (avetSessionMatch == null) {
            return;
        }

        sessionDTO.setExternalData(buildExternalDataMap(avetSessionMatch));
    }

    private static Map<String, Object> buildExternalDataMap(SessionMatch avetSessionMatch) {
        Map<String, Object> externalData = new HashMap<>();

        if (avetSessionMatch.getAvetMatchId() != null) {
            externalData.put(ExternalDataConstants.SESSION_MATCH_ID, avetSessionMatch.getAvetMatchId());
        }

        if (avetSessionMatch.getMatchName() != null) {
            externalData.put(ExternalDataConstants.SESSION_MATCH_NAME, avetSessionMatch.getMatchName());
        }

        if (avetSessionMatch.getSmartBookingContingency() != null) {
            externalData.put(ExternalDataConstants.SMART_BOOKING_CONTINGENCY, avetSessionMatch.getSmartBookingContingency());
        }

        return MapUtils.isNotEmpty(externalData) ? externalData : null;
    }

    @MySQLRead
    public List<LinkedSessionDTO> getLinkedSessions(Long eventId, Long sessionId) {
        List<LinkedSessionDTO> sessions = new ArrayList<>();
        sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        final List<Long> sessionPacksBySessionId = seasonSessionDao.findSessionPacksBySessionId(sessionId);
        if (CollectionUtils.isNotEmpty(sessionPacksBySessionId)) {
            sessions = sessionPacksBySessionId.stream()
                    .map(targetSessionId -> {
                        SessionRecord session = sessionValidationHelper.getSessionAndValidate(targetSessionId);
                        return SessionConverter.toLinkedSessionDTO(session);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return sessions;
    }

    @MySQLRead
    public SessionDTO getSessionWithoutEventId(Long sessionId) {
        SessionRecord sessionRecord = sessionValidationHelper.getSessionAndValidate(sessionId);
        SessionSecondaryMarketConfigDTO secondaryMarket =
                sessionSecondaryMarketConfigService.getSessionSecondaryMarketConfigDTOSafely(
                        sessionRecord.getEntityId(), sessionId
                );
        SessionDTO sessionDTO = convertSession(sessionRecord, secondaryMarket);
        setExternalData(sessionId, sessionDTO);
        return sessionDTO;
    }

    @MySQLRead
    public SessionPackDTO getSessionPack(Long eventId, Long sessionId) {
        SessionRecord sessionRecord = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        return convertSessionPack(sessionRecord);
    }

    @MySQLRead
    public SessionDTO getSession(Long sessionId) {
        SessionRecord sessionRecord = sessionDao.findSession(sessionId);
        if (sessionRecord == null) {
            return null;
        }
        SessionSecondaryMarketConfigDTO secondaryMarket =
                sessionSecondaryMarketConfigService.getSessionSecondaryMarketConfigDTOSafely(
                        sessionRecord.getEntityId(), sessionId
                );
        return convertSession(sessionRecord, secondaryMarket);
    }

    @MySQLRead
    public SessionsDTO searchSessions(Long eventId, SessionSearchFilter filter) {
        if (eventId != null) {
            filter.setEventId(Collections.singletonList(eventId));
        }
        return searchSessions(filter);
    }

    @MySQLRead
    public SessionsDTO searchSessions(SessionSearchFilter filter) {
        SessionsDTO sessionsDataDTO = new SessionsDTO();
        Long totalWithTimezones = null;
        SessionSearchFilter filterCopy = null;
        if(filter.getEndDate() != null
                || filter.getRangeDateFrom() != null
                || filter.getRangeDateTo() != null
                || filter.getStartDate() != null
                || filter.getStartDateFrom() != null
                || filter.getStartDateTo() != null
                || (filter.getDaysOfWeek() != null && !filter.getDaysOfWeek().isEmpty())) {
            filterCopy = new SessionSearchFilter();
            filterCopy = SerializationUtils.clone(filter);
            totalWithTimezones = sessionDao.countByFilterWithTimezones(filter);
        }

        // Find the count first, to use the total as limit for the internal query
        SessionSearchFilter countFilterCopy = SerializationUtils.clone(filterCopy);
        sessionsDataDTO.setMetadata(MetadataBuilder.build(filterCopy != null ? filterCopy : filter, sessionDao.countByFilter(countFilterCopy != null ? countFilterCopy : filter)));

        sessionsDataDTO.setData(sessionDao.findSessions(filter, totalWithTimezones).stream()
                .map(r -> SessionConverter.toSessionDTO(r, filter.getFields(), filter.getOlsonId()))
                .collect(toList()));
        if (CollectionUtils.isEmpty(filter.getFields())) {
            fillSessionPackIdsInformation(sessionsDataDTO);
        }
        if (filter.isGetQueueitInfo()) {
            Map<Long, SessionDTO> sessionMap =
                    sessionsDataDTO.getData().stream()
                            .collect(Collectors.toMap(SessionDTO::getId, Function.identity()));
            setQueueItData(sessionMap);
        }
        sessionsDataDTO.getData().forEach(sessionDTO -> {
            if (BooleanUtils.isTrue(filter.getIncludeDynamicPriceConfig())) {
                SessionDynamicPriceConfig sessionDynamicPriceConfig = sessionConfigCouchDao.findDynamicPriceBySessionId(sessionDTO.getId());
                if (sessionDynamicPriceConfig != null) {
                    sessionDTO.setUseDynamicPrices(sessionDynamicPriceConfig.getActive());
                }
            }
        });

        return sessionsDataDTO;
    }

    public SessionConfigDTO getSessionConfig(Long sessionId) {
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        return SessionConverter.toSessionConfigDTO(sessionConfig);
    }

    private void setQueueItData(Map<Long, SessionDTO> sessionMap) {
        List<SessionConfig> sessionConfigList =
                sessionConfigCouchDao.bulkGet(
                        sessionMap.keySet().stream().collect(Collectors.toCollection(ArrayList::new))
                );
        sessionConfigList.forEach(sessionConfig -> {
            SessionDTO sessionDTO = sessionMap.get(sessionConfig.getSessionId().longValue());
            fillQueueItInformation(sessionDTO, sessionConfig);
        });
    }

    private void fillSessionPackIdsInformation(SessionsDTO sessionsDataDTO) {
        Map<Boolean, List<SessionDTO>> sessionDtos = sessionsDataDTO.getData().stream()
                .collect(Collectors.partitioningBy(sessionDataDTO -> sessionDataDTO.getSessionType() == SessionType.SESSION));

        // SessionType.SESSION sessions
        if (CollectionUtils.isNotEmpty(sessionDtos.get(true))) {
            Map<Integer, List<Integer>> sessionsBySessionPackId = seasonSessionDao.findAllSessionPacksBySessionIds(
                    sessionDtos.get(true).stream()
                            .map(SessionDTO::getId)
                            .map(Long::intValue)
                            .collect(toList())
            );
            sessionDtos.get(true).forEach(
                    sessionDTO -> {
                        List<Integer> sessionIds = sessionsBySessionPackId.get(sessionDTO.getId().intValue());
                        if (sessionIds != null) {
                            sessionDTO.setSeasonIds(sessionIds.stream().map(Long::valueOf).collect(toList()));
                        }
                    }
            );
        }

        // NOT SessionType.SESSION sessions
        if (CollectionUtils.isNotEmpty(sessionDtos.get(false))) {
            Map<Integer, List<Integer>> finalSessionsBySessionPackId = seasonSessionDao.findAllSessionsBySessionPackIds(
                    sessionDtos.get(false).stream()
                            .map(SessionDTO::getId)
                            .map(Long::intValue)
                            .collect(toList())
            );
            sessionDtos.get(false).forEach(
                    sessionDTO -> {
                        List<Integer> sessionIds = finalSessionsBySessionPackId.get(sessionDTO.getId().intValue());
                        if (sessionIds != null) {
                            sessionDTO.setSessionIds(sessionIds.stream().map(Long::valueOf).collect(toList()));
                        }
                    }
            );
        }
    }

    @MySQLRead
    public SessionsGroupsDTO searchGroups(Long eventId, SessionsGroupsSearchFilter filter) {
        if (eventId != null) {
            filter.setEventId(Collections.singletonList(eventId));
        }

        List<SessionsGroupDataRecord> records = sessionDao.searchSessionsGroups(filter);
        return SessionConverter.toDTO(records, filter.getGroupType());
    }

    @MySQLRead
    public String calculateOlsonId(SessionSearchFilter filter) {

        if (filter.getVenueConfigId() != null) {
            VenueTemplate venueTemplate = venuesRepository.getVenueTemplate(filter.getVenueConfigId());
            return venueTemplate.getVenue().getTimezone().getOlsonId();
        } else if (CollectionUtils.isNotEmpty(filter.getVenueId())) {
            String olsonId = venuesRepository.getVenue(filter.getVenueId().get(0)).getTimezone().getOlsonId();
            for (int i = 1; i < filter.getVenueId().size(); i++) {
                if (!olsonId.equals(venuesRepository.getVenue(filter.getVenueId().get(i)).getTimezone().getOlsonId())) {
                    throw OneboxRestException.builder(MsEventSessionErrorCode.VENUES_WITH_DIFFERENT_OLSON_ID).build();
                }
            }
            return olsonId;
        } else {
            SessionSearchFilter sessionSearchFilter = (SessionSearchFilter) SerializationUtils.clone(filter);
            sessionSearchFilter.setDaysOfWeek(null);
            List<String> olsonIds = sessionDao.countDifferentOlsonIds(sessionSearchFilter);
            if (olsonIds.isEmpty()) {
                return null;
            } else if (olsonIds.size() > 1) {
                throw OneboxRestException.builder(MsEventSessionErrorCode.VENUES_WITH_DIFFERENT_OLSON_ID).build();
            }
            return olsonIds.get(0);
        }
    }

    @MySQLWrite
    public Long createSession(Long eventId, CreateSessionDTO request) {
        List<Long> ids = createSessions(eventId, Collections.singletonList(request));

        if (ids == null || ids.size() != 1) {
            throw OneboxRestException.builder(CoreErrorCode.GENERIC_ERROR)
                    .setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .setMessage("Incorrect amount of sessions inserted")
                    .build();
        }
        Long sessionId = ids.get(0);
        if (CommonUtils.isTrue(request.getSeasonPass()) && CollectionUtils.isNotEmpty(request.getSeasonSessions())) {
            seasonSessionDao.asociateSessionstoSeason(sessionId, request.getSeasonSessions());
        }
        if (request.getSecondaryMarketStartDate() != null && request.getSecondaryMarketEndDate() != null) {
            createSecondaryMarketDatesConfig(request, sessionId, eventId);
        }

        return sessionId;
    }

    @MySQLWrite
    public List<Long> createSessions(Long eventId, List<CreateSessionDTO> sessions) {
        if (eventId == null || eventId <= 0) {
            throw OneboxRestException.builder(CoreErrorCode.BAD_PARAMETER).setHttpStatus(HttpStatus.BAD_REQUEST)
                    .setMessage("EventId cannot be null and must be positive").build();
        }
        if (CommonUtils.isEmpty(sessions)) {
            throw OneboxRestException.builder(CoreErrorCode.BAD_PARAMETER).setHttpStatus(HttpStatus.BAD_REQUEST)
                    .setMessage("Sessions cannot be null or empty").build();
        }
        if (sessions.size() > BULK_SESSION_CREATE_LIMIT) {
            throw OneboxRestException.builder(CoreErrorCode.BAD_PARAMETER).setHttpStatus(HttpStatus.BAD_REQUEST)
                    .setMessage("Bulk create maximum limit exceeded: " + BULK_SESSION_CREATE_LIMIT).build();
        }

        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());
        List<RateRecord> eventRates = rateDao.getRatesByEventId(eventId.intValue());
        Boolean smartBookingEnabled = Boolean.FALSE;
        ExternalEntityConfig externalEntityConfig = null;

        for (CreateSessionDTO session : sessions) {
            if (CommonUtils.isTrue(session.getSeasonPass()) && CollectionUtils.isNotEmpty(session.getSeasonSessions())) {
                fillSessionPassValues(session);
            }
            if (BooleanUtils.isTrue(session.getSmartBooking()) && externalEntityConfig == null) {
                externalEntityConfig = entitiesRepository.getExternalEntityConfig(event.getIdentidad());
                smartBookingEnabled = externalEntityConfig != null && externalEntityConfig.getSmartBooking() != null
                        && BooleanUtils.isTrue(externalEntityConfig.getSmartBooking().getEnabled());
            }
            validateCreation(eventId, session, event, eventRates, smartBookingEnabled);
            fillDefaultValues(session, event, eventConfig);
        }

        List<Long> sessionIds = sessionDao.bulkInsertSessions(SessionConverter.toEntity(sessions));

        IntStream.range(0, sessions.size()).forEach(i -> {
            CreateSessionDTO session = sessions.get(i);
            Long sessionId = sessionIds.get(i);
            if (session.getSecondaryMarketStartDate() != null && session.getSecondaryMarketEndDate() != null) {
                createSecondaryMarketDatesConfig(session, sessionId, eventId);
            }
            if (session.getLoyaltyPointsConfig() != null) {
                addLoyaltyPointsConfig(sessionId, session.getLoyaltyPointsConfig());
            }
            initSessionConfig(sessionId, eventId);
            if (session.getTicketTaxIds() != null && !session.getTicketTaxIds().isEmpty()) {
                for (Long taxId : session.getTicketTaxIds()) {
                    CpanelSessionTaxesRecord cpanelSessionTaxes = new CpanelSessionTaxesRecord();
                    cpanelSessionTaxes.setSessionId(sessionId.intValue());
                    cpanelSessionTaxes.setTaxId(taxId.intValue());
                    cpanelSessionTaxes.setTipo(SessionTaxesType.TICKETS.getType());
                    sessionTaxesDao.insert(cpanelSessionTaxes);
                }
            }
            if (session.getChargeTaxIds() != null && !session.getChargeTaxIds().isEmpty()) {
                for (Long taxId : session.getChargeTaxIds()) {
                    CpanelSessionTaxesRecord cpanelSessionTaxes = new CpanelSessionTaxesRecord();
                    cpanelSessionTaxes.setSessionId(sessionId.intValue());
                    cpanelSessionTaxes.setTaxId(taxId.intValue());
                    cpanelSessionTaxes.setTipo(SessionTaxesType.CHARGES.getType());
                    sessionTaxesDao.insert(cpanelSessionTaxes);
                }
            }
        });

        sessions.stream().findFirst().ifPresent(s -> createSessionsRates(sessionIds, s.getRates()));
        eventDao.updateEventDatesFromSessionCriteria(eventId);

        if (EventUtils.isAvet(event.getTipoevento())) {
            boolean isAvetSocketEvent = isAvetSocketEvent(event.getTipoevento(), eventId);
            List<RateGroupRecord> eventGroupRates = rateGroupDao.getRatesGroupByEventId(eventId.intValue());
            for (Long sessionId : sessionIds) {
                initAvetSession(sessionId, isAvetSocketEvent);
                RateRecord sessionRateRecord = rateDao.getRatesByDefaultSessionId(sessionId.intValue(), null, null);
                String sessionRateName = sessionRateRecord != null ? sessionRateRecord.getNombre() : null;
                fillSessionRates(
                        eventGroupRates.stream()
                                .filter(rate -> !BooleanUtils.toBoolean(rate.getDefecto()))
                                .collect(Collectors.toList()),
                        sessionRateName,
                        eventId,
                        sessionId.intValue()
                );
            }
        }

        if (EventUtils.isActivity(event.getTipoevento()) && sessions.get(0).getSaleType() != null && sessions.get(0).getSaleType() != SessionSalesType.INDIVIDUAL.getType()) {
            // Check if group prices exist
            List<RateZoneDTO> rateZones = priceZoneAssignmentDao.findIndividualPricesBySession(sessionIds.get(0).intValue());
            for (RateZoneDTO rateZoneDTO : rateZones) {
                if (eventPricesDao.findGroup(rateZoneDTO.getZoneId(), rateZoneDTO.getRateId()) == null) {
                    eventPricesDao.addGroup(rateZoneDTO.getZoneId(), rateZoneDTO.getRateId(), 0d);
                }
            }
        }

        return sessionIds;
    }

    private void initAvetSession(Long sessionId, boolean isAvetSocketEvent) {
        if (isAvetSocketEvent) {
            SessionRecord sessionRecord = sessionDao.findSession(sessionId);
            avetAvailabilityMatchScheduleService.createAvetAvailabilitySchedule(sessionRecord.getIdexterno(), sessionId.intValue());
        }
    }

    public void fillSessionPassValues(CreateSessionDTO session) {
        if (session.getSessionStartDate() == null) {
            SessionSearchFilter filter = new SessionSearchFilter();
            filter.setIds(session.getSeasonSessions());
            filter.setFields(Collections.singletonList("date.start"));
            List<SessionRecord> sessions = sessionDao.findSessions(filter, null);
            Timestamp tMin = Timestamp.from(Instant.MIN);
            for (SessionRecord sessionRecord : sessions) {
                Timestamp t = sessionRecord.getFechainiciosesion();
                if (tMin.after(t)) {
                    tMin = t;
                }
            }
            session.setSessionStartDate(tMin.toLocalDateTime().atOffset(ZoneOffset.UTC).toZonedDateTime());
        }
    }

    public void fillSessionRates(List<RateGroupRecord> eventGroupRates, String name, Long eventId, Integer sessionId) {
        for (RateGroupRecord eventGroupRate : eventGroupRates) {
            String rateName = name + " - " + eventGroupRate.getNombre();
            RateGroupDTO rateGroupDTO = RateGroupConverter.convert(eventGroupRate, rateName);
            CpanelTarifaRecord cpanelTarifaRecord =
                    CommonRatesGroup.createSessionRates(rateName, rateGroupDTO.getId().intValue(), Math.toIntExact(eventId), eventGroupRate.getElementoComDescripcion(), rateGroupDTO.getDefaultRate());
            cpanelTarifaRecord = rateDao.insert(cpanelTarifaRecord);
            if (cpanelTarifaRecord.getDefecto().equals((byte) 1)) {
                updateEventVenueTemplatePriceZones(Math.toIntExact(eventId), cpanelTarifaRecord.getIdtarifa());
            }
            rateGroupDao.createSesionRate(sessionId, cpanelTarifaRecord.getIdtarifa());
        }
    }

    @MySQLWrite
    public Long cloneSession(Long eventId, Long sessionId, CloneSessionDTO sessionData) {
        SessionRecord sourceSession = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);

        validateClone(sourceSession);

        CpanelSesionRecord clonedSession = sourceSession.copy();
        clonedSession.setIdsesion(null);
        clonedSession.setElementocomticket(null);
        clonedSession.setElementocomtickettaquilla(null);
        clonedSession.setNombre(sessionData.getName());
        if (sessionData.getReference() != null) {
            clonedSession.setReference(sessionData.getReference());
        }
        clonedSession.setEstado(SessionStatus.SCHEDULED.getId());
        clonedSession.setEstadogeneracionaforo(EventType.ACTIVITY.getId().equals(sourceSession.getEventType()) ?
                SessionGenerationStatus.ACTIVE.getId() : SessionGenerationStatus.IN_PROGRESS.getId());

        fillCloneDates(clonedSession, sessionData);
        checkCloneBookings(eventId, sourceSession, clonedSession);

        CpanelSesionRecord newSession = sessionDao.insert(clonedSession);
        long newSessionId = newSession.getIdsesion().longValue();

        cloneSessionRates(sessionId, newSessionId);

        sessionCommunicationElementService.cloneCommunicationElements(eventId, sessionId, newSessionId);

        return newSession.getIdsesion().longValue();
    }

    public void postCreateSession(Long eventId, Long sessionId, CreateSessionDTO createData) {
        if (CommonUtils.isTrue(createData.getSeasonPass()) && CommonUtils.isTrue(createData.getAllowPartialRefund())) {
            sessionRefundConditionsService.initRefundConditions(sessionId, createData);
        }

        postCreateSessions(eventId, Collections.singletonList(sessionId), createData, null);
    }

    public void postCloneSession(Long eventId, Long sessionId, CloneSessionDTO cloneData) {
        postCreateSessions(eventId, Collections.singletonList(sessionId), null, cloneData);
    }

    public void postCreateSessions(Long eventId, List<Long> sessionIds, CreateSessionDTO createData, CloneSessionDTO cloneData) {


        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        Boolean smartBookingEnabled = Boolean.FALSE;
        ExternalEntityConfig externalEntityConfig = null;
        for (Long sessionId : sessionIds) {
            sessionRepository.createSession(sessionId, event.getIdevento().longValue(), event.getIdentidad().longValue());
            if (cloneData != null) {
                Long sourceSessionId = cloneData.getSourceSessionId();
                if (EventUtils.isActivity(event.getTipoevento())) {
                    priceTypeLabelSessionDao.clone(cloneData.getSourceSessionId(), sessionId);
                    sessionRepository.cloneSession(cloneData.getSourceSessionId(), event.getIdevento().longValue(), event.getIdentidad().longValue(), EventUtils.isActivity(event.getTipoevento()), sessionId);
                    generateSeatService.generateSeats(sessionId, false, null, false);
                } else {
                    sessionCloneService.cloneSession(sourceSessionId, sessionId, cloneData);
                }
                cloneAttendantsConfig(eventId, sourceSessionId, sessionId);
                cloneSessionConfig(sourceSessionId, sessionId);
                cloneLoyaltyPointsConfig(sourceSessionId, sessionId);
            } else {
                boolean isAvetSocket = isAvetSocketEvent(event.getTipoevento(), eventId);
                if (createData != null) {
                    generateSeatService.generateSeats(sessionId, CommonUtils.isTrue(createData.getSeasonPass()),
                            createData.getSeasonPassBlockingActions(), isAvetSocket);
                } else {
                    generateSeatService.generateSeats(sessionId, false, null, isAvetSocket);
                }
            }
            if (createData != null) {
                if (BooleanUtils.isTrue(createData.getSmartBooking())) {
                    if (externalEntityConfig == null) {
                        externalEntityConfig = entitiesRepository.getExternalEntityConfig(event.getIdentidad());
                        smartBookingEnabled = externalEntityConfig != null && externalEntityConfig.getSmartBooking() != null
                                && BooleanUtils.isTrue(externalEntityConfig.getSmartBooking().getEnabled());
                    }
                    if (BooleanUtils.isTrue(smartBookingEnabled)) {
                        Long sbSessionId = createSmartBookingSession(event.getIdentidad().longValue(), eventId, sessionId, createData);
                        sessionRepository.createSession(sbSessionId, event.getIdevento().longValue(), event.getIdentidad().longValue());
                        generateSeatService.generateSBSeats(sbSessionId);
                    }
                }
            }
        }
        this.notifyChanges(eventId, sessionIds, null);
    }

    private Long createSmartBookingSession(Long entityId, Long eventId, Long sessionId, CreateSessionDTO request) {

        //Create SmartBooking template from parent session
        Long sbTemplateId = createSmartBookingSessionTemplate(entityId, eventId, sessionId, request);

        //Create sbSession
        CpanelEntidadRecintoConfigRecord sbTemplateRelation = entityVenueTemplateDao.getByVenueTemplateId(sbTemplateId);
        request.setName(request.getName());
        request.setVenueConfigId(sbTemplateId);
        request.setVenueEntityConfigId(sbTemplateRelation.getIdrelacionentrecinto().longValue());
        Long sbSessionId = createSession(eventId, request);

        //Create parent relation
        CpanelSesionRecord original = new CpanelSesionRecord();
        original.setIdsesion(sessionId.intValue());
        original.setSbsesionrelacionada(sbSessionId.intValue());
        sessionDao.update(original);

        //Update sbStatus on child sbSession
        CpanelSesionRecord sbSession = new CpanelSesionRecord();
        sbSession.setIdsesion(sbSessionId.intValue());
        sbSession.setSbsesionrelacionada(sessionId.intValue());
        sbSession.setUsaaccesosplantilla(Boolean.TRUE);
        sessionDao.update(sbSession);

        CpanelTarifaRecord sbRate = createRateAndSessionRate(sessionId, sbSessionId);
        initAssigmentPriceZones(sbRate.getIdtarifa(), sbTemplateId, sbSessionId);

        return sbSessionId;
    }

    private void initAssigmentPriceZones(Integer rateId, Long venueTemplateId, Long sbSessionId) {
        List<IdNameCodeDTO> venueTemplatePriceTypes = venuesRepository.getPriceTypes(venueTemplateId);
        if (CollectionUtils.isEmpty(venueTemplatePriceTypes)) {
            LOGGER.error("Error creating assigment price zones for session id {} ", sbSessionId);
        } else {
            List<CpanelAsignacionZonaPreciosRecord> assigmentPriceZoneList = new ArrayList<>();
            venueTemplatePriceTypes.forEach(pt -> {
                        CpanelAsignacionZonaPreciosRecord assigmentPriceZone = new CpanelAsignacionZonaPreciosRecord();
                        assigmentPriceZone.setIdtarifa(rateId);
                        assigmentPriceZone.setIdzona(pt.getId().intValue());
                        assigmentPriceZone.setPrecio(99999.0);
                        assigmentPriceZoneList.add(assigmentPriceZone);
                    }
            );
            if (CollectionUtils.isNotEmpty(assigmentPriceZoneList)) {
                eventPricesDao.bulkInsertIndividual(assigmentPriceZoneList);
            }

        }
    }

    private CpanelTarifaRecord createRateAndSessionRate(Long sessionId, Long sbSessionId) {
        RateRecord sourceRate = rateDao.getRatesByDefaultSessionId(sessionId.intValue(), 1L, 0L);
        CpanelTarifaRecord newRate = new CpanelTarifaRecord();
        newRate.setIdevento(sourceRate.getIdEvento());
        newRate.setNombre(sourceRate.getNombre());
        newRate.setDescripcion(sourceRate.getDescripcion());
        newRate.setElementocomdescripcion(sourceRate.getElementoComDescripcion());
        newRate.setDefecto(sourceRate.getDefecto().byteValue());
        newRate.setAccesorestrictivo(sourceRate.getAccesoRestrictivo().byteValue());
        newRate.setIdgrupotarifa(sourceRate.getIdGrupoTarifa());
        newRate = rateDao.insert(newRate);
        sessionRateDao.updateSessionRateToNewRateId(sbSessionId.intValue(), sourceRate.getIdTarifa(), newRate.getIdtarifa());
        return newRate;
    }

    private Long createSmartBookingSessionTemplate(Long entityId, Long eventId, Long originalSessionId, CreateSessionDTO request) {
        CpanelConfigRecintoRecord sourceTemplate = eventDao.getEventVenueTemplate(eventId,
                request.getVenueConfigId(), request.getVenueEntityConfigId());
        List<Integer> sourceSpaces = sessionDao.getSessionAvailableSpaces(originalSessionId.intValue());

        List<CpanelZonaPreciosConfigRecord> venueTemplates
                = venueTemplateDao.getActiveVenueTemplateIdsAndTemplateType(eventId, es.onebox.event.events.domain.VenueTemplateType.ACTIVITY);
        if (CollectionUtils.isEmpty(venueTemplates)) {
            return venuesRepository.createVenueTemplate(SMART_BOOKING_DEFAULT_TEMPLATE_NAME, eventId,
                    sourceTemplate.getIdrecinto().longValue(), sourceSpaces.iterator().next().longValue(),
                    entityId, VenueTemplateType.ACTIVITY, request.getSmartBooking());
        } else if (venueTemplates.size() == 1) {
            return (long) venueTemplates.get(0).getIdconfiguracion();
        } else {
            throw new OneboxRestException(MsEventErrorCode.EVENT_SESSION_SB_TOO_MANY_VENUE_TEMPLATES);
        }
    }

    @MySQLWrite
    public void updateSession(Long eventId, UpdateSessionRequestDTO request) {
        Long sessionId = request.getId();
        SessionRecord sessionRecord = sessionValidationHelper.getSessionAndValidate(eventId, sessionId);
        CpanelEventoRecord eventRecord = eventDao.getById(sessionRecord.getIdevento());
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        SessionSecondaryMarketConfigExtended secondaryMarket =
                sessionSecondaryMarketConfigService.getSessionSecondaryMarketConfigSafely(
                        sessionRecord.getEntityId(),
                        sessionId
                );
        if (request.getSaleType() != null) {
            SessionDynamicPriceConfig dynamicPriceConfig = sessionConfigCouchDao.findDynamicPriceBySessionId(request.getId());

            if (dynamicPriceConfig != null && Boolean.TRUE.equals(dynamicPriceConfig.getActive())) {
                if (!request.getSaleType().equals(SessionSalesType.INDIVIDUAL.getType())) {
                    throw new OneboxRestException(MsEventSessionErrorCode.GROUPS_INCOMPATIBLE_WITH_DYNAMIC_PRICES);
                }
            }
        }

        validateUpdate(request, sessionRecord, eventRecord, sessionConfig, secondaryMarket);

        Optional<Boolean> newUseTemplateAccessValue;
        if (request.getUseTemplateAccess() != null
                && !request.getUseTemplateAccess().equals(sessionRecord.getUsaaccesosplantilla())
                && EventUtils.isActivity(sessionRecord.getEventType())) {
            newUseTemplateAccessValue = Optional.of(request.getUseTemplateAccess());
        } else {
            newUseTemplateAccessValue = Optional.empty();
        }

        // when the session sale type is changed to NOT INDIVIDUAL, check is the group prices exist
        if (EventUtils.isActivity(sessionRecord.getEventType()) && request.getSaleType() != null
                && !request.getSaleType().equals(SessionSalesType.INDIVIDUAL.getType())
                && sessionRecord.getTipoventa() != null
                && sessionRecord.getTipoventa().equals(SessionSalesType.INDIVIDUAL.getType())) {
            List<RateZoneDTO> rateZones = priceZoneAssignmentDao.findIndividualPricesBySession(sessionId.intValue());
            for (RateZoneDTO rateZoneDTO : rateZones) {
                if (eventPricesDao.findGroup(rateZoneDTO.getZoneId(), rateZoneDTO.getRateId()) == null) {
                    eventPricesDao.addGroup(rateZoneDTO.getZoneId(), rateZoneDTO.getRateId(), 0d);
                }
            }
        }

        SessionConverter.updateRecord(sessionRecord, request, eventRecord);

        boolean sessionHasOperations =
                !MapUtils.isEmpty(ordersRepository.sessionOperations(Collections.singletonList(sessionId.intValue())));
        if (SessionStatus.DELETED.equals(request.getStatus())) {
            deleteSession(eventId, request, sessionRecord, sessionHasOperations);
            if (sessionRecord.getSbsesionrelacionada() != null) {
                Integer venueTemplateType = sessionDao.getVenueTemplateTypeBySessionId(sessionRecord.getIdsesion());
                Long sessionIdExternalDelete = EventUtils.isActivityTemplate(venueTemplateType) ?
                        sessionId : sessionRecord.getSbsesionrelacionada().longValue();
                intAvetConnectorRepository.deleteExternalSession(eventRecord.getIdentidad().longValue(), eventId, sessionIdExternalDelete);
            }
        } else {
            updateSession(eventId, request, sessionRecord, sessionConfig, sessionHasOperations, newUseTemplateAccessValue, secondaryMarket);
            checkAndUpdateSessionPack(sessionRecord);
        }

    }

    private void updateSession(Long eventId, UpdateSessionRequestDTO session, SessionRecord sessionRecord,
                               SessionConfig sessionConfig, boolean sessionHasOperations, Optional<Boolean> newUseTemplateAccessValue,
                               SessionSecondaryMarketConfigExtended secondaryMarket) {
        sessionDao.update(sessionRecord);
        if (EventType.AVET.getId().equals(sessionRecord.getEventType())) {
            updateAVETSessionRates(session);
        } else {
            updateSessionRates(eventId, session, sessionRecord);
        }

        updateSessionConfig(session, sessionRecord, sessionConfig, sessionHasOperations);
        if (session.getDate() != null) {
            eventDao.updateEventDatesFromSessionCriteria(eventId);
        }

        if (session.getEnableSecondaryMarket() != null && sessionSecondaryMarketConfigService.getAllowSecondaryMarket(sessionRecord.getEntityId())) {
                updateSecondaryMarketConfig(session.getId(), session, secondaryMarket, sessionRecord);
            }

        if (session.getTicketTaxes() != null && !session.getTicketTaxes().isEmpty()) {
            List<CpanelSessionTaxesRecord> ticketTaxes = sessionTaxesDao.findFlatSessionsTaxes(null, session.getId().intValue(), SessionTaxesType.TICKETS);
            List<Integer> ddbbTicketTaxesIds = ticketTaxes.stream().map(CpanelSessionTaxesRecord::getTaxId).collect(Collectors.toList());
            List<Long> requestTicketTaxIds = session.getTicketTaxes().stream().map(IdNameDTO::getId).collect(Collectors.toList());
            for (CpanelSessionTaxesRecord cpanelSessionTaxesRecord : ticketTaxes) {
                if (!requestTicketTaxIds.contains(cpanelSessionTaxesRecord.getTaxId().longValue())) {
                    sessionTaxesDao.delete(cpanelSessionTaxesRecord);
                }
            }
            for (Long requestTicketTaxId : requestTicketTaxIds) {
                if (!ddbbTicketTaxesIds.contains(requestTicketTaxId.intValue())) {
                    CpanelSessionTaxesRecord cpanelSessionTaxesRecord = new CpanelSessionTaxesRecord();
                    cpanelSessionTaxesRecord.setSessionId(session.getId().intValue());
                    cpanelSessionTaxesRecord.setTipo(SessionTaxesType.TICKETS.getType());
                    cpanelSessionTaxesRecord.setTaxId(requestTicketTaxId.intValue());
                    sessionTaxesDao.insert(cpanelSessionTaxesRecord);
                }
            }
        }
        if (session.getTicketTaxes() != null && !session.getTicketTaxes().isEmpty()) {
            List<CpanelSessionTaxesRecord> chargesTaxes = sessionTaxesDao.findFlatSessionsTaxes(null, session.getId().intValue(), SessionTaxesType.CHARGES);
            List<Integer> ddbbChargeTaxesIds = chargesTaxes.stream().map(CpanelSessionTaxesRecord::getTaxId).collect(Collectors.toList());
            List<Long> requestChargesTaxIds = session.getChargesTaxes().stream().map(IdNameDTO::getId).collect(Collectors.toList());
            for (CpanelSessionTaxesRecord cpanelSessionTaxesRecord : chargesTaxes) {
                if (!requestChargesTaxIds.contains(cpanelSessionTaxesRecord.getTaxId().longValue())) {
                    sessionTaxesDao.delete(cpanelSessionTaxesRecord);
                }
            }
            for (Long requestChargeTaxId : requestChargesTaxIds) {
                if (!ddbbChargeTaxesIds.contains(requestChargeTaxId.intValue())) {
                    CpanelSessionTaxesRecord cpanelSessionTaxesRecord = new CpanelSessionTaxesRecord();
                    cpanelSessionTaxesRecord.setSessionId(session.getId().intValue());
                    cpanelSessionTaxesRecord.setTipo(SessionTaxesType.CHARGES.getType());
                    cpanelSessionTaxesRecord.setTaxId(requestChargeTaxId.intValue());
                    sessionTaxesDao.insert(cpanelSessionTaxesRecord);
                }
            }
        }

        newUseTemplateAccessValue.ifPresent(useTemplateAccessValue -> replaceSessionPriceTypeLabels(session, sessionRecord, useTemplateAccessValue));
    }

    public void postUpdateSession(Long eventId, UpdateSessionRequestDTO request) {
        postUpdateSession(eventId, request, null);
    }

    public void postUpdateSession(Long eventId, UpdateSessionRequestDTO request, SessionStatus oldStatus) {
        SessionRecord sessionRecord = sessionDao.findSession(request.getId());
        if (sessionRecord != null) {
            if (CommonUtils.isTrue(sessionRecord.getEsabono()) && CommonUtils.isTrue(sessionRecord.getAllowpartialrefund())) {
                sessionRefundConditionsService.updateRefundConditionsMap(request.getId(), sessionRecord.getVenueTemplateId().longValue());
            }
            if (request.getSpace() != null && request.getSpace().getId() != null) {
                whitelistGenerationService.generateWhiteList(Collections.singletonList(sessionRecord.getSessionId()));
            }
            
            if (SessionStatus.READY.equals(request.getStatus()) && oldStatus != null && !SessionStatus.READY.equals(oldStatus)) {
                publishSessionToExternalProviders(eventId);
            }
        }
        this.notifyChanges(eventId, List.of(request.getId()), request);
    }

    private void publishSessionToExternalProviders(Long eventId) {
        EventConfig eventConfig = eventConfigCouchDao.get(String.valueOf(eventId));
        if (eventConfig == null || !Provider.ITALIAN_COMPLIANCE.equals(eventConfig.getInventoryProvider())) {
            return;
        }
        
        var event = eventDao.findEvent(eventId);
        if (event == null || !EventStatus.READY.getId().equals(event.getKey().getEstado())) {
            return;
        }
        
        try {
            Long entityId = event.getKey().getIdentidad().longValue();
            intDispatcherRepository.publishEvent(entityId, eventId);
        } catch (Exception e) {
            LOGGER.error("Failed to publish event {} to external providers", eventId, e);
        }
    }

    public void postUpdateSessions(Long eventId, Map<Long, String> sessionsUpdateStatus, UpdateSessionsRequestDTO request, Boolean preview) {
        postUpdateSessions(eventId, sessionsUpdateStatus, request, preview, null);
    }

    public void postUpdateSessions(Long eventId, Map<Long, String> sessionsUpdateStatus, UpdateSessionsRequestDTO request, Boolean preview, Map<Long, SessionStatus> oldStatuses) {
        if (CommonUtils.isFalse(preview)) {
            List<Long> sessionIds = new ArrayList<>();
            for (Map.Entry<Long, String> updateStatusEntry : sessionsUpdateStatus.entrySet()) {
                if (updateStatusEntry.getValue() == null) {
                    sessionIds.add(updateStatusEntry.getKey());
                }
            }
            this.notifyChanges(eventId, sessionIds, request.getValue());
            
            if (SessionStatus.READY.equals(request.getValue().getStatus()) && oldStatuses != null) {
                boolean anySessionTransitionedToReady = sessionIds.stream()
                        .map(oldStatuses::get)
                        .anyMatch(oldStatus -> oldStatus != null && !SessionStatus.READY.equals(oldStatus));
                
                if (anySessionTransitionedToReady) {
                    publishSessionToExternalProviders(eventId);
                }
            }
        }
    }

    public void postDeleteSession(Long eventId, Long sessionId, UpdateSessionRequestDTO request) {
        this.notifyChanges(eventId, List.of(sessionId), request);
    }

    private void notifyChanges(Long eventId, List<Long> sessionIds, UpdateSessionRequestDTO request) {

        NotificationSubtype notificationSubtype;
        if (request == null) {
            notificationSubtype = NotificationSubtype.SESSION_CREATED;
            refreshDataService.refreshEvent(eventId, "postCreateSessions");
        } else if (request.getStatus() != null && request.getStatus().equals(SessionStatus.DELETED)) {
            notificationSubtype = NotificationSubtype.SESSION_DELETED;
            refreshDataService.refreshEvent(eventId, "postDeleteSessions");
        } else {
            notificationSubtype = NotificationSubtype.SESSION_GENERAL_DATA;
            refreshDataService.refreshSessions(eventId, sessionIds, "postUpdateSessions", request);
        }
        for (Long sessionId : sessionIds) {
            if (notificationSubtype.equals(NotificationSubtype.SESSION_DELETED)) {
                webhookService.sendWebhookSessionDelete(sessionId, eventId, notificationSubtype);
                channelSuggestionsCleanUpService.sendSessionSuggestionCleaner(sessionId);
            } else {
                webhookService.sendSessionNotification(sessionId, notificationSubtype);
            }
        }
    }

    private void checkAndUpdateSessionPack(SessionRecord sessionRecord) {
        if (!CommonUtils.isTrue(sessionRecord.getEsabono())) {
            // Check if session has linked session packs
            List<Long> sessionPacksIds = seasonSessionDao.findSessionPacksBySessionId(sessionRecord.getIdsesion().longValue());
            if (!sessionPacksIds.isEmpty()) {
                for (Long sessionPackId : sessionPacksIds) {
                    List<SessionRecord> sessionsFromSessionPack =
                            sessionDao.findSessionsFromSessionPackOrderByFechaInicio(sessionPackId.intValue());
                    SessionRecord sessionPack = sessionDao.findSession(sessionPackId);
                    sessionPack.setFechainiciosesion(sessionsFromSessionPack.get(0).getFechainiciosesion());

                    SessionRecord lastSession = sessionsFromSessionPack.get(sessionsFromSessionPack.size() - 1);

                    // FechaRealFinSession. If absent, the latest between Fechainiciosesion and Fechafinsesion (sales end date)
                    if (lastSession.getFecharealfinsesion() != null) {
                        sessionPack.setFecharealfinsesion(lastSession.getFecharealfinsesion());
                    } else if (lastSession.getFechainiciosesion().after(lastSession.getFechafinsesion())) {
                        sessionPack.setFecharealfinsesion(lastSession.getFechainiciosesion());
                    } else {
                        sessionPack.setFecharealfinsesion(lastSession.getFechafinsesion());
                    }
                    sessionDao.update(sessionPack);
                }
            }
        }
    }

    @MySQLWrite
    public Map<Long, String> updateSessions(Long eventId, UpdateSessionsRequestDTO updateSessionsRequestDTO, Boolean preview) {
        // session search to check all changes with all data
        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setIds(updateSessionsRequestDTO.getIds());
        List<SessionRecord> sessionsToUpdate = sessionDao.findSessions(filter, null);
        Map<Integer, SessionConfig> sessionConfigs = sessionsToUpdate.stream().collect(Collectors.toMap(CpanelSesionRecord::getIdsesion,
                s -> sessionConfigCouchDao.getOrInitSessionConfig(s.getIdsesion().longValue())));
        Map<Long, Long> sessionsOperations =
                ordersRepository.sessionOperations(
                        sessionsToUpdate.stream().map(CpanelSesionRecord::getIdsesion).collect(toList()));

        Map<Integer, SessionSecondaryMarketConfigExtended> sessionSecondaryMarketConfigs = sessionsToUpdate.stream()
                .collect(HashMap::new,
                        (map, sessionRecord) -> {
                            map.put(sessionRecord.getIdsesion(),
                                    sessionSecondaryMarketConfigService.getSessionSecondaryMarketConfigSafely(
                                            sessionRecord.getEntityId(),
                                            sessionRecord.getIdsesion().longValue()
                                    )
                            );
                        },
                        HashMap::putAll
                );

        // change validation
        Map<Long, String> updateStatus = validateUpdate(eventId, updateSessionsRequestDTO, sessionsToUpdate, sessionConfigs, sessionsOperations, sessionSecondaryMarketConfigs);
        // proceeding to change
        if (CommonUtils.isFalse(preview)) {
            //Only update sessions without errors on validation
            List<Long> sessionIds = updateStatus.entrySet().stream()
                    .filter(e -> e.getValue() == null)
                    .map(Map.Entry::getKey)
                    .collect(toList());
            if (SessionStatus.DELETED.equals(updateSessionsRequestDTO.getValue().getStatus())) {
                deleteSessions(eventId, updateSessionsRequestDTO, sessionsToUpdate, sessionIds, sessionsOperations);
            } else {
                int updatedSessions = bulkUpdateSessions(updateSessionsRequestDTO.getValue(), sessionIds, sessionsToUpdate, eventId);
                if (updatedSessions != sessionIds.size()) {
                    throw new OneboxRestException(CoreErrorCode.PERSISTENCE_ERROR, "Unexpected error on bulk update of sessions", null);
                }
                if (updateSessionsRequestDTO.getValue().getDate() != null) {
                    eventDao.updateEventDatesFromSessionCriteria(eventId);
                    Set<SessionRecord> sessionRecords = sessionsToUpdate.stream()
                            .filter(sessionRecord -> sessionIds.contains(sessionRecord.getIdsesion().longValue()))
                            .collect(Collectors.toSet());
                    sessionRecords.forEach(this::checkAndUpdateSessionPack);
                }
                if (SessionValidator.hasAnySessionConfigFields(updateSessionsRequestDTO.getValue())) {
                    sessionsToUpdate.forEach(session -> updateSessionConfig(updateSessionsRequestDTO.getValue(), session,
                            sessionConfigs.get(session.getIdsesion()), sessionsOperations.containsKey(session.getIdsesion().longValue())));
                }
                bulkUpdateRates(sessionIds, updateSessionsRequestDTO.getValue());
                if (sessionsToUpdate.stream().anyMatch(session ->
                        SessionValidator.hasUpdateSecondaryMarketConfigs(
                                updateSessionsRequestDTO.getValue()))) {
                    updateSecondaryMarketConfigs(updateSessionsRequestDTO, sessionSecondaryMarketConfigs, sessionsToUpdate, sessionIds);
                }

            }
        }
        return updateStatus;
    }

    private Integer bulkUpdateSessions(UpdateSessionRequestDTO updateSessionRequestDTO, List<Long> sessionIds,
                                       List<SessionRecord> sessionsToUpdate, Long eventId) {

        if (updateSessionRequestDTO.getUseTemplateAccess() != null
                && EventUtils.isActivity(sessionsToUpdate.get(0).getEventType())) {
            bulkReplaceSessionPriceTypeLabels(updateSessionRequestDTO, sessionsToUpdate);
        }

        int updatedSessions;
        if (SessionValidator.hasNotRelativeDates(updateSessionRequestDTO.getDate())) {
            updatedSessions = sessionDao.bulkUpdateSessions(sessionIds, updateSessionRequestDTO);
        } else {
            CpanelEventoRecord event = eventDao.getById(eventId.intValue());
            sessionsToUpdate.forEach(session -> {
                SessionConverter.updateRecord(session, updateSessionRequestDTO, event);
                sessionDao.update(session);
            });
            updatedSessions = sessionsToUpdate.size();
        }
        return updatedSessions;
    }

    @MySQLRead
    public PriceTypesDTO getPriceTypes(Long eventId, Long sessionId, PriceTypeBaseFilter filter) {
        SessionRecord sessionRecord = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);

        Long venueConfigId = sessionRecord.getVenueTemplateId().longValue();
        Long total = priceZoneConfigDao.countTotalByVenueConfigId(venueConfigId);

        List<ZonaPreciosConfigRecord> cpanelZonaPreciosConfigRecords;
        if (BooleanUtils.isTrue(sessionRecord.getUsaaccesosplantilla())) {
            cpanelZonaPreciosConfigRecords = priceZoneConfigDao.getPriceZone(venueConfigId, filter);
        } else {
            cpanelZonaPreciosConfigRecords = priceZoneConfigDao.getPriceZoneBySession(venueConfigId, filter, sessionId);
        }

        List<PriceTypeDTO> priceTypeList = cpanelZonaPreciosConfigRecords.stream()
                .map(PriceTypeConverter::toPriceTypeConfigDTO).collect(toList());

        PriceTypesDTO priceZoneConfigsDTO = new PriceTypesDTO();
        priceZoneConfigsDTO.setMetadata(MetadataBuilder.build(filter, total));
        priceZoneConfigsDTO.setData(priceTypeList);

        return priceZoneConfigsDTO;
    }

    @MySQLRead
    public SessionRatesDTO getSessionRates(Long eventId, Long sessionId, RatesFilter ratesFilter) {
        sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        SessionRatesDTO ratesResponse = new SessionRatesDTO();
        ratesResponse.setMetadata(MetadataBuilder.build(
                ratesFilter, rateDao.countBySessionId(sessionId.intValue())));
        ratesResponse.setData(rateDao
                .getRatesBySessionId(sessionId.intValue(), ratesFilter.getLimit(), ratesFilter.getOffset())
                .stream()
                .map(RateConverter::convertDTO)
                .collect(Collectors.toList()));
        return ratesResponse;
    }

    private SessionDTO convertSession(SessionRecord sessionRecord, SessionSecondaryMarketConfigDTO secondaryMarket) {
        SessionDTO sessionDTO = SessionConverter.toSessionDTO(sessionRecord, Collections.emptyList(), secondaryMarket, null);
        fillRatesInformation(sessionDTO);
        fillSessionPackInformation(sessionDTO);
        fillPreSalesInformation(sessionDTO);
        fillSessionConfigInformation(sessionDTO);
        SessionConfigDTO sessionConfig = getSessionConfig(sessionRecord.getIdsesion().longValue());
        if (sessionConfig != null && sessionConfig.getSessionDynamicPriceConfigDTO() != null) {
            sessionDTO.setUseDynamicPrices(sessionConfig.getSessionDynamicPriceConfigDTO().getActive());
        }
        return sessionDTO;
    }

    private SessionPackDTO convertSessionPack(SessionRecord sessionRecord) {
        SessionPackDTO sessionPackDTO = SessionConverter.toSessionPackDTO(sessionRecord);

        sessionPackDTO.setSessionIds(seasonSessionDao.findSessionsBySessionPackId(sessionPackDTO.getId()));
        fillSessionPackDates(sessionPackDTO, sessionRecord);

        return sessionPackDTO;
    }

    private void fillSessionPackDates(SessionPackDTO sessionPackDTO, SessionRecord sessionRecord) {
        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        sessionSearchFilter.setIds(sessionPackDTO.getSessionIds());
        SessionsDTO sessions = searchSessions(null, sessionSearchFilter);
        ZonedDateTime sessionPackEndDate = DateUtils.now();
        ZonedDateTime sessionPackStartDate = CommonUtils.timestampToZonedDateTime(sessionRecord.getFechainiciosesion());
        for (SessionDTO session : sessions.getData()) {
            if (session.getDate().getEnd() != null && sessionPackEndDate.isBefore(session.getDate().getEnd())) {
                sessionPackEndDate = session.getDate().getEnd();
            } else if (sessionPackEndDate.isBefore(session.getDate().getStart())) {
                sessionPackEndDate = session.getDate().getStart();
            }
        }
        SessionPackDateDTO sessionPackDateDTO = new SessionPackDateDTO();
        sessionPackDateDTO.setStart(sessionPackStartDate);
        sessionPackDateDTO.setEnd(sessionPackEndDate);
        sessionPackDTO.setDate(sessionPackDateDTO);
    }

    private void updateSessionRates(Long eventId, UpdateSessionRequestDTO request, SessionRecord sessionRecord) {
        List<RateDTO> ratesRequest = request.getRates();
        if (CollectionUtils.isNotEmpty(ratesRequest)) {
            // Block rate updates for Italian Compliance events when both event and session are READY
            validateRateEditingAllowed(eventId, sessionRecord);
            
            //Check if rates request changes anything before updating
            List<CpanelSesionTarifaRecord> sessionRates = rateDao.getSessionRates(request.getId().intValue());
            if (ratesRequest.size() == sessionRates.size()) {
                boolean updated = false;
                boolean found = false;
                for (RateDTO rateRequest : ratesRequest) {
                    for (CpanelSesionTarifaRecord rate : sessionRates) {
                        if (rateRequest.getId().equals(rate.getIdtarifa().longValue())) {
                            updated = !rate.getDefecto().equals(rateRequest.isDefault());
                            found = true;
                            break;
                        }
                    }
                    if (updated) {
                        break;
                    }
                }
                if (found && !updated) {
                    request.setRates(null);
                    return;
                }
            }
            List<RateRecord> eventRates = rateDao.getRatesByEventId(eventId.intValue());
            SessionValidator.validateRates(eventRates, ratesRequest);
            sessionRateDao.cleanRatesForSessionId(request.getId().intValue());
            createSessionsRates(Collections.singletonList(request.getId()), ratesRequest);
        }
    }

    private void validateRateEditingAllowed(Long eventId, SessionRecord sessionRecord) {
        EventConfig eventConfig = eventConfigCouchDao.get(String.valueOf(eventId));
        if (EventUtils.isItalianCompliance(EventUtils.getInventoryProvider(eventConfig))) {
            boolean isEventReady = EventStatus.READY.getId().equals(sessionRecord.getEventStatus());
            boolean isSessionReady = SessionStatus.READY.getId().equals(sessionRecord.getEstado());
            
            if (isEventReady && isSessionReady) {
                throw new OneboxRestException(MsEventRateErrorCode.RATE_EDITING_BLOCKED);
            }
        }
    }

    private void updateAVETSessionRates(UpdateSessionRequestDTO session) {
        if (!CommonUtils.isEmpty(session.getRates())) {
            List<CpanelSesionTarifaRecord> sessionRates =
                    sessionRateDao.getSessionRatesBySessionId(session.getId().intValue());
            SessionValidator.validateSessionRates(sessionRates, session.getRates());
            List<CpanelSesionTarifaRecord> cpanelSesionTarifaWithVisibility =
                    resetSessionRatesVisibilityAndUpdate(sessionRates, session.getRates());
            cpanelSesionTarifaWithVisibility.forEach(sessionRateDao::updateSesionTarifaVisibilities);
        }
    }

    private List<CpanelSesionTarifaRecord> resetSessionRatesVisibilityAndUpdate(List<CpanelSesionTarifaRecord> sessionRates, List<RateDTO> updatedSessionRates) {
        return sessionRates
                .stream()
                .map(sessionRate -> {
                    if (!BooleanUtils.toBoolean(sessionRate.getDefecto())) {
                        sessionRate.setVisibilidad(false);
                    }
                    RateDTO rate =
                            updatedSessionRates
                                    .stream()
                                    .filter(currentRate -> sessionRate.getIdtarifa().equals(currentRate.getId().intValue()))
                                    .findFirst()
                                    .orElse(null);
                    if (rate != null) {
                        sessionRate.setVisibilidad(true);
                    }
                    return sessionRate;
                }).collect(Collectors.toList());
    }

    private void updateSessionConfig(UpdateSessionRequestDTO session, SessionRecord sessionRecord,
                                     SessionConfig sessionConfig, boolean sessionHasOperations) {
        SessionConverter.updateSessionConfig(sessionConfig, session, sessionRecord);
        updateSessionStreaming(session, sessionRecord, sessionConfig, sessionHasOperations);
        sessionConfigCouchDao.upsert(sessionRecord.getIdsesion().toString(), sessionConfig);
    }

    private void updateSessionStreaming(UpdateSessionRequestDTO session, SessionRecord sessionRecord,
                                        SessionConfig sessionConfig, boolean sessionHasOperations) {
        boolean streamingWasEnabled = sessionConfig.getStreamingVendorConfig() != null &&
                BooleanUtils.isTrue(sessionConfig.getStreamingVendorConfig().getEnabled());
        if (session.getStreaming() != null) {
            StreamingVendorConfig streamingConfig = updateStreamingConfig(session, sessionConfig);
            updateStreamingJob(sessionRecord, streamingConfig, streamingWasEnabled, sessionHasOperations);
        } else if (streamingWasEnabled && (session.getDate() != null && session.getDate().getStart() != null)) {
            updateStreamingJob(sessionRecord, sessionConfig.getStreamingVendorConfig(), true, sessionHasOperations);
        }
    }

    private void updateStreamingJob(SessionRecord sessionRecord, StreamingVendorConfig streamingConfig,
                                    boolean streamingWasEnabled, boolean sessionHasOperations) {
        Integer sessionId = sessionRecord.getIdsesion();
        if (CommonUtils.isTrue(streamingConfig.getEnabled()) &&
                (streamingConfig.getSendEmail() == null || BooleanUtils.isTrue(streamingConfig.getSendEmail()))) {
            ZonedDateTime startDate = CommonUtils.timestampToZonedDateTime(sessionRecord.getFechainiciosesion());
            ZonedDateTime launchDate = startDate.minusMinutes(streamingConfig.getEmailMinutesBeforeStart());

            TaskInfo taskInfo = taskService.get(buildStreamingJobName(sessionId), SESSION_STREAMING_EMAIL);

            //If calculated launchDate is not valid because is before now, check if delete current job or keep alive
            if (launchDate.isBefore(ZonedDateTime.now()) && !currentJobBetweenNowAndSessionStart(startDate, taskInfo)) {
                taskService.delete(buildStreamingJobName(sessionId), SESSION_STREAMING_EMAIL);
                return;
            }

            if (taskInfo != null) {
                taskService.edit(buildStreamingTaskInfo(sessionId, launchDate));
            } else {
                taskService.addJob(buildStreamingTaskInfo(sessionId, launchDate));
            }

            LOGGER.info("[SESSION STREAMING] id: {} - email cron scheduled to run at {}", sessionId, launchDate);
        } else if (streamingWasEnabled) {
            if (BooleanUtils.isFalse(streamingConfig.getEnabled())) {
                if (!sessionHasOperations) {
                    taskService.delete(buildStreamingJobName(sessionId), SESSION_STREAMING_EMAIL);
                } else {
                    throw new OneboxRestException(MsEventSessionErrorCode.SESSION_WITH_BOOKED_SEAT,
                            "Session with booked seats, sessionId: " + sessionId, null);
                }
            } else if (streamingConfig.getSendEmail() != null && BooleanUtils.isFalse(streamingConfig.getSendEmail())) {
                taskService.delete(buildStreamingJobName(sessionId), SESSION_STREAMING_EMAIL);
            }
        }
    }

    private static boolean currentJobBetweenNowAndSessionStart(ZonedDateTime startDate, TaskInfo taskInfo) {
        return taskInfo != null && (taskInfo.getNextFireTime().after(DateUtils.getDate(ZonedDateTime.now())) &&
                taskInfo.getNextFireTime().before(DateUtils.getDate(startDate)));
    }

    private static String buildStreamingJobName(Integer sessionId) {
        return SESSION_STREAMING_EMAIL + "_" + sessionId;
    }

    private static TaskInfo buildStreamingTaskInfo(Integer sessionId, ZonedDateTime launchDate) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setJobName(buildStreamingJobName(sessionId));
        taskInfo.setJobGroup(SESSION_STREAMING_EMAIL);
        taskInfo.setCronExpression(CronUtils.buildCron(launchDate));
        taskInfo.setJobClass(SessionStreamingEmailJob.class);
        HashMap<String, Long> data = new HashMap<>();
        data.put(SessionStreamingEmailJob.SESSION_ID, sessionId.longValue());
        taskInfo.setData(data);
        return taskInfo;
    }

    private static StreamingVendorConfig updateStreamingConfig(UpdateSessionRequestDTO session, SessionConfig sessionConfig) {
        StreamingVendorConfig streamingConfig = sessionConfig.getStreamingVendorConfig();
        if (streamingConfig == null) {
            streamingConfig = new StreamingVendorConfig();
        }
        streamingConfig.setEnabled(CommonUtils.isTrue(session.getStreaming().getEnabled()));
        if (BooleanUtils.isTrue(streamingConfig.getEnabled())) {
            if (session.getStreaming().getVendor() != null) {
                streamingConfig.setVendor(es.onebox.event.sessions.domain.sessionconfig.StreamingVendor.valueOf(
                        session.getStreaming().getVendor().name()));
            }
            if (session.getStreaming().getValue() != null) {
                streamingConfig.setValue(session.getStreaming().getValue());
            }
            streamingConfig.setEmailMinutesBeforeStart(streamingConfig.getEmailMinutesBeforeStart() != null ?
                    streamingConfig.getEmailMinutesBeforeStart() : DEFAULT_STREAMING_SCHEDULER_BEFORE_START);
            if (session.getStreaming().getSendEmail() != null) {
                streamingConfig.setSendEmail(session.getStreaming().getSendEmail());
            }
        }
        sessionConfig.setStreamingVendorConfig(streamingConfig);

        return streamingConfig;
    }

    protected void validateUpdate(UpdateSessionRequestDTO request, CpanelSesionRecord sessionRecord,
                                  CpanelEventoRecord eventRecord, SessionConfig sessionConfig,
                                  SessionSecondaryMarketConfigExtended sessionSecondaryMarketConfig) {

        if (sessionRecord.getEstado().equals(SessionState.DELETED.value())) {
            throw new OneboxRestException(CoreErrorCode.FORBIDDEN_OPERATION, "request is already deleted. It cant be updated!", null);
        }
        if (request.getSpace() != null && request.getSpace().getId() != null) {
            List<Integer> sessionAvailableSpaces = sessionDao.getSessionAvailableSpaces(sessionRecord.getIdsesion());
            if (!sessionAvailableSpaces.contains(request.getSpace().getId().intValue())) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SPACE, "Invalid space for request", null);
            }
        }
        if (request.getTicketTax() != null && request.getTicketTax().getId().equals(sessionRecord.getIdimpuesto().longValue())) {
            request.setTicketTax(null);
        }
        if (request.getChargesTax() != null && request.getChargesTax().getId().equals(sessionRecord.getIdimpuestorecargo().longValue())) {
            request.setChargesTax(null);
        }
        if (request.getTicketTax() != null || request.getChargesTax() != null) {
            List<Long> eventTaxes = taxDao.getEventTaxes(sessionRecord.getIdevento().longValue());
            checkTax(request.getTicketTax() != null ? request.getTicketTax().getId() : null, eventTaxes);
            checkTax(request.getChargesTax() != null ? request.getChargesTax().getId() : null, eventTaxes);
        }

        SessionValidator.validateSessionName(request.getName());

        if (request.getDate() != null) {
            SessionValidator.validateUpdateDates(request, sessionRecord, eventRecord, sessionSecondaryMarketConfig);
        }
        if (CommonUtils.isTrue(request.getEnableMembersLoginsLimit()) &&
                request.getMembersLoginsLimit() == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "request membersLoginsLimit is required to enable", null);
        }
        if (request.getStreaming() != null && CommonUtils.isTrue(request.getStreaming().getEnabled()) &&
                request.getStreaming().getVendor() == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "request streaming vendor is required to enable", null);
        }
        if (request.getProducerId() != null) {
            Long idEntityProducer = entitiesRepository.getProducer(request.getProducerId()).getEntity().getId();
            if (!idEntityProducer.equals(eventRecord.getIdentidad().longValue())) {
                throw OneboxRestException.builder(MsEventSessionErrorCode.PRODUCER_ID_NOT_VALID).build();
            }
        }
        if (request.getProducerId() != null) {
            if (request.getStatus() != null && request.getStatus() != SessionStatus.SCHEDULED || !sessionRecord.getEstado().equals(SessionStatus.SCHEDULED.getId())) {
                throw OneboxRestException.builder(MsEventSessionErrorCode.SESSION_INVALID_STATUS_FOR_INVOICE_MODIFICATION).build();
            }

            ProducerInvoiceProvider provider = entitiesRepository.getProducerInvoiceProvider(request.getProducerId());
            if (request.getInvoicePrefixId() == null && provider.getStatus().equals(RequestStatus.COMPLETED)) {
                throw OneboxRestException.builder(MsEventSessionErrorCode.INVOICE_PREFIX_ID_MANDATORY).build();
            }

            if (request.getInvoicePrefixId() != null) {
                InvoicePrefix invoicePrefix = entitiesRepository.getInvoicePrefix(request.getProducerId(), request.getInvoicePrefixId());
                if (invoicePrefix == null) {
                    throw OneboxRestException.builder(MsEventSessionErrorCode.INVOICE_PREFIX_NOT_FOUND).build();
                }
            }
        }
        if (request.getEnableProducerTaxData() != null && request.getEnableProducerTaxData().equals(Boolean.TRUE) &&
                request.getProducerId() == null && sessionRecord.getIdpromotor() == null) {
            throw OneboxRestException.builder(MsEventSessionErrorCode.PRODUCER_ID_DO_NOT_EXIST).build();
        }

        if (request.getSubscriptionListId() != null) {
            SubscriptionDTO subscription = subscriptionsRepository.getSubscriptionList(eventRecord.getIdentidad(), request.getSubscriptionListId());
            if (subscription == null || BooleanUtils.isFalse(subscription.getActive())) {
                throw OneboxRestException.builder(MsEventSessionErrorCode.SUBSCRIPTION_LIST_ID_NOT_FOUND).build();
            }
        }
        if (Boolean.TRUE.equals(request.getEnableCountryFilter()) && (CollectionUtils.isEmpty(request.getCountries()) ||
                (request.getCountries() == null && (sessionConfig == null || sessionConfig.getRestrictions() == null ||
                        sessionConfig.getRestrictions().getCountryConfig() == null ||
                        CollectionUtils.isEmpty(sessionConfig.getRestrictions().getCountryConfig().getCountries()))))) {
            throw OneboxRestException.builder(MsEventSessionErrorCode.EMPTY_COUNTRY_FILTER_LIST).build();
        }
        if (Boolean.TRUE.equals(request.getEnableQueue()) && StringUtils.isBlank(request.getQueueAlias()) && (sessionConfig == null ||
                sessionConfig.getQueueItConfig() == null || StringUtils.isBlank(sessionConfig.getQueueItConfig().getAlias()))) {
            throw OneboxRestException.builder(MsEventSessionErrorCode.QUEUE_ALIAS_MANDATORY).build();
        }

        if (SessionStatus.READY.equals(request.getStatus()) && BooleanUtils.isTrue(sessionRecord.getIsexternal()) && !EventType.AVET.getId().equals(eventRecord.getTipoevento())) {
            ExternalBarcodeEntityConfigDTO entityConfig = this.entitiesRepository.getExternalEntityBarcodeConfig(eventRecord.getIdentidad());
            ExternalBarcodeSessionConfigDTO barcodeSessionConfigDTO = sessionExternalBarcodeConfigService.getExternalBarcodeSessionConfig(sessionRecord.getIdsesion().longValue());
            if (entityConfig != null && BooleanUtils.isTrue(entityConfig.getAllowExternalBarcode()) && barcodeSessionConfigDTO == null) {
                throw new OneboxRestException(MsEventSessionErrorCode.SESSION_EXTERNAL_BARCODES_MUST_BE_CONFIGURED);
            }
        }
    }

    private static void checkTax(Long tax, List<Long> eventTaxes) {
        if (tax != null && !eventTaxes.contains(tax)) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_TAX, "Invalid tax for session", null);
        }
    }

    private Map<Long, String> validateUpdate(Long eventId, UpdateSessionsRequestDTO updateSessionsRequestDTO,
                                             List<SessionRecord> sessionsToUpdate, Map<Integer, SessionConfig> sessionConfigs,
                                             Map<Long, Long> sessionOperations, Map<Integer, SessionSecondaryMarketConfigExtended> sessionSecondaryMarketConfigs) {
        if (sessionsToUpdate == null) {
            throw OneboxRestException.builder(MsEventSessionErrorCode.SESSION_NOT_FOUND).build();
        }
        boolean deletingSessions = SessionStatus.DELETED.equals(updateSessionsRequestDTO.getValue().getStatus());
        // result container
        Map<Long, String> updateStatus = new HashMap<>();
        CpanelEventoRecord eventRecord = eventDao.getById(eventId.intValue());
        // received rates check
        if (!deletingSessions) {
            this.validateRates(eventId, updateSessionsRequestDTO);
        }
        // session by session validation
        for (SessionRecord sessionRecord : sessionsToUpdate) {
            String errorCode = null;
            errorCode = extractErrorCode(eventId, updateSessionsRequestDTO, deletingSessions, eventRecord,
                    sessionRecord, errorCode, sessionConfigs.get(sessionRecord.getIdsesion()),
                    sessionOperations.containsKey(sessionRecord.getIdsesion().longValue()),
                    sessionSecondaryMarketConfigs != null ? sessionSecondaryMarketConfigs.get(sessionRecord.getIdsesion()) : null);
            updateStatus.put(sessionRecord.getIdsesion().longValue(), errorCode);
        }
        return updateStatus;
    }

    private String extractErrorCode(Long eventId, UpdateSessionsRequestDTO updateSessionsRequestDTO,
                                    boolean deletingSessions, CpanelEventoRecord eventRecord, SessionRecord sessionRecord, String errorCode,
                                    SessionConfig sessionConfig, boolean sessionHasOperations, SessionSecondaryMarketConfigExtended sessionSecondaryMarketConfig) {
        if (!eventId.equals(sessionRecord.getIdevento().longValue())) {
            // event session mismatching
            errorCode = SESSION_NOT_MATCH_EVENT.getErrorCode();
        } else if (CommonUtils.isTrue(sessionRecord.getEsabono())) {
            // season check
            errorCode = BULK_SEASON_UPDATE_FORBIDEN.getErrorCode();
        } else if (deletingSessions) {
            if (sessionHasOperations) {
                // session with operations checks
                errorCode = SESSION_WITH_BOOKED_SEAT.getErrorCode();
            } else if (CollectionUtils.isNotEmpty(
                    seasonSessionDao.findSessionPacksBySessionId(sessionRecord.getIdsesion().longValue()))) {
                // not included in seasons check
                errorCode = SEASON_LOCK.getErrorCode();
            }
        } else {
            // other checks
            try {
                validateUpdate(updateSessionsRequestDTO.getValue(), sessionRecord, eventRecord, sessionConfig, sessionSecondaryMarketConfig);
            } catch (OneboxRestException e) {
                errorCode = e.getErrorCode();
            }
        }
        return errorCode;
    }

    private void validateRates(Long eventId, UpdateSessionsRequestDTO updateSessionsRequestDTO) {
        if (!CommonUtils.isEmpty(updateSessionsRequestDTO.getValue().getRates())) {
            List<RateRecord> eventRates = rateDao.getRatesByEventId(eventId.intValue());
            SessionValidator.validateRates(eventRates, updateSessionsRequestDTO.getValue().getRates());
        }
    }

    private void createSessionsRates(List<Long> sessionIds, List<RateDTO> rates) {
        List<SessionRate> sessionRates = new ArrayList<>();

        if (!CommonUtils.isEmpty(rates)) {
            for (Long sessionId : sessionIds) {
                for (RateDTO sessionRate : rates) {
                    sessionRates.add(new SessionRate(sessionId, sessionRate.getId().intValue(), sessionRate.isDefault()));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(sessionRates)) {
            sessionRateDao.bulkInsertSessionRates(sessionRates);
        }
    }

    private void fillSessionPackInformation(SessionDTO sessionDataDTO) {
        if (sessionDataDTO.getSessionType() == SessionType.SESSION) {
            sessionDataDTO.setSeasonIds(seasonSessionDao.findSessionPacksBySessionId(sessionDataDTO.getId()));
        } else {
            sessionDataDTO.setSessionIds(seasonSessionDao.findSessionsBySessionPackId(sessionDataDTO.getId()));
        }
    }

    private void fillRatesInformation(SessionDTO sessionDTO) {
        List<CpanelTarifaRecord> sessionRates = sessionRateDao.getRatesBySessionId(sessionDTO.getId().intValue());
        List<RateDTO> rates = new ArrayList<>();

        for (CpanelTarifaRecord sessionRate : sessionRates) {
            RateDTO rateDTO = new RateDTO();
            rateDTO.setId(sessionRate.getIdtarifa().longValue());
            rateDTO.setName(sessionRate.getNombre());
            rateDTO.setDefault(ONE.equals(sessionRate.getDefecto()));
            rateDTO.setPosition(sessionRate.getPosition());
            rates.add(rateDTO);
        }
        sessionDTO.setRates(rates);
    }

    private void fillPreSalesInformation(SessionDTO sessionDTO) {
        List<CpanelPreventaRecord> presales = presaleDao.findSessionPresalesBySessionId(sessionDTO.getId());
        if (CollectionUtils.isNotEmpty(presales)) {
            sessionDTO.setPreSales(new ArrayList<>());
            presales.forEach(presale -> {
                long presaleId = presale.getIdpreventa().longValue();
                List<Integer> channelIds = presaleChannelDao.findPresaleChannelIds(presaleId);
                List<Integer> customerTypeIds = presaleCustomTypeDao.findPresaleCustomTypeIds(presaleId);
                CpanelPreventaLoyaltyProgramRecord loyaltyProgram = presaleLoyaltyProgramDao.getByPresaleId(presaleId);
                sessionDTO.getPreSales().add(SessionPreSaleConfigConverter.toDTO(presale, channelIds, customerTypeIds, loyaltyProgram));
            });
        }
    }

    private void fillSessionConfigInformation(SessionDTO sessionDTO) {
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionDTO.getId());

        fillMembersLoginsLimitInformation(sessionDTO, sessionConfig);
        fillCountryFilterInformation(sessionDTO, sessionConfig);
        fillQueueItInformation(sessionDTO, sessionConfig);
        fillStreamingInformation(sessionDTO, sessionConfig);
        fillSessionPresalesInformation(sessionDTO, sessionConfig);
        fillSessionExternalConfig(sessionDTO, sessionConfig);
    }

    private static void fillMembersLoginsLimitInformation(SessionDTO sessionDTO, SessionConfig sessionConfig) {
        if (sessionConfig.getMaxMembers() != null) {
            sessionDTO.setEnableMembersLoginsLimit(Boolean.TRUE);
            sessionDTO.setMembersLoginsLimit(sessionConfig.getMaxMembers());
        } else {
            sessionDTO.setEnableMembersLoginsLimit(Boolean.FALSE);
        }
    }

    private static void fillCountryFilterInformation(SessionDTO sessionDTO, SessionConfig sessionConfig) {
        if (sessionConfig.getRestrictions() != null && sessionConfig.getRestrictions().getCountryConfig() != null) {
            sessionDTO.setEnableCountryFilter(sessionConfig.getRestrictions().getCountryConfig().isActive());
            if (CollectionUtils.isNotEmpty(sessionConfig.getRestrictions().getCountryConfig().getCountries())) {
                sessionDTO.setCountries(sessionConfig.getRestrictions().getCountryConfig().getCountries());
            }
        }
    }

    private static void fillQueueItInformation(SessionDTO sessionDTO, SessionConfig sessionConfig) {
        if (sessionConfig.getQueueItConfig() != null) {
            sessionDTO.setQueueAlias(sessionConfig.getQueueItConfig().getAlias());
            sessionDTO.setEnableQueue(sessionConfig.getQueueItConfig().isActive());
            sessionDTO.setSkipQueueToken(sessionConfig.getQueueItConfig().getValue());
            if (sessionConfig.getQueueItConfig().getVersion() != null) {
                sessionDTO.setQueueVersion(SessionVirtualQueueVersion.getByName(sessionConfig.getQueueItConfig().getVersion()));
            } else {
                sessionDTO.setQueueVersion(SessionVirtualQueueVersion.V3);
            }
        }
    }

    private static void fillStreamingInformation(SessionDTO sessionDTO, SessionConfig sessionConfig) {
        StreamingVendorConfig vendorConfig = sessionConfig.getStreamingVendorConfig();
        if (vendorConfig != null) {
            SessionStreamingDTO streaming = new SessionStreamingDTO();
            streaming.setEnabled(vendorConfig.getEnabled());
            if (vendorConfig.getVendor() != null) {
                streaming.setVendor(StreamingVendor.valueOf(vendorConfig.getVendor().name()));
            }
            streaming.setSendEmail(vendorConfig.getSendEmail() == null || BooleanUtils.isTrue(vendorConfig.getSendEmail()));
            streaming.setValue(vendorConfig.getValue());
            sessionDTO.setStreaming(streaming);
        }
    }

    private static void fillSessionPresalesInformation(SessionDTO sessionDTO, SessionConfig sessionConfig) {
        SessionPresalesConfig sessionPresalesConfig = sessionConfig.getSessionPresalesConfig();

        if (sessionPresalesConfig != null && sessionPresalesConfig.getPresalesRedirectionPolicy() != null) {
            PresalesRedirectionPolicyDTO presalesRedirectionPolicy = new PresalesRedirectionPolicyDTO();

            presalesRedirectionPolicy.setValue(sessionPresalesConfig.getPresalesRedirectionPolicy().getValue());
            if (sessionPresalesConfig.getPresalesRedirectionPolicy().getMode() != null) {
                presalesRedirectionPolicy.setMode(PresalesLinkMode.valueOf(sessionPresalesConfig.getPresalesRedirectionPolicy().getMode().name()));
            }

            sessionDTO.setPresalesRedirectionPolicy(presalesRedirectionPolicy);
        }
    }

    private static void fillSessionExternalConfig(SessionDTO sessionDTO, SessionConfig sessionConfig) {
        SessionExternalConfig sessionExternalConfig = sessionConfig.getSessionExternalConfig();
        if (sessionExternalConfig != null) {
            SessionExternalConfigDTO sessionExternalConfigDTO = new SessionExternalConfigDTO();
            sessionExternalConfigDTO.setDigitalTicketMode(sessionConfig.getSessionExternalConfig().getDigitalTicketMode());
            sessionDTO.setSessionExternalConfig(sessionExternalConfigDTO);
        }
    }


    private void validateCreation(Long eventId, CreateSessionDTO session, CpanelEventoRecord event, List<RateRecord> eventRates,
                                  Boolean smartBookingEnabled) {
        if (session.getEventId() != null && !eventId.equals(session.getEventId())) {
            throw OneboxRestException.builder(CoreErrorCode.BAD_PARAMETER).setHttpStatus(HttpStatus.BAD_REQUEST)
                    .setMessage("All sessions must belong to event " + eventId).build();
        }
        session.setEventId(eventId);
        String name = session.getName();
        if (name == null || name.isEmpty()) {
            throw new OneboxRestException(INVALID_NAME_FORMAT, "session name is mandatory", null);
        }
        SessionValidator.validateSessionName(name);
        if (session.getVenueConfigId() == null && session.getVenueEntityConfigId() == null) {
            throw new OneboxRestException(INVALID_VENUE_TEMPLATE, "One of venueConfigId or venueEntityConfigId parameters is mandatory", null);
        }
        CpanelConfigRecintoRecord venueTemplate = eventDao.getEventVenueTemplate(eventId, session.getVenueConfigId(), session.getVenueEntityConfigId());
        if (venueTemplate == null) {
            throw new OneboxRestException(INVALID_VENUE_TEMPLATE, "venueConfigId or venueEntityConfigId is not valid for event", null);
        }
        if (!EventType.AVET.getId().equals(event.getTipoevento())) {
            SessionValidator.validateRates(eventRates, session.getRates());
        }
        if (CollectionUtils.isNotEmpty(session.getSeasonSessions()) && (event.getTipoabono() == null || SessionPackType.DISABLED.getId() == event.getTipoabono())) {
            throw new OneboxRestException(MsEventSessionErrorCode.EVENT_SESSION_PACKS_NOT_ALLOWED);
        }
        if (session.getTaxId() == null || session.getChargeTaxId() == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_TAX, "Tax are mandatory", null);
        }
        List<Long> eventTaxes = taxDao.getEventTaxes(eventId);
        checkTax(session.getTaxId(), eventTaxes);
        checkTax(session.getChargeTaxId(), eventTaxes);
        if (BooleanUtils.isTrue(session.getSmartBooking()) && BooleanUtils.isFalse(smartBookingEnabled)) {
            throw new OneboxRestException(MsEventSessionErrorCode.SMART_BOOKING_NOT_ALLOWED);
        }
        validateSessionPackParams(event, session, venueTemplate);
        SessionValidator.validateActivity(session, event);
        SessionValidator.validateCreateDates(session, event);
        SessionValidator.validateSessionSalesType(session);
    }

    private void validateSessionPackParams(CpanelEventoRecord event, CreateSessionDTO session, CpanelConfigRecintoRecord venueTemplate) {
        if (CommonUtils.isTrue(session.getSeasonPass()) && CommonUtils.isFalse(session.getSeasonTicket())) {
            if (CommonUtils.isEmpty(session.getSeasonSessions())) {
                throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "seasonSesssions is required for pack", null);
            }
            checkSessionPackRelatedSessions(session, event.getIdevento().longValue());
            if (CommonUtils.isTrue(session.getAllowPartialRefund())) {
                if (SessionPackType.UNRESTRICTED.getId() != event.getTipoabono()) {
                    throw new OneboxRestException(MsEventSessionErrorCode.SESSION_CREATE_PACK_ALLOW_PARTIAL_REFUND);
                }
                if (CommonUtils.isTrue(event.getUsetieredpricing())) {
                    throw new OneboxRestException(MsEventSessionErrorCode.SESSION_PARTIAL_REFUND_TIERS_INCOMPATIBLE);
                }

            }

            if (!MapUtils.isEmpty(session.getSeasonPassBlockingActions())) {
                for (Integer blockingReason : session.getSeasonPassBlockingActions().keySet()) {
                    CpanelRazonBloqueoRecord record = blockingReasonDao.findById(blockingReason);
                    if (record == null || !record.getIdconfiguracion().equals(venueTemplate.getIdconfiguracion())) {
                        throw new OneboxRestException(MsEventSessionErrorCode.SESSION_CREATE_PACK_BLOCKING_REASON, "Invalid blocking reason for session", null);
                    }
                }
            }
        }
    }

    private void checkSessionPackRelatedSessions(CreateSessionDTO session, Long eventId) {
        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setEventId(Collections.singletonList(eventId));
        List<CpanelSesionRecord> eventSessions = sessionDao.findFlatSessions(filter);
        for (Long seasonSession : session.getSeasonSessions()) {
            CpanelSesionRecord sessionRecord = eventSessions.stream().
                    filter(s -> s.getIdsesion().equals(seasonSession.intValue())).
                    findFirst().orElse(null);
            if (sessionRecord == null || CommonUtils.isTrue(sessionRecord.getEsabono())) {
                throw new OneboxRestException(SESSION_NOT_MATCH_EVENT, "Session " + seasonSession + " not valid for event", null);
            }
            if (!SessionStatus.SCHEDULED.getId().equals(sessionRecord.getEstado())) {
                throw new OneboxRestException(MsEventSessionErrorCode.SESSION_CREATE_PACK_STATUS, null);
            }
        }
    }

    private void validateClone(SessionRecord sourceSession) {
        if (!SessionGenerationStatus.ACTIVE.getId().equals(sourceSession.getEstadogeneracionaforo())) {
            throw new OneboxRestException(MsEventSessionErrorCode.SESSION_CLONE_GENERATION_STATUS);
        }
        if (CommonUtils.isTrue(sourceSession.getEsabono())) {
            throw new OneboxRestException(MsEventSessionErrorCode.SESSION_CLONE_SESSION_PACK);
        }
    }

    private void cloneSessionRates(Long sessionId, long newSessionId) {
        Collection<RateRecord> sessionRates = rateDao.getRatesBySessionId(sessionId.intValue(), null, null);
        List<SessionRate> newSessionRates = sessionRates.stream().map(
                        r -> new SessionRate(newSessionId, r.getIdTarifa(), CommonUtils.isTrue(r.getDefecto()))).
                collect(toList());
        sessionRateDao.bulkInsertSessionRates(newSessionRates);
    }

    private void fillDefaultValues(CreateSessionDTO session, CpanelEventoRecord event, EventConfig eventConfig) {
        session.setStatus(SessionStatus.SCHEDULED);
        session.setCapacityGenerationStatus(SessionGenerationStatus.IN_PROGRESS.getId());
        session.setExternalId(session.getExternalId());
        session.setExternal(isExternalSession(session, event, eventConfig));
        session.setCapacity(0); //This value will be updated after seat generation on oracle

        boolean activityEvent = EventUtils.isActivity(event.getTipoevento());
        if (activityEvent) {
            if (session.getUseLimitsQuotasTemplateEvent() == null) {
                session.setUseLimitsQuotasTemplateEvent(true);
            }
            session.setUseTemplateAccess(true);
        }

        if (session.getVenueEntityConfigId() == null) {
            CpanelEntidadRecintoConfigRecord venueTemplateRelation = entityVenueTemplateDao.getByVenueTemplateId(session.getVenueConfigId());
            session.setVenueEntityConfigId(venueTemplateRelation.getIdrelacionentrecinto().longValue());
        }

        if (session.getSaleType() == null) {
            session.setSaleType(SessionSalesType.INDIVIDUAL.getType());
        }
        ExternalBarcodeEventConfigDTO externalBarcodeEventConfig = eventExternalBarcodeConfigService.getExternalBarcodeEventConfig(session.getEventId());
        if (externalBarcodeEventConfig != null) {
            session.setExternal(externalBarcodeEventConfig.getAllow());
        }
        session.setPublished(true);
        session.setBookings(ConverterUtils.isByteAsATrue(event.getPermitereservas()));
        session.setOnSale(true);
        session.setUseProducerTaxData(Boolean.FALSE);
        session.setProducerId(event.getIdpromotor().longValue());
        if (event.getInvoiceprefixid() != null) {
            session.setInvoicePrefixId(event.getInvoiceprefixid().longValue());
        }
    }

    private static boolean isExternalSession(CreateSessionDTO session, CpanelEventoRecord event, EventConfig eventConfig) {
        return CommonUtils.isTrue(session.getExternal())
                || (session.getExternalId() != null && !EventType.THEME_PARK.getId().equals(event.getTipoevento())
                || eventConfig != null && eventConfig.getInventoryProvider() != null);
    }

    private static void fillCloneDates(CpanelSesionRecord session, CloneSessionDTO sessionData) {
        long dateDiff = sessionData.getStartDate().toInstant().toEpochMilli() - session.getFechainiciosesion().getTime();
        session.setFechainiciosesion(CommonUtils.zonedDateTimeToTimestamp(sessionData.getStartDate()));
        session.setFechafinsesion(new Timestamp(session.getFechafinsesion().getTime() + dateDiff));
        session.setFechapublicacion(new Timestamp(session.getFechapublicacion().getTime() + dateDiff));
        session.setFechaventa(new Timestamp(session.getFechaventa().getTime() + dateDiff));

        if (sessionData.getEndDate() != null) {
            session.setFecharealfinsesion(CommonUtils.zonedDateTimeToTimestamp(sessionData.getEndDate()));
        } else if (session.getFecharealfinsesion() != null) {
            session.setFecharealfinsesion(new Timestamp(session.getFecharealfinsesion().getTime() + dateDiff));
        }
        if (session.getFechainicioreserva() != null) {
            session.setFechainicioreserva(new Timestamp(session.getFechainicioreserva().getTime() + dateDiff));
        }
        if (session.getFechafinreserva() != null) {
            session.setFechafinreserva(new Timestamp(session.getFechafinreserva().getTime() + dateDiff));
        }
        if (session.getTipohorarioaccesos() != null && AccessHourType.SPECIFIC.getId() == session.getTipohorarioaccesos()) {
            session.setAperturaaccesos(new Timestamp(session.getAperturaaccesos().getTime() + dateDiff));
            session.setCierreaccesos(new Timestamp(session.getCierreaccesos().getTime() + dateDiff));
        }
        session.setCreateDate(Timestamp.from(Instant.now()));
        session.setUpdateDate(session.getCreateDate());
    }

    private void checkCloneBookings(Long eventId, SessionRecord sourceSession, CpanelSesionRecord clonedSession) {
        if (CommonUtils.isTrue(sourceSession.getReservasactivas()) && sourceSession.getFechafinreserva() != null) {
            CpanelEventoRecord eventRecord = eventDao.findById(eventId.intValue());
            clonedSession.setReservasactivas((byte) 0);
            if (CommonUtils.isTrue(eventRecord.getPermitereservas())) {
                boolean validation = true;
                try {
                    SessionValidator.validateBookingsEnd(eventRecord,
                            DateUtils.getZonedDateTime(clonedSession.getFechainiciosesion()),
                            DateUtils.getZonedDateTime(clonedSession.getFechafinsesion()),
                            DateUtils.getZonedDateTime(clonedSession.getFechainicioreserva()),
                            DateUtils.getZonedDateTime(clonedSession.getFechafinreserva()));
                } catch (OneboxRestException e) {
                    validation = false;
                }
                if (validation) {
                    clonedSession.setReservasactivas((byte) 1);
                }
            }
        }
    }

    private void deleteSessions(Long eventId, UpdateSessionsRequestDTO updateSessionsRequestDTO,
                                List<SessionRecord> sessionsToUpdate, List<Long> sessionIds,
                                Map<Long, Long> sessionsOperations) {
        for (SessionRecord sessionRecord : sessionsToUpdate) {
            if (sessionIds.contains(sessionRecord.getIdsesion().longValue())) {
                sessionRecord.setEstado(SessionStatus.DELETED.getId());
                deleteSession(eventId, updateSessionsRequestDTO.getValue(), sessionRecord,
                        sessionsOperations.containsKey(sessionRecord.getIdsesion().longValue()));
            }
        }
    }

    private void deleteSession(Long eventId, UpdateSessionRequestDTO request, CpanelSesionRecord sessionRecord,
                               boolean sessionHasOperations) {
        Long sessionId = sessionRecord.getIdsesion().longValue();
        if (!sessionHasOperations) {
            CpanelEventoRecord eventRecord = eventDao.getById(eventId.intValue());

            if (CommonUtils.isTrue(sessionRecord.getEsabono())) {
                checkBlockingReason(sessionRecord, request);
            } else {
                //Check relation with any session pack
                List<Long> packSessionIds = seasonSessionDao.findSessionPacksBySessionId(sessionRecord.getIdsesion().longValue());
                if (CollectionUtils.isNotEmpty(packSessionIds)) {
                    throw OneboxRestException.builder(MsEventErrorCode.SEASON_LOCK)
                            .setHttpStatus(HttpStatus.BAD_REQUEST)
                            .setMessage("Session not allowed to remove is related with season session, sessionId: " + sessionRecord.getIdsesion())
                            .build();
                }
            }

            if (sessionRecord.getSbsesionrelacionada() != null) {
                Integer venueTemplateType = sessionDao.getVenueTemplateTypeBySessionId(sessionRecord.getIdsesion());
                if (EventUtils.isSbAvetSession(sessionRecord, venueTemplateType)) {
                    CpanelSesionRecord relatedSBSession = sessionDao.getById(sessionRecord.getSbsesionrelacionada());
                    deleteSession(sessionRecord.getIdevento().longValue(), request, relatedSBSession, sessionHasOperations);
                }
            }
            if (sessionConfigCouchDao.get(sessionId.toString()) != null) {
                sessionConfigCouchDao.remove(sessionId.toString());
            }
            if (sessionLoyaltyPointsConfigCouchDao.get(sessionId.toString()) != null) {
                sessionLoyaltyPointsConfigCouchDao.remove(sessionId.toString());
            }
            attendantsConfigService.deleteSessionAttendantsConfig(sessionId, eventId);

            List<CpanelTarifaRecord> rates = sessionRateDao.getRatesBySessionId(sessionRecord.getIdsesion());

            sessionRateDao.cleanRatesForSessionId(sessionId.intValue());

            if (EventUtils.isAvet(eventRecord.getTipoevento())) {
                for (CpanelTarifaRecord rate : rates) {
                    // Remove TARIFA, ASIGNACION_ZONA_PRECIOS & PRECIOS_GRUPOS.
                    eventRateService.deleteEventRateForAvetEvents(eventId.intValue(), rate.getIdtarifa(), sessionId.intValue());
                }
                if (isAvetSocketEvent(eventRecord.getTipoevento(), eventId)) {
                    avetAvailabilityMatchScheduleService.deleteAvetAvailabilitySchedule(sessionRecord.getIdexterno(),
                            sessionRecord.getIdsesion());
                }

                List<RateGroupSessionRecord> rateSessions = rateGroupDao.getSessionsDefaultRatesBySessionId(sessionRecord.getIdsesion());
                CpanelTarifaRecord cpanelTarifaRecord;
                //Delete cpanelSesionRecord for any session already exists
                for (RateGroupSessionRecord rateSession : rateSessions) {
                    cpanelTarifaRecord = rateDao.getEventRate(eventId.intValue(), rateSession.getIdTarifa());
                    if (null != cpanelTarifaRecord) {
                        rateDao.delete(cpanelTarifaRecord);
                        rateGroupDao.deleteSessionRate(rateSession.getIdSesion(), rateSession.getIdTarifa());
                    }
                }
            }

            // Force nullify idExterno on removeSession to prevent problems with avet match configuration
            sessionRecord.setIdexterno(null);

            sessionDao.update(sessionRecord);
            eventDao.updateEventDatesFromSessionCriteria(eventId);

            removeSeats(request, sessionRecord, eventRecord.getTipoabono());

            seasonSessionDao.unlinkAllSessionsOfPack(sessionId);

            priceTypeLabelSessionDao.deleteBySessionIds(Collections.singleton(sessionId.intValue()));
        } else {
            throw OneboxRestException.builder(MsEventSessionErrorCode.SESSION_WITH_BOOKED_SEAT)
                    .setHttpStatus(HttpStatus.BAD_REQUEST)
                    .setMessage("Session with booked seats, sessionId: " + sessionId)
                    .build();
        }
    }

    private void removeSeats(UpdateSessionRequestDTO session, CpanelSesionRecord sessionRecord, Byte sessionPackType) {
        if (CommonUtils.isTrue(sessionRecord.getEsabono())) {
            List<Integer> sessionIds = seasonSessionDao.findSessionsBySessionPackId(sessionRecord.getIdsesion().longValue())
                    .stream().map(Long::intValue).collect(Collectors.toList());
            SeatDeleteStatus newStatus = session.getDeleteData() != null ? session.getDeleteData().getStatus() : null;
            Integer newBlockingReason = session.getDeleteData() != null ? session.getDeleteData().getBlockingReasonId() : null;
            seatRemoveService.removeSeats(sessionRecord.getIdsesion(), true,
                    newStatus != null ? newStatus.getId() : null, newBlockingReason,
                    sessionIds, sessionPackType);
        } else {
            boolean hasCancelledOrders = ordersRepository.numberOperations(Collections.singletonList(sessionRecord.getIdsesion()),
                    Collections.singletonList(OrderState.CANCELLED), null) > 0;
            if (hasCancelledOrders) {
                sessionRecord.setEstadopurgado(SessionGenerationStatus.PENDING.getId().byteValue());
            } else {
                seatRemoveService.removeSeats(sessionRecord.getIdsesion());
            }
        }
    }

    private void bulkUpdateRates(List<Long> sessionIds, UpdateSessionRequestDTO sessionData) {
        if (!CommonUtils.isEmpty(sessionData.getRates())) {
            List<SessionRate> sessionRates = new ArrayList<>();
            for (RateDTO sessionRate : sessionData.getRates()) {
                for (Long sessionId : sessionIds) {
                    sessionRates.add(new SessionRate(sessionId, sessionRate.getId().intValue(), sessionRate.isDefault()));
                }
            }
            sessionRateDao.cleanRatesForSessionIds(sessionIds);
            int updatedRates = sessionRateDao.bulkInsertSessionRates(sessionRates);

            if (updatedRates != (sessionData.getRates().size() * sessionIds.size())) {
                throw new OneboxRestException(CoreErrorCode.PERSISTENCE_ERROR,
                        "Unexpected error on bulk update of sessions rates", null);
            }
        }
    }

    private void checkBlockingReason(CpanelSesionRecord sessionRecord, UpdateSessionRequestDTO request) {
        if (request.getDeleteData() != null && request.getDeleteData().getBlockingReasonId() != null) {
            CpanelEntidadRecintoConfigRecord venueTemplateRelation = entityVenueTemplateDao.getById(sessionRecord.getIdrelacionentidadrecinto());
            CpanelRazonBloqueoRecord blockingReason = blockingReasonDao.findById(request.getDeleteData().getBlockingReasonId());
            if (!blockingReason.getIdconfiguracion().equals(venueTemplateRelation.getIdconfiguracion())) {
                throw OneboxRestException.builder(CoreErrorCode.BAD_PARAMETER)
                        .setMessage("Blocking reason with id: " + blockingReason.getIdconfiguracion() +
                                " is not from venuetemplate: " + venueTemplateRelation.getIdconfiguracion())
                        .build();
            }
        }
    }

    private boolean isAvetSocketEvent(Integer eventType, Long eventId) {
        boolean isAvetSocketEvent = false;
        if (EventUtils.isAvet(eventType)) {
            if (eventAvetConfigCouchDao.get(eventId.toString()) == null) {
                isAvetSocketEvent = false;
            } else {
                isAvetSocketEvent = eventAvetConfigCouchDao.get(eventId.toString()).getIsSocket();
            }
        }
        return isAvetSocketEvent;
    }

    private void cloneAttendantsConfig(Long eventId, Long sourceSessionId, Long targetSessionId) {
        SessionAttendantsConfigDTO sac = attendantsConfigService.getSessionAttendantsConfig(sourceSessionId);
        if (sac != null) {
            SessionAttendantsConfig newSac = new SessionAttendantsConfig();
            newSac.setSessionId(targetSessionId);
            newSac.setActive(sac.getActive());
            newSac.setAllChannelsActive(sac.getAllChannelsActive());
            newSac.setAutomaticChannelAssignment(sac.getAutomaticChannelAssignment());
            newSac.setActiveChannels(sac.getActiveChannels());
            newSac.setAutofill(sac.getAutofill());
            attendantsConfigService.upsertSessionAttendantsConfig(targetSessionId, eventId, newSac);
        }

    }

    private void cloneSessionConfig(Long sourceSessionId, Long targetSessionId) {
        SessionConfig sourceSessionConfig = sessionConfigCouchDao.get(sourceSessionId.toString());
        if (sourceSessionConfig == null) {
            return;
        }
        SessionConfig targetSessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(targetSessionId);
        targetSessionConfig.setRestrictions(sourceSessionConfig.getRestrictions());
        targetSessionConfig.setPriceTypeLimits(sourceSessionConfig.getPriceTypeLimits());
        targetSessionConfig.setCustomersLimits(sourceSessionConfig.getCustomersLimits());

        sessionConfigCouchDao.upsert(targetSessionId.toString(), targetSessionConfig);
    }

    private void cloneLoyaltyPointsConfig(Long sourceSessionId, Long targetSessionId) {
        SessionLoyaltyPointsConfig sourceSessionLoyaltyPointsConfig = sessionLoyaltyPointsConfigCouchDao.get(sourceSessionId.toString());
        if (sourceSessionLoyaltyPointsConfig == null) {
            return;
        }
        SessionLoyaltyPointsConfig targetSessionLoyaltyPointsConfig = sessionLoyaltyPointsConfigCouchDao.getOrInitSessionLoyaltyPointsConfig(targetSessionId);
        targetSessionLoyaltyPointsConfig.setPointGain(sourceSessionLoyaltyPointsConfig.getPointGain());

        sessionLoyaltyPointsConfigCouchDao.upsert(targetSessionId.toString(), targetSessionLoyaltyPointsConfig);
    }

    public void initSessionConfig(Long sessionId, Long eventId) {
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        sessionConfig.setEventId(eventId);
        sessionConfigCouchDao.upsert(sessionId.toString(), sessionConfig);
    }

    public void updateGateId(Long eventId, Long sessionId, Long priceTypeId, PriceTypeRequestDTO request) {
        SessionRecord sessionRecord = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);

        List<ZonaPreciosConfigRecord> priceZones = priceZoneConfigDao.getPriceZone(sessionRecord.getVenueTemplateId().longValue(), null);
        if (priceZones.stream().noneMatch(priceZone -> priceZone.getIdzona().equals(priceTypeId.intValue()))) {
            throw OneboxRestException.builder(MsEventSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION).build();
        }
        if (request.getAdditionalConfig() != null && request.getAdditionalConfig().getGateId() != null) {

            if (venuesRepository.getGates(sessionRecord.getVenueTemplateId().longValue())
                    .stream()
                    .filter(currentGate -> currentGate.getId().equals(request.getAdditionalConfig().getGateId()))
                    .count() == 0) {
                throw OneboxRestException.builder(MsEventSessionErrorCode.GATE_ID_INVALID).build();
            }

            priceTypeLabelSessionDao.delete(priceTypeId, sessionId);

            CpanelZonaPrecioEtiquetaSesionRecord cpanelZonaPrecioEtiquetaSesionRecord = new CpanelZonaPrecioEtiquetaSesionRecord();

            cpanelZonaPrecioEtiquetaSesionRecord.setIdsesion(sessionId.intValue());
            cpanelZonaPrecioEtiquetaSesionRecord.setIdzonaprecio(priceTypeId.intValue());
            cpanelZonaPrecioEtiquetaSesionRecord.setIdetiqueta(request.getAdditionalConfig().getGateId().intValue());

            priceTypeLabelSessionDao.insert(cpanelZonaPrecioEtiquetaSesionRecord);
        }
    }

    public SessionGroupConfigDTO getSessionGroup(Integer eventId, Integer sessionId) {
        CpanelConfigSesionGruposRecord groupConfigRecord = sessionGroupDao.findById(sessionId);

        SessionGroupConfigDTO groupConfig = new SessionGroupConfigDTO();
        if (groupConfigRecord != null) {
            groupConfig = SessionConverter.toGroupConfigDTO(groupConfigRecord);
        }
        return groupConfig;
    }

    @MySQLWrite
    public void updateSessionGroup(Long eventId, Long sessionId, SessionGroupConfigDTO request) {
        checkActivitySession(eventId, sessionId);

        CpanelConfigSesionGruposRecord groupConfigRecord = sessionGroupDao.findById(sessionId.intValue());
        if (groupConfigRecord == null) {
            groupConfigRecord = new CpanelConfigSesionGruposRecord();
        }
        checkUpdateGroupLimits(request.getMinAttendees(), request.getMaxAttendees(), groupConfigRecord.getMinasistentes());
        checkUpdateGroupLimits(request.getMinCompanions(), request.getMaxCompanions(), groupConfigRecord.getMinasistentes());
        SessionConverter.updateSessionGroupRecord(groupConfigRecord, request);
        if (groupConfigRecord.getMinacompanyantes() == null) {
            groupConfigRecord.setMinacompanyantes(DEFAULT_GROUP_MIN);
        }
        if (groupConfigRecord.getMinasistentes() == null) {
            groupConfigRecord.setMinasistentes(DEFAULT_GROUP_MIN);
        }
        if (groupConfigRecord.changed()) {
            if (groupConfigRecord.getIdsesion() == null) {
                groupConfigRecord.setIdsesion(sessionId.intValue());
                sessionGroupDao.insert(groupConfigRecord);
            } else {
                sessionGroupDao.update(groupConfigRecord);
            }
        }
    }

    @MySQLWrite
    public void deleteSessionGroup(Long eventId, Long sessionId) {
        checkActivitySession(eventId, sessionId);

        CpanelConfigSesionGruposRecord record = new CpanelConfigSesionGruposRecord();
        record.setIdsesion(sessionId.intValue());
        sessionGroupDao.delete(record);
    }

    private void checkActivitySession(Long eventId, Long sessionId) {
        SessionRecord sessionRecord = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);

        if (!EventUtils.isActivity(sessionRecord.getEventType())) {
            throw OneboxRestException.builder(MsEventSessionErrorCode.GROUPS_ACTIVITY_SESSION).build();
        }
    }

    private static void checkUpdateGroupLimits(Integer lowerLimit, Integer upperLimit, Integer lowerLimitRecord) {
        if (lowerLimit != null && lowerLimit < 0) {
            throw OneboxRestException.builder(CoreErrorCode.BAD_PARAMETER).build();
        }
        if (upperLimit != null && upperLimit != -1 &&
                ((lowerLimit != null && (lowerLimit > upperLimit)) ||
                        (lowerLimit == null && lowerLimitRecord > upperLimit))) {
            throw OneboxRestException.builder(CoreErrorCode.BAD_PARAMETER).build();
        }
    }

    public void updateEventVenueTemplatePriceZones(Integer eventId, Integer rateId) {
        final Map<CpanelConfigRecintoRecord, List<CpanelZonaPreciosConfigRecord>> templatesPriceZones =
                venueTemplateDao.getEventVenueTemplatesWithPriceZones(eventId);

        for (Map.Entry<CpanelConfigRecintoRecord, List<CpanelZonaPreciosConfigRecord>> templatePriceZone : templatesPriceZones.entrySet()) {
            for (CpanelZonaPreciosConfigRecord priceZone : templatePriceZone.getValue()) {
                CpanelAsignacionZonaPreciosRecord pz = new CpanelAsignacionZonaPreciosRecord();
                pz.setIdtarifa(rateId);
                pz.setIdzona(priceZone.getIdzona());
                pz.setPrecio(0.0);
                priceZoneAssignmentDao.insert(pz);
            }
        }
    }

    private void replaceSessionPriceTypeLabels(UpdateSessionRequestDTO session, SessionRecord sessionRecord, Boolean newUseTemplateAccessValue) {
        // if useTemplateAccess has been enabled, delete all defined price-type label session gates
        // if it has been disabled, replace the current gates by the ones defined at the venueTemplate
        priceTypeLabelSessionDao.deleteBySessionIds(Collections.singleton(session.getId().intValue()));
        if (!newUseTemplateAccessValue) {
            List<CpanelZonaPrecioEtiquetaRecord> defaultsByVenueTemplate =
                    priceTypeLabelDao.getByVenueTemplateId(sessionRecord.getVenueTemplateId());
            if (CollectionUtils.isNotEmpty(defaultsByVenueTemplate)) {
                Set<CpanelZonaPrecioEtiquetaSesionRecord> priceTypeLabelSessionRecordsToInsert = new HashSet<>();
                for (CpanelZonaPrecioEtiquetaRecord priceTypeLabel : defaultsByVenueTemplate) {
                    CpanelZonaPrecioEtiquetaSesionRecord cpanelZonaPrecioEtiquetaSesionRecord = new CpanelZonaPrecioEtiquetaSesionRecord();
                    cpanelZonaPrecioEtiquetaSesionRecord.setIdsesion(session.getId().intValue());
                    cpanelZonaPrecioEtiquetaSesionRecord.setIdzonaprecio(priceTypeLabel.getIdzonaprecio());
                    cpanelZonaPrecioEtiquetaSesionRecord.setIdetiqueta(priceTypeLabel.getIdetiqueta());
                    priceTypeLabelSessionRecordsToInsert.add(cpanelZonaPrecioEtiquetaSesionRecord);
                }
                priceTypeLabelSessionDao.insertBatch(priceTypeLabelSessionRecordsToInsert);
            }
        }
    }

    private void bulkReplaceSessionPriceTypeLabels(UpdateSessionRequestDTO updateSessionRequestDTO, List<SessionRecord> sessionsToUpdate) {
        // filter out sessions which useTemplateAccess value hasn't changed
        List<SessionRecord> useTemplateAccessModifiedSessions = sessionsToUpdate.stream()
                .filter(sessionRecord -> updateSessionRequestDTO.getUseTemplateAccess() != null
                        && !updateSessionRequestDTO.getUseTemplateAccess().equals(sessionRecord.getUsaaccesosplantilla()))
                .toList();

        // if useTemplateAccess has been enabled, delete all defined price-type label session gates
        // if it has been disabled, replace the current gates by the ones defined at the venueTemplate
        priceTypeLabelSessionDao.deleteBySessionIds(
                useTemplateAccessModifiedSessions.stream()
                        .map(SessionRecord::getSessionId)
                        .collect(Collectors.toSet()));

        if (!updateSessionRequestDTO.getUseTemplateAccess()) {
            Map<Integer, List<CpanelZonaPrecioEtiquetaRecord>> defaultsByVenueTemplate =
                    priceTypeLabelDao.getByVenueTemplateIds(useTemplateAccessModifiedSessions.stream()
                            .map(SessionRecord::getVenueTemplateId)
                            .collect(Collectors.toSet()));

            if (MapUtils.isNotEmpty(defaultsByVenueTemplate)) {
                Set<CpanelZonaPrecioEtiquetaSesionRecord> priceTypeLabelSessionRecordsToInsert = new HashSet<>();
                for (SessionRecord sessionRecord : useTemplateAccessModifiedSessions) {
                    for (CpanelZonaPrecioEtiquetaRecord priceTypeLabel : defaultsByVenueTemplate.get(sessionRecord.getVenueTemplateId())) {
                        CpanelZonaPrecioEtiquetaSesionRecord cpanelZonaPrecioEtiquetaSesionRecord = new CpanelZonaPrecioEtiquetaSesionRecord();
                        cpanelZonaPrecioEtiquetaSesionRecord.setIdsesion(sessionRecord.getIdsesion());
                        cpanelZonaPrecioEtiquetaSesionRecord.setIdzonaprecio(priceTypeLabel.getIdzonaprecio());
                        cpanelZonaPrecioEtiquetaSesionRecord.setIdetiqueta(priceTypeLabel.getIdetiqueta());
                        priceTypeLabelSessionRecordsToInsert.add(cpanelZonaPrecioEtiquetaSesionRecord);
                    }
                }
                priceTypeLabelSessionDao.insertBatch(priceTypeLabelSessionRecordsToInsert);
            }
        }
    }

    public void updateGenerationStatus(Long eventId, Long sessionId, GenerationStatusSessionRequestDTO generationStatusSessionRequest) {
        SessionRecord sessionRecord = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        switch (generationStatusSessionRequest.getSessionGenerationStatus()) {
            case IN_PROGRESS -> {
                if (sessionRecord.getEstadogeneracionaforo() == null || !sessionRecord.equals(SessionGenerationStatus.IN_PROGRESS.getId())) {
                    sessionRecord.setEstadogeneracionaforo(generationStatusSessionRequest.getSessionGenerationStatus().getId());
                    sessionDao.update(sessionRecord);
                }
            }
            case PENDING, ACTIVE -> {
                if (sessionRecord.getEstadogeneracionaforo() == null || !sessionRecord.getEstadogeneracionaforo().equals(SessionGenerationStatus.IN_PROGRESS.getId())) {
                    throw OneboxRestException.builder(MsEventSessionErrorCode.SESSION_GENERATION_STATUS_INVALID).build();
                }
                sessionRecord.setEstadogeneracionaforo(generationStatusSessionRequest.getSessionGenerationStatus().getId());
            }
            case ERROR -> {
                sessionRecord.setEstadogeneracionaforo(SessionGenerationStatus.ERROR.getId());
            }
        }
        sessionDao.update(sessionRecord);
    }

    private void createSecondaryMarketDatesConfig(CreateSessionDTO request, Long sessionId, Long eventId) {

        if (request.getSecondaryMarketStartDate().isAfter(request.getSecondaryMarketEndDate())) {
            throw new OneboxRestException(MsEventSessionErrorCode.SECONDARY_MARKET_START_DATE_AFTER_END_DATE);
        }

        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        if (sessionConfig.getSecondaryMarketDates() == null) {
            sessionConfig.setSecondaryMarketDates(new SessionSecondaryMarketDates());
        }
        sessionConfig.setEventId(eventId);

        sessionConfig.setSecondaryMarketDates(SessionSecondaryMarketConverter.buildSessionSecondaryMarketDateToSession(request));
        sessionConfigCouchDao.upsert(sessionId.toString(), sessionConfig);

    }

    private void updateSecondaryMarketConfig(Long sessionId, UpdateSessionRequestDTO request,
                                             SessionSecondaryMarketConfigExtended secondaryMarket,
                                             SessionRecord sessionRecord) {

        if (secondaryMarket == null) {
            secondaryMarket = new SessionSecondaryMarketConfigExtended();
            secondaryMarket.setDates(new SessionSecondaryMarketDates());
        }

        ZonedDateTime newStartDate = SecondaryMarketUtils.calculateNewDate(
                request.getDate().getSecondaryMarketStart(),
                sessionRecord.getFechainiciosesion(),
                SecondaryMarketUtils.getSecondaryMarketStartDate(secondaryMarket),
                sessionRecord.getVenueTZ()
        );

        ZonedDateTime newEndDate = SecondaryMarketUtils.calculateNewDate(
                request.getDate().getSecondaryMarketEnd(),
                sessionRecord.getFechainiciosesion(),
                SecondaryMarketUtils.getSecondaryMarketEndDate(secondaryMarket),
                sessionRecord.getVenueTZ()
        );

        Long eventId = sessionRecord.getIdevento().longValue();
        updateSessionConfigDates(sessionId, newStartDate, newEndDate, request, eventId);
    }

    private void updateSessionConfigDates(Long sessionId, ZonedDateTime newStartDate, ZonedDateTime newEndDate,
                                          UpdateSessionRequestDTO request, Long eventId) {
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        if (sessionConfig.getSecondaryMarketDates() == null) {
            sessionConfig.setSecondaryMarketDates(new SessionSecondaryMarketDates());
        }
        updateField(sessionConfig.getSecondaryMarketDates()::setStartDate, newStartDate);
        updateField(sessionConfig.getSecondaryMarketDates()::setEndDate, newEndDate);
        updateField(sessionConfig.getSecondaryMarketDates()::setEnabled, request.getEnableSecondaryMarket());

        if (sessionConfig.getEventId() == null) {
            sessionConfig.setEventId(eventId);
        }

        sessionConfigCouchDao.upsert(sessionId.toString(), sessionConfig);
    }

    private void updateSecondaryMarketConfigs(UpdateSessionsRequestDTO updateSessionsRequestDTO,
                                              Map<Integer, SessionSecondaryMarketConfigExtended> sessionSecondaryMarketConfigs,
                                              List<SessionRecord> sessionsToUpdate, List<Long> sessionIds) {
        sessionIds.forEach(id -> {
            UpdateSessionRequestDTO singleRequest = updateSessionsRequestDTO.getValue();

            SessionRecord sessionRecord = sessionsToUpdate.stream()
                    .filter(session -> session.getIdsesion().equals(id.intValue()))
                    .findFirst()
                    .orElseThrow(() -> new OneboxRestException(MsEventSessionErrorCode.SESSION_NOT_FOUND));

            if (sessionSecondaryMarketConfigService.getAllowSecondaryMarket(sessionRecord.getEntityId())) {
                updateSecondaryMarketConfig(id, singleRequest, sessionSecondaryMarketConfigs.get(id.intValue()), sessionRecord);
            }
        });
    }

    private void addLoyaltyPointsConfig(Long sessionId, CreateSessionLoyaltyPointsConfigDTO loyaltyPointsConfigDTO) {
        UpdateSessionLoyaltyPointsConfigDTO updateLoyaltyPointsConfig = SessionLoyaltyPointsConverter.toUpdateLoyaltyPointsConfigDTO(loyaltyPointsConfigDTO);
        SessionLoyaltyPointsConfig sessionLoyaltyPointsConfig = sessionLoyaltyPointsConfigCouchDao.getOrInitSessionLoyaltyPointsConfig(sessionId);
        SessionLoyaltyPointsConverter.updateLoyaltyPointsConfig(sessionLoyaltyPointsConfig, updateLoyaltyPointsConfig);
        sessionLoyaltyPointsConfigCouchDao.upsert(sessionId.toString(), sessionLoyaltyPointsConfig);
    }

    @MySQLWrite
    public void updateSessionTaxes(Long sessionId, UpdateSessionTaxDTO updateTaxes) {
        CpanelSesionRecord sessionRecord = sessionDao.findById(sessionId.intValue());
        if (sessionRecord == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.SESSION_NOT_FOUND);
        }

        // cpanel_sesion
        sessionRecord.setIdimpuesto(updateTaxes.getTaxId().intValue());
        sessionRecord.setIdimpuestorecargo(updateTaxes.getChargesTaxId().intValue());
        sessionDao.update(sessionRecord);

        // cpanel_session_taxes
        List<CpanelSessionTaxesRecord> existingTicketTaxes = sessionTaxesDao.findFlatSessionsTaxes(null, sessionId.intValue(), SessionTaxesType.TICKETS);
        if (!existingTicketTaxes.isEmpty()) {
            CpanelSessionTaxesRecord ticketTaxRecord = existingTicketTaxes.get(0);
            ticketTaxRecord.setTaxId(updateTaxes.getTaxId().intValue());
            sessionTaxesDao.update(ticketTaxRecord);
        }

        List<CpanelSessionTaxesRecord> existingChargesTaxes = sessionTaxesDao.findFlatSessionsTaxes(null, sessionId.intValue(), SessionTaxesType.CHARGES);
        if (!existingChargesTaxes.isEmpty()) {
            CpanelSessionTaxesRecord chargesTaxRecord = existingChargesTaxes.get(0);
            chargesTaxRecord.setTaxId(updateTaxes.getChargesTaxId().intValue());
            sessionTaxesDao.update(chargesTaxRecord);
        }

        refreshDataService.refreshSession(sessionId, "updateSessionTaxes");
    }

	public void incrementLimit(Integer eventId, Integer sessionId, Integer priceZoneId, SessionCounterDTO counter) {
		Long currentCounter = invitationCounterCouchDao.get(sessionId, priceZoneId);
        Long incrementedCounter;
		if (currentCounter == null) {
			incrementedCounter = invitationCounterCouchDao.insert(sessionId, priceZoneId, counter.count());
		} else {
			incrementedCounter = invitationCounterCouchDao.increment(sessionId, priceZoneId, counter.count());
		}

		LOGGER.info("[INVITATIONS] Incremented a total of: {}, having now: {}", counter.count(), incrementedCounter);
	}

	public void decrementLimit(Integer eventId, Integer sessionId, Integer priceZoneId, SessionCounterDTO counter) {
		Long currentValue = invitationCounterCouchDao.get(sessionId, priceZoneId);

		if (currentValue == null || currentValue < counter.count()) {
			LOGGER.warn("Cannot decrement counter for session {} priceZone {} - current: {}, requested: {}",
					sessionId, priceZoneId, currentValue, counter.count());
			throw new OneboxRestException(MsEventSessionErrorCode.SESSION_NOT_FOUND);
		}

		Long result = invitationCounterCouchDao.decrement(sessionId, priceZoneId, counter.count());
		LOGGER.info("Decremented counter for session {} priceZone {} to: {}", sessionId, priceZoneId, result);
	}

    public long getLimit(Integer eventId, Integer sessionId, Integer priceZoneId) {

        return Optional.ofNullable(invitationCounterCouchDao.get(sessionId, priceZoneId)).orElse(0L);
    }
}
