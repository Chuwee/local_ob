package es.onebox.mgmt.realms;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.realms.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(
        value = RolesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class RolesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/roles";

    private static final String AUDIT_COLLECTION = "ROLES";

    private final RolesService rolesService;

    @Autowired
    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<RoleDTO> getAllRoles() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET_AVAILABLE);
        return rolesService.getAllRoles();
    }

}
