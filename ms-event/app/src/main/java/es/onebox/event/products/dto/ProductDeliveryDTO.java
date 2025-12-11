package es.onebox.event.products.dto;

import es.onebox.event.products.enums.ProductDeliveryTimeUnitType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.products.enums.ProductDeliveryType;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductDeliveryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    @NotNull
    private ProductDeliveryType deliveryType;
    private ProductDeliveryTimeUnitType startTimeUnit;
    private Long startTimeValue;
    private ProductDeliveryTimeUnitType endTimeUnit;
    private Long endTimeValue;
    private ZonedDateTime deliveryDateFrom;
    private ZonedDateTime deliveryDateTo;

    public ProductDeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(ProductDeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public ProductDeliveryTimeUnitType getStartTimeUnit() {
        return startTimeUnit;
    }

    public void setStartTimeUnit(ProductDeliveryTimeUnitType startTimeUnit) {
        this.startTimeUnit = startTimeUnit;
    }

    public Long getStartTimeValue() {
        return startTimeValue;
    }

    public void setStartTimeValue(Long startTimeValue) {
        this.startTimeValue = startTimeValue;
    }

    public ProductDeliveryTimeUnitType getEndTimeUnit() {
        return endTimeUnit;
    }

    public void setEndTimeUnit(ProductDeliveryTimeUnitType endTimeUnit) {
        this.endTimeUnit = endTimeUnit;
    }

    public Long getEndTimeValue() {
        return endTimeValue;
    }

    public void setEndTimeValue(Long endTimeValue) {
        this.endTimeValue = endTimeValue;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
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
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
