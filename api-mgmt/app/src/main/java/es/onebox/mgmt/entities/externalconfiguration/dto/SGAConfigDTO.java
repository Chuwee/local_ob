package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class SGAConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6763193858793869702L;
    private Boolean enabled;
    @JsonProperty("connection")
    private SgaConnectionDTO sgaConnectionDTO;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public SgaConnectionDTO getSgaConnectionDTO() {
        return sgaConnectionDTO;
    }

    public void setSgaConnectionDTO(SgaConnectionDTO sgaConnectionDTO) {
        this.sgaConnectionDTO = sgaConnectionDTO;
    }
}
