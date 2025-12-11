package es.onebox.mgmt.users.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.users.dto.realms.AvailableResourceCreateDTO;
import es.onebox.mgmt.users.dto.realms.AvailableResourceDTO;
import es.onebox.mgmt.users.service.UserRolesService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;

@RestController
@Validated
@RequestMapping(value = UserRealmsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRealmsController {
    
    static final String BASE_URI = ApiConfig.BASE_URL + "/users/{userId}";
    private static final String AUDIT_SUBCOLLECTION = "USER_REALMS";
    
    private final UserRolesService userRolesService;
    
    public UserRealmsController(UserRolesService userRolesService) {
        this.userRolesService = userRolesService;
    }

    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR})
    @GetMapping("/resource-servers")
    public List<AvailableResourceDTO> fetch(@PathVariable Long userId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AuditTag.AUDIT_ACTION_SEARCH, AUDIT_SUBCOLLECTION);
        return this.userRolesService.availableResourceServers(userId);
    }

    @Secured({ROLE_SYS_MGR})
    @PostMapping("/resource-servers")
    public void upsert(@PathVariable Long userId, @RequestBody @Valid AvailableResourceCreateDTO upsert) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AuditTag.AUDIT_ACTION_SEARCH, AUDIT_SUBCOLLECTION);
        this.userRolesService.upsertResourceServers(userId, upsert);
    }

}
