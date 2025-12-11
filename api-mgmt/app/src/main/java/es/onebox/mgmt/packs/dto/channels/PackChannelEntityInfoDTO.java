package es.onebox.mgmt.packs.dto.channels;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.channels.enums.ChannelSubtype;

import java.io.Serializable;

public class PackChannelEntityInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String logo;

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
