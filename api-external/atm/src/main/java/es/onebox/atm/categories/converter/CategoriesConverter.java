package es.onebox.atm.categories.converter;

import es.onebox.atm.categories.dto.CategoryAdditonalData;
import es.onebox.atm.categories.dto.CategoryAdditonalDataDTO;
import es.onebox.atm.categories.dto.CategoryDTO;
import es.onebox.common.datasources.common.dto.Category;

import java.util.HashMap;
import java.util.Map;

public class CategoriesConverter {

    private CategoriesConverter() {}

    public static CategoryDTO toDto(Category category, Map<String, CategoryAdditonalData> categoryImages) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setCode(category.getCode());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setAdditionalInfo(convert(categoryImages));
        return categoryDTO;
    }

    private static Map<String, CategoryAdditonalDataDTO> convert(Map<String, CategoryAdditonalData> categoryImages) {
        if(categoryImages != null) {
            Map<String, CategoryAdditonalDataDTO> result = new HashMap<>();
            for (Map.Entry<String, CategoryAdditonalData> entry : categoryImages.entrySet()) {
                result.put(entry.getKey(), new CategoryAdditonalDataDTO(entry.getValue().getImageUrl(), entry.getValue().getName()));
            }
            return result;
        } else {
            return null;
        }
    }

}