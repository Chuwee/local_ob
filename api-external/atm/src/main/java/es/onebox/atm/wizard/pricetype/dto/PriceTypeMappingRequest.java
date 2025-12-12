package es.onebox.atm.wizard.pricetype.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

public class PriceTypeMappingRequest implements Serializable {

    private static final long serialVersionUID = 4110949144874466564L;

    @NotEmpty(message = "Price type mappings must be provided")
    @JsonProperty("price_type_codes")
    private List<PriceTypeMapping> priceTypeCodes;

    public List<PriceTypeMapping> getPriceTypeCodes() {
        return priceTypeCodes;
    }

    public void setPriceTypeCodes(List<PriceTypeMapping> priceTypeCodes) {
        this.priceTypeCodes = priceTypeCodes;
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
