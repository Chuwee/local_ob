package es.onebox.event.products.domain;

import es.onebox.jooq.cpanel.tables.records.CpanelProductDeliveryPointRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductDeliveryPointRelationRecord extends CpanelProductDeliveryPointRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String productName;
    private String productDeliveryPointName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDeliveryPointName() {
        return productDeliveryPointName;
    }

    public void setProductDeliveryPointName(String productDeliveryPointName) {
        this.productDeliveryPointName = productDeliveryPointName;
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
