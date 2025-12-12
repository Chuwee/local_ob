package es.onebox.atm.wizard.pricetype.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.atm.wizard.pricetype.enums.PriceTypeCode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PriceTypeMapping implements Serializable {

    private static final long serialVersionUID = -3274095933329141190L;

    @JsonProperty("price_type_id")
    private Long priceTypeId;
    @JsonProperty("member_type")
    private PriceTypeCode code;

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public PriceTypeCode getCode() {
        return code;
    }

    public void setCode(PriceTypeCode code) {
        this.code = code;
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
