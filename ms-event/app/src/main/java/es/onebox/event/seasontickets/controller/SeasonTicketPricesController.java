package es.onebox.event.seasontickets.controller;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.seasontickets.dto.SeasonTicketPriceDTO;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketPriceDTO;
import es.onebox.event.seasontickets.service.SeasonTicketPricesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(SeasonTicketPricesController.SEASON_TICKET_PRICES)
public class SeasonTicketPricesController {

    public static final String SEASON_TICKET_PRICES = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/prices";

    private SeasonTicketPricesService seasonTicketPricesService;

    @Autowired
    public SeasonTicketPricesController(SeasonTicketPricesService seasonTicketPricesService) {
        this.seasonTicketPricesService = seasonTicketPricesService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<SeasonTicketPriceDTO> getPrices(@PathVariable("seasonTicketId") Long seasonTicketId) {
        checkInputParameters(seasonTicketId);
        return seasonTicketPricesService.getPrices(seasonTicketId);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updatePrices(@PathVariable("seasonTicketId") Long seasonTicketId,
                                          @RequestBody UpdateSeasonTicketPriceDTO[] prices) {
        checkInputParameters(seasonTicketId);
        seasonTicketPricesService.updatePrices(seasonTicketId, Arrays.asList(prices));
    }

    private void checkInputParameters(@PathVariable("seasonTicketId") Long seasonTicketId) {
        if (seasonTicketId == null || seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
    }
}
