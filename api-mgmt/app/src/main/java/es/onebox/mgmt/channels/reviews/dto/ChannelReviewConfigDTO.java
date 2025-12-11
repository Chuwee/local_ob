package es.onebox.mgmt.channels.reviews.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewScope;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewsSendCriteria;

import java.io.Serial;
import java.io.Serializable;

public class ChannelReviewConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1868456821314905655L;

    private ChannelReviewScope scope;
    @JsonProperty("scope_id")
    private Integer scopeId;
    @JsonProperty("send_criteria")
    private ChannelReviewsSendCriteria sendCriteria;
    private ChannelReviewConfigDetailsDTO details;

    public ChannelReviewScope getScope() {
        return scope;
    }

    public void setScope(ChannelReviewScope scope) {
        this.scope = scope;
    }

    public Integer getScopeId() {
        return scopeId;
    }

    public void setScopeId(Integer scopeId) {
        this.scopeId = scopeId;
    }

    public ChannelReviewsSendCriteria getSendCriteria() {
        return sendCriteria;
    }

    public void setSendCriteria(ChannelReviewsSendCriteria sendCriteria) {
        this.sendCriteria = sendCriteria;
    }

    public ChannelReviewConfigDetailsDTO getDetails() {
        return details;
    }

    public void setDetails(ChannelReviewConfigDetailsDTO details) {
        this.details = details;
    }
}
