package es.onebox.event.sessions;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.service.EventService;
import es.onebox.event.sessions.dto.SessionTaxDTO;
import es.onebox.event.sessions.dto.UpdateSessionTaxDTO;
import es.onebox.event.sessions.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConfig.BASE_URL)
public class SessionTaxController {

    private final EventService eventService;
    private final SessionService sessionService;

    @Autowired
    public SessionTaxController(EventService eventService, SessionService sessionService) {
        this.eventService = eventService;
        this.sessionService = sessionService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/events/{eventId}/sessions/{sessionId}/taxes")
    public List<SessionTaxDTO> getSessionTaxes(@PathVariable(value = "eventId") Long eventId,
                                               @PathVariable(value = "sessionId") Long sessionId) {
        return eventService.getTaxesBySession(eventId, sessionId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/sessions/{sessionId}/taxes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSessionTaxes(@PathVariable(value = "sessionId") Long sessionId,
                                   @RequestBody @Valid UpdateSessionTaxDTO updateTaxes) {
        sessionService.updateSessionTaxes(sessionId, updateTaxes);
    }
}
