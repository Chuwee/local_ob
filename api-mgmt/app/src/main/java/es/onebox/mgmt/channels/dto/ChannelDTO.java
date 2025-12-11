package es.onebox.mgmt.channels.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.channels.enums.ChannelStatus;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.seasontickets.dto.channels.ChannelEntityDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ChannelDTO extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private ChannelEntityDTO entity;
    private ChannelSubtype type;
    private ChannelStatus status;
    private IdNameDTO operator;
    private String url;

    public ChannelEntityDTO getEntity() {
        return entity;
    }

    public void setEntity(ChannelEntityDTO entity) {
        this.entity = entity;
    }

    public void setStatus(ChannelStatus status) {
        this.status = status;
    }

    public ChannelStatus getStatus() {
        return status;
    }

    public ChannelSubtype getType() {
        return type;
    }

    public void setType(ChannelSubtype type) {
        this.type = type;
    }

    public IdNameDTO getOperator() {
        return operator;
    }

    public void setOperator(IdNameDTO operator) {
        this.operator = operator;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
