package es.onebox.mgmt.sessions.dto;

import java.io.Serializable;

public class SettingsAccessControlSpaceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean override;

    private Long id;

    public Boolean getOverride() {
        return override;
    }

    public void setOverride(Boolean override) {
        this.override = override;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
