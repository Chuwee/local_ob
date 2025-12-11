package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.surcharges.dto.SeasonTicketSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SeasonTicketSurchargeListDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.seasontickets.service.SeasonTicketSurchargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = SeasonTicketSurchargeController.BASE_URI)
public class SeasonTicketSurchargeController {

    public static final String BASE_URI = SeasonTicketController.BASE_URI + "/{seasonTicketId}/surcharges";
    private static final String AUDIT_COLLECTION = "SEASON_TICKET_SURCHARGES";

    private final SeasonTicketSurchargeService seasonTicketSurchargeService;

    @Autowired
    public SeasonTicketSurchargeController(SeasonTicketSurchargeService seasonTicketSurchargeService) {
        this.seasonTicketSurchargeService = seasonTicketSurchargeService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public List<SeasonTicketSurchargeDTO> getSurcharges(@PathVariable Long seasonTicketId,
                                                        @RequestParam(value = "type", required = false) List<SurchargeTypeDTO> types) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return seasonTicketSurchargeService.getSurcharges(seasonTicketId, types);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setSurcharge(@PathVariable Long seasonTicketId, @RequestBody SeasonTicketSurchargeListDTO seasonTicketSurchargeListDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        seasonTicketSurchargeService.setSurcharge(seasonTicketId, seasonTicketSurchargeListDTO);
    }

}
