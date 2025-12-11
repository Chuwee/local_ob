package es.onebox.mgmt.loyaltypoints.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.loyaltypoints.seasontickets.dto.SeasonTicketLoyaltyPointsConfigDTO;
import es.onebox.mgmt.loyaltypoints.seasontickets.service.SeasonTicketLoyaltyPointsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static es.onebox.core.security.Roles.Codes.*;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = SeasonTicketLoyaltyPointsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class SeasonTicketLoyaltyPointsController {
    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/loyalty-points";
    private static final String AUDIT_SEASON_TICKET_LOYALTY_POINTS = "SEASON_TICKET_LOYALTY_POINTS";

    private final SeasonTicketLoyaltyPointsService seasonTicketLoyaltyPointsService;

    @Autowired
    public SeasonTicketLoyaltyPointsController(SeasonTicketLoyaltyPointsService seasonTicketLoyaltyPointsService) {
        this.seasonTicketLoyaltyPointsService = seasonTicketLoyaltyPointsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public SeasonTicketLoyaltyPointsConfigDTO getSeasonTicketLoyaltyPoints(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_SEASON_TICKET_LOYALTY_POINTS, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketLoyaltyPointsService.getSeasonTicketLoyaltyPoints(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping
    public void updateSeasonTicketLoyaltyPoints(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
            @Valid @NotNull @RequestBody SeasonTicketLoyaltyPointsConfigDTO updateLoyaltyPointsConfigDTOS) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_SEASON_TICKET_LOYALTY_POINTS, AuditTag.AUDIT_ACTION_UPDATE);

        seasonTicketLoyaltyPointsService.updateSeasonTicketLoyaltyPoints(seasonTicketId, updateLoyaltyPointsConfigDTOS);
    }
}