package es.onebox.fever.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.datasources.webhook.dto.fever.FeverMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.NotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.fever.service.WebhookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ApiConfig.FeverApiConfig.BASE_URL)
@Validated
public class WebhookController {

    @Autowired
    private WebhookService webhookService;

    @PostMapping("/webhook")
    public void sendWebhookToFever(@RequestBody @Valid NotificationMessageDTO notification, HttpServletRequest request)
            throws JsonProcessingException {
        webhookService.sendWebhookToFever(new WebhookFeverDTO(notification, request, new FeverMessageDTO()));
    }
}
