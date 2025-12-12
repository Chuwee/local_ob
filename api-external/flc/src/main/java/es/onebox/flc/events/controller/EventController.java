package es.onebox.flc.events.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.flc.common.GenericRequest;
import es.onebox.flc.events.dto.Event;
import es.onebox.flc.events.dto.EventState;
import es.onebox.flc.events.dto.SessionState;
import es.onebox.flc.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;

@RestController(value = "flcEventController")
@RequestMapping(ApiConfig.FLCApiConfig.BASE_URL + "/events")
public class EventController {
    @Autowired
    @Qualifier(value = "flcEventService")
    private EventService eventService;

    @GetMapping()
    public List<Event> getEvents(@RequestParam(value = "event_ids", required = false) List<Long> eventIds,
                                 @RequestParam(value = "start_date[gte]", required = false) ZonedDateTime gte,
                                 @RequestParam(value = "start_date[lte]", required = false) ZonedDateTime lte,
                                 @RequestParam(value = "session_start_date", required = false) ZonedDateTime sessionStartDate,
                                 @RequestParam(value = "session_end_date", required = false) ZonedDateTime sessionEndDate,
                                 @RequestParam(value = "event_states", required = false) List<EventState> eventStates,
                                 @RequestParam(value = "session_states", required = false) List<SessionState> sessionStates,
                                 @RequestParam(value = "venue_ids", required = false) List<Long> venueIds,
                                 @RequestParam(value = "external_reference_code", required = false) String externalReferenceCode,
                                 final @Valid @BindUsingJackson GenericRequest request) {

        return eventService.getEvents(eventIds, gte, lte, sessionStartDate, sessionEndDate, eventStates, sessionStates,
                venueIds, externalReferenceCode, request.getLimit(), request.getOffset());
    }
}
