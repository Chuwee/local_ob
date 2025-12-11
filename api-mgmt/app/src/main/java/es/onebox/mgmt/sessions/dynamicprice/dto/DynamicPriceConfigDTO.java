package es.onebox.mgmt.sessions.dynamicprice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class DynamicPriceConfigDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5829475837458392847L;

    @JsonProperty("dynamic_price_zones")
    public List<DynamicPriceZoneDTO> dynamicPriceZone;

    public List<DynamicPriceZoneDTO> getDynamicPriceZone() {
        return dynamicPriceZone;
    }

    public void setDynamicPriceZone(List<DynamicPriceZoneDTO> dynamicPriceZone) {
        this.dynamicPriceZone = dynamicPriceZone;
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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
