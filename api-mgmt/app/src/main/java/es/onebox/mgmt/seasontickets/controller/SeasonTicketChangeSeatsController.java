package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketChangeSeatDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketChangeSeatDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.ChangeSeatSeasonTicketPriceCompleteRelationDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.ChangeSeatSeasonTicketPriceFilterDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.UpdateChangeSeatSeasonTicketPriceRelationsDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketChangeSeatsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@Validated
@RequestMapping(
        value = SeasonTicketChangeSeatsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SeasonTicketChangeSeatsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";
    private static final String SEASON_TICKET_ID = "/{seasonTicketId}";
    private static final String CHANGE_SEATS = "/change-seats";
    private static final String SEASON_TICKET_CHANGE_SEATS = SEASON_TICKET_ID + CHANGE_SEATS;
    private static final String PRICES = "/prices";
    private static final String SEASON_TICKET_CHANGE_SEATS_PRICES = SEASON_TICKET_CHANGE_SEATS + PRICES;

    private static final String AUDIT_COLLECTION = "SEASON_TICKETS_CHANGE_SEATS";
    private static final String CHANGE_SEATS_ACTION = "CHANGE_SEATS";

    private final SeasonTicketChangeSeatsService seasonTicketChangeSeatsService;

    @Autowired
    public SeasonTicketChangeSeatsController(SeasonTicketChangeSeatsService seasonTicketChangeSeatsService) {
        this.seasonTicketChangeSeatsService = seasonTicketChangeSeatsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = SEASON_TICKET_CHANGE_SEATS_PRICES, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChangeSeatSeasonTicketPriceCompleteRelationDTO> searchChangeSeatPriceRelations(@PathVariable(value = "seasonTicketId") @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                                                               @Valid @BindUsingJackson ChangeSeatSeasonTicketPriceFilterDTO seasonTicketPriceFilter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketChangeSeatsService.searchChangeSeatPriceRelations(seasonTicketId, seasonTicketPriceFilter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = SEASON_TICKET_CHANGE_SEATS_PRICES, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChangeSeatPriceRelations(@PathVariable(value = "seasonTicketId") @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                               @Valid @RequestBody UpdateChangeSeatSeasonTicketPriceRelationsDTO updatePriceRelations) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        seasonTicketChangeSeatsService.updateChangeSeatPriceRelations(seasonTicketId, updatePriceRelations);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = GET, value = SEASON_TICKET_CHANGE_SEATS)
    public SeasonTicketChangeSeatDTO getSeasonTicketChangeSeat(@PathVariable(value = "seasonTicketId")
                                                               @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        Audit.addTags(CHANGE_SEATS_ACTION, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketChangeSeatsService.getSeasonTicketChangeSeat(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = PUT, value = SEASON_TICKET_CHANGE_SEATS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicketChangeSeat(@PathVariable(value = "seasonTicketId")
                                             @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                             @Valid @RequestBody UpdateSeasonTicketChangeSeatDTO updateSeasonTicketChangeSeat) {
        Audit.addTags(CHANGE_SEATS_ACTION, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        seasonTicketChangeSeatsService.updateSeasonTicketChangeSeat(seasonTicketId, updateSeasonTicketChangeSeat);
    }
}
