package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serial;
import java.io.Serializable;

public class WhatsappConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean overrideEntityConfig;

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