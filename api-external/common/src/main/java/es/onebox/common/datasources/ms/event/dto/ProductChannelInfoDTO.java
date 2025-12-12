package es.onebox.common.datasources.ms.event.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ProductChannelInfoDTO extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private ChannelEntityDTO entity;
    private ChannelSubtype type;

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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}