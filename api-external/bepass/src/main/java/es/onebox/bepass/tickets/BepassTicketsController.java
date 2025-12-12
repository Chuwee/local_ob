package es.onebox.bepass.tickets;

import es.onebox.bepass.datasources.bepass.dto.CreateTicketResponse;
import es.onebox.bepass.datasources.bepass.dto.TicketsResponse;
import es.onebox.bepass.tickets.service.BepassTicketsService;
import es.onebox.common.config.ApiConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.BepassApiConfig.BASE_URL)
public class BepassTicketsController {

    private final BepassTicketsService bepassTicketsService;

    public BepassTicketsController(BepassTicketsService bepassTicketsService) {
        this.bepassTicketsService = bepassTicketsService;
    }

    @GetMapping("/tickets")
    public TicketsResponse get(@RequestParam(required = false) Long page) {
        return bepassTicketsService.searchTickets(page);
    }

    @GetMapping("/events/{externalEventId}/tickets")
    public TicketsResponse getEventTickets(@PathVariable("externalEventId") String externalEventId, @RequestParam(required = false) Long page) {
        return bepassTicketsService.searchTicketsByEvent(externalEventId, page);
    }

    @PostMapping("/orders/{code}")
    public CreateTicketResponse create(@PathVariable("code") String code) {
        return bepassTicketsService.sendTicket(code);
    }

}
