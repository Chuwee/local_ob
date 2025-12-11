package es.onebox.event.sessions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.request.PriceTypeBaseFilter;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.sessions.dto.CloneSessionDTO;
import es.onebox.event.sessions.dto.CreateSessionDTO;
import es.onebox.event.sessions.dto.GenerationStatusSessionDTO;
import es.onebox.event.sessions.dto.GenerationStatusSessionRequestDTO;
import es.onebox.event.sessions.dto.LinkedSessionDTO;
import es.onebox.event.sessions.dto.PriceTypeRequestDTO;
import es.onebox.event.sessions.dto.PriceTypesDTO;
import es.onebox.event.sessions.dto.SessionConfigDTO;
import es.onebox.event.sessions.dto.SessionCounterDTO;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionPackDTO;
import es.onebox.event.sessions.dto.SessionRatesDTO;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.dto.SessionsDTO;
import es.onebox.event.sessions.dto.SessionsGroupsDTO;
import es.onebox.event.sessions.dto.SessionsPresaleUpdateDTO;
import es.onebox.event.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.event.sessions.dto.UpdateSessionsRequestDTO;
import es.onebox.event.sessions.quartz.SessionStreamingEmailService;
import es.onebox.event.sessions.request.SessionIDSearchFilter;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.sessions.request.SessionsGroupsSearchFilter;
import es.onebox.event.sessions.service.SessionPresaleService;
import es.onebox.event.sessions.service.SessionService;
import es.onebox.event.sessions.utils.SessionValidator;
import es.onebox.event.sorting.SessionField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping(ApiConfig.BASE_URL)
public class SessionController {

    private final SessionService sessionService;
    private final SessionStreamingEmailService sessionStreamingEmailService;
    private final SessionPresaleService sessionPresaleService;

    @Autowired
    public SessionController(SessionService sessionService, SessionStreamingEmailService sessionStreamingEmailService,
                             SessionPresaleService sessionPresaleService) {
        this.sessionService = sessionService;
        this.sessionStreamingEmailService = sessionStreamingEmailService;
        this.sessionPresaleService = sessionPresaleService;
    }

    @GetMapping("/sessions")
    public SessionsDTO search(@NotNull SessionSearchFilter filter) {
        return sessionService.searchSessions(null, filter);
    }

    @GetMapping("/sessions/ids")
    public SessionsDTO searchSessionIds(@NotNull SessionIDSearchFilter filter) {
        filter.setFields(List.of(SessionField.ID.getRequestField()));
        return sessionService.searchSessions(null, filter);
    }

    @GetMapping("/sessions/{sessionId:[0-9]+}")
    public SessionDTO getSessionWithoutEventId(@PathVariable Long sessionId) {
        return sessionService.getSessionWithoutEventId(sessionId);
    }

    @GetMapping("/sessions/{sessionId}/config")
    public SessionConfigDTO getSessionConfig(@PathVariable Long sessionId) {
        return sessionService.getSessionConfig(sessionId);
    }

    @GetMapping("/events/{eventId}/sessions")
    public SessionsDTO search(@PathVariable(value = "eventId") Long eventId, @NotNull SessionSearchFilter filter) {
        return sessionService.searchSessions(eventId, filter);
    }

    @GetMapping("/events/{eventId}/sessions/groups")
    public SessionsGroupsDTO searchGroups(@PathVariable(value = "eventId") Long eventId, @NotNull SessionsGroupsSearchFilter filter) {
        return sessionService.searchGroups(eventId, filter);
    }

    @GetMapping("/events/{eventId}/sessions/{sessionId}")
    public SessionDTO getSession(@PathVariable Long eventId, @PathVariable Long sessionId) {
        return sessionService.getSession(eventId, sessionId);
    }

