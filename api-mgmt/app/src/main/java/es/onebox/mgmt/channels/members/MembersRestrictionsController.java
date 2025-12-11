package es.onebox.mgmt.channels.members;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.dto.MemberRestrictionCreateRequestDTO;
import es.onebox.mgmt.channels.dto.MemberRestrictionDTO;
import es.onebox.mgmt.channels.dto.MemberRestrictionDetailDTO;
import es.onebox.mgmt.channels.dto.MemberRestrictionRequestDTO;
import es.onebox.mgmt.channels.members.service.MembersRestrictionsService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = MembersRestrictionsController.BASE_URI)
public class MembersRestrictionsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/member-config/restrictions";

    private static final String AUDIT_COLLECTION = "CHANNEL_MEMBERS_RESTRICTIONS";

    private final MembersRestrictionsService membersRestrictionsService;

    @Autowired
    public MembersRestrictionsController(MembersRestrictionsService membersRestrictionsService) {
        this.membersRestrictionsService = membersRestrictionsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(value = "/{sid}")
    @ResponseStatus(HttpStatus.OK)
    public MemberRestrictionDetailDTO get(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                          @PathVariable(value = "sid") @NotNull String restrictionSid) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.membersRestrictionsService.getMemberRestriction(channelId, restrictionSid);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<MemberRestrictionDTO> getList(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return this.membersRestrictionsService.getMemberRestrictions(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                       @RequestBody @Valid MemberRestrictionCreateRequestDTO memberRestrictionCreateRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        this.membersRestrictionsService.createMemberRestriction(channelId, memberRestrictionCreateRequestDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @PutMapping(value = "/{sid}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                       @PathVariable(value = "sid") @NotNull String restrictionSid,
                       @Valid @RequestBody MemberRestrictionRequestDTO memberRestrictionRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.membersRestrictionsService.updateMemberRestriction(channelId, restrictionSid, memberRestrictionRequestDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @DeleteMapping(value = "/{sid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                       @PathVariable(value = "sid") @NotNull String restrictionSid) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        this.membersRestrictionsService.deleteMemberRestriction(channelId, restrictionSid);
    }

}
