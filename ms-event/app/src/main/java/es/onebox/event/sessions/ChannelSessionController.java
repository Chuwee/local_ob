package es.onebox.event.sessions;

import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.request.ChannelEventCommunicationElementFilter;
import es.onebox.event.sessions.service.ChannelSessionCommunicationElementsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(ChannelSessionController.BASE_URI)
public class ChannelSessionController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/channels";
    private final ChannelSessionCommunicationElementsService channelSessionCommunicationElementsService;
    private final WebhookService webhookService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public ChannelSessionController(ChannelSessionCommunicationElementsService channelSessionCommunicationElementsService, WebhookService webhookService, RefreshDataService refreshDataService) {
        this.channelSessionCommunicationElementsService = channelSessionCommunicationElementsService;
        this.webhookService = webhookService;
        this.refreshDataService = refreshDataService;
    }

    @GetMapping(value = "/{channelId}/communication-elements")
    public List<EventCommunicationElementDTO> getChannelSessionCommunicationElements(@PathVariable Long eventId,
                                                                                     @PathVariable Long sessionId,
                                                                                     @PathVariable Long channelId,
                                                                                     @Valid ChannelEventCommunicationElementFilter filter) {
        return channelSessionCommunicationElementsService.findCommunicationElements(eventId, sessionId, channelId, filter);
    }


    @PostMapping(value = "/{channelId}/communication-elements")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelSessionCommunicationElements(@PathVariable Long eventId,
                                                          @PathVariable Long sessionId,
                                                          @PathVariable Long channelId,
                                                          @Valid @RequestBody EventCommunicationElementDTO[] elements) {
        channelSessionCommunicationElementsService.updateChannelSessionCommunicationElements(eventId,sessionId, channelId, Arrays.asList(elements));

        refreshDataService.refreshSession(sessionId, "updateChannelSessionCommunicationElements");
        webhookService.sendSessionNotification(sessionId, channelId, NotificationSubtype.SESSION_COMMUNICATION_IMAGES);
    }
}
