package es.onebox.event.sessions;

import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.sessions.dto.SessionGroupConfigDTO;
import es.onebox.event.sessions.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/groups")
public class SessionGroupController {

    private final SessionService sessionService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public SessionGroupController(SessionService sessionService, RefreshDataService refreshDataService) {
        this.sessionService = sessionService;
        this.refreshDataService = refreshDataService;
    }

    @RequestMapping(method = GET)
    public SessionGroupConfigDTO getSessionGroup(@PathVariable(value = "eventId") Integer eventId,
                                                 @PathVariable(value = "sessionId") Integer sessionId) {

        return sessionService.getSessionGroup(eventId, sessionId);
    }


    @RequestMapping(method = PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSessionGroup(@PathVariable(value = "eventId") Long eventId,
                                   @PathVariable(value = "sessionId") Long sessionId,
                                   @RequestBody SessionGroupConfigDTO request) {
        sessionService.updateSessionGroup(eventId, sessionId, request);
        refreshDataService.refreshSession(sessionId, "updateSessionGroup");
    }

    @RequestMapping(method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSessionGroup(@PathVariable(value = "eventId") Long eventId,
                                   @PathVariable(value = "sessionId") Long sessionId) {

        sessionService.deleteSessionGroup(eventId, sessionId);
        refreshDataService.refreshSession(sessionId, "deleteSessionGroup");
    }
}
