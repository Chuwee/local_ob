package es.onebox.eci.events.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.eci.common.GenericRequest;
import es.onebox.eci.events.dto.Event;
import es.onebox.eci.events.dto.Session;
import es.onebox.eci.events.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping(ApiConfig.ECIApiConfig.BASE_URL + "/{channelIdentifier}/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping()
    public List<Event> getEvents(@RequestParam(value = "session_start_date[gte]", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime gte,
                                 @RequestParam(value = "session_start_date[lte]", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime lte,
                                 @PathVariable("channelIdentifier") String channelIdentifier,
                                 final @Valid @BindUsingJackson GenericRequest request) {

        return eventService.getEvents(gte, lte, request.getLimit(), request.getOffset(), channelIdentifier);
    }

    @GetMapping(value = "/{eventId}")
    public Event getEvent(@PathVariable("eventId") String eventId,
                          @PathVariable("channelIdentifier") String channelIdentifier) {

        return eventService.getEvent(channelIdentifier, Long.valueOf(eventId));
    }

    @GetMapping(value = "/{eventId}/sessions")
    public List<Session> getSessions(@PathVariable("eventId") String eventId,
                               @PathVariable("channelIdentifier") String channelIdentifier,
                               @RequestParam(value = "start_date[gte]", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime gte,
                               @RequestParam(value = "start_date[lte]", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime lte,
                               final @Valid @BindUsingJackson GenericRequest request) {

        return eventService.getSessions(gte, lte, request.getLimit(), request.getOffset(), channelIdentifier, Long.valueOf(eventId));
    }

    @GetMapping(value = "/{eventId}/sessions/{sessionId}")
    public Session getSession(@PathVariable("eventId") String eventId,
                            @PathVariable("sessionId") String sessionId,
                            @PathVariable("channelIdentifier") String channelIdentifier) {

        return eventService.getSession(channelIdentifier, Long.valueOf(eventId), Long.valueOf(sessionId));
    }
}
