package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class MsSaleRequestSurchargesExtendedDTO implements Serializable {

    private static final long serialVersionUID = -2507365781318338122L;

    private Long channelId;
    private Long entityId;
    private List<MsSaleRequestSurchargesDTO> surcharges;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public List<MsSaleRequestSurchargesDTO> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<MsSaleRequestSurchargesDTO> surcharges) {
        this.surcharges = surcharges;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
