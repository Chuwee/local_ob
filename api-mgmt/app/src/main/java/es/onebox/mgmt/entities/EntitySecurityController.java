package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.dto.EntitySecurityConfigDTO;
import es.onebox.mgmt.entities.dto.UpdateEntitySecurityConfigRequestDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;

@RestController
@Validated
@RequestMapping(value = EntitySecurityController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class EntitySecurityController {
    static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/security-config";

    private static final String AUDIT_COLLECTION = "ENTITIES";
    private static final String AUDIT_SUBCOLLECTION_SECURITY = "SECURITY_CONFIG";

    private final EntitySecurityService entitySecurityService;

    @Autowired
    public EntitySecurityController(EntitySecurityService entitySecurityService) {
        this.entitySecurityService = entitySecurityService;
    }

    @GetMapping
    @Secured({ ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_SYS_MGR})
    public EntitySecurityConfigDTO getEntitySecurityConfig(@PathVariable Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_SECURITY, AuditTag.AUDIT_ACTION_GET);
        return entitySecurityService.getEntitySecurityConfig(entityId);
    }

    @PutMapping
    @Secured({ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEntitySecurityConfig(@PathVariable Long entityId, @Valid @RequestBody UpdateEntitySecurityConfigRequestDTO request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_SECURITY, AuditTag.AUDIT_ACTION_UPDATE);
        entitySecurityService.updateEntitySecurityConfig(entityId, request);
    }
}
