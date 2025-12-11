package es.onebox.mgmt.packs.dto.channels;

import es.onebox.mgmt.channels.enums.ChannelSubtype;

import java.io.Serializable;

public class PackChannelInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private PackChannelEntityInfoDTO entity;
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

    public PackChannelEntityInfoDTO getEntity() {
        return entity;
    }

    public void setEntity(PackChannelEntityInfoDTO entity) {
        this.entity = entity;
    }

    public ChannelSubtype getType() {
        return type;
    }

    public void setType(ChannelSubtype type) {
        this.type = type;
    }
}
