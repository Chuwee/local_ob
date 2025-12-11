package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.events.dto.channel.ChannelEntityDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ProductChannelInfoDTO extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductChannelInfoDTO() {

    }

    public ProductChannelInfoDTO(Long id, String name, ChannelEntityDTO entity, ChannelSubtype type) {
        super(id, name);
        this.entity = entity;
        this.type = type;
    }

    @JsonProperty("entity")
    private ChannelEntityDTO entity;
    @JsonProperty("type")
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
