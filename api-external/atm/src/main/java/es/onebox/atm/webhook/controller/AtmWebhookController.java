package es.onebox.atm.webhook.controller;

import es.onebox.atm.cart.ATMVendorConstants;
import es.onebox.atm.webhook.service.AtmWebhookService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.datasources.webhook.dto.OrderNotificationMessageDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
@Validated
@RequestMapping(value = AtmWebhookController.BASE_URI)
public class AtmWebhookController {
    public static final String BASE_URI = ApiConfig.ATMApiConfig.BASE_URL + "/webhook";

    private final AtmWebhookService atmWebhookService;

    @Autowired
    public AtmWebhookController(AtmWebhookService atmWebhookService) {
        this.atmWebhookService = atmWebhookService;
    }

    @PostMapping()
    public ResponseEntity<Serializable> webhookNotification(HttpServletRequest request) {
        atmWebhookService.webhookNotification(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // WARNING! This endpoint is for manual use only! It does not calculate the hash correctly
    @GetMapping("/orders/{orderCode}/body")
    public OrderNotificationMessageDTO getATMWebhookOrderBody(@PathVariable("orderCode") String orderCode) throws Exception {
        return atmWebhookService.getATMWebhookMessage(orderCode, ATMVendorConstants.TICKETING_ORDER_TYPE);
    }

    // WARNING! This endpoint is for manual use only! It does not calculate the hash correctly
    @GetMapping("/member-orders/{orderCode}/body")
    public OrderNotificationMessageDTO getATMWebhookMemberOrderBody(@PathVariable("orderCode") String orderCode) throws Exception {
        return atmWebhookService.getATMWebhookMessage(orderCode, ATMVendorConstants.MEMBER_ORDER_TYPE);
    }
}