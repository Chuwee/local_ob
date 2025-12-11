package es.onebox.mgmt.entities.friends.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.friends.dto.EntityFriendsConfigDTO;
import es.onebox.mgmt.entities.friends.service.EntityFriendsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@Validated
@RestController
@RequestMapping(value = EntityFriendsController.BASE_URI)
public class EntityFriendsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/friends";

    private static final String AUDIT_COLLECTION = "ENTITIES_FRIENDS_CONFIG";

    private final EntityFriendsService service;

    @Autowired
    public EntityFriendsController(EntityFriendsService service) {
        this.service = service;
    }

    @GetMapping
    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public EntityFriendsConfigDTO getConfig(
            @PathVariable @Min(value = 1, message = "entityId must be greater than 1") Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getConfig(entityId);
    }

    @PutMapping
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateConfig(
            @PathVariable @Min(value = 1, message = "entityId must be greater than 1") Long entityId,
            @RequestBody @Valid EntityFriendsConfigDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateConfig(entityId, body);
    }
}
