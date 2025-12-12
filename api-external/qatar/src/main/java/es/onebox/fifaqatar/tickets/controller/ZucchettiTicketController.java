package es.onebox.fifaqatar.tickets.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.fifaqatar.tickets.dto.ZucchettiTicket;
import es.onebox.fifaqatar.tickets.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("zucchettiTicketController")
@RequestMapping(ApiConfig.ZucchettiApiConfig.BASE_URL + "/sessions/{sessionId}")
public class ZucchettiTicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping(value = "/whitelist")
    public List<ZucchettiTicket> getTickets(@PathVariable Long sessionId) {
        return ticketService.getWhitelist(sessionId);
    }
}
