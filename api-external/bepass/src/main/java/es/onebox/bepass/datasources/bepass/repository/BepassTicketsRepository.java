package es.onebox.bepass.datasources.bepass.repository;

import es.onebox.bepass.auth.BepassAuthService;
import es.onebox.bepass.datasources.bepass.BepassEventsDatasource;
import es.onebox.bepass.datasources.bepass.dto.Ticket;
import es.onebox.bepass.datasources.bepass.dto.CreateTicketResponse;
import es.onebox.bepass.datasources.bepass.dto.TicketsResponse;
import es.onebox.bepass.datasources.bepass.dto.UpdateTicketResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BepassTicketsRepository {

    private final BepassEventsDatasource bepassDatasource;
    private final BepassAuthService authService;

    public BepassTicketsRepository(BepassEventsDatasource bepassDatasource, BepassAuthService authService) {
        this.bepassDatasource = bepassDatasource;
        this.authService = authService;
    }

    public CreateTicketResponse addTicket(List<Ticket> body) {
        String token = this.authService.getToken();
        return bepassDatasource.addTicket(token, body);
    }

    public UpdateTicketResponse updateTicket(Ticket body) {
        String token = this.authService.getToken();
        return bepassDatasource.updateTicket(token, body);
    }

    public TicketsResponse getRawTickets(Long page) {
        String token = this.authService.getToken();
        return this.bepassDatasource.searchTickets(token, page);
    }

    public TicketsResponse getEventTickets(String eventId, Long page) {
        String token = this.authService.getToken();
        return this.bepassDatasource.searchEventTickets(token, eventId, page);
    }

}
