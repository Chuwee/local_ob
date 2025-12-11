package es.onebox.mgmt.common.channelcontents;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChannelContentImageListDTO<T extends Serializable> extends ArrayList<ChannelContentImageDTO<T>> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ChannelContentImageListDTO() {
    }

    public ChannelContentImageListDTO(Collection<? extends ChannelContentImageDTO<T>> c) {
        super(c);
    }

    public List<ChannelContentImageDTO<T>> getImages() {
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
