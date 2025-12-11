package es.onebox.event.events.controller;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.EventVenueViewConfigDTO;
import es.onebox.event.events.service.EventConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/venue-view-config")
public class EventVenueViewConfigController {

    private final EventConfigService eventConfigService;

    @Autowired
    public EventVenueViewConfigController(EventConfigService eventConfigService) {
        this.eventConfigService = eventConfigService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public EventVenueViewConfigDTO getEventVenueViewConfig(@PathVariable(value = "eventId") Long eventId) {
        validateIdentifier(eventId);
        return eventConfigService.getEventVenueViewConfig(eventId);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateEventVenueViewConfig(@PathVariable(value = "eventId") Long eventId,
                                           @RequestBody EventVenueViewConfigDTO eventVenueViewConfigDTO) {
        validateIdentifier(eventId);
        if (eventVenueViewConfigDTO == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "eventVenueViewConfigDTO is mandatory", null);
        }
        eventConfigService.updateEventVenueViewConfig(eventId, eventVenueViewConfigDTO);
    }

    private void validateIdentifier(Long anId) {
        if (anId == null || anId <= 0) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "eventId is mandatory", null);
        }
    }
}
