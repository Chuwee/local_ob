package es.onebox.mgmt.seasontickets.controller;

import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.seasontickets.dto.redemption.UpdateSeasonTicketRedemptionDTO;
import es.onebox.mgmt.seasontickets.dto.redemption.SeasonTicketRedemptionConfigDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketRedemptionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(SeasonTicketRedemptionController.BASE_URI)
public class SeasonTicketRedemptionController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";
    private static final String SEASON_TICKET_ID = "/{seasonTicketId}";
    private static final String REDEMPTION = "/redemption";
    private static final String SEASON_TICKET_REDEMPTION = SEASON_TICKET_ID + REDEMPTION;

    private final SeasonTicketRedemptionService seasonTicketRedemptionService;

    public SeasonTicketRedemptionController(SeasonTicketRedemptionService seasonTicketRedemptionService) {
        this.seasonTicketRedemptionService = seasonTicketRedemptionService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(SEASON_TICKET_REDEMPTION)
    public SeasonTicketRedemptionConfigDTO getSeasonTicketRedemption(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        return seasonTicketRedemptionService.getSeasonTicketRedemption(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(SEASON_TICKET_REDEMPTION)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicketRedemption(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
            @Valid @RequestBody UpdateSeasonTicketRedemptionDTO request) {
        seasonTicketRedemptionService.updateSeasonTicketRedemption(seasonTicketId, request);
    }
}
