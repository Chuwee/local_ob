package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.venues.service.ExternalVenuesService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;

@RestController
@Validated
@RequestMapping(
        value = ExternalVenuesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ExternalVenuesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}";

    private static final String AUDIT_COLLECTION = "EXTERNAL-VENUES";
    private static final String AUDIT_CITIES_COLLECTION = "EXTERNAL-VENUE-TEMPLATES";

    private final ExternalVenuesService externalVenuesService;

    @Autowired
    public ExternalVenuesController(ExternalVenuesService externalVenuesService){
        this.externalVenuesService = externalVenuesService;
    }


    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/external-venues")
    public List<IdNameCodeDTO> getProviderVenues(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return externalVenuesService.getExternalVenues(entityId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/external-venues/{externalVenueId}/external-venue-templates")
    public List<IdNameCodeDTO> getProviderVenueTemplates(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId, @PathVariable @Min(value = 1, message = "externalVenueId must be above 0") Long externalVenueId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_CITIES_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return externalVenuesService.getExternalVenueTemplates(entityId, externalVenueId);
    }

}
