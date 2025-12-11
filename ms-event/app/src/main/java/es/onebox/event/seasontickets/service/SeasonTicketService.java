package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.dal.dto.couch.enums.OrderState;
import es.onebox.event.attendants.AttendantsConfigService;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.services.CommonRatesService;
import es.onebox.event.common.services.CommonTicketTemplateService;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.datasources.ms.accesscontrol.dto.enums.AccessControlSystem;
import es.onebox.event.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.events.amqp.eventremove.EventRemoveService;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.EventLanguageDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.dto.EventLanguageDTO;
import es.onebox.event.events.dto.VenueDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.events.enums.TicketFormat;
import es.onebox.event.events.service.EventConfigService;
import es.onebox.event.events.service.EventExternalService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.seasontickets.converter.SeasonTicketGenerationStatusConverter;
import es.onebox.event.seasontickets.converter.SeasonTicketRecordConverter;
import es.onebox.event.seasontickets.converter.SeasonTicketStatusConverter;
import es.onebox.event.seasontickets.dao.SeasonTicketDao;
import es.onebox.event.seasontickets.dao.SeasonTicketEventDao;
import es.onebox.event.seasontickets.dao.SeasonTicketSessionDao;
import es.onebox.event.seasontickets.dao.VenueConfigDao;
import es.onebox.event.seasontickets.dao.couch.RenewalType;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeatCouchDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfig;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfigCouchDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketTransferConfigCouchDao;
import es.onebox.event.seasontickets.dao.record.SessionCapacityGenerationStatusRecord;
import es.onebox.event.seasontickets.dao.record.VenueConfigStatusRecord;
import es.onebox.event.seasontickets.dto.CreateSeasonTicketRequestDTO;
import es.onebox.event.seasontickets.dto.SearchSeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketInternalGenerationStatus;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusResponseDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketTicketTemplatesDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketsDTO;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketRequestDTO;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketStatusRequestDTO;
import es.onebox.event.seasontickets.request.SeasonTicketSearchFilter;
import es.onebox.event.seasontickets.service.changeseats.SeasonTicketChangeSeatsValidator;
import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsService;
import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsValidator;
import es.onebox.event.secondarymarket.domain.SessionSecondaryMarketDates;
import es.onebox.event.secondarymarket.service.EventSecondaryMarketConfigService;
import es.onebox.event.sessions.amqp.seatremove.SeatRemoveService;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.Session;
import es.onebox.event.sessions.domain.sessionconfig.PresalesRedirectionLinkMode;
import es.onebox.event.sessions.domain.sessionconfig.PresalesRedirectionPolicy;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionPresalesConfig;
import es.onebox.event.sessions.dto.PresalesLinkMode;
import es.onebox.event.sessions.dto.PresalesRedirectionPolicyDTO;
import es.onebox.event.sessions.dto.SessionGenerationStatus;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.venues.dao.VenueTemplateDao;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static es.onebox.core.exception.CoreErrorCode.BAD_PARAMETER;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
public class SeasonTicketService {

    private static final String SEASON_TICKET_INVALID_CHAR = "|";
    private static final int SEASON_TICKET_MAX_BUYING_LIMIT_MIN_VALUE = 1;
    private static final int SEASON_TICKET_MAX_BUYING_LIMIT_MAX_VALUE = 10;
    private static final Byte ONE = (byte) 1;
    private final SeasonTicketEventDao seasonTicketEventDao;
    private final SeasonTicketSessionDao seasonTicketSessionDao;
    private final RefreshDataService refreshDataService;
    private final VenueTemplateDao venueTemplateDao;
    private final SessionDao sessionDao;
    private final EventRemoveService eventRemoveService;
    private final EventSecondaryMarketConfigService eventSecondaryMarketConfigService;
    private final EventLanguageDao eventLanguageDao;
    private final SeasonSessionDao seasonSessionDao;
    private final CommonRatesService commonRatesService;
    private final EntitiesRepository entitiesRepository;
    private final SeasonTicketServiceHelper helper;
    private final VenueConfigDao venueConfigDao;
    private final OrdersRepository ordersRepository;
    private final SeatRemoveService seatRemoveService;
    private final SessionConfigCouchDao sessionConfigCouchDao;
    private final AttendantsConfigService attendantsConfigService;
    private final SeasonTicketRateService seasonTicketRateService;
    private final CommonTicketTemplateService commonTicketTemplateService;
    private final SeasonTicketSurchargesService seasonTicketSurchargesService;
    private final SeasonTicketDao seasonTicketDao;
    private final SeasonTicketRenewalsService seasonTicketRenewalsService;
    private final EventConfigService eventConfigService;
    private final EventDao eventDao;
    private final SeasonTicketHelper seasonTicketHelper;
    private final SeasonTicketReleaseSeatCouchDao seasonTicketReleaseSeatCouchDao;
    private final SeasonTicketRenewalConfigCouchDao seasonTicketRenewalConfigCouchDao;
    private final EventExternalService externalEventService;
    private final AccessControlSystemsRepository accessControlSystemsRepository;

