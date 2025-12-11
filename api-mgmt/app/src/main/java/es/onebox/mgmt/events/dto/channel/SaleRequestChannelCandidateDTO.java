package es.onebox.mgmt.events.dto.channel;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import es.onebox.mgmt.channels.enums.ChannelStatus;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class SaleRequestChannelCandidateDTO extends IdNameDTO {

    private static final long serialVersionUID = 1L;

    private ChannelEntityDTO entity;
    private ChannelSubtype type;
    private ChannelStatus status;

    public ChannelEntityDTO getEntity() {
        return entity;
    }

    public void setEntity(ChannelEntityDTO entity) {
        this.entity = entity;
    }

    public ChannelSubtype getType() {
        return type;
    }

    public void setType(ChannelSubtype type) {
        this.type = type;
    }

    public ChannelStatus getStatus() {
        return status;
    }

    public void setStatus(ChannelStatus status) {
        this.status = status;
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