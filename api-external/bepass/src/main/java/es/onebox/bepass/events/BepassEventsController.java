package es.onebox.bepass.events;

import es.onebox.bepass.datasources.bepass.dto.EventResponse;
import es.onebox.bepass.events.dto.CreateEventDTO;
import es.onebox.bepass.events.dto.EventDTO;
import es.onebox.bepass.events.dto.UpdateEventDTO;
import es.onebox.common.config.ApiConfig;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping(ApiConfig.BepassApiConfig.BASE_URL + "/events")
@RestController
public class BepassEventsController {

    private final BepassEventsService eventsService;

    public BepassEventsController(BepassEventsService eventsService) {
        this.eventsService = eventsService;
    }

    @PostMapping
    public EventResponse create(@Valid @RequestBody CreateEventDTO body) {
        return eventsService.createEvent(body);
    }

    @PutMapping("/{id}")
    public EventResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateEventDTO body) {
        return eventsService.updateEvent(id, body);
    }

    @GetMapping
    public List<EventDTO> search() {
        return eventsService.searchEvents();
    }
}
