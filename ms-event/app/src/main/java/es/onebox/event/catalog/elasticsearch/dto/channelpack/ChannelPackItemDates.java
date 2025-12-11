package es.onebox.event.catalog.elasticsearch.dto.channelpack;

import es.onebox.event.products.enums.ProductDeliveryTimeUnitType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ChannelPackItemDates implements Serializable {

    private static final long serialVersionUID = 1L;

    private ZonedDateTime start;
    private ZonedDateTime end;
    private ZonedDateTime saleStart;
    private ZonedDateTime saleEnd;
    private ProductDeliveryTimeUnitType startTimeUnit;
    private Long startTimeValue;
    private ProductDeliveryTimeUnitType endTimeUnit;
    private Long endTimeValue;
    private Boolean startUnconfirmed;

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public ZonedDateTime getSaleStart() {
        return saleStart;
    }

    public void setSaleStart(ZonedDateTime saleStart) {
        this.saleStart = saleStart;
    }

    public ZonedDateTime getSaleEnd() {
        return saleEnd;
    }

    public void setSaleEnd(ZonedDateTime saleEnd) {
        this.saleEnd = saleEnd;
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

    public Boolean getStartUnconfirmed() {
        return startUnconfirmed;
    }

    public void setStartUnconfirmed(Boolean startUnconfirmed) {
        this.startUnconfirmed = startUnconfirmed;
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
