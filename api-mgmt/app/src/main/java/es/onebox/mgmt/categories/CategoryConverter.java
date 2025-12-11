package es.onebox.mgmt.categories;

import es.onebox.mgmt.datasources.ms.entity.dto.Category;
import es.onebox.mgmt.datasources.ms.entity.dto.CategoryMapping;
import es.onebox.mgmt.entities.dto.CategoryMappingDTO;
import es.onebox.mgmt.entities.dto.CategoryMappingsDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryConverter {

    private CategoryConverter() {
    }

    public static CategoryDTO fromEntity(Category baseEntity, CategoryDTO target) {
        if (baseEntity == null) {
            return null;
        }
        fromEntityBaseCategory(baseEntity, target);
        target.setId(baseEntity.getId().longValue());
        return target;
    }

    public static EntityCategoryResponseDTO fromEntity(Category baseEntity, EntityCategoryResponseDTO target) {
        if (baseEntity == null) {
            return null;
        }
        fromEntityBaseCategory(baseEntity, target);
        target.setId(baseEntity.getId().longValue());
        target.setBaseCategoryId(baseEntity.getBaseCategoryId());
        return target;
    }

    private static <T extends BaseCategoryDTO> T fromEntityBaseCategory(Category baseEntity, T baseCategoryDTO) {
        if (baseEntity == null) {
            return null;
        }
        baseCategoryDTO.setParentId(baseEntity.getParentId());
        baseCategoryDTO.setCode(baseEntity.getCode());
        baseCategoryDTO.setDescription(baseEntity.getDescription());

        return baseCategoryDTO;
    }

    public static List<CategoryDTO> fromEntityBaseCategories(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }
        return categories.stream().map(category -> CategoryConverter.fromEntity(
                category, new CategoryDTO())).collect(Collectors.toList());
    }

    public static List<EntityCategoryResponseDTO> fromEntityCategories(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }
        return categories.stream().map(category -> CategoryConverter.fromEntity(
                category, new EntityCategoryResponseDTO())).collect(Collectors.toList());
    }

    public static Category fromDTO(EntityCategoryRequestDTO entityCategoryRequestDTO) {
        if (entityCategoryRequestDTO == null) {
            return null;
        }
        Category category = new Category();

        category.setParentId(entityCategoryRequestDTO.getParentId());
        category.setCode(entityCategoryRequestDTO.getCode());
        category.setDescription(entityCategoryRequestDTO.getDescription());
        category.setBaseCategoryId(entityCategoryRequestDTO.getBaseCategoryId());

        return category;
    }

    public static Category fromDTO(BaseCategoryDTO baseCategoryDTO) {
        if (baseCategoryDTO == null) {
            return null;
        }
        Category category = new Category();

        category.setParentId(baseCategoryDTO.getParentId());
        category.setCode(baseCategoryDTO.getCode());
        category.setDescription(baseCategoryDTO.getDescription());

        return category;
    }

    public static CategoryMappingsDTO fromEntity(List<CategoryMapping> mappings) {
        return new CategoryMappingsDTO(mappings.stream().map(CategoryConverter::fromEntity).collect(Collectors.toList()));
    }

    private static CategoryMappingDTO fromEntity(CategoryMapping mapping) {
        CategoryMappingDTO out = new CategoryMappingDTO();
        out.setCategoryId(mapping.getCategoryId());
        out.setBaseCategoryId(mapping.getBaseCategoryId());
        return out;
    }

    public static List<CategoryMapping> toEntity(CategoryMappingsDTO in) {
        return in.stream().map(CategoryConverter::toEntity).collect(Collectors.toList());
    }

    private static CategoryMapping toEntity(CategoryMappingDTO mapping) {
        CategoryMapping out = new CategoryMapping();
        out.setCategoryId(mapping.getCategoryId());
        out.setBaseCategoryId(mapping.getBaseCategoryId());
        return out;
    }

}
