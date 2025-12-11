package es.onebox.event.products.domain;

import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ProductVariantRecord extends CpanelProductVariantRecord {

    private String productName;
    private String productFirstAttributeName;
    private String productSecondAttributeName;
    private String productFirstValueName;
    private String productSecondValueName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductFirstAttributeName() {
        return productFirstAttributeName;
    }

    public void setProductFirstAttributeName(String productFirstAttributeName) {
        this.productFirstAttributeName = productFirstAttributeName;
    }

    public String getProductSecondAttributeName() {
        return productSecondAttributeName;
    }

    public void setProductSecondAttributeName(String productSecondAttributeName) {
        this.productSecondAttributeName = productSecondAttributeName;
    }

    public String getProductFirstValueName() {
        return productFirstValueName;
    }

    public void setProductFirstValueName(String productFirstValueName) {
        this.productFirstValueName = productFirstValueName;
    }

    public String getProductSecondValueName() {
        return productSecondValueName;
    }

    public void setProductSecondValueName(String productSecondValueName) {
        this.productSecondValueName = productSecondValueName;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
