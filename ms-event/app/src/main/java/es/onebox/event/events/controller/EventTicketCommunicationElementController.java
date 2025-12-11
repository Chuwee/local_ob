package es.onebox.event.events.controller;

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
import es.onebox.event.events.service.EventTicketCommunicationElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/ticket-communication-elements")
public class EventTicketCommunicationElementController {

    private final EventTicketCommunicationElementService ticketCommunicationElementService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public EventTicketCommunicationElementController(
            EventTicketCommunicationElementService ticketCommunicationElementService, RefreshDataService refreshDataService) {
        this.ticketCommunicationElementService = ticketCommunicationElementService;
        this.refreshDataService = refreshDataService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{type}")
    public List<TicketCommunicationElementDTO> getEventTicketCommunicationElements(@PathVariable Long eventId,
                                                                                   @PathVariable TicketCommunicationElementCategory type, TicketCommunicationElementFilter filter) {
        return this.ticketCommunicationElementService.getEventCommunicationElements(eventId, filter, type);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/PASSBOOK")
    public List<PassbookCommunicationElementDTO> getEventPassbookCommunicationElements(@PathVariable Long eventId,
                                                                                       PassbookCommunicationElementFilter filter) {
        return this.ticketCommunicationElementService.getEventPassbookCommunicationElements(eventId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = POST, value = "/{type}")
    public void updateCommunicationElements(@PathVariable Long eventId, @PathVariable TicketCommunicationElementCategory type, @Valid @RequestBody TicketCommunicationElementDTO[] elements) {
        ticketCommunicationElementService.updateEventCommunicationElements(eventId, new HashSet<>(Arrays.asList(elements)), type);
        refreshDataService.refreshEvent(eventId, "updateCommunicationElements", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = POST, value = "/PASSBOOK")
    public void updatePassbookCommunicationElements(@PathVariable Long eventId, @Valid @RequestBody PassbookCommunicationElementDTO[] elements) {
        ticketCommunicationElementService.updateEventPassbookCommunicationElements(eventId, new HashSet<>(Arrays.asList(elements)));
        refreshDataService.refreshEvent(eventId, "updatePassbookCommunicationElements", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/{type}/languages/{language}/types/{tag}")
    public void deleteCommunicationElement(@PathVariable Long eventId, @PathVariable String language, @PathVariable TicketCommunicationElementCategory type, @PathVariable TicketCommunicationElementTagType tag) {
        ticketCommunicationElementService.deleteEventCommunicationElement(eventId, tag, language, type);
        refreshDataService.refreshEvent(eventId, "deleteCommunicationElement", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/PASSBOOK/languages/{language}/types/{tag}")
    public void deletePassbookCommunicationElement(@PathVariable Long eventId, @PathVariable String language, @PathVariable PassbookCommunicationElementTagType tag) {
        ticketCommunicationElementService.deleteEventPassbookCommunicationElement(eventId, tag, language);
        refreshDataService.refreshEvent(eventId, "deletePassbookCommunicationElement", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }
}
