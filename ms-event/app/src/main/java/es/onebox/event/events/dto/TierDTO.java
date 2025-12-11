package es.onebox.event.events.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class TierDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Long priceTypeId;
    private String priceTypeName;
    private ZonedDateTime startDate;
    private String olsonId;
    private Double price;
    private Boolean onSale;
    private Boolean active;
    private Integer limit;
    private TierCondition condition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getPriceTypeName() {
        return priceTypeName;
    }

    public void setPriceTypeName(String priceTypeName) {
        this.priceTypeName = priceTypeName;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getOlsonId() {
        return olsonId;
    }

    public void setOlsonId(String olsonId) {
        this.olsonId = olsonId;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "TierDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", priceTypeId=" + priceTypeId +
                ", priceTypeName='" + priceTypeName + '\'' +
                ", startDate=" + startDate +
                ", timeZone=" + olsonId +
                ", price=" + price +
                ", onSale=" + onSale +
                ", active=" + active +
                ", limit=" + limit +
                '}';
    }


    public TierCondition getCondition() {
        return condition;
    }

    public void setCondition(TierCondition condition) {
        this.condition = condition;
    }
}
