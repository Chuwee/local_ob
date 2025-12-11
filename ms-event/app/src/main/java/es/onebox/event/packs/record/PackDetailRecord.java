package es.onebox.event.packs.record;

import es.onebox.event.packs.dao.domain.PackRecord;

public class PackDetailRecord extends PackRecord {

    //Join by pack.taxonomyid
    private String baseCategoryDescription;
    private String baseCategoryCode;

    //Join by pack.customtaxonomyid
    private String customCategoryDescription;
    private String customCategoryCode;

    //Join by pack.taxId
    private String taxName;

    public String getBaseCategoryDescription() {
        return baseCategoryDescription;
    }

    public void setBaseCategoryDescription(String baseCategoryDescription) {
        this.baseCategoryDescription = baseCategoryDescription;
    }

    public String getBaseCategoryCode() {
        return baseCategoryCode;
    }

    public void setBaseCategoryCode(String baseCategoryCode) {
        this.baseCategoryCode = baseCategoryCode;
    }

    public String getCustomCategoryDescription() {
        return customCategoryDescription;
    }

    public void setCustomCategoryDescription(String customCategoryDescription) {
        this.customCategoryDescription = customCategoryDescription;
    }

    public String getCustomCategoryCode() {
        return customCategoryCode;
    }

    public void setCustomCategoryCode(String customCategoryCode) {
        this.customCategoryCode = customCategoryCode;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }
}
