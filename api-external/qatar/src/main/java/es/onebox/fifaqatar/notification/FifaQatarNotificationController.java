package es.onebox.fifaqatar.notification;

import es.onebox.common.config.ApiConfig;
import es.onebox.fifaqatar.notification.dto.request.BarcodeRequestDTO;
import es.onebox.fifaqatar.notification.dto.request.NotificationMessageDTO;
import es.onebox.fifaqatar.notification.mapping.entity.SessionBarcodeMapping;
import es.onebox.fifaqatar.notification.mapping.entity.SessionBarcodesMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = ApiConfig.QatarApiConfig.BASE_URL)
public class FifaQatarNotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FifaQatarNotificationController.class);

    private final FifaQatarNotificationService notificationService;

    public FifaQatarNotificationController(FifaQatarNotificationService notificationService) {
        this.notificationService = notificationService;
    }

//    @PostMapping("/v1/notifications/barcodes")
//    public void barcodes(@RequestBody BarcodeRequestDTO barcodeRequest) {
//        notificationService.migrateBarcodes(barcodeRequest);
//    }

    @PostMapping("/v1/webhooks")
    public void webhook(@RequestBody NotificationMessageDTO notificationMessage,
                        @RequestHeader(name = "Ob-Action", required = false) String action,
                        @RequestHeader(name = "Ob-Event", required = false) String event) {
        notificationService.attemptHook(notificationMessage, action, event);
    }

    @PostMapping("/v1/barcodes-import/sessions/{sessionId}")
    public void importBarcodes(@PathVariable Long sessionId) {
        notificationService.migrateSessionMappedBarcodes(sessionId);
    }

    @GetMapping("/v1/barcodes-import/sessions-mapping")
    public List<SessionBarcodeMapping> sessionsMapping(
            @RequestParam(required = false, value = "source-session-id") Long sourceSessionId,
            @RequestParam(required = false, value = "destination-session-id") Long destinationSessionId
    ) {
        return notificationService.getSessionsMapping(sourceSessionId, destinationSessionId);
    }
}
