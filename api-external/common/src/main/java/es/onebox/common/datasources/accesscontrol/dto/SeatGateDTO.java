package es.onebox.common.datasources.accesscontrol.dto;

import java.io.Serializable;

public class SeatGateDTO implements Serializable {

    private static final long serialVersionUID = -5030685761625616126L;

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
