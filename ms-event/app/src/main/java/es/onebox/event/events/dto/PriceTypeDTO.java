package es.onebox.event.events.dto;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.onebox.core.serializer.dto.common.IdNameDTO;

public class PriceTypeDTO extends IdNameDTO implements Serializable {

    private static final long serialVersionUID = -8928453776245340039L;

    @JsonProperty("venueTemplate")
    private IdNameDTO venueConfig;
    private Boolean upsell;

    public Boolean getUpsell() {
        return upsell;
    }
    
    public void setUpsell(Boolean upsell) {
        this.upsell = upsell;
    }
    
    public IdNameDTO getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(IdNameDTO venueConfig) {
        this.venueConfig = venueConfig;
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
