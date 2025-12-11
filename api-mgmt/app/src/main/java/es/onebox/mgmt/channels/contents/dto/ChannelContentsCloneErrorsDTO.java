package es.onebox.mgmt.channels.contents.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class ChannelContentsCloneErrorsDTO extends ArrayList<ChannelContentsCloneErrorDTO> {

    private static final long serialVersionUID = 1L;

    public ChannelContentsCloneErrorsDTO() {
    }

    public ChannelContentsCloneErrorsDTO(Collection<ChannelContentsCloneErrorDTO> collection) {
        super(collection);
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
