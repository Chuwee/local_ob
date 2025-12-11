package es.onebox.mgmt.channels.purchaseconfig.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class ChannelPurchaseConfigRedirectionPolicyDTO extends ArrayList<ChannelPurchaseConfigLinkDestinationDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ChannelPurchaseConfigRedirectionPolicyDTO() {}

    public ChannelPurchaseConfigRedirectionPolicyDTO(Collection<ChannelPurchaseConfigLinkDestinationDTO> values) {
        super(values);
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
