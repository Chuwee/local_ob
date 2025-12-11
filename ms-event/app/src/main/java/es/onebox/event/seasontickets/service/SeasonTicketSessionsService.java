package es.onebox.event.seasontickets.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.FilterAggregate;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.datasources.ms.ticket.dto.LinkSessionCapacityResponse;
import es.onebox.event.datasources.ms.ticket.dto.SessionCompatibilityValidationResponse;
import es.onebox.event.datasources.ms.ticket.dto.SessionUnlinkResponse;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearchFilter;
import es.onebox.event.datasources.ms.ticket.repository.SeasonTicketRepository;
import es.onebox.event.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.events.service.EventService;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.loyaltypoints.seasontickets.service.SeasonTicketLoyaltyPointsService;
import es.onebox.event.seasontickets.converter.SeasonTicketSessionsConverter;
import es.onebox.event.seasontickets.converter.SessionAssignableConverter;
import es.onebox.event.seasontickets.dao.SessionElasticDao;
import es.onebox.event.seasontickets.dto.AssignSessionReason;
import es.onebox.event.seasontickets.dto.AssignSessionRequestDTO;
import es.onebox.event.seasontickets.dto.AssignSessionResponseDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketInternalGenerationStatus;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionValidationReason;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionValidationResponse;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionsDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionsEventList;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionsSummary;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusResponseDTO;
import es.onebox.event.seasontickets.dto.SessionAssignationStatusDTO;
import es.onebox.event.seasontickets.dto.SessionResultDTO;
import es.onebox.event.seasontickets.dto.UnAssignSessionReason;
import es.onebox.event.seasontickets.dto.UnAssignSessionResponseDTO;
import es.onebox.event.seasontickets.elasticsearch.PaginationUtils;
import es.onebox.event.seasontickets.request.SeasonTicketSessionsEventsFilter;
import es.onebox.event.seasontickets.request.SeasonTicketSessionsSearchFilter;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.SaleType;
import es.onebox.event.secondarymarket.service.EventSecondaryMarketConfigService;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dto.SessionConfigDTO;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.enums.SessionType;
import es.onebox.event.sessions.service.SessionService;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class SeasonTicketSessionsService {

    private final SessionElasticDao sessionElasticDao;
    private final SeasonTicketService seasonTicketService;
    private final SessionService sessionService;
    private final EventService eventService;
    private final SeasonSessionDao seasonSessionDao;
    private final RefreshDataService refreshDataService;
    private final SeasonTicketRepository seasonTicketRepository;
    private final SeasonTicketLoyaltyPointsService seasonTicketLoyaltyPointsService;
    private final EventSecondaryMarketConfigService seasonTicketSecondaryMarketService;
    private final TicketsRepository ticketsRepository;
    private final OrdersRepository ordersRepository;

    @Autowired
    public SeasonTicketSessionsService(@Qualifier("seasonTicketSessionElasticDao") SessionElasticDao sessionElasticDao,
                                       SeasonTicketService seasonTicketService, SessionService sessionService,
                                       EventService eventService, SeasonSessionDao seasonSessionDao,
                                       RefreshDataService refreshDataService, SeasonTicketRepository seasonTicketRepository,
                                       SeasonTicketLoyaltyPointsService seasonTicketLoyaltyPointsService,
                                       EventSecondaryMarketConfigService seasonTicketSecondaryMarketService, TicketsRepository ticketsRepository, OrdersRepository ordersRepository) {
        this.sessionElasticDao = sessionElasticDao;
        this.seasonTicketService = seasonTicketService;
        this.sessionService = sessionService;
        this.eventService = eventService;
        this.seasonSessionDao = seasonSessionDao;
        this.refreshDataService = refreshDataService;
        this.seasonTicketRepository = seasonTicketRepository;
        this.seasonTicketLoyaltyPointsService = seasonTicketLoyaltyPointsService;
        this.seasonTicketSecondaryMarketService = seasonTicketSecondaryMarketService;
        this.ticketsRepository = ticketsRepository;
        this.ordersRepository = ordersRepository;
    }

    public SeasonTicketSessionsDTO searchCandidateSessions(SeasonTicketSessionsSearchFilter filter,
                                                           Long seasonTicketId) {
        // Check if season ticket exists
        SeasonTicketDTO seasonTicketDTO = seasonTicketService.getSeasonTicket(seasonTicketId);

        // Check status of season ticket
        SeasonTicketInternalGenerationStatus status = seasonTicketService.getGenerationStatus(seasonTicketId);
        if (Objects.isNull(status) || !SeasonTicketInternalGenerationStatus.READY.equals(status)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_IN_CREATION);
        }

        SearchResponse<SessionData> response = sessionElasticDao.getSessions(filter, seasonTicketDTO);
        List<SessionResultDTO> seasonTicketCandidateSessions = sessionElasticDao.getSeasonTicketCandidateSessionsDTO(response);

        List<SeasonTicketSessionDTO> data = SeasonTicketSessionsConverter
                .convert(seasonTicketCandidateSessions, seasonTicketDTO.getVenueTimeZone(), seasonTicketDTO.getSessionId());

        SeasonTicketSessionsDTO seasonTicketSessionsDTO = new SeasonTicketSessionsDTO();
        PaginationUtils.fillPaginationResult(seasonTicketSessionsDTO, filter, response, data);

        getAndFillSummary(seasonTicketSessionsDTO, seasonTicketDTO);

        return seasonTicketSessionsDTO;
    }

    private void getAndFillSummary(SeasonTicketSessionsDTO seasonTicketSessionsDTO, SeasonTicketDTO seasonTicketDTO) {
        SeasonTicketSessionsSummary summary = new SeasonTicketSessionsSummary();

        SearchResponse<SessionData> summaryResponse = sessionElasticDao.getSeasonTicketSessionsSummary(seasonTicketDTO);
        if (summaryResponse != null && MapUtils.isNotEmpty(summaryResponse.aggregations())) {

            // Total sessions
            Integer totalSessions = Math.toIntExact(summaryResponse.hits().total().value());
            summary.setTotalSessions(totalSessions);

            // Listed events
            summary.setListedEvents(getListedEvents(summaryResponse));

            // Sessions on sale
            summary.setSessionsOnSale(getSessionsOnSale(summaryResponse));

            // Assigned sessions
            summary.setAssignedSessions(getAssignedSessions(summaryResponse));
        }

        seasonTicketSessionsDTO.setSummary(summary);
    }

    public SeasonTicketSessionsEventList getEventsList(Long seasonTicketId, SeasonTicketSessionsEventsFilter filter) {
        SeasonTicketDTO seasonTicketDTO = seasonTicketService.getSeasonTicket(seasonTicketId);

        SeasonTicketInternalGenerationStatus status = seasonTicketService.getGenerationStatus(seasonTicketId);
        if (Objects.isNull(status) || !SeasonTicketInternalGenerationStatus.READY.equals(status)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_IN_CREATION);
        }

        SearchResponse<SessionData> searchResponse = sessionElasticDao.getPossibleEventsQuery(seasonTicketDTO, filter);
        List<IdNameDTO> data = sessionElasticDao.getSeasonTicketSessionEventsDTO(searchResponse);
        SeasonTicketSessionsEventList response = new SeasonTicketSessionsEventList();
        PaginationUtils.fillPaginationResult(response, filter, searchResponse, data);

        return response;
    }

    private Integer getListedEvents(SearchResponse<SessionData> summaryResponse) {
        if (summaryResponse.aggregations().containsKey(SessionElasticDao.DIFFERENT_EVENTS_FILTER)) {
            FilterAggregate filter = summaryResponse.aggregations().get(SessionElasticDao.DIFFERENT_EVENTS_FILTER).filter();
            Aggregate aggregate = filter.aggregations().get(SessionElasticDao.DIFFERENT_EVENTS_AGGREGATION);
            if (aggregate != null) {
                return Math.toIntExact(aggregate.cardinality().value());
            }
        }
        return null;
    }

    private Integer getSessionsOnSale(SearchResponse<SessionData> summaryResponse) {
        if (summaryResponse.aggregations().containsKey(SessionElasticDao.SESSIONS_ON_SALE_FILTER)) {
            FilterAggregate filter = summaryResponse.aggregations().get(SessionElasticDao.SESSIONS_ON_SALE_FILTER).filter();
            Aggregate aggregate = filter.aggregations().get(SessionElasticDao.SESSIONS_ON_SALE_AGGREGATION);
            if (aggregate != null) {
                return NumberUtils.doubleToInt(aggregate.valueCount().value());
            }
        }
        return null;
    }

    private Integer getAssignedSessions(SearchResponse<SessionData> summaryResponse) {
        if (summaryResponse.aggregations().containsKey(SessionElasticDao.ASSIGNED_SESSIONS_FILTER)) {
            FilterAggregate filter = summaryResponse.aggregations()
                    .get(SessionElasticDao.ASSIGNED_SESSIONS_FILTER).filter();
            Aggregate aggregate = filter.aggregations().get(SessionElasticDao.ASSIGNED_SESSIONS_AGGREGATION);
            if (aggregate != null) {
                return NumberUtils.doubleToInt(aggregate.valueCount().value());
            }
        }
        return null;
    }

    @MySQLRead
    public SeasonTicketSessionValidationResponse verifySession(Long seasonTicketId, Long sessionId, Boolean includeSeats) {
        SeasonTicketStatusResponseDTO seasonTicketStatus = seasonTicketService.getStatus(seasonTicketId);
        SeasonTicketSessionValidationResponse response = new SeasonTicketSessionValidationResponse();

        if (seasonTicketStatus.getGenerationStatus() != SeasonTicketInternalGenerationStatus.READY ||
                seasonTicketStatus.getStatus() != SeasonTicketStatusDTO.SET_UP) {
            response.setResult(false);
            response.setReason(SeasonTicketSessionValidationReason.SEASON_TICKET_GENERATION_STATUS_NOT_VALID);
            return response;
        }

        SeasonTicketDTO seasonTicketDTO = seasonTicketService.getSeasonTicket(seasonTicketId);

        SessionDTO sessionDTO = sessionService.getSession(sessionId);
        if (sessionDTO == null || SessionStatus.DELETED.equals(sessionDTO.getStatus())) {
            throw OneboxRestException.builder(MsEventSessionErrorCode.SESSION_NOT_FOUND).build();
        }
        if (!SessionType.SESSION.equals(sessionDTO.getSessionType())) {
            response.setResult(false);
            response.setReason(SeasonTicketSessionValidationReason.SESSION_TYPE_INVALID);
            return response;
        }

        if (CommonUtils.isFalse(sessionDTO.getVenueConfigGraphic())) {
            response.setResult(false);
            response.setReason(SeasonTicketSessionValidationReason.SESSION_VENUE_NOT_GRAPHIC);
            return response;
        }

        if (!seasonTicketDTO.getVenues().get(0).getId().equals(sessionDTO.getVenueId())) {
            response.setResult(false);
            response.setReason(SeasonTicketSessionValidationReason.DIFFERENT_VENUE_IDS);
            return response;
        }

        EventDTO eventDTO = eventService.getEvent(sessionDTO.getEventId());

        if (!seasonTicketDTO.getEntityId().equals(eventDTO.getEntityId()) || !seasonTicketDTO.getEntityId().equals(sessionDTO.getEntityId())) {
            response.setResult(false);
            response.setReason(SeasonTicketSessionValidationReason.DIFFERENT_ENTITY_IDS);
            return response;
        }

        if (SessionPackType.RESTRICTED.equals(eventDTO.getSessionPackType())) {
            response.setResult(false);
            response.setReason(SeasonTicketSessionValidationReason.SEASON_TICKET_SESSION_RESTRICTED);
            return response;
        }

        if (SessionAssignableConverter.isAssignable(eventDTO.getStatus(), sessionDTO.getStatus()).equals(false)) {
            response.setResult(false);
            response.setReason(SeasonTicketSessionValidationReason.EVENT_OR_SESSION_STATUS_NOT_VALID);
            return response;
        }

        List<Long> sessionsBySessionPack = seasonSessionDao
                .findSessionsBySessionPackId(seasonTicketDTO.getSessionId().longValue());
        if (sessionsBySessionPack.contains(sessionId)) {
            response.setResult(false);
            response.setReason(SeasonTicketSessionValidationReason.SEASON_TICKET_SESSION_ALREADY_ASSIGNED);
            return response;
        }

        SessionConfigDTO sessionConfig = sessionService.getSessionConfig(seasonTicketDTO.getSessionId().longValue());
        if (sessionConfig != null && BooleanUtils.isTrue(sessionConfig.getSeasonTicketMultiticket()) &&
                ordersRepository.countByEventAndChannel(seasonTicketId, null) > 0) {
            response.setResult(false);
            response.setReason(SeasonTicketSessionValidationReason.SESSION_TYPE_INVALID);
            return response;
        }

        response.setResult(true);

        if (Boolean.TRUE.equals(includeSeats)) {
            response = verifySessionSeats(seasonTicketDTO, sessionId);
        }

        return response;
    }

    @MySQLWrite
    public AssignSessionResponseDTO assignSession(Long seasonTicketId, AssignSessionRequestDTO assignSessionRequestDTO) {

        Long sessionId = assignSessionRequestDTO.getSessionId();

        AssignSessionResponseDTO sessionAssignation = new AssignSessionResponseDTO();

        SeasonTicketSessionValidationResponse validationResponse = verifySession(seasonTicketId, assignSessionRequestDTO.getSessionId(), Boolean.FALSE);
        if (validationResponse.getResult() != null && Boolean.TRUE.equals(validationResponse.getResult())) {

            SeasonTicketDTO seasonTicket = seasonTicketService.getSeasonTicket(seasonTicketId);
            assignSessionToSeasonTicketSession(seasonTicket.getSessionId().longValue(), assignSessionRequestDTO);


            // Link capacity seats from both sessions
            AssignSessionResponseDTO assignSessionSeatsResponse = assignSessionSeats(seasonTicket, assignSessionRequestDTO.getSessionId(),
                    assignSessionRequestDTO.getUpdateBarcodes());

            if (Boolean.FALSE.equals(assignSessionSeatsResponse.getResult())) {
                // Rollback in case that link capacity seats is in error
                rollbackAssignSessionFromSeasonTicket(seasonTicket.getSessionId().longValue(), sessionId);
                sessionAssignation.setResult(Boolean.FALSE);
                sessionAssignation.setReason(AssignSessionReason.valueOf(assignSessionSeatsResponse.getReason().name()));
            } else {
                sessionAssignation.setResult(Boolean.TRUE);
                seasonTicketLoyaltyPointsService.onSessionLink(seasonTicketId, sessionId);
            }

        } else {
            sessionAssignation.setResult(Boolean.FALSE);
            if (Objects.nonNull(validationResponse.getReason())) {
                sessionAssignation.setReason(AssignSessionReason.valueOf(validationResponse.getReason().name()));
            } else {
                sessionAssignation.setReason(AssignSessionReason.ASSIGN_SESSION_GENERIC_ERROR);
            }
        }


        return sessionAssignation;
    }

    @MySQLWrite
    public UnAssignSessionResponseDTO unAssignSession(Long seasonTicketId, Long sessionId, Boolean updateBarcodes) {
        UnAssignSessionResponseDTO response = new UnAssignSessionResponseDTO();

        SeasonTicketDTO seasonTicket = seasonTicketService.getSeasonTicket(seasonTicketId);

        if (!SeasonTicketStatusDTO.SET_UP.equals(seasonTicket.getStatus())) {
            response.setResult(Boolean.FALSE);
            response.setReason(UnAssignSessionReason.INVALID_SEASON_TICKET_STATUS);
            return response;
        }

        if (!seasonTicketHasSession(seasonTicketId, sessionId)) {
            response.setResult(Boolean.FALSE);
            response.setReason(UnAssignSessionReason.SESSION_ID_NOT_VALID);
            return response;
        }

        SessionConfigDTO sessionConfig = sessionService.getSessionConfig(seasonTicket.getSessionId().longValue());
        if (sessionConfig != null && BooleanUtils.isTrue(sessionConfig.getSeasonTicketMultiticket()) &&
                ordersRepository.countByEventAndChannel(seasonTicketId, null) > 0) {
            response.setResult(Boolean.FALSE);
            response.setReason(UnAssignSessionReason.UNLINK_IMPOSSIBLE);
            return response;
        }

        EventSecondaryMarketConfigDTO stConfig = seasonTicketSecondaryMarketService.getEventSecondaryMarketConfigSafely(seasonTicketId);
        if (stConfig != null && SaleType.PARTIAL.equals(stConfig.getSaleType())) {
            SecondaryMarketSearchFilter filter = new SecondaryMarketSearchFilter();
            filter.setSessionId(sessionId);
            filter.setSeasonTicketIds(Collections.singletonList(seasonTicketId));
            if (ticketsRepository.getSecondaryMarketLocationsCount(filter) > 0) {
                response.setResult(Boolean.FALSE);
                response.setReason(UnAssignSessionReason.SEC_MKT_LOCATIONS_FOUND);
                return response;
            }
        }


        unassignSessionToSeasonTicketSession(seasonTicket.getSessionId().longValue(), sessionId);

        SessionUnlinkResponse unLinkResult = seasonTicketRepository.unLinkSessionCapacity(seasonTicket.getSessionId().longValue(),
                sessionId, updateBarcodes);
        if (Boolean.TRUE.equals(unLinkResult.getResult())) {
            response.setResult(Boolean.TRUE);
            seasonTicketLoyaltyPointsService.onSessionUnlink(seasonTicketId, sessionId);
        } else {
            rollbackUnassignSessionFromSeasonTicket(seasonTicket.getSessionId().longValue(), sessionId);
            response.setResult(Boolean.FALSE);
            if (unLinkResult.getReason() != null) {
                response.setReason(UnAssignSessionReason.valueOf(unLinkResult.getReason().name()));
            } else {
                response.setReason(UnAssignSessionReason.UNLINK_IMPOSSIBLE);
            }
        }

        return response;
    }

    @MySQLRead
    public void migrateTargetSession(Long targetSessionId) {
        SessionDTO targetSession = sessionService.getSession(targetSessionId);
        if (targetSession == null) {
            throw OneboxRestException.builder(MsEventSessionErrorCode.SESSION_NOT_FOUND).
                    setMessage("Session: " + targetSessionId + " not found").build();
        }
        refreshDataService.refreshEvent(targetSession.getEventId(), "migrateTargetSession");
    }

    private SeasonTicketSessionValidationResponse verifySessionSeats(SeasonTicketDTO seasonTicket, Long sessionId) {
        SeasonTicketSessionValidationResponse sessionResponse;
        SessionCompatibilityValidationResponse seatVerificationResponse = seasonTicketRepository
                .validateSessionCompatibility(seasonTicket.getSessionId().longValue(), sessionId);
        SeasonTicketSessionValidationReason reason = null;
        if (Objects.nonNull(seatVerificationResponse.getReason())) {
            reason = SeasonTicketSessionValidationReason.valueOf(seatVerificationResponse.getReason().name());
        }
        sessionResponse = new SeasonTicketSessionValidationResponse();
        sessionResponse.setResult(seatVerificationResponse.getResult());
        sessionResponse.setReason(reason);
        return sessionResponse;
    }

    private AssignSessionResponseDTO assignSessionSeats(SeasonTicketDTO seasonTicket, Long sessionId, Boolean updateBarcodes) {
        LinkSessionCapacityResponse linkSessionCapacityResponse = seasonTicketRepository
                .linkSessionCapacity(seasonTicket.getSessionId().longValue(), sessionId, updateBarcodes);

        AssignSessionResponseDTO sessionResponse = new AssignSessionResponseDTO();
        if (Boolean.TRUE.equals(linkSessionCapacityResponse.getResult())) {
            sessionResponse.setResult(Boolean.TRUE);
        } else {
            sessionResponse.setResult(Boolean.FALSE);
            if (Objects.nonNull(linkSessionCapacityResponse.getReason())) {
                sessionResponse.setReason(AssignSessionReason.valueOf(linkSessionCapacityResponse.getReason().name()));
            } else {
                sessionResponse.setReason(AssignSessionReason.ASSIGN_SESSION_GENERIC_ERROR);
            }
        }
        return sessionResponse;
    }

    @MySQLWrite
    public void rollbackAssignSessionFromSeasonTicket(Long seasonTicketSessionId, Long sessionId) {
        seasonSessionDao.disasociateSessionOfSeason(seasonTicketSessionId, sessionId);
    }

    private boolean seasonTicketHasSession(Long seasonTicketId, Long sessionId) {
        SeasonTicketSessionsSearchFilter seasonTicketSessionsSearchFilter = new SeasonTicketSessionsSearchFilter();
        seasonTicketSessionsSearchFilter.setSessionId(sessionId);
        SeasonTicketSessionsDTO sessions = searchCandidateSessions(seasonTicketSessionsSearchFilter, seasonTicketId);
        return sessions.getData()
                .stream()
                .filter(s -> SessionAssignationStatusDTO.ASSIGNED.equals(s.getStatus()))
                .anyMatch(s -> s.getSessionId().equals(sessionId.intValue()));
    }

    @MySQLWrite
    public void rollbackUnassignSessionFromSeasonTicket(Long seasonTicketSessionId, Long sessionId) {
        seasonSessionDao.asociateSessionstoSeason(seasonTicketSessionId, Collections.singletonList(sessionId));
    }

    @MySQLWrite
    public void assignSessionToSeasonTicketSession(Long seasonTicketSessionId, AssignSessionRequestDTO assignSessionRequestDTO) {
        seasonSessionDao.asociateSessionstoSeason(seasonTicketSessionId,
                Collections.singletonList(assignSessionRequestDTO.getSessionId()));
    }

    @MySQLWrite
    public void unassignSessionToSeasonTicketSession(Long seasonTicketSessionId, Long sessionId) {
        seasonSessionDao.disasociateSessionOfSeason(seasonTicketSessionId, sessionId);
    }

    public void updateBarcodes(Long seasonTicketId) {
        SeasonTicketStatusResponseDTO seasonTicketStatus = seasonTicketService.getStatus(seasonTicketId);
        if (!SeasonTicketInternalGenerationStatus.READY.equals(seasonTicketStatus.getGenerationStatus())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_IN_CREATION);
        }
        if (!SeasonTicketStatusDTO.SET_UP.equals(seasonTicketStatus.getStatus())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_IN_CREATION);
        }
        SeasonTicketDTO seasonTicket = seasonTicketService.getSeasonTicket(seasonTicketId);
        seasonTicketRepository.updateBarcodes(seasonTicket.getSessionId().longValue());
    }
}
