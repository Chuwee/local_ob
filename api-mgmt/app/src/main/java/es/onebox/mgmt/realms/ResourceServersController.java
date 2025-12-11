package es.onebox.mgmt.realms;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.ResourceServer;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;

@RestController
@RequestMapping(value = ResourceServersController.BASE_URI)
public class ResourceServersController {
    
    static final String BASE_URI = ApiConfig.BASE_URL + "/resource-servers";
    private static final String AUDIT_SUBCOLLECTION= "REALMS";


    private final MasterdataService masterdataService;

    public ResourceServersController(MasterdataService masterdataService) {
        this.masterdataService = masterdataService;
    }
    
    @Secured({ROLE_OPR_ANS, ROLE_OPR_MGR, ROLE_SYS_ANS, ROLE_SYS_MGR})
    @GetMapping
    public List<ResourceServer> fetch() {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AuditTag.AUDIT_ACTION_SEARCH, AUDIT_SUBCOLLECTION);
        return this.masterdataService.getAllResourceServers();
    }
}
