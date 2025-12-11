package es.onebox.mgmt.events.dto;

import es.onebox.mgmt.common.CommunicationElementFilter;
import es.onebox.mgmt.common.channelcontents.TierChannelContentTextType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TierChannelContentFilter extends CommunicationElementFilter<TierChannelContentTextType> {

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
