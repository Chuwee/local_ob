package es.onebox.mgmt.users.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.realms.dto.RoleDTO;
import es.onebox.mgmt.realms.dto.RolesDTO;
import es.onebox.mgmt.users.service.UserRolesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = UserRolesController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRolesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/users/{userId}/roles";
    private static final String AUDIT_COLLECTION = "USER_ROLES";

    private final UserRolesService service;

    @Autowired
    public UserRolesController(UserRolesService userRolesService) {
        this.service = userRolesService;
    }

    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_SYS_ANS, ROLE_SYS_MGR})
    @GetMapping
    public List<RoleDTO> getUserRoles(@PathVariable Long userId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return service.getRoles(userId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_SYS_ANS, ROLE_SYS_MGR})
    @GetMapping(value = "/available")
    public List<RoleDTO> getUserRolesAvailable(@PathVariable Long userId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET_AVAILABLE);

        return service.getRolesAvailable(userId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addRole(@PathVariable Long userId, @RequestBody @Valid RoleDTO roleDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_ADD);

        service.setRole(userId, roleDTO);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addRoles(@PathVariable Long userId, @RequestBody @Valid RolesDTO rolesDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        service.setRoles(userId, rolesDTO);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @DeleteMapping(value = "/{roleCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable Long userId, @PathVariable String roleCode) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        service.unsetRole(userId, roleCode);
    }
    
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @PostMapping(value = "/{roleCode}/permissions/{permissionCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addPermission(@PathVariable Long userId, @PathVariable String roleCode, @PathVariable String permissionCode) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        service.addPermission(userId, roleCode, permissionCode);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @DeleteMapping(value = "/{roleCode}/permissions/{permissionCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermission(@PathVariable Long userId, @PathVariable String roleCode, @PathVariable String permissionCode) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        service.deletePermission(userId, roleCode, permissionCode);
    }





}