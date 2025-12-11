package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PriceTypeRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("additional_config")
    private PriceTypeAdditionalConfigRequestDTO additionalConfig;

    public PriceTypeAdditionalConfigRequestDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(PriceTypeAdditionalConfigRequestDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

}
