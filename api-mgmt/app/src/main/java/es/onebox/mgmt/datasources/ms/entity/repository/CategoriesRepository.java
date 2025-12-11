package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.Category;
import es.onebox.mgmt.datasources.ms.entity.dto.CategoryMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoriesRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public CategoriesRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public List<Category> getBaseCategories() {
        return msEntityDatasource.getBaseCategories();
    }

    public Category getCategory(Integer categoryId) {
        return msEntityDatasource.getCategory(categoryId);
    }

    public List<Category> getEntityCategories(Long entityId) {
        return msEntityDatasource.getEntityCategories(entityId);
    }

    public Category getEntityCategory(Long entityId, Long categoryId) {
        return msEntityDatasource.getEntityCategory(entityId, categoryId);
    }

    public void deleteEntityCategory(Long entityId, Long categoryId) {
        msEntityDatasource.deleteEntityCategory(entityId, categoryId);
    }

    public Long createEntityCategory(Long entityId, Category category) {
        return msEntityDatasource.createEntityCategory(entityId, category);
    }

    public void updateEntityCategory(Long entityId, Long categoryId, Category category) {
        msEntityDatasource.updateEntityCategory(entityId, categoryId, category);
    }

    public void putCategoryMapping(Long entityId, List<CategoryMapping> mappings) {
        msEntityDatasource.putCategoryMapping(entityId, mappings);
    }

    public List<CategoryMapping> getCategoryMapping(Long entityId) {
        return msEntityDatasource.getCategoryMapping(entityId);
    }


}
