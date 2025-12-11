package es.onebox.mgmt.common;

import es.onebox.mgmt.categories.CategoryDTO;

import java.io.Serializable;

public class CategoriesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private CategoryDTO base;

    private CategoryDTO custom;

    public CategoryDTO getBase() {
        return base;
    }

    public void setBase(CategoryDTO base) {
        this.base = base;
    }

    public CategoryDTO getCustom() {
        return custom;
    }

    public void setCustom(CategoryDTO custom) {
        this.custom = custom;
    }

}
