package es.onebox.atm.email.controller;

import es.onebox.atm.email.service.AtmEmailService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.Role;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
@Validated
@RequestMapping(value = AtmEmailController.BASE_URI)
public class AtmEmailController {
    public static final String BASE_URI = ApiConfig.ATMApiConfig.BASE_URL + "/orders/{orderCode}/send-external-email";

    private final AtmEmailService atmEmailService;

    @Autowired
    public AtmEmailController(AtmEmailService atmEmailService) {
        this.atmEmailService = atmEmailService;
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @PostMapping()
    public ResponseEntity<Serializable> sendExternalEmail(@PathVariable("orderCode") @NotNull String orderCode) {
        atmEmailService.sendExternalEmail(orderCode);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}