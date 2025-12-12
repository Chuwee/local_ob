package es.onebox.flc.sessions.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.flc.common.GenericRequest;
import es.onebox.flc.events.dto.SessionState;
import es.onebox.flc.sessions.dto.Session;
import es.onebox.flc.sessions.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController(value = "flcSessionController")
@RequestMapping(ApiConfig.FLCApiConfig.BASE_URL + "/sessions")
public class SessionController {

    @Autowired
    @Qualifier(value = "flcSessionService")
    private SessionService sessionService;

    @GetMapping()
    public List<Session> getSessions(@RequestParam(value = "session_ids", required = false) List<Long> sessionIds,
                                     @RequestParam(value = "session_states", required = false) List<SessionState> sessionStates,
                                     @RequestParam(value = "session_start", required = false) ZonedDateTime sessionStart,
                                     @RequestParam(value = "session_end", required = false) ZonedDateTime sessionEnd,
                                     @RequestParam(value = "event_ids", required = false) List<Long> eventIds,
                                     @RequestParam(value = "venue_ids", required = false) List<Long> venueIds,
                                     @RequestParam(value = "access_validation_space_ids", required = false) List<Long> accessValidationSpaceIds,
                                     final @Valid @BindUsingJackson GenericRequest request) {
        return sessionService.getSessions(sessionIds, sessionStates, sessionStart, sessionEnd, eventIds, venueIds, accessValidationSpaceIds, request.getLimit(), request.getOffset());
    }

    @GetMapping(value = "/{sessionId}")
    public Session getSession(@PathVariable("sessionId") Long sessionId) {
        return sessionService.getSession(sessionId);
    }
}
