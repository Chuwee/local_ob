package es.onebox.common.datasources.accesscontrol.dto;

import java.io.Serializable;

public class PriceDTO implements Serializable {

    private static final long serialVersionUID = -3022116986012481360L;

    private Long id;
    private String name;
    private Boolean restrictive;

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

    public Boolean getRestrictive() {
        return restrictive;
    }

    public void setRestrictive(Boolean restrictive) {
        this.restrictive = restrictive;
    }
}
