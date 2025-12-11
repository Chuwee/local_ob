package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class SmartBookingDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1751791910644169392L;

    private Boolean enabled;
    @JsonProperty("connection")
    private ConnectionBaseDTO connectionBaseDTO;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ConnectionBaseDTO getConnectionBaseDTO() {
        return connectionBaseDTO;
    }

    public void setConnectionBaseDTO(ConnectionBaseDTO connectionBaseDTO) {
        this.connectionBaseDTO = connectionBaseDTO;
    }
}
