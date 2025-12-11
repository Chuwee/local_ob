package es.onebox.mgmt.events.avetrestrictions.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionStructureDTO;
import es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionType;
import es.onebox.mgmt.events.avetrestrictions.service.AvetSectorRestrictionStructureService;
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
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = AvetSectorRestrictionStructureController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class AvetSectorRestrictionStructureController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}";

    private static final String AUDIT_MEMBER_RESTRICTION_STRUCTURE = "AUDIT_AVET_SECTOR_RESTRICTION_STRUCTURE";

    private final AvetSectorRestrictionStructureService avetSectorRestrictionStructureService;

    @Autowired
    public AvetSectorRestrictionStructureController(AvetSectorRestrictionStructureService avetSectorRestrictionStructureService) {
        this.avetSectorRestrictionStructureService = avetSectorRestrictionStructureService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/avet-restrictions-dynamic-configuration")
    public List<AvetSectorRestrictionStructureDTO> getMemberRestrictionConfigurationStructures(@RequestParam(value = "restriction_type", required = false) AvetSectorRestrictionType restrictionType,
                                                                                               @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_MEMBER_RESTRICTION_STRUCTURE, AuditTag.AUDIT_ACTION_GET);

        return avetSectorRestrictionStructureService.getAvetSectorRestrictionsStructure(restrictionType, eventId);
    }
}
