package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.categories.BaseCategoryDTO;
import es.onebox.mgmt.categories.EntityCategoryRequestDTO;
import es.onebox.mgmt.categories.EntityCategoryResponseDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Deprecated
@Validated
@RequestMapping(
        value = EntityCategoriesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class EntityCategoriesController {


    private static final String AUDIT_COLLECTION = "ENTITIES";
    private static final String AUDIT_SUBCOLLECTION_CATEGORIES = "CATEGORIES";

    static final String BASE_URI = ApiConfig.BASE_URL + "/entities";

    @Autowired
    private EntityCategoriesService entityCategoriesService;

    @Deprecated
    @RequestMapping(method = RequestMethod.GET, value = "/{entityId}/categories")
    @Secured({ROLE_EVN_MGR, ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public List<EntityCategoryResponseDTO> getCategories(@PathVariable Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CATEGORIES, AuditTag.AUDIT_ACTION_GET);

        return entityCategoriesService.getEntityCategories(entityId);
    }

    @Deprecated
    @RequestMapping(method = RequestMethod.POST, value = "/{entityId}/categories")
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    public ResponseEntity<IdDTO> createEntityCategory(@PathVariable Long entityId, @RequestBody BaseCategoryDTO baseCategoryDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CATEGORIES, AuditTag.AUDIT_ACTION_CREATE);

        return new ResponseEntity<>(new IdDTO(entityCategoriesService.createEntityCategory(entityId, baseCategoryDTO)), HttpStatus.CREATED);
    }

    @Deprecated
    @RequestMapping(method = RequestMethod.PUT, value = "/{entityId}/categories/{categoryId}")
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putEntityCategories(@PathVariable Long entityId, @PathVariable Long categoryId, @RequestBody EntityCategoryRequestDTO entityCategoryRequestDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CATEGORIES, AuditTag.AUDIT_ACTION_UPDATE);

        entityCategoriesService.updateEntityCategory(entityId, categoryId, entityCategoryRequestDTO);

    }

    @Deprecated
    @RequestMapping(method = RequestMethod.DELETE, value = "/{entityId}/categories/{categoryId}")
    @Secured({ROLE_EVN_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntityCategory(@PathVariable Long entityId, @PathVariable Long categoryId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CATEGORIES, AuditTag.AUDIT_ACTION_DELETE);

        entityCategoriesService.deleteEntityCategories(entityId, categoryId);
    }

}
