package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.seasontickets.dto.tax.SeasonTicketTaxDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketTaxService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(SeasonTicketTaxController.BASE_URI)
public class SeasonTicketTaxController {

    private static final String AUDIT_COLLECTION = "SEASON_TICKETS";
    private static final String AUDIT_SUBCOLLECTION_TAXES = "TAXES";

    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/taxes";

    private final SeasonTicketTaxService seasonTicketTaxService;

    public SeasonTicketTaxController(SeasonTicketTaxService seasonTicketTaxService) {
        this.seasonTicketTaxService = seasonTicketTaxService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public SeasonTicketTaxDTO getSeasonTicketTaxes(@PathVariable Long seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAXES, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketTaxService.getSeasonTicketTaxes(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateSeasonTicketTaxes(@PathVariable Long seasonTicketId,
                                        @RequestBody @Valid SeasonTicketTaxDTO updateTaxes) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAXES, AuditTag.AUDIT_ACTION_UPDATE);
        seasonTicketTaxService.updateSeasonTicketTaxes(seasonTicketId, updateTaxes);
    }
}

