package es.onebox.mgmt.common.channelcontents;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChannelContentTextListDTO<T extends Serializable> extends ArrayList<ChannelContentTextDTO<T>> {

    private static final long serialVersionUID = 1L;

    public ChannelContentTextListDTO() {
    }

    public ChannelContentTextListDTO(Collection<? extends ChannelContentTextDTO<T>> c) {
        super(c);
    }

    public List<ChannelContentTextDTO<T>> getTexts() {
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
