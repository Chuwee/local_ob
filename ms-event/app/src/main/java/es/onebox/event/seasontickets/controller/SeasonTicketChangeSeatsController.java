package es.onebox.event.seasontickets.controller;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.seasontickets.dto.changeseat.ChangeSeatSeasonTicketPriceFilter;
import es.onebox.event.seasontickets.dto.changeseat.ChangeSeatSeasonTicketPriceRelationDTO;
import es.onebox.event.seasontickets.dto.changeseat.ChangeSeatSeasonTicketPriceRelations;
import es.onebox.event.seasontickets.dto.changeseat.SeasonTicketChangeSeatDTO;
import es.onebox.event.seasontickets.dto.changeseat.UpdateChangeSeatSeasonTicketPriceRelations;
import es.onebox.event.seasontickets.dto.changeseat.UpdateSeasonTicketChangeSeat;
import es.onebox.event.seasontickets.service.changeseats.SeasonTicketChangeSeatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(SeasonTicketChangeSeatsController.BASE_URI)
public class SeasonTicketChangeSeatsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";
    private static final String SEASON_TICKET_ID = "/{seasonTicketId}";
    private static final String CHANGE_SEATS = "/change-seats";
    private static final String SEASON_TICKET_CHANGE_SEATS = SEASON_TICKET_ID + CHANGE_SEATS;
    private static final String PRICES = "/prices";
    private static final String SEASON_TICKET_CHANGE_SEATS_PRICES = SEASON_TICKET_CHANGE_SEATS + PRICES;

    private final SeasonTicketChangeSeatsService seasonTicketChangeSeatsService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public SeasonTicketChangeSeatsController(SeasonTicketChangeSeatsService seasonTicketChangeSeatsService,
                                             RefreshDataService refreshDataService) {
        this.seasonTicketChangeSeatsService = seasonTicketChangeSeatsService;
        this.refreshDataService = refreshDataService;
    }

    @RequestMapping(method = POST, value = SEASON_TICKET_CHANGE_SEATS_PRICES)
    public void createChangeSeatPricesRelations(@PathVariable(value = "seasonTicketId") @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                @Valid @RequestBody ChangeSeatSeasonTicketPriceRelations changeSeatSeasonTicketPriceRelations) {
        seasonTicketChangeSeatsService.createChangeSeatPricesRelations(seasonTicketId, changeSeatSeasonTicketPriceRelations);
        refreshDataService.refreshEvent(seasonTicketId, "changeSeatPrices", EventIndexationType.SEASON_TICKET);
    }

    @RequestMapping(method = GET, value = SEASON_TICKET_CHANGE_SEATS_PRICES)
    public List<ChangeSeatSeasonTicketPriceRelationDTO> searchChangeSeatPricesTable(@PathVariable(value = "seasonTicketId") @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                                                    @Valid ChangeSeatSeasonTicketPriceFilter seasonTicketPriceFilter) {
        return seasonTicketChangeSeatsService.searchChangeSeatPricesTable(seasonTicketId, seasonTicketPriceFilter);
    }

    @RequestMapping(method = PUT, value = SEASON_TICKET_CHANGE_SEATS_PRICES)
    public void updateChangeSeatPricesRelations(@PathVariable(value = "seasonTicketId") @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                @Valid @RequestBody UpdateChangeSeatSeasonTicketPriceRelations updateChangeSeatSeasonTicketPriceRelations) {
        seasonTicketChangeSeatsService.updateChangeSeatPricesRelations(seasonTicketId, updateChangeSeatSeasonTicketPriceRelations);
        refreshDataService.refreshEvent(seasonTicketId, "changeSeatPrices", EventIndexationType.SEASON_TICKET);
    }

    @RequestMapping(method = GET, value = SEASON_TICKET_CHANGE_SEATS)
    public SeasonTicketChangeSeatDTO getSeasonTicketChangeSeat(@PathVariable(value = "seasonTicketId")
                                                               @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        return seasonTicketChangeSeatsService.getSeasonTicketChangeSeat(seasonTicketId.intValue());
    }

    @RequestMapping(method = PUT, value = SEASON_TICKET_CHANGE_SEATS)
    public void updateSeasonTicketChangeSeat(@PathVariable(value = "seasonTicketId")
                                             @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                             @RequestBody UpdateSeasonTicketChangeSeat updateSeasonTicketChangeSeat) {
        seasonTicketChangeSeatsService.updateSeasonTicketChangeSeat(seasonTicketId.intValue(), updateSeasonTicketChangeSeat);
        refreshDataService.refreshEvent(seasonTicketId, "changeSeat", EventIndexationType.SEASON_TICKET);
    }
}