    @Autowired
    public SeasonTicketService(SeasonTicketEventDao seasonTicketEventDao, SeasonTicketSessionDao seasonTicketSessionDao,
                               VenueTemplateDao venueTemplateDao, SessionDao sessionDao, EventRemoveService eventRemoveService,
                               EventSecondaryMarketConfigService eventSecondaryMarketConfigService,
                               EventLanguageDao eventLanguageDao, CommonRatesService commonRatesService, EntitiesRepository entitiesRepository,
                               SeasonTicketServiceHelper helper, VenueConfigDao venueConfigDao, OrdersRepository ordersRepository,
                               SeatRemoveService seatRemoveService, SeasonSessionDao seasonSessionDao, SessionConfigCouchDao sessionConfigCouchDao,
                               AttendantsConfigService attendantsConfigService, SeasonTicketRateService seasonTicketRateService,
                               CommonTicketTemplateService commonTicketTemplateService,
                               SeasonTicketDao seasonTicketDao, RefreshDataService refreshDataService,
                               SeasonTicketRenewalsService seasonTicketRenewalsService, EventConfigService eventConfigService, EventDao eventDao,
                               SeasonTicketSurchargesService seasonTicketSurchargesService, SeasonTicketHelper seasonTicketHelper,
                               SeasonTicketReleaseSeatCouchDao seasonTicketReleaseSeatCouchDao, SeasonTicketRenewalConfigCouchDao seasonTicketRenewalConfigCouchDao,
                               EventExternalService externalEventService, AccessControlSystemsRepository accessControlSystemsRepository, SeasonTicketTransferConfigCouchDao seasonTicketTransferConfigCouchDao) {
        this.seasonTicketEventDao = seasonTicketEventDao;
        this.seasonTicketSessionDao = seasonTicketSessionDao;
        this.venueTemplateDao = venueTemplateDao;
        this.sessionDao = sessionDao;
        this.eventRemoveService = eventRemoveService;
        this.eventSecondaryMarketConfigService = eventSecondaryMarketConfigService;
        this.eventLanguageDao = eventLanguageDao;
        this.commonRatesService = commonRatesService;
        this.entitiesRepository = entitiesRepository;
        this.refreshDataService = refreshDataService;
        this.helper = helper;
        this.venueConfigDao = venueConfigDao;
        this.ordersRepository = ordersRepository;
        this.seatRemoveService = seatRemoveService;
        this.seasonSessionDao = seasonSessionDao;
        this.sessionConfigCouchDao = sessionConfigCouchDao;
        this.attendantsConfigService = attendantsConfigService;
        this.seasonTicketRateService = seasonTicketRateService;
        this.commonTicketTemplateService = commonTicketTemplateService;
        this.seasonTicketDao = seasonTicketDao;
        this.seasonTicketRenewalsService = seasonTicketRenewalsService;
        this.eventConfigService = eventConfigService;
        this.eventDao = eventDao;
        this.seasonTicketSurchargesService = seasonTicketSurchargesService;
        this.seasonTicketHelper = seasonTicketHelper;
        this.seasonTicketReleaseSeatCouchDao = seasonTicketReleaseSeatCouchDao;
        this.seasonTicketRenewalConfigCouchDao = seasonTicketRenewalConfigCouchDao;
        this.externalEventService = externalEventService;
        this.accessControlSystemsRepository = accessControlSystemsRepository;
    }

    public Long createSeasonTicket(CreateSeasonTicketRequestDTO body) {

        validateSeasonTicketName(body.getName(), body.getEntityId(), null);

        CpanelEventoRecord record = SeasonTicketRecordConverter.toRecord(body);
        commonTicketTemplateService.createDefaultTicketTemplate(record);

        CpanelEventoRecord newRecord = seasonTicketEventDao.insert(record);
        Integer newEventId = newRecord.getIdevento();

        Provider inventoryProvider = body.getInventoryProvider();
        if (inventoryProvider == null) {
            commonRatesService.createDefaultEventRate(newEventId);
        }
        createDefaultLanguages(newEventId, newRecord.getIdentidad());
        seasonTicketSurchargesService.initSeasonTicketSurcharges(record);

        //TODO check EventService createEvent **/
        /** please check {@link es.onebox.event.events.service.EventService#createEvent(CreateEventRequestDTO)} */

        createDefaultSeasonTicketCustomData(newRecord.getIdevento());

        if (inventoryProvider != null) {
            EventConfig newConfig = new EventConfig();
            newConfig.setInventoryProvider(inventoryProvider);
            eventConfigService.storeEventConfig(newEventId.longValue(), newConfig);
            externalEventService.createEventConnectorRelationship(inventoryProvider, newEventId.longValue());
        }

        return newRecord.getIdevento().longValue();
    }

    private void createDefaultLanguages(Integer seasonTicketId, Integer entityId) {
        EntityDTO entity = entitiesRepository.getEntity(entityId);
        Map<Long, String> allLanguages = entitiesRepository.getAllIdAndCodeLanguages();

        List<EventLanguageDTO> eventLanguageDTOList = helper.generateDefaultLanguageList(entity, allLanguages);
        updateEventLanguages(eventLanguageDTOList, seasonTicketId.longValue());
    }

    @MySQLWrite
    public void updateSeasonTicket(UpdateSeasonTicketRequestDTO body) {

        Long id = body.getId();
        EventRecord seasonTicketRecord = getSeasonTicketRecord(id);
        SessionRecord sessionRecord = getSeasonTicketSessionRecord(seasonTicketRecord.getIdevento().longValue());
        CpanelSeasonTicketRecord cpanelSeasonTicketRecord = seasonTicketDao.getById(seasonTicketRecord.getIdevento());

        validateUpdateEvent(body, seasonTicketRecord);
        SeasonTicketRecordConverter.updateEventRecord(seasonTicketRecord, body);
        seasonTicketEventDao.update(seasonTicketRecord);
        updateEventLanguages(body.getLanguages(), id);
        SeasonTicketTicketTemplatesDTO ticketTemplates = body.getSeasonTicketTicketTemplatesDTO();
        if (ticketTemplates != null) {
            eventConfigService.updateEventPassbookConfig(id, ticketTemplates);
        }
        if (body.getEventVenueViewConfig() != null) {
            eventConfigService.updateEventVenueViewConfig(id, body.getEventVenueViewConfig());
        }
        if (validateUpdateSession(body, sessionRecord)) {
            SeasonTicketRecordConverter.updateSessionRecord(sessionRecord, body);
            seasonTicketSessionDao.update(sessionRecord);
            if (ObjectUtils.anyNotNull(body.getChannelPublishingDate(), body.getSalesEndDate(), body.getSalesStartingDate(),
                    body.getBookingStartingDate(), body.getBookingEndDate())) {
                eventDao.updateEventDatesFromSessionCriteria(id);
            }
            updateSessionBookingDisabled(body);
        }

        if (validateAndCheckModificationsOnUpdateSeasonTicket(body, cpanelSeasonTicketRecord, sessionRecord)) {
            SeasonTicketRecordConverter.updateSeasonTicketCustomData(cpanelSeasonTicketRecord, body);
            seasonTicketDao.update(cpanelSeasonTicketRecord);
        }

        SessionConfig sessionConfig = null;

        if (body.getEnableSecondaryMarketSale() != null
                || body.getSecondaryMarketSaleStartingDate() != null || body.getSecondaryMarketSaleEndDate() != null) {

            sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionRecord.getIdsesion().longValue());

            if (sessionConfig.getEventId() == null) {
                sessionConfig.setEventId(CommonUtils.ifNotNull(sessionRecord.getSessionId(), () -> sessionRecord.getSessionId().longValue()));
            }
            if (sessionConfig.getSecondaryMarketDates() == null) {
                sessionConfig.setSecondaryMarketDates(new SessionSecondaryMarketDates());
            }
            ConverterUtils.updateField(sessionConfig.getSecondaryMarketDates()::setEnabled, body.getEnableSecondaryMarketSale());
            ConverterUtils.updateField(sessionConfig.getSecondaryMarketDates()::setStartDate, body.getSecondaryMarketSaleStartingDate());
            ConverterUtils.updateField(sessionConfig.getSecondaryMarketDates()::setEndDate, body.getSecondaryMarketSaleEndDate());
        }