	@PutMapping(value = "/events/{eventId}/sessions/{sessionId}/price-zones/{priceZoneId}/invitation-limits/increase")
	public ResponseEntity<Void> incrementLimit(@PathVariable(value = "eventId") Integer eventId,
			@RequestBody @Valid SessionCounterDTO counter, @PathVariable Integer priceZoneId,
			@PathVariable Integer sessionId) {
		sessionService.incrementLimit(eventId, sessionId, priceZoneId, counter);
		return ResponseEntity.noContent().build();
	}

	@PutMapping(value = "/events/{eventId}/sessions/{sessionId}/price-zones/{priceZoneId}/invitation-limits/decrease")
	public ResponseEntity<Void> decrementLimit(@PathVariable(value = "eventId") Integer eventId,
			@RequestBody @Valid SessionCounterDTO counter, @PathVariable Integer priceZoneId,
			@PathVariable Integer sessionId) {
		sessionService.decrementLimit(eventId, sessionId, priceZoneId, counter);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/events/{eventId}/sessions/{sessionId}/price-zones/{priceZoneId}/invitation-limits")
	public ResponseEntity<SessionCounterDTO> getLimit(@PathVariable(value = "eventId") Integer eventId,
			@PathVariable Integer priceZoneId,
			@PathVariable Integer sessionId) {
		return ResponseEntity.ok(new SessionCounterDTO(sessionService.getLimit(eventId, sessionId, priceZoneId)));
	}

    @GetMapping("/events/{eventId}/sessions/{sessionId}/linked-sessions")
    public List<LinkedSessionDTO> getLinkedSessions(@PathVariable Long eventId, @PathVariable Long sessionId) {
        return sessionService.getLinkedSessions(eventId, sessionId);
    }

    @GetMapping("/events/{eventId}/session-packs/{sessionId}")
    public SessionPackDTO getSessionPack(@PathVariable Long eventId, @PathVariable Long sessionId) {
        return sessionService.getSessionPack(eventId, sessionId);
    }

    @PostMapping("/events/{eventId}/sessions")
    public ResponseEntity<Long> createSession(@PathVariable(value = "eventId") Long eventId, @RequestBody @NotNull CreateSessionDTO session) {
        Long sessionId = sessionService.createSession(eventId, session);
        sessionService.postCreateSession(eventId, sessionId, session);

        return new ResponseEntity<>(sessionId, HttpStatus.CREATED);
    }

    @PostMapping("/events/{eventId}/sessions/bulk")
    public ResponseEntity<List<Long>> createSessions(@PathVariable(value = "eventId") Long eventId, @RequestBody @NotEmpty CreateSessionDTO[] sessions) {

        List<Long> sessionIds = sessionService.createSessions(eventId, Arrays.asList(sessions));

        sessionService.postCreateSessions(eventId, sessionIds, null, null);

        return new ResponseEntity<>(sessionIds, HttpStatus.CREATED);
    }

    @PostMapping("/events/{eventId}/sessions/{sessionId}/clone")
    public ResponseEntity<Long> cloneSession(@PathVariable(value = "eventId") Long eventId, @PathVariable Long sessionId, @RequestBody @NotNull CloneSessionDTO cloneData) {

        Long newSessionId = sessionService.cloneSession(eventId, sessionId, cloneData);

        cloneData.setSourceSessionId(sessionId);
        sessionService.postCloneSession(eventId, newSessionId, cloneData);

        return new ResponseEntity<>(newSessionId, HttpStatus.CREATED);
    }

    @GetMapping("/events/{eventId}/sessions/{sessionId}/generation-status")
    public GenerationStatusSessionDTO generationStatus(@PathVariable(value = "eventId") Long eventId, @PathVariable Long sessionId) {
        SessionDTO session = sessionService.getSession(eventId, sessionId);

        GenerationStatusSessionDTO generationStatusSessionDTO = new GenerationStatusSessionDTO();
        generationStatusSessionDTO.setSessionGenerationStatus(session.getGenerationStatus());
        generationStatusSessionDTO.setStatus(session.getStatus());

        return generationStatusSessionDTO;
    }

    @PutMapping("/events/{eventId}/sessions/{sessionId}/generation-status")
    public void generationStatus(@PathVariable(value = "eventId") Long eventId, @PathVariable Long sessionId, @RequestBody @NotNull GenerationStatusSessionRequestDTO generationStatusSessionRequest) {
        sessionService.updateGenerationStatus(eventId, sessionId, generationStatusSessionRequest);
    }

    @PutMapping("/events/{eventId}/sessions/{sessionId}")
    public void updateSession(@PathVariable Long eventId,
                              @PathVariable Long sessionId,
                              @RequestBody @NotNull @Valid UpdateSessionRequestDTO request) {

        if (request.getId() == null) {
            request.setId(sessionId);
        } else if (!request.getId().equals(sessionId)) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Field session.id is not equal to current path sessionId", null);
        }

        SessionValidator.validateNonRelativeDates(request.getDate());

        SessionStatus oldStatus = sessionService.getSessionStatus(sessionId);
        sessionService.updateSession(eventId, request);
        sessionService.postUpdateSession(eventId, request, oldStatus);
    }

