package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Venue;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.AssignSessionResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSessionValidationMsEventResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSessions;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSessionsEventList;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UnAssignSessionResponse;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandler;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandlerStrategyProvider;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketSessionsConverter;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketSessionsSummary;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketStatusResponseDTO;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionAssignation;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionAssignationFilter;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionAssignationResponse;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionDTO;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionUnAssignation;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionValidation;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionValidationFilter;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionValidationResponse;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsEventResponse;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsEventsFilter;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsEventsResponse;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsResponse;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsSearchFilter;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketGenerationStatus;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketSessionUnAssignationReason;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketSessionValidationReason;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.SEASON_TICKET_NOT_READY;

@Service
public class SeasonTicketSessionsService {

    private final SeasonTicketService seasonTicketService;
    private final SeasonTicketRepository seasonTicketRepository;
    private final ReleasedSeatsQuotaHelper releasedSeatsQuotaHelper;
    private final AccessControlSystemsRepository accessControlSystemsRepository;
    private final ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider;


    @Autowired
    public SeasonTicketSessionsService(SeasonTicketService seasonTicketService,
                                       SeasonTicketRepository seasonTicketRepository,
                                       ReleasedSeatsQuotaHelper releasedSeatsQuotaHelper, AccessControlSystemsRepository accessControlSystemsRepository, ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider) {
        this.seasonTicketService = seasonTicketService;
        this.seasonTicketRepository = seasonTicketRepository;
        this.releasedSeatsQuotaHelper = releasedSeatsQuotaHelper;
        this.accessControlSystemsRepository = accessControlSystemsRepository;
        this.externalAccessControlHandlerStrategyProvider = externalAccessControlHandlerStrategyProvider;
    }

    public SeasonTicketSessionsResponse getSessions(SeasonTicketSessionsSearchFilter filter, Long seasonTicketId) {
        checkSeasonTicketValid(seasonTicketId);

        SeasonTicketSessions seasonTicketSessions = seasonTicketRepository
                .getSeasonTicketCandidateSessions(filter, seasonTicketId);

        List<SeasonTicketSessionDTO> seasonTicketSessionsList = seasonTicketSessions.getData().stream()
                .map(SeasonTicketSessionsConverter::fromMsEvent)
                .collect(Collectors.toList());

        SeasonTicketSessionsSummary seasonTicketSessionsSummary = SeasonTicketSessionsConverter
                .fromMsEvent(seasonTicketSessions.getSummary());

        SeasonTicketSessionsResponse seasonTicketSessionsResponse = new SeasonTicketSessionsResponse();
        seasonTicketSessionsResponse.setData(seasonTicketSessionsList);
        seasonTicketSessionsResponse.setMetadata(seasonTicketSessions.getMetadata());
        seasonTicketSessionsResponse.setSummary(seasonTicketSessionsSummary);

        return seasonTicketSessionsResponse;
    }

    public SeasonTicketSessionsEventsResponse getEventList(Long seasonTicketId, SeasonTicketSessionsEventsFilter filter) {
        checkSeasonTicketValid(seasonTicketId);

        SeasonTicketSessionsEventList seasonTicketEvents = seasonTicketRepository
                .getSeasonTicketSessionsEvents(seasonTicketId, filter);

        List<SeasonTicketSessionsEventResponse> seasonTicketSessionsEventResp = seasonTicketEvents.getData().stream()
                .map(SeasonTicketSessionsConverter::fromMsEvent)
                .collect(Collectors.toList());

        SeasonTicketSessionsEventsResponse response = new SeasonTicketSessionsEventsResponse();
        response.setData(seasonTicketSessionsEventResp);
        response.setMetadata(seasonTicketEvents.getMetadata());

        return response;
    }

