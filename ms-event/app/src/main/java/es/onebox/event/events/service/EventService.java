package es.onebox.event.events.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.DateUtils;
import es.onebox.event.common.amqp.channelsuggestionscleanup.ChannelSuggestionsCleanUpService;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.common.services.CommonCommunicationElementService;
import es.onebox.event.common.services.CommonRatesService;
import es.onebox.event.common.services.CommonSurchargesService;
import es.onebox.event.common.services.CommonTicketTemplateService;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.datasources.ms.accesscontrol.dto.enums.AccessControlSystem;
import es.onebox.event.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.event.datasources.ms.crm.dto.SubscriptionDTO;
import es.onebox.event.datasources.ms.crm.repository.SubscriptionsRepository;
import es.onebox.event.datasources.ms.entity.dto.AccommodationsEntityConfig;
import es.onebox.event.datasources.ms.entity.dto.AccommodationsVendor;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.dto.OperatorCurrenciesDTO;
import es.onebox.event.datasources.ms.entity.dto.OperatorCurrencyDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.datasources.integration.dispatcher.repository.IntDispatcherRepository;
import es.onebox.event.events.amqp.eventnotification.ExternalEventConsumeNotificationService;
import es.onebox.event.events.amqp.eventremove.EventRemoveService;
import es.onebox.event.events.amqp.tiermodification.TierModificationMessage;
import es.onebox.event.events.amqp.whitelistgeneration.WhitelistGenerationService;
import es.onebox.event.events.converter.EventConverter;
import es.onebox.event.events.converter.TicketTemplateConverter;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.EventLanguageDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.TierDao;
import es.onebox.event.events.dao.TourDao;
import es.onebox.event.events.dao.record.EventLanguageRecord;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.TierRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.domain.eventconfig.EventPassbookConfig;
import es.onebox.event.events.domain.eventconfig.EventTransferTicketConfig;
import es.onebox.event.events.dto.CreateEventRequestDTO;
import es.onebox.event.events.dto.EventConfigDTO;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.dto.EventLanguageDTO;
import es.onebox.event.events.dto.EventSessionSelectionDTO;
import es.onebox.event.events.dto.EventTicketTemplatesDTO;
import es.onebox.event.events.dto.EventTransferTicketDTO;
import es.onebox.event.events.dto.EventsDTO;
import es.onebox.event.events.dto.TicketTemplateDTO;
import es.onebox.event.events.dto.UpdateEventRequestDTO;
import es.onebox.event.events.dto.VenueDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.events.enums.SessionSelectType;
import es.onebox.event.events.enums.SessionState;
import es.onebox.event.events.enums.TaxModeDTO;
import es.onebox.event.events.enums.TicketFormat;
import es.onebox.event.events.request.EventSearchFilter;
import es.onebox.event.events.utils.EvaluableTierWrapper;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.events.utils.TierEvaluator;
import es.onebox.event.exception.MSEventNotFoundException;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.exception.MsEventTierErrorCode;
import es.onebox.event.priceengine.request.ChannelSubtype;
import es.onebox.event.priceengine.request.EventChannelSearchFilter;
import es.onebox.event.priceengine.request.StatusRequestType;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductEventDeliveryPointDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.dao.ProductSessionDeliveryPointDao;
import es.onebox.event.products.dto.UpdateProductDTO;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.event.products.service.ProductService;
import es.onebox.event.secondarymarket.service.EventSecondaryMarketConfigService;
import es.onebox.event.sessions.converter.SessionTaxConverter;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.TaxDao;
import es.onebox.event.sessions.domain.Session;
import es.onebox.event.sessions.dto.SessionGenerationStatus;
import es.onebox.event.sessions.dto.SessionSalesType;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.dto.SessionTaxDTO;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.tickettemplates.dao.TicketTemplateRecord;
import es.onebox.event.venues.dao.VenueTemplateDao;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelGiraRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductEventRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static es.onebox.core.exception.CoreErrorCode.BAD_PARAMETER;

