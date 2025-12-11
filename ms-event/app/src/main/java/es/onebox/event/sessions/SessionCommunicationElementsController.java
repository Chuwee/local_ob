package es.onebox.event.sessions;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.event.sessions.dto.UpdateSessionCommunicationElementsBulkDTO;
import es.onebox.event.sessions.service.SessionCommunicationElementsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Validated
@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/sessions")
public class SessionCommunicationElementsController {

    private final SessionCommunicationElementsService sessionCommunicationElementsService;
    private final RefreshDataService refreshDataService;
    private final WebhookService webhookService;

    private static final String SESSION_ID_MUST_BE_ABOVE_0 = "Session Id must be above 0";
    private static final String EVENT_ID_MUST_BE_ABOVE_0 = "Event Id must be above 0";

    @Autowired
    public SessionCommunicationElementsController(
            SessionCommunicationElementsService sessionCommunicationElementsService, RefreshDataService refreshDataService, WebhookService webhookService) {
        this.sessionCommunicationElementsService = sessionCommunicationElementsService;
        this.refreshDataService = refreshDataService;
        this.webhookService = webhookService;
    }

    @RequestMapping(
            method = GET,
            value = "/{sessionId}/communication-elements")
    public List<EventCommunicationElementDTO> getSessionCommunicationElements(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                              @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                                              @Valid EventCommunicationElementFilter filter) {
        return sessionCommunicationElementsService.findCommunicationElements(eventId, sessionId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(
            method = POST,
            value = "/{sessionId}/communication-elements")
    public void updateSessionCommunicationElements(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                   @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                   @Valid @RequestBody EventCommunicationElementDTO[] elements) {
        sessionCommunicationElementsService.updateCommunicationElements(eventId, sessionId, Arrays.asList(elements));
        refreshDataService.refreshSession(sessionId, "updateSessionCommunicationElements", EventIndexationType.PARTIAL_COM_ELEMENTS);

        NotificationSubtype notificationSubtype = NotificationSubtype.SESSION_COMMUNICATION_TEXTS;
        if (Arrays.stream(elements).findFirst().isPresent() && Objects.nonNull(Arrays.stream(elements).findFirst().get().getImageBinary())) {
            notificationSubtype = NotificationSubtype.SESSION_COMMUNICATION_IMAGES;
        }

        webhookService.sendSessionNotification(sessionId, notificationSubtype);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(
            method = POST,
            value = "/communication-elements")
    public void updateSessionCommunicationElementsBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                       @Valid @RequestBody UpdateSessionCommunicationElementsBulkDTO data) {
        sessionCommunicationElementsService.updateCommunicationElementsBulk(eventId, data);
        for (Long sessionId : data.getIds()) {

            NotificationSubtype notificationSubtype = NotificationSubtype.SESSION_COMMUNICATION_TEXTS;
            if (data.getValues().stream().findFirst().isPresent() && Objects.nonNull(data.getValues().stream().findFirst().get().getImageBinary())) {
                notificationSubtype = NotificationSubtype.SESSION_COMMUNICATION_IMAGES;
            }
            webhookService.sendSessionNotification(sessionId, notificationSubtype);
        }
        refreshDataService.refreshSessions(eventId, data.getIds(), "updateSessionCommunicationElementsBulk", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(
            method = DELETE,
            value = "/communication-elements/languages/{language}")
    public void deleteSessionCommunicationElementsBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                       @PathVariable String language,
                                                       @RequestParam(value = "sessionId") List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds) {
        sessionCommunicationElementsService.deleteCommunicationElementsBulk(eventId, language, sessionIds);
        for (Long sessionId : sessionIds) {
            webhookService.sendSessionNotification(sessionId,
                    NotificationSubtype.SESSION_COMMUNICATION_DELETED);
        }
        refreshDataService.refreshSessions(eventId, sessionIds, "deleteSessionCommunicationElementsBulk", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

}
