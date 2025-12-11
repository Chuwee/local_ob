package es.onebox.mgmt.entities.dto;

import java.io.Serial;
import java.io.Serializable;

public class PostBookingQuestionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6147545965387260970L;

    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
