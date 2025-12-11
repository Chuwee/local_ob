package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.categories.BaseCategoryDTO;
import es.onebox.mgmt.categories.EntityCategoryResponseDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.dto.CategoryMappingsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@Validated
@RequestMapping(
        value = CategoriesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoriesController {


    private static final String AUDIT_COLLECTION = "ENTITIES";
    private static final String AUDIT_SUBCOLLECTION_CATEGORIES = "CATEGORIES";

    static final String BASE_URI = ApiConfig.BASE_URL + "/entity-categories";

    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping()
    @Secured({ROLE_EVN_MGR, ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public List<EntityCategoryResponseDTO> getCategories(@RequestParam(required = false, name = "entity_id") Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CATEGORIES, AuditTag.AUDIT_ACTION_GET);
        return categoriesService.getCategories(entityId);
    }

    @GetMapping(value = "/{categoryId}")
    @Secured({ROLE_EVN_MGR, ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public EntityCategoryResponseDTO getCategory(@RequestParam(required = false, name = "entity_id") Long entityId, @PathVariable Long categoryId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CATEGORIES, AuditTag.AUDIT_ACTION_GET);
        return categoriesService.getCategory(entityId, categoryId);
    }

    @PostMapping()
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    public ResponseEntity<IdDTO> createCategory(@RequestParam(required = false, name = "entity_id") Long entityId, @RequestBody BaseCategoryDTO baseCategoryDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CATEGORIES, AuditTag.AUDIT_ACTION_CREATE);
        return new ResponseEntity<>(new IdDTO(categoriesService.createCategory(entityId, baseCategoryDTO)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{categoryId}")
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCategory(@RequestParam(required = false, name = "entity_id") Long entityId, @PathVariable Long categoryId, @RequestBody BaseCategoryDTO entityCategoryRequestDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CATEGORIES, AuditTag.AUDIT_ACTION_UPDATE);
        categoriesService.updateCategory(entityId, categoryId, entityCategoryRequestDTO);

    }

    @DeleteMapping(value = "/{categoryId}")
    @Secured({ROLE_EVN_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@RequestParam(required = false, name = "entity_id") Long entityId, @PathVariable Long categoryId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CATEGORIES, AuditTag.AUDIT_ACTION_DELETE);
        categoriesService.deleteCategory(entityId, categoryId);
    }

    @PutMapping(value = "/mapping")
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putCategoryMapping(@RequestParam(required = false, name = "entity_id") Long entityId, @RequestBody CategoryMappingsDTO categoryMappingsDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CATEGORIES, AuditTag.AUDIT_ACTION_UPDATE);
        categoriesService.putCategoryMapping(entityId, categoryMappingsDTO);

    }

    @GetMapping(value = "/mapping")
    @Secured({ROLE_EVN_MGR, ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public CategoryMappingsDTO getCategoryMapping(@RequestParam(required = false, name = "entity_id") Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CATEGORIES, AuditTag.AUDIT_ACTION_UPDATE);
        return categoriesService.getCategoryMapping(entityId);

    }

}
