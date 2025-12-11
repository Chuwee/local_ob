package es.onebox.mgmt.seasontickets.dto.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SeasonTicketChannelInfoDTO extends IdNameDTO {

    private static final long serialVersionUID = 1L;

    private ChannelEntityDTO entity;
    private ChannelSubtype type;
    @JsonProperty("is_v4")
    private Boolean isV4;

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

    public Boolean getIsV4() {
        return isV4;
    }

    public void setIsV4(Boolean isV4) {
        this.isV4 = isV4;
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
