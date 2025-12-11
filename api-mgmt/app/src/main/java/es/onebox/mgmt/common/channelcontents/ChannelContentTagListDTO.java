package es.onebox.mgmt.common.channelcontents;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChannelContentTagListDTO<T extends Serializable> extends ArrayList<ChannelContentTagDTO<T>> {

    @Serial
    private static final long serialVersionUID = -1342338673890855346L;

    public ChannelContentTagListDTO() {
    }

    public ChannelContentTagListDTO(Collection<? extends ChannelContentTagDTO<T>> c) {
        super(c);
    }

    public List<ChannelContentTagDTO<T>> getTags() {
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
