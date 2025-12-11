package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.channels.enums.WhitelabelType;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTicketTemplates;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EventChannelInfoDTO extends IdNameDTO {

    private static final long serialVersionUID = 1L;

    public EventChannelInfoDTO(Long id, String name) {
        super(id, name);
    }

    @JsonProperty("entity")
    private ChannelEntityDTO entity;
    @JsonProperty("type")
    private ChannelSubtype type;
    @JsonProperty("is_v4")
    private Boolean isV4;
    @JsonProperty("favorite")
    private Boolean favorite;
    @JsonProperty("whitelabel_type")
    private WhitelabelType whitelabelType;
    @JsonProperty("ticket_templates")
    private EventTicketTemplates ticketTemplates;
    @JsonProperty("force_square_pictures")
    private Boolean forceSquarePictures;

    public ChannelEntityDTO getEntity() {
        return entity;
    }

    public void setEntity(ChannelEntityDTO entity) {
        this.entity = entity;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
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

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public WhitelabelType getWhitelabelType() {
        return whitelabelType;
    }

    public void setWhitelabelType(WhitelabelType whitelabelType) {
        this.whitelabelType = whitelabelType;
    }

    public EventTicketTemplates getTicketTemplates() {
        return ticketTemplates;
    }

    public void setTicketTemplates(EventTicketTemplates ticketTemplates) {
        this.ticketTemplates = ticketTemplates;
    }

    public Boolean getForceSquarePictures() {return forceSquarePictures;}

    public void setForceSquarePictures(Boolean forceSquarePictures) {this.forceSquarePictures = forceSquarePictures;}
}