        if (body.getPresalesRedirectionPolicy() != null) {
            if (sessionConfig == null) {
                sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionRecord.getIdsesion().longValue());
            }
            updateSessionPresalesConfig(sessionConfig, body.getPresalesRedirectionPolicy());
        }

        if (sessionConfig != null) {
            sessionConfigCouchDao.upsert(String.valueOf(sessionConfig.getSessionId()), sessionConfig);
        }

        if (body.getRenewal() != null) {
            SeasonTicketRenewalConfig renewalsConfig = seasonTicketRenewalConfigCouchDao.getOrInit(id);
            if (body.getRenewal().getRenewalType() != null) {
                renewalsConfig.setRenewalType(RenewalType.valueOf(body.getRenewal().getRenewalType().name()));
            }
            ConverterUtils.updateField(renewalsConfig::setBankAccountId, body.getRenewal().getBankAccountId());
            ConverterUtils.updateField(renewalsConfig::setGroupByReference, body.getRenewal().getGroupByReference());
            ConverterUtils.updateField(renewalsConfig::setAutoRenewalMandatory, body.getRenewal().getAutoRenewalMandatory());
            seasonTicketRenewalConfigCouchDao.upsert(String.valueOf(id), renewalsConfig);
        }
    }

    private void updateSessionPresalesConfig(SessionConfig sessionConfig, PresalesRedirectionPolicyDTO policyDTO) {
        if (sessionConfig != null && policyDTO != null) {
            SessionPresalesConfig presalesConfig = new SessionPresalesConfig();
            PresalesRedirectionPolicy policy = new PresalesRedirectionPolicy();
            policy.setMode(PresalesRedirectionLinkMode.valueOf(policyDTO.getMode().name()));
            policy.setValue(policyDTO.getValue());

            presalesConfig.setPresalesRedirectionPolicy(policy);
            ConverterUtils.updateField(sessionConfig::setSessionPresalesConfig, presalesConfig);
        }
    }

    private void updateEventLanguages(List<EventLanguageDTO> languages, Long seasonTicketId) {
        if (!CommonUtils.isEmpty(languages)) {
            eventLanguageDao.deleteByEvent(seasonTicketId);
            for (EventLanguageDTO language : languages) {
                eventLanguageDao.insert(new CpanelIdiomaComEventoRecord(language.getId().intValue(),
                        seasonTicketId.intValue(), ConverterUtils.isTrueAsByte(language.getDefault())));
            }
        }
    }

    private void updateSessionBookingDisabled(UpdateSeasonTicketRequestDTO seasonTicket) {
        if (seasonTicket.getBooking() != null && CommonUtils.isFalse(seasonTicket.getBooking().getAllowed())) {
            seasonTicketSessionDao.disableBookingByEvent(seasonTicket.getId());
        }
    }

    private void validateSeasonTicketName(String dtoName, Long entityId, String dbName) {
        if (dtoName.contains(SEASON_TICKET_INVALID_CHAR)) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_NAME_FORMAT,
                    "season ticket name has invalid characters. | not allowed", null);
        }

        if (!dtoName.equals(dbName)) {
            SeasonTicketSearchFilter filter = new SeasonTicketSearchFilter();
            filter.setEntityId(entityId);
            filter.setName(dtoName);
            if (seasonTicketEventDao.countByFilter(filter) > 0) {
                throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_INVALID_NAME_CONFLICT,
                        "season ticket name already used for entity", null);
            }
        }
    }

    private void validateUpdateStatus(Long seasonTicketId, UpdateSeasonTicketStatusRequestDTO updateSeasonTicketStatusRequestDTO) {
        if (updateSeasonTicketStatusRequestDTO.getStatus() == SeasonTicketStatusDTO.PENDING_PUBLICATION || updateSeasonTicketStatusRequestDTO.getStatus() == SeasonTicketStatusDTO.READY) {
            SessionSearchFilter sessionFilter = new SessionSearchFilter();
            sessionFilter.setEventId(Collections.singletonList(seasonTicketId));
            sessionFilter.setStatus(Arrays.asList(SessionStatus.PLANNED, SessionStatus.SCHEDULED,
                    SessionStatus.READY, SessionStatus.IN_PROGRESS));
            sessionFilter.setGenerationStatus(Arrays.asList(SessionGenerationStatus.IN_PROGRESS, SessionGenerationStatus.PENDING));
            if (venueTemplateDao.countActiveVenueTemplates(seasonTicketId.intValue()) > 0 || sessionDao.countByFilter(sessionFilter) > 0) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_INVALID_STATUS_GENERATION,
                        "state transition to PENDING_PUBLICATION or READY forbidden for season ticket with session or venue config pending " +
                                "seat generations", null);
            }
        }

        if (updateSeasonTicketStatusRequestDTO.getStatus() == SeasonTicketStatusDTO.DELETED) {
            if (ordersRepository.countByEventAndChannel(seasonTicketId, null) == 0L) {
                eventRemoveService.removeSeats(seasonTicketId.intValue());
                eventSecondaryMarketConfigService.deleteEventSecondaryMarketConfig(seasonTicketId);
            } else {
                throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_REMOVABLE,
                        "You cannot delete the season ticket with id: " + seasonTicketId +
                                " because it have sold seats.", null);
            }
        }
    }

    public EventRecord getAndCheckSeasonTicket(Long seasonTicketId) {
        return this.seasonTicketHelper.getAndCheckSeasonTicket(seasonTicketId);
    }

    public static void checkOperativeDates(UpdateSeasonTicketRequestDTO seasonTicket, SessionRecord sessionRecord) {
        Timestamp salesStartDate = CommonUtils.zonedDateTimeToTimestamp(seasonTicket.getSalesStartingDate());
        Timestamp salesEndDate = CommonUtils.zonedDateTimeToTimestamp(seasonTicket.getSalesEndDate());
        Timestamp publicationDate = CommonUtils.zonedDateTimeToTimestamp(seasonTicket.getChannelPublishingDate());
        Timestamp bookingStartDate = CommonUtils.zonedDateTimeToTimestamp(seasonTicket.getBookingStartingDate());
        Timestamp bookingEndDate = CommonUtils.zonedDateTimeToTimestamp(seasonTicket.getBookingEndDate());

        if (Objects.nonNull(salesStartDate) || Objects.nonNull(salesEndDate) || Objects.nonNull(publicationDate)
                || Objects.nonNull(bookingStartDate) || Objects.nonNull(bookingEndDate)) {
            if (Objects.isNull(salesStartDate)) {
                salesStartDate = sessionRecord.getFechaventa();
            }
            if (Objects.isNull(salesEndDate)) {
                salesEndDate = sessionRecord.getFechafinsesion();
            }
            if (Objects.isNull(publicationDate)) {
                publicationDate = sessionRecord.getFechapublicacion();
            }
            if (Objects.isNull(bookingStartDate)) {
                bookingStartDate = sessionRecord.getFechainicioreserva();
            }
            if (Objects.isNull(bookingEndDate)) {
                bookingEndDate = sessionRecord.getFechafinreserva();
            }

            if (salesStartDate != null && salesEndDate != null && salesStartDate.after(salesEndDate)) {
                throw new OneboxRestException(MsEventErrorCode.INCONSISTENT_DATES,
                        "Sales starting date must be before the ending sales date", null);
            }
            if (publicationDate != null && salesEndDate != null && publicationDate.after(salesEndDate)) {
                throw new OneboxRestException(MsEventErrorCode.INCONSISTENT_DATES,
                        "Publication date must be before the ending sales date", null);
            }
            if (publicationDate != null && salesStartDate != null && publicationDate.after(salesStartDate)) {
                throw new OneboxRestException(MsEventErrorCode.INCONSISTENT_DATES,
                        "Publication date must be before the starting sales date", null);
            }
            if (bookingStartDate != null && bookingEndDate != null && bookingStartDate.after(bookingEndDate)) {
                throw new OneboxRestException(MsEventErrorCode.INCONSISTENT_DATES,
                        "Booking start date must be before the ending booking date", null);
            }
            if (publicationDate != null && bookingStartDate != null && publicationDate.after(bookingStartDate)) {
                throw new OneboxRestException(MsEventErrorCode.INCONSISTENT_DATES,
                        "Publication date must be before the ending booking date", null);
            }
        }
    }

    public static void checkOperativeStates(UpdateSeasonTicketRequestDTO seasonTicket, SessionRecord sessionRecord) {
        Boolean enableSales = seasonTicket.getEnableSales();
        Boolean enableChannels = seasonTicket.getEnableChannels();

        if (Objects.nonNull(enableChannels) || Objects.nonNull(enableSales)) {
            if (Objects.isNull(enableChannels)) {
                enableChannels = CommonUtils.isTrue(sessionRecord.getPublicado());
            }
            if (Objects.isNull(enableSales)) {
                enableSales = CommonUtils.isTrue(sessionRecord.getEnventa());
            }

            if (enableSales && !enableChannels) {
                throw new OneboxRestException(MsEventErrorCode.INCONSISTENT_PUBLISHED_ONSALE_STATE);
            }
        }
    }

    @MySQLRead
    public SeasonTicketDTO getSeasonTicket(Long seasonTicketId) {
        Map.Entry<EventRecord, List<VenueRecord>> record = seasonTicketEventDao.findSeasonTicket(seasonTicketId);
        List<SessionRecord> sessionRecords = seasonTicketSessionDao.searchSessionInfoByEventId(seasonTicketId);
        SeasonTicketDTO dto;
        SessionRecord sessionRecord = null;

        if (!CommonUtils.isEmpty(sessionRecords) && sessionRecords.size() == 1) {
            sessionRecord = sessionRecords.stream().findFirst().orElse(null);
        }

        SeasonTicketRenewalConfig renewalsConfig = seasonTicketRenewalConfigCouchDao.get(String.valueOf(seasonTicketId));
        dto = Optional.ofNullable(SeasonTicketRecordConverter.fromEntity(record, sessionRecord, renewalsConfig))
                .orElseThrow(() -> new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_NOT_FOUND));

        dto = SeasonTicketRecordConverter.fromEntity(eventLanguageDao.findByEventId(seasonTicketId), dto);

        if (BooleanUtils.isTrue(dto.getAllowRenewal()) && dto.getRenewal() != null) {
            boolean isRenewalInProcess = seasonTicketRenewalsService.isRenewalInProcess(seasonTicketId);
            dto.getRenewal().setRenewalInProcess(isRenewalInProcess);
        }

        EventConfig eventConfig = eventConfigService.getEventConfig(seasonTicketId);
        dto.setEventVenueViewConfig(EventConfigService.extractEventVenueViewConfig(eventConfig));
        fillPassbookTemplates(dto);
        SessionConfig seasonTicketSessionConfig = sessionConfigCouchDao.get(String.valueOf(dto.getSessionId()));
        fillSeasonTicketSecondaryMarketOperative(dto, seasonTicketSessionConfig);
        fillPresalesRedirectionPolicy(dto, seasonTicketSessionConfig);
        fillVenueAccessControlSystems(dto);

        if (eventConfig != null) {
            dto.setInventoryProvider(eventConfig.getInventoryProvider());
        }

        return dto;
    }

    private void fillPresalesRedirectionPolicy(SeasonTicketDTO dto, SessionConfig seasonTicketSessionConfig) {
        if (seasonTicketSessionConfig != null && seasonTicketSessionConfig.getSessionPresalesConfig() != null) {
            PresalesRedirectionPolicy sessionPolicy = seasonTicketSessionConfig.getSessionPresalesConfig().getPresalesRedirectionPolicy();

            if (sessionPolicy != null) {
                PresalesRedirectionPolicyDTO policyDTO = new PresalesRedirectionPolicyDTO();
                policyDTO.setMode(PresalesLinkMode.valueOf(sessionPolicy.getMode().name()));
                policyDTO.setValue(sessionPolicy.getValue());
                dto.setPresalesRedirectionPolicy(policyDTO);
            }
        }
    }

    private void fillVenueAccessControlSystems(SeasonTicketDTO seasonTicket) {
        if (CollectionUtils.isNotEmpty(seasonTicket.getVenues())) {

            List<Long> venueIds = seasonTicket.getVenues().stream().map(VenueDTO::getId).distinct().toList();
            Map<Long, List<AccessControlSystem>> venueAccessControlSystems = new HashMap<>();
            venueIds.forEach(venueId -> {
                List<AccessControlSystem> accessControlSystems = accessControlSystemsRepository.findByVenueId(venueId);
                if (CollectionUtils.isNotEmpty(accessControlSystems)) {
                    venueAccessControlSystems.put(venueId, new ArrayList<>(accessControlSystems));
                }
            });

            seasonTicket.getVenues().forEach(venue -> {
                if (venueAccessControlSystems.containsKey(venue.getId()) && CollectionUtils.isNotEmpty(venueAccessControlSystems.get(venue.getId()))) {
                    venue.setAccessControlSystems(venueAccessControlSystems.get(venue.getId()));
                }
            });
        }
    }

    private void fillSeasonTicketSecondaryMarketOperative(SeasonTicketDTO dto, SessionConfig seasonTicketSessionConfig) {
        if (seasonTicketSessionConfig != null && seasonTicketSessionConfig.getSecondaryMarketDates() != null) {
            dto.setEnableSecondaryMarketSale(seasonTicketSessionConfig.getSecondaryMarketDates().getEnabled());
            dto.setSecondaryMarketSaleStartingDate(seasonTicketSessionConfig.getSecondaryMarketDates().getStartDate());
            dto.setSecondaryMarketSaleEndDate(seasonTicketSessionConfig.getSecondaryMarketDates().getEndDate());
        }
    }

    private void fillPassbookTemplates(SeasonTicketDTO seasonTicket) {
        EventConfig eventConfig = eventConfigService.getEventConfig(seasonTicket.getId());
        if (eventConfig == null || eventConfig.getEventPassbookConfig() == null) {
            return;
        }
        if (seasonTicket.getSeasonTicketTicketTemplatesDTO() == null) {
            seasonTicket.setSeasonTicketTicketTemplatesDTO(new SeasonTicketTicketTemplatesDTO());
        }
        seasonTicket.getSeasonTicketTicketTemplatesDTO().setIndividualTicketPassbookTemplateCode(eventConfig.getEventPassbookConfig().getIndividualPassbookTemplate());
        seasonTicket.getSeasonTicketTicketTemplatesDTO().setIndividualInvitationPassbookTemplateCode(eventConfig.getEventPassbookConfig().getIndividualInvitationPassbookTemplate());
        seasonTicket.getSeasonTicketTicketTemplatesDTO().setGroupTicketPassbookTemplateCode(eventConfig.getEventPassbookConfig().getGroupPassbookTemplate());
        seasonTicket.getSeasonTicketTicketTemplatesDTO().setGroupInvitationPassbookTemplateCode(eventConfig.getEventPassbookConfig().getGroupInvitationPassbookTemplate());
        seasonTicket.getSeasonTicketTicketTemplatesDTO().setSessionPackPassbookTemplateCode(eventConfig.getEventPassbookConfig().getSessionPackPassbookTemplate());

    }

    @MySQLRead
    public SeasonTicketsDTO searchSeasonTickets(SeasonTicketSearchFilter filter) {
        SeasonTicketsDTO seasonTicketsDTO = new SeasonTicketsDTO();
        seasonTicketsDTO.setData(
                seasonTicketEventDao.findSeasonTickets(filter).entrySet()
                        .stream()
                        .map(record -> SeasonTicketRecordConverter.fromEventToSeasons(record, new SearchSeasonTicketDTO()))
                        .toList());

        List<Long> seasonTicketIds = seasonTicketsDTO.getData()
                .stream()
                .map(SearchSeasonTicketDTO::getId)
                .toList();
        List<SessionRecord> sessionRecords = seasonTicketSessionDao.searchSessionInfoByEventIds(seasonTicketIds);

        Map<Long, SessionCapacityGenerationStatusRecord> sessionCapacityMap = new HashMap<>();

        if (!CommonUtils.isEmpty(sessionRecords)) {
            Map<Integer, List<SessionRecord>> seasonTicketSessionsMap = sessionRecords
                    .stream()
                    .collect(Collectors.groupingBy(SessionRecord::getIdevento));

            seasonTicketsDTO.getData().forEach(seasonTicketDTO -> {
                List<SessionRecord> sessionRecordList = seasonTicketSessionsMap.get(seasonTicketDTO.getId().intValue());
                if (!CommonUtils.isEmpty(sessionRecordList) && sessionRecordList.size() == 1) {
                    SessionRecord sessionRecord = sessionRecordList.stream().findFirst().get();
                    seasonTicketDTO.setSessionId(sessionRecord.getIdsesion());
                    SeasonTicketRecordConverter.fromSessionRecord(seasonTicketDTO, sessionRecord);
                    sessionCapacityMap.put(seasonTicketDTO.getId(), new SessionCapacityGenerationStatusRecord(sessionRecord.getEstadogeneracionaforo()));
                }
            });
        }

        Map<Long, VenueConfigStatusRecord> recintoConfigMap = new HashMap<>();

        List<CpanelConfigRecintoRecord> venueRecords = venueConfigDao.getVenueConfigListBySeasonTicketIdList(seasonTicketIds);
        if (!CommonUtils.isEmpty(venueRecords)) {
            Map<Integer, List<CpanelConfigRecintoRecord>> seasonTicketVenuesMap = venueRecords
                    .stream()
                    .collect(Collectors.groupingBy(CpanelConfigRecintoRecord::getIdevento));

            seasonTicketsDTO.getData().forEach(seasonTicketDTO -> {
                List<CpanelConfigRecintoRecord> venueRecordList = seasonTicketVenuesMap.get(seasonTicketDTO.getId().intValue());
                if (!CommonUtils.isEmpty(venueRecordList) && venueRecordList.size() == 1) {
                    CpanelConfigRecintoRecord configRecintoRecord = venueRecordList.stream().findFirst().get();
                    recintoConfigMap.put(seasonTicketDTO.getId(), new VenueConfigStatusRecord(configRecintoRecord.getEstado()));
                }
            });
        }

        seasonTicketsDTO.getData().forEach(seasonTicketDTO -> {
            VenueConfigStatusRecord venueConfigStatus = recintoConfigMap.get(seasonTicketDTO.getId());
            SessionCapacityGenerationStatusRecord generationStatus = sessionCapacityMap.get(seasonTicketDTO.getId());

            seasonTicketDTO.setGenerationStatus(SeasonTicketGenerationStatusConverter.convert(venueConfigStatus, generationStatus));
        });

        seasonTicketsDTO.setMetadata(MetadataBuilder.build(filter, seasonTicketEventDao.countByFilter(filter)));
        return seasonTicketsDTO;
    }

    public SeasonTicketStatusResponseDTO getStatus(Long seasonTicketId) {
        SeasonTicketStatusDTO status;
        SeasonTicketStatusResponseDTO responseDTO = new SeasonTicketStatusResponseDTO();

        SeasonTicketInternalGenerationStatus generationStatus = getGenerationStatus(seasonTicketId);
        responseDTO.setGenerationStatus(generationStatus);
        responseDTO.setSeasonTicketId(Math.toIntExact(seasonTicketId));

        if (generationStatus == SeasonTicketInternalGenerationStatus.READY) {
            List<SessionRecord> records = seasonTicketSessionDao.getSessionStatus(seasonTicketId);
            if (records.size() == 1) {
                SessionRecord sessionRecord = records.get(0);
                status = SeasonTicketStatusConverter.fromSessionStatus(SessionStatus.byId(sessionRecord.getEstado()),
                        sessionRecord.getIspreview());
                responseDTO.setStatus(status);
            }
        }
        return responseDTO;
    }

    @MySQLWrite
    public void deleteSeasonTicket(Long seasonTicketId) {

        CpanelEventoRecord eventRecord = seasonTicketEventDao.getById(seasonTicketId.intValue());

        validateDeleteSeasonTicket(seasonTicketId);

        List<Long> sessionsBySessionPack = null;

        if (getGenerationStatus(seasonTicketId) == SeasonTicketInternalGenerationStatus.READY) {
            List<SessionRecord> sessionRecords = seasonTicketSessionDao.searchSessionInfoByEventId(seasonTicketId);

            SessionRecord sessionRecord = sessionRecords.get(0);

            sessionsBySessionPack = seasonSessionDao
                    .findAllSessionsBySessionPackId(sessionRecord.getIdsesion().longValue());

            prepareDeleteSession(seasonTicketId, sessionRecord);
            sessionRecord.setEstado(SessionStatus.DELETED.getId());
            seasonTicketSessionDao.update(sessionRecord);
        }

        eventRemoveService.removeSeats(seasonTicketId.intValue());
        eventSecondaryMarketConfigService.deleteEventSecondaryMarketConfig(seasonTicketId);

        eventRecord.setEstado(EventStatus.DELETED.getId());
        seasonTicketEventDao.update(eventRecord);

        seasonTicketReleaseSeatCouchDao.remove(seasonTicketId.toString());

        seasonTicketRenewalConfigCouchDao.remove(seasonTicketId.toString());

        migrateSeasonTicketAndAssignedSessionsAfterDelete(seasonTicketId, sessionsBySessionPack);
    }

    private void validateDeleteSeasonTicket(Long seasonTicketId) {
        if (ordersRepository.countByEventAndChannel(seasonTicketId, null) > 0L) {
            throw OneboxRestException.builder(MsEventSeasonTicketErrorCode.SEASON_TICKET_WITH_BOOKED_SEAT)
                    .setHttpStatus(HttpStatus.BAD_REQUEST)
                    .setMessage("Season ticket with booked seats, seasonTicketID: " + seasonTicketId)
                    .build();
        }

        seasonTicketRenewalsService.verifyPendingRenewal(seasonTicketId);
    }

    private void migrateSeasonTicketAndAssignedSessionsAfterDelete(Long seasonTicketId, List<Long> sessionsBySessionPack) {
        refreshDataService.refreshEvent(seasonTicketId, "migrateSeasonTicket", EventIndexationType.SEASON_TICKET);
        if (sessionsBySessionPack != null && !sessionsBySessionPack.isEmpty()) {
            List<Session> sessionList = sessionDao.findSessionsById(sessionsBySessionPack);
            if (CollectionUtils.isNotEmpty(sessionList)) {
                //TODO-RAUL use migrateSessions and check different event than seasonTicketId to avoid duplicate
                sessionList.forEach(s -> refreshDataService.refreshSession(s.getSessionId(), "migrateSeasonTicket"));
            }
        }
    }

    public void prepareDeleteSession(Long seasonTicketId, SessionRecord sessionRecord) {
        Integer sessionId = sessionRecord.getIdsesion();
        if (MapUtils.isEmpty(ordersRepository.sessionOperations(Collections.singletonList(sessionId)))) {

            removeSeats(sessionRecord);

            if (sessionConfigCouchDao.get(sessionId.toString()) != null) {
                sessionConfigCouchDao.remove(sessionId.toString());
            }
            attendantsConfigService.deleteSessionAttendantsConfig(sessionId.longValue(), seasonTicketId);
            seasonTicketRateService.cleanRatesForSessionId(sessionId);

            // Force nullify idExterno on removeSession to prevent problems with avet match configuration
            sessionRecord.setIdexterno(null);

            seasonSessionDao.unlinkAllSessionsOfPack(sessionRecord.getIdsesion().longValue());
        } else {
            throw OneboxRestException.builder(MsEventSeasonTicketErrorCode.SEASON_TICKET_WITH_BOOKED_SEAT)
                    .setHttpStatus(HttpStatus.BAD_REQUEST)
                    .setMessage("Session with booked seats, sessionId: " + sessionId)
                    .build();
        }
    }

    private void removeSeats(SessionRecord sessionRecord) {
        checkSeasonSession(sessionRecord);

        Integer cancelledSeats = ordersRepository.numberOperations(Collections.singletonList(sessionRecord.getIdsesion()), Collections.singletonList(OrderState.CANCELLED), null);
        if (cancelledSeats == 0) {
            seatRemoveService.removeSeats(sessionRecord.getIdsesion());
        } else {
            sessionRecord.setEstadopurgado(SessionGenerationStatus.PENDING.getId().byteValue());
        }
    }

    private void checkSeasonSession(SessionRecord sessionRecord) {
        if (!ONE.equals(sessionRecord.getEsabono())) {
            List<Long> seasonSessionIds = seasonSessionDao.findSessionPacksBySessionId(sessionRecord.getIdsesion().longValue());
            if (CollectionUtils.isNotEmpty(seasonSessionIds)) {
                throw OneboxRestException.builder(MsEventErrorCode.SEASON_LOCK)
                        .setHttpStatus(HttpStatus.BAD_REQUEST)
                        .setMessage("Session not allowed to remove is related with season session, sessionId: " + sessionRecord.getIdsesion())
                        .build();
            }
        }
    }

    @MySQLRead
    public SeasonTicketInternalGenerationStatus getGenerationStatus(Long seasonTicketId) {
        VenueConfigStatusRecord statusRecord = venueConfigDao.getVenueConfigStatusBySeasonTicketId(seasonTicketId.intValue());
        SessionCapacityGenerationStatusRecord generationStatus = seasonTicketSessionDao.getCapacityGenerationStatusBySeasonTicketId(seasonTicketId.intValue());

        return SeasonTicketGenerationStatusConverter.convert(statusRecord, generationStatus);
    }

    public boolean isSeasonTicketSessionCreated(Long seasonTicketId) {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setEventId(Collections.singletonList(seasonTicketId));
        return sessionDao.countByFilter(sessionFilter) > 0;
    }

    public static void checkOperativeMaxBuyingLimit(UpdateSeasonTicketRequestDTO seasonTicket) {
        if (Objects.nonNull(seasonTicket) && Objects.nonNull(seasonTicket.getMaxBuyingLimit())
                && Objects.nonNull(seasonTicket.getMaxBuyingLimit().getValue())) {
            Integer value = seasonTicket.getMaxBuyingLimit().getValue();
            if (value < SEASON_TICKET_MAX_BUYING_LIMIT_MIN_VALUE || value > SEASON_TICKET_MAX_BUYING_LIMIT_MAX_VALUE) {
                throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_MAX_BUYING_LIMIT_RANGE);
            }
        }
    }

    @MySQLWrite
    public void updateStatus(Long seasonTicketId, UpdateSeasonTicketStatusRequestDTO updateSeasonTicketStatusRequestDTO) {

        EventRecord seasonTicketRecord = getSeasonTicketRecord(seasonTicketId);
        SessionRecord sessionRecord = getSeasonTicketSessionRecord(seasonTicketRecord.getIdevento().longValue());

        SeasonTicketInternalGenerationStatus actualGenerationStatus = getGenerationStatus(seasonTicketId);
        if (!SeasonTicketInternalGenerationStatus.READY.equals(actualGenerationStatus)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_IN_CREATION);
        }

        SessionStatus sessionStatus = SessionStatus.byId(sessionRecord.getEstado());
        SeasonTicketStatusDTO seasonTicketStatusDTO = SeasonTicketStatusConverter.fromSessionStatus(sessionStatus, sessionRecord.getIspreview());
        boolean statusChanged = updateSeasonTicketStatusRequestDTO.getStatus() != null
                && !updateSeasonTicketStatusRequestDTO.getStatus().equals(seasonTicketStatusDTO);
        if (statusChanged) {
            validateUpdateStatus(seasonTicketId, updateSeasonTicketStatusRequestDTO);

            EventStatus eventStatus = SeasonTicketStatusConverter.fromSeasonTicketStatus(updateSeasonTicketStatusRequestDTO.getStatus());
            seasonTicketRecord.setEstado(eventStatus.getId());
            seasonTicketRecord.setFechacambioestado(Timestamp.from(ZonedDateTime.now().toInstant()));
            seasonTicketEventDao.update(seasonTicketRecord);

            SeasonTicketStatusConverter.fromSeasonStatus(updateSeasonTicketStatusRequestDTO.getStatus(), sessionRecord);
            seasonTicketSessionDao.update(sessionRecord);
        }
    }

    private SessionRecord getSeasonTicketSessionRecord(Long seasonTicketRecordId) {
        List<SessionRecord> sessionRecords = seasonTicketSessionDao.searchSessionInfoByEventId(seasonTicketRecordId);
        SessionRecord sessionRecord = null;
        if (!CommonUtils.isEmpty(sessionRecords) && sessionRecords.size() == 1) {
            sessionRecord = sessionRecords.stream().findFirst().orElse(null);
        }
        return sessionRecord;
    }

    private EventRecord getSeasonTicketRecord(Long seasonTicketId) {
        EventRecord seasonTicketRecord;
        try {
            Map.Entry<EventRecord, List<VenueRecord>> record = seasonTicketEventDao.findSeasonTicket(seasonTicketId);
            seasonTicketRecord = record.getKey();
        } catch (EntityNotFoundException e) {
            throw OneboxRestException.builder(MsEventSeasonTicketErrorCode.SEASON_TICKET_NOT_FOUND).setMessage("Season ticket not found for id " +
                    seasonTicketId).build();
        }
        return seasonTicketRecord;
    }

    private void validateUpdateTicketTemplates(UpdateSeasonTicketRequestDTO seasonTicket, CpanelEventoRecord eventRecord) {
        SeasonTicketTicketTemplatesDTO templates = seasonTicket.getSeasonTicketTicketTemplatesDTO();
        if (templates != null) {
            Integer entityId = eventRecord.getIdentidad();
            commonTicketTemplateService.validateUpdateTicketTemplate(templates.getTicketPdfTemplateId(), TicketFormat.PDF, entityId);
            commonTicketTemplateService.validateUpdateTicketTemplate(templates.getTicketPrinterTemplateId(), TicketFormat.ZPL, entityId);
            commonTicketTemplateService.validatePassbookTemplateExists(templates.getIndividualTicketPassbookTemplateCode(), entityId);

            commonTicketTemplateService.validateUpdateTicketTemplate(templates.getGroupTicketPdfTemplateId(), TicketFormat.PDF, entityId);
            commonTicketTemplateService.validateUpdateTicketTemplate(templates.getGroupTicketPrinterTemplateId(), TicketFormat.ZPL, entityId);
            commonTicketTemplateService.validatePassbookTemplateExists(templates.getGroupTicketPassbookTemplateCode(), entityId);

            commonTicketTemplateService.validateUpdateTicketTemplate(templates.getIndividualInvitationPdfTemplateId(), TicketFormat.PDF, entityId);
            commonTicketTemplateService.validateUpdateTicketTemplate(templates.getIndividualInvitationPrinterTemplateId(), TicketFormat.ZPL, entityId);
            commonTicketTemplateService.validatePassbookTemplateExists(templates.getIndividualInvitationPassbookTemplateCode(), entityId);

            commonTicketTemplateService.validateUpdateTicketTemplate(templates.getGroupInvitationPdfTemplateId(), TicketFormat.PDF, entityId);
            commonTicketTemplateService.validateUpdateTicketTemplate(templates.getGroupInvitationPrinterTemplateId(), TicketFormat.ZPL, entityId);
            commonTicketTemplateService.validatePassbookTemplateExists(templates.getGroupInvitationPassbookTemplateCode(), entityId);
        }
    }

    private void createDefaultSeasonTicketCustomData(Integer idevento) {
        CpanelSeasonTicketRecord seasonTicketRecord = new CpanelSeasonTicketRecord();
        seasonTicketRecord.setIdevento(idevento);
        seasonTicketRecord.setIsmembermandatory(FALSE);
        seasonTicketRecord.setAllowrenewal(FALSE);
        seasonTicketRecord.setAllowchangeseat(FALSE);
        seasonTicketRecord.setRenewalenabled(FALSE);
        seasonTicketDao.insert(seasonTicketRecord);
    }

    private boolean validateAndCheckModificationsOnUpdateSeasonTicket(UpdateSeasonTicketRequestDTO body, CpanelSeasonTicketRecord stRecord, SessionRecord sessionRecord) {

        boolean memberMandatoryModified = ((body.getMemberMandatory() != null
                && !body.getMemberMandatory().equals(stRecord.getIsmembermandatory()))
                || (body.getRegisterMandatory() != null && !body.getRegisterMandatory().equals(stRecord.getRegistermandatory())));

        boolean customerMaxSeatsModified = body.getCustomerMaxSeats() != null
                && !Objects.equals(body.getCustomerMaxSeats(), stRecord.getCustomermaxseats());

        boolean allowRenewalModified = body.getAllowRenewal() != null
                && !body.getAllowRenewal().equals(stRecord.getAllowrenewal());

        boolean renewalModified = body.getRenewal() != null && SeasonTicketRenewalsValidator.isRenewalModified(body.getRenewal(), stRecord);

        boolean isRenewalInProcess = seasonTicketRenewalsService.isRenewalInProcess(body.getId());

        boolean allowChangeSeatModified = body.getAllowChangeSeat() != null
                && !body.getAllowChangeSeat().equals(stRecord.getAllowchangeseat());

        boolean changeSeatModified = body.getChangeSeat() != null && SeasonTicketChangeSeatsValidator.isChangeSeatModified(body.getChangeSeat(), stRecord);

        boolean allowTransferModified = body.getAllowTransferTicket() != null && !body.getAllowTransferTicket().equals(stRecord.getAllowtransferticket());

        boolean allowReleaseSeatModified = body.getAllowReleaseSeat() != null && !body.getAllowReleaseSeat().equals(stRecord.getAllowreleaseseat());

        if (!memberMandatoryModified && !allowRenewalModified && !renewalModified && !allowChangeSeatModified && !changeSeatModified
                && !allowReleaseSeatModified && !customerMaxSeatsModified && !allowTransferModified) {
            return false;
        }
        if (memberMandatoryModified) {
            validateHasSoldTickets(body.getId());
        }
        if (allowRenewalModified || renewalModified) {
            SeasonTicketRenewalsValidator.validateRenewalOnUpdateSeasonTicket(body, stRecord, sessionRecord);
        }
        if (allowRenewalModified && isRenewalInProcess) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IN_PROGRESS);
        }
        if (allowChangeSeatModified || changeSeatModified) {
            SeasonTicketChangeSeatsValidator.validateChangeSeatOnUpdateSeasonTicket(body, stRecord, sessionRecord);
        }
        return true;
    }

    private static boolean containsSessionRecordField(UpdateSeasonTicketRequestDTO seasonTicket) {
        return ObjectUtils.anyNotNull(seasonTicket.getSalesStartingDate(), seasonTicket.getSalesEndDate(),
                seasonTicket.getChannelPublishingDate(), seasonTicket.getEnableChannels(), seasonTicket.getEnableSales(),
                seasonTicket.getMaxBuyingLimit(), seasonTicket.getBookingEndDate(), seasonTicket.getBookingStartingDate(),
                seasonTicket.getBookingEnabled());
    }

    private void validateHasSoldTickets(Long seasonTicketId) {
        ProductSearchResponse response = ordersRepository.getAlmostOneActiveProduct(Collections.singletonList(seasonTicketId));
        if (response != null && CollectionUtils.isNotEmpty(response.getData())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_WITH_BOOKED_SEAT_REGISTRY_CHANGE);
        }
    }

    private void validateUpdateEvent(UpdateSeasonTicketRequestDTO seasonTicket, CpanelEventoRecord eventRecord) {
        if (seasonTicket.getId() < 1) {
            throw new OneboxRestException(BAD_PARAMETER, "season ticket id is mandatory", null);
        }
        if (seasonTicket.getName() != null) {
            validateSeasonTicketName(seasonTicket.getName(), eventRecord.getIdentidad().longValue(), eventRecord.getNombre());
        }
        if (!CommonUtils.isEmpty(seasonTicket.getLanguages())
                && seasonTicket.getLanguages().stream().filter(l -> TRUE.equals(l.getDefault())).count() != 1) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_INVALID_DEFAULT_LANGUAGE,
                    "season ticket languages must define exactly 1 by default", null);
        }

        validateUpdateTicketTemplates(seasonTicket, eventRecord);
    }

    private static boolean validateUpdateSession(UpdateSeasonTicketRequestDTO seasonTicket, SessionRecord sessionRecord) {
        if (sessionRecord == null && containsSessionRecordField(seasonTicket)) {
            throw new OneboxRestException(MsEventErrorCode.FIELD_NOT_UPGRADEABLE, "One of the fields is not upgradeable at the moment",
                    null);
        }
        if (Objects.nonNull(sessionRecord) && SeasonTicketStatusConverter.checkIsDeleted(sessionRecord)) {
            throw new OneboxRestException(CoreErrorCode.FORBIDDEN_OPERATION, "season ticket is already deleted. It cant be updated!", null);
        } else if (Objects.nonNull(sessionRecord) && containsSessionRecordField(seasonTicket)) {
            checkOperativeDates(seasonTicket, sessionRecord);
            checkOperativeStates(seasonTicket, sessionRecord);
            checkOperativeMaxBuyingLimit(seasonTicket);
            return true;
        }
        return false;
    }

}
