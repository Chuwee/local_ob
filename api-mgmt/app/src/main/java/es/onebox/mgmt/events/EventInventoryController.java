package es.onebox.mgmt.events;

import es.onebox.mgmt.config.ApiConfig;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(value = EventInventoryController.BASE_URI)
public class EventInventoryController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events";
    private final EventsService eventsService;

    public EventInventoryController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{eventId}/external-inventory")
    public void updateActivityInventory(@PathVariable(value = "eventId") Long eventId) {
        eventsService.updateActivityExternalInventory(eventId);
    }
}
