package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class WhatsappConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("override_entity_config")
    private Boolean overrideEntityConfig;

    @JsonProperty("whatsapp_template")
    private Long whatsappTemplate;

    public Boolean getOverrideEntityConfig() {
        return overrideEntityConfig;
    }

    public void setOverrideEntityConfig(Boolean overrideEntityConfig) {
        this.overrideEntityConfig = overrideEntityConfig;
    }

    public Long getWhatsappTemplate() {
        return whatsappTemplate;
    }

    public void setWhatsappTemplate(Long whatsappTemplate) {
        this.whatsappTemplate = whatsappTemplate;
    }
}
