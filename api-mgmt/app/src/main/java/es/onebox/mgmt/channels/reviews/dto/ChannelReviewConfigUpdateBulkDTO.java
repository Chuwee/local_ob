package es.onebox.mgmt.channels.reviews.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewsSendCriteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelReviewConfigUpdateBulkDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5456713725663934272L;

    @JsonProperty("send_criteria")
    private ChannelReviewsSendCriteria sendCriteria;

    @JsonProperty("scope_ids")
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
