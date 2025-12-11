package es.onebox.event.seasontickets.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.seasontickets.dto.CreateSeasonTicketRequestDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusResponseDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketsDTO;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketRequestDTO;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketStatusRequestDTO;
import es.onebox.event.seasontickets.request.SeasonTicketSearchFilter;
import es.onebox.event.seasontickets.service.SeasonTicketService;

@RestController
@RequestMapping(SeasonTicketController.BASE_URI)
public class SeasonTicketController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";
    private static final String SEASON_TICKET_ID = "/{seasonTicketId}";

    private final SeasonTicketService seasonTicketService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public SeasonTicketController(SeasonTicketService seasonTicketService, RefreshDataService refreshDataService) {
        this.seasonTicketService = seasonTicketService;
        this.refreshDataService = refreshDataService;
    }

    @RequestMapping(method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Long createSeasonTicket(@Valid @RequestBody CreateSeasonTicketRequestDTO seasonTicket) {
        Long seasonTicketId = seasonTicketService.createSeasonTicket(seasonTicket);
        refreshDataService.refreshEvent(seasonTicketId, "createSeasonTicket");
        return seasonTicketId;
    }

    @RequestMapping(method = GET, value = SEASON_TICKET_ID)
    public SeasonTicketDTO getSeasonTicket(@PathVariable Long seasonTicketId) {

        validateNotNull(seasonTicketId, "seasonTicket id is mandatory");
        if (seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
        return seasonTicketService.getSeasonTicket(seasonTicketId);
    }

    @RequestMapping(method = GET)
    public SeasonTicketsDTO searchSeasonTickets(@Valid SeasonTicketSearchFilter filter) {
        validateNotNull(filter, null);
        return seasonTicketService.searchSeasonTickets(filter);
    }

    @RequestMapping(method = PUT, value = "/{seasonTicketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicket(@PathVariable Long seasonTicketId, @Valid @RequestBody UpdateSeasonTicketRequestDTO body) {

        if (body.getId() == null) {
            body.setId(seasonTicketId);
        } else if (!body.getId().equals(seasonTicketId)) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Field seasonTicketId.id is not equal to current path seasonTicketId", null);
        }

        seasonTicketService.updateSeasonTicket(body);
        refreshDataService.refreshEvent(seasonTicketId, "updateSeasonTicket");
    }

    @RequestMapping(method = DELETE, value = SEASON_TICKET_ID)
    public void deleteSeasonTicket(@PathVariable Long seasonTicketId) {
        validateNotNull(seasonTicketId, "season ticket id is mandatory");

        seasonTicketService.deleteSeasonTicket(seasonTicketId);
    }

    @RequestMapping(method = GET, value = SEASON_TICKET_ID + "/status")
    public SeasonTicketStatusResponseDTO getGenerationStatus(@PathVariable Long seasonTicketId) {
        validateNotNull(seasonTicketId, "seasonTicket id is mandatory");
        if (seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
        return seasonTicketService.getStatus(seasonTicketId);
    }

    private static void validateNotNull(Object o, String message) {
        if (o == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, message, null);
        }
    }

    @RequestMapping(method = PUT, value = SEASON_TICKET_ID + "/status")
    public void updateStatus(@PathVariable Long seasonTicketId, @RequestBody UpdateSeasonTicketStatusRequestDTO updateSeasonTicketStatusRequestDTO) {
        validateNotNull(seasonTicketId, "seasonTicket id is mandatory");
        if (seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
        seasonTicketService.updateStatus(seasonTicketId, updateSeasonTicketStatusRequestDTO);
        refreshDataService.refreshEvent(seasonTicketId, "updateStatus");
    }
}
