package es.onebox.eci.tickets.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.tickets.dto.OrderPrintDTO;
import es.onebox.common.tickets.dto.OrderPrintRequest;
import es.onebox.eci.tickets.service.ECITicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(ApiConfig.ECIApiConfig.BASE_URL)
public class TicketController {

    private final ECITicketService eciTicketService;

    @Autowired
    public TicketController(ECITicketService eciTicketService) {
        this.eciTicketService = eciTicketService;
    }

    @GetMapping(value = "/orders/{code}/print")
    public ResponseEntity<OrderPrintDTO> generateTicket(@PathVariable String code) {
        OrderPrintDTO orderPrintDTO = eciTicketService.print(code, null);
        if (orderPrintDTO != null) {
            return new ResponseEntity<>(orderPrintDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/orders/{code}/print")
    public ResponseEntity<OrderPrintDTO> generateTicket(@PathVariable String code, @RequestBody OrderPrintRequest request) {
        OrderPrintDTO orderPrintDTO = eciTicketService.print(code, request);
        if (orderPrintDTO != null) {
            return new ResponseEntity<>(orderPrintDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
