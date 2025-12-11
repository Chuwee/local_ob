package es.onebox.mgmt.channels.contents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.contents.enums.ChannelContentType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelContentsCloneDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "source channelId can not be null")
    @JsonProperty("channel_id")
    private Long channelId;
    private List<ChannelContentType> contents;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public List<ChannelContentType> getContents() {
        return contents;
    }

    public void setContents(List<ChannelContentType> contents) {
        this.contents = contents;
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
