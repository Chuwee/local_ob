package es.onebox.common.datasources.accesscontrol.dto;

import java.io.Serializable;

public class SeatNotNumberedZoneDTO implements Serializable {

    private static final long serialVersionUID = -984685727921062639L;

    private Long id;
    private String name;

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
}
