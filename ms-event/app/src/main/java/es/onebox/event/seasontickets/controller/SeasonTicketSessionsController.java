package es.onebox.event.seasontickets.controller;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.seasontickets.dto.AssignSessionRequestDTO;
import es.onebox.event.seasontickets.dto.AssignSessionResponseDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionValidationResponse;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionsDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionsEventList;
import es.onebox.event.seasontickets.dto.UnAssignSessionResponseDTO;
import es.onebox.event.seasontickets.request.SeasonTicketSessionsEventsFilter;
import es.onebox.event.seasontickets.request.SeasonTicketSessionsSearchFilter;
import es.onebox.event.seasontickets.service.SeasonTicketSessionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(SeasonTicketSessionsController.BASE_URI)
public class SeasonTicketSessionsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/sessions";

    private SeasonTicketSessionsService seasonTicketSessionsService;

    @Autowired
    public SeasonTicketSessionsController(SeasonTicketSessionsService seasonTicketSessionsService) {
        this.seasonTicketSessionsService = seasonTicketSessionsService;
    }


    @RequestMapping(method = GET)
    public SeasonTicketSessionsDTO listSessions(@PathVariable Long seasonTicketId,
                                                SeasonTicketSessionsSearchFilter searchFilter) {
        validateNotNull(seasonTicketId, "seasonTicketId is mandatory");

        return seasonTicketSessionsService.searchCandidateSessions(searchFilter, seasonTicketId);
    }

    @RequestMapping(method = GET, value = "/events")
    public SeasonTicketSessionsEventList listEvents(@PathVariable Long seasonTicketId, @Valid SeasonTicketSessionsEventsFilter filter) {
        validateNotNull(seasonTicketId, "seasonTicketId is mandatory");
        return seasonTicketSessionsService.getEventsList(seasonTicketId, filter);
    }

    @RequestMapping(method = GET, value = "/{sessionId}/validations")
    public SeasonTicketSessionValidationResponse verifySession(@PathVariable("seasonTicketId") Long seasonTicketId,
                                                               @PathVariable("sessionId") Long sessionId,
                                                               @RequestParam(value = "includeSeats", required = false) Boolean includeSeats) {
        validateNotNull(seasonTicketId, "seasonTicketId is mandatory");
        validateNotNull(sessionId, "sessionId is mandatory");
        return seasonTicketSessionsService.verifySession(seasonTicketId, sessionId, CommonUtils.isTrue(includeSeats));
    }

    @RequestMapping(method = POST)
    public AssignSessionResponseDTO assignSession(@PathVariable("seasonTicketId") Long seasonTicketId,
                                                  @RequestBody AssignSessionRequestDTO assignSessionRequestDTO) {
        validateNotNull(seasonTicketId, "seasonTicketId is mandatory");
        validateNotNull(assignSessionRequestDTO, "sessionId is mandatory");
        validateNotNull(assignSessionRequestDTO.getSessionId(), "sessionId is mandatory");
        AssignSessionResponseDTO assignSessionResponseDTO = seasonTicketSessionsService
                .assignSession(seasonTicketId, assignSessionRequestDTO);
        seasonTicketSessionsService.migrateTargetSession(assignSessionRequestDTO.getSessionId());
        return assignSessionResponseDTO;
    }

    @RequestMapping(method = DELETE, value = "/{sessionId}")
    public UnAssignSessionResponseDTO unAssignSession(@PathVariable("seasonTicketId") Long seasonTicketId,
                                                      @PathVariable("sessionId") Long sessionId,
                                                      @RequestParam(value = "updateBarcodes", required = false) Boolean updateBarcodes) {
        validateNotNull(seasonTicketId, "seasonTicketId is mandatory");
        validateNotNull(sessionId, "sessionId is mandatory");
        UnAssignSessionResponseDTO response = seasonTicketSessionsService.unAssignSession(seasonTicketId, sessionId, updateBarcodes);
        seasonTicketSessionsService.migrateTargetSession(sessionId);
        return response;
    }

    private static void validateNotNull(Object o, String message) {
        if (o == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, message, null);
        }
    }

    @RequestMapping(method = POST, value = "/update-barcodes")
    public void updateBarcodes(@PathVariable Long seasonTicketId) {
        validateNotNull(seasonTicketId, "seasonTicketId is mandatory");
        seasonTicketSessionsService.updateBarcodes(seasonTicketId);
    }
}