package es.onebox.mgmt.common.channelcontents;

import es.onebox.mgmt.common.CommunicationElementTextDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelContentTextDTO<T extends Serializable> extends CommunicationElementTextDTO<T> {

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
