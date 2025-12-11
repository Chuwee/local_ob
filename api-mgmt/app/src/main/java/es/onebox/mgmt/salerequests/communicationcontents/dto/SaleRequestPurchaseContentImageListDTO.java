package es.onebox.mgmt.salerequests.communicationcontents.dto;

import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseImageContentRequestType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SaleRequestPurchaseContentImageListDTO extends ChannelContentImageListDTO<SaleRequestPurchaseImageContentRequestType> {

    private static final long serialVersionUID = 2L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
