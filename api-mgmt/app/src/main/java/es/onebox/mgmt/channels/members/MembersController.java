package es.onebox.mgmt.channels.members;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.enums.AvetPermission;
import es.onebox.mgmt.channels.members.dto.AforoInfoDTO;
import es.onebox.mgmt.channels.members.dto.AvetEventDTO;
import es.onebox.mgmt.channels.members.dto.PeriodicityDTO;
import es.onebox.mgmt.channels.members.dto.RolPartnerInfoDTO;
import es.onebox.mgmt.channels.members.dto.TermDTO;
import es.onebox.mgmt.channels.members.service.MembersService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = MembersController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class MembersController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}";

    private static final String AUDIT_COLLECTION = "MEMBERS";

    private final MembersService membersService;

    @Autowired
    public MembersController(MembersService membersService) {
        this.membersService = membersService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(value = "/periodicities")
    public List<PeriodicityDTO> getMemberPeriodicities(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return membersService.getMemberPeriodicities(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(value = "/terms")
    public List<TermDTO> getMemberTerms(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return membersService.getMemberTerms(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(value = "/roles")
    public List<RolPartnerInfoDTO> getMemberRoles(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return membersService.getMemberRoles(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(value = "/capacities")
    public List<AforoInfoDTO> getMemberAforos(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return membersService.getMemberAforos(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(value = "/next-matches")
    public List<AvetEventDTO> getMemberEvents(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return membersService.getMemberEvents(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(value = "/permissions")
    public List<AvetPermission> getMemberAvetPermissions(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        return Arrays.stream(AvetPermission.values()).toList();
    }

}
