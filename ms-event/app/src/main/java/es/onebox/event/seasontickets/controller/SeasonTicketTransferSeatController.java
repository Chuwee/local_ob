package es.onebox.event.seasontickets.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.seasontickets.dto.transferseat.SeasonTicketTransferConfigDTO;
import es.onebox.event.seasontickets.dto.transferseat.SeasonTicketTransferConfigUpdateDTO;
import es.onebox.event.seasontickets.service.transferseat.SeasonTicketTransferSeatService;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(SeasonTicketTransferSeatController.BASE_URI)
public class SeasonTicketTransferSeatController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";
    private static final String SEASON_TICKET_ID = "/{seasonTicketId}";
    private static final String TRANSFER_SEAT = "/transfer-seat";
    private static final String SEASON_TICKET_TRANSFER_SEAT = SEASON_TICKET_ID + TRANSFER_SEAT;

    private final SeasonTicketTransferSeatService service;

    public SeasonTicketTransferSeatController(SeasonTicketTransferSeatService seasonTicketTransferSeatService) {
        this.service = seasonTicketTransferSeatService;
    }

    @RequestMapping(method = GET, value = SEASON_TICKET_TRANSFER_SEAT)
    public SeasonTicketTransferConfigDTO getSeasonTicketTransferSeat(@Min(value = 1, message = "seasonTicketId must be above 0")
                                                                         @PathVariable(value = "seasonTicketId") Long seasonTicketId) {
        return service.getSeasonTicketTransferSeat(seasonTicketId);
    }

    @RequestMapping(method = PUT, value = SEASON_TICKET_TRANSFER_SEAT)
    public void updateSeasonTicketTransferSeat(@PathVariable(value = "seasonTicketId")
                                               @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                               @RequestBody SeasonTicketTransferConfigUpdateDTO seasonTicketTransferSeat) {
        service.updateSeasonTicketTransferSeat(seasonTicketId, seasonTicketTransferSeat);
    }
}
