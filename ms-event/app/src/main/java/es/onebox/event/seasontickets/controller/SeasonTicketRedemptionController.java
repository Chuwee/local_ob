package es.onebox.event.seasontickets.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.seasontickets.dto.redemption.SeasonTicketRedemption;
import es.onebox.event.seasontickets.service.redemption.SeasonTicketRedemptionService;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(SeasonTicketRedemptionController.BASE_URI)
public class SeasonTicketRedemptionController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";
    private static final String SEASON_TICKET_ID = "/{seasonTicketId}";
    private static final String REDEMPTION = "/redemption";
    private static final String SEASON_TICKET_REDEMPTION = SEASON_TICKET_ID + REDEMPTION;

    private final SeasonTicketRedemptionService service;

    public SeasonTicketRedemptionController(SeasonTicketRedemptionService seasonTicketRedemptionService) {
        this.service = seasonTicketRedemptionService;
    }

    @RequestMapping(method = GET, value = SEASON_TICKET_REDEMPTION)
    public SeasonTicketRedemption getSeasonTicketRedemption(
            @PathVariable(value = "seasonTicketId") @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        return service.getSeasonTicketRedemption(seasonTicketId);
    }

    @RequestMapping(method = PUT, value = SEASON_TICKET_REDEMPTION)
    public void updateSeasonTicketRedemption(
            @PathVariable(value = "seasonTicketId") @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
            @RequestBody SeasonTicketRedemption seasonTicketRedemption) {
        service.updateSeasonTicketRedemption(seasonTicketId, seasonTicketRedemption);
    }
}
