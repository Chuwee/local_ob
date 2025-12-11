package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class DestinationChannelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("destination_channel_type")
    private String destinationChannelType;
    @JsonProperty("destination_channel_id")
    private String destinationChannelId;

    public DestinationChannelDTO() {}

    public DestinationChannelDTO(String destinationChannelType, String destinationChannelId) {
        this.destinationChannelType = destinationChannelType;
        this.destinationChannelId = destinationChannelId;
    }

    public String getDestinationChannelType() {
        return destinationChannelType;
    }

    public void setDestinationChannelType(String destinationChannelType) {
        this.destinationChannelType = destinationChannelType;
    }

    public String getDestinationChannelId() {
        return destinationChannelId;
    }

    public void setDestinationChannelId(String destinationChannelId) {
        this.destinationChannelId = destinationChannelId;
    }
}
