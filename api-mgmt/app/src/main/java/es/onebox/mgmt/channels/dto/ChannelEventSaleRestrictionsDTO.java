package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelEventSaleRestrictionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("required_event_id")
    @NotNull(message = "Event id can not be null")
    private Long requiredEventId;

    @JsonProperty("cart_event_ids")
    private List<Long> cartEventIds;

    public ChannelEventSaleRestrictionsDTO() {
        super();
    }

    public ChannelEventSaleRestrictionsDTO(Long requiredEventId, List<Long> cartEventIds) {
        this.requiredEventId = requiredEventId;
        this.cartEventIds = cartEventIds;
    }

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
