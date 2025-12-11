package es.onebox.mgmt.channels.purchaseconfig.dto;

import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelLinkDestinationMode;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelLinkDestinationType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class ChannelPurchaseConfigLinkDestinationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "type can not be null")
    private ChannelLinkDestinationType type;

    @NotNull(message = "mode can not be null")
    private ChannelLinkDestinationMode mode;

    private Map<String, String> value;

    public ChannelLinkDestinationType getType() {
        return type;
    }

    public void setType(ChannelLinkDestinationType type) {
        this.type = type;
    }

    public ChannelLinkDestinationMode getMode() {
        return mode;
    }

    public void setMode(ChannelLinkDestinationMode mode) {
        this.mode = mode;
    }

    public Map<String, String> getValue() {
        return value;
    }

    public void setValue(Map<String, String> value) {
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
