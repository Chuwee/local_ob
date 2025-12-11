package es.onebox.mgmt.packs.dto.prices;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdatePackPriceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "price_type_code cannot be null")
    @Min(value = 1, message = "price_type_code must be positive")
    @JsonProperty("price_type_id")
    private Long priceTypeId;

    @NotNull(message = "rate_id cannot be null")
    @Min(value = 1, message = "rate_id must be positive")
    @JsonProperty("rate_id")
    private Long rateId;

    @NotNull(message = "value cannot be null")
    @Min(value = 0, message = "value must be positive")
    private Double value;

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
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
