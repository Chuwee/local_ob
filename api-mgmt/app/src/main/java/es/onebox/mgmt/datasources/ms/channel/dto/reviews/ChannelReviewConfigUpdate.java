package es.onebox.mgmt.datasources.ms.channel.dto.reviews;

import java.io.Serial;
import java.io.Serializable;

public class ChannelReviewConfigUpdate implements Serializable {

    @Serial
    private static final long serialVersionUID = 7353705192981580048L;

    private ChannelReviewsSendCriteria sendCriteria;

    public ChannelReviewsSendCriteria getSendCriteria() {
        return sendCriteria;
    }

    public void setSendCriteria(ChannelReviewsSendCriteria sendCriteria) {
        this.sendCriteria = sendCriteria;
    }
}
