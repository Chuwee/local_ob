package es.onebox.event.events.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class TierUpdateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private ZonedDateTime startDate;
    private Double price;
    private Boolean onSale;
    private Integer limit;
    private TierCondition condition;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public TierCondition getCondition() {
        return condition;
    }

    public void setCondition(TierCondition condition) {
        this.condition = condition;
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
        return "TierUpdateRequestDTO{" +
                "name='" + name + '\'' +
                ", startDate=" + startDate +
                ", price=" + price +
                ", onSale=" + onSale +
                ", limit=" + limit +
                ", condition=" + condition +
                '}';
    }
}
