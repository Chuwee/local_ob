package es.onebox.event.loyaltypoints.sessions.controller;

import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.loyaltypoints.sessions.dto.SessionLoyaltyPointsConfigDTO;
import es.onebox.event.loyaltypoints.sessions.dto.UpdateSessionLoyaltyPointsConfigDTO;
import es.onebox.event.loyaltypoints.sessions.service.SessionLoyaltyPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/loyalty-points")
public class SessionLoyaltyPointsController {

    private static final String ORIGIN = "updateSessionLoyaltyPointsConfig";

    private final SessionLoyaltyPointsService sessionLoyaltyPointsService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public SessionLoyaltyPointsController(SessionLoyaltyPointsService sessionLoyaltyPointsService, RefreshDataService refreshDataService) {
        this.sessionLoyaltyPointsService = sessionLoyaltyPointsService;
        this.refreshDataService = refreshDataService;
    }

    @GetMapping()
    public SessionLoyaltyPointsConfigDTO getSessionLoyaltyPointsConfig(@PathVariable Long eventId, @PathVariable Long sessionId) {
        return sessionLoyaltyPointsService.getSessionLoyaltyPointsConfig(sessionId);
    }

    @PutMapping()
    public void updateSessionLoyaltyPointsConfig(@PathVariable Long eventId,
                                                 @PathVariable Long sessionId,
                                                 @RequestBody @Valid UpdateSessionLoyaltyPointsConfigDTO request) {
        sessionLoyaltyPointsService.updateSessionLoyaltyPointsConfig(sessionId, request);
        refreshDataService.refreshSession(sessionId, ORIGIN);
    }
}
