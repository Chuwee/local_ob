package es.onebox.mgmt.integrations.authvendor;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.integrations.authvendor.dto.AuthVendorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = AuthVendorController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthVendorController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/integrations/auth-vendors";

    private static final String AUDIT_COLLECTION = "AUTH_VENDOR";

    private final AuthVendorService authVendorService;

    @Autowired
    public AuthVendorController(AuthVendorService authVendorService) {
        this.authVendorService = authVendorService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    public List<AuthVendor> getAuthVendors() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return authVendorService.getAuthVendors();
    }

    @RequestMapping(value = "/{vendorId}", method = RequestMethod.GET)
    @Secured({ROLE_ENT_MGR,ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    public AuthVendorDTO getAuthVendors(@PathVariable String vendorId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return authVendorService.getAuthVendor(vendorId);
    }

}
