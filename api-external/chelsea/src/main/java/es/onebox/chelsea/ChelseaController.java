package es.onebox.chelsea;

import es.onebox.chelsea.dto.NotificationMessageDTO;
import es.onebox.common.config.ApiConfig;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(value = ApiConfig.ChelseaApiConfig.BASE_URL)
public class ChelseaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChelseaController.class);

    private static final String EVENT_ORDER = "ORDER";

    private final ChelseaService chelseaService;

    @Autowired
    public ChelseaController(ChelseaService chelseaService) {
        this.chelseaService = chelseaService;
    }

    @PostMapping("/webhook")
    public void registerOperation(@Valid @RequestBody NotificationMessageDTO notification,
                                  @RequestHeader(name = "Ob-Action") String action,
                                  @RequestHeader(name = "Ob-Event") String event) {

        String notificationKey = notification.getCode() != null ? notification.getCode() : notification.getMovementId();
        LOGGER.info("[CHELSEA WEBHOOK] Received notification: {} - {} - {}", notificationKey, event, action);

        try {
            if (EVENT_ORDER.equals(event)) {
                chelseaService.registerOperation(notification.getCode());
            }
        } catch (Exception e) {
            LOGGER.error("[CHELSEA WEBHOOK] Notification processing failed: {} - {} - {}", notificationKey, event, action);
            throw e;
        }
    }

}
