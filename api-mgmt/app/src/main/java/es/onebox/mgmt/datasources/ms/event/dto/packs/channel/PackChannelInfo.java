package es.onebox.mgmt.datasources.ms.event.dto.packs.channel;

import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;

import java.io.Serializable;

public class PackChannelInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Long entityId;
    private String entityName;
    private String entityLogo;
    private ChannelSubtype type;

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
}
