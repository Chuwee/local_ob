package es.onebox.mgmt.entities.contents;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityBlockType;
import es.onebox.mgmt.entities.contents.dto.EntityLiteralsDTO;
import es.onebox.mgmt.entities.contents.dto.EntityTextBlocksDTO;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocksDTO;
import es.onebox.mgmt.entities.contents.enums.EntityBlockCategory;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = EntityContentsController.BASE_URI)
public class EntityContentsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}";

    private static final String AUDIT_COLLECTION = "ENTITY_CONTENTS";

    private final EntityContentsService service;

    @Autowired
    public EntityContentsController(EntityContentsService service) {
        this.service = service;
    }

    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/text-contents/languages/{language}")
    public EntityLiteralsDTO get(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable @LanguageIETF String language) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getEntityLiterals(entityId, language);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.POST, value = "/text-contents/languages/{language}")
    public void upsert(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable @LanguageIETF String language, @RequestBody @NotEmpty @Valid EntityLiteralsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        this.service.upsertEntityLiterals(entityId, language, body);
    }

    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/contents/{category}")
    public EntityTextBlocksDTO getCommunicationElement(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable EntityBlockCategory category,
            @RequestParam(value = "language", required = false) @LanguageIETF String language,
            @RequestParam(value = "type", required = false) List<EntityBlockType> type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getEntityTextBlocks(entityId, language, category, type);
    }

    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.PUT, value = "/contents/{category}")
    public void updateCommunicationElement(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable EntityBlockCategory category,
            @NotEmpty @RequestBody @Valid UpdateEntityTextBlocksDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateEntityTextBlocks(entityId, category, body);
    }
}
