package es.onebox.ms.notification.webhooks;

import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.ms.notification.config.ApiConfig;
import es.onebox.ms.notification.webhooks.dto.CreateNotificationConfigDTO;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigDTO;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigsDTO;
import es.onebox.ms.notification.webhooks.dto.SearchNotificationConfigFilterDTO;
import es.onebox.ms.notification.webhooks.dto.UpdateNotificationConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
@RequestMapping(value = WebhookController.BASE_URI)
public class WebhookController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/webhooks";

    private final WebhookService webhookService;

    @Autowired
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @GetMapping(value = "/{documentId}")
    public NotificationConfigDTO getNotificationConfig(@PathVariable String documentId) {
        return webhookService.getNotificationConfig(documentId);
    }

    @GetMapping
    public NotificationConfigsDTO getNotificationConfigs(@BindUsingJackson SearchNotificationConfigFilterDTO filter) {
        return webhookService.getNotificationConfigs(filter);
    }

    @PostMapping
    public NotificationConfigDTO createNotificationConfig(@RequestBody CreateNotificationConfigDTO createDTO) {
        return webhookService.createNotificationConfig(createDTO);
    }

    @PutMapping(value = "/{documentId}")
    public ResponseEntity<Serializable> updateNotificationConfig(@PathVariable String documentId, @RequestBody UpdateNotificationConfigDTO updateDTO) {
        webhookService.updateNotificationConfig(documentId, updateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{documentId}")
    public ResponseEntity<Serializable> deleteNotificationConfig(@PathVariable String documentId) {
        webhookService.deleteNotificationConfig(documentId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping(value = "/{documentId}/apikey/regenerate")
    public NotificationConfigDTO regenerateApiKey(@PathVariable String documentId) {
        return webhookService.regenerateApiKey(documentId);
    }

}
