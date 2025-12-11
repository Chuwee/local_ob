package es.onebox.mgmt.channels.members;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.members.service.MembersMappingService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = MembersMappingController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class MembersMappingController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/capacity-mapping";

    private static final String AUDIT_COLLECTION = "MEMBERS_MAPPING";

    private final MembersMappingService membersMappingService;

    @Autowired
    public MembersMappingController(MembersMappingService membersMappingService) {
        this.membersMappingService = membersMappingService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @PostMapping()
    public void mapCapacity(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        membersMappingService.mapCapacity(channelId);
    }

}
