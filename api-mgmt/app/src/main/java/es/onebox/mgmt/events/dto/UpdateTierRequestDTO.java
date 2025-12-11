package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.TierConditionDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class UpdateTierRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    @JsonProperty("start_date")
    private ZonedDateTime startDate;
    private Double price;
    @JsonProperty("on_sale")
    private Boolean onSale;
    private Long limit;
    private TierConditionDTO condition;

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
        return "UpdateTierRequestDTO{" +
                "name='" + name + '\'' +
                ", startDate=" + startDate +
                ", price=" + price +
                ", onSale=" + onSale +
                '}';
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public TierConditionDTO getCondition() {
        return condition;
    }

    public void setCondition(TierConditionDTO condition) {
        this.condition = condition;
    }
}
