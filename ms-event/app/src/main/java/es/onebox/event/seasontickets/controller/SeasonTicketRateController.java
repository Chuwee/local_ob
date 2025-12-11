package es.onebox.event.seasontickets.controller;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.CommonIdResponse;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.event.seasontickets.dto.SeasonTicketRateDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketRatesDTO;
import es.onebox.event.seasontickets.service.SeasonTicketRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

@RestController
@RequestMapping(SeasonTicketRateController.BASE_URI)
public class SeasonTicketRateController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/rates";

    private final SeasonTicketRateService seasonTicketRateService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public SeasonTicketRateController(SeasonTicketRateService seasonTicketRateService, RefreshDataService refreshDataService) {
        this.seasonTicketRateService = seasonTicketRateService;
        this.refreshDataService = refreshDataService;
    }

    @GetMapping()
    public SeasonTicketRatesDTO getSeasonTicketRates(RatesFilter filter, @PathVariable(value = "seasonTicketId") Integer seasonTicketId) {
        return seasonTicketRateService.findRatesBySeasonTicketId(seasonTicketId, filter);
    }

    @PostMapping()
    public CommonIdResponse createSeasonTicketRate(@RequestBody SeasonTicketRateDTO seasonTicketRateDTO,
                                                   @PathVariable(value = "seasonTicketId") Integer seasonTicketId) {

        CommonIdResponse response =  seasonTicketRateService.createSeasonTicketRate(seasonTicketId, seasonTicketRateDTO);
        refreshDataService.refreshEvent(seasonTicketId.longValue(), "createSeasonTicketRate");
        return response;
    }

    @PutMapping()
    public void updateSeasonTicketRates(@RequestBody SeasonTicketRateDTO[] seasonTicketRatesDTO,
                                        @PathVariable(value = "seasonTicketId") Integer seasonTicketId) {

        seasonTicketRateService.updateSeasonTicketRates(seasonTicketId, Arrays.asList(seasonTicketRatesDTO));
        refreshDataService.refreshEvent(seasonTicketId.longValue(), "updateSeasonTicketRates");
    }

    @PutMapping(value = "/{rateId}")
    public void updateIndividualSeasonTicketRates(@RequestBody SeasonTicketRateDTO seasonTicketRatesDTO,
                                                  @PathVariable(value = "seasonTicketId") Integer seasonTicketId,
                                                  @PathVariable(value = "rateId") Long rateId) {

        if (seasonTicketRatesDTO.getId() == null) {
            seasonTicketRatesDTO.setId(rateId);
        } else if (!seasonTicketRatesDTO.getId().equals(rateId)) {
            throw OneboxRestException.builder(MsEventRateErrorCode.ID_RATES_NOT_COHERENT).build();
        }
        seasonTicketRateService.updateSeasonTicketRates(seasonTicketId, Collections.singletonList(seasonTicketRatesDTO));
        refreshDataService.refreshEvent(seasonTicketId.longValue(), "updateIndividualSeasonTicketRates");
    }

    @DeleteMapping(value = "/{rateId}")
    public void deleteSeasonTicketRate(@PathVariable(value = "seasonTicketId") Integer seasonTicketId,
                                       @PathVariable(value = "rateId") Integer rateId) {
        seasonTicketRateService.deleteSeasonTicketRate(seasonTicketId, rateId);
        refreshDataService.refreshEvent(seasonTicketId.longValue(), "deleteSeasonTicketRate");
    }
}
