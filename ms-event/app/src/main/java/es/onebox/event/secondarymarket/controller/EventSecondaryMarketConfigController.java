package es.onebox.event.secondarymarket.controller;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.secondarymarket.dto.CreateEventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.service.EventSecondaryMarketConfigService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/secondary-market")
public class EventSecondaryMarketConfigController {

    private final EventSecondaryMarketConfigService eventSecondaryMarketConfigService;

    @Autowired
    public EventSecondaryMarketConfigController(EventSecondaryMarketConfigService eventSecondaryMarketConfigService) {
        this.eventSecondaryMarketConfigService = eventSecondaryMarketConfigService;
    }

    @RequestMapping(
            method = GET,
            value = "events-config/{eventId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public EventSecondaryMarketConfigDTO getEventSecondaryMarketConfig(@PathVariable(value = "eventId") Long eventId) {
        return eventSecondaryMarketConfigService.getEventSecondaryMarketConfig(eventId);
    }

    @RequestMapping(
            method = POST,
            value = "events-config/{eventId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createEventSecondaryMarketConfig(@PathVariable(value = "eventId") Long eventId,
                                                 @RequestBody @Valid CreateEventSecondaryMarketConfigDTO createEventSecondaryMarketConfigDTO) {
        if (createEventSecondaryMarketConfigDTO == null) {
            throw OneboxRestException.builder(CoreErrorCode.BAD_PARAMETER).build();
        }
        eventSecondaryMarketConfigService.createEventSecondaryMarketConfig(eventId, createEventSecondaryMarketConfigDTO);
    }

    @RequestMapping(
            method = PUT,
            value = "events-config/{eventId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void upsertEventSecondaryMarketConfig(@PathVariable(value = "eventId") Long eventId,
                                                 @RequestBody @Valid CreateEventSecondaryMarketConfigDTO createEventSecondaryMarketConfigDTO) {
        eventSecondaryMarketConfigService.updateEventSecondaryMarketConfig(eventId, createEventSecondaryMarketConfigDTO);
    }

    @RequestMapping(
            method = DELETE,
            value = "events-config/{eventId}")
    public void deleteEventSecondaryMarketConfig(@PathVariable(value = "eventId") Long eventId) {
        eventSecondaryMarketConfigService.deleteEventSecondaryMarketConfig(eventId);
    }

    @RequestMapping(
            method = GET,
            value = "sessions/{sessionId}/channels/{channelId}")
    public EventSecondaryMarketConfigDTO existsChannelIdForEvent(@PathVariable(value = "sessionId") Long sessionId,
                                                                 @PathVariable(value = "channelId") Long channelId) {
        return eventSecondaryMarketConfigService.existsChannelIdForEvent(sessionId, channelId);
    }

}
