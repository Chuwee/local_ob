package es.onebox.event.events.controller;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.sessions.dto.SessionSaleConstraintDTO;
import es.onebox.event.sessions.service.SessionSaleConstraintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/sessions-sale-constraints")
public class EventSessionSaleConstraintsController {

    @Autowired
    private SessionSaleConstraintService sessionSaleConstraintService;

    @RequestMapping(method = GET)
    public List<SessionSaleConstraintDTO> getEventSessionsSaleConstraints(@PathVariable(value = "eventId") Long eventId) {

        if (eventId == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "EventId cannot be null", null);
        }

        return sessionSaleConstraintService.getEventSaleConstraints(eventId);
    }
}
