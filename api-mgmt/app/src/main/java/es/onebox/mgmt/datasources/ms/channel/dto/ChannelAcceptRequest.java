package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serializable;

public class ChannelAcceptRequest implements Serializable {

    private static final long serialVersionUID = 1;

    private Long userId;
    private Long channelMailingListId;

    public Long getChannelMailingListId() {
        return channelMailingListId;
    }

    public void setChannelMailingListId(Long channelMailingListId) {
        this.channelMailingListId = channelMailingListId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
