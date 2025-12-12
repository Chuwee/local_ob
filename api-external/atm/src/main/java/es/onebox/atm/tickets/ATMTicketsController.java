package es.onebox.atm.tickets;

import es.onebox.atm.tickets.dto.TicketURLContentDTO;
import es.onebox.common.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@Validated
@RequestMapping(value = ATMTicketsController.BASE_URI)
public class ATMTicketsController {
    public static final String BASE_URI = ApiConfig.ATMApiConfig.BASE_URL + "/orders/{orderCode}";

    private final ATMTicketService atmTicketService;

    @Autowired
    public ATMTicketsController(ATMTicketService atmTicketService) {
        this.atmTicketService = atmTicketService;
    }

    @GetMapping(value = "/tickets-url")
    public TicketURLContentDTO getTicketsURLContent(@PathVariable("orderCode") String orderCode) {
        return atmTicketService.getTicketsURLContent(orderCode);
    }

    @GetMapping(value = "/tickets/{verificationHash}")
    public RedirectView getTickets(@PathVariable("orderCode") String orderCode,
                                   @PathVariable("verificationHash") String verificationHash) {
        return atmTicketService.getTickets(orderCode, verificationHash);
    }

    @GetMapping(value = "/items/{itemId}/tickets/{verificationHash}")
    public RedirectView getSingleTicketsURLContent(@PathVariable("orderCode") String orderCode,
                                                   @PathVariable("itemId") Long productId,
                                                   @PathVariable("verificationHash") String verificationHash) {
        return atmTicketService.getTicket(orderCode, productId, verificationHash);
    }

}