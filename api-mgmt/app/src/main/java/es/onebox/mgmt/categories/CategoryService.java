package es.onebox.mgmt.categories;

import es.onebox.mgmt.datasources.ms.entity.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    public List<CategoryDTO> getBaseCategories() {
        return CategoryConverter.fromEntityBaseCategories(categoriesRepository.getBaseCategories());
    }

}
