package es.onebox.event.events.dto;

import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.WhitelabelType;

import java.io.Serializable;

public class EventChannelInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private ChannelSubtype type;
    private Boolean v4Enabled;
    private Boolean v4ConfigEnabled;
    private Long entityId;
    private String entityName;
    private String entityLogo;
    private Boolean favorite;
    private WhitelabelType whitelabelType;
    private EventTicketTemplatesDTO ticketTemplates;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityLogo() {
        return entityLogo;
    }

    public void setEntityLogo(String entityLogo) {
        this.entityLogo = entityLogo;
    }

    public ChannelSubtype getType() {
        return type;
    }

    public void setType(ChannelSubtype type) {
        this.type = type;
    }

    public Boolean getV4Enabled() {
        return v4Enabled;
    }

    public void setV4Enabled(Boolean isV4) {
        this.v4Enabled = isV4;
    }

    public Boolean getV4ConfigEnabled() {
        return v4ConfigEnabled;
    }

    public void setV4ConfigEnabled(Boolean v4ConfigEnabled) {
        this.v4ConfigEnabled = v4ConfigEnabled;
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

    public EventTicketTemplatesDTO getTicketTemplates() {
        return ticketTemplates;
    }

    public void setTicketTemplates(EventTicketTemplatesDTO ticketTemplates) {
        this.ticketTemplates = ticketTemplates;
    }
}
