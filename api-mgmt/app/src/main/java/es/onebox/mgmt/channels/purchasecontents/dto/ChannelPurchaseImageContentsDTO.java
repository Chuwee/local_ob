package es.onebox.mgmt.channels.purchasecontents.dto;

import es.onebox.mgmt.channels.purchasecontents.enums.ChannelPurchaseImageContentType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;

public class ChannelPurchaseImageContentsDTO extends ArrayList<ChannelPurchaseImageContentDTO<ChannelPurchaseImageContentType>> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
