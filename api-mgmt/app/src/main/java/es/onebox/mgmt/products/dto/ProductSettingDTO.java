package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.CategoriesDTO;
import es.onebox.mgmt.products.enums.TaxModeDTO;

public class ProductSettingDTO {

    private CategoriesDTO categories;

    @JsonProperty("tax_mode")
    private TaxModeDTO taxMode;

    public CategoriesDTO getCategories() {
        return categories;
    }

    public void setCategories(CategoriesDTO categories) {
        this.categories = categories;
    }

    public TaxModeDTO getTaxMode() {
        return taxMode;
    }

    public void setTaxMode(TaxModeDTO taxMode) {
        this.taxMode = taxMode;
    }
}