    public void checkSeasonTicketValid(Long seasonTicketId) {
        this.seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        SeasonTicketStatusResponseDTO seasonTicketStatusResponseDTO = seasonTicketService
                .getSeasonTicketStatus(seasonTicketId);

        if(!SeasonTicketGenerationStatus.READY.equals(seasonTicketStatusResponseDTO.getGenerationStatus())) {
            throw new OneboxRestException.Builder<>(SEASON_TICKET_NOT_READY)
                    .setMessage("Season ticket must be ready")
                    .setHttpStatus(HttpStatus.PRECONDITION_FAILED)
                    .build();
        }
    }

    public SeasonTicketSessionValidationResponse verifySessions(Long seasonTicketId,
                                                                SeasonTicketSessionValidationFilter filter) {

        this.seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        if(filter == null || filter.getSessionList() == null || filter.getSessionList().isEmpty()) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "sessions must be a positive integer", null);
        }

        SeasonTicketSessionValidationResponse response = new SeasonTicketSessionValidationResponse();
        for(Long sessionId : filter.getSessionList()) {
            SeasonTicketSessionValidation seasonTicketSessionValidation = verifySession(seasonTicketId, sessionId,
                    Boolean.TRUE);
            response.getResult().add(seasonTicketSessionValidation);
        }
        return response;
    }

    private SeasonTicketSessionValidation verifySession(Long seasonTicketId, Long sessionId, Boolean includeSeats) {
        SeasonTicketSessionValidationMsEventResponse staticVerificationResponse = seasonTicketRepository
                .verifySessionsFromSeasonTicket(seasonTicketId, sessionId, includeSeats);
        SeasonTicketSessionValidation sessionResponse;
        SeasonTicketSessionValidationReason reason = null;
        if(staticVerificationResponse.getResult().equals(false) &&
                Objects.nonNull(staticVerificationResponse.getReason())) {
            reason = SeasonTicketSessionValidationReason.valueOf(staticVerificationResponse.getReason().name());
        }
        sessionResponse = new SeasonTicketSessionValidation(seasonTicketId, sessionId,
                staticVerificationResponse.getResult(), reason);
        return sessionResponse;
    }

    public SeasonTicketSessionAssignationResponse assignationProcess(Long seasonTicketId,
                                                                     SeasonTicketSessionAssignationFilter filter) {

        if (filter == null || filter.getSessionList() == null || filter.getSessionList().isEmpty()) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "sessions must be a positive integer", null);
        }

        SeasonTicket seasonTicket = this.seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);


        SeasonTicketSessionAssignationResponse response = assignSessions(seasonTicket, filter);
        if (response != null && CollectionUtils.isNotEmpty(response.getResult())) {
            response.getResult().stream().filter(result -> BooleanUtils.isTrue(result.getSessionAssigned()))
                    .forEach(result -> checkAndProcessExternalSeasonTicket(seasonTicket, result.getSessionId(), true));
        }
        return response;
    }

    public SeasonTicketSessionAssignationResponse assignSessions(SeasonTicket seasonTicket,
                                                                 SeasonTicketSessionAssignationFilter filter) {
        SeasonTicketSessionAssignationResponse response = new SeasonTicketSessionAssignationResponse();
        for(Long sessionId : filter.getSessionList()) {

            SeasonTicketSessionAssignation sessionAssignation = new SeasonTicketSessionAssignation();
            sessionAssignation.setSeasonTicketId(seasonTicket.getId());
            sessionAssignation.setSessionId(sessionId);

            // Verify and assign season ticket fake session to target event session
            AssignSessionResponse assignSessionResult = seasonTicketRepository
                    .assignSession(seasonTicket.getId(), sessionId, filter.getUpdateBarcodes());

            if (Boolean.TRUE.equals(assignSessionResult.getResult())) {
                sessionAssignation.setSessionAssigned(Boolean.TRUE);
            } else {
                sessionAssignation.setSessionAssigned(Boolean.FALSE);
                if(Objects.nonNull(assignSessionResult.getReason())) {
                    sessionAssignation.setReason(SeasonTicketSessionValidationReason.valueOf(assignSessionResult.getReason().name()));
                } else {
                    sessionAssignation.setReason(SeasonTicketSessionValidationReason.ASSIGN_SESSION_GENERIC_ERROR);
                }
            }

            response.getResult().add(sessionAssignation);
        }
        createReleasedSeatsQuota(response, seasonTicket);

        return response;
    }

    private void createReleasedSeatsQuota(SeasonTicketSessionAssignationResponse response, SeasonTicket seasonTicket) {
        if (response.getResult() != null && BooleanUtils.isTrue(seasonTicket.getAllowReleaseSeat())) {
            Set<Long> assignedSessionIds = response.getResult().stream()
                    .filter(SeasonTicketSessionAssignation::getSessionAssigned)
                    .map(SeasonTicketSessionAssignation::getSessionId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            releasedSeatsQuotaHelper.initReleasedSeatsQuota(assignedSessionIds);
        }
    }

    public SeasonTicketSessionUnAssignation unAssignProcess(Long seasonTicketId, Long sessionId, Boolean updateBarcodes) {
        if(seasonTicketId < 0) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "seasonTicketId must be a positive integer", null);
        }
        if(sessionId < 0) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "sessionId must be a positive integer", null);
        }
        SeasonTicket seasonTicket = this.seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);

        SeasonTicketSessionUnAssignation response = unAssignSession(seasonTicket, sessionId, updateBarcodes);
        if (BooleanUtils.isTrue(response.getSessionUnAssigned())) {
            checkAndProcessExternalSeasonTicket(seasonTicket, sessionId, updateBarcodes);
        }

        return response;
    }

    public SeasonTicketSessionUnAssignation unAssignSession(SeasonTicket seasonTicket, Long sessionId, Boolean updateBarcodes) {

        SeasonTicketSessionUnAssignation response = new SeasonTicketSessionUnAssignation();
        response.setSessionId(sessionId);
        response.setSeasonTicketId(seasonTicket.getId());

        UnAssignSessionResponse unAssignSessionResponse = seasonTicketRepository.unAssignSession(seasonTicket.getId(), sessionId, updateBarcodes);

        if (Boolean.TRUE.equals(unAssignSessionResponse.getResult())) {
            response.setSessionUnAssigned(Boolean.TRUE);
        } else {
            response.setSessionUnAssigned(Boolean.FALSE);
            if(unAssignSessionResponse.getReason() != null) {
                response.setReason(unAssignSessionResponse.getReason());
            } else {
                response.setReason(SeasonTicketSessionUnAssignationReason.UNLINK_IMPOSSIBLE);
            }
        }

        return response;
    }

    public void updateBarcodes(Long seasonTicketId) {
        if(seasonTicketId < 0) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "seasonTicketId must be a positive integer", null);
        }
        this.seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        seasonTicketRepository.updateBarcodes(seasonTicketId);
    }


    private void checkAndProcessExternalSeasonTicket(SeasonTicket seasonTicket, Long sessionId, boolean isAssignation) {
        if (seasonTicket == null || CollectionUtils.isEmpty(seasonTicket.getVenues())) {
            return;
        }
        List<Long> venueIds = seasonTicket.getVenues().stream().map(Venue::getId).distinct().toList();
        List<AccessControlSystem> accessControlSystems = new ArrayList<>();
        venueIds.forEach(venueId -> {
            List<AccessControlSystem> venueAccessControlSystems = accessControlSystemsRepository.findByVenueIdCached(venueId);
            if (CollectionUtils.isNotEmpty(venueAccessControlSystems)) {
                accessControlSystems.addAll(venueAccessControlSystems);
            }
        });

        if (CollectionUtils.isNotEmpty(accessControlSystems)) {
            accessControlSystems.stream().distinct().forEach(accessControlSystem -> {
                ExternalAccessControlHandler externalAccessControlHandler;
                externalAccessControlHandler = externalAccessControlHandlerStrategyProvider.provide(accessControlSystem.name());


                if (externalAccessControlHandler == null) {
                    return;
                }

                if (isAssignation) {
                    externalAccessControlHandler.assignSessionToSeasonTicket(seasonTicket, sessionId);
                } else {
                    externalAccessControlHandler.unassignSessionFromSeasonTicket(seasonTicket, sessionId);
                }
            });
        }


    }

}