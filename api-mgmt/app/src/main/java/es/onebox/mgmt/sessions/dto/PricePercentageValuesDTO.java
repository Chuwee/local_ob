package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PricePercentageValuesDTO {
    @JsonProperty("price_type")
    private IdNameDTO priceType;
    private RateDTO rate;
    private Double value;

    public IdNameDTO getPriceType() {
        return priceType;
    }

    public void setPriceType(IdNameDTO priceType) {
        this.priceType = priceType;
    }

    public RateDTO getRate() {
        return rate;
    }

    public void setRate(RateDTO rate) {
        this.rate = rate;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
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
