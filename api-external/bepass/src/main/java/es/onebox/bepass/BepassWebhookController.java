package es.onebox.bepass;

import es.onebox.bepass.tickets.dto.NotificationMessageDTO;
import es.onebox.bepass.tickets.service.BepassTicketsService;
import es.onebox.bepass.users.BepassUsersService;
import es.onebox.bepass.users.dto.UserValidationNotificationDTO;
import es.onebox.common.config.ApiConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.BepassApiConfig.BASE_URL)
public class BepassWebhookController {

    private final BepassTicketsService ticketService;
    private final BepassUsersService usersService;

    public BepassWebhookController(BepassTicketsService ticketService, BepassUsersService usersService) {
        this.ticketService = ticketService;
        this.usersService = usersService;
    }

    @PostMapping("/webhook")
    public void postTicket(@RequestHeader("ob-action") String action, @RequestHeader("ob-event") String event, @RequestBody NotificationMessageDTO body) {
        ticketService.sendTicket(action, event, body);
    }

    @PostMapping(path = "/users/webhook")
    public void postback(@RequestBody UserValidationNotificationDTO body) {
        usersService.postback(body);
    }
}
