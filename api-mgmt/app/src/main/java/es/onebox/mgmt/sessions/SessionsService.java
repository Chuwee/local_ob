package es.onebox.mgmt.sessions;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.dal.dto.couch.order.OrderProductDTO;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.common.LimitlessValueDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.common.dto.QuotaCapacity;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.Match;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.MatchPriceDTO;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.integration.avetsocketconnector.repository.AvetSocketConnectorRepository;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.dto.Tax;
import es.onebox.mgmt.datasources.ms.entity.enums.TaxType;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.Tier;
import es.onebox.mgmt.datasources.ms.event.dto.TierExtended;
import es.onebox.mgmt.datasources.ms.event.dto.Tiers;
import es.onebox.mgmt.datasources.ms.event.dto.event.AdditionalConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.Attribute;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventAvetConfigType;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannels;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventStatus;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import es.onebox.mgmt.datasources.ms.event.dto.event.Provider;
import es.onebox.mgmt.datasources.ms.event.dto.event.SessionPackType;
import es.onebox.mgmt.datasources.ms.event.dto.event.VenueTemplatePrice;
import es.onebox.mgmt.datasources.ms.event.dto.session.CloneSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.CreateSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.DeleteSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.LinkedSession;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionAttendantsConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionBulkUpdateResponse;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionGroupConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionsGroups;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPrice;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceZone;
import es.onebox.mgmt.datasources.ms.event.repository.AttendantTicketsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.DynamicPriceRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchRequest;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.mgmt.datasources.ms.order.repository.OrderProductsRepository;
import es.onebox.mgmt.datasources.ms.order.repository.OrdersRepository;
import es.onebox.mgmt.datasources.ms.ticket.dto.SessionOccupationDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.SessionPriceZoneOccupationResponseDTO;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.PriceTypeCapacity;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Quota;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.entities.converter.AttributeConverter;
import es.onebox.mgmt.entities.dto.AttributeDTO;
import es.onebox.mgmt.entities.dto.AttributeRequestValuesDTO;
import es.onebox.mgmt.entities.dto.AttributeSearchFilter;
import es.onebox.mgmt.entities.enums.AttributeScope;
import es.onebox.mgmt.entities.factory.InventoryProviderService;
import es.onebox.mgmt.entities.factory.InventoryProviderServiceFactory;
import es.onebox.mgmt.events.converter.AttendantsConverter;
import es.onebox.mgmt.events.dto.channel.EventChannelSearchFilter;
import es.onebox.mgmt.events.enums.TicketType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.exception.ApiMgmtTierErrorCode;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandler;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandlerStrategyProvider;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.converters.MatchConverter;
import es.onebox.mgmt.sessions.converters.SessionConverter;
import es.onebox.mgmt.sessions.dto.AttendantTicketsSessionStatusDTO;
import es.onebox.mgmt.sessions.dto.BaseSessionDTO;
import es.onebox.mgmt.sessions.dto.CloneSessionRequestDTO;
import es.onebox.mgmt.sessions.dto.CreateSessionRequestDTO;
import es.onebox.mgmt.sessions.dto.IntegrationEventEntityDTO;
import es.onebox.mgmt.sessions.dto.LinkedSessionDTO;
import es.onebox.mgmt.sessions.dto.MatchDTO;
import es.onebox.mgmt.sessions.dto.PackBlockingActionsDTO;
import es.onebox.mgmt.sessions.dto.RateDTO;
import es.onebox.mgmt.sessions.dto.SearchSessionsDTO;
import es.onebox.mgmt.sessions.dto.SearchSessionsResponse;
import es.onebox.mgmt.sessions.dto.SeatDeleteStatus;
import es.onebox.mgmt.sessions.dto.SessionAdditionalConfigDTO;
import es.onebox.mgmt.sessions.dto.SessionAttendantTicketsDTO;
import es.onebox.mgmt.sessions.dto.SessionAvailabilityDTO;
import es.onebox.mgmt.sessions.dto.SessionAvailabilityDetailDTO;
import es.onebox.mgmt.sessions.dto.SessionDTO;
import es.onebox.mgmt.sessions.dto.SessionGroupDTO;
import es.onebox.mgmt.sessions.dto.SessionPriceTypesAvailabilityDTO;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.sessions.dto.SessionSettingsDTO;
import es.onebox.mgmt.sessions.dto.SessionSettingsLimitsDTO;
import es.onebox.mgmt.sessions.dto.SessionsGroupsDTO;
import es.onebox.mgmt.sessions.dto.SessionsGroupsSearchFilter;
import es.onebox.mgmt.sessions.dto.SettingsAccessControlDTO;
import es.onebox.mgmt.sessions.dto.TierQuotaAvailabilityDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionResponseDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionSettingsDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionsRequestDTO;
import es.onebox.mgmt.sessions.enums.SessionField;
import es.onebox.mgmt.sessions.enums.SessionStatus;
import es.onebox.mgmt.sessions.enums.SubscriptionListType;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.converter.VenueTemplateQuotaConverter;
import es.onebox.mgmt.venues.dto.capacity.QuotaCapacityDTO;
import es.onebox.mgmt.venues.dto.capacity.QuotaCapacityListDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
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

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.EVENT_TYPE_NOT_ALLOWED;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.FORBIDDEN;

@Service
public class SessionsService {

    private static final Set<SessionStatus> ALLOWED_UPDATE_STATUS = Set.of(
            SessionStatus.PLANNED, SessionStatus.PREVIEW, SessionStatus.SCHEDULED, SessionStatus.READY,
            SessionStatus.CANCELLED, SessionStatus.FINALIZED);

    private static final int BULK_SESSION_CREATE_LIMIT = 1000;
    private static final Long PRODUCTS_LIMIT = 1000L;
    public static final String ONLY_THE_NAME_CAN_BE_UPDATED_FOR_FINALIZED_SESSIONS = "Only the name can be updated for finalized sessions.";

    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;
    private final ValidationService validationService;
    private final EventsRepository eventsRepository;
    private final EventChannelsRepository eventChannelsRepository;
    private final TicketsRepository ticketsRepository;
    private final VenuesRepository venuesRepository;
    private final AvetConfigRepository avetConfigRepository;
    private final OrderProductsRepository orderProductsRepository;
    private final AttendantTicketsRepository attendantTicketsRepository;
    private final EntitiesRepository entitiesRepository;
    private final AvetSocketConnectorRepository avetSocketConnectorRepository;
    private final OrdersRepository ordersRepository;
    private final DispatcherRepository dispatcherRepository;
    private final InventoryProviderServiceFactory externalInventoryProviderServiceFactory;
    private final DynamicPriceRepository dynamicPriceRepository;
    private final ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider;


    @Autowired
    public SessionsService(
            SecurityManager securityManager, MasterdataService masterdataService, EventsRepository eventsRepository,
            TicketsRepository ticketsRepository, VenuesRepository venuesRepository, AvetConfigRepository avetConfigRepository,
            ValidationService validationService, OrderProductsRepository orderProductsRepository,
            AttendantTicketsRepository attendantTicketsRepository, EntitiesRepository entitiesRepository,
            AvetSocketConnectorRepository avetSocketConnectorRepository, EventChannelsRepository eventChannelsRepository,
            OrdersRepository ordersRepository, DispatcherRepository dispatcherRepository,
            InventoryProviderServiceFactory externalInventoryProviderServiceFactory, DynamicPriceRepository dynamicPriceRepository,
            ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider) {
        this.securityManager = securityManager;
        this.masterdataService = masterdataService;
        this.eventsRepository = eventsRepository;
        this.ticketsRepository = ticketsRepository;
        this.venuesRepository = venuesRepository;
        this.avetConfigRepository = avetConfigRepository;
        this.validationService = validationService;
        this.orderProductsRepository = orderProductsRepository;
        this.attendantTicketsRepository = attendantTicketsRepository;
        this.entitiesRepository = entitiesRepository;
        this.avetSocketConnectorRepository = avetSocketConnectorRepository;
        this.eventChannelsRepository = eventChannelsRepository;
        this.ordersRepository = ordersRepository;
        this.dispatcherRepository = dispatcherRepository;
        this.externalInventoryProviderServiceFactory = externalInventoryProviderServiceFactory;
        this.dynamicPriceRepository = dynamicPriceRepository;
        this.externalAccessControlHandlerStrategyProvider = externalAccessControlHandlerStrategyProvider;
    }

