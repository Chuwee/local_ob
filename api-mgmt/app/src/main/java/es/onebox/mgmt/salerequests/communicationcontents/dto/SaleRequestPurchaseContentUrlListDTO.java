package es.onebox.mgmt.salerequests.communicationcontents.dto;

import es.onebox.mgmt.common.channelcontents.ChannelContentUrlListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseUrlContentType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SaleRequestPurchaseContentUrlListDTO extends ChannelContentUrlListDTO<SaleRequestPurchaseUrlContentType> {

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
