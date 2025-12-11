package es.onebox.event.sessions;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.sessions.dto.SessionSaleConstraintDTO;
import es.onebox.event.sessions.dto.UpdateSaleConstraintDTO;
import es.onebox.event.sessions.service.SessionSaleConstraintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SessionSaleConstraintController.SALE_CONSTRAINTS_URL)
public class SessionSaleConstraintController {

    public static final String SALE_CONSTRAINTS_URL = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/sale-constraints";

    @Autowired
    private SessionSaleConstraintService sessionSaleConstraintService;

    @GetMapping()
    public SessionSaleConstraintDTO getSaleConstraints(@PathVariable Long eventId, @PathVariable Long sessionId) {
        return sessionSaleConstraintService.getSaleConstraints(sessionId);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void upsertSaleConstraints(@PathVariable Long eventId, @PathVariable Long sessionId,
                                      @RequestBody UpdateSaleConstraintDTO request) {
        sessionSaleConstraintService.upsertSaleConstraints(eventId, sessionId, request);
    }
}
