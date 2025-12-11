package es.onebox.event.sessions;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.sessions.domain.sessionconfig.DigitalTicketMode;
import es.onebox.event.sessions.service.ExternalTicketModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.BASE_URL)
public class ExternalTicketModeController {


    private final ExternalTicketModeService externalTicketModeService;

    @Autowired
    public ExternalTicketModeController(ExternalTicketModeService externalTicketModeService) {
        this.externalTicketModeService = externalTicketModeService;
    }

    @GetMapping("/entities/{entityId}/events/{eventId}/sessions/{sessionId}/external-ticket-mode")
    public DigitalTicketMode getDigitalTicketMode(@PathVariable(value = "entityId") Integer entityId, @PathVariable(value = "eventId") Long eventId,
                                                  @PathVariable(value = "sessionId") Long sessionId) {
        return externalTicketModeService.getDigitalTicketMode(entityId, eventId, sessionId);
    }


}
