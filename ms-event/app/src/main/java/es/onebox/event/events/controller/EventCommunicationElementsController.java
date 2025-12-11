package es.onebox.event.events.controller;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.event.events.service.EventCommunicationElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = ApiConfig.BASE_URL + "/events/{eventId}/communication-elements")
public class EventCommunicationElementsController {

    private final EventCommunicationElementService eventCommunicationElementService;
    private final RefreshDataService refreshDataService;
    private final WebhookService webhookService;

    @Autowired
    public EventCommunicationElementsController(EventCommunicationElementService eventCommunicationElementService, RefreshDataService refreshDataService, WebhookService webhookService) {
        this.eventCommunicationElementService = eventCommunicationElementService;
        this.refreshDataService = refreshDataService;
        this.webhookService = webhookService;
    }

    @RequestMapping(method = GET)
    public List<EventCommunicationElementDTO> getEventCommunicationElements(@PathVariable Long eventId, @Valid EventCommunicationElementFilter filter) {
        return eventCommunicationElementService.findCommunicationElements(eventId, filter);
    }

    @RequestMapping(method = POST)
    public void updateEventCommunicationElements(@PathVariable Long eventId, @Valid @RequestBody EventCommunicationElementDTO[] elements) {

        eventCommunicationElementService.updateCommunicationElements(eventId, Arrays.asList(elements));
        refreshDataService.refreshEvent(eventId, "updateEventCommunicationElements", EventIndexationType.PARTIAL_COM_ELEMENTS);

        webhookService.sendEventNotification(eventId, EventTagType.getTagTypeById(Arrays.stream(elements).findFirst().get().getTagId()).isImage()
                ? NotificationSubtype.EVENT_COMMUNICATION_IMAGES : NotificationSubtype.EVENT_COMMUNICATION_TEXTS);
    }

}
