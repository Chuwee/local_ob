package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.seasontickets.dto.presales.AdditionalConfigDTO;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class CreateSeasonTicketPresaleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private String name;

    @JsonProperty("additional_config")
    private AdditionalConfigDTO additionalConfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AdditionalConfigDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(AdditionalConfigDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
    }
}
