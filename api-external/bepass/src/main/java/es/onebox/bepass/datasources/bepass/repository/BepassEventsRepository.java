package es.onebox.bepass.datasources.bepass.repository;

import es.onebox.bepass.auth.BepassAuthService;
import es.onebox.bepass.datasources.bepass.BepassEventsDatasource;
import es.onebox.bepass.datasources.bepass.dto.CreateEventRequest;
import es.onebox.bepass.datasources.bepass.dto.Event;
import es.onebox.bepass.datasources.bepass.dto.EventResponse;
import es.onebox.bepass.datasources.bepass.dto.UpdateEventRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BepassEventsRepository {

    private final BepassEventsDatasource bepassDatasource;
    private final BepassAuthService authService;


    public BepassEventsRepository(BepassEventsDatasource bepassDatasource, BepassAuthService authService) {
        this.bepassDatasource = bepassDatasource;
        this.authService = authService;
    }

    public List<Event> getEvents() {
        String token = this.authService.getToken();
        return bepassDatasource.getEvents(token);
    }

    public EventResponse createEvent(CreateEventRequest body) {
        String token = this.authService.getToken();
        return bepassDatasource.createEvent(token, body);
    }

    public EventResponse updateEvent(String eventId, UpdateEventRequest body) {
        String token = this.authService.getToken();
        return bepassDatasource.updateEvent(token, eventId, body);
    }

}
