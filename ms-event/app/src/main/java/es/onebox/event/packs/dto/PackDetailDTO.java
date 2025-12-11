package es.onebox.event.packs.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;

public class PackDetailDTO extends PackDTO {

    private CategoryDTO baseCategory;
    private CategoryDTO customCategory;
    private IdNameDTO tax;

    public CategoryDTO getBaseCategory() {
        return baseCategory;
    }

    public void setBaseCategory(CategoryDTO baseCategory) {
        this.baseCategory = baseCategory;
    }

    public CategoryDTO getCustomCategory() {
        return customCategory;
    }

    public void setCustomCategory(CategoryDTO customCategory) {
        this.customCategory = customCategory;
    }

    public IdNameDTO getTax() {
        return tax;
    }

    public void setTax(IdNameDTO tax) {
        this.tax = tax;
    }

}
