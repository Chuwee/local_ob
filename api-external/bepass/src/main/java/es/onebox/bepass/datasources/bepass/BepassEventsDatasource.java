package es.onebox.bepass.datasources.bepass;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.bepass.datasources.bepass.config.BepassConfig;
import es.onebox.bepass.datasources.bepass.dto.CreateEventRequest;
import es.onebox.bepass.datasources.bepass.dto.CreateTicketResponse;
import es.onebox.bepass.datasources.bepass.dto.Event;
import es.onebox.bepass.datasources.bepass.dto.EventResponse;
import es.onebox.bepass.datasources.bepass.dto.Ticket;
import es.onebox.bepass.datasources.bepass.dto.TicketsResponse;
import es.onebox.bepass.datasources.bepass.dto.UpdateEventRequest;
import es.onebox.bepass.datasources.bepass.dto.UpdateTicketResponse;
import es.onebox.bepass.exception.BepassErrorCode;
import es.onebox.bepass.exception.BepassHttpExceptionBuilder;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.stereotype.Component;

import javax.inject.Qualifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BepassEventsDatasource extends BepassDatasource {

    private final HttpClient httpClient;

    private static final String EVENTS_ALL = "/event/all";
    private static final String EVENT = "/event";
    private static final String TICKETS = "/tickets";
    private static final String TICKET = TICKETS + "/{ticketId}";

    private static final String EVENT_TICKETS = "/tickets/event/{eventId}";

    private static final Map<String, BepassErrorCode> ERROR_CODES = new HashMap<>();

    public BepassEventsDatasource(BepassConfig config,
                                  TracingInterceptor bepassTracingInterceptor,
                                  ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .connectTimeout(CONNECTION_TIMEOUT)
                .readTimeout(READ_TIMEOUT)
                .interceptors(bepassTracingInterceptor)
                .baseUrl(config.getEvents().getUrl())
                .jacksonMapper(jacksonMapper)
                .exceptionBuilder(new BepassHttpExceptionBuilder(jacksonMapper, ERROR_CODES))
                .build();
    }

    public List<Event> getEvents(String token) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS_ALL)
                .headers(prepareHeaders(token))
                .execute(ListType.of(Event.class));
    }

    public EventResponse createEvent(String token, CreateEventRequest body) {
        return httpClient.buildRequest(HttpMethod.POST, EVENT)
                .headers(prepareHeaders(token))
                .body(new ClientRequestBody(body))
                .execute(EventResponse.class);
    }

    public EventResponse updateEvent(String token, String eventId, UpdateEventRequest body) {
        return httpClient.buildRequest(HttpMethod.PUT, EVENT + "/{eventId}")
                .headers(prepareHeaders(token))
                .pathParams(eventId)
                .body(new ClientRequestBody(body))
                .execute(EventResponse.class);
    }

    public CreateTicketResponse addTicket(String token, List<Ticket> ticketsRequest) {
        return httpClient.buildRequest(HttpMethod.POST, TICKETS)
                .headers(prepareHeaders(token))
                .body(new ClientRequestBody(ticketsRequest))
                .execute(CreateTicketResponse.class);
    }

    public UpdateTicketResponse updateTicket(String token, Ticket ticketsRequest) {
        return httpClient.buildRequest(HttpMethod.PUT, TICKET)
                .headers(prepareHeaders(token))
                .pathParams(ticketsRequest.getExternalId())
                .body(new ClientRequestBody(ticketsRequest))
                .execute(UpdateTicketResponse.class);
    }

    public TicketsResponse searchTickets(String token, Long page) {
        QueryParameters params = new QueryParameters.Builder().addQueryParameter("page", page).build();
        return httpClient.buildRequest(HttpMethod.GET, TICKETS)
                .headers(prepareHeaders(token))
                .params(params)
                .execute(TicketsResponse.class);
    }

    public TicketsResponse searchEventTickets(String token, String eventId, Long page) {
        QueryParameters params = new QueryParameters.Builder().addQueryParameter("page", page).build();
        return httpClient.buildRequest(HttpMethod.GET, EVENT_TICKETS)
                .headers(prepareHeaders(token))
                .params(params)
                .pathParams(eventId)
                .execute(TicketsResponse.class);
    }
}
