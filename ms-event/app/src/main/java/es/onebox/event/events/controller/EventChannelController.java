package es.onebox.event.events.controller;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.ChannelEventImageConfigDTO;
import es.onebox.event.events.dto.CreateEventChannelDTO;
import es.onebox.event.events.dto.EventChannelDTO;
import es.onebox.event.events.dto.EventChannelSurchargesDTO;
import es.onebox.event.events.dto.EventChannelSurchargesListDTO;
import es.onebox.event.events.dto.EventChannelsDTO;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.dto.RequestSalesEventChannelDTO;
import es.onebox.event.events.dto.UpdateEventChannelDTO;
import es.onebox.event.events.request.ChannelEventCommunicationElementFilter;
import es.onebox.event.events.service.ChannelEventCommunicationElementsService;
import es.onebox.event.events.service.EventChannelService;
import es.onebox.event.events.service.EventChannelSurchargesService;
import es.onebox.event.priceengine.request.EventChannelSearchFilter;
import es.onebox.event.pricesengine.PriceSimulationService;
import es.onebox.event.pricesengine.dto.VenueConfigPricesSimulationDTO;
import es.onebox.event.surcharges.dto.SurchargeTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(EventChannelController.BASE_URI)
public class EventChannelController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/channels";
    private final EventChannelService eventChannelService;
    private final RefreshDataService refreshDataService;
    private final EventChannelSurchargesService eventChannelSurchargesService;
    private final PriceSimulationService priceSimulationService;
    private final WebhookService webhookService;
    private final ChannelEventCommunicationElementsService channelEventCommunicationElementsService;

    @Autowired
    public EventChannelController(EventChannelService eventChannelService, RefreshDataService refreshDataService,
                                  EventChannelSurchargesService eventChannelSurchargesService,
                                  PriceSimulationService priceSimulationService, WebhookService webhookService,
                                  ChannelEventCommunicationElementsService channelEventCommunicationElementsService) {
        this.eventChannelService = eventChannelService;
        this.refreshDataService = refreshDataService;
        this.eventChannelSurchargesService = eventChannelSurchargesService;
        this.priceSimulationService = priceSimulationService;
        this.webhookService = webhookService;
        this.channelEventCommunicationElementsService = channelEventCommunicationElementsService;
    }

    @DeleteMapping(value = "/{channelId}")
    public void deleteEventChannel(@PathVariable Long eventId, @PathVariable Long channelId) {
        eventChannelService.delete(eventId, channelId);
        refreshDataService.refreshEvent(eventId, "deleteEventChannel");
    }

    @GetMapping()
    public EventChannelsDTO getEventChannels(@PathVariable Long eventId,
                                             @Valid EventChannelSearchFilter eventChannelSearchFilter) {
        return eventChannelService.getEventChannels(eventId, eventChannelSearchFilter);
    }

    @GetMapping(value = "/{channelId}")
    public EventChannelDTO getEventChannel(@PathVariable Long eventId, @PathVariable Long channelId) {
        return eventChannelService.getEventChannel(eventId, channelId);
    }

    @PostMapping()
    public ResponseEntity<Serializable> createEventChannel(@PathVariable Long eventId,
                                                           @RequestBody CreateEventChannelDTO createEventChannelDTO) {
        eventChannelService.createEventChannel(eventId, createEventChannelDTO.getChannelId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/{channelId}/request-approval")
    public ResponseEntity<Serializable> requestChannelApproval(@PathVariable Long eventId, @PathVariable Long channelId,
                                                               @RequestBody RequestSalesEventChannelDTO requestSalesEventChannelDTO) {
        if (requestSalesEventChannelDTO == null || requestSalesEventChannelDTO.getUserId() == null
                || requestSalesEventChannelDTO.getUserId() <= 0) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "userId is mandatory", null);
        }
        eventChannelService.requestChannelApproval(eventId, channelId, requestSalesEventChannelDTO.getUserId());
        refreshDataService.refreshEvent(eventId, "requestChannelApproval");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/{channelId}")
    public ResponseEntity<Serializable> updateEventChannel(@PathVariable Long eventId, @PathVariable Long channelId, @RequestBody UpdateEventChannelDTO updateEventChannel) {
        eventChannelService.updateEventChannel(eventId, channelId, updateEventChannel);
        refreshDataService.refreshEvent(eventId, "updateEventChannel");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{channelId}/surcharges")
    public ResponseEntity<List<EventChannelSurchargesDTO>> getEventChannelSurcharges(@PathVariable Long eventId, @PathVariable Long channelId,
                                                                                     @RequestParam(value = "type", required = false) List<SurchargeTypeDTO> types) {
        List<EventChannelSurchargesDTO> surcharges = eventChannelSurchargesService.getEventChannelSurcharges(eventId, channelId, types);

        if (Objects.nonNull(surcharges)) {
            return new ResponseEntity<>(surcharges, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/{channelId}/surcharges")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setEventChannelSurcharges(@PathVariable Long eventId, @PathVariable Long channelId, @RequestBody EventChannelSurchargesListDTO surchargesRequest) {
        eventChannelSurchargesService.setEventChannelSurcharges(eventId, channelId, surchargesRequest);
        refreshDataService.refreshEvent(eventId, "setEventChannelSurcharges");
        webhookService.sendWebhookGenericEvent(eventId, null, null, channelId, NotificationSubtype.EVENT_CHANNEL_SURCHARGES);
    }

    @GetMapping(value = "/{channelId}/price-simulation")
    public List<VenueConfigPricesSimulationDTO> getEventChannelPricesSimulation(@PathVariable Long eventId,
                                                                                @PathVariable Long channelId) {
        return priceSimulationService.getPriceSimulationIdEventAndChannelId(eventId, channelId);
    }

    @GetMapping(value = "/{channelId}/communication-elements")
    public List<EventCommunicationElementDTO> getChannelEventCommunicationElements(@PathVariable Long eventId,
                                                                                   @PathVariable Long channelId,
                                                                                   @Valid ChannelEventCommunicationElementFilter filter) {
        return channelEventCommunicationElementsService.findCommunicationElements(eventId, channelId, filter);
    }

    @GetMapping(value = "/{channelId}/communication-elements/images-config")
    public List<ChannelEventImageConfigDTO> getChannelEventImagesConfiguration(@PathVariable Long eventId,
                                                                               @PathVariable Long channelId) {
        return channelEventCommunicationElementsService.getChannelEventImagesConfiguration(eventId, channelId);
    }

    @PostMapping(value = "/{channelId}/communication-elements")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelEventCommunicationElements(@PathVariable Long eventId, @PathVariable Long channelId,
                                                        @Valid @RequestBody EventCommunicationElementDTO[] elements) {
        channelEventCommunicationElementsService.updateChannelEventCommunicationElements(eventId, channelId, Arrays.asList(elements));

        refreshDataService.refreshEvent(eventId, "updateChannelEventCommunicationElements");
        webhookService.sendWebhookGenericEvent(eventId, null, null, channelId, NotificationSubtype.EVENT_COMMUNICATION_IMAGES);
    }

}
