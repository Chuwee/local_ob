package es.onebox.event.seasontickets.controller;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusResponseDTO;
import es.onebox.event.seasontickets.service.SeasonTicketService;
import es.onebox.event.seasontickets.service.SeasonTicketSurchargesService;
import es.onebox.event.surcharges.dto.SurchargeListDTO;
import es.onebox.event.surcharges.dto.SurchargeTypeDTO;
import es.onebox.event.surcharges.dto.SurchargesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(SeasonTicketSurchargesController.BASE_URI)
public class SeasonTicketSurchargesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";

    @Autowired
    private SeasonTicketSurchargesService seasonTicketSurchargesService;

    @Autowired
    private SeasonTicketService seasonTicketService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{seasonTicketId}/surcharges")
    public List<SurchargesDTO> getSeasonTicketSurcharges(@PathVariable Long seasonTicketId,
                                                         @RequestParam(value = "type", required = false) List<SurchargeTypeDTO> types) {
        return seasonTicketSurchargesService.getSeasonTicketSurcharges(seasonTicketId, types);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/{seasonTicketId}/surcharges")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setSeasonTicketSurcharges(@PathVariable Long seasonTicketId, @RequestBody SurchargeListDTO surchargeListDTO) {
        seasonTicketSurchargesService.setSeasonTicketSurcharges(seasonTicketId, surchargeListDTO);
    }

}