    @DeleteMapping("/events/{eventId}/sessions/{sessionId}")
    public void deleteSession(@PathVariable Long eventId, @PathVariable Long sessionId) {

        UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
        request.setId(sessionId);
        request.setStatus(SessionStatus.DELETED);

        sessionService.updateSession(eventId, request);
        sessionService.postDeleteSession(eventId, sessionId, request);
    }

    @PutMapping("/events/{eventId}/sessions/bulk")
    public ResponseEntity<Map<Long, String>> updateSessions(@PathVariable Long eventId,
                                                            @RequestParam(value = "preview", required = false) Boolean preview,
                                                            @RequestBody @NotNull @Valid UpdateSessionsRequestDTO request) {

        Map<Long, SessionStatus> oldStatuses = sessionService.getSessionStatuses(request.getIds());
        Map<Long, String> sessionsUpdateStatus = sessionService.updateSessions(eventId, request, preview);
        sessionService.postUpdateSessions(eventId, sessionsUpdateStatus, request, preview, oldStatuses);

        return ResponseEntity.ok(sessionsUpdateStatus);
    }

    @GetMapping("/events/{eventId}/sessions/{sessionId}/price-types")
    public PriceTypesDTO getPriceTypes(@PathVariable Long eventId,
                                       @PathVariable Long sessionId,
                                       PriceTypeBaseFilter filter) {
        return sessionService.getPriceTypes(eventId, sessionId, filter);
    }

    @PutMapping("/events/{eventId}/sessions/{sessionId}/price-types/{priceTypeId}")
    public void updatePriceTypes(@PathVariable Long eventId,
                                          @PathVariable Long sessionId,
                                          @PathVariable Long priceTypeId,
                                          @RequestBody @Valid PriceTypeRequestDTO request) {
        sessionService.updateGateId(eventId, sessionId, priceTypeId, request);
    }

    @GetMapping("/events/{eventId}/sessions/{sessionId}/rates")
    public SessionRatesDTO getSessionRates(RatesFilter filter,
                                           @PathVariable(value = "eventId") Long eventId,
                                           @PathVariable(value = "sessionId") Long sessionId) {

        return sessionService.getSessionRates(eventId, sessionId, filter);
    }

    @PostMapping("/events/{eventId}/sessions/{sessionId}/live/sendmail")
    public void sendLiveSessionMails(@PathVariable Long eventId, @PathVariable Long sessionId,
                                     @RequestParam(required = false) String orderCode) {
        sessionStreamingEmailService.sendEmails(sessionId, orderCode);
    }

    @PutMapping("/events/{eventId}/presale/{promotionId}")
    public void updatePresale(@PathVariable Long eventId,
                              @PathVariable Long promotionId,
                              @RequestBody @Valid SessionsPresaleUpdateDTO request) {
        sessionPresaleService.updatePresale(eventId, promotionId, request);
    }
}
