package es.onebox.mgmt.entities.communicationElements;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.communicationElements.dto.EntityCommElementsImageListDTO;
import es.onebox.mgmt.entities.communicationElements.dto.EntityCommElementsTextListDTO;
import es.onebox.mgmt.entities.communicationElements.dto.UpdateEntityCommElementsImageDTO;
import es.onebox.mgmt.entities.communicationElements.dto.UpdateEntityCommElementsTextDTO;
import es.onebox.mgmt.entities.enums.EntityImageContentType;
import es.onebox.mgmt.entities.enums.EntityTextContentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping(value = EntityCommElementsController.BASE_URI)
public class EntityCommElementsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/communication-elements";
    public static final String DETAILS_URI = "/languages/{language}/types/{type}";

    private static final String AUDIT_COLLECTION = "ENTITY_COMMUNICATION_ELEMENTS";



    private final EntityCommElementsService service;

    public EntityCommElementsController(EntityCommElementsService service) {
        this.service = service;
    }
    @GetMapping("/images")
    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public EntityCommElementsImageListDTO<EntityImageContentType> getEntityCommunicationElementsImages(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return service.getEntityCommunicationElementsImages(entityId);
    }

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    public void updateEntityCommunicationElementsImages(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @RequestBody @NotEmpty(message = "body can't be empty") @Valid UpdateEntityCommElementsImageDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateEntityCommunicationElementsImages(entityId, body);
    }

    @DeleteMapping("/images" + DETAILS_URI)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    public void deleteEntityCommunicationElementsImages(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable("language") String language, @PathVariable("type") EntityImageContentType type) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        service.deleteEntityCommunicationElementsImages(entityId, language, type);
    }

    @GetMapping("/texts")
    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public EntityCommElementsTextListDTO<EntityTextContentType> getEntityCommunicationElementsTexts(
            @PathVariable("entityId") @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getEntityCommunicationElementsText(entityId);
    }

    @PostMapping("/texts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    public EntityCommElementsTextListDTO<EntityTextContentType> updateEntityCommunicationElementsTexts(
            @PathVariable("entityId") @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @RequestBody @Valid UpdateEntityCommElementsTextDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        return service.updateEntityCommunicationElementsText(entityId, body);
    }

}
