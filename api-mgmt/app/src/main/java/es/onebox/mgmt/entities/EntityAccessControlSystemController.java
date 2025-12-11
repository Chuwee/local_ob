package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.NameDTO;
import es.onebox.mgmt.accesscontrol.dto.AccessControlSystemsDTO;
import es.onebox.mgmt.config.AuditTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;

@RestController
@RequestMapping(value = EntityAccessControlSystemController.BASE_URI)
public class EntityAccessControlSystemController {

    public static final String BASE_URI = EntitiesController.BASE_URI + "/{entityId}/access-control-systems";
    private static final String AUDIT_COLLECTION = "ENTITY_ACCESS_CONTROL_SYSTEMS";

    private final EntityAccessControlSystemService entityAccessControlSystemService;

    @Autowired
    public EntityAccessControlSystemController(EntityAccessControlSystemService entityAccessControlSystemService) {
        this.entityAccessControlSystemService = entityAccessControlSystemService;
    }

    @Secured({ROLE_REC_MGR,ROLE_ENT_MGR,ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public AccessControlSystemsDTO getAccessControlSystems(@PathVariable Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
      return entityAccessControlSystemService.getAccessControlSystems(entityId);
    }

    @Secured({ROLE_REC_MGR,ROLE_ENT_MGR,ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Serializable> createAccessControlSystems(@PathVariable Long entityId, @RequestBody NameDTO accessControlSystem) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        entityAccessControlSystemService.createAccessControlSystems(entityId, accessControlSystem);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Secured({ROLE_REC_MGR,ROLE_ENT_MGR,ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value="/{ac_system}")
    public ResponseEntity<Serializable> deleteAccessControlSystems(@PathVariable Long entityId,
                                                                   @PathVariable(value="ac_system") String acSystem) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        entityAccessControlSystemService.deleteAccessControlSystems(entityId, acSystem);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
