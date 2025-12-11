package es.onebox.mgmt.datasources.ms.channel.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelEventSaleRestrictions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long requiredEventId;
    private List<Long> cartEventIds;

    public Long getRequiredEventId() {
        return requiredEventId;
    }

    public void setRequiredEventId(Long requiredEventId) {
        this.requiredEventId = requiredEventId;
    }

    public List<Long> getCartEventIds() {
        return cartEventIds;
    }

    public void setCartEventIds(List<Long> cartEventIds) {
        this.cartEventIds = cartEventIds;
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
