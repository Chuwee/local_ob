package es.onebox.mgmt.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class SessionChannelContentImageListDTO extends ArrayList<SessionChannelContentImageDTO> {

    private static final long serialVersionUID = 1L;

    public SessionChannelContentImageListDTO() {
    }

    public SessionChannelContentImageListDTO(Collection<? extends SessionChannelContentImageDTO> c) {
        super(c);
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
