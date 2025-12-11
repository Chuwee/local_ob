package es.onebox.event.events.dto;

import java.io.Serializable;
import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UpdatePriceTypesDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotEmpty(message = "ids must be informed")
    private Set<Long> ids;
    @NotNull(message = "upsell info must be informed")
    private Boolean upsell;
    
    public UpdatePriceTypesDTO() {
    }
    
    public UpdatePriceTypesDTO(Set<Long> ids, Boolean upsell) {
        this.ids = ids;
        this.upsell = upsell;
    }

    public Set<Long> getIds() {
        return ids;
    }

    public void setIds(Set<Long> ids) {
        this.ids = ids;
    }

    public Boolean getUpsell() {
        return upsell;
    }

    public void setUpsell(Boolean upsell) {
        this.upsell = upsell;
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
