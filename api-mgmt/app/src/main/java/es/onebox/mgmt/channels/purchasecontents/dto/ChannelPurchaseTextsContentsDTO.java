package es.onebox.mgmt.channels.purchasecontents.dto;

import es.onebox.mgmt.channels.purchasecontents.enums.ChannelPurchaseTextsContentType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;

public class ChannelPurchaseTextsContentsDTO extends ArrayList<ChannelPurchaseTextsContentDTO<ChannelPurchaseTextsContentType>> {

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
