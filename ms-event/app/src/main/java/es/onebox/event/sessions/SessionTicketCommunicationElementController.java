package es.onebox.event.sessions;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.communicationelements.enums.PassbookCommunicationElementTagType;
import es.onebox.event.communicationelements.enums.TicketCommunicationElementTagType;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.PassbookCommunicationElementDTO;
import es.onebox.event.events.dto.TicketCommunicationElementDTO;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.events.request.PassbookCommunicationElementFilter;
import es.onebox.event.events.request.TicketCommunicationElementFilter;
import es.onebox.event.sessions.dto.PassbookCommunicationElementBulkDTO;
import es.onebox.event.sessions.dto.TicketCommunicationElementBulkDTO;
import es.onebox.event.sessions.service.SessionTicketCommunicationElementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Validated
@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/sessions")
public class SessionTicketCommunicationElementController {

    @Autowired
    private SessionTicketCommunicationElementService ticketCommunicationElementService;
    @Autowired
    private RefreshDataService refreshDataService;

    private static final String SESSION_ID_MUST_BE_ABOVE_0 = "Session Id must be above 0";
    private static final String EVENT_ID_MUST_BE_ABOVE_0 = "Event Id must be above 0";

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = POST, value = "/{sessionId}/ticket-communication-elements/PASSBOOK")
    public void updatePassbookCommunicationElements(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                    @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                    @Valid @RequestBody PassbookCommunicationElementDTO[] elements) {
        ticketCommunicationElementService.updateSessionPassbookCommunicationElements(eventId, sessionId, new HashSet<>(Arrays.asList(elements)));
        refreshDataService.refreshSession(sessionId, "updatePassbookCommunicationElements");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = POST, value = "/ticket-communication-elements/PASSBOOK")
    public void updatePassbookCommunicationElementsBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                        @Valid @RequestBody PassbookCommunicationElementBulkDTO request) {
        ticketCommunicationElementService.updateSessionPassbookCommunicationElementsBulk(eventId, request);
        refreshDataService.refreshSessions(eventId, request.getIds(), "updatePassbookCommunicationElementsBulk");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{sessionId}/ticket-communication-elements/PASSBOOK")
    public List<PassbookCommunicationElementDTO> getSessionPassbookCommunicationElements(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                                         @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                                                         PassbookCommunicationElementFilter filter) {
        return this.ticketCommunicationElementService.getSessionPassbookCommunicationElements(eventId, sessionId, filter);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/{sessionId}/ticket-communication-elements/PASSBOOK/languages/{language}/types/{tag}")
    public void deletePassbookCommunicationElement(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                   @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                   @PathVariable String language,
                                                   @PathVariable PassbookCommunicationElementTagType tag) {
        ticketCommunicationElementService.deleteSessionPassbookCommunicationElement(sessionId, tag, language);
        refreshDataService.refreshSession(sessionId, "deletePassbookCommunicationElement");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/ticket-communication-elements/PASSBOOK/languages/{language}/types/{tag}")
    public void deletePassbookCommunicationElementBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                       @PathVariable String language,
                                                       @PathVariable PassbookCommunicationElementTagType tag,
                                                       @RequestParam(value = "sessionId") @NotEmpty List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds) {
        ticketCommunicationElementService.deleteSessionPassbookCommunicationElementBulk(eventId, sessionIds, tag, language);
        refreshDataService.refreshSessions(eventId, sessionIds, "deletePassbookCommunicationElementBulk");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/ticket-communication-elements/PASSBOOK/images/languages/{language}")
    public void deletePassbookImageCommunicationElementsBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                             @PathVariable String language,
                                                             @RequestParam(value = "sessionId") @NotEmpty List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds) {
        ticketCommunicationElementService.deleteSessionPassbookImageCommunicationElementsBulk(eventId, sessionIds, language);
        refreshDataService.refreshSessions(eventId, sessionIds, "deletePassbookImageCommunicationElementsBulk");
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{sessionId}/ticket-communication-elements/{type}")
    public List<TicketCommunicationElementDTO> getSessionCommunicationElements(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                               @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                                               @PathVariable TicketCommunicationElementCategory type,
                                                                               TicketCommunicationElementFilter filter) {
        return this.ticketCommunicationElementService.getSessionCommunicationElements(eventId, sessionId, type, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = POST, value = "/{sessionId}/ticket-communication-elements/{type}")
    public void updateSessionCommunicationElements(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                   @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                   @PathVariable TicketCommunicationElementCategory type,
                                                   @Valid @RequestBody TicketCommunicationElementDTO[] elements) {
        ticketCommunicationElementService.updateSessionCommunicationElements(eventId, sessionId, type, new HashSet<>(Arrays.asList(elements)));
        refreshDataService.refreshSession(sessionId, "updateSessionCommunicationElements");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = POST, value = "/ticket-communication-elements/{type}")
    public void updateSessionCommunicationElementsBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                       @PathVariable TicketCommunicationElementCategory type,
                                                       @Valid @RequestBody TicketCommunicationElementBulkDTO request) {
        ticketCommunicationElementService.updateSessionCommunicationElementsBulk(eventId, type, request);
        refreshDataService.refreshSessions(eventId, request.getIds(), "updateSessionCommunicationElements", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/{sessionId}/ticket-communication-elements/{type}/languages/{language}/types/{tag}")
    public void deleteSessionCommunicationElements(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                   @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                   @PathVariable TicketCommunicationElementCategory type,
                                                   @PathVariable String language, @PathVariable TicketCommunicationElementTagType tag) {
        ticketCommunicationElementService.deleteSessionCommunicationElements(eventId, sessionId, type, language, tag);
        refreshDataService.refreshSession(sessionId, "deleteSessionCommunicationElements", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/ticket-communication-elements/{type}/languages/{language}/types/{tag}")
    public void deleteSessionCommunicationElementBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                      @PathVariable String language,
                                                      @PathVariable TicketCommunicationElementCategory type,
                                                      @PathVariable TicketCommunicationElementTagType tag,
                                                      @RequestParam(value = "sessionId") @NotEmpty List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds) {
        ticketCommunicationElementService.deleteSessionCommunicationElementBulk(eventId, sessionIds, type, language, tag);
        refreshDataService.refreshSessions(eventId, sessionIds, "deleteSessionCommunicationElementBulk", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/ticket-communication-elements/{type}/images/languages/{language}")
    public void deleteSessionImageCommunicationElementsBulk(@PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long eventId,
                                                            @PathVariable String language,
                                                            @PathVariable TicketCommunicationElementCategory type,
                                                            @RequestParam(value = "sessionId") @NotEmpty List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds) {
        ticketCommunicationElementService.deleteAllImageSessionCommunicationElementsBulk(eventId, sessionIds, type, language);
        refreshDataService.refreshSessions(eventId, sessionIds, "deleteSessionImageCommunicationElementsBulk");
    }

}
