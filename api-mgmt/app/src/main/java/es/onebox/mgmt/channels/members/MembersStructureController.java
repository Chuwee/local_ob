package es.onebox.mgmt.channels.members;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.dto.MemberConfigurationStructureDTO;
import es.onebox.mgmt.channels.members.service.MembersService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.members.DynamicBusinessRuleTypes;
import es.onebox.mgmt.members.MemberOrderType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = MembersStructureController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class MembersStructureController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels";

    private static final String AUDIT_MEMBER_STRUCTURE = "AUDIT_MEMBER_STRUCTURE";

    private final MembersService membersService;

    @Autowired
    public MembersStructureController(MembersService membersService) {
        this.membersService = membersService;
    }

    @Secured({ROLE_OPR_MGR, ROLE_CNL_MGR})
    @GetMapping("/member-config/dynamic-configuration")
    public List<MemberConfigurationStructureDTO> getMemberConfigurationStructures(@RequestParam(value = "type", required = false) DynamicBusinessRuleTypes type,
                                                                                  @RequestParam(value = "order_type", required = false) MemberOrderType orderType) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_MEMBER_STRUCTURE, AuditTag.AUDIT_ACTION_GET);
        return membersService.getMemberConfigStructure(type, orderType);
    }

    @Secured({ROLE_OPR_MGR, ROLE_CNL_MGR})
    @GetMapping(value = "/{channelId}/member-config/dynamic-configuration")
    public List<MemberConfigurationStructureDTO> getMemberConfigurationStructures(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                                                  @RequestParam(value = "type", required = false) DynamicBusinessRuleTypes type,
                                                                                  @RequestParam(value = "order_type", required = false) MemberOrderType orderType) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_MEMBER_STRUCTURE, AuditTag.AUDIT_ACTION_GET);
        return membersService.getChannelMemberConfigStructure(channelId, type, orderType);
    }

    @Secured({ROLE_OPR_MGR, ROLE_CNL_MGR})
    @PutMapping(value = "/{channelId}/member-config/dynamic-configuration/{operationName}")
    public void updateMemberConfigurationStructures(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                    @PathVariable @NotNull String operationName,
                                                    @RequestBody @NotNull MemberConfigurationStructureDTO memberConfigurationStructureDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_MEMBER_STRUCTURE, AuditTag.AUDIT_ACTION_UPDATE);
        membersService.updateChannelMemberConfigStructure(channelId, operationName, memberConfigurationStructureDTO);
    }

}
