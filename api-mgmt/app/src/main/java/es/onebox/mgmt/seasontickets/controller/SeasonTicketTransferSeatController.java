package es.onebox.mgmt.seasontickets.controller;

import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.seasontickets.dto.transferseat.SeasonTicketTransferUpdateDTO;
import es.onebox.mgmt.seasontickets.dto.transferseat.SeasonTicketTransferDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketTransferSeatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(SeasonTicketTransferSeatController.BASE_URI)
public class SeasonTicketTransferSeatController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";
    private static final String SEASON_TICKET_ID = "/{seasonTicketId}";
    private static final String TRANSFER_SEAT = "/transfer-seat";
    private static final String SEASON_TICKET_TRANSFER_SEAT = SEASON_TICKET_ID + TRANSFER_SEAT;

    private final SeasonTicketTransferSeatService seasonTicketTransferSeatService;

    public SeasonTicketTransferSeatController(SeasonTicketTransferSeatService seasonTicketTransferSeatService) {
        this.seasonTicketTransferSeatService = seasonTicketTransferSeatService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(SEASON_TICKET_TRANSFER_SEAT)
    public SeasonTicketTransferDTO getSeasonTicketTransferSeat(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        return seasonTicketTransferSeatService.getSeasonTicketTransferSeat(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(SEASON_TICKET_TRANSFER_SEAT)
    public void updateSeasonTicketTransferSeat(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
            @Valid @RequestBody SeasonTicketTransferUpdateDTO request) {
        seasonTicketTransferSeatService.updateSeasonTicketTransferSeat(seasonTicketId, request);
    }
}
