package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.products.enums.ProductDeliveryType;
import es.onebox.mgmt.products.enums.ProductDeliveryUnit;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductDelivery implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ProductDeliveryType deliveryType;
    private ProductDeliveryUnit startTimeUnit;
    private Long startTimeValue;
    private ProductDeliveryUnit endTimeUnit;
    private Long endTimeValue;
    private ZonedDateTime deliveryDateFrom;
    private ZonedDateTime deliveryDateTo;

    public ProductDeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(ProductDeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public ProductDeliveryUnit getStartTimeUnit() {
        return startTimeUnit;
    }

    public void setStartTimeUnit(ProductDeliveryUnit startTimeUnit) {
        this.startTimeUnit = startTimeUnit;
    }

    public Long getStartTimeValue() {
        return startTimeValue;
    }

    public void setStartTimeValue(Long startTimeValue) {
        this.startTimeValue = startTimeValue;
    }

    public ProductDeliveryUnit getEndTimeUnit() {
        return endTimeUnit;
    }

    public void setEndTimeUnit(ProductDeliveryUnit endTimeUnit) {
        this.endTimeUnit = endTimeUnit;
    }

    public Long getEndTimeValue() {
        return endTimeValue;
    }

    public void setEndTimeValue(Long endTimeValue) {
        this.endTimeValue = endTimeValue;
    }

    public ZonedDateTime getDeliveryDateFrom() {
        return deliveryDateFrom;
    }

    public void setDeliveryDateFrom(ZonedDateTime deliveryDateFrom) {
        this.deliveryDateFrom = deliveryDateFrom;
    }

    public ZonedDateTime getDeliveryDateTo() {
        return deliveryDateTo;
    }

    public void setDeliveryDateTo(ZonedDateTime deliveryDateTo) {
        this.deliveryDateTo = deliveryDateTo;
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
