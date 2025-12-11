package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.dto.DateConvertible;
import es.onebox.mgmt.events.enums.TierConditionDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TierDTO implements Serializable, DateConvertible {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    @JsonProperty("price_type")
    private PriceTypeTierDTO priceType;
    @JsonProperty("start_date")
    private ZonedDateTime startDate;
    @JsonIgnore
    private String olsonId;
    private Double price;
    @JsonProperty("on_sale")
    private Boolean onSale;
    private Boolean active;
    private Long limit;
    private TierConditionDTO condition;

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

    public PriceTypeTierDTO getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceTypeTierDTO priceType) {
        this.priceType = priceType;
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

    public String getOlsonId() {
        return olsonId;
    }

    public void setOlsonId(String olsonId) {
        this.olsonId = olsonId;
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

    @Override
    public void convertDates() {
        this.startDate = this.startDate.withZoneSameInstant(ZoneId.of(olsonId));
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
                ", priceType=" + priceType +
                ", startDate=" + startDate +
                ", timeZone=" + olsonId +
                ", price=" + price +
                ", onSale=" + onSale +
                ", active=" + active +
                ", limit=" + limit +
                ", condition=" + condition +
                '}';
    }

}
