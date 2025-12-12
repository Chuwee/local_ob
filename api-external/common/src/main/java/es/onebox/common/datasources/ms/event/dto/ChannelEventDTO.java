package es.onebox.common.datasources.ms.event.dto;

import java.io.Serializable;

public class ChannelEventDTO implements Serializable {

    private static final long serialVersionUID = 6249331833563913836L;

    private Long channelId;
    private Long eventId;
    private Long channelEventId;
    private Integer channelEventStatus;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getChannelEventId() {
        return channelEventId;
    }

    public void setChannelEventId(Long channelEventId) {
        this.channelEventId = channelEventId;
    }

    public Integer getChannelEventStatus() {
        return channelEventStatus;
    }

    public void setChannelEventStatus(Integer channelEventStatus) {
        this.channelEventStatus = channelEventStatus;
    }
}
