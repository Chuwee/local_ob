package es.onebox.mgmt.datasources.ms.event.dto.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.Category;

public class PackDetail extends Pack {

    private Category baseCategory;
    private Category customCategory;
    private IdNameDTO tax;

    public Category getBaseCategory() {
        return baseCategory;
    }

    public void setBaseCategory(Category baseCategory) {
        this.baseCategory = baseCategory;
    }

    public Category getCustomCategory() {
        return customCategory;
    }

    public void setCustomCategory(Category customCategory) {
        this.customCategory = customCategory;
    }

    public IdNameDTO getTax() {
        return tax;
    }

    public void setTax(IdNameDTO tax) {
        this.tax = tax;
    }
}
