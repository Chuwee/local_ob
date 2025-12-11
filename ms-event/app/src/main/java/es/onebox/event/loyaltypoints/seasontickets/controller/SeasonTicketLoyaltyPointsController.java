package es.onebox.event.loyaltypoints.seasontickets.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.loyaltypoints.seasontickets.dto.SeasonTicketLoyaltyPointsConfigDTO;
import es.onebox.event.loyaltypoints.seasontickets.service.SeasonTicketLoyaltyPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(SeasonTicketLoyaltyPointsController.BASE_URI)
public class SeasonTicketLoyaltyPointsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/loyalty-points";

    private final SeasonTicketLoyaltyPointsService service;

    @Autowired
    public SeasonTicketLoyaltyPointsController(SeasonTicketLoyaltyPointsService service) {
        this.service = service;
    }

    @GetMapping
    public SeasonTicketLoyaltyPointsConfigDTO getSeasonTicketLoyaltyPoints(@PathVariable(value = "seasonTicketId") @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        return service.getSeasonTicketLoyaltyPoints(seasonTicketId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicketLoyaltyPoints(@PathVariable(value = "seasonTicketId")
                                                @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                @Valid @NotNull(message = "Request body cannot be null") @RequestBody SeasonTicketLoyaltyPointsConfigDTO seasonTicketLoyaltyPointsConfigDTO) {
        service.updateSeasonTicketLoyaltyPoints(seasonTicketId, seasonTicketLoyaltyPointsConfigDTO);
    }
}
