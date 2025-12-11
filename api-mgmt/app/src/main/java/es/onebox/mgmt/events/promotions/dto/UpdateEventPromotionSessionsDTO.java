package es.onebox.mgmt.events.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.dto.PromotionTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Set;

public class UpdateEventPromotionSessionsDTO extends PromotionTarget {

    private static final long serialVersionUID = 1L;

    @Override
    @JsonProperty("sessions")
    public Set<Long> getData() {
        return super.data;
    }

    @JsonProperty("sessions")
    public Set<Long> setData() {
        return super.data;
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
