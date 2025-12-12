package es.onebox.common.datasources.ms.channel.dto.config;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ChannelEventSaleRestrictionResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -2162771872265293981L;

    Map<Integer, List<Integer>> eventSaleRestrictions;

    public Map<Integer, List<Integer>> getEventSaleRestrictions() {
        return eventSaleRestrictions;
    }

    public void setEventSaleRestrictions(Map<Integer, List<Integer>> eventSaleRestrictions) {
        this.eventSaleRestrictions = eventSaleRestrictions;
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
