package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.seasontickets.dto.customertypes.SeasonTicketCustomerTypeDTO;
import es.onebox.mgmt.seasontickets.dto.customertypes.UpdateSeasonTicketCustomerTypesDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketCustomerTypesService;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(SeasonTicketCustomerTypesController.BASE_URI)
public class SeasonTicketCustomerTypesController {

    private static final String AUDIT_COLLECTION = "SEASON_TICKET_CUSTOMER_TYPES";

    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/customer-types";

    private final SeasonTicketCustomerTypesService service;

    public SeasonTicketCustomerTypesController(SeasonTicketCustomerTypesService service) {
        this.service = service;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public List<SeasonTicketCustomerTypeDTO> getSeasonTicketCustomerTypes(@PathVariable(value = "seasonTicketId")
                                                            @Min(value = 1, message = "seasonTicketId must be above 0")
                                                            Integer seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getSeasonTicketCustomerTypes(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping
    public void putSeasonTicketCustomerTypes(@PathVariable(value = "seasonTicketId")
                                      @Min(value = 1, message = "seasonTicketId must be above 0")
                                      Integer seasonTicketId, @RequestBody UpdateSeasonTicketCustomerTypesDTO dto) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateSeasonTicketCustomerTypes(seasonTicketId, dto);
    }
}
