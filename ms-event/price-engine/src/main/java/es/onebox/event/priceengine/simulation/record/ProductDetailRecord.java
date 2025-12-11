package es.onebox.event.priceengine.simulation.record;

public class ProductDetailRecord extends ProductRecord {

    //Join by product.taxonomyid
    private String categoryDescription;
    private String categoryCode;

    //Join by product.customtaxonomyid
    private String customCategoryDescription;
    private String customCategoryRef;

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCustomCategoryDescription() {
        return customCategoryDescription;
    }

    public void setCustomCategoryDescription(String customCategoryDescription) {
        this.customCategoryDescription = customCategoryDescription;
    }

    public String getCustomCategoryRef() {
        return customCategoryRef;
    }

    public void setCustomCategoryRef(String customCategoryRef) {
        this.customCategoryRef = customCategoryRef;
    }
}