    public SessionDTO getSession(Long eventId, Long sessionId) {
        Session repositorySession = validationService.getAndCheckSession(eventId, sessionId);

        SessionDTO session = SessionConverter.fromMsEvent(repositorySession);
        fillSession(session, repositorySession);

        SessionAttendantsConfigDTO sessionAttendantsConfig = attendantTicketsRepository.getSessionAttendantsConfig(sessionId, eventId);
        EventChannels channels = eventChannelsRepository.getEventChannels(eventId,
                EventChannelSearchFilter.builder().limit(999L).offset(0L).build());
        session.getSettings().setAttendantTickets(AttendantsConverter.sessionFromMsEvent(sessionAttendantsConfig, channels));
        session.setUpdatingCapacity(ticketsRepository.getSessionCapacityUpdating(eventId, sessionId));
        session.setGeneratingCapacity(ticketsRepository.getSessionCapacityGenerating(eventId, sessionId));
        session.setHasSales(ordersRepository.sessionHasOrders(sessionId));

        return session;
    }

    public SessionAdditionalConfigDTO getSessionAdditionalConfig(Long eventId, Long sessionId) {
        Session session = validationService.getAndCheckSession(eventId, sessionId);

        Event event = validationService.getAndCheckEvent(eventId);

        if (session.getExternalId() == null) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_NOT_AVET);
        }

        MatchDTO matchDTO = MatchConverter.fromMs(avetConfigRepository.getMatch(event.getExternalId(), session.getExternalId()));

        IntegrationEventEntityDTO integrationEvent = avetConfigRepository.getIntegrationEvents(sessionId);

        Boolean externalState = integrationEvent == null ? Boolean.FALSE : integrationEvent.getState();

        return new SessionAdditionalConfigDTO(externalState, matchDTO);
    }

    public List<LinkedSessionDTO> getLinkedSessions(Long eventId, Long sessionId) {
        validationService.getAndCheckSession(eventId, sessionId);
        List<LinkedSession> linkedSessionsDatasource = eventsRepository.getLinkedSessions(eventId, sessionId);
        return linkedSessionsDatasource.stream()
                .filter(Objects::nonNull)
                .map(SessionConverter::fromMsLinkedSession)
                .collect(Collectors.toList());
    }

    public SearchSessionsResponse searchSessions(Long eventId, SessionSearchFilter filter) {
        securityManager.checkEntityAccessible(filter);
        if (CollectionUtils.isNotEmpty(filter.getFields())) {
            // in a future development, this field should be explicitly added by the request, but for the moment we patch OB-25337 like this
            filter.getFields().add(SessionField.VENUE_TEMPLATE_TIMEZONE.getName());
        }
        Sessions sessions = eventsRepository.getSessions(SecurityUtils.getUserOperatorId(), eventId, filter);

        SearchSessionsResponse sessionsDTO = new SearchSessionsResponse();
        List<SearchSessionsDTO> outList = sessions.getData().stream()
                .map(s -> {
                    SearchSessionsDTO session = (SearchSessionsDTO) SessionConverter.fromMsEvent(s, new SearchSessionsDTO());
                    fillSession(session, s);
                    SessionConverter.fillSettingsSearchSessions(session, s, filter);
                    return session;
                }).collect(Collectors.toList());
        Set<Long> capacityUpdatingSessions = new HashSet<>(ticketsRepository.getEventSessionsCapacityUpdating(eventId));
        Set<Long> capacityGeneratingSessions = new HashSet<>(ticketsRepository.getEventSessionsCapacityGenerating(eventId));
        outList.forEach(session -> session.setUpdatingCapacity(capacityUpdatingSessions.contains(session.getId())));
        outList.forEach(session -> session.setGeneratingCapacity(capacityGeneratingSessions.contains(session.getId())));
        sessionsDTO.setData(outList);
        sessionsDTO.setMetadata(sessions.getMetadata());

        return sessionsDTO;
    }

    public SessionsGroupsDTO searchSessionsGroups(Long eventId, SessionsGroupsSearchFilter filter) {
        securityManager.checkEntityAccessible(filter);
        SessionsGroups sessionGroups = eventsRepository.getSessionsGroups(SecurityUtils.getUserOperatorId(), eventId, filter);
        return SessionConverter.toDTO(sessionGroups);
    }

    public Long createSession(Long eventId, CreateSessionRequestDTO sessionData) {

        Event event = validationService.getAndCheckEvent(eventId);

        if (SessionUtils.isAvetEvent(event.getType())) {
            return createAvetSession(event, sessionData);
        }
        validateCreateSession(sessionData, event);
        validateExternalCreateEventSessions(event, sessionData);

        InventoryProviderService inventoryProviderService = getInventoryProvider(event.getEntityId(), event.getInventoryProvider());

        if (!SessionUtils.isSgaEvent(event.getInventoryProvider())) {
            SessionUtils.checkRates(sessionData.getRates());
        }

        Entity entity = entitiesRepository.getEntity(event.getEntityId());

        if (sessionData.getTaxTicketId() == null && BooleanUtils.isNotTrue(sessionData.getAutomaticTaxes())) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "tax_ticket_id is mandatory", null);
        }
        if (sessionData.getTaxChargesId() == null && BooleanUtils.isNotTrue(sessionData.getAutomaticTaxes())) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "tax_charges_id is mandatory", null);
        }

        CreateSessionData session = SessionConverter.toMsEventSessionData(sessionData, event.getEntityId(), entity);
        if (BooleanUtils.isTrue(sessionData.getAutomaticTaxes())) {
            VenueTemplate venueTemplate = venuesRepository.getVenueTemplate(sessionData.getVenueTemplateId());
            if (venueTemplate == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND);
            }
            List<Tax> ticketTaxes = entitiesRepository.getEntityTaxes(event.getEntityId(), event.getId(), venueTemplate.getVenue().getId(), TaxType.TICKET);
            List<Tax> chargesTaxes = entitiesRepository.getEntityTaxes(event.getEntityId(), event.getId(), venueTemplate.getVenue().getId(), TaxType.CHARGES);
            if (ticketTaxes.isEmpty() || chargesTaxes.isEmpty()) {
                throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_TAXES_NOT_FOUND);
            }
            session.setTaxId(ticketTaxes.get(0).getId());
            session.setTicketTaxIds(ticketTaxes.stream().map(Tax::getDetailId).collect(Collectors.toList()));
            session.setChargeTaxId(chargesTaxes.get(0).getId());
            session.setChargeTaxIds(chargesTaxes.stream().map(Tax::getDetailId).collect(Collectors.toList()));
        } else {
            session.setTaxId(sessionData.getTaxTicketId());
            session.setChargeTaxId(sessionData.getTaxChargesId());
        }

        Long sessionId = inventoryProviderService.createSession(eventId, session);

        checkAndProcessExternalCreateEventSessions(event, List.of(sessionId));

        return sessionId;

    }

    public Long createAvetSession(Event event, CreateSessionRequestDTO sessionData) {
        Long eventId = event.getId();
        SessionUtils.checkAvetMatch(sessionData);

        RateDTO rateDTO = createEventRate(event, sessionData.getAdditionalConfig().getAvetMatchId());
        sessionData.setRates(Collections.singletonList(rateDTO));
        sessionData.getDates().setBookingsEndDate(sessionData.getDates().getSalesEndDate());
        sessionData.getDates().setBookingsStartDate(sessionData.getDates().getSalesStartDate());

        SessionUtils.checkRates(sessionData.getRates());
        Long sessionId;
        Entity entity = entitiesRepository.getEntity(event.getEntityId());

        try {

            CreateSessionData session = SessionConverter.toMsEventSessionData(sessionData, entity);

            sessionId = eventsRepository.createSession(eventId, session);
        } catch (OneboxRestException e) {
            eventsRepository.deleteEventRate(eventId, rateDTO.getId());
            throw e;
        }

        Long matchId = sessionData.getAdditionalConfig().getAvetMatchId();
        Long venueTemplateId = sessionData.getVenueTemplateId();

        updateAvetVenueTemplatePrices(event, matchId, venueTemplateId, rateDTO.getId());

        return sessionId;
    }

    public List<Long> createSessions(Long eventId, List<CreateSessionRequestDTO> sessionData) {

        if (sessionData.size() > BULK_SESSION_CREATE_LIMIT) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "Bulk create maximum limit exceeded: " + BULK_SESSION_CREATE_LIMIT, null);
        }

        Event event = validationService.getAndCheckEvent(eventId);

        if (event.getType().equals(EventType.AVET)) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.AVET_SESSIONS_CANNOT_BE_BULK_CREATED, "Avet sessions cannot be created in bulk", null);
        }

        for (CreateSessionRequestDTO session : sessionData) {
            SessionUtils.checkRates(session.getRates());
            if (session.getLoyaltyPointsConfig() != null) {
                validateLoyaltyPointsConfig(event.getEntityId());
            }
            validateExternalCreateEventSessions(event, session);
        }

        Entity entity = entitiesRepository.getEntity(event.getEntityId());
        List<CreateSessionData> createSessionData = new ArrayList<>();
        for (CreateSessionRequestDTO sessionDateElement : sessionData) {
            CreateSessionData createSessionDataElement = SessionConverter.toMsEventSessionData(sessionDateElement, entity);

            if (sessionDateElement.getTaxTicketId() == null && BooleanUtils.isNotTrue(sessionDateElement.getAutomaticTaxes())) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER, "tax_ticket_id is mandatory", null);
            }
            if (sessionDateElement.getTaxChargesId() == null && BooleanUtils.isNotTrue(sessionDateElement.getAutomaticTaxes())) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER, "tax_charges_id is mandatory", null);
            }

            if (BooleanUtils.isTrue(sessionDateElement.getAutomaticTaxes())) {
                VenueTemplate venueTemplate = venuesRepository.getVenueTemplate(sessionDateElement.getVenueTemplateId());
                if (venueTemplate == null) {
                    throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND);
                }
                List<Tax> ticketTaxes = entitiesRepository.getEntityTaxes(event.getEntityId(), event.getId(), venueTemplate.getVenue().getId(),TaxType.TICKET);
                List<Tax> chargesTaxes = entitiesRepository.getEntityTaxes(event.getEntityId(), event.getId(), venueTemplate.getVenue().getId(), TaxType.CHARGES);
                createSessionDataElement.setTaxId(ticketTaxes.get(0).getId());
                createSessionDataElement.setTicketTaxIds(ticketTaxes.stream().map(Tax::getDetailId).collect(Collectors.toList()));
                createSessionDataElement.setChargeTaxId(chargesTaxes.get(0).getId());
                createSessionDataElement.setChargeTaxIds(chargesTaxes.stream().map(Tax::getDetailId).collect(Collectors.toList()));
            } else {
                createSessionDataElement.setTaxId(createSessionDataElement.getTaxId());
                createSessionDataElement.setChargeTaxId(createSessionDataElement.getChargeTaxId());
            }
            createSessionData.add(createSessionDataElement);
        }

        List<Long> sessionIds = eventsRepository.createSessions(eventId, createSessionData);

        checkAndProcessExternalCreateEventSessions(event, sessionIds);

        return sessionIds;
    }

    public Long cloneSession(Long eventId, Long sourceSessionId, CloneSessionRequestDTO sessionData) {
        Session sessionToClone = validationService.getAndCheckSession(eventId, sourceSessionId);
        Event event = validationService.getAndCheckEvent(eventId);
        validateCloneExternalCreateEventSession(event, sessionData);

        Long targetId = SessionUtils.validateSessionPackSeatTarget(sessionToClone,
                sessionData.getSessionPackSeatsTarget(), venuesRepository);

        CloneSessionData cloneData = SessionConverter.toMsEventCloneSessionBody(sessionData, targetId);

        Long sessionId = eventsRepository.cloneSession(eventId, sourceSessionId, cloneData);

        checkAndProcessExternalCreateEventSessions(event, List.of(sessionId));

        return sessionId;
    }

    public void updateSession(Long eventId, Long sessionId, UpdateSessionRequestDTO updateRequest) {

        UpdateSessionSettingsDTO settings = updateRequest.getSettings();
        checkUpdateSettingsParams(settings);

        Session sessionToUpdate = validationService.getAndCheckSession(eventId, sessionId);
        validateUpdateSession(updateRequest, sessionToUpdate);
        validateUpdateRatesWithDynamicPrice(updateRequest, eventId, sessionId);
        SessionAttendantsConfigDTO sessionAttendantsConfigOld = null;

        if (settings != null) {
            if (settings.getAttendantTickets() != null) {
                sessionAttendantsConfigOld = updateAttendantsConfig(eventId, sessionId, settings.getAttendantTickets());
            }
            if (settings.getUseVenueTemplateCapacityConfig() != null &&
                    CommonUtils.isFalse(sessionToUpdate.getUseVenueConfigCapacity()) &&
                    CommonUtils.isTrue(settings.getUseVenueTemplateCapacityConfig())) {
                List<QuotaCapacity> capacity = venuesRepository.getQuotasCapacity(sessionToUpdate.getVenueConfigId());
                ticketsRepository.updateQuotasCapacity(eventId, sessionId, capacity);
            }
        }

        Session sessionDTO = SessionConverter.toMsEvent(updateRequest);
        sessionDTO.setId(sessionId);

        try {
            eventsRepository.updateSession(eventId, sessionDTO);
        } catch (OneboxRestException e) {
            attendantTicketsUpdateRollback(sessionId, eventId, updateRequest, sessionAttendantsConfigOld);
            throw e;
        }
    }

    private SessionAttendantsConfigDTO updateAttendantsConfig(Long eventId, Long sessionId, SessionAttendantTicketsDTO attendantsTickets) {
        SessionAttendantsConfigDTO sessionAttendantsConfigOld = attendantTicketsRepository.getSessionAttendantsConfig(sessionId, eventId);
        if (AttendantTicketsSessionStatusDTO.EVENT_CONFIG.equals(attendantsTickets.getStatus())) {
            if (sessionAttendantsConfigOld != null) {
                attendantTicketsRepository.deleteSessionAttendantsConfig(sessionId, eventId);
            }
        } else {
            SessionAttendantsConfigDTO sessionAttendantsConfigNew = AttendantsConverter.toMsEvent(sessionId, attendantsTickets);
            if (!Objects.equals(sessionAttendantsConfigOld, sessionAttendantsConfigNew)) {
                SessionUtils.validateAttendantsUpdate(attendantsTickets);
                attendantTicketsRepository.upsertSessionAttendantsConfig(sessionId, eventId, sessionAttendantsConfigNew);
            }
        }
        return sessionAttendantsConfigOld;
    }

    private void attendantTicketsUpdateRollback(Long sessionId, Long eventId, UpdateSessionRequestDTO updateRequest,
                                                SessionAttendantsConfigDTO sessionAttendantsConfig) {
        if (updateRequest.getSettings() != null && updateRequest.getSettings().getAttendantTickets() != null) {
            if (sessionAttendantsConfig == null) {
                attendantTicketsRepository.deleteSessionAttendantsConfig(sessionId, eventId);
            } else {
                attendantTicketsRepository.upsertSessionAttendantsConfig(sessionId, eventId, sessionAttendantsConfig);
            }
        }
    }

    private static void validateUpdateSession(UpdateSessionRequestDTO updateRequest, Session sessionToUpdate) {
        if (updateRequest.getStatus() != null
                && !ALLOWED_UPDATE_STATUS.contains(updateRequest.getStatus())
                && (updateRequest.getStatus().toString().equals(sessionToUpdate.getStatus().toString()))
                && (sessionToUpdate.getDate().getSalesEnd() != null
                && sessionToUpdate.getDate().getSalesEnd().absolute().isBefore(ZonedDateTime.now()))) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_STATUS_NOT_VALID);
        }

        if (sessionToUpdate.getEventType().equals(EventType.AVET)) {
            if (SessionUtils.isUpdatingDate(sessionToUpdate.getDate().getStart(), updateRequest.getStartDate())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_START_DATE_NOT_UPDATABLE,
                        "Start_date cannot be updated on an AVET event.", null);
            }

            if (SessionUtils.isUpdatingDate(sessionToUpdate.getDate().getEnd(), updateRequest.getEndDate())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_END_DATE_NOT_UPDATABLE,
                        "End_date cannot be updated on an AVET event.", null);
            }

            if (updateRequest.getSettings() != null && SessionUtils.isUpdatingAVETRatesValid(sessionToUpdate.getRates(), updateRequest.getSettings().getRates())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_RATES_NOT_UPDATABLE,
                        "Rates cannot be updated on an AVET event.", null);
            }
        }
        if (es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.FINALIZED.equals(sessionToUpdate.getStatus()) &&
                ((SessionStatus.FINALIZED.equals(updateRequest.getStatus())) || updateRequest.getStatus() == null)) {
            validateUpdateFinished(updateRequest, sessionToUpdate);
        }

        if (updateRequest.getSettings() != null && updateRequest.getSettings().getAttendantTickets() != null
                && SessionUtils.isAttendantTicketInvalid(updateRequest.getSettings().getAttendantTickets())) {
            throw OneboxRestException.builder(ApiMgmtSessionErrorCode.ATTENDANT_CONFIG_NOT_ALLOWED).build();
        }
    }

    public void validateUpdateRatesWithDynamicPrice(UpdateSessionRequestDTO updateRequest, Long eventId, Long sessionId) {
        DynamicPriceConfig dynamicPriceConfig = dynamicPriceRepository.getDynamicPriceConfig(eventId, sessionId, false);
        if(dynamicPriceConfig == null){
            return;
        }
        if (areAllDynamicPricesBlocked(dynamicPriceConfig)) {
            return;
        }
        List<DynamicPriceZone> zones = dynamicPriceConfig.getDynamicPriceZoneDTO();

        if (CollectionUtils.isEmpty(zones) ||
                zones.stream().allMatch(zone -> CollectionUtils.isEmpty(zone.getDynamicPricesDTO()))) {
            return;
        }
        List<RateDTO> currentRates = Optional.ofNullable(getSession(eventId, sessionId))
                .map(SessionDTO::getSettings)
                .map(SessionSettingsDTO::getRates)
                .orElseGet(Collections::emptyList);
        List<RateDTO> updatedRates = Optional.ofNullable(updateRequest.getSettings())
                .map(UpdateSessionSettingsDTO::getRates)
                .orElseGet(Collections::emptyList);

        if (CollectionUtils.isNotEmpty(updatedRates) && !areRateListsEquivalent(currentRates, updatedRates)) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_RATES_LOCKED_BY_CONFIGURED_DYNAMIC_PRICES);
        }
    }

    private boolean areAllDynamicPricesBlocked(DynamicPriceConfig config) {
        return Optional.ofNullable(config)
                .map(DynamicPriceConfig::getDynamicPriceZoneDTO)
                .orElseGet(Collections::emptyList)
                .stream()
                .allMatch(dynamicPriceZone -> {
                    List<DynamicPrice> dynamicPrices = dynamicPriceZone.getDynamicPricesDTO();
                    if (dynamicPrices == null || dynamicPrices.isEmpty()) {
                        return true;
                    }
                    Long activeZone = dynamicPriceZone.getActiveZone();
                    return activeZone == null || activeZone >= dynamicPrices.size();
                });
    }

    private boolean areRateListsEquivalent(List<RateDTO> list1, List<RateDTO> list2) {
        if (list1.size() != list2.size()) return false;

        Set<Long> ids1 = list1.stream().map(RateDTO::getId).collect(Collectors.toSet());
        Set<Long> ids2 = list2.stream().map(RateDTO::getId).collect(Collectors.toSet());

        return ids1.equals(ids2);
    }

    public List<UpdateSessionResponseDTO> updateSessions(Long eventId, UpdateSessionsRequestDTO sessionsData, Boolean preview) {
        UpdateSessionSettingsDTO settings = sessionsData.getValue().getSettings();
        checkUpdateSettingsParams(settings);
        Event event = validationService.getAndCheckEvent(eventId);

        if (EventType.AVET.equals(event.getType())) {
            throw OneboxRestException.builder(ApiMgmtSessionErrorCode.AVET_SESSIONS_CANNOT_BE_BULK_UPDATED).build();
        }

        if (sessionsData.getValue().getSettings() != null && sessionsData.getValue().getSettings().getRates() != null) {
            SessionUtils.checkRates(sessionsData.getValue().getSettings().getRates());
        }

        Session sessionDTO = SessionConverter.toMsEvent(sessionsData.getValue());

        SessionBulkUpdateResponse response = eventsRepository.updateSessions(eventId, sessionsData.getIds(), sessionDTO, preview);

        return SessionConverter.fromBulkUpdate(response, sessionsData.getIds());
    }

    public void delete(Long eventId, Long sessionId, String relatedSeats) {
        validationService.getAndCheckSession(eventId, sessionId);

        Session updateStatus = new Session();
        updateStatus.setId(sessionId);
        updateStatus.setStatus(es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.DELETED);
        if (relatedSeats != null) {
            updateStatus.setDeleteData(new DeleteSessionData());
            if (StringUtils.isNumeric(relatedSeats)) {
                updateStatus.getDeleteData().setStatus(SeatDeleteStatus.LOCKED);
                updateStatus.getDeleteData().setBlockingReasonId(Integer.parseInt(relatedSeats));
            } else if (relatedSeats.equals(SeatDeleteStatus.FREE.name())) {
                updateStatus.getDeleteData().setStatus(SeatDeleteStatus.FREE);
            }
        }

        Event event = validationService.getAndCheckEvent(eventId);

        InventoryProviderService inventoryProviderService = getInventoryProvider(event.getEntityId(), event.getInventoryProvider());

        updateStatus.setEntityId(event.getEntityId());

        inventoryProviderService.deleteSession(eventId, updateStatus);
    }

    public InventoryProviderService getInventoryProvider(Long entityId, Provider provider) {
        String providerId = provider == null ? null : provider.getCode();
        return externalInventoryProviderServiceFactory.getIntegrationService(entityId, providerId);
    }

    public List<UpdateSessionResponseDTO> deleteSessions(Long eventId, List<Long> sessionIds, Boolean preview) {
        validationService.getAndCheckEvent(eventId);

        Session sessionData = new Session();
        sessionData.setStatus(es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.DELETED);
        SessionBulkUpdateResponse response = eventsRepository.updateSessions(eventId, sessionIds, sessionData, preview);

        return SessionConverter.fromBulkUpdate(response, sessionIds);
    }

    public SessionAvailabilityDTO getSessionAvailability(Long eventId, Long sessionId) {

        validationService.getAndCheckSession(eventId, sessionId);

        SessionOccupationDTO sessionOccupation = ticketsRepository.getSessionOccupation(sessionId);

        return SessionConverter.fromSessionOccupation(sessionOccupation);
    }

    public List<SessionPriceTypesAvailabilityDTO> getSessionPriceTypesAvailability(Long eventId, Long sessionId) {

        Session session = validationService.getAndCheckSession(eventId, sessionId);
        List<Quota> quotas = venuesRepository.getQuotas(session.getVenueConfigId());

        //Load occupation of normal events sessions or individual activity seats

        boolean isActivitySession = SessionUtils.isActivitySession(session);
        EventType eventType = BooleanUtils.isTrue(isActivitySession) ? EventType.ACTIVITY : session.getEventType();
        List<SessionPriceZoneOccupationResponseDTO> sessionAvailability = ticketsRepository.getSessionOccupationsByPriceZones(
                eventType, session.getId(), quotas.stream().map(Quota::getId).collect(Collectors.toList()));

        List<SessionPriceTypesAvailabilityDTO> response = SessionConverter.fromSessionAvailability(
                sessionAvailability, isActivitySession);

        //Skip default quota availability
        Quota defaultQuota = quotas.stream().filter(Quota::getDefault).findFirst().orElse(null);
        if (isActivitySession && defaultQuota != null) {
            response.removeIf(a -> a.getQuota() != null && a.getQuota().getId().equals(defaultQuota.getId()));
        }

        List<QuotaCapacity> quotasCapacity = ticketsRepository.getQuotasCapacity(eventId, sessionId);
        List<PriceType> pricetypes = venuesRepository.getPriceTypes(session.getVenueConfigId());
        Map<Long, SessionPriceTypesAvailabilityDTO> quotasAggr = new HashMap<>();
        for (SessionPriceTypesAvailabilityDTO availabilityDTO : response) {
            fillPriceType(pricetypes, availabilityDTO);
            fillQuota(quotas, isActivitySession, defaultQuota, quotasCapacity, quotasAggr, availabilityDTO);
        }
        response.addAll(quotasAggr.values());

        //Load occupation of groups if proceed
        if (SessionUtils.isActivitySessionWithGroups(session)) {
            SessionOccupationDTO groupsOccupation = ticketsRepository.getSessionGroupsOccupation(session.getId());
            SessionPriceTypesAvailabilityDTO groupResponse = SessionConverter.fromSessionGroupAvailability(groupsOccupation);
            if (groupResponse != null) {
                SessionGroupConfig sessionGroup = eventsRepository.getSessionGroup(eventId, sessionId);
                Integer maxGroups;
                if(Objects.nonNull(sessionGroup) && Objects.nonNull(sessionGroup.getId())) {
                    maxGroups = sessionGroup.getMaxGroups();
                } else {
                    VenueTemplate venueTemplate = venuesRepository.getVenueTemplate(session.getVenueConfigId());
                    maxGroups = venueTemplate.getMaxGroups();
                }

                groupResponse.getAvailability().setTotal(new LimitlessValueDTO(maxGroups));
                response.add(groupResponse);
            }
        }

        return response;
    }

    private void fillQuota(List<Quota> quotas, boolean isActivitySession, Quota defaultQuota, List<QuotaCapacity> quotasCapacity, Map<Long, SessionPriceTypesAvailabilityDTO> quotasAggr, SessionPriceTypesAvailabilityDTO availabilityDTO) {
        if (availabilityDTO.getQuota() != null) {
            Quota quota = quotas.stream().
                    filter(q -> q.getId().equals(availabilityDTO.getQuota().getId())).findFirst().orElse(null);
            if (quota != null) {
                availabilityDTO.getQuota().setName(quota.getName());
            }
            buildQuotaTotal(quotasAggr, availabilityDTO, quotasCapacity, quota, isActivitySession, false);
        } else {
            buildQuotaTotal(quotasAggr, availabilityDTO, quotasCapacity, isActivitySession ? defaultQuota : null,
                    isActivitySession, true);
        }
    }

    private void fillPriceType(List<PriceType> pricetypes, SessionPriceTypesAvailabilityDTO availabilityDTO) {
        if (availabilityDTO.getPriceType() != null) {
            availabilityDTO.getPriceType().setName(pricetypes.stream().
                    filter(p -> p.getId().equals(availabilityDTO.getPriceType().getId())).
                    map(PriceType::getName).findFirst().orElse(null));
        }
    }

    private void buildQuotaTotal(Map<Long, SessionPriceTypesAvailabilityDTO> quotasAggr,
                                 SessionPriceTypesAvailabilityDTO availabilityDTO,
                                 List<QuotaCapacity> quotasCapacity, Quota quota, boolean isActivitySession, boolean isDefault) {

        Long quotaId = quota == null ? 0 : quota.getId();

        SessionPriceTypesAvailabilityDTO quotaTotal = quotasAggr.get(quotaId);
        if (quotaTotal == null) {
            quotaTotal = new SessionPriceTypesAvailabilityDTO();
            if (!isDefault && quota != null) {
                quotaTotal.setQuota(new IdNameDTO(quota.getId(), quota.getName()));
            }
            SessionAvailabilityDetailDTO defaultAvailability = new SessionAvailabilityDetailDTO();
            if (isActivitySession) {
                quotasCapacity.stream().filter(q -> q.getId().equals(quotaId)).findFirst().ifPresent(capacity ->
                        defaultAvailability.setTotal(new LimitlessValueDTO(capacity.getMaxCapacity())));
            } else {
                defaultAvailability.setTotal(new LimitlessValueDTO(0));
            }
            defaultAvailability.setAvailable(defaultAvailability.getTotal().getValue());
            quotaTotal.setAvailability(defaultAvailability);

        }
        quotaTotal.setTicketType(TicketType.INDIVIDUAL);
        SessionAvailabilityDetailDTO total = quotaTotal.getAvailability();
        SessionAvailabilityDetailDTO item = availabilityDTO.getAvailability();
        total.setPurchase(NumberUtils.zeroIfNull(total.getPurchase()) + NumberUtils.zeroIfNull(item.getPurchase()));
        total.setInvitation(NumberUtils.zeroIfNull(total.getInvitation()) + NumberUtils.zeroIfNull(item.getInvitation()));
        total.setBooking(NumberUtils.zeroIfNull(total.getBooking()) + NumberUtils.zeroIfNull(item.getBooking()));
        total.setIssue(NumberUtils.zeroIfNull(total.getIssue()) + NumberUtils.zeroIfNull(item.getIssue()));
        total.setInProgress(NumberUtils.zeroIfNull(total.getInProgress()) + NumberUtils.zeroIfNull(item.getInProgress()));
        if (isActivitySession) {
            long value = NumberUtils.zeroIfNull(total.getAvailable()) -
                    (NumberUtils.zeroIfNull(item.getPurchase()) +
                            NumberUtils.zeroIfNull(item.getInProgress()) +
                            NumberUtils.zeroIfNull(item.getBooking()) +
                            NumberUtils.zeroIfNull(item.getIssue()));
            total.setAvailable(isDefault ? value : Math.min(value, item.getAvailable()));
        } else {
            total.setAvailable(NumberUtils.zeroIfNull(total.getAvailable()) + NumberUtils.zeroIfNull(item.getAvailable()));
            total.setKill(NumberUtils.zeroIfNull(total.getKill()) + NumberUtils.zeroIfNull(item.getKill()));
            total.setPromoterBlocked(NumberUtils.zeroIfNull(total.getPromoterBlocked()) + NumberUtils.zeroIfNull(item.getPromoterBlocked()));
            total.setSessionPack(NumberUtils.zeroIfNull(total.getSessionPack()) + NumberUtils.zeroIfNull(item.getSessionPack()));
            total.getTotal().setValue(total.getTotal().getValue() + SessionConverter.getTotalValue(item));
        }
        quotasAggr.put(quotaId, quotaTotal);
    }


    public void updateExternalAvailability(Long eventId, Long sessionId) {
        Event event = validationService.getAndCheckEvent(eventId);
        if (!SessionUtils.isAvetEvent(event.getType()) && event.getInventoryProvider() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_UNSUPPORTED_TYPE);
        }

        if (EventStatus.FINISHED.equals(event.getStatus())) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_INVALID_STATUS);
        }

        Session session = validationService.getAndCheckOnlySession(eventId, sessionId);
        if (es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.CANCELLED.equals(session.getStatus())
                || es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.FINALIZED.equals(session.getStatus())) {
            throw new OneboxRestException(ApiMgmtErrorCode.SESSION_INVALID_STATUS);
        }

        if (EventAvetConfigType.SOCKET.equals(event.getAvetConfig())) {
            Match match = avetConfigRepository.getMatch(event.getExternalId(), session.getExternalId());
            if (Objects.isNull(match)) {
                throw new OneboxRestException(ApiMgmtErrorCode.MATCH_NOT_FOUND);
            }

            try {
                avetSocketConnectorRepository.updateMatchAvailability(match.getMatchId(), sessionId);
            } catch (ResponseStatusException e) {
                if (ApiMgmtErrorCode.EXTERNAL_AVAILABILITY_UPDATE_IN_PROGRESS.getHttpStatus().value() == e.getStatusCode().value()) {
                    throw new OneboxRestException(ApiMgmtErrorCode.EXTERNAL_AVAILABILITY_UPDATE_IN_PROGRESS);
                }
                throw e;
            }
        } else {
            dispatcherRepository.updateSessionInventory(event.getEntityId(), eventId, sessionId, session.getIsSmartBooking() != null);
        }

    }

    private void validateCreateSession(CreateSessionRequestDTO sessionData, Event event) {
        if (sessionData.getPackConfig() != null) {
            if (SessionPackType.DISABLED.equals(event.getSessionPackType())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.EVENT_SESSION_PACKS_NOT_ALLOWED);
            }
            if (CommonUtils.isEmpty(sessionData.getPackConfig().getPackSessionIds())) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER, "pack session ids required for pack creation", null);
            }
            if (!CommonUtils.isEmpty(sessionData.getPackConfig().getPackBlockingActions())) {
                for (PackBlockingActionsDTO action : sessionData.getPackConfig().getPackBlockingActions()) {
                    if (action.getId() == null || action.getAction() == null) {
                        throw new OneboxRestException(BAD_REQUEST_PARAMETER, "pack actions must define id and action", null);
                    }
                }
            }
            if (CommonUtils.isTrue(sessionData.getPackConfig().getAllowPartialRefund())) {
                if (!SessionPackType.UNRESTRICTED.equals(event.getSessionPackType())) {
                    throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_CREATE_PACK_ALLOW_PARTIAL_REFUND);
                }
                if (CommonUtils.isTrue(event.getUseTieredPricing())) {
                    throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PARTIAL_REFUND_TIERS_INCOMPATIBLE);
                }
            }
        }
        if (sessionData.getLoyaltyPointsConfig() != null) {
            validateLoyaltyPointsConfig(event.getEntityId());
        }
    }

    private RateDTO createEventRate(Event event, Long matchId) {
        MatchDTO match = MatchConverter.fromMs(avetConfigRepository.getMatch(event.getExternalId(), matchId));
        if (match == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.NOT_FOUND, "No match found for avet_match_id " + matchId, null);
        }

        es.onebox.mgmt.datasources.ms.event.dto.event.Rate eventRate = new es.onebox.mgmt.datasources.ms.event.dto.event.Rate();
        eventRate.setName(match.getName());
        eventRate.setDescription(match.getName());
        eventRate.setDefaultRate(true);
        eventRate.setRestrictive(false);

        Long eventRateId = eventsRepository.createEventRate(event.getId(), eventRate);

        RateDTO rate = new RateDTO();
        rate.setName(match.getName());
        rate.setDefaultRate(true);
        rate.setId(eventRateId);
        return rate;
    }

    private void checkUpdateSettingsParams(UpdateSessionSettingsDTO settings) {
        if (settings == null) {
            return;
        }
        if (settings.getAccessControl() != null) {
            SettingsAccessControlDTO ac = settings.getAccessControl();
            if (ac.getDates() != null && CommonUtils.isTrue(ac.getDates().getOverride()) &&
                    (ac.getDates().getStart() == null || ac.getDates().getEnd() == null)) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER, "dates.start and dates.end must be defined on override", null);
            }
            if (ac.getSpace() != null && CommonUtils.isTrue(ac.getSpace().getOverride()) && ac.getSpace().getId() == null) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER, "space_id must be defined on override", null);
            }
        }
        if (settings.getLimits() != null) {
            SessionSettingsLimitsDTO limits = settings.getLimits();
            if (limits.getMembersLoginsLimit() != null && CommonUtils.isTrue(limits.getMembersLoginsLimit().getEnableMembersLoginsLimit())
                    && limits.getMembersLoginsLimit().getMembersLoginsLimit() == null) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER, "max members_logins limit must be defined on enable", null);
            }
        }
        if (settings.getSubscriptionList() != null && SubscriptionListType.EVENT.equals(settings.getSubscriptionList().getScope())
                && settings.getSubscriptionList().getId() != null) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                    "subscription_list.id should be informed only for subscription_list.scope SESSION", null);
        }

        if (settings.getSessionVirtualQueue() != null && !SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR, Roles.ROLE_OPR_ANS)) {
            throw ExceptionBuilder.build(ApiMgmtSessionErrorCode.SESSION_UPDATE_NOT_ALLOWED, "settings.virtual_queue");

        }
    }

    private void fillSession(BaseSessionDTO session, Session baseSession) {
        if (baseSession.getCountryId() != null) {
            MasterdataValue country = masterdataService.getCountry(baseSession.getCountryId());
            session.getVenueTemplate().getVenue().setCountry(country.getCode());
        }
    }

    private void updateAvetVenueTemplatePrices(Event event, Long matchId, Long venueTemplateId, Long rateId) {
        List<MatchPriceDTO> matchPrices = avetConfigRepository.getMatchPrices(event.getExternalId(), matchId);
        List<PriceType> venueTemplatePriceTypes = venuesRepository.getPriceTypes(venueTemplateId);
        List<VenueTemplatePrice> venueTemplatePrices = new ArrayList<>();

        for (MatchPriceDTO matchPrice : matchPrices) {
            Optional<PriceType> venueTemplatePriceType = venueTemplatePriceTypes.stream()
                    .filter(priceType -> priceType.getAdditionalConfig().getAvetPriceId().equals(matchPrice.getPriceId().longValue()))
                    .findFirst();

            if (venueTemplatePriceType.isPresent()) {
                VenueTemplatePrice venueTemplatePrice = new VenueTemplatePrice();
                venueTemplatePrice.setPriceTypeId(venueTemplatePriceType.get().getId());
                venueTemplatePrice.setRateId(rateId.intValue());
                venueTemplatePrice.setPrice(matchPrice.getPrice().doubleValue());
                venueTemplatePrice.setAdditionalConfig(new AdditionalConfigDTO(matchPrice.getMatchPriceId().longValue()));

                venueTemplatePrices.add(venueTemplatePrice);
            }
        }

        eventsRepository.updateVenueTemplatePrices(event.getId(), venueTemplateId, venueTemplatePrices);
    }

    public List<TierQuotaAvailabilityDTO> getTiersAvailability(Long eventId, Long sessionId) {
        Event event = validationService.getAndCheckEvent(eventId);
        if (!event.getUseTieredPricing()) {
            throw new OneboxRestException(ApiMgmtTierErrorCode.NON_TIERED_EVENT);
        }
        Long venueTemplateId = validationService.getAndCheckOnlySession(eventId, sessionId).getVenueConfigId();
        List<Quota> quotas = venuesRepository.getQuotas(venueTemplateId);
        Tiers tiers = eventsRepository.getEventTiers(eventId, venueTemplateId, false, null, null);
        List<PriceTypeCapacity> priceTypeCapacities = venuesRepository.getPriceTypeCapacity(venueTemplateId);
        List<OrderProductDTO> products = getAllSessionProducts(sessionId);

        Map<Long, Long> priceTypesToCapacity = priceTypeCapacities.stream()
                .collect(Collectors.toMap(PriceTypeCapacity::getId, PriceTypeCapacity::getCapacity));
        Map<Long, List<OrderProductDTO>> productsByTier = products.stream()
                .collect(Collectors.groupingBy(OrderProductDTO::getTierId));

        List<TierQuotaAvailabilityDTO> result = new ArrayList<>();

        for (Tier t : tiers.getData()) {
            TierExtended tier = eventsRepository.getEventTier(eventId, t.getId());
            Long capacity = priceTypesToCapacity.get(tier.getPriceTypeId());

            Map<Long, TierQuotaAvailabilityDTO> tierAvailabilities = quotas.stream()
                    .map(quota -> SessionConverter.toSaleGroupTierAvailability(tier, quota.getId(), quota.getName(), null, capacity))
                    .collect(Collectors.toMap(sgto -> sgto.getQuota().getId(), Function.identity()));

            Optional.ofNullable(tier.getSalesGroupLimit()).orElse(new ArrayList<>())
                    .forEach(tsgl -> tierAvailabilities.get(tsgl.getId()).getQuota().setLimit(tsgl.getLimit()));

            for (Quota quota : quotas) {
                tierAvailabilities.computeIfAbsent(quota.getId(),
                        k -> SessionConverter.toSaleGroupTierAvailability(tier, k, quota.getName(), null, capacity));
            }
            productsByTier.computeIfAbsent(t.getId(), k -> new ArrayList<>());

            for (OrderProductDTO p : productsByTier.get(t.getId())) {
                TierQuotaAvailabilityDTO availability = tierAvailabilities.get(p.getTicketData().getQuotaId().longValue());
                switch (p.getAdditionalData().getOrderType()) {
                    case PURCHASE, SEC_MKT_PURCHASE -> availability.addSold();
                    case REFUND -> availability.addRefunded();
                }
            }
            result.addAll(tierAvailabilities.values());
        }
        return result;
    }

    public List<QuotaCapacityDTO> getQuotasCapacity(Long eventId, Long sessionId) {
        var session = validationService.getAndCheckSession(eventId, sessionId);
        if (!SessionUtils.isActivitySession(session)) {
            throw new OneboxRestException(EVENT_TYPE_NOT_ALLOWED);
        }
        var venueConfigQuotas = venuesRepository.getQuotas(session.getVenueConfigId()).stream()
                .collect(Collectors.toMap(Quota::getId, Function.identity()));
        var venueConfigPriceTypes = venuesRepository.getPriceTypes(session.getVenueConfigId()).stream()
                .collect(Collectors.toMap(PriceType::getId, Function.identity()));

        List<QuotaCapacity> capacity = ticketsRepository.getQuotasCapacity(eventId, sessionId).stream().filter(q -> venueConfigQuotas.containsKey(q.getId()))
                .collect(Collectors.toList());

        return VenueTemplateQuotaConverter.from(capacity, venueConfigQuotas, venueConfigPriceTypes);
    }

    public void updateQuotasCapacity(Long eventId, Long sessionId, QuotaCapacityListDTO requestDTO) {
        Session session = validationService.getAndCheckSession(eventId, sessionId);
        if (!SessionUtils.isActivitySession(session)) {
            throw new OneboxRestException(EVENT_TYPE_NOT_ALLOWED);
        }
        if (CommonUtils.isTrue(session.getUseVenueConfigCapacity())) {
            throw new OneboxRestException(FORBIDDEN, "Session has enabled venue-template override", null);
        }

        ticketsRepository.updateQuotasCapacity(eventId, sessionId, VenueTemplateQuotaConverter.toMsDTO(requestDTO));
    }

    public List<AttributeDTO> getSessionAttributes(Long eventId, Long sessionId, boolean fullLoad) {
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckSession(eventId, sessionId);

        List<Attribute> attributes = eventsRepository.getSessionAttributes(eventId, sessionId);

        AttributeSearchFilter filter = new AttributeSearchFilter();
        filter.setScope(AttributeScope.SESSION);
        List<es.onebox.mgmt.datasources.ms.entity.dto.Attribute> entityAttributes = entitiesRepository.getAttributes(event.getEntityId(), filter);

        Map<Long, String> attrAssignedValues = attributes.stream()
                .filter(ea -> StringUtils.isNotEmpty(ea.getValue()))
                .collect(Collectors.toMap(Attribute::getId, Attribute::getValue));

        Map<Long, List<Long>> validAttrValueIds = attributes.stream()
                .filter(eventAttribute -> eventAttribute.getSelected() != null)
                .collect(Collectors.toMap(Attribute::getId, Attribute::getSelected));

        Map<Long, String> languages = masterdataService.getLanguagesByIds();
        List<AttributeDTO> result = new ArrayList<>();

        for (es.onebox.mgmt.datasources.ms.entity.dto.Attribute attribute : entityAttributes) {
            if (!fullLoad) {
                attribute.getTexts().getValues().removeIf(av -> validAttrValueIds.get(attribute.getId()) != null && !validAttrValueIds.get(attribute.getId()).contains(av.getId()));
            }
            AttributeDTO attributeDTO = AttributeConverter.fromMsEntity(attribute, languages, attrAssignedValues.get(attribute.getId()));
            if (CollectionUtils.isNotEmpty(validAttrValueIds.get(attribute.getId()))) {
                attributeDTO.setSelectedValuesIds(validAttrValueIds.get(attribute.getId()));
            }
            if (fullLoad || attrAssignedValues.get(attribute.getId()) != null || CollectionUtils.isNotEmpty(validAttrValueIds.get(attribute.getId()))) {
                result.add(attributeDTO);
            }
        }

        return result;
    }

    public void putSessionAttributeValue(Long eventId, Long sessionId, AttributeRequestValuesDTO attributeRequestValuesDTO) {
        validationService.getAndCheckSession(eventId, sessionId);
        eventsRepository.putSessionAttributes(eventId, sessionId, attributeRequestValuesDTO);
    }

    public SessionGroupDTO getSessionGroup(Long eventId, Long sessionId) {
        Session session = validationService.getAndCheckSession(eventId, sessionId);
        SessionGroupConfig sessionGroup = eventsRepository.getSessionGroup(eventId, sessionId);
        VenueTemplate venueTemplate = venuesRepository.getVenueTemplate(session.getVenueConfigId());
        return SessionConverter.fromMsGroupConfig(sessionGroup, venueTemplate);
    }

    public void updateSessionGroup(Long eventId, Long sessionId, SessionGroupDTO request) {
        validationService.getAndCheckSession(eventId, sessionId);

        if (request.getUseVenueTemplateGroupConfig() != null &&
                CommonUtils.isTrue(request.getUseVenueTemplateGroupConfig())) {
            eventsRepository.deleteSessionGroupConfig(eventId, sessionId);
        } else {
            SessionGroupConfig config = SessionConverter.toMsGroupConfig(request);
            eventsRepository.updateSessionGroupConfig(eventId, sessionId, config);
        }
    }

    private List<OrderProductDTO> getAllSessionProducts(Long sessionId) {
        ProductSearchRequest filter = new ProductSearchRequest();
        filter.setSessionIds(Collections.singletonList(sessionId));
        filter.setOffset(0L);
        filter.setLimit(PRODUCTS_LIMIT);

        ProductSearchResponse products = orderProductsRepository.searchProducts(filter);

        List<OrderProductDTO> result = new ArrayList<>(products.getData());
        Long total = products.getMetadata().getTotal();

        while (total > (PRODUCTS_LIMIT + filter.getOffset())) {
            filter.setOffset(filter.getOffset() + PRODUCTS_LIMIT);
            ProductSearchResponse resp = orderProductsRepository.searchProducts(filter);
            result.addAll(resp.getData());
        }

        return result;
    }

    private static void validateUpdateFinished(UpdateSessionRequestDTO updateRequest, Session sessionToUpdate) {

        if (updateRequest.getName() == null) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_STATUS_NOT_VALID,
                    ONLY_THE_NAME_CAN_BE_UPDATED_FOR_FINALIZED_SESSIONS, null);
        }

        if (!Objects.equals(sessionToUpdate.getReference(), updateRequest.getReference())) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_STATUS_NOT_VALID,
                    ONLY_THE_NAME_CAN_BE_UPDATED_FOR_FINALIZED_SESSIONS, null);
        }

        UpdateSessionSettingsDTO settings = updateRequest.getSettings();
        if (settings != null) {
            if (settings.getAccessControl() != null || settings.getLiveStreaming() != null ||
                    settings.getEnableCaptcha() != null || settings.getEnableOrphanSeats() != null ||
                    settings.getUseVenueTemplateCapacityConfig() != null || settings.getUseVenueTemplateAccess() != null ||
                    settings.getAttendantTickets() != null || settings.getSessionPackSettings() != null ||
                    settings.getSessionCountryFilter() != null || settings.getLimits() != null ||
                    settings.getSessionChannelSettings() != null || settings.getSessionVirtualQueue() != null ||
                    settings.getSubscriptionList() != null) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_STATUS_NOT_VALID,
                        ONLY_THE_NAME_CAN_BE_UPDATED_FOR_FINALIZED_SESSIONS, null);
            }

            if (settings.getRates() != null && !settings.getRates().equals(SessionConverter.toDTO(sessionToUpdate.getRates()))) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_STATUS_NOT_VALID,
                        ONLY_THE_NAME_CAN_BE_UPDATED_FOR_FINALIZED_SESSIONS, null);
            }

            if (settings.getActivitySaleType() != null && !settings.getActivitySaleType().getId().equals(sessionToUpdate.getSaleType())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_STATUS_NOT_VALID,
                        ONLY_THE_NAME_CAN_BE_UPDATED_FOR_FINALIZED_SESSIONS, null);
            }

            if (settings.getTaxes() != null) {
                if (!settings.getTaxes().getTicket().getId().equals(sessionToUpdate.getTicketTax().getId()) ||
                        !settings.getTaxes().getCharges().getId().equals(sessionToUpdate.getChargesTax().getId())) {
                    throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_STATUS_NOT_VALID,
                            ONLY_THE_NAME_CAN_BE_UPDATED_FOR_FINALIZED_SESSIONS, null);
                }
            }


            if (settings.getRelease() != null) {
                if (!settings.getRelease().getEnable().equals(sessionToUpdate.getEnableChannels()) ||
                        !settings.getRelease().getDate().absolute().isEqual(sessionToUpdate.getDate().getChannelPublication().absolute())) {
                    throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_STATUS_NOT_VALID,
                            ONLY_THE_NAME_CAN_BE_UPDATED_FOR_FINALIZED_SESSIONS, null);
                }
            }


            if (settings.getBooking() != null) {
                if (!settings.getBooking().getEnable().equals(sessionToUpdate.getEnableBookings()) ||
                        (settings.getBooking().getStartDate() != null &&
                                !settings.getBooking().getStartDate().absolute().isEqual(sessionToUpdate.getDate().getBookingsStart().absolute())) ||
                        (settings.getBooking().getEndDate() != null &&
                                !settings.getBooking().getEndDate().absolute().isEqual(sessionToUpdate.getDate().getBookingsEnd().absolute()))) {
                    throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_STATUS_NOT_VALID,
                            ONLY_THE_NAME_CAN_BE_UPDATED_FOR_FINALIZED_SESSIONS, null);
                }
            }

            if (settings.getSale() != null) {
                if (!settings.getSale().getEnable().equals(sessionToUpdate.getEnableSales()) ||
                        !settings.getSale().getStartDate().absolute().isEqual(sessionToUpdate.getDate().getSalesStart().absolute()) ||
                        !settings.getSale().getEndDate().absolute().isEqual(sessionToUpdate.getDate().getSalesEnd().absolute())) {
                    throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_STATUS_NOT_VALID,
                            ONLY_THE_NAME_CAN_BE_UPDATED_FOR_FINALIZED_SESSIONS, null);
                }
            }

            if (updateRequest.getStartDate() != null && !Objects.equals(updateRequest.getStartDate().toLocalDateTime(), sessionToUpdate.getDate().getStart().toLocalDateTime())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_STATUS_NOT_VALID,
                        ONLY_THE_NAME_CAN_BE_UPDATED_FOR_FINALIZED_SESSIONS, null);
            }

            if (updateRequest.getEndDate() != null && !Objects.equals(updateRequest.getEndDate().toLocalDateTime(), sessionToUpdate.getDate().getEnd().toLocalDateTime())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_STATUS_NOT_VALID,
                        ONLY_THE_NAME_CAN_BE_UPDATED_FOR_FINALIZED_SESSIONS, null);
            }
        }
    }

    private void validateLoyaltyPointsConfig(Long entityId) {
        Entity entity = entitiesRepository.getEntity(entityId);

        if (BooleanUtils.isNotTrue(entity.getAllowLoyaltyPoints())) {
            throw new OneboxRestException(ApiMgmtErrorCode.LOYALTY_POINTS_NOT_ALLOWED);
        }
    }


    private void checkAndProcessExternalCreateEventSessions(Event event, List<Long> sessionIds) {
        List<AccessControlSystem> accessControlSystems = getAccessControlSystems(event);
        if (CollectionUtils.isEmpty(accessControlSystems)) {
            return;
        }

        accessControlSystems.stream().distinct().forEach(accessControlSystem -> {
            ExternalAccessControlHandler externalAccessControlHandler;
            externalAccessControlHandler = externalAccessControlHandlerStrategyProvider.provide(accessControlSystem.name());

            if (externalAccessControlHandler == null) {
                return;
            }
            externalAccessControlHandler.createSessions(event, sessionIds);
        });

    }

    private void validateExternalCreateEventSessions(Event event, CreateSessionRequestDTO request) {
        List<AccessControlSystem> accessControlSystems = getAccessControlSystems(event);
        if (CollectionUtils.isEmpty(accessControlSystems)) {
            return;
        }

        accessControlSystems.stream().distinct().forEach(accessControlSystem -> {
            ExternalAccessControlHandler externalAccessControlHandler;
            externalAccessControlHandler = externalAccessControlHandlerStrategyProvider.provide(accessControlSystem.name());

            if (externalAccessControlHandler == null) {
                return;
            }
            externalAccessControlHandler.validateCreateSession(event, request);
        });

    }

    private void validateCloneExternalCreateEventSession(Event event, CloneSessionRequestDTO request) {
        List<AccessControlSystem> accessControlSystems = getAccessControlSystems(event);
        if (CollectionUtils.isEmpty(accessControlSystems)) {
            return;
        }

        accessControlSystems.stream().distinct().forEach(accessControlSystem -> {
            ExternalAccessControlHandler externalAccessControlHandler;
            externalAccessControlHandler = externalAccessControlHandlerStrategyProvider.provide(accessControlSystem.name());

            if (externalAccessControlHandler == null) {
                return;
            }
            externalAccessControlHandler.validateCloneSession(event, request);
        });

    }


    private List<AccessControlSystem> getAccessControlSystems(Event event) {
        if (event == null || CollectionUtils.isEmpty(event.getVenues())) {
            return Collections.emptyList();
        }

        List<AccessControlSystem> accessControlSystems = new ArrayList<>();

        event.getVenues().forEach(venue -> {
            if (CollectionUtils.isNotEmpty(venue.getAccessControlSystems())) {
                venue.getAccessControlSystems().stream()
                        .filter(system -> !accessControlSystems.contains(system))
                        .forEach(accessControlSystems::add);
            }
        });
        return accessControlSystems;
    }

}
