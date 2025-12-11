package es.onebox.mgmt.packs.dto;

import es.onebox.mgmt.common.CategoriesDTO;

public class PackSettingsDTO {

    private CategoriesDTO categories;

    public CategoriesDTO getCategories() {
        return categories;
    }

    public void setCategories(CategoriesDTO categories) {
        this.categories = categories;
    }
}
