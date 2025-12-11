package es.onebox.mgmt.common.channelcontents;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChannelContentUrlListDTO<T extends Serializable> extends ArrayList<ChannelContentUrlDTO<T>> {

    private static final long serialVersionUID = 1L;

    public ChannelContentUrlListDTO() {
    }

    public ChannelContentUrlListDTO(Collection<? extends ChannelContentUrlDTO<T>> c) {
        super(c);
    }

    public List<ChannelContentUrlDTO<T>> getLinks() {
        return this;
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
