package es.onebox.mgmt.datasources.ms.channel.dto.reviews;

import java.io.Serial;
import java.io.Serializable;

public class ChannelReviewConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -3741858237783646619L;

    private ChannelReviewScope scope;
    private Integer scopeId;
    private ChannelReviewsSendCriteria sendCriteria;
    private ChannelReviewConfigDetails details;

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

    public ChannelReviewConfigDetails getDetails() {

        return details;
    }

    public void setDetails(ChannelReviewConfigDetails details) {
        this.details = details;
    }
}
