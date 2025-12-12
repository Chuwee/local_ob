package es.onebox.ms.notification.webhooks.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class WrapperDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3017063784996936091L;

    private Long entityId;

    private Long channelId;

    private PayloadRequest payloadRequest;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public PayloadRequest getPayloadRequest() {
        return payloadRequest;
    }

    public void setPayloadRequest(PayloadRequest payloadRequest) {
        this.payloadRequest = payloadRequest;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
