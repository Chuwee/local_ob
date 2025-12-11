package es.onebox.mgmt.entities.dto;

import java.io.Serial;
import java.io.Serializable;

public class EntityDonationProviderDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5059179432407704624L;

    private Long id;
    private String name;
    private Boolean enabled;

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