@Service
public class EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);

    private static final int EVENT_NAME_LENGTH = 50;
    private static final String EVENT_INVALID_CHAR = "|";
    private static final String CHANNEL_SUGGESTIONS_CLEAN_UP = "[CHANNEL SUGGESTIONS CLEAN UP]";
    public static final Set<ChronoUnit> ALLOWED_CHANGE_SEAT_EXPIRY_TIME_UNITS =
            Set.of(ChronoUnit.HOURS, ChronoUnit.DAYS, ChronoUnit.WEEKS, ChronoUnit.MONTHS);

    private final EventDao eventDao;
    private final EventLanguageDao eventLanguageDao;
    private final SessionDao sessionDao;
    private final TaxDao taxDao;
    private final RateDao rateDao;
    private final VenueTemplateDao venueTemplateDao;
    private final TourDao tourDao;
    private final TierDao tierDao;
    private final EventConfigCouchDao eventConfigCouchDao;
    private final EventConfigService eventConfigService;
    private final SubscriptionsRepository subscriptionsRepository;
    private final EventChannelService eventChannelService;
    private final EntitiesRepository entitiesRepository;
    private final EventExternalService externalEventService;
    private final EventRateGroupService eventRateGroupService;
    private final CommonRatesService commonRatesService;
    private final ProductEventDao productEventDao;
    private final ProductSessionDao productSessionDao;
    private final ProductService productService;
    private final ProductEventDeliveryPointDao productEventDeliveryPointDao;
    private final ProductSessionDeliveryPointDao productSessionDeliveryPointDao;
    private final ChannelEventDao channelEventDao;

    // Async processes
    private final EventSecondaryMarketConfigService eventSecondaryMarketConfigService;
    private final EventRemoveService eventRemoveService;
    private final RefreshDataService refreshDataService;
    private final WhitelistGenerationService whitelistGenerationService;
    private final ExternalEventConsumeNotificationService externalEventConsumeNotificationService;
    private final OrdersRepository ordersRepository;
    private final CommonTicketTemplateService commonTicketTemplateService;
    private final CommonSurchargesService commonSurchargesService;
    private final CommonCommunicationElementService commonCommunicationElementService;
    private final WebhookService webhookService;
    private final DefaultProducer tierModificationProducer;
    private final ChannelSuggestionsCleanUpService channelSuggestionsCleanUpService;
    private final AccessControlSystemsRepository accessControlSystemsRepository;
    private final IntDispatcherRepository intDispatcherRepository;

    @Value("${onebox.exclude-archive.entities:}")
    private String excludeEntities;

    @Autowired
    public EventService(EventDao eventDao,
                        SessionDao sessionDao,
                        TaxDao taxDao,
                        RateDao rateDao,
                        EventExternalService externalEventService,
                        EventLanguageDao eventLanguageDao,
                        VenueTemplateDao venueTemplateDao,
                        EventConfigCouchDao eventConfigCouchDao,
                        EventRemoveService eventRemoveService,
                        EventSecondaryMarketConfigService eventSecondaryMarketConfigService,
                        RefreshDataService refreshDataService,
                        WhitelistGenerationService whitelistGenerationService,
                        ExternalEventConsumeNotificationService externalEventConsumeNotificationService,
                        TourDao tourDao,
                        TierDao tierDao,
                        OrdersRepository ordersRepository,
                        CommonTicketTemplateService commonTicketTemplateService,
                        CommonSurchargesService commonSurchargesService,
                        EventConfigService eventConfigService,
                        SubscriptionsRepository subscriptionsRepository,
                        EventChannelService eventChannelService,
                        CommonCommunicationElementService commonCommunicationElementService,
                        WebhookService webhookService,
                        DefaultProducer tierModificationProducer,
                        EntitiesRepository entitiesRepository,
                        EventRateGroupService eventRateGroupService,
                        CommonRatesService commonRatesService,
                        ChannelSuggestionsCleanUpService channelSuggestionsCleanUpService,
                        ProductEventDao productEventDao,
                        ProductSessionDao productSessionDao,
                        ProductService productService,
                        ProductEventDeliveryPointDao productEventDeliveryPointDao,
                        ProductSessionDeliveryPointDao productSessionDeliveryPointDao, ChannelEventDao channelEventDao,
                        AccessControlSystemsRepository accessControlSystemsRepository,
                        IntDispatcherRepository intDispatcherRepository) {
        this.eventDao = eventDao;
        this.externalEventService = externalEventService;
        this.sessionDao = sessionDao;
        this.taxDao = taxDao;
        this.rateDao = rateDao;
        this.venueTemplateDao = venueTemplateDao;
        this.eventLanguageDao = eventLanguageDao;
        this.eventConfigCouchDao = eventConfigCouchDao;
        this.ordersRepository = ordersRepository;
        this.eventRemoveService = eventRemoveService;
        this.eventSecondaryMarketConfigService = eventSecondaryMarketConfigService;
        this.refreshDataService = refreshDataService;
        this.whitelistGenerationService = whitelistGenerationService;
        this.externalEventConsumeNotificationService = externalEventConsumeNotificationService;
        this.tourDao = tourDao;
        this.tierDao = tierDao;
        this.commonTicketTemplateService = commonTicketTemplateService;
        this.commonSurchargesService = commonSurchargesService;
        this.eventConfigService = eventConfigService;
        this.subscriptionsRepository = subscriptionsRepository;
        this.eventChannelService = eventChannelService;
        this.commonCommunicationElementService = commonCommunicationElementService;
        this.webhookService = webhookService;
        this.tierModificationProducer = tierModificationProducer;
        this.entitiesRepository = entitiesRepository;
        this.eventRateGroupService = eventRateGroupService;
        this.commonRatesService = commonRatesService;
        this.channelSuggestionsCleanUpService = channelSuggestionsCleanUpService;
        this.productEventDao = productEventDao;
        this.productSessionDao = productSessionDao;
        this.productService = productService;
        this.productEventDeliveryPointDao = productEventDeliveryPointDao;
        this.productSessionDeliveryPointDao = productSessionDeliveryPointDao;
        this.channelEventDao = channelEventDao;
        this.accessControlSystemsRepository = accessControlSystemsRepository;
        this.intDispatcherRepository = intDispatcherRepository;
    }

    @MySQLRead
    public EventDTO getEvent(Long eventId) {
        EventDTO dto = EventConverter.fromEntity(eventDao.findEvent(eventId));
        if (dto != null) {
            dto = EventConverter.fromEntity(eventLanguageDao.findByEventId(eventId), dto);
            fillPassbookTemplates(dto);
            fillVenueAccessControlSystems(dto);
            EventConfig eventConfig = eventConfigService.getEventConfig(eventId);
            dto.setEventVenueViewConfig(EventConfigService.extractEventVenueViewConfig(eventConfig));
            dto.setAccommodationsConfig(EventConfigService.extractEventAccommodationsConfig(eventConfig));
            dto.setWhitelabelSettings(EventConfigService.extractEventWhitelabelSettings(dto.getSupraEvent(), eventConfig, null));
            dto.setEventExternalConfig(EventConfigService.extractEventExternalConfig(eventConfig));
            fillEventChangeSeat(dto, eventConfig);
            fillEventTransferTicket(dto, eventConfig);
            dto.setPhoneVerificationRequired(eventConfig != null ? eventConfig.getPhoneVerificationRequired() : null);
            dto.setAttendantVerificationRequired(eventConfig != null ? eventConfig.getAttendantVerificationRequired() : null);
            externalEventService.checkAndfillExternalData(dto, eventConfig);
        }
        return dto;
    }

    public EventConfigDTO getEventConfig(Long eventId) {
        EventConfig eventConfig = eventConfigCouchDao.getOrInitEventConfig(eventId);
        return EventConverter.toEventConfigDTO(eventConfig);
    }

    private void fillPassbookTemplates(EventDTO event) {
        EventConfig eventConfig = eventConfigService.getEventConfig(event.getId());
        if (eventConfig == null || eventConfig.getEventPassbookConfig() == null) {
            return;
        }
        EventPassbookConfig eventPassbookConfig = eventConfig.getEventPassbookConfig();
        if (event.getTicketTemplates() == null) {
            event.setTicketTemplates(new EventTicketTemplatesDTO());
        }
        EventTicketTemplatesDTO ticketTemplates = event.getTicketTemplates();
        ticketTemplates.setIndividualTicketPassbookTemplateCode(eventPassbookConfig.getIndividualPassbookTemplate());
        ticketTemplates.setIndividualInvitationPassbookTemplateCode(eventPassbookConfig.getIndividualInvitationPassbookTemplate());
        ticketTemplates.setGroupTicketPassbookTemplateCode(eventPassbookConfig.getGroupPassbookTemplate());
        ticketTemplates.setGroupInvitationPassbookTemplateCode(eventPassbookConfig.getGroupInvitationPassbookTemplate());
        ticketTemplates.setSessionPackPassbookTemplateCode(eventPassbookConfig.getSessionPackPassbookTemplate());

    }

    private void fillVenueAccessControlSystems(EventDTO event) {

        if (CollectionUtils.isNotEmpty(event.getVenues())) {

            List<Long> venueIds = event.getVenues().stream().map(VenueDTO::getId).distinct().toList();
            Map<Long, List<AccessControlSystem>> venueAccessControlSystems = new HashMap<>();
            venueIds.forEach(venueId -> {
                List<AccessControlSystem> accessControlSystems = accessControlSystemsRepository.findByVenueId(venueId);
                if (CollectionUtils.isNotEmpty(accessControlSystems)) {
                    venueAccessControlSystems.put(venueId, new ArrayList<>(accessControlSystems));
                }
            });

            event.getVenues().forEach(venue -> {
                if (venueAccessControlSystems.containsKey(venue.getId()) && CollectionUtils.isNotEmpty(venueAccessControlSystems.get(venue.getId()))) {
                    venue.setAccessControlSystems(venueAccessControlSystems.get(venue.getId()));
                }
            });
        }

    }

    @MySQLRead
    public EventsDTO searchEvents(EventSearchFilter filter) {
        EventsDTO eventsDTO = new EventsDTO();
        List<EventDTO> events = eventDao.findEvents(filter).entrySet().stream().map(EventConverter::fromEntity).toList();
        if (CollectionUtils.isEmpty(filter.getFields())) {
            List<EventLanguageRecord> eventLanguages = eventLanguageDao.findByEventIds(events.stream().map(EventDTO::getId).collect(Collectors.toList()));
            events.stream().forEach(eventDTO -> EventConverter.fromEntity(eventLanguages.stream().filter(eventLanguageRecord ->
                    eventLanguageRecord.getEventId().equals(eventDTO.getId())).collect(Collectors.toList()), eventDTO));
        }
        eventsDTO.setData(events);
        eventsDTO.setMetadata(MetadataBuilder.build(filter, eventDao.countByFilter(filter)));
        return eventsDTO;
    }

    @MySQLWrite
    public Long createEvent(CreateEventRequestDTO event) {

        validateCreation(event);

        EntityDTO entity = entitiesRepository.getEntity(event.getEntityId().intValue());
        if (entity != null && event.getCurrencyId() == null && entity.getOperator() != null) {
            OperatorCurrenciesDTO currencies = entitiesRepository.getOperatorCurrencies(entity.getOperator().getId());
            if (currencies != null && CollectionUtils.isNotEmpty(currencies.getSelected())) {
                event.setCurrencyId(currencies.getSelected().stream()
                        .filter(operatorCurrencyDTO -> operatorCurrencyDTO.getCode().equals(currencies.getDefaultCurrency()))
                        .map(OperatorCurrencyDTO::getId).findFirst().orElse(null));
            }
        }

        CpanelEventoRecord cpanelEventoRecord = EventConverter.toRecord(event);

        commonTicketTemplateService.createDefaultTicketTemplate(cpanelEventoRecord);

        boolean isAvetEvent = EventType.AVET.equals(event.getType());
        if (isAvetEvent) {
            validateAvetEventConfig(event);
            cpanelEventoRecord.setIdexterno(event.getAvetCompetitionId());
            cpanelEventoRecord.setTaxmode(TaxModeDTO.INCLUDED.getId());
        }

        CpanelEventoRecord newEvent = eventDao.insert(cpanelEventoRecord);

        Provider inventoryProvider = event.getInventoryProvider();
        initEventRelations(newEvent, event.getDefaultLangId(), inventoryProvider);
        commonSurchargesService.initEventSurcharges(cpanelEventoRecord);

        if (isAvetEvent) {
            externalEventService.createEventAvetConfig(event, newEvent);
        }

        long newEventId = newEvent.getIdevento().longValue();
        if (CollectionUtils.isNotEmpty(event.getEntityFavoriteChannels())) {
            addFavoriteChannelsToEvent(newEventId, event.getEntityFavoriteChannels());
        }
        commonCommunicationElementService.createDefaultEventCommunicationElements(newEvent);

        if (inventoryProvider != null) {
            EventConfig newConfig = new EventConfig();
            newConfig.setInventoryProvider(inventoryProvider);
            eventConfigService.storeEventConfig(newEventId, newConfig);
            externalEventService.createEventConnectorRelationship(inventoryProvider, newEventId);
        }

        return newEventId;
    }


    private static void validateAvetEventConfig(CreateEventRequestDTO event) {
        if (event.getAvetCompetitionId() == null) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_AVET_COMPETITION_MANDATORY, "Avet competition is mandatory in Avet Event",
                    null);
        }
        if (event.getAvetConfig() == null) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_AVET_CONFIG_MANDATORY,
                    "Avet config is mandatory in Avet Event", null);
        }
    }

    private void initEventRelations(CpanelEventoRecord newEvent, Integer defaultLangId, Provider inventoryProvider) {
        boolean isSGA = inventoryProvider == Provider.SGA;
        if (EventType.AVET.getId().equals(newEvent.getTipoevento())) {
            eventRateGroupService.createAvetDefaultEventRateGroup(newEvent.getIdevento());
        } else if (!isSGA) {
            commonRatesService.createDefaultEventRate(newEvent.getIdevento());
        }

        if (defaultLangId != null) {
            eventLanguageDao.insert(new CpanelIdiomaComEventoRecord(defaultLangId, newEvent.getIdevento(), (byte) 1));
        }
    }

    private void validateCreation(CreateEventRequestDTO event) {

        String name = event.getName();
        if (name == null || name.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_NAME_FORMAT, "event name is mandatory", null);
        } else if (name.length() > EVENT_NAME_LENGTH) {
            throw new OneboxRestException(BAD_PARAMETER, "event name length cannot be above "
                    + EVENT_NAME_LENGTH + " characters", null);
        }
        if (name.contains(EVENT_INVALID_CHAR)) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_NAME_FORMAT, "event name has invalid characters. | not allowed", null);
        }
        if (event.getEntityId() == null || event.getEntityId() < 1) {
            throw new OneboxRestException(BAD_PARAMETER, "valid entityId is mandatory", null);
        }
        if (event.getProducerId() == null || event.getProducerId() < 1) {
            throw new OneboxRestException(BAD_PARAMETER, "valid producerId is mandatory", null);
        }
        if (event.getCategoryId() == null || event.getCategoryId() < 1) {
            throw new OneboxRestException(BAD_PARAMETER, "valid categoryId is mandatory", null);
        }
        EventSearchFilter filter = new EventSearchFilter();
        filter.setEntityId(event.getEntityId());
        filter.setName(name);
        if (eventDao.countByFilter(filter) > 0) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_NAME_CONFLICT, "event name already used for entity", null);
        }
    }

    @MySQLWrite
    public void updateEvent(UpdateEventRequestDTO request) {
        Map.Entry<EventRecord, List<VenueRecord>> eventVenueRecord;
        try {
            eventVenueRecord = eventDao.findEvent(request.getId());
        } catch (EntityNotFoundException e) {
            throw OneboxRestException.builder(MsEventErrorCode.EVENT_NOT_FOUND).
                    setMessage("Event not found for id " + request.getId()).build();
        }
        EventRecord eventRecord = eventVenueRecord.getKey();
        validateUpdate(request, eventRecord);

        if (BooleanUtils.isTrue(request.getUseTieredPricing()) && !ConverterUtils.isByteAsATrue(eventRecord.getUsetieredpricing())) {
            TierModificationMessage message = new TierModificationMessage();
            message.setAction(TierModificationMessage.Action.CREATE_DEFAULT_TIERS_FOR_EVENT);
            message.setEventId(request.getId());
            try {
                tierModificationProducer.sendMessage(message);
            } catch (Exception e) {
                LOGGER.error("Error while creating tiers for request", e);
            }
        }

        EventConverter.updateRecord(eventRecord, request);

        eventDao.update(eventRecord);

        updateEventLanguages(request);
        updateSessionBookingDisabled(request);

        if (request.getEventVenueViewConfig() != null) {
            eventConfigService.updateEventVenueViewConfig(request.getId(), request.getEventVenueViewConfig());
        }
        EventTicketTemplatesDTO ticketTemplates = request.getTicketTemplates();
        if (ticketTemplates != null) {
            eventConfigService.updateEventPassbookConfig(request.getId(), ticketTemplates);
        }
        if (request.getCustomSelectTemplate() != null) {
            eventConfigService.updateCustomSelectTemplate(request.getId(), request.getCustomSelectTemplate());
        }
        if (request.getAccommodationsConfig() != null) {
            eventConfigService.updateEventAccommodationsConfig(request.getId(), request.getAccommodationsConfig());
        }
        boolean isSupraEventUpdating = request.getSupraEvent() != null;
        boolean isSupraEvent = request.getSupraEvent() != null ? request.getSupraEvent() : false;
        if (request.getWhitelabelSettings() != null || request.getSupraEvent() != null) {
            eventConfigService.updateEventWhitelabelSettings(request.getId(), isSupraEventUpdating, isSupraEvent, request.getWhitelabelSettings());
        }
        if (request.getEventExternalConfig() != null && request.getEventExternalConfig().getDigitalTicketMode() != null) {
            eventConfigService.updateEventExternalConfig(request.getId(), request.getEventExternalConfig());
        }
        if (request.getAllowChangeSeat() != null || request.getChangeSeat() != null) {
            EventChannelSearchFilter filter = new EventChannelSearchFilter();
            filter.setRequestStatus(EnumSet.of(StatusRequestType.ACCEPTED));
            filter.setSubtype(List.of(ChannelSubtype.PORTAL_WEB));
            filter.setEntityId(Long.valueOf(eventRecord.getIdentidad()));
            List<Long> eventObPortalChannels = channelEventDao.findChannelEvents(request.getId(), filter).stream().map(EventChannelRecord::getChannelId).toList();
            if (!eventObPortalChannels.contains(request.getChangeSeat().getReallocationChannel().getId())) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_CHANNEL_NOT_FOUND);
            }
            eventConfigService.updateEventChangeSeatsConfig(request.getId(), request.getAllowChangeSeat(), request.getChangeSeat());
        }
        if(request.getTransfer() != null && request.getAllowTransferTicket() != null){
            eventConfigService.updateEventTransferTicketConfig(
                    request.getId(), request.getAllowTransferTicket(), request.getTransfer());
        }
        if(request.getPhoneVerificationRequired() != null || request.getAttendantVerificationRequired() != null) {
            eventConfigService.updatePhoneVerificationRequired(request.getId(), request.getPhoneVerificationRequired(), request.getAttendantVerificationRequired());
        }
    }

    @MySQLWrite
    public void postUpdateEvent(Long eventId, EventDTO oldEvent, UpdateEventRequestDTO request) {

        refreshDataService.refreshEvent(eventId, "postUpdate", request);

        NotificationSubtype notificationSubtype = NotificationSubtype.EVENT_GENERAL_DATA;
        if (request != null) {
            checkSessionsToGenerateWhitelist(eventId, oldEvent, request);
            externalEventConsumeNotificationService.notificationEvent(request);

            if (EventStatus.DELETED.equals(request.getStatus())) {
                channelSuggestionsCleanUpService.sendEventSuggestionCleaner(eventId);
                LOGGER.info("{} - Send event to delete in channel suggestions id: {} ", CHANNEL_SUGGESTIONS_CLEAN_UP, eventId);

                // remove related product events
                List<CpanelProductEventRecord> productEventRecords = productEventDao.findByEventId(eventId.intValue(), true);
                if (CollectionUtils.isNotEmpty(productEventRecords)) {
                    for (CpanelProductEventRecord productEventRecord : productEventRecords) {
                        Integer productId = productEventRecord.getProductid();
                        if (productEventRecord.getSessionsselectiontype().equals(SelectionType.RESTRICTED.getId())) {
                            productSessionDao.deleteByProductEventId(productEventRecord.getProducteventid());
                        }

                        productSessionDeliveryPointDao.deleteByProductEventId(productEventRecord.getProducteventid());
                        productEventDeliveryPointDao.deleteByProductEventId(productEventRecord.getProducteventid());
                        productEventDao.delete(productEventRecord);

                        List<CpanelProductEventRecord> productEvents = productEventDao.findByEventId(eventId.intValue(), true);
                        if (CollectionUtils.isEmpty(productEvents)) {
                            UpdateProductDTO updateProductDTO = new UpdateProductDTO();
                            updateProductDTO.setProductState(ProductState.INACTIVE);
                            productService.updateProduct(productId.longValue(), updateProductDTO);
                        }
                    }
                }
            }
        } else {
            notificationSubtype = NotificationSubtype.EVENT_CREATED;
        }

        webhookService.sendEventNotification(eventId, notificationSubtype);
    }

    private void checkSessionsToGenerateWhitelist(Long eventId, EventDTO oldEvent, UpdateEventRequestDTO newEvent) {
        if (EventStatus.READY.equals(newEvent.getStatus()) && !oldEvent.getStatus().equals(EventStatus.READY)) {
            SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
            sessionSearchFilter.setEventId(Collections.singletonList(eventId));
            List<Integer> sessionIds = sessionDao.findFlatSessions(sessionSearchFilter)
                    .stream()
                    .filter(sesion -> DateUtils.isSameDay(sesion.getFechainiciosesion(), new Date())
                            && sesion.getEstado().equals(SessionStatus.READY.getId()))
                    .map(CpanelSesionRecord::getIdsesion)
                    .toList();
            if (CollectionUtils.isNotEmpty(sessionIds)) {
                whitelistGenerationService.generateWhiteList(sessionIds);
            }
            
            publishEventToExternalProviders(eventId, oldEvent);
        }
    }

    private void publishEventToExternalProviders(Long eventId, EventDTO event) {
        if (event.getInventoryProvider() == null || !Provider.ITALIAN_COMPLIANCE.equals(event.getInventoryProvider())) {
            return;
        }
        
        try {
            intDispatcherRepository.publishEvent(event.getEntityId(), eventId);
        } catch (Exception e) {
            LOGGER.error("Error publishing event {} to external providers", eventId, e);
        }
    }

    private void updateEventLanguages(UpdateEventRequestDTO event) {
        if (!CommonUtils.isEmpty(event.getLanguages())) {
            eventLanguageDao.deleteByEvent(event.getId());
            for (EventLanguageDTO language : event.getLanguages()) {
                eventLanguageDao.insert(new CpanelIdiomaComEventoRecord(language.getId().intValue(),
                        event.getId().intValue(), ConverterUtils.isTrueAsByte(language.getDefault())));
            }
        }
    }

    private void updateSessionBookingDisabled(UpdateEventRequestDTO event) {
        if (event.getBooking() != null && CommonUtils.isFalse(event.getBooking().getAllowed())) {
            sessionDao.disableBookingByEvent(event.getId());
        }
    }

    public List<Long> getSessionIdsToArchive(ZonedDateTime archivedDate, int shardId) {

        List<Long> excludeEntityIds = getExcludeEntities();
        List<Session> finalizedEventSessions = sessionDao.getFinalizedEventSessions(archivedDate, excludeEntityIds);

        Set<Long> eventsIdsToArchive = new HashSet<>();
        Set<Long> invalidEvents = new HashSet<>();

        //Identify if sessions from Events to archive has valid state to archive all event sessions
        for (Session session : finalizedEventSessions) {
            Integer sessionShard = sessionDao.getSessionShard(session.getSessionId());
            if (sessionShard.equals(shardId)) {
                if (archivableState(session.getStatus())) {
                    eventsIdsToArchive.add(session.getEventId());
                } else {
                    invalidEvents.add(session.getEventId());
                    LOGGER.warn("[EVENT ARCHIVER] shard: {} - Event: {} - Invalid session {} state {} for finalized event",
                            shardId, session.getEventId(), session.getSessionId(), session.getStatus());
                }
            }
        }

        //Clean eventsIdsToArchive from invalidEvents identified
        eventsIdsToArchive.removeAll(invalidEvents);

        //Obtain all sessionIds of eventsIdsToArchive
        List<Long> sessionIdsToArchive = finalizedEventSessions.stream().
                filter(s -> eventsIdsToArchive.contains(s.getEventId())).
                map(Session::getSessionId).
                toList();

        LOGGER.info("[EVENT ARCHIVER] shard: {} - Events to archive: {} - Sessions to archive: {} - "
                        + "Finalized events without all sessions finalized: {}",
                shardId, eventsIdsToArchive.size(), sessionIdsToArchive.size(), invalidEvents.size());

        eventDao.archiveEvents(eventsIdsToArchive);
        LOGGER.info("[EVENT ARCHIVER] shard: {} - Archived events: {}", shardId, eventsIdsToArchive);

        sessionDao.archiveSessions(sessionIdsToArchive);
        LOGGER.info("[EVENT ARCHIVER] shard: {} - Archived-deleted sessions: {}", shardId, sessionIdsToArchive);

        return sessionIdsToArchive;
    }

    private List<Long> getExcludeEntities() {
        if (StringUtils.isNotEmpty(excludeEntities)) {
            return Stream.of(excludeEntities.split(","))
                    .map(Long::parseLong)
                    .toList();
        }
        return new ArrayList<>();
    }

    public List<SessionTaxDTO> getTaxesBySession(Long eventId, Long sessionId) {
        List<SessionTaxDTO> taxes = new ArrayList<>();

        try {
            CpanelImpuestoRecord ticketTax = taxDao.getTicketTaxBySession(eventId, sessionId);
            SessionTaxDTO ticketTaxDTO = SessionTaxConverter.taxToSessionTaxDTO(ticketTax, SessionTaxDTO.SessionTaxType.TICKET_TAX);
            taxes.add(ticketTaxDTO);
        } catch (DataAccessException ex) {
            LOGGER.warn("Ticket tax for event {} and session {} not found", eventId, sessionId);
        }

        try {
            CpanelImpuestoRecord chargesTax = taxDao.getChargesTaxBySession(eventId, sessionId);
            SessionTaxDTO chargesTaxDTO = SessionTaxConverter.taxToSessionTaxDTO(chargesTax, SessionTaxDTO.SessionTaxType.CHARGES_TAX);
            taxes.add(chargesTaxDTO);
        } catch (DataAccessException ex) {
            LOGGER.warn("Charges tax for event {} and session {} not found", eventId, sessionId);
        }

        if (taxes.isEmpty()) {
            throw new MSEventNotFoundException();
        }

        return taxes;
    }

    public List<TicketTemplateDTO> findTicketTemplatesByEventId(Integer eventId) {
        List<TicketTemplateDTO> result = new ArrayList<>();
        List<TicketTemplateRecord> records = eventDao.getTicketTemplates(eventId);
        for (TicketTemplateRecord record : records) {
            result.add(TicketTemplateConverter.convert(record));
        }
        return result;
    }


    private void validateUpdate(UpdateEventRequestDTO request, EventRecord eventRecord) {
        if (request.getId() == null || request.getId() < 1) {
            throw new OneboxRestException(BAD_PARAMETER, "request id is mandatory", null);
        }
        if (EventStatus.DELETED.getId().equals(eventRecord.getEstado())) {
            throw new OneboxRestException(CoreErrorCode.FORBIDDEN_OPERATION, "request is already deleted. It cant be updated!", null);
        }
        if (request.getName() != null) {
            validateUpdateName(request, eventRecord);
        }
        if (!CommonUtils.isEmpty(request.getLanguages()) &&
                request.getLanguages().stream().filter(l -> l.getDefault().equals(true)).count() != 1) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_INVALID_DEFAULT_LANGUAGE, "request languages must define exactly 1 by default", null);
        }
        if (request.getUseTieredPricing() != null) {
            validateUseTiers(request.getId(), request.getUseTieredPricing(), CommonUtils.isTrue(eventRecord.getUsetieredpricing()));
        }
        boolean statusChanged = request.getStatus() != null && !request.getStatus().getId().equals(eventRecord.getEstado());
        if (statusChanged) {
            boolean useTieredPricing = Optional
                    .ofNullable(request.getUseTieredPricing())
                    .orElse(ConverterUtils.isByteAsATrue(eventRecord.getUsetieredpricing()));
            validateUpdateStatus(request, eventRecord.getEstado(), useTieredPricing, eventRecord.getFechaventa(), eventRecord.getFechafin(), eventRecord.getTaxmode());
        }
        if (request.getTour() != null && request.getTour().getId() != null) {
            CpanelGiraRecord tour = tourDao.findById(request.getTour().getId().intValue());
            if (tour == null || !tour.getIdentidad().equals(eventRecord.getIdentidad())) {
                throw new OneboxRestException(MsEventErrorCode.TOUR_NOT_FOUND, "request not found for tour", null);
            }
        }

        if (request.getSubscriptionListId() != null) {
            SubscriptionDTO subscription = subscriptionsRepository.getSubscriptionList(eventRecord.getIdentidad(), request.getSubscriptionListId());
            if (subscription == null || BooleanUtils.isFalse(subscription.getActive())) {
                throw OneboxRestException.builder(MsEventSessionErrorCode.SUBSCRIPTION_LIST_ID_NOT_FOUND).build();
            }
        }
        validateUpdateGroups(request, eventRecord);
        validateUpdatePackType(request, eventRecord);
        validateUpdateTicketTemplates(request, eventRecord);
        if ((request.getStatus() != null && !request.getStatus().getId().equals(EventStatus.IN_PROGRAMMING.getId()))
                || (request.getStatus() == null && !eventRecord.getEstado().equals(EventStatus.IN_PROGRAMMING.getId()))) {
            if (
                    (eventRecord.getInvoiceprefixid() == null && request.getInvoicePrefixId() != null)
                            ||
                            (eventRecord.getInvoiceprefixid() != null && request.getInvoicePrefixId() != null
                                    && !eventRecord.getInvoiceprefixid().equals(request.getInvoicePrefixId()))
            ) {
                throw OneboxRestException.builder(MsEventErrorCode.INVOICE_PREFIX_CANNOT_BE_MODIFIED).build();
            }
        }

        if (((eventRecord.getInvoiceprefixid() == null && request.getInvoicePrefixId() != null)
                || (eventRecord.getInvoiceprefixid() != null && request.getInvoicePrefixId() != null
                && !eventRecord.getInvoiceprefixid().equals(request.getInvoicePrefixId())))
                && !CommonUtils.isTrue(eventRecord.getUseSimplifiedInvoice())) {
            throw OneboxRestException.builder(MsEventErrorCode.PRODUCER_SIMPLIFIED_INVOICE_FLAG).build();
        }

        if (request.getUseProducerFiscalData() != null && !request.getUseProducerFiscalData()
                && eventRecord.getUsardatosfiscalesproductor() != null && CommonUtils.isTrue(eventRecord.getUsardatosfiscalesproductor())) {
            request.setInvoicePrefixId(null);
        } else if ((request.getUseProducerFiscalData() != null && !request.getUseProducerFiscalData()) || (request.getUseProducerFiscalData() == null && eventRecord.getUsardatosfiscalesproductor() != null
                && !CommonUtils.isTrue(eventRecord.getUsardatosfiscalesproductor()))) {
            if (request.getInvoicePrefixId() != null || (request.getInvoicePrefixId() == null && eventRecord.getInvoiceprefixid() != null)) {
                throw new OneboxRestException(MsEventErrorCode.SIMPLIFIED_INVOICES_CANNOT_BE_USED);
            }
        }
        if (request.getAccommodationsConfig() != null) {
            if (CommonUtils.isTrue(request.getAccommodationsConfig().getEnabled())) {
                if (request.getAccommodationsConfig().getVendor() == null
                        || request.getAccommodationsConfig().getValue() == null) {
                    throw new OneboxRestException(MsEventErrorCode.EVENT_ACCOMMODATIONS_CONFIG_NOT_PROPERLY_ENABLED);
                }
                Integer eventEntityId = eventRecord.getIdentidad();
                AccommodationsEntityConfig entityConfig = Optional.ofNullable(entitiesRepository.getEntityConfig(eventEntityId))
                        .flatMap(conf -> Optional.ofNullable(conf.getAccommodationsConfig()))
                        .orElse(null);
                if (entityConfig == null || CommonUtils.isFalse(entityConfig.getEnabled())) {
                    throw new OneboxRestException(MsEventErrorCode.ACCOMMODATIONS_CONFIG_NOT_ENABLED_BY_ENTITY);
                } else if (!entityConfig.getAllowedVendors().contains(
                        AccommodationsVendor.valueOf(request.getAccommodationsConfig().getVendor().name()))) {
                    throw new OneboxRestException(MsEventErrorCode.ACCOMMODATIONS_VENDOR_NOT_ALLOWED_BY_ENTITY);
                }
            }
        }

        if (request.getWhitelabelSettings() != null && request.getWhitelabelSettings().getSessionSelection() != null) {
            EventSessionSelectionDTO ss = request.getWhitelabelSettings().getSessionSelection();
            if (isSupraEvent(request, eventRecord) &&
                    (Boolean.FALSE.equals(ss.getRestrictType())
                            || SessionSelectType.CALENDAR.equals(ss.getType())
                            || ss.getCalendar() != null
                            || ss.getList() == null || Boolean.FALSE.equals(ss.getList().getContainsImage()))) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_TYPE_SUPRA_UI_CONFIGURATION_CONFLICT);
            }
        }

        if (request.getChangeSeat() != null) {
            if (request.getChangeSeat().getEventChangeSeatExpiry() != null &&
                    !ALLOWED_CHANGE_SEAT_EXPIRY_TIME_UNITS.contains(request.getChangeSeat().getEventChangeSeatExpiry().getTimeOffsetLimitUnit())) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_EVENT_CHANGE_SEAT_EXPIRY_TIME_UNIT);
            }
            if (request.getAllowChangeSeat() == null) {
                throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER);
            }
        }

        if (request.getTaxMode() != null) {
            if (EventType.AVET.getId().equals(eventRecord.getTipoevento()) && TaxModeDTO.ON_TOP.equals(request.getTaxMode())) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_TAX_MODE_NOT_ALLOWED);
            }
            if (!request.getTaxMode().getId().equals(eventRecord.getTaxmode()) && ordersRepository.countByEventAndChannel(request.getId(), null) > 0) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_UPDATE_TAX_MODE_HAS_SALES);
            }
            if (EventStatus.READY.getId().equals(eventRecord.getEstado())) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_UPDATE_TAX_MODE_INVALID_EVENT_STATUS);
            }
        }

        if(request.getTransfer() != null && BooleanUtils.isTrue(request.getTransfer().getRestrictTransferBySessions())){
            if(CollectionUtils.isEmpty(request.getTransfer().getAllowedTransferSessions())) {
                throw new OneboxRestException(MsEventErrorCode.EMPTY_TRANSFER_TICKET_SESSION_LIST);
            }
            SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
            sessionSearchFilter.setIds(request.getTransfer().getAllowedTransferSessions());
            sessionSearchFilter.setEventId(List.of(request.getId()));
            List<CpanelSesionRecord> flatSessions = sessionDao.findFlatSessions(sessionSearchFilter);
            if(flatSessions == null || flatSessions.size() != request.getTransfer().getAllowedTransferSessions().size()){
                throw new OneboxRestException(MsEventErrorCode.INVALID_TRANSFER_TICKET_SESSION_LIST);
            }
        }
    }

    private static Boolean isSupraEvent(UpdateEventRequestDTO event, EventRecord eventRecord) {
        return (event.getSupraEvent() != null && Boolean.TRUE.equals(event.getSupraEvent()))
                || (event.getSupraEvent() == null && ConverterUtils.isByteAsATrue(eventRecord.getEssupraevento()));
    }

    private void validateUpdateGroups(UpdateEventRequestDTO event, CpanelEventoRecord eventRecord) {
        if (CommonUtils.isTrue(event.getAllowGroups()) && !EventUtils.isActivity(eventRecord.getTipoevento())) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_GROUP_NOT_ALLOWED);
        }
        if (event.getAllowGroups() != null && !event.getAllowGroups() && CommonUtils.isTrue(eventRecord.getPermitegrupos())) {
            SessionSearchFilter filter = new SessionSearchFilter();
            filter.setEventId(Collections.singletonList(event.getId()));
            filter.setSaleType(Arrays.asList(SessionSalesType.GROUP, SessionSalesType.MIXED));
            if (sessionDao.countByFilter(filter) > 0) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_INVALID_GROUP_DISABLE);
            }
        }
        if (CommonUtils.isTrue(event.getGroupCompanionPayment()) && (
                (event.getGroupPrice() == null && eventRecord.getPreciogrupos() != 2) ||
                        (event.getGroupPrice() != null && event.getGroupPrice() != 2))) {
            throw new OneboxRestException(CoreErrorCode.FORBIDDEN_OPERATION, "companion payment only valid for group price individual", null);
        }
    }

    private void validateUpdatePackType(UpdateEventRequestDTO event, CpanelEventoRecord eventRecord) {
        if (event.getSessionPackType() != null &&
                !eventRecord.getTipoabono().equals(event.getSessionPackType().getId())) {
            SessionSearchFilter filter = new SessionSearchFilter();
            filter.setEventId(Collections.singletonList(event.getId()));
            filter.setSessionPack(true);
            if (sessionDao.countByFilter(filter) > 0) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_ALREADY_HAS_SESSION_PACKS);
            }
        }
    }

    private void validateUpdateTicketTemplates(UpdateEventRequestDTO event, CpanelEventoRecord eventRecord) {
        EventTicketTemplatesDTO templates = event.getTicketTemplates();
        if (templates != null) {
            Integer entityId = eventRecord.getIdentidad();
            commonTicketTemplateService.validateUpdateTicketTemplate(templates.getIndividualTicketPdfTemplateId(), TicketFormat.PDF, entityId);
            commonTicketTemplateService.validateUpdateTicketTemplate(templates.getIndividualTicketPrinterTemplateId(), TicketFormat.ZPL, entityId);
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

    private void validateUseTiers(Long eventId, boolean newValue, boolean oldValue) {
        if (newValue == oldValue) {
            return;
        }
        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setEventId(Collections.singletonList(eventId));
        if (sessionDao.countByFilter(filter).intValue() != 0) {
            throw new OneboxRestException(MsEventTierErrorCode.CHANGE_TIERS_WITH_SESSIONS);
        }

        if (newValue) {
            if (!rateDao.countByEventId(eventId.intValue()).equals(1L)) {
                throw new OneboxRestException(MsEventTierErrorCode.USE_TIERS_WITH_RATES);
            }
            if (venueTemplateDao.countActiveGraphicalVenueTemplates(eventId.intValue()) > 0L) {
                throw new OneboxRestException(MsEventTierErrorCode.USE_TIERS_WITH_GRAPHIC_VENUE_TEMPLATE);
            }
        } else {
            if (tierDao.countByEventId(eventId.intValue(), null) > 0L) {
                throw new OneboxRestException(MsEventTierErrorCode.DEACTIVATE_TIERS_WITH_TIERS_CREATED);
            }
        }
    }

    private void validateUpdateStatus(UpdateEventRequestDTO event, Integer previousStatus, boolean useTieredPricing, Timestamp saleDate, Timestamp endDate, Integer taxMode) {
        if (previousStatus.equals(EventStatus.IN_PROGRAMMING.getId())) {
            SessionSearchFilter sessionFilter = new SessionSearchFilter();
            sessionFilter.setEventId(Collections.singletonList(event.getId()));
            sessionFilter.setStatus(Arrays.asList(SessionStatus.PLANNED, SessionStatus.SCHEDULED, SessionStatus.READY, SessionStatus.IN_PROGRESS));
            sessionFilter.setGenerationStatus(Arrays.asList(SessionGenerationStatus.IN_PROGRESS, SessionGenerationStatus.PENDING));
            if (venueTemplateDao.countActiveVenueTemplates(event.getId().intValue()) > 0 || sessionDao.countByFilter(sessionFilter) > 0) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_INVALID_STATUS_GENERATION,
                        "state transition to IN_PROGRAMMING forbidden for event " +
                                "with session or venue config pending seat generations", null);
            }
        }
        if (event.getStatus() == EventStatus.FINISHED) {
            SessionSearchFilter filter = new SessionSearchFilter();
            filter.setEventId(Collections.singletonList(event.getId()));
            filter.setStatus(Arrays.asList(SessionStatus.PLANNED, SessionStatus.SCHEDULED, SessionStatus.READY, SessionStatus.IN_PROGRESS));
            if (sessionDao.countByFilter(filter) > 0) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_INVALID_STATUS_FINISH, "event with alive sessions cant be finished", null);
            }
        }
        validateDeleteStatus(event, useTieredPricing);

        if (event.getStatus().equals(EventStatus.READY)) {
            if (taxMode == null) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_EVENT_TAX_MODE);
            }
            if (useTieredPricing) {

                if (saleDate == null || endDate == null) {
                    throw new OneboxRestException(MsEventTierErrorCode.EVENT_DATES_NEEDED_TO_ACTIVATE_TIERED_EVENT);
                }

                Map<Integer, List<TierRecord>> tiersByTemplate = tierDao.findByEventId(event.getId().intValue(), null, null, null)
                        .stream()
                        .collect(Collectors.groupingBy(TierRecord::getVenueTemplateId));

                for (List<TierRecord> tiers : tiersByTemplate.values()) {
                    validateTiersOfVenueTemplate(tiers);
                }
            }
        }

    }

    private void validateDeleteStatus(UpdateEventRequestDTO event, boolean useTieredPricing) {
        if (event.getStatus() == EventStatus.DELETED) {
            if (ordersRepository.countByEventAndChannel(event.getId(), null) == 0L) {
                eventRemoveService.removeSeats(event.getId().intValue());
                eventSecondaryMarketConfigService.deleteEventSecondaryMarketConfig(event.getId());
                if (useTieredPricing) {
                    TierModificationMessage message = new TierModificationMessage();
                    message.setAction(TierModificationMessage.Action.DELETE_TIERS_FOR_EVENT);
                    message.setEventId(event.getId());
                    try {
                        tierModificationProducer.sendMessage(message);
                    } catch (Exception e) {
                        LOGGER.error("Error while deleting tiers for event", e);
                    }
                }
            } else {
                throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_REMOVABLE, "You cannot delete the event with id: " + event.getId() +
                        " because it have sold seats.", null);
            }
        }
    }

    private static void validateTiersOfVenueTemplate(List<TierRecord> tiers) {
        Map<Integer, List<TierRecord>> tiersByPriceType = tiers
                .stream()
                .collect(Collectors.groupingBy(TierRecord::getIdzona));

        validatePerpetualTierOnSale(tiersByPriceType.values());

    }

    private static void validatePerpetualTierOnSale(Collection<List<TierRecord>> tiersByPriceType) {
        boolean invalid = tiersByPriceType
                .stream()
                // Get all tier changes
                .flatMap(List::stream)
                .map(TierRecord::getFechaInicio)
                .map(Timestamp::toInstant)
                .distinct()
                // As soon as there is a tier change where there aren't any active <tier>s
                // for all the pricetypes we know its incorrect
                .anyMatch(timestamp -> tiersByPriceType.stream().noneMatch(tiers -> onSaleTierAt(tiers, timestamp)));

        if (invalid) {
            throw new OneboxRestException(MsEventTierErrorCode.NO_ON_SALE_TIER_FOR_ENTIRE_EVENT_LIFESPAN);
        }
    }

    private static boolean onSaleTierAt(List<TierRecord> recordsOfPriceType, Instant date) {
        List<EvaluableTierWrapper> onSaleTiers = recordsOfPriceType.stream()
                .filter(t -> ConverterUtils.isByteAsATrue(t.getVenta()))
                .map(EvaluableTierWrapper::new)
                .collect(Collectors.toList());
        return TierEvaluator.getActivePriceTypeTierAt(onSaleTiers, date) != null;
    }


    private void validateUpdateName(UpdateEventRequestDTO event, CpanelEventoRecord eventRecord) {
        if (event.getName().length() > EVENT_NAME_LENGTH) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_NAME_FORMAT, "event name length cannot be above "
                    + EVENT_NAME_LENGTH + " characters", null);
        }
        if (event.getName().contains(EVENT_INVALID_CHAR)) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_NAME_FORMAT, "event name has invalid characters. | not allowed", null);
        }
        if (!event.getName().equals(eventRecord.getNombre())) {
            EventSearchFilter filter = new EventSearchFilter();
            filter.setEntityId(eventRecord.getIdentidad().longValue());
            filter.setName(event.getName());
            if (eventDao.countByFilter(filter) > 0) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_NAME_CONFLICT, "event name already used for entity", null);
            }
        }
    }

    private static boolean archivableState(int sessionState) {
        return sessionState == SessionState.FINALIZED.value()
                || sessionState == SessionState.DELETED.value()
                || sessionState == SessionState.CANCELLED.value()
                || sessionState == SessionState.CANCELLED_EXTERNAL.value();
    }

    public EventRecord getAndCheckEvent(Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(BAD_PARAMETER, "eventId must have a value and be greater than 0", null);
        }
        Map.Entry<EventRecord, List<VenueRecord>> event = eventDao.findEvent(eventId);
        if (event == null || EventStatus.DELETED.getId().equals(event.getKey().getEstado())) {
            throw OneboxRestException.builder(MsEventErrorCode.EVENT_NOT_FOUND).
                    setMessage("Event: " + event + " not found").build();
        }
        return event.getKey();
    }

    public void addFavoriteChannelsToEvent(Long eventId, List<Long> entityFavoriteChannels) {
        entityFavoriteChannels.forEach(channel -> eventChannelService.createEventChannel(eventId, channel));
    }

    private void fillEventChangeSeat(EventDTO dto, EventConfig eventConfig) {
        if (eventConfig != null && eventConfig.getEventChangeSeatConfig() != null) {
            dto.setChangeSeat(EventConverter.toEventChangeSeatDTO(eventConfig.getEventChangeSeatConfig()));
            dto.setAllowChangeSeat(eventConfig.getEventChangeSeatConfig().getAllowChangeSeat());
        }
    }

    private void fillEventTransferTicket(EventDTO dto, EventConfig eventConfig){
        if (eventConfig != null && eventConfig.getEventTransferTicketConfig() != null) {
            EventTransferTicketConfig eventTransferTicketConfig = eventConfig.getEventTransferTicketConfig();
            dto.setAllowTransferTicket(eventTransferTicketConfig.getAllowTransferTicket());
            EventTransferTicketDTO eventTransferTicketDTO = new EventTransferTicketDTO();
            eventTransferTicketDTO.setMaxTicketTransfers(eventTransferTicketConfig.getMaxTicketTransfers());
            eventTransferTicketDTO.setEnableMaxTicketTransfers(eventTransferTicketConfig.getEnableMaxTicketTransfers());
            eventTransferTicketDTO.setTransferTicketMinDelayTime(eventTransferTicketConfig.getTransferTicketMinDelayTime());
            eventTransferTicketDTO.setTransferTicketMaxDelayTime(eventTransferTicketConfig.getTransferTicketMaxDelayTime());
            eventTransferTicketDTO.setRecoveryTicketMaxDelayTime(eventTransferTicketConfig.getRecoveryTicketMaxDelayTime());
            eventTransferTicketDTO.setTransferPolicy(eventTransferTicketConfig.getTransferPolicy());
            eventTransferTicketDTO.setRestrictTransferBySessions(eventTransferTicketConfig.getRestrictTransferBySessions());
            eventTransferTicketDTO.setAllowedTransferSessions(eventTransferTicketConfig.getAllowedTransferSessions());
            eventTransferTicketDTO.setAllowMultipleTransfers(eventTransferTicketConfig.getAllowMultipleTransfers());
            dto.setTransfer(eventTransferTicketDTO);
        }
    }
}
