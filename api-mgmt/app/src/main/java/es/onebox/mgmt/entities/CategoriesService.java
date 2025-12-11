package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.categories.BaseCategoryDTO;
import es.onebox.mgmt.categories.CategoryConverter;
import es.onebox.mgmt.categories.EntityCategoryResponseDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Category;
import es.onebox.mgmt.datasources.ms.entity.dto.CategoryMapping;
import es.onebox.mgmt.datasources.ms.entity.repository.CategoriesRepository;
import es.onebox.mgmt.entities.dto.CategoryMappingsDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {

    @Autowired
    private SecurityManager securityManager;

    @Autowired
    private CategoriesRepository categoriesRepository;

    public List<EntityCategoryResponseDTO> getCategories(Long entityId) {
        Long finalEntityId = checkEntity(entityId);
        return CategoryConverter.fromEntityCategories(categoriesRepository.getEntityCategories(finalEntityId));
    }

    public EntityCategoryResponseDTO getCategory(Long entityId, Long categoryId) {
        Long finalEntityId = checkEntity(entityId);
        return CategoryConverter.fromEntity(categoriesRepository.getEntityCategory(finalEntityId, categoryId), new EntityCategoryResponseDTO());
    }

    public Long createCategory(Long entityId, BaseCategoryDTO baseCategoryDTO) {
        Long finalEntityId = checkEntity(entityId);
        Category category = CategoryConverter.fromDTO(baseCategoryDTO);
        return categoriesRepository.createEntityCategory(finalEntityId, category);
    }

    public void updateCategory(Long entityId, Long categoryId, BaseCategoryDTO entityCategoryRequestDTO) {
        Long finalEntityId = checkEntity(entityId);
        Category category = CategoryConverter.fromDTO(entityCategoryRequestDTO);
        categoriesRepository.updateEntityCategory(finalEntityId, categoryId, category);
    }

    public void deleteCategory(Long entityId, Long categoryId) {
        Long finalEntityId = checkEntity(entityId);
        categoriesRepository.deleteEntityCategory(finalEntityId, categoryId);
    }

    public void putCategoryMapping(Long entityId, CategoryMappingsDTO categoryMappingsDTO) {
        Long finalEntityId = checkEntity(entityId);
        categoriesRepository.putCategoryMapping(finalEntityId, CategoryConverter.toEntity(categoryMappingsDTO));
    }

    public CategoryMappingsDTO getCategoryMapping(Long entityId) {
        Long finalEntityId = checkEntity(entityId);
        List<CategoryMapping> mappings = categoriesRepository.getCategoryMapping(finalEntityId);
        return CategoryConverter.fromEntity(mappings);
    }

    private Long checkEntity(Long entityId) {
        if (SecurityUtils.isOperatorEntity() && entityId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_ID_MANDATORY);
        }
        if (entityId != null) {
            securityManager.checkEntityAccessible(entityId);
        }
        return entityId != null ? entityId : SecurityUtils.getUserEntityId();
    }
}
