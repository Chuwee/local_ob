package es.onebox.event.sessions.dto;

import java.io.Serializable;

public class PriceTypeRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private PriceTypeAdditionalConfigRequestDTO additionalConfig;

    public PriceTypeAdditionalConfigRequestDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(PriceTypeAdditionalConfigRequestDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

}
