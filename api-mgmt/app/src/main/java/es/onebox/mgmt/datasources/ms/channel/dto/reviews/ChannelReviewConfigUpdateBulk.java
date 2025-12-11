package es.onebox.mgmt.datasources.ms.channel.dto.reviews;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelReviewConfigUpdateBulk implements Serializable {

    @Serial
    private static final long serialVersionUID = 7353705192981580048L;

    private ChannelReviewsSendCriteria sendCriteria;
    private List<Integer> scopeIds;

    public ChannelReviewsSendCriteria getSendCriteria() {
        return sendCriteria;
    }

    public void setSendCriteria(ChannelReviewsSendCriteria sendCriteria) {
        this.sendCriteria = sendCriteria;
    }

    public List<Integer> getScopeIds() {
        return scopeIds;
    }

    public void setScopeIds(List<Integer> scopeIds) {
        this.scopeIds = scopeIds;
    }
}
