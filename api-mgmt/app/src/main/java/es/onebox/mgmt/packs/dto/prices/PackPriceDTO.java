package es.onebox.mgmt.packs.dto.prices;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.events.dto.PriceTypeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class PackPriceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4930464092462900870L;

    private IdNameDTO rate;
    @JsonProperty("price_type")
    private PriceTypeDTO priceType;
    private Double value;

    public IdNameDTO getRate() {
        return rate;
    }

    public void setRate(IdNameDTO rate) {
        this.rate = rate;
    }

    public PriceTypeDTO getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceTypeDTO priceType) {
        this.priceType = priceType;
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
