package es.onebox.fifaqatar.tickets.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.datasources.accesscontrol.dto.TicketFilter;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.fifaqatar.tickets.dto.HayyaTicketResponse;
import es.onebox.fifaqatar.tickets.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController("hayyaTicketController")
@RequestMapping(ApiConfig.HayyaApiConfig.BASE_URL + "/tickets")
public class HayyaTicketController {
    @Autowired
    private TicketService ticketService;

    @GetMapping()
    public HayyaTicketResponse getTickets(@BindUsingJackson @Valid TicketFilter filter) {
        return ticketService.getTickets(filter);
    }
}
