package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class RateExtendedDTO extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = -712442505536344871L;

    @JsonProperty("rate_group")
    private IdNameDTO rateGroup;

    public IdNameDTO getRateGroup() {
        return rateGroup;
    }

    public void setRateGroup(IdNameDTO rateGroup) {
        this.rateGroup = rateGroup;
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
