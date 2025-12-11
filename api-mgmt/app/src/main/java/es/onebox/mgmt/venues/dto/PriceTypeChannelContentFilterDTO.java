package es.onebox.mgmt.venues.dto;

import es.onebox.mgmt.common.CommunicationElementFilter;
import es.onebox.mgmt.common.channelcontents.PriceTypeChannelContentTextType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PriceTypeChannelContentFilterDTO extends CommunicationElementFilter<PriceTypeChannelContentTextType> {

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
