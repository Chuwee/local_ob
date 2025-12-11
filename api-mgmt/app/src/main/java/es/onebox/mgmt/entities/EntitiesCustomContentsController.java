package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.core.security.Roles;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.dto.EntityCustomContentsDTO;
import es.onebox.mgmt.entities.dto.UpdateEntityCustomContentsListDTO;
import es.onebox.mgmt.entities.enums.EntityCustomContentsType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping(value = EntitiesCustomContentsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class EntitiesCustomContentsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/custom-contents";

    private static final String AUDIT_COLLECTION = "ENTITIES_CUSTOM_CONTENTS";

    private final EntitiesCustomContentsService entitiesCustomContentsService;

    @Autowired
    public EntitiesCustomContentsController(EntitiesCustomContentsService entitiesCustomContentsService) {
        this.entitiesCustomContentsService = entitiesCustomContentsService;
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_ENT_MGR, Roles.Codes.ROLE_ENT_ANS})
    @GetMapping
    public List<EntityCustomContentsDTO> getCustomContents(@PathVariable Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return entitiesCustomContentsService.getCustomContents(entityId);
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_ENT_MGR})
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setCustomContentsEntity(@PathVariable Long entityId, @RequestBody @Valid UpdateEntityCustomContentsListDTO filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        entitiesCustomContentsService.setCustomContents(entityId, filter);
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_ENT_MGR})
    @DeleteMapping("/{tag}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomContentsEntity(@PathVariable Long entityId, @PathVariable EntityCustomContentsType tag) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        entitiesCustomContentsService.deleteCustomContents(entityId, tag);
    }
}
