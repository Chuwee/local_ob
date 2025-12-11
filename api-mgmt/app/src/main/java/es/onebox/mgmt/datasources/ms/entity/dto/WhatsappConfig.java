package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;

public class WhatsappConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;

    private Long whatsappTemplate;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getWhatsappTemplate() {
        return whatsappTemplate;
    }

    public void setWhatsappTemplate(Long whatsappTemplate) {
        this.whatsappTemplate = whatsappTemplate;
    }
}
