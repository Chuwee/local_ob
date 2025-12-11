package es.onebox.mgmt.channels.members;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.enums.RestrictionType;
import es.onebox.mgmt.channels.members.service.MembersRestrictionsStructureService;
import es.onebox.mgmt.common.restrictions.dto.RestrictionsStructureDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = MembersRestrictionsStructureController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class MembersRestrictionsStructureController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels";

    private static final String AUDIT_MEMBER_RESTRICTION_STRUCTURE = "AUDIT_MEMBER_RESTRICTION_STRUCTURE";

    private final MembersRestrictionsStructureService membersRestrictionsStructureService;

    @Autowired
    public MembersRestrictionsStructureController(MembersRestrictionsStructureService membersRestrictionsStructureService) {
        this.membersRestrictionsStructureService = membersRestrictionsStructureService;
    }

    @Secured({ROLE_OPR_MGR, ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping("/member-config/restrictions-dynamic-configuration")
    public List<RestrictionsStructureDTO> getMemberRestrictionConfigurationStructures(@RequestParam(value = "restriction_type", required = false) RestrictionType restrictionType) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_MEMBER_RESTRICTION_STRUCTURE, AuditTag.AUDIT_ACTION_GET);
        return membersRestrictionsStructureService.getMemberConfigRestrictionStructure(restrictionType);
    }

}
