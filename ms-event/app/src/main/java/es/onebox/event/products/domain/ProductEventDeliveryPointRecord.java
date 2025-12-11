package es.onebox.event.products.domain;

import es.onebox.jooq.cpanel.tables.records.CpanelProductEventDeliveryPointRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductEventDeliveryPointRecord extends CpanelProductEventDeliveryPointRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer productId;
    private String productName;
    private String productDeliveryPointName;
    private Integer eventId;
    private String eventName;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

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

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
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
