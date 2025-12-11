package es.onebox.mgmt.accesscontrol;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.accesscontrol.dto.AccessControlSystemsDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;

@RestController
@RequestMapping(
        value = AccessControlSystemsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AccessControlSystemsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/access-control-systems";

    private static final String AUDIT_COLLECTION = "ACCESS_CONTROL_SYSTEMS";

    private AccessControlSystemsService accessControlSystemsService;

    @Autowired
    public AccessControlSystemsController(AccessControlSystemsService accessControlSystemsService){
        this.accessControlSystemsService = accessControlSystemsService;
    }

    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public AccessControlSystemsDTO getSystems() {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return accessControlSystemsService.getAvailableSystems();
    }
}
