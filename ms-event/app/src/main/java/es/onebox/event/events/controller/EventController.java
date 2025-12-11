package es.onebox.event.events.controller;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.CreateEventRequestDTO;
import es.onebox.event.events.dto.EventConfigDTO;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.dto.EventsDTO;
import es.onebox.event.events.dto.UpdateEventRequestDTO;
import es.onebox.event.events.request.EventSearchFilter;
import es.onebox.event.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public EventsDTO search(@Valid EventSearchFilter eventsFilter) {

        validateNotNull(eventsFilter);

        return eventService.searchEvents(eventsFilter);
    }

    @GetMapping("/{eventId:[0-9]+}")
    public EventDTO getEvent(@PathVariable(value = "eventId") Long eventId) {
        validateNotNull(eventId);
        return eventService.getEvent(eventId);
    }

    @GetMapping("/{eventId}/config")
    public EventConfigDTO getEventConfig(@PathVariable Long eventId) {
        return eventService.getEventConfig(eventId);
    }

   @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createEvent(@RequestBody CreateEventRequestDTO request) {
        validateNotNull(request, "request data is mandatory");

        if (request.getName() == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Field name is required", null);
        }
        if (request.getType() == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Field type is required", null);
        }
        if (request.getEntityId() == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Field entityId is required", null);
        }
        if (request.getProducerId() == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Field producerId is required", null);
        }
        if (request.getCategoryId() == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Field categoryId is required", null);
        }

        Long eventId = eventService.createEvent(request);
        eventService.postUpdateEvent(eventId, null, null);

        return new ResponseEntity<>(eventId, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{eventId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateEvent(@PathVariable Long eventId, @RequestBody @Valid UpdateEventRequestDTO request) {
        validateNotNull(request, "request data is mandatory");

        if (request.getId() == null) {
            request.setId(eventId);
        } else if (!request.getId().equals(eventId)) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Field request.id is not equal to current path eventId", null);
        }

        EventDTO oldEvent = eventService.getEvent(eventId);

        eventService.updateEvent(request);
        eventService.postUpdateEvent(eventId, oldEvent, request);
    }


    public static void validateNotNull(Object o, String message) {
        if (o == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, message, null);
        }
    }

    public static void validateNotNull(Object o) {
        validateNotNull(o, null);
    }

}
