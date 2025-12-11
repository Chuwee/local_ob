package es.onebox.mgmt.entities;

import es.onebox.mgmt.categories.BaseCategoryDTO;
import es.onebox.mgmt.categories.CategoryConverter;
import es.onebox.mgmt.categories.EntityCategoryRequestDTO;
import es.onebox.mgmt.categories.EntityCategoryResponseDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Category;
import es.onebox.mgmt.datasources.ms.entity.repository.CategoriesRepository;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Deprecated
@Service
public class EntityCategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private SecurityManager securityManager;

    @Deprecated
    public List<EntityCategoryResponseDTO> getEntityCategories(Long entityId) {
        securityManager.checkEntityAccessible(entityId);
        return CategoryConverter.fromEntityCategories(categoriesRepository.getEntityCategories(entityId));
    }

    @Deprecated
    public void deleteEntityCategories(Long entityId, Long categoryId) {
        securityManager.checkEntityAccessible(entityId);
        categoriesRepository.deleteEntityCategory(entityId, categoryId);
    }

    @Deprecated
    public Long createEntityCategory(Long entityId, BaseCategoryDTO baseCategoryDTO) {
        securityManager.checkEntityAccessible(entityId);
        Category category = CategoryConverter.fromDTO(baseCategoryDTO);
        return categoriesRepository.createEntityCategory(entityId, category);
    }

    @Deprecated
    public void updateEntityCategory(Long entityId, Long categoryId, EntityCategoryRequestDTO entityCategoryRequestDTO) {
        securityManager.checkEntityAccessible(entityId);
        Category category = CategoryConverter.fromDTO(entityCategoryRequestDTO);
        categoriesRepository.updateEntityCategory(entityId, categoryId, category);
    }

}
