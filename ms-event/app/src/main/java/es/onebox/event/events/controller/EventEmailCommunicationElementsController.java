package es.onebox.event.events.controller;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.communicationelements.enums.EmailCommunicationElementTagType;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.EmailCommunicationElementDTO;
import es.onebox.event.events.request.EmailCommunicationElementFilter;
import es.onebox.event.events.service.EventEmailCommunicationElementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Validated
@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/email-communication-elements")
public class EventEmailCommunicationElementsController {

    private final EventEmailCommunicationElementService eventEmailCommunicationElementService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public EventEmailCommunicationElementsController(EventEmailCommunicationElementService eventEmailCommunicationElementService,
                                                     RefreshDataService refreshDataService) {
        this.eventEmailCommunicationElementService = eventEmailCommunicationElementService;
        this.refreshDataService = refreshDataService;
    }

    @RequestMapping(method = GET)
    public List<EmailCommunicationElementDTO> getCommunicationElements(@PathVariable Long eventId, @Valid EmailCommunicationElementFilter filter) {
        return eventEmailCommunicationElementService.findCommunicationElements(eventId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = POST)
    public void updateCommunicationElements(@PathVariable Long eventId, @Valid @RequestBody EmailCommunicationElementDTO[] elements) {
        eventEmailCommunicationElementService.updateEventCommunicationElements(eventId, new HashSet<>(Arrays.asList(elements)));
        refreshDataService.refreshEvent(eventId, "updateCommunicationElements", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/languages/{language}/types/{type}")
    public void deleteCommunicationElement(@PathVariable Long eventId, @PathVariable String language, @PathVariable EmailCommunicationElementTagType type) {
        eventEmailCommunicationElementService.deleteCommunicationElement(eventId, type, language);
        refreshDataService.refreshEvent(eventId, "deleteCommunicationElement", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

}
