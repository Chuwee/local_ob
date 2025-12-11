package es.onebox.event.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CreateEventChannelDTO implements Serializable {

    private static final long serialVersionUID = -5423675427715060709L;
    @JsonProperty("channel_id")
    private Long channelId;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
