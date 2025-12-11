package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.seasontickets.service.SeasonTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = SeasonTicketExternalInventoryController.BASE_URI)
public class SeasonTicketExternalInventoryController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";
    private static final String SEASON_TICKET_ID = "/{seasonTicketId}";
    private static final String EXTERNAL_INVENTORY = "/external-inventory";

    private static final String AUDIT_COLLECTION = "SEASON_TICKETS";

    private final SeasonTicketService seasonTicketService;

    @Autowired
    public SeasonTicketExternalInventoryController(SeasonTicketService seasonTicketService) {
        this.seasonTicketService = seasonTicketService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(value = SEASON_TICKET_ID + EXTERNAL_INVENTORY)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicketExternalInventory(@PathVariable Long seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        seasonTicketService.updateSeasonTicketExternalInventory(seasonTicketId);
    }
}
