package es.onebox.common.datasources.webhook.dto.fever;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SaleRequestStatusDTO  implements Serializable {

    @JsonProperty("event_id")
    private Integer eventId;
    @JsonProperty("channel_id")
    private Integer channelId;
    private SaleRequestStatus status;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public SaleRequestStatus getStatus() {
        return status;
    }

    public void setStatus(SaleRequestStatus status) {
        this.status = status;
    }


}
