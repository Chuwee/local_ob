package es.onebox.mgmt.channels.reviews.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewsSendCriteria;

import java.io.Serial;
import java.io.Serializable;

public class ChannelReviewConfigUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5456713725663934272L;

    @JsonProperty("send_criteria")
    private ChannelReviewsSendCriteria sendCriteria;

    public ChannelReviewsSendCriteria getSendCriteria() {
        return sendCriteria;
    }

    public void setSendCriteria(ChannelReviewsSendCriteria sendCriteria) {
        this.sendCriteria = sendCriteria;
    }
}
