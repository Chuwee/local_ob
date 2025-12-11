package es.onebox.mgmt.salerequests.communicationcontents.dto;

import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestChannelTextContent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SaleRequestChannelContentTextListDTO extends ChannelContentTextListDTO<SaleRequestChannelTextContent> {

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
