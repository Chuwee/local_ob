package es.onebox.eci.ticketsales.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.eci.common.GenericRequest;
import es.onebox.eci.ticketsales.dto.Order;
import es.onebox.eci.ticketsales.service.TicketSalesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping(ApiConfig.ECIApiConfig.BASE_URL)
public class TicketSalesController {

    private final TicketSalesService ticketSalesService;

    @Autowired
    public TicketSalesController(TicketSalesService ticketSalesService) {
        this.ticketSalesService = ticketSalesService;
    }

    @GetMapping(value = "/{channelIdentifier}/ticket-sales")
    public List<Order> getTicketSalesByChannel(@RequestParam(value = "order_date[gte]", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime orderGTE,
                                               @RequestParam(value = "order_date[lte]", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime orderLTE,
                                               @RequestParam(value = "event_identifier", required = false) String eventId,
                                               @RequestParam(value = "session_start_date[gte]", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime sessionGTE,
                                               @RequestParam(value = "session_start_date[lte]", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime sessionLTE,
                                               @PathVariable("channelIdentifier") String channelIdentifier,
                                               final @Valid @BindUsingJackson GenericRequest request) {
        return ticketSalesService.getTicketSales(orderGTE, orderLTE, eventId, sessionGTE, sessionLTE, request.getLimit(),
                request.getOffset(), channelIdentifier);
    }
}
