package es.onebox.mgmt.vouchers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateVoucherGroupGiftCardDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("price_range")
    private PriceRangeDTO priceRange;

    public PriceRangeDTO getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(PriceRangeDTO priceRange) {
        this.priceRange = priceRange;
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
