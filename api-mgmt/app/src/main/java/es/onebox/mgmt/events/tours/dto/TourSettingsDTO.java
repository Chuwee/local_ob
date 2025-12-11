package es.onebox.mgmt.events.tours.dto;

import java.io.Serializable;

public class TourSettingsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enable;
    private Long id;

    public TourSettingsDTO() {
    }

    public TourSettingsDTO(Long id) {
        this.id = id;
        this.enable = this.id != null;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
