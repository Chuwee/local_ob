package es.onebox.mgmt.channels.contents.dto;

import es.onebox.mgmt.channels.contents.enums.ChannelContentType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelContentsCloneErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public ChannelContentsCloneErrorDTO() {
    }

    public ChannelContentsCloneErrorDTO(ChannelContentType type, String error) {
        this.type = type;
        this.error = error;
    }

    private ChannelContentType type;

    private String error;

    public ChannelContentType getType() {
        return type;
    }

    public void setType(ChannelContentType type) {
        this.type = type;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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
