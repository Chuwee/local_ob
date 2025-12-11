/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.query.events;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.query.events.dto.EventQueryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author ignasi
 */
@RestController
@RequestMapping(ApiConfig.BASE_URL + "/query")
public class EventsQueryController {

    private final EventsQueryService eventsQueryService;

    @Autowired
    public EventsQueryController(EventsQueryService eventsQueryService) {
        this.eventsQueryService = eventsQueryService;
    }

    @RequestMapping(method = GET,
            value = "/events")
    public Object getEvents() {
        throw new UnsupportedOperationException("TODO");
    }

    @RequestMapping(method = GET,
            value = "/events/{eventId}")
    public EventQueryDTO getEvent(@PathVariable Long eventId) {
        return this.eventsQueryService.getEvent(eventId);
    }

    @RequestMapping(method = GET,
            value = "/events/{eventId}/sessions")
    public Object getEventSessions(@PathVariable Long eventId) {
        throw new UnsupportedOperationException("TODO");
    }

    @RequestMapping(method = GET,
            value = "/events/{eventId}/sessions/{sessionId}")
    public Object getSession(@PathVariable Long eventId, @PathVariable Long sessionId) {
        throw new UnsupportedOperationException("TODO");
    }
}
