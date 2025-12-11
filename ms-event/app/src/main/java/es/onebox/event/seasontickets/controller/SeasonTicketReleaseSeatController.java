package es.onebox.event.seasontickets.controller;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.seasontickets.dto.releaseseat.SeasonTicketReleaseSeatDTO;
import es.onebox.event.seasontickets.service.releaseseat.SeasonTicketReleaseSeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Min;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(SeasonTicketReleaseSeatController.BASE_URI)
public class SeasonTicketReleaseSeatController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";
    private static final String SEASON_TICKET_ID = "/{seasonTicketId}";
    private static final String RELEASE_SEAT = "/release-seat";
    private static final String SEASON_TICKET_RELEASE_SEAT = SEASON_TICKET_ID + RELEASE_SEAT;

    private final SeasonTicketReleaseSeatService service;
    private final RefreshDataService refreshDataService;

    @Autowired
    public SeasonTicketReleaseSeatController(SeasonTicketReleaseSeatService seasonTicketReleaseSeatService, RefreshDataService refreshDataService) {
        this.service = seasonTicketReleaseSeatService;
        this.refreshDataService = refreshDataService;
    }

    @RequestMapping(method = GET, value = SEASON_TICKET_RELEASE_SEAT)
    public SeasonTicketReleaseSeatDTO getSeasonTicketReleaseSeat(@PathVariable(value = "seasonTicketId")
                                                               @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        return service.getSeasonTicketReleaseSeat(seasonTicketId);
    }

    @RequestMapping(method = PUT, value = SEASON_TICKET_RELEASE_SEAT)
    public void updateSeasonTicketReleaseSeat(@PathVariable(value = "seasonTicketId")
                                             @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                             @RequestBody SeasonTicketReleaseSeatDTO seasonTicketReleaseSeatDTO) {
        service.updateSeasonTicketReleaseSeat(seasonTicketId, seasonTicketReleaseSeatDTO);
        refreshDataService.refreshEvent(seasonTicketId, "updateReleaseSeat", EventIndexationType.SEASON_TICKET);
    }
}
