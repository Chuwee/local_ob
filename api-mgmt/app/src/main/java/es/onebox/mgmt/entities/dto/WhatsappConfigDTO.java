package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class WhatsappConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8119545965387260970L;

    @JsonProperty("enabled")
    @NotNull(message = "enabled can not be null")
    private Boolean enabled;

    @JsonProperty("whatsapp_template")
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
