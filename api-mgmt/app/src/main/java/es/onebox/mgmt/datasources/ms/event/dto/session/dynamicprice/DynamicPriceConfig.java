package es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class DynamicPriceConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean active;
    private List<DynamicPriceZone> dynamicPriceZoneDTO;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<DynamicPriceZone> getDynamicPriceZoneDTO() {
        return dynamicPriceZoneDTO;
    }

    public void setDynamicPriceZoneDTO(List<DynamicPriceZone> dynamicPriceZoneDTO) {
        this.dynamicPriceZoneDTO = dynamicPriceZoneDTO;
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